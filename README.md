<div align="center">


# ☑️ To-Do: 할 일 정리 & 협업 도구


---

<img src="https://img.shields.io/badge/Java-C00000?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">  <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">  <img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white"> <img src="https://img.shields.io/badge/jpa-59666C?style=for-the-badge&logo=jpa&logoColor=white"> 

<img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">  <img src="https://img.shields.io/badge/json web tokens-000000?style=for-the-badge&logo=json web tokens&logoColor=white"> <img src="https://img.shields.io/badge/j unit-25A162?style=for-the-badge&logo=j unit&logoColor=white">  <img src="https://img.shields.io/badge/mockito-3111324?style=for-the-badge&logo=mockito&logoColor=white">
<img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white">

<img src="https://img.shields.io/badge/MyBatis-006600?style=for-the-badge&logo=mybatis&logoColor=white"> <img src="https://img.shields.io/badge/PageHelper-E25A1C?style=for-the-badge&logo=pagehelper&logoColor=white"> <img src="https://img.shields.io/badge/Thumbnailator-4285F4?style=for-the-badge&logo=thumbnailrator&logoColor=white"> <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white">

---

<img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white"> <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=white"> <img src="https://img.shields.io/badge/AntDesign-0170FE?style=for-the-badge&logo=antdesign&logoColor=white"> <img src="https://img.shields.io/badge/ReactRouter-CA4245?style=for-the-badge&logo=reactrouter&logoColor=white">

<img src="https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=axios&logoColor=white"> <img src="https://img.shields.io/badge/Moment-5A6AB1?style=for-the-badge&logo=moment&logoColor=white"> <img src="https://img.shields.io/badge/ReactWindow-80B3FF?style=for-the-badge&logo=react&logoColor=white"> <img src="https://img.shields.io/badge/ReactWindowInfiniteLoader-020B24?style=for-the-badge&logo=react&logoColor=white">

---


`Notion 링크` : [Notion](https://www.notion.so/To-Do-19303366e7b380fab3bbf45b7c6e4cba?pvs=4)

</div>

---

`📆 BE 개발기간` : 2025.2.7 - 2025.2.14

`📆 FE 개발기간` : 2025.2.14 - 2025.2.23

---

<div align="center">

## 🗺️ ERD

<img width="828" alt="스크린샷 2025-02-23 오후 2 15 05" src="https://github.com/user-attachments/assets/b3e9a512-06b8-45b3-83e6-1640740498f7" />

---

## 📝 API 명세서
`Notion 링크 첨부` : [API](https://www.notion.so/API-19a03366e7b38093b18ffade14f2db37?pvs=4)

---

## ⭐️ 주요 기능
</div>


### 1. 회원관련

- 아이디는 이메일 값으로 하며 비밀번호는 해싱 되어 암호화 처리
- 이메일과 중복 체크 후 가입 진행
- 프로필 이미지 설정 가능
    - 설정하지 않으면 기본 이미지로 보여지며, 이 이미지는 댓글등에 사용됨
- refresh 토큰을 cookie, redis 에 저장 및 활용하여 보안 강화

### 2. 기본기능 - 개인 To-Do 관리

- 간단한 제목과 추가 설명으로 등록 가능
- 마감기한 설정
- 개인용, 업무용 등으로 카테고리 분류
- 우선사항(중요도) 설정 가능
- 동적 필터링 으로 인해 보고싶은 To - Do 조회 가능

### 3. 협업 기능 - 특정 To - Do 를 Project 로 관리

- 사용자는 원하는 To - Do 를 프로젝트 하위에 두어 여러 개의 To - Do 관리 가능
- 사용자는 여러 개의 프로젝트를 관리 가능
- 프로젝트 생성 시 프로젝트명 과  추가적인 설명 기재 가능

### 4. 협업 기능 - Project 에 공동 작업자 관리

- 사용자가 원하는 특정  프로젝트 에 공동작업자를 추가할 수 있음
- 공동작업자는 To - Do 를 같이 확인하고 editor / viewer 로 역할 구분 가능
- 공동 작업자로 초대된 회원은 승인을 하면 프로젝트에 참여 가능

### 5. 댓글 기능

- 프로젝트에 속한 사용자들은 프로젝트에 속한 특정 To - Do 에 댓글 추가 가능
- 댓글 내용은 수정 및 삭제 가능

### 6. 알림 기능

- 프로젝트 내의 각 활동에 대해 적절한 사용자들에게 알림 전송
- 누군가가 나를 프로젝트에 초대하였을 때 알림 전송
- 해당 프로젝트에 참여하였을 때 팀원들에게 알림 전송
- 해당 프로젝트에 누군가가 제외되었을 때 팀원들에게 알림 전송
- 해당 프로젝트에 To - Do 가 생성되었을 때 팀원들에게 알림 전송
- 해당 프로젝트의 To - Do 의 상태가 변경되었을 때 팀원들에게 알림 전송
- 해당 프로젝트의 To - Do 에 댓글이 달리면 팀원들에게 알림 전송
- 알림 목록을 조회하여 읽지않은 알림 조회
- 알림을 읽음처리 하면 알림목록에서 사라지는 기능

### 7. 활동 로그 기능

- 협업 프로젝트의 To - Do 가 생성되면 활동 로그 기록
- 협업 프로젝트의 To - Do 가 수정되면 활동 로그 기록
- 협업 프로젝트의 To - Do 가 완료되면 활동 로그 기록
- 협업 프로젝트에 To - Do 가 추가되면 활동 로그 기록
- 협업 프로젝트에 팀원이 제외되면 활동 로그 기록
- 해당 프로젝트의 활동 기록 조회 가능

### 8. 특정 로그 롤백 기능

- 모든 할일은 버전관리가 되며, 활동 로그에 기록된 로그 중 특정 시점으로 To - Do 롤백이 가능


---

<div align="center">


## ⭐️ BE - 사용한 라이브러리

</div>

- `spring-boot-starter-data-jpa`: JPA 를 이용해 DB 와의 객체 관계 매핑을 쉽게 구현할 수 있도록 도와주는 라이브러리. 간편한 CRUD 처리
- `spring-boot-starter-data-redis`: refresh 토큰을 DB 에 저장 시 비교적 느린 속도로 인해 사용자의 불편함을 초래할 수 있다고 생각되어 빠른 읽음처리가 가능한 redis 사용
- `io.jsonwebtoken:jjwt-api, jjwt-impl, jjwt-jackson`: JSON WEB Token 생성, 검증, 파싱하는 데 필요한 기능. 해당 기능으로 JWT 기반 인증 및 인가 구현
- `mybatis-spring-boot-starter`: 동적 쿼리 구현 및 추가적인 Mybatis 학습을 하고자 사용.
- `pagehelper-spring-boot-starter`: Mybatis 와 연동하여 페이징 처리를 쉽게 구현할수 있도록 도와주는 라이브러리
- `thumbnailator`: 이미지 리사이징 및 처리 기능

---

<div align="center">

## ⭐️ FE - 사용한 라이브러리 및 컴포넌트

</div>

- `Ant Design`: 간결한 디자인 시스템, 일관된 UI 스타일
  - `Button`: 버튼 스일링
  - `Modal`: 할일 및 프로젝트 상세정보 보기 팝업
  - `Drawer`: 사이드 메뉴 구현
  - `Select`: 필터링 및 카테고리 선택
  - `Typography`: 텍스트 스타일 적용
  - `List`: 목록 데이터 렌더링
- `react-router-dom`: 클라이언트 사이드 라우팅 지원하여 페이지 전환 처리
  - `useNavigate`: 페이지 이동
- `react-window`: 많은 데이터를 효율적으로 렌더링하기 위한 가상 리스트 컴포넌트
- `react-window-infinite-loader`: 무한 스크롤 구현
- `Axios`: HTTP 요청을 쉽게 관리하고 API 통신을 지원

---


## 🏃‍♂️ 소스 빌드 및 실행방법

</div>

### 1. 환경설정
- 프로젝트 실행에 필요한 주요 설정은 `scr/main/resources/application.properties` 파일에 정의
- MySQL 및 Redis 가 로컬에서 실행중이어야 함

```properties
# 애플리케이션 기본 정보
spring.application.name=Todo

# MySQL 데이터베이스 설정
spring.datasource.url=jdbc:mysql://localhost:3306/todo_db?characterEncoding=UTF-8&serverTimezone=Asia/Seoul
spring.datasource.username=todo_user
spring.datasource.password=12345678!
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Redis 설정
spring.data.redis.host=127.0.0.1
spring.data.redis.port=6379

# JPA 설정
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.jdbc.time_zone=Asia/Seoul

# JWT 설정
jwt.secret=very-very-long-secure-secret-key-very-long
jwt.access.expiration=300000      # 5분
jwt.refresh.expiration=604800000   # 7일

# 로깅 설정
logging.level.org.hibernate.sql=info
logging.level.org.hibernate.type.descriptor=trace

# MyBatis 설정
mybatis.mapper-locations=classpath:mapper/**/*.xml
mybatis.type-aliases-package=com.todo.todo.entity
```

### 2. 빌드 및 실행

- 터미널에서
  - 루트 디렉토리에서 `./gradlew clean build` 실행
  - build/libs/ 하위에 파일이 생성되면 해당파일 실행
    - `java -jar build/libs/Todo-0.0.1-SNAPSHOT.jar`
 
- IDE 에서 project run

### 3. DB 셋업

- DB 는 MySQL 사용하여 todo_db 생성
```sql
CREATE DATABASE todo_db;
```
- 사용자 계정 생성
```sql
CREATE USER 'todo_user'@'localhost' IDENTIFIED BY '12345678!';
```
- 사용자 권한 부여
```sql
GRANT ALL PRIVILEGES ON todo_db.* TO 'todo_user'@'localhost';
FLUSH PRIVILEGES;
```

- schema.sql 파일은 루트/database 에 schema.sql 파일 또는 프로젝트 run 할때 자동으로 테이블 생성
- 기초 데이터 예시 data.sql 파일은 루트/database 에 data.sql 파일에 작성

### 4. FE 빌드

- frontend 패키지(package.json 이 있는 디렉토리) 에서 npm 설치
```
npm install
```

- 해당 디렉토리 에서 실행
```
npm start
```

---

## 회원가입

https://github.com/user-attachments/assets/fcf08a76-af8f-46ce-a123-5355e849e0f9

## 로그인

https://github.com/user-attachments/assets/788f35d0-6e40-466b-9d02-e6f7e0f7a099

## 할일생성

https://github.com/user-attachments/assets/42e10767-4d59-414f-87d8-a46940cf6d2d

## 할일 필터링

https://github.com/user-attachments/assets/0a9d74a2-ec7b-41ec-98c3-514e0c85b1ea

## 프로젝트 생성

https://github.com/user-attachments/assets/39d5e6c2-5011-42ad-aaf4-076b29270047


## 프로젝트 수정

https://github.com/user-attachments/assets/3efcc176-fd2c-4b7b-aa5a-61106aadb4e9

## 할일 및 프로젝트 검색

https://github.com/user-attachments/assets/eade0109-b76f-49b0-b96f-9f0c370e06ce

## 사이드바

https://github.com/user-attachments/assets/8fc0fbdf-4ecb-4d7d-adca-fc8aef72e652

## 프로젝트 할일 생성

https://github.com/user-attachments/assets/d46ef4ad-4e56-45fd-9fc0-46132fee0dd7

## 프로젝트 할일 수정 및 필터링

https://github.com/user-attachments/assets/ea194bd0-5f40-433d-b539-15f1ee5434fe

## 협업자 목록보기 및 초대

https://github.com/user-attachments/assets/e1a1f0a6-b943-4a11-b64c-1ee15d35be05

## 협업자 초대 승인

https://github.com/user-attachments/assets/d404a054-34fb-457f-bbcf-99b77ef6d30c

## 협업자 Viewer 권한 확인 및 프로젝트 할일 댓글 달기

https://github.com/user-attachments/assets/1136f003-7565-42fc-ae61-e0477763ab6b

## 알림 읽음처리

https://github.com/user-attachments/assets/6276e80d-8259-434f-8a39-503c66ec69c5

## 협업자 권한 수정

https://github.com/user-attachments/assets/ac5d858e-d063-4248-80aa-d5629d4220ef

## 협업자 Editor 권한 확인

https://github.com/user-attachments/assets/3c1eb4e6-8678-41ab-b13f-c02b2a1ef2f9

## 협업자 삭제

https://github.com/user-attachments/assets/3f22dcf1-6e70-410d-9f2d-9c71dffdad60

## 내 정보 조회 및 수정

https://github.com/user-attachments/assets/65162f8f-40f9-483f-bc64-598d5e038361

## 댓글 수정 및 삭제

https://github.com/user-attachments/assets/af38733b-378b-4d47-aaf8-75c87c1b661e

## 

https://github.com/user-attachments/assets/5309a333-ba87-4cda-8849-bb4de08eb1ae

---

## 모바일 화면

<img width="386" alt="스크린샷 2025-02-16 오후 9 35 06" src="https://github.com/user-attachments/assets/26977fb6-1e9e-4746-8615-2c1f6cd14cd9" />
<img width="326" alt="스크린샷 2025-02-16 오후 9 35 36" src="https://github.com/user-attachments/assets/d7e278d5-3ad8-494e-8830-07ec616cb466" />

## 태블릿 화면

<img width="681" alt="스크린샷 2025-02-16 오후 9 35 19" src="https://github.com/user-attachments/assets/c5144b2c-9d74-4969-a7b1-ca478c6f35a4" />

---

