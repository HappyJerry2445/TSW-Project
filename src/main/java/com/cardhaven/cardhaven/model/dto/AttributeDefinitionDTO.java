package com.cardhaven.cardhaven.model.dto;


import java.io.Serializable;
import java.util.Objects;

public class AttributeDefinitionDTO implements Serializable {
    private int attributeId;    //PK
    private String attributeName;
    private DataType dataType;
    private ApplicableTo applicableTo;

    public AttributeDefinitionDTO() {
    }

    public AttributeDefinitionDTO(int attributeId, String attributeName, DataType dataType, ApplicableTo applicableTo) {
        this.attributeId = attributeId;
        this.attributeName = attributeName;
        this.dataType = dataType;
        this.applicableTo = applicableTo;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public ApplicableTo getApplicableTo() {
        return applicableTo;
    }

    public void setApplicableTo(ApplicableTo applicableTo) {
        this.applicableTo = applicableTo;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        AttributeDefinitionDTO attributeDefinitionDTO = (AttributeDefinitionDTO) object;
        return attributeId == attributeDefinitionDTO.attributeId && Objects.equals(attributeName, attributeDefinitionDTO.attributeName) && dataType == attributeDefinitionDTO.dataType && applicableTo == attributeDefinitionDTO.applicableTo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeId, attributeName, dataType, applicableTo);
    }

    public String toString() {
        return "AttributeDefinition{" +
                "attributeId=" + attributeId + '\'' +
                "attributeName=" + attributeName + '\'' +
                "dataType=" + dataType + '\'' +
                "applicableTo=" + applicableTo + '\'' +
                '}'
                ;
    }

    public enum DataType {
        String,
        Number,
        Boolean,
        Date
    }

    public enum ApplicableTo {
        Card,
        Accessory,
        All
    }
}