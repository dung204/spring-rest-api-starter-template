package com.example.modules.users.dtos;

import com.example.base.utils.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class UpdateProfileDTO {

  @Schema(
    description = "The first name of the user",
    example = SwaggerExamples.FIRST_NAME,
    implementation = String.class
  )
  @Size(min = 2)
  private JsonNullable<String> firstName = JsonNullable.undefined();

  @Schema(
    description = "The last name of the user",
    example = SwaggerExamples.LAST_NAME,
    implementation = String.class
  )
  @Size(min = 2)
  private JsonNullable<String> lastName = JsonNullable.undefined();
}
