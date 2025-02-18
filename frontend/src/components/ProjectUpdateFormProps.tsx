import React from "react";
import {Button, Form, Input} from "antd";
import {useForm} from "antd/es/form/Form";
import {useNavigate} from "react-router-dom";

interface ProjectUpdate {
  id: number;
  name: string;
  description: string;
}

interface ProjectUpdateFormProps {
  initialValues: ProjectUpdate;
  onSubmit: (values: ProjectUpdate) => void;
}

const ProjectUpdateFormProps: React.FC<ProjectUpdateFormProps> = ({initialValues, onSubmit}) => {
  const [form] = useForm();
  const navigate = useNavigate();

  return (
      <Form
          form={form}
          layout="vertical"
          initialValues={initialValues}
          onFinish={(values) => onSubmit({...values, id: initialValues.id})}>
        <Form.Item label="프로젝트 명" name="name" rules={[{required: true, message: "프로젝트명을 입력하세요."}]}>
          <Input/>
        </Form.Item>
        <Form.Item label="설명" name="description">
          <Input.TextArea/>
        </Form.Item>
        <Form.Item>
          <Button type="default" onClick={() => navigate(`/projects/${initialValues.id}/todos`)} htmlType="submit">프로젝트 들어가기</Button>
          </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">수정 완료</Button>
        </Form.Item>
      </Form>
  );
};

export default ProjectUpdateFormProps;