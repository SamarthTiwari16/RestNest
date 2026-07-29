package com.rentnest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "Property enquiry request payload")
public record EnquiryRequest(
        @Schema(description = "Personal introduction message to the property owner", example = "Hi, I am interested in this flat. I work nearby.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String message,

        @Schema(description = "Intended move-in date (must be today or in the future)", example = "2026-08-01")
        @NotNull(message = "Move-in date is required")
        @FutureOrPresent(message = "Move-in date must be in the present or future")
        LocalDate moveInDate,

        @Schema(description = "Total number of occupants intending to live in the property", example = "2")
        @NotNull(message = "Number of occupants is required")
        @Min(value = 1, message = "There must be at least 1 occupant")
        Integer occupants
) {}
