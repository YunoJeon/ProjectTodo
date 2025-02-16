import {useForm} from "antd/es/form/Form";
import React, {useState} from "react";
import moment from "moment";
import {Button, DatePicker, Form, Input, Select} from "antd";
import PriorityToggle from "./PriorityToggle";
import {Option} from "antd/es/mentions";
import CompletedToggle from "./CompletedToggle";

interface TodoUpdate {
  projectId: number;
  title: string;
  description: string;
  todoCategory: string;
  isPriority: boolean;
  isCompleted: boolean;
  dueDate: moment.Moment;
}

interface TodoUpdateFormProps {
  initialValues: TodoUpdate;
  onSubmit: (values: TodoUpdate) => void;
}

const TodoUpdateForm: React.FC<TodoUpdateFormProps> = ({initialValues, onSubmit}) => {
  const [form] = useForm();
  const [isPriority, setPriority] = useState(initialValues.isPriority);
  const [isCompleted, setCompleted] = useState(initialValues.isCompleted);

  const handleFinish = (values: any) => {
    onSubmit({...values, isPriority, isCompleted, dueDate: values.dueDate});
  };

  return (
      <Form
          form={form}
          layout="vertical"
          initialValues={initialValues}
          onFinish={handleFinish}
      >
        <Form.Item
            label="카테고리"
            name="todoCategory"
            rules={[{required: true, message: "카데고리를 설정해 주세요."}]}>
            <Select placeholder="카테고리 선택">
              <Option value="INDIVIDUAL">😁 개인용 이예요</Option>
              <Option value="WORK">💼 업무용 이예요</Option>
            </Select>
          </Form.Item>
        <Form.Item
            label="제목"
            name="title"
            rules={[{required: true, message: "제목은 필수 입력입니다."}]}
        >
          <Input/>
        </Form.Item>
        <Form.Item
            label="설명"
            name="description"
        >
          <Input.TextArea/>
        </Form.Item>
        <Form.Item label="⭐️중요">
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
        <Form.Item label="✅ 완료처리">
          <CompletedToggle
              isCompleted={isCompleted}
              onChange={(newCompleted) => setCompleted(newCompleted)}
          />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">
            수정완료
          </Button>
        </Form.Item>
      </Form>
  );
};

export default TodoUpdateForm;