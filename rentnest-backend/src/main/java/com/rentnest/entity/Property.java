package com.rentnest.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String locality;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rent;

    @Column(nullable = false)
    private Integer bhk;

    @Column(name = "property_type", nullable = false, length = 50)
    private String propertyType;

    @Column(nullable = false)
    private Boolean furnished;

    @Column(name = "pet_friendly", nullable = false)
    private Boolean petFriendly;

    @Column(nullable = false)
    private Boolean parking;

    @Column(name = "available_from", nullable = false)
    private LocalDate availableFrom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PropertyStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<PropertyImage> images = new ArrayList<>();

    protected Property() {
    }

    public Property(User owner, String title, String city, String locality, BigDecimal rent, Integer bhk, 
                    String propertyType, Boolean furnished, Boolean petFriendly, Boolean parking, 
                    LocalDate availableFrom, PropertyStatus status) {
        this.owner = owner;
        this.title = title;
        this.city = city;
        this.locality = locality;
        this.rent = rent;
        this.bhk = bhk;
        this.propertyType = propertyType;
        this.furnished = furnished;
        this.petFriendly = petFriendly;
        this.parking = parking;
        this.availableFrom = availableFrom;
        this.status = status;
    }

    @PrePersist
    void assignCreatedAt() {
        createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public User getOwner() { return owner; }
    public String getTitle() { return title; }
    public String getCity() { return city; }
    public String getLocality() { return locality; }
    public BigDecimal getRent() { return rent; }
    public Integer getBhk() { return bhk; }
    public String getPropertyType() { return propertyType; }
    public Boolean getFurnished() { return furnished; }
    public Boolean getPetFriendly() { return petFriendly; }
    public Boolean getParking() { return parking; }
    public LocalDate getAvailableFrom() { return availableFrom; }
    public PropertyStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public List<PropertyImage> getImages() { return images; }

    public String getRejectionReason() { return rejectionReason; }

    public void setOwner(User owner) { this.owner = owner; }
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setCity(String city) { this.city = city; }
    public void setLocality(String locality) { this.locality = locality; }
    public void setRent(BigDecimal rent) { this.rent = rent; }
    public void setBhk(Integer bhk) { this.bhk = bhk; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }
    public void setFurnished(Boolean furnished) { this.furnished = furnished; }
    public void setPetFriendly(Boolean petFriendly) { this.petFriendly = petFriendly; }
    public void setParking(Boolean parking) { this.parking = parking; }
    public void setAvailableFrom(LocalDate availableFrom) { this.availableFrom = availableFrom; }
    public void setStatus(PropertyStatus status) { this.status = status; }
    public void setImages(List<PropertyImage> images) { this.images = images; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
