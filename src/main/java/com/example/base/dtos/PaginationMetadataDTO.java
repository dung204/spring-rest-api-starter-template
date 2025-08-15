package com.example.base.dtos;

import lombok.Data;
import org.springframework.data.domain.Pageable;

@Data
public class PaginationMetadataDTO {

  private final int currentPage;

  private final int pageSize;

  private final int total;

  private final int totalPage;

  private final boolean hasNextPage;

  private final boolean hasPreviousPage;

  public PaginationMetadataDTO(Pageable pageable, int total) {
    this.currentPage = pageable.getPageNumber();
    this.pageSize = pageable.getPageSize();
    this.total = total;
    this.totalPage = (int) Math.ceil((double) total / pageable.getPageSize());
    this.hasNextPage = currentPage < totalPage - 1;
    this.hasPreviousPage = currentPage > 0;
  }
}
