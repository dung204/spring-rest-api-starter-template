package com.example.base.dtos;

import com.example.base.utils.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@RequiredArgsConstructor
@AllArgsConstructor
public abstract class ResponseDTO {

  @Schema(example = SwaggerExamples.STATUS_CODE, description = "HTTP status code")
  @Builder.Default
  protected int status = 200;

  protected String message;

  @Builder.Default
  @Schema(example = SwaggerExamples.TIMESTAMP, description = "Response timestamp")
  protected String timestamp = Instant.now().toString();
}
