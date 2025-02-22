import React from "react";
import {Form, Input, Button, message} from "antd";
import api from "../services/api";

interface CommentInputProps {
  todoId: number;
  onCommentAdded: () => void;
}

const CommentInput: React.FC<CommentInputProps> = ({
                                                     todoId,
                                                     onCommentAdded
                                                   }) => {
  const [form] = Form.useForm();

  const handleSubmit = async (values: { content: string}) => {

    try {
      await api.post(`/todos/${todoId}/comments`, values);
      message.success("댓글이 등록되었습니다.");
      form.resetFields();
      onCommentAdded();
    } catch (error) {
      console.error("댓글 등록 실패", error);
      message.error("댓글 등록에 실패했습니다.");
    }
  };

  return (
      <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          style={{marginTop: 16}}
      >
        <Form.Item
            name="content"
            rules={[{required: true, message: "댓글 내용을 입력하세요."}]}
        >
          <Input.TextArea
              placeholder="댓글을 입력하세요"
              autoSize={{minRows: 2, maxRows: 4}}
          />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">
            댓글 등록
          </Button>
        </Form.Item>
      </Form>
  );
};

export default CommentInput;