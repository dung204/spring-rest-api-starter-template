package com.example.modules.auth.dtos;

import com.example.base.utils.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDTO {

  @Schema(
    description = "The token attached to the reset password link",
    example = SwaggerExamples.ACCESS_TOKEN
  )
  @NotBlank
  private String token;

  @Schema(description = "The new password to set", example = SwaggerExamples.PASSWORD)
  @NotBlank
  @Size(min = 6, message = "{auth.password.weak}")
  private String password;
}
