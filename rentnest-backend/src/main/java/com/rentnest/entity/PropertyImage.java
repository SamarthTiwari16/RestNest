package com.rentnest.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "property_images")
public class PropertyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    protected PropertyImage() {
    }

    public PropertyImage(Property property, String imageUrl, Integer sortOrder) {
        this.property = property;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }

    public Long getId() { return id; }
    public Property getProperty() { return property; }
    public String getImageUrl() { return imageUrl; }
    public Integer getSortOrder() { return sortOrder; }

    public void setProperty(Property property) { this.property = property; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
