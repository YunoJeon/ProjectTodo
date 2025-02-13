package com.todo.collaborator.service;

import static com.todo.collaborator.type.ConfirmType.FALSE;
import static com.todo.collaborator.type.ConfirmType.TRUE;
import static com.todo.collaborator.type.RoleType.EDITOR;
import static com.todo.collaborator.type.RoleType.VIEWER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.todo.activity.service.ActivityLogService;
import com.todo.collaborator.dto.CollaboratorDto;
import com.todo.collaborator.dto.CollaboratorsDto;
import com.todo.collaborator.entity.Collaborator;
import com.todo.collaborator.repository.CollaboratorRepository;
import com.todo.notification.service.NotificationService;
import com.todo.project.entity.Project;
import com.todo.project.service.ProjectQueryService;
import com.todo.user.entity.User;
import com.todo.user.service.UserQueryService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class CollaboratorServiceTest {

  @Mock
  private UserQueryService userQueryService;

  @Mock
  private ProjectQueryService projectQueryService;

  @Mock
  private CollaboratorQueryService collaboratorQueryService;

  @Mock
  private CollaboratorRepository collaboratorRepository;

  @Mock
  private NotificationService notificationService;

  @Mock
  private ActivityLogService activityLogService;

  @InjectMocks
  private CollaboratorService collaboratorService;

  private User owner;
  private User invitedUser;
  private Project project;
  private Collaborator collaboratorOwner;
  private Collaborator collaboratorInvited;
  private Authentication auth;

  @BeforeEach
  void setUp() {

    owner = User.builder()
        .id(1L)
        .email("test@test.com")
        .name("소유자")
        .build();

    invitedUser = User.builder()
        .id(2L)
        .email("test2@test.com")
        .name("초대받음")
        .build();

    project = Project.builder()
        .id(1L)
        .owner(owner)
        .name("프로젝트")
        .build();

    collaboratorOwner = Collaborator.of(owner, project, EDITOR, TRUE);
    collaboratorInvited = Collaborator.of(owner, project, VIEWER, FALSE);

    auth = new UsernamePasswordAuthenticationToken(owner.getEmail(), null);
  }

  @Test
  @DisplayName("협업자 추가가 성공한다")
  void add_collaborator_success() {
    // given
    when(userQueryService.findByEmail(owner.getEmail())).thenReturn(owner);
    when(projectQueryService.findById(project.getId())).thenReturn(project);
    when(userQueryService.findById(invitedUser.getId())).thenReturn(invitedUser);

    CollaboratorDto collaboratorDto = new CollaboratorDto(invitedUser.getId(), VIEWER);
    // when
    collaboratorService.addCollaborator(auth, project.getId(), collaboratorDto);
    // then
    verify(collaboratorRepository).save(argThat(collaborator ->
        collaborator.getCollaborator().getId().equals(owner.getId()) &&
        collaborator.getRoleType() == EDITOR &&
        collaborator.getConfirmType() == TRUE));

    verify(collaboratorRepository).save(argThat(collaborator ->
        collaborator.getCollaborator().getId().equals(invitedUser.getId()) &&
        collaborator.getRoleType() == VIEWER &&
        collaborator.getConfirmType() == FALSE));
  }

  @Test
  @DisplayName("협업자 목록 조회에 성공한다")
  void get_collaborator_success() {
    // given
    when(userQueryService.findByEmail(owner.getEmail())).thenReturn(owner);
    when(projectQueryService.findById(project.getId())).thenReturn(project);
    when(collaboratorQueryService.existsByProjectAndCollaboratorAndIsConfirmed(
        project, owner)).thenReturn(true);

    List<Collaborator> collaborators = List.of(collaboratorOwner, collaboratorInvited);
    when(collaboratorQueryService.findByProject(project)).thenReturn(collaborators);
    // when
    List<CollaboratorsDto> result = collaboratorService.getCollaborators(auth, project.getId());
    // then
    assertEquals(2, result.size());
  }

  @Test
  @DisplayName("협업자 수정에 성공한다")
  void update_collaborator_success() {
    // given
    when(userQueryService.findByEmail(owner.getEmail())).thenReturn(owner);
    when(projectQueryService.findById(project.getId())).thenReturn(project);
    when(collaboratorQueryService.findById(collaboratorInvited.getId())).thenReturn(collaboratorInvited);

    List<Collaborator> collaborators = List.of(collaboratorOwner, collaboratorInvited);
    when(collaboratorQueryService.findByProject(project)).thenReturn(collaborators);
    // when
    collaboratorService.updateCollaborator(auth, project.getId(), collaboratorInvited.getId(), EDITOR);
    // then
    assertEquals(EDITOR, collaboratorInvited.getRoleType());
  }

  @Test
  @DisplayName("협업자 삭제에 성공한다")
  void delete_collaborator_success() {
    // given
    when(userQueryService.findByEmail(owner.getEmail())).thenReturn(owner);
    when(projectQueryService.findById(project.getId())).thenReturn(project);
    when(collaboratorQueryService.findById(collaboratorInvited.getId())).thenReturn(collaboratorInvited);

    when(collaboratorQueryService.findByProject(project)).thenReturn(List.of(collaboratorOwner));
    // when
    List<CollaboratorsDto> result = collaboratorService.deleteCollaborator(auth, project.getId(), collaboratorInvited.getId());
    // then
    assertEquals(1, result.size());
  }
}