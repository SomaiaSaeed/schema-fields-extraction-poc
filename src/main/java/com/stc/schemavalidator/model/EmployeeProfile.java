package com.stc.schemavalidator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeProfile {
    @JsonProperty("userType")
    private String userType;

    @JsonProperty("employeeNumber")
    private String employeeNumber;

    @JsonProperty("profileImage")
    private String profileImage;

    @JsonProperty("name")
    private String name;

    @JsonProperty("title")
    private String title;

    @JsonProperty("email")
    private String email;

    @JsonProperty("nationality")
    private Nationality nationality;

    @JsonProperty("dob")
    private String dob;

    @JsonProperty("mailingAddress")
    private MailingAddress mailingAddress;

    @JsonProperty("contact")
    private Contact contact;

    @JsonProperty("socialNetworkInfo")
    private SocialNetworkInfo socialNetworkInfo;

    @JsonProperty("personId")
    private String personId;

    @JsonProperty("gosiNumber")
    private String gosiNumber;

    @JsonProperty("dateOfJoin")
    private String dateOfJoin;

    @JsonProperty("type")
    private String type;

    @JsonProperty("role")
    private String role;

    @JsonProperty("location")
    private String location;

    @JsonProperty("payrollLocation")
    private String payrollLocation;

    @JsonProperty("hrLocation")
    private String hrLocation;

    @JsonProperty("hrLocationCode")
    private String hrLocationCode;

    @JsonProperty("gradeName")
    private String gradeName;

    @JsonProperty("gradeId")
    private String gradeId;

    @JsonProperty("lastPromotionDate")
    private String lastPromotionDate;

    @JsonProperty("hasSubOrdinates")
    private String hasSubOrdinates;

    @JsonProperty("job")
    private Job job;

    @JsonProperty("position")
    private Position position;

    @JsonProperty("organization")
    private Organization organization;

    @JsonProperty("cOrganization")
    private COrganization cOrganization;

    @JsonProperty("sector")
    private Sector sector;

    @JsonProperty("generalDepartment")
    private GeneralDepartment generalDepartment;

    @JsonProperty("department")
    private Department department;

    @JsonProperty("section")
    private Section section;

    @JsonProperty("seniorSectorName")
    private SeniorSectorName seniorSectorName;

    @JsonProperty("manager")
    private Manager manager;

    @JsonProperty("delegate")
    private Delegate delegate;

    @JsonProperty("isExecutive")
    private String isExecutive;

    @JsonProperty("gradeCode")
    private String gradeCode;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("genderCode")
    private String genderCode;

    @JsonProperty("jobBenefitLevelCode")
    private String jobBenefitLevelCode;

    @JsonProperty("contract")
    private Contract contract;

    @JsonProperty("payrollLocationCode")
    private String payrollLocationCode;

    @JsonProperty("usageType")
    private String usageType;

    @JsonProperty("qualification")
    private Qualification qualification;

    @JsonProperty("englishLevel")
    private String englishLevel;

    @JsonProperty("volunteeringStatus")
    private String volunteeringStatus;

    @JsonProperty("totalExperience")
    private String totalExperience;

//    @JsonProperty("corganization")
//    private COrganization corganization;

}
