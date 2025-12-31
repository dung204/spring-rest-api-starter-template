package com.example.modules.posts.services;

import static com.example.base.enums.ErrorCode.POST_NOT_FOUND;

import com.example.base.exceptions.ResourceNotFoundException;
import com.example.base.utils.ObjectUtils;
import com.example.modules.posts.dtos.CreatePostDTO;
import com.example.modules.posts.dtos.MePostsSearchDTO;
import com.example.modules.posts.dtos.PostResponseDTO;
import com.example.modules.posts.dtos.PostsSearchDTO;
import com.example.modules.posts.dtos.UpdatePostDTO;
import com.example.modules.posts.entities.Post;
import com.example.modules.posts.repositories.PostsRepository;
import com.example.modules.posts.utils.PostMapper;
import com.example.modules.posts.utils.PostsSpecification;
import com.example.modules.users.entities.User;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostsService {

  private final PostsRepository postsRepository;
  private final PostMapper postMapper;

  public Page<PostResponseDTO> findAllPublicPosts(PostsSearchDTO postsSearchDTO) {
    return postsRepository
      .findAll(
        PostsSpecification.builder()
          .containsTitle(postsSearchDTO.getTitle())
          .ownedBy(postsSearchDTO.getUser())
          .publicOnly()
          .notDeleted()
          .build(),
        postsSearchDTO.toPageRequest()
      )
      .map(postMapper::toPostResponseDTO);
  }

  public Page<PostResponseDTO> findAllPostsOfCurrentUser(
    MePostsSearchDTO postsSearchDTO,
    User currentUser
  ) {
    return postsRepository
      .findAll(
        PostsSpecification.builder()
          .containsTitle(postsSearchDTO.getName())
          .ownedBy(currentUser.getId())
          .build(),
        postsSearchDTO.toPageRequest()
      )
      .map(postMapper::toPostResponseDTO);
  }

  @SuppressWarnings("null")
  public PostResponseDTO findPostById(String id, User currentUser) {
    return postMapper.toPostResponseDTO(
      postsRepository
        .findOne(
          PostsSpecification.builder()
            .withId(id)
            .<PostsSpecification>conditionally(
              currentUser == null,
              spec -> spec.publicOnly().notDeleted(),
              spec -> spec.publicOrOwnedBy(currentUser.getId())
            )
            .build()
        )
        .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND))
    );
  }

  public PostResponseDTO createPost(CreatePostDTO createPostDTO, User currentUser) {
    return postMapper.toPostResponseDTO(
      postsRepository.save(
        Post.builder()
          .user(currentUser)
          .title(createPostDTO.getTitle())
          .content(createPostDTO.getContent())
          .isPublic(createPostDTO.getIsPublic().orElse(false))
          .build()
      )
    );
  }

  public PostResponseDTO updatePost(String id, UpdatePostDTO updatePostDTO, User currentUser) {
    Post post = postsRepository
      .findOne(
        PostsSpecification.builder().ownedBy(currentUser.getId()).notDeleted().withId(id).build()
      )
      .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));

    ObjectUtils.assign(post, updatePostDTO);
    return postMapper.toPostResponseDTO(postsRepository.save(post));
  }

  public void deletePost(String id, User currentUser) {
    Post post = postsRepository
      .findOne(
        PostsSpecification.builder().ownedBy(currentUser.getId()).notDeleted().withId(id).build()
      )
      .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));

    post.setDeletedTimestamp(Instant.now());
    postsRepository.save(post);
  }

  public PostResponseDTO restorePost(String id, User currentUser) {
    Post post = postsRepository
      .findOne(
        PostsSpecification.builder().ownedBy(currentUser.getId()).deletedOnly().withId(id).build()
      )
      .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));

    post.setDeletedTimestamp(null);
    return postMapper.toPostResponseDTO(postsRepository.save(post));
  }
}
