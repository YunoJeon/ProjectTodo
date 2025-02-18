import {Button, message, Space, Spin, Typography} from "antd";
import React, {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import api from "../services/api";
import TodoListVirtualized from "../components/TodoListVirtualized";
import TodoCreateModal from "../components/TodoCreateModal";
import TodoDetailModal from "../components/TodoDetailModal";

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
  const [todos, setTodos] = useState<Todo[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalVisible, setModalVisible] = useState(false);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [showAllCompleted, setShowAllCompleted] = useState<boolean>(false);
  const [showImportantOnly, setShowImportantOnly] = useState<boolean>(false);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedTodoId, setSelectedTodoId] = useState<string | null>(null);

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
        <Space style={{marginBottom: "1rem"}}>
          <Button type={showAllCompleted ? "primary" : "default"}
                  onClick={() => setShowAllCompleted((prev) => !prev)}>
            ✅
          </Button>
          <Button type={showImportantOnly ? "primary" : "default"}
                  onClick={() => setShowImportantOnly((prev) => !prev)}>
            ⭐️
          </Button>
        </Space>
        <Button type="primary" onClick={() => setModalVisible(true)} style={{marginBottom: "1rem"}}>
          새 할일 생성
        </Button>
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
      </div>
  );
};

export default ProjectTodosPage;