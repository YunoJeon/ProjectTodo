package com.todo.comment.service;

import static com.todo.exception.ErrorCode.COMMENT_NOT_FOUND;
import static com.todo.exception.ErrorCode.FORBIDDEN;
import static com.todo.exception.ErrorCode.PROJECT_NOT_FOUND;

import com.todo.collaborator.entity.Collaborator;
import com.todo.collaborator.service.CollaboratorQueryService;
import com.todo.comment.dto.CommentDto;
import com.todo.comment.dto.CommentResponseDto;
import com.todo.comment.dto.CommentUpdateDto;
import com.todo.comment.entity.Comment;
import com.todo.comment.repository.CommentRepository;
import com.todo.exception.CustomException;
import com.todo.project.entity.Project;
import com.todo.project.service.ProjectQueryService;
import com.todo.todo.entity.Todo;
import com.todo.todo.service.TodoQueryService;
import com.todo.user.entity.User;
import com.todo.user.service.UserQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;

  private final CommentQueryService commentQueryService;

  private final UserQueryService userQueryService;

  private final TodoQueryService todoQueryService;

  private final ProjectQueryService projectQueryService;

  private final CollaboratorQueryService collaboratorQueryService;

  @Transactional
  public void addComment(Authentication auth, Long todoId, CommentDto commentDto) {

    Todo todo = validProject(todoId);

    User commentAuthor = validCommentAuthor(auth, todo);

    commentRepository.save(Comment.of(commentAuthor, todo, commentDto));
  }

  public Page<CommentResponseDto> getComments(Authentication auth, Long todoId,
      int page, int pageSize) {

    Todo todo = validProject(todoId);

    validCommentAuthor(auth, todo);

    Pageable pageable = PageRequest.of(page - 1, pageSize);

    Page<Comment> commentPage = commentQueryService.findByTodo(todo, pageable);

    List<CommentResponseDto> dtoList = commentPage.getContent().stream().map(
        CommentResponseDto::fromEntity).toList();

    return new PageImpl<>(dtoList, pageable, commentPage.getTotalElements());
  }

  public void updateComments(Authentication auth,
      Long todoId, Long commentId, CommentUpdateDto commentUpdateDto) {

    Todo todo = validProject(todoId);

    User commentAuthor = validCommentAuthor(auth, todo);

    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

    if (!commentAuthor.getId().equals(comment.getCommentAuthor().getId())) {
      throw new CustomException(FORBIDDEN);
    }

    comment.update(commentUpdateDto);
  }

  private Todo validProject(Long todoId) {

    Todo todo = todoQueryService.findById(todoId);

    if (todo.getProjectId() == null) {
      throw new CustomException(PROJECT_NOT_FOUND);
    }

    return todo;
  }

  private User validCommentAuthor(Authentication auth, Todo todo) {

    Project project = projectQueryService.findById(todo.getProjectId());

    User commentAuthor = userQueryService.findByEmail(auth.getName());

    Collaborator collaborator =
        collaboratorQueryService.findByProjectAndCollaboratorIsConfirmed(project, commentAuthor);

    if (collaborator == null ||
        !commentAuthor.getId().equals(collaborator.getCollaborator().getId())) {
      throw new CustomException(FORBIDDEN);
    }
    return collaborator.getCollaborator();
  }
}