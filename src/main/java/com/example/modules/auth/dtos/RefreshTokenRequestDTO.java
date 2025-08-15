package com.example.modules.auth.dtos;

import com.example.base.utils.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RefreshTokenRequestDTO {

  @Schema(
    description = "The JWT token to create new (refresh) the access token if it expires",
    example = SwaggerExamples.REFRESH_TOKEN
  )
  @NotBlank
  private String refreshToken;
}
