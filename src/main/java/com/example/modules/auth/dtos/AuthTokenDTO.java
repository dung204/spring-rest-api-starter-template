package com.example.modules.auth.dtos;

import com.example.base.utils.SwaggerExamples;
import com.example.modules.users.dtos.UserProfileDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenDTO {

  @Schema(
    description = "Access token for authenticated user",
    example = SwaggerExamples.ACCESS_TOKEN
  )
  @NonNull
  private String accessToken;

  @Schema(hidden = true)
  @JsonIgnore
  @NonNull
  private String refreshToken;

  @NonNull
  private UserProfileDTO user;
}
