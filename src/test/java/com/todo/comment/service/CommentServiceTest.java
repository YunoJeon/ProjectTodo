package com.todo.comment.service;

import static com.todo.exception.ErrorCode.FORBIDDEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.todo.collaborator.entity.Collaborator;
import com.todo.collaborator.service.CollaboratorQueryService;
import com.todo.comment.dto.CommentDto;
import com.todo.comment.dto.CommentResponseDto;
import com.todo.comment.dto.CommentUpdateDto;
import com.todo.comment.entity.Comment;
import com.todo.comment.repository.CommentRepository;
import com.todo.exception.CustomException;
import com.todo.notification.service.NotificationService;
import com.todo.project.entity.Project;
import com.todo.project.service.ProjectQueryService;
import com.todo.todo.entity.Todo;
import com.todo.todo.service.TodoQueryService;
import com.todo.user.entity.User;
import com.todo.user.service.UserQueryService;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private CommentQueryService commentQueryService;

  @Mock
  private UserQueryService userQueryService;

  @Mock
  private TodoQueryService todoQueryService;

  @Mock
  private ProjectQueryService projectQueryService;

  @Mock
  private CollaboratorQueryService collaboratorQueryService;

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private CommentService commentService;

  private Authentication auth;
  private User testUser;
  private Todo todo;
  private Project project;
  private Collaborator collaborator;
  private Comment comment;

  @BeforeEach
  void setUp() {
    auth = new UsernamePasswordAuthenticationToken("test@test.com", null);

    testUser = User.builder()
        .id(1L)
        .email("test@test.com")
        .name("이름")
        .profileImageUrl("url")
        .build();

    todo = Todo.builder()
        .id(5L)
        .projectId(10L)
        .build();

    project = Project.builder()
        .id(10L)
        .build();

    collaborator = Collaborator.builder()
        .id(11L)
        .collaborator(testUser)
        .project(project)
        .build();

    comment = Comment.builder()
        .id(100L)
        .parentCommentId(null)
        .content("댓글요")
        .commentAuthor(testUser)
        .build();
  }

  @Test
  @DisplayName("댓글 추가에 성공한다")
  void add_comment_success() {
    // given
    when(todoQueryService.findById(todo.getId())).thenReturn(todo);
    when(projectQueryService.findById(project.getId())).thenReturn(project);
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(collaboratorQueryService.findByProjectAndCollaboratorIsConfirmed(
        project, testUser)).thenReturn(collaborator);
    // when
    commentService.addComment(auth, todo.getId(), new CommentDto(null, "댓글요"));
    // then
    verify(commentRepository).save(any(Comment.class));
  }

  @Test
  @DisplayName("댓글 목록 조회에 성공한다")
  void get_comments_success() {
    // given
    when(todoQueryService.findById(todo.getId())).thenReturn(todo);
    when(projectQueryService.findById(project.getId())).thenReturn(project);
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(collaboratorQueryService.findByProjectAndCollaboratorIsConfirmed(
        project, testUser)).thenReturn(collaborator);

    Page<Comment> commentPage = new PageImpl<>(Collections.singletonList(comment),
        PageRequest.of(0, 10), 1);
    when(commentQueryService.findByTodo(todo, PageRequest.of(0, 10))).thenReturn(commentPage);
    // when
    Page<CommentResponseDto> result = commentService.getComments(auth, todo.getId(), 1, 10);
    // then
    assertEquals(1, result.getTotalElements());
    assertEquals("댓글요", result.getContent().get(0).content());
  }

  @Test
  @DisplayName("댓글 수정에 성공한다")
  void update_comment_success() {
    // given
    when(todoQueryService.findById(todo.getId())).thenReturn(todo);
    when(projectQueryService.findById(project.getId())).thenReturn(project);
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(collaboratorQueryService.findByProjectAndCollaboratorIsConfirmed(
        project, testUser)).thenReturn(collaborator);
    when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));

    CommentUpdateDto updateDto = new CommentUpdateDto("수정요", false);
    // when
    commentService.updateComments(auth, todo.getId(), comment.getId(), updateDto);
    // then
    assertEquals("수정요", comment.getContent());
  }

  @Test
  @DisplayName("댓글 작성자가 다르면 수정에 실패한다")
  void update_comment_failure_forbidden() {
    // given
    when(todoQueryService.findById(todo.getId())).thenReturn(todo);
    when(projectQueryService.findById(project.getId())).thenReturn(project);
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(collaboratorQueryService.findByProjectAndCollaboratorIsConfirmed(
        project, testUser)).thenReturn(collaborator);

    User anotherUser = User.builder()
        .id(300L)
        .build();
    Comment anotherComment = Comment.builder()
        .id(200L)
        .commentAuthor(anotherUser)
        .build();
    when(commentRepository.findById(anotherComment.getId())).thenReturn(
        Optional.of(anotherComment));
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> commentService.updateComments(auth, todo.getId(), anotherComment.getId(),
            new CommentUpdateDto("수정요", false)));
    // then
    assertEquals(FORBIDDEN, e.getErrorCode());
  }
}