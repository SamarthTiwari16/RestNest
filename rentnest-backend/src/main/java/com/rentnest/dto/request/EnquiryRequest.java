package com.rentnest.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EnquiryRequest(
        String message,

        @NotNull(message = "Move-in date is required")
        @FutureOrPresent(message = "Move-in date must be in the present or future")
        LocalDate moveInDate,

        @NotNull(message = "Number of occupants is required")
        @Min(value = 1, message = "There must be at least 1 occupant")
        Integer occupants
) {}
