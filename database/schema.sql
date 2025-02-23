create table if not exists todo_db.users
(
    create_at         datetime(6)  null,
    id                bigint auto_increment primary key,
    email             varchar(255) not null,
    name              varchar(255) not null,
    password          varchar(255) not null,
    phone             varchar(255) not null,
    profile_image_url varchar(255) null,
    constraint UK6dotkott2kjsp8vw4d0m25fb7
        unique (email)
);

create table todo_db.todos
(
    is_completed  bit                         not null,
    is_priority   bit                         not null,
    author_id     bigint                      not null,
    created_at    datetime(6)                 null,
    due_date      datetime(6)                 null,
    id            bigint auto_increment primary key,
    project_id    bigint                      null,
    version       bigint                      null,
    description   varchar(255)                null,
    title         varchar(255)                not null,
    todo_category enum ('INDIVIDUAL', 'WORK') null,
    constraint FKpnc6mlrfi9yhs4ocwmlpudnvj
        foreign key (author_id) references todo_db.users (id)
);

create table todo_db.projects
(
    created_at  datetime(6)  null,
    id          bigint auto_increment primary key,
    owner_id    bigint       not null,
    description varchar(255) null,
    name        varchar(255) not null,
    constraint FKmueqy6cpcwpfl8gnnag4idjt9
        foreign key (owner_id) references todo_db.users (id)
);

create table if not exists todo_db.collaborators
(
    collaborator_id bigint                    not null,
    id              bigint auto_increment primary key,
    project_id      bigint                    not null,
    confirm_type    enum ('FALSE', 'TRUE')    null,
    role_type       enum ('EDITOR', 'VIEWER') null,
    constraint FK7s91b5rsk225mesbtoqg74hh1
        foreign key (project_id) references todo_db.projects (id),
    constraint FKhnstx2yryc1b4p9j8v82atnwo
        foreign key (collaborator_id) references todo_db.users (id)
);

create table todo_db.comments
(
    comment_author_id bigint       not null,
    created_at        datetime(6)  null,
    id                bigint auto_increment primary key,
    todo_id           bigint       not null,
    content           varchar(255) null,
    constraint FKe1y9i9m8qipbmqcjkmoaqqnka
        foreign key (comment_author_id) references todo_db.users (id),
    constraint FKhq2jvyd0htxaj4avgceuigt4c
        foreign key (todo_id) references todo_db.todos (id)
);

create table todo_db.notifications
(
    is_invitation bit          not null,
    is_read       bit          not null,
    created_at    datetime(6)  null,
    id            bigint auto_increment primary key,
    project_id    bigint       null,
    user_id       bigint       not null,
    message       varchar(255) null,
    constraint FK9y21adhxn0ayjhfocscqox7bh
        foreign key (user_id) references todo_db.users (id)
);

create table if not exists todo_db.snapshots
(
    is_completed  bit                         not null,
    is_priority   bit                         not null,
    due_date      datetime(6)                 null,
    id            bigint auto_increment primary key,
    todo_id       bigint                      not null,
    version       bigint                      null,
    description   varchar(255)                null,
    title         varchar(255)                null,
    todo_category enum ('INDIVIDUAL', 'WORK') null,
    constraint FKp6o3xhk93pff5hhss9gy604vq
        foreign key (todo_id) references todo_db.todos (id)
);

create table if not exists todo_db.activity_logs
(
    created_at    datetime(6)              null,
    id            bigint auto_increment primary key,
    project_id    bigint                   not null,
    snapshot_id   bigint                   null,
    todo_id       bigint                   null,
    todo_version  bigint                   null,
    action_detail varchar(255)             null,
    changer_name  varchar(255)             null,
    action_type   enum ('PROJECT', 'TODO') null,
    constraint FK4xmjf1hke4tpxpftoxd1rllax
        foreign key (todo_id) references todo_db.todos (id),
    constraint FKavkl7v9l6yfc9hhpaqi1q6f5u
        foreign key (project_id) references todo_db.projects (id)
);