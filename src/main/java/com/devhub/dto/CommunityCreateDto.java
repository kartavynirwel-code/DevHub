package com.devhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityCreateDto {
    
    @NotBlank(message = "Community name is required")
    @Size(min = 2, max = 50, message = "Community name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Community description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;
}
