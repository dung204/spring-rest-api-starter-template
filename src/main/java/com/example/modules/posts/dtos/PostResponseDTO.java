package com.example.modules.posts.dtos;

import com.example.base.dtos.EntityDTO;
import com.example.modules.users.dtos.UserProfileDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class PostResponseDTO extends EntityDTO {

  @Schema(description = "The title of the post", example = "Title")
  private String title;

  @Schema(description = "The content of the post", example = "Content")
  private String content;

  @Schema(description = "The author of the post")
  private UserProfileDTO user;

  @Schema(description = "Whether the post is public or not")
  private Boolean isPublic;
}
