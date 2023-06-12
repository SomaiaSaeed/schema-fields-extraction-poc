package com.stc.schemavalidator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.saasquatch.jsonschemainferrer.*;
import com.stc.schemavalidator.model.EmployeeProfile;
import org.apache.commons.collections.map.HashedMap;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

@Service
public class SchemaGeneratorService {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonSchemaInferrer inferrer = JsonSchemaInferrer.newBuilder()
            .setSpecVersion(SpecVersion.DRAFT_07)
            // Requires commons-validator
//            .addFormatInferrers(FormatInferrers.email(), FormatInferrers.ip())
            .setAdditionalPropertiesPolicy(AdditionalPropertiesPolicies.notAllowed())
            .setRequiredPolicy(RequiredPolicies.nonNullCommonFields())
            .addEnumExtractors(EnumExtractors.validEnum(java.time.Month.class),
                    EnumExtractors.validEnum(java.time.DayOfWeek.class))
            .build();


    public JsonNode generateSchema(JsonNode jsonNode) {

        return inferrer.inferForSample(jsonNode);
    }

    public List<FieldDTO> getFieldNames(JsonNode jsonNode) throws JsonProcessingException {

        ObjectNode schema = inferrer.inferForSample(jsonNode);
        return extractProperties(schema.get("properties"), new ArrayList<>(), "");
    }

    public List<String> validateDataByScheme(JsonNode fields) throws JsonProcessingException {

        String json = mapper.writeValueAsString(fields);
        JSONObject jsonObject = new JSONObject(json);
        return validate(jsonObject);
    }

    public List<String> validateDataByScheme1(JsonNode fields, JsonNode schema) throws JsonProcessingException {
        String schemaJson = mapper.writeValueAsString(schema);

        String json = mapper.writeValueAsString(fields);
        JSONObject jsonObject = new JSONObject(json);
        return validate1(jsonObject, schemaJson);
    }

    public JsonNode generateSchemaFromFields(String json) throws Exception {
//        String json = mapper.writeValueAsString(fields);
        return generateSchema(generateJson(json));
    }

    private List<FieldDTO> extractProperties(JsonNode properties, List<FieldDTO> fields, String root) {

        if (properties.isEmpty()) return Collections.emptyList();

        final Iterator<Map.Entry<String, JsonNode>> fields1 = properties.fields();

        while (fields1.hasNext()) {
            final Map.Entry<String, JsonNode> field = fields1.next();

            final String type = field.getValue().get("type").asText();
            String name = "";
            if (type.equals("object")) {
                name = root.isBlank() ? field.getKey() : root + "." + field.getKey();
                fields.addAll(extractProperties(field.getValue().get("properties"), new ArrayList<>(), name));
            } else if (type.equals("array")) {
                JsonNode item = field.getValue().get("items");
                name = root.isBlank() ? field.getKey() + "[*]" : root + "." + field.getKey();
                if (item.get("properties") != null) {
                    fields.addAll(extractProperties(item.get("properties"), new ArrayList<>(), name));
                } else {
                    fields.add(new FieldDTO(name, type));

                }
            } else {
                name = root.isBlank() ? field.getKey() : root + "." + field.getKey();
                fields.add(new FieldDTO(name, type));

            }
        }
        return fields;
    }

    private List<String> validate(JSONObject inp) {
        List<String> validationResult = new ArrayList<>();

        try (InputStream inputStream = getClass().getResourceAsStream("/schema.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            Schema schema = SchemaLoader.load(rawSchema);
            try {
                schema.validate(inp); // throws a ValidationException if this object is invalid
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                validationResult.add(e.getMessage());
                e.getCausingExceptions().forEach(ex -> {
                    validationResult.add(ex.getMessage());
                    System.out.println(ex.getMessage());
                });
            }
        } catch (IOException ex) {

        }
        return validationResult;
    }

    private List<String> validate1(JSONObject inp, String schemaJson) {
        List<String> validationResult = new ArrayList<>();

        JSONObject rawSchema = new JSONObject(new JSONTokener(schemaJson));
        Schema schema = SchemaLoader.load(rawSchema);
        try {
            schema.validate(inp); // throws a ValidationException if this object is invalid
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
            validationResult.add(e.getMessage());
            e.getCausingExceptions().forEach(ex -> {
                validationResult.add(ex.getMessage());
                System.out.println(ex.getMessage());
            });
        }
        return validationResult;
    }


    public static JsonNode generateJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode fieldDefinitions = mapper.readTree(json);
        ObjectNode generatedJson = mapper.createObjectNode();

        for (JsonNode fieldDefinition : fieldDefinitions) {
            String fieldName = fieldDefinition.get("name").asText();
            String[] fieldPath = fieldName.split("\\.");

            String fieldType = fieldDefinition.get("type").asText();
            setValue(generatedJson, fieldPath, fieldType);
        }

        return generatedJson;
    }

    private static void setValue(ObjectNode parentNode, String[] fieldPath, String fieldType) {
        ObjectNode currentNode = parentNode;
        int pathLength = fieldPath.length;
        for (int i = 0; i < pathLength; i++) {
            String pathSegment = fieldPath[i];
            boolean isArray = pathSegment.matches(".+\\[\\d+\\]$");

            if (i == pathLength - 1) {
                // Last segment, set the value based on the field type
                if (isArray) {
                    ArrayNode arrayNode = currentNode.putArray(pathSegment.replaceAll("\\[\\d+\\]$", ""));
                    addValueToArray(arrayNode, fieldType);
                } else {
                    currentNode.put(pathSegment, getValueByType(fieldType));
                }
            } else {
                // Intermediate segments, create nested objects if they don't exist
                if (isArray) {
                    String arrayName = pathSegment.replaceAll("\\[\\d+\\]$", "");
                    ArrayNode arrayNode = currentNode.putArray(arrayName);
                    int arrayIndex = extractArrayIndex(pathSegment);
                    ensureArraySize(arrayNode, arrayIndex + 1);
                    ObjectNode nestedNode = (ObjectNode) arrayNode.get(arrayIndex);
                    if (nestedNode == null) {
                        nestedNode = arrayNode.addObject();
                        arrayNode.set(arrayIndex, nestedNode);
                    }
                    currentNode = nestedNode;
                } else {
                    ObjectNode nestedNode = (ObjectNode) currentNode.get(pathSegment);
                    if (nestedNode == null) {
                        nestedNode = currentNode.putObject(pathSegment);
                    }
                    currentNode = nestedNode;
                }
            }
        }
    }

    private static int extractArrayIndex(String arraySegment) {
        int startIndex = arraySegment.lastIndexOf('[');
        int endIndex = arraySegment.lastIndexOf(']');
        String indexString = arraySegment.substring(startIndex + 1, endIndex);
        return Integer.parseInt(indexString);
    }

    private static void ensureArraySize(ArrayNode arrayNode, int size) {
        int currentSize = arrayNode.size();
        if (size > currentSize) {
            for (int i = currentSize; i < size; i++) {
                arrayNode.addNull();
            }
        }
    }

    private static void addValueToArray(ArrayNode arrayNode, String fieldType) {
        arrayNode.add(getValueByType(fieldType));
    }

    private static JsonNode getValueByType(String fieldType) {
        return switch (fieldType) {
            case "string" -> new ObjectMapper().getNodeFactory().textNode("string");
            case "integer" -> new ObjectMapper().getNodeFactory().numberNode(2);
            case "boolean" -> new ObjectMapper().getNodeFactory().booleanNode(false);
            case "array" -> new ObjectMapper().getNodeFactory().arrayNode();
            default -> new ObjectMapper().getNodeFactory().nullNode();
        };
    }

    public String ConvertJsonToCsv(JsonNode jsonNode, Class<?> type) throws IOException, IllegalAccessException {
        String stringData = mapper.writeValueAsString(jsonNode);
        List<?> data = mapper.readValue(stringData, mapper.getTypeFactory().constructCollectionType(List.class, type));
        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        Field[] fields = type.getDeclaredFields();

        // addHeader
        addCSVColumnsHeader(fields, csvSchemaBuilder, data);

        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.writerFor(List.class).with(csvSchema).writeValue(new File("output.csv"), getValues(data, fields));
        return "Converted Successfully";
    }

    private void addCSVColumnsHeader(Field[] fields, CsvSchema.Builder csvSchemaBuilder, List<?> data) throws IllegalAccessException {

        for (Field field : fields) {

            String fieldName = field.getName();
            if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
                // Add primitive fields directly
                csvSchemaBuilder.addColumn(fieldName);
            } else {
                // Flatten nested objects
                field.setAccessible(true);
                flattenNestedObjects(field, field.get(data.get(0)), csvSchemaBuilder);
            }
        }

    }


    List<Map<String, Object>> getValues(List<?> users, Field[] fields) {
        List<Map<String, Object>> employees = new ArrayList<>();
        users.forEach(employeeProfile -> {
            Map<String, Object> employee = new HashedMap();
            for (Field field : fields) {
                // Enable access to private fields
                field.setAccessible(true);

                String fieldName = field.getName();
                if (field.getType().isPrimitive() || field.getType().equals(String.class) || field.getType().equals(List.class)) {
                    try {
                        employee.put(fieldName, field.get(employeeProfile));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // Flatten nested objects
                    try {
                        if (field.get(employeeProfile) != null)
                            flattenNestedObjectsValues(fieldName, field.getType(), employee, field.get(employeeProfile));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            employees.add(employee);

        });
        return employees;
    }

    private static void flattenNestedObjects(Field parent, Object data, CsvSchema.Builder csvSchemaBuilder)  {
        String prefix = parent.getName();
        Field[] fields = parent.getType().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = prefix + "." + field.getName();
            if (field.getType().isPrimitive() || field.getType().equals(String.class) || field.getType().equals(List.class)) {
                csvSchemaBuilder.addColumn(fieldName);
            } else {
                flattenNestedObjects(field, data, csvSchemaBuilder);
            }
        }
    }

    private static void flattenNestedObjectsValues(String prefix, Class<?> clazz, Map<String, Object> employees, Object users) throws IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = prefix + "." + field.getName();
            if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
                employees.put(fieldName, field.get(users));

            } else if (field.getType().equals(List.class)) {
                StringBuilder fieldValue = new StringBuilder();
                JsonNode json = mapper.convertValue(field.get(users), JsonNode.class);
                for (Object obj : json) {
                    if (obj instanceof TextNode)
                        obj = ((TextNode) obj).asText().replaceAll("\"", "");
                    fieldValue.append(obj.toString()).append(",\n");
                }
                employees.put(fieldName, fieldValue.toString());

            } else {
                flattenNestedObjectsValues(fieldName, field.getType(), employees, users);
            }
        }
    }

}
