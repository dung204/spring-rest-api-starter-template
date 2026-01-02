package com.example.modules.auth.dtos;

import com.example.base.utils.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequestDTO {

  @Schema(description = "The email to retrieve the password", example = SwaggerExamples.EMAIL)
  @NotBlank
  @Email
  private String email;
}
