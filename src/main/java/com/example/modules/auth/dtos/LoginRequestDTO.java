package com.example.modules.auth.dtos;

import com.example.base.utils.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDTO {

  @Schema(description = "The email of the user", example = SwaggerExamples.EMAIL)
  @NotBlank
  @Email
  private String email;

  @Schema(description = "The password of the user", example = SwaggerExamples.PASSWORD)
  @NotBlank
  private String password;
}
