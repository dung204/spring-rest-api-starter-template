package com.example.base.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Order {
  ASC("ASC"),
  DESC("DESC");

  private final String value;
}
