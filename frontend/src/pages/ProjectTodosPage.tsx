import {Button, message, Space, Spin, Typography} from "antd";
import React, {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import api from "../services/api";
import TodoListVirtualized from "../components/TodoListVirtualized";
import TodoCreateModal from "../components/TodoCreateModal";
import TodoDetailModal from "../components/TodoDetailModal";
import CollaboratorInviteModal from "../components/CollaboratorInviteModal";
import CollaboratorsModal from "../components/CollaboratorsModal";

interface Todo {
  id: number;
  title: string;
  isCompleted: boolean;
  isPriority: boolean;
  todoCategory: string;
  dueDate: string;
}

interface TodoResponse {
  total: number;
  list: Todo[];
  pageNum: number;
  pageSize: number;
}

const ProjectTodosPage: React.FC = () => {
  const {projectId} = useParams<{ projectId: string }>();
  const numericProjectId = Number(projectId);
  const [todos, setTodos] = useState<Todo[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalVisible, setModalVisible] = useState(false);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [showAllCompleted, setShowAllCompleted] = useState<boolean>(false);
  const [showImportantOnly, setShowImportantOnly] = useState<boolean>(false);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedTodoId, setSelectedTodoId] = useState<string | null>(null);
  const [inviteModalVisible, setInviteModalVisible] = useState(false);
  const [collaboratorsModalVisible, setCollaboratorsModalVisible] = useState(false);

  const fetchTodos = async (currentPage: number) => {
    const params: any = {
      projectId,
      page: currentPage,
      pageSize: 10,
    };
    if (!showAllCompleted) params.completed = false;
    if (showImportantOnly) params.priority = true;

    try {
      const response = await api.get<TodoResponse>("/todos", {params});
      return response.data;
    } catch (error) {
      console.error("투두 목록 조회 실패", error);
      message.error("투두 목록을 불러오지 못했습니다.");
      return null;
    }
  };

  const fetchData = async (reset: boolean = false) => {
    setLoading(true);

    try {
      if (reset) {
        setPage(1);
        setHasMore(true);
        const todoResponse = await fetchTodos(1);
        if (todoResponse) {
          setTodos(todoResponse.list.map(todo => ({
            ...todo, todoCategory: "WORK"
          })));
          setHasMore(todoResponse.list.length >= 10);
        }
      } else {
        const todoResponse = await fetchTodos(page);
        if (todoResponse) {
          setHasMore(todoResponse.list.length >= 10);
          setTodos((prevTodos) => {
            if (page === 1) return todoResponse.list.map(todo => ({
              ...todo, todoCategory: "WORK"
            }));
            const existingIds = new Set(prevTodos.map((todo) => todo.id));
            return [...prevTodos, ...todoResponse.list.filter(todo => !existingIds.has(todo.id)).map(todo => ({
              ...todo, todoCategory: "WORK"
            }))];
          });
        }
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(true);
  }, [showAllCompleted, showImportantOnly]);

  useEffect(() => {
    if (page > 1) fetchData();
  }, [page]);

  const loadMoreData = async () => setPage((prevPage) => prevPage + 1);

  if (loading && page === 1) {
    return <Spin style={{display: "block", textAlign: "center", marginTop: "2rem"}}/>;
  }

  return (
      <div style={{padding: "2rem"}}>
        <Typography.Title level={1}>🧑‍💻 협업 & 공동작업 하기</Typography.Title>
        <div style={{marginBottom: "1rem"}}>
          <div style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            width: "100%"
          }}
          >
            <Space>
              <Button
                  type={showAllCompleted ? "primary" : "default"}
                  onClick={() => setShowAllCompleted((prev) => !prev)}>
                ✅
              </Button>
              <Button
                  type={showImportantOnly ? "primary" : "default"}
                  onClick={() => setShowImportantOnly((prev) => !prev)}>
                ⭐️
              </Button>
            </Space>
            <Button
                type="primary"
                onClick={() => setInviteModalVisible(true)}
            >
              협업자 초대
            </Button>
          </div>
          <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                width: "100%",
                marginTop: "0.5rem"
              }}
          >
            <Button
                type="primary"
                onClick={() => setModalVisible(true)}
            >
              새 할일 생성
            </Button>
            <Button
                type="primary"
                onClick={() => setCollaboratorsModalVisible(true)}
            >
              협업자 목록 보기
            </Button>
          </div>
        </div>

        <TodoListVirtualized
            todos={todos}
            loadMore={loadMoreData}
            hasMore={hasMore}
            onTodoClick={(id) => {
              setSelectedTodoId(id.toString());
              setDetailModalVisible(true);
            }}
        />
        <TodoCreateModal
            projectId={projectId}
            visible={modalVisible}
            onClose={() => setModalVisible(false)}
            onTodoCreated={() => fetchData(true)}
            isProjectTodo={true}
        />
        <TodoDetailModal
            todoId={selectedTodoId} visible={detailModalVisible}
            onClose={() => setDetailModalVisible(false)}
            onTodoUpdated={() => fetchData(true)}
        />

        <CollaboratorInviteModal
            projectId={numericProjectId}
            visible={inviteModalVisible}
            onClose={() => setInviteModalVisible(false)}
            onCollaboratorAdded={() => message.success("협업자가 추가되었습니다.")}
        />

        <CollaboratorsModal
            projectId={numericProjectId}
            visible={collaboratorsModalVisible}
            onClose={() => setCollaboratorsModalVisible(false)}
        />
      </div>
  )
      ;
};

export default ProjectTodosPage;