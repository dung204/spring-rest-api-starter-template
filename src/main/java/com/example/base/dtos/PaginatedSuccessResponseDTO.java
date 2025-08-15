package com.example.base.dtos;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class PaginatedSuccessResponseDTO<T> extends ResponseDTO {

  @Builder.Default
  private final List<T> data = Collections.emptyList();

  @NonNull
  private final PaginatedSuccessResponseMetadataDTO metadata;
}
