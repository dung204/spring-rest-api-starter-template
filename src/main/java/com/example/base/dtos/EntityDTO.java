package com.example.base.dtos;

import com.example.base.utils.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Base Data Transfer Object (DTO) class that provides common fields for all entity DTOs.
 * This class serves as a parent class for other DTO classes and includes standard
 * audit fields that are commonly used across different entities.
 *
 * @apiNote When extending this class, please remember to use {@code @SuperBuilder} so that DTO
 * using MapStruct works.
 */
@SuperBuilder
@Getter
@Setter
public class EntityDTO {

  @Schema(description = "The UUID of the entity", example = SwaggerExamples.UUID)
  protected String id;

  @Schema(
    description = "The timestamp indicating when the entity is created",
    example = SwaggerExamples.TIMESTAMP
  )
  protected String createdTimestamp;

  @Schema(
    description = "The timestamp indicating when the entity is last modified",
    example = SwaggerExamples.TIMESTAMP
  )
  protected String updatedTimestamp;

  @Schema(
    description = "The timestamp indicating when the entity is soft deleted",
    example = SwaggerExamples.TIMESTAMP,
    nullable = true
  )
  protected String deletedTimestamp;
}
