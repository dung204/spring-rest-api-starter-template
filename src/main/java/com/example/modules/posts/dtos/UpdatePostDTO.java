package com.example.modules.posts.dtos;

import com.example.base.utils.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UpdatePostDTO {

  @Schema(
    description = "The title of the post",
    example = SwaggerExamples.EMAIL,
    implementation = String.class
  )
  @NotNull
  @Length(max = 255)
  private JsonNullable<String> title = JsonNullable.undefined();

  @Schema(description = "The content of the user", example = SwaggerExamples.EMAIL)
  @NotNull
  private JsonNullable<String> content = JsonNullable.undefined();

  @Schema(description = "Whether the post is public or not", implementation = Boolean.class)
  private JsonNullable<Boolean> isPublic = JsonNullable.undefined();
}
