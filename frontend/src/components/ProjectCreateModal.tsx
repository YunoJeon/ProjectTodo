import {Button, Form, Input, message, Modal} from "antd";
import React, {useState} from "react";
import api from "../services/api";

interface ProjectCreateModalProps {
  visible: boolean;
  onClose: () => void;
  onProjectCreated: () => void;
}

const ProjectCreateModal: React.FC<ProjectCreateModalProps> = ({
                                                                 visible,
                                                                 onClose,
                                                                 onProjectCreated
                                                               }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleCreateProject = async (values: { name: string; description?: string }) => {
    setLoading(true);

    try {
      await api.post("/projects", values);
      message.success("프로젝트가 생성되었습니다.");
      form.resetFields();
      onProjectCreated();
      onClose();
    } catch (error) {
      console.error("프로젝트 생성 실패", error);
      message.error("프로젝트 생성에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
      <Modal
          open={visible}
          title="새 프로젝트 생성"
          onCancel={onClose}
          footer={null}
      >
        <Form form={form} layout="vertical" onFinish={handleCreateProject}>
          <Form.Item
              label="프로젝트 명"
              name="name"
              rules={[{ required: true, message: "프로젝트명을 입력해주세요." }]}
          >
              <Input />
          </Form.Item>
          <Form.Item label="설명" name="description">
            <Input.TextArea placeholder="프로젝트에 대한 설명을 입력해주세요."/>
          </Form.Item>
          <Form.Item>
              <Button type="primary" htmlType="submit" loading={loading} block>
                프로젝트 생성
              </Button>
          </Form.Item>
        </Form>
      </Modal>
  );
};

export default ProjectCreateModal;