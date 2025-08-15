package com.example.base.dtos;

import io.jsonwebtoken.lang.Collections;
import java.util.HashMap;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@Builder
public class PaginatedSuccessResponseMetadataDTO {

  @NonNull
  private final PaginationMetadataDTO pagination;

  @Builder.Default
  private final List<OrderDTO> order = Collections.emptyList();

  @Builder.Default
  private final HashMap<String, Object> filters = new HashMap<>();
}
