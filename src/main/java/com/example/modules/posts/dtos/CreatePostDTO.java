package com.example.modules.posts.dtos;

import com.example.base.utils.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class CreatePostDTO {

  @Schema(description = "The title of the post", example = SwaggerExamples.EMAIL)
  @NotBlank
  @Length(max = 255)
  private String title;

  @Schema(description = "The content of the user", example = SwaggerExamples.EMAIL)
  @NotBlank
  private String content;

  @Schema(description = "Whether the post is public or not", implementation = Boolean.class)
  private JsonNullable<Boolean> isPublic = JsonNullable.undefined();
}
