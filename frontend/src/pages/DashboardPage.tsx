import React, {useEffect, useState} from "react";
import api from "../services/api";
import {Button, Select, Space, Spin, Typography} from "antd";
import TodoCreateModal from "../components/TodoCreateModal";
import TodoDetailModal from "../components/TodoDetailModal";
import TodoListVirtualized from "../components/TodoListVirtualized";
import ProjectListVirtualized from "../components/ProjectListVirtualized";
import ProjectCreateModal from "../components/ProjectCreateModal";
import ProjectDetailModal from "../components/ProjectDetailModal";

const {Option} = Select;

interface Todo {
  id: number;
  authorId: number;
  projectId: number;
  title: string;
  todoCategory: string;
  isCompleted: boolean;
  isPriority: boolean;
  dueDate: string;
}

interface Project {
  id: number;
  name: string;
}

interface TodoResponse {
  total: number;
  list: Todo[];
  pageNum: number;
  pageSize: number;
}

interface ProjectResponse {
  number: number;
  size: number;
  numberOfElements: number;
  content: Project[];
}

const DashboardPage: React.FC = () => {
      const [todos, setTodos] = useState<Todo[]>([]);
      const [projects, setProjects] = useState<Project[]>([]);
      const [loading, setLoading] = useState(true);
      const [modalVisible, setModalVisible] = useState(false);
      const [projectModalVisible, setProjectModalVisible] = useState(false);
      const [detailModalVisible, setDetailModalVisible] = useState(false);
      const [selectedTodoId, setSelectedTodoId] = useState<string | null>(null);
      const [selectedProjectId, setSelectedProjectId] = useState<string | null>(null);
      const [projectDetailModalVisible, setProjectDetailModalVisible] = useState(false);
      const [page, setPage] = useState(1);
      const [projectPage, setProjectPage] = useState(1);
      const [hasMore, setHasMore] = useState(true);
      const [hasMoreProjects, setHasMoreProjects] = useState(true);
      const [showAllCompleted, setShowAllCompleted] = useState<boolean>(false);
      const [filterCategory, setFilterCategory] = useState<string>("전체");
      const [showImportantOnly, setShowImportantOnly] = useState<boolean>(false);

      const fetchTodos = (currentPage: number) => {
        const params: any = {
          page: currentPage,
          pageSize: 10
        };
        if (!showAllCompleted) {
          params.completed = false;
        }

        if (filterCategory !== "전체") {
          params.category = filterCategory;
        }

        if (showImportantOnly) {
          params.priority = true;
        }

        return api.get<TodoResponse>("/todos", {params});
      };

      const fetchProjects = (currentPage: number) => {
        return api.get<ProjectResponse>("/projects", {params: {page: currentPage, pageSize: 10}});
      };

      const fetchData = async (reset: boolean = false) => {
        setLoading(true);

        try {
          if (reset) {
            setPage(1);
            setProjectPage(1);
            setHasMore(true);
            setHasMoreProjects(true);

            const [todoResponse, projectResponse] = await Promise.all([
              fetchTodos(1),
              fetchProjects(1)
            ]);

            setTodos(todoResponse.data.list);
            setProjects(projectResponse.data.content);
            setHasMore(todoResponse.data.list.length >= 10);
            setHasMoreProjects(projectResponse.data.numberOfElements > 0);
            return;
          }

          const [todoResponse] = await Promise.all([fetchTodos(page)]);
          const newTodos = todoResponse.data.list;

          setHasMore(newTodos.length >= 10);
          setTodos((prevTodos) => {
            if (page === 1) return newTodos;
            const existingIds = new Set(prevTodos.map((todo) => todo.id));
            return [...prevTodos, ...newTodos.filter(todo => !existingIds.has(todo.id))];
          });

        } catch (error) {
          console.error("API 호출 에러", error);
        } finally {
          setLoading(false);
        }
      };

      useEffect(() => {
        fetchData(true);
      }, [showAllCompleted, filterCategory, showImportantOnly]);

      useEffect(() => {
        if (page > 1 || projectPage > 1) fetchData();
      }, [page, projectPage]);

      const loadMoreData = async () => {
        setPage((prevPage) => prevPage + 1);
      };

      const loadMoreProjects = async () => {
        if (!hasMoreProjects) return;

        try {
          const nextPage = projectPage + 1;
          const response = await fetchProjects(nextPage);
          const newProjects = response.data.content;

          if (newProjects.length === 0) {
            setHasMoreProjects(false);
            return;
          }
          setProjects((prevProjects) => {
            const existingIds = new Set(prevProjects.map((project) => project.id));
            return [...prevProjects, ...newProjects.filter((project) => !existingIds.has(project.id))];
          });
          setProjectPage(nextPage);
        } catch (error) {
          console.error("프로젝트 로딩 실패", error);
        }
      };

      if (loading && page === 1) {
        return <Spin style={{display: "block", textAlign: "center", marginTop: "2rem"}}/>;
      }

      return (
          <div style={{padding: "2rem"}}>
            <Typography.Title level={1}>📝 내 할일</Typography.Title>
            <div style={{marginBottom: "0.5rem"}}>
              <Space style={{marginBottom: "0.5rem"}}>
                <Button
                    type={showAllCompleted ? "primary" : "default"}
                    onClick={() => {
                      setShowAllCompleted((prev) => !prev);
                    }}
                >
                  ✅
                </Button>
                <Button
                    type={showImportantOnly ? "primary" : "default"}
                    onClick={() => {
                      setShowImportantOnly((prev) => !prev);
                    }}
                >
                  ⭐️
                </Button>
                <Select
                    defaultValue="전체"
                    style={{width: 120}}
                    value={filterCategory}
                    onChange={(value) => {
                      setFilterCategory(value);
                    }}
                >
                  <Option value="전체">𝘼 전체</Option>
                  <Option value="INDIVIDUAL">😁 개인</Option>
                  <Option value="WORK">💼 업무</Option>
                </Select>
              </Space>
            </div>
            <div>
              <Button
                  type="primary"
                  onClick={() => setModalVisible(true)}
              >
                새 할일 생성
              </Button>
            </div>

            <TodoListVirtualized
                todos={todos}
                onTodoClick={(id) => {
                  setSelectedTodoId(id.toString());
                  setDetailModalVisible(true)
                }}
                loadMore={loadMoreData}
                hasMore={hasMore}
            />
            <Typography.Title level={1}>📁 내 프로젝트</Typography.Title>
            <Button
                type="primary"
                onClick={() => setProjectModalVisible(true)}
                style={{marginBottom: "1rem"}}
            >
              새 프로젝트 생성
            </Button>

            <ProjectListVirtualized
                projects={projects}
                onProjectClick={(id) => {
                  setSelectedProjectId(id.toString());
                  setProjectDetailModalVisible(true);
                }}
                loadMore={loadMoreProjects}
                hasMore={hasMoreProjects}
            />

            <TodoCreateModal
                visible={modalVisible}
                onClose={() => setModalVisible(false)}
                onTodoCreated={() => {
                  fetchData(true);
                }}
            />
            <TodoDetailModal todoId={selectedTodoId}
                             visible={detailModalVisible}
                             onClose={() => setDetailModalVisible(false)}
                             onTodoUpdated={() => {
                               fetchData(true);
                             }}
            />

            <ProjectCreateModal
                visible={projectModalVisible}
                onClose={() => setProjectModalVisible(false)}
                onProjectCreated={() => {
                  fetchData(true);
                }}
            />

            <ProjectDetailModal
                projectId={selectedProjectId}
                visible={projectDetailModalVisible}
                onClose={() => setProjectDetailModalVisible(false)}
                onProjectUpdated={() => {
                  fetchData(true);
                }}
            />
          </div>
      );
    }
;

export default DashboardPage;