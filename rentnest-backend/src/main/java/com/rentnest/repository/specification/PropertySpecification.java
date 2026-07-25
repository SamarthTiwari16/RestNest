package com.rentnest.repository.specification;

import com.rentnest.entity.Property;
import com.rentnest.entity.PropertyStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import jakarta.persistence.criteria.Predicate;

public class PropertySpecification {

    public static Specification<Property> filterProperties(
            String city,
            String locality,
            BigDecimal minRent,
            BigDecimal maxRent,
            Integer bhk,
            Boolean furnished,
            Boolean parking,
            Boolean petFriendly,
            LocalDate availableFrom,
            String propertyType
    ) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            // Always restrict to ACTIVE status
            predicates.add(cb.equal(root.get("status"), PropertyStatus.ACTIVE));

            if (city != null && !city.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), city.trim().toLowerCase()));
            }

            if (locality != null && !locality.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("locality")), "%" + locality.trim().toLowerCase() + "%"));
            }

            if (minRent != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rent"), minRent));
            }

            if (maxRent != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("rent"), maxRent));
            }

            if (bhk != null) {
                predicates.add(cb.equal(root.get("bhk"), bhk));
            }

            if (furnished != null) {
                predicates.add(cb.equal(root.get("furnished"), furnished));
            }

            if (parking != null) {
                predicates.add(cb.equal(root.get("parking"), parking));
            }

            if (petFriendly != null) {
                predicates.add(cb.equal(root.get("petFriendly"), petFriendly));
            }

            if (availableFrom != null) {
                // If tenant wants it available by availableFrom date, the property availableFrom date must be <= desired date.
                predicates.add(cb.lessThanOrEqualTo(root.get("availableFrom"), availableFrom));
            }

            if (propertyType != null && !propertyType.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("propertyType")), propertyType.trim().toUpperCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
