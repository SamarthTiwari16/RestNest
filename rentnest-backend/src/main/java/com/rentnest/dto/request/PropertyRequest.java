package com.rentnest.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PropertyRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 100) String locality,
        @NotNull @DecimalMin(value = "0.01", message = "must be greater than 0") BigDecimal rent,
        @NotNull @Min(value = 1, message = "must be at least 1") Integer bhk,
        @NotBlank @Size(max = 50) String propertyType,
        @NotNull Boolean furnished,
        @NotNull Boolean petFriendly,
        @NotNull Boolean parking,
        @NotNull LocalDate availableFrom
) {}
