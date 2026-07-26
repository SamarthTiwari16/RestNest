package com.rentnest.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "recently_viewed", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "property_id"})
})
public class RecentlyViewed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "viewed_at", nullable = false)
    private Instant viewedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        viewedAt = Instant.now();
    }

    public RecentlyViewed() {}

    public RecentlyViewed(User user, Property property) {
        this.user = user;
        this.property = property;
        this.viewedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Instant getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(Instant viewedAt) {
        this.viewedAt = viewedAt;
    }
}
