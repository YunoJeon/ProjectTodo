import React, {useEffect, useState} from "react";
import api from "../services/api";
import {message, Modal, Spin, Typography} from "antd";
import moment from "moment";
import TodoUpdateForm from "./TodoUpdateFormProps";
import {useForm} from "antd/es/form/Form";

interface TodoDetail {
  id: number;
  authorId: number;
  projectId: number;
  title: string;
  description: string;
  todoCategory: string;
  isCompleted: boolean;
  isPriority: boolean;
  version: number;
  dueDate: string;
  createdAt: string;
}

interface TodoDetailModalProps {
  todoId: string | null;
  visible: boolean;
  onClose: () => void;
  onTodoUpdated: () => void;
}

const TodoDetailModal: React.FC<TodoDetailModalProps> = ({todoId, visible, onClose, onTodoUpdated}) => {
  const [todo, setTodo] = useState<TodoDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [form] = useForm();

  useEffect(() => {
    if (todoId) {
      setLoading(true);
      api.get<TodoDetail>(`/todos/${todoId}`)
      .then(response => {
        setTodo(response.data);
        form.setFieldsValue({
          ...response.data,
          projectId: response.data.projectId
        })
      })
      .catch(error => {
        console.error('할일 상세 정보 조회 실패', error);
      })
      .finally(() => {
        setLoading(false);
      });
    }
  }, [todoId]);

  const handleSubmit = (values: any) => {
    const payload = {
      ...values,
      projectId: todo?.projectId ?? values.projectId,
      dueDate: values.dueDate ? values.dueDate.format("YYYY-MM-DD HH:mm:ss") : null
    };
    api.put(`/todos/${todoId}`, payload)
    .then(() => {
      onClose();
      onTodoUpdated();
    })
    .catch((error) => {
      console.error("할일 업데이트 실패", error);
      if (error.response && error.status === 403) {
        message.error("수정 권한이 없습니다.");
      } else {
        message.error("할일 업데이트 중 오류가 발생했습니다.");
      }
    });
  };

  return (
      <Modal
          open={visible}
          title={todo ? todo.title : "할일 상세"}
          onCancel={onClose}
          footer={null}
      >
        {loading ? (
            <Spin style={{display: "block", textAlign: "center", marginTop: "2rem"}}/>
        ) : todo ? (
            <TodoUpdateForm
                initialValues={{
                  projectId: todo.projectId,
                  title: todo.title,
                  description: todo.description,
                  todoCategory: todo.todoCategory,
                  isPriority: todo.isPriority,
                  isCompleted: todo.isCompleted,
                  dueDate: todo.dueDate ? moment(todo.dueDate) : moment()
                }}
                onSubmit={handleSubmit}
            />
        ) : (
            <Typography.Text>할일을 찾을 수 없습니다.</Typography.Text>
        )}
      </Modal>
  );
};

export default TodoDetailModal;