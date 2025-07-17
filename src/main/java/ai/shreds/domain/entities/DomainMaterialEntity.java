package ai.shreds.domain.entities;

import ai.shreds.domain.value_objects.DomainMaterialId;

/**
 * Domain entity representing a material that can be ordered.
 * Contains material specifications, category information, and quality requirements.
 */
public class DomainMaterialEntity {
    private final DomainMaterialId materialId;
    private String name;
    private String specifications;
    private String category;
    private String unitOfMeasure;
    private String hazardousInfo;
    private String qualityRequirements;

    /**
     * Creates a new material entity with the required information.
     *
     * @param materialId the unique identifier of the material
     * @param name the name of the material
     * @param specifications the technical specifications of the material
     * @param category the category or classification of the material
     * @param unitOfMeasure the unit of measure for this material (e.g., kg, pieces, liters)
     * @param hazardousInfo information about hazardous properties, if any
     * @param qualityRequirements the quality requirements and standards for this material
     */
    public DomainMaterialEntity(DomainMaterialId materialId,
                               String name,
                               String specifications,
                               String category,
                               String unitOfMeasure,
                               String hazardousInfo,
                               String qualityRequirements) {
        if (materialId == null) {
            throw new IllegalArgumentException("materialId cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (specifications == null || specifications.trim().isEmpty()) {
            throw new IllegalArgumentException("specifications cannot be null or empty");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("category cannot be null or empty");
        }
        if (unitOfMeasure == null || unitOfMeasure.trim().isEmpty()) {
            throw new IllegalArgumentException("unitOfMeasure cannot be null or empty");
        }
        
        this.materialId = materialId;
        this.name = name;
        this.specifications = specifications;
        this.category = category;
        this.unitOfMeasure = unitOfMeasure;
        this.hazardousInfo = hazardousInfo;
        this.qualityRequirements = qualityRequirements;
    }

    /**
     * Validates the material specifications to ensure they meet requirements.
     *
     * @return true if specifications are valid, false otherwise
     */
    public boolean validateSpecifications() {
        // Basic validation - in a real system this would be more complex
        return specifications != null && !specifications.trim().isEmpty() &&
               specifications.length() >= 10; // Minimum specification length
    }

    /**
     * Updates the quality requirements for this material.
     *
     * @param requirements the new quality requirements
     */
    public void updateQualityRequirements(String requirements) {
        if (requirements == null || requirements.trim().isEmpty()) {
            throw new IllegalArgumentException("Quality requirements cannot be null or empty");
        }
        this.qualityRequirements = requirements;
    }

    /**
     * Checks if this material is classified as hazardous.
     *
     * @return true if the material is hazardous, false otherwise
     */
    public boolean isHazardous() {
        return hazardousInfo != null && !hazardousInfo.trim().isEmpty() &&
               !hazardousInfo.toUpperCase().contains("NON-HAZARDOUS");
    }

    // Getters
    public DomainMaterialId getMaterialId() {
        return materialId;
    }

    public String getName() {
        return name;
    }

    public String getSpecifications() {
        return specifications;
    }

    public String getCategory() {
        return category;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public String getHazardousInfo() {
        return hazardousInfo;
    }

    public String getQualityRequirements() {
        return qualityRequirements;
    }
    
    // Setters for mutable properties
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        this.name = name;
    }

    public void setSpecifications(String specifications) {
        if (specifications == null || specifications.trim().isEmpty()) {
            throw new IllegalArgumentException("specifications cannot be null or empty");
        }
        this.specifications = specifications;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("category cannot be null or empty");
        }
        this.category = category;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        if (unitOfMeasure == null || unitOfMeasure.trim().isEmpty()) {
            throw new IllegalArgumentException("unitOfMeasure cannot be null or empty");
        }
        this.unitOfMeasure = unitOfMeasure;
    }

    public void setHazardousInfo(String hazardousInfo) {
        this.hazardousInfo = hazardousInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainMaterialEntity)) return false;
        DomainMaterialEntity that = (DomainMaterialEntity) o;
        return materialId.equals(that.materialId);
    }

    @Override
    public int hashCode() {
        return materialId.hashCode();
    }

    @Override
    public String toString() {
        return "DomainMaterialEntity{" +
                "materialId=" + materialId +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", unitOfMeasure='" + unitOfMeasure + '\'' +
                ", hazardous=" + isHazardous() +
                '}';
    }
}