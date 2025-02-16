import {Button, DatePicker, Form, Input, message, Modal, Select} from "antd";
import moment from "moment";
import api from "../services/api";
import React, {useState} from "react";
import PriorityToggle from "./PriorityToggle";

const {Option} = Select;

interface CreateTodoModalProps {
  visible: boolean;
  onClose: () => void;
  onTodoCreated: () => void;
}

interface CreateTodoFormValues {
  projectId?: number;
  title: string;
  description?: string;
  todoCategory: string;
  isPriority: boolean;
  dueDate: moment.Moment;
}

const TodoCreateModal: React.FC<CreateTodoModalProps> = ({visible, onClose, onTodoCreated}) => {
  const [form] = Form.useForm();
  const [isPriority, setPriority] = useState<boolean>(false);
  const onFinish = (values: CreateTodoFormValues) => {
    const payload = {
      ...values,
      dueDate: values.dueDate ? values.dueDate.format("YYYY-MM-DD HH:mm:ss") : null,
      isPriority
    };

    api.post('/todos', payload)
    .then(() => {
      message.success('할일이 생성되었습니다.');
      form.resetFields();
      onTodoCreated();
      onClose();
    })
    .catch((error) => {
      console.error(error);
      message.error('할일 생성에 실패했습니다.');
    });
  };
  return (
      <Modal
          title="새 할일 생성"
          open={visible}
          onCancel={onClose}
          footer={null}
      >
        <Form
            form={form}
            layout="vertical"
            onFinish={onFinish}
            initialValues={{isPriority: false}}
        >
          <Form.Item
              label="카테고리"
              name="todoCategory"
              rules={[{required: true, message: "카테고리를 설정해 주세요."}]}
          >
            <Select placeholder="카테고리 선택">
              <Option value="INDIVIDUAL">😁 개인용 이예요</Option>
              <Option value="WORK">💼 업무용 이예요</Option>
            </Select>
          </Form.Item>
          <Form.Item
              label="제목"
              name="title"
              rules={[{required: true, message: '제목은 필수 입력입니다.'}]}
          >
            <Input placeholder="할일을 입력해주세요"/>
          </Form.Item>
          <Form.Item label="상세 설명" name="description">
            <Input.TextArea placeholder="투두 설명 (선택사항 입니다)"/>
          </Form.Item>
          <Form.Item label="중요도">
            <PriorityToggle
                isPriority={isPriority}
                onChange={(newPriority) => setPriority(newPriority)}
            />
          </Form.Item>
          <Form.Item
              label="마감일"
              name="dueDate"
          >
            <DatePicker style={{width: "100%"}} showTime format="YYYY-MM-DD HH:mm"/>
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">
              생성
            </Button>
          </Form.Item>
        </Form>
      </Modal>
  )
}

export default TodoCreateModal;