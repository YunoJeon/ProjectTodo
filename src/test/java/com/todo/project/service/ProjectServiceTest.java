package com.todo.project.service;

import static com.todo.exception.ErrorCode.FORBIDDEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.todo.collaborator.service.CollaboratorQueryService;
import com.todo.exception.CustomException;
import com.todo.project.dto.ProjectDto;
import com.todo.project.dto.ProjectPageResponseDto;
import com.todo.project.dto.ProjectResponseDto;
import com.todo.project.entity.Project;
import com.todo.project.repository.ProjectRepository;
import com.todo.user.entity.User;
import com.todo.user.service.UserQueryService;
import java.time.LocalDateTime;
import java.util.List;
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
class ProjectServiceTest {

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private UserQueryService userQueryService;

  @Mock
  private ProjectQueryService projectQueryService;

  @Mock
  private CollaboratorQueryService collaboratorQueryService;

  @InjectMocks
  private ProjectService projectService;

  private Authentication auth;
  private User testUser;
  private Project testProject;

  @BeforeEach
  void setUp() {
    auth = new UsernamePasswordAuthenticationToken("test@test.com", null);

    testUser = User.builder()
        .id(1L)
        .email("test@test.com")
        .name("이름")
        .build();

    testProject = Project.builder()
        .id(1L)
        .owner(testUser)
        .name("프로젝트")
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("프로젝트 생성이 성공한다")
  void creat_project_success() {
    // given
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    // when
    projectService.createProject(auth, new ProjectDto("프로젝트", null));
    // then
    verify(projectRepository).save(any(Project.class));
  }

  @Test
  @DisplayName("프로젝트 목록 조회가 성공한다")
  void get_projects_success() {
    // given
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(projectRepository.findProjectByUser(testUser, PageRequest.of(0, 10))).thenReturn(
        new PageImpl<>(List.of(testProject)));
    // when
    Page<ProjectPageResponseDto> projects = projectService.getProjects(auth, 1, 10);
    // then
    assertEquals(testProject.getId(), projects.getContent().get(0).id());
    assertEquals(testProject.getName(), projects.getContent().get(0).name());
  }

  @Test
  @DisplayName("프로젝트 상세 조회가 성공한다")
  void get_project_detail_success() {
    // given
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(projectQueryService.findById(1L)).thenReturn(testProject);
    // when
    ProjectResponseDto projectResponseDto = projectService.getProjectDetail(auth, 1L);
    // then
    assertEquals(testProject.getId(), projectResponseDto.id());
    assertEquals(testProject.getName(), projectResponseDto.name());
    assertEquals(testProject.getOwner().getName(), projectResponseDto.ownerName());
  }

  @Test
  @DisplayName("소유자가 아니면 프로젝트 상세 조회가 실패한다")
  void get_project_detail_failure_owner_mismatch() {
    // given
    User otherUser = User.builder().id(2L).email("other@email.com").build();
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(projectQueryService.findById(1L)).thenReturn(
        Project.builder().id(2L).owner(otherUser).build()
    );
    // when
    CustomException e = assertThrows(CustomException.class,
        () -> projectService.getProjectDetail(auth, 1L));
    // then
    assertEquals(FORBIDDEN, e.getErrorCode());
  }

  @Test
  @DisplayName("프로젝트 수정이 성공한다")
  void update_project_success() {
    // given
    when(userQueryService.findByEmail(testUser.getEmail())).thenReturn(testUser);
    when(projectQueryService.findById(1L)).thenReturn(testProject);
    // when
    ProjectResponseDto projectResponseDto = projectService.updateProject(auth, 1L,
        new ProjectDto("변경", null));
    // then
    assertEquals(projectResponseDto.name(), testProject.getName());
  }
}