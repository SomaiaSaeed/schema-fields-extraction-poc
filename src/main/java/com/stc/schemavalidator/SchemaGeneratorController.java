package com.stc.schemavalidator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stc.schemavalidator.model.EmployeeProfile;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SchemaGeneratorController {

    @Autowired
    SchemaGeneratorService schemaGeneratorService;

    @PostMapping("/generateSchema")
    public JsonNode generateSchemaFromData(@RequestBody JsonNode JsonNode) throws Exception {
        return schemaGeneratorService.generateSchema(JsonNode);
    }

    @PostMapping("/fieldNames")
    public List<FieldDTO> getFieldNames(@RequestBody JsonNode fields) throws JsonProcessingException {
        return schemaGeneratorService.getFieldNames(fields);
    }

    @PostMapping("/validateData")
    public List<String> validateDataByScheme(@RequestBody JsonNode fields) throws JsonProcessingException {
        return schemaGeneratorService.validateDataByScheme(fields);
    }

    @PostMapping("/generateSchemaFromFields")
    public JsonNode generateSchemaFromFields(@RequestBody JsonNode fields) throws Exception {
                ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(fields);

        return schemaGeneratorService.generateSchemaFromFields(json);
    }

    @PostMapping("/flow")
    public List<String> flowValidation(@RequestBody JsonNode data) throws Exception {
        List<FieldDTO> fieldsName = schemaGeneratorService.getFieldNames(data);
        fieldsName.forEach(fieldDTO -> {
            fieldDTO.setName(fieldDTO.getName().replace("[*]", ""));
        });        ObjectWriter ow = new ObjectMapper().writer();
        String fieldsJson = ow.writeValueAsString(fieldsName);
        JsonNode schema = schemaGeneratorService.generateSchemaFromFields(fieldsJson);
        return  schemaGeneratorService.validateDataByScheme1( data,schema);
        
    }

    public static JSONObject convertToJSONObject(List<FieldDTO> FieldDTOs) {
        JSONObject result = new JSONObject();
        JSONArray FieldDTOArray = new JSONArray();

        for (FieldDTO FieldDTO : FieldDTOs) {
            JSONObject FieldDTOObject = new JSONObject();
            FieldDTOObject.put("type", FieldDTO.getType());
            FieldDTOObject.put("name", FieldDTO.getName());
            FieldDTOArray.put(FieldDTOObject);
        }

        result.put("FieldDTOs", FieldDTOArray);
        return result;
    }
    @PostMapping("/jsonToCsv")
    public String ConvertJsonToCsv(@RequestBody JsonNode data) throws IOException, IllegalAccessException {
        // Sample nested array of objects
//        List<Person> persons = new ArrayList<>();
//        persons.add(new Person("John", "Doe", 30));
//        persons.add(new Person("Jane", "Smith", 25));
//
//        // Convert nested array of objects to CSV
//        String csv = convertNestedObjectsToCsv(persons);
//        System.out.println(csv);
      return schemaGeneratorService.ConvertJsonToCsv(data, EmployeeProfile.class);
    }


    public static String convertNestedObjectsToCsv(List<?> objects) {
        if (objects.isEmpty()) {
            return "";
        }

        // Extract headers from the first object
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode firstObjectNode = objectMapper.valueToTree(objects.get(0));
        List<String> headers = new ArrayList<>();
        extractHeaders("", firstObjectNode, headers);

        // Build CSV header row
        StringBuilder csvBuilder = new StringBuilder();
        for (String header : headers) {
            csvBuilder.append(header).append(",");
        }
        csvBuilder.deleteCharAt(csvBuilder.length() - 1); // Remove trailing comma
        csvBuilder.append("\n");

        // Build CSV data rows
        for (Object object : objects) {
            ObjectNode objectNode = objectMapper.valueToTree(object);
            extractData(objectNode, csvBuilder);
            csvBuilder.append("\n");
        }

        return csvBuilder.toString();
    }

    private static void extractHeaders(String parentKey, ObjectNode objectNode, List<String> headers) {
        objectNode.fields().forEachRemaining(entry -> {
            String fullKey = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
            if (entry.getValue().isObject()) {
                extractHeaders(fullKey, (ObjectNode) entry.getValue(), headers);
            } else {
                headers.add(fullKey);
            }
        });
    }

    private static void extractData(ObjectNode objectNode, StringBuilder csvBuilder) {
        objectNode.fields().forEachRemaining(entry -> {
            if (entry.getValue().isArray()) {
                ArrayNode arrayNode = (ArrayNode) entry.getValue();
                csvBuilder.append("[");
                for (int i = 0; i < arrayNode.size(); i++) {
                    ObjectNode arrayObjectNode = (ObjectNode) arrayNode.get(i);
                    extractData(arrayObjectNode, csvBuilder);
                    if (i < arrayNode.size() - 1) {
                        csvBuilder.append(", ");
                    }
                }
                csvBuilder.append("]");
            } else {
                csvBuilder.append(entry.getValue().asText()).append(",");
            }
        });
    }

    @Data
    private static class Person {
        @JsonProperty("firstName")

        private String firstName;
        @JsonProperty("lastName")

        private String lastName;
        @JsonProperty("age")

        private int age;

        public Person(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }
    }
}
