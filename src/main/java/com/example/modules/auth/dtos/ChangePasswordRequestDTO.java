package com.example.modules.auth.dtos;

import com.example.base.utils.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangePasswordRequestDTO {

  @Schema(
    description = "The old password of the user (not required if the user does not have a password)",
    example = SwaggerExamples.PASSWORD
  )
  private String password;

  @Schema(
    description = "The old password of the user (not required if the user does not have a password)",
    example = SwaggerExamples.PASSWORD
  )
  @NotBlank
  private String newPassword;
}
