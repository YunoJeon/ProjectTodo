# Users 테이블
INSERT INTO todo_db.users (create_at, email, name, password, phone, profile_image_url)
VALUES (NOW(), 'test@test.com', '첫 사용자', '12345678', '010-1234-5678', NULL);

# Projects 테이블
INSERT INTO todo_db.projects (created_at, owner_id, description, name)
VALUES (NOW(), 1, '프로젝트 설명', '첫 프로젝트');

# Todos 테이블
INSERT INTO todo_db.todos (is_completed, is_priority, author_id, created_at, due_date, project_id, version, description, title, todo_category)
VALUES (false, false, 1, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 1, 1, '투두 설명', '첫 투두', 'WORK');

# Collaborators 테이블
INSERT INTO todo_db.collaborators (confirm_type, collaborator_id, project_id, role_type)
VALUES ('TRUE', 1, 1, 'EDITOR');

# Comments 테이블
INSERT INTO todo_db.comments (comment_author_id, created_at, deleted_at, parent_comment_id, todo_id, content)
VALUES (1, NOW(), null, null, 1, '첫 댓글');

# Notifications 테이블
INSERT INTO todo_db.notifications (is_read, created_at, user_id, message)
VALUES (false, NOW(), 1, '테스트 알림');

# Snapshots 테이블
INSERT INTO todo_db.snapshots (is_completed, is_priority, todo_category, due_date, todo_id, version, description, title)
VALUES (false, false, 'WORK', DATE_ADD(NOW(), INTERVAL 7 DAY), 1, 1, '투두 설명', '첫 투두');

# Activity Logs 테이블
INSERT INTO todo_db.activity_logs (created_at, project_id, snapshot_id, todo_id, todo_version, action_detail, changer_name, action_type)
VALUES (NOW(), 1, 1, 1, 1, '첫 투두 할일이 생성되었습니다.', '첫 사용자', 'TODO');