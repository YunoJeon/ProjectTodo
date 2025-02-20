import {Button, Form, Input, message, Modal} from "antd";
import api from "../services/api";

interface ChangePasswordDto {
  password: string;
  confirmPassword: string;
}

interface ChangePasswordModalProps {
  visible: boolean;
  onClose: () => void;
}

const ChangePasswordModal: React.FC<ChangePasswordModalProps> = ({
                                                                   visible,
                                                                   onClose
                                                                 }) => {
  const [form] = Form.useForm();

  const handleFinish = (values: any) => {

    const payload: ChangePasswordDto = {
      password: values.password,
      confirmPassword: values.confirmPassword
    };

    api.put("/users/me/reset-password", payload)
    .then(() => {
      message.success("비밀번호가 변경되었습니다.");
      form.resetFields();
      onClose();
    })
    .catch((error) => {
      console.error("비밀번호 변경 실패", error);
      message.error("비밀번호 변경에 실패했습니다.");
    });
  };

  return (
      <Modal
          open={visible}
          title="비밀번호 변경"
          onCancel={onClose}
          footer={null}
      >
        <Form form={form} layout="vertical" onFinish={handleFinish}>
          <Form.Item
              name="password"
              label="새 비밀번호"
              rules={[{required: true, message: '비밀번호를 입력해주세요.'},
                {min: 8, message: '비밀번호는 최소 8자 이상이어야 합니다.'},
                {pattern: /[!@#$%^&*]/, message: '비밀번호에는 특수문자(! @ # $ % ^ & *)가 포함되어야 합니다.'}
              ]}
          >
            <Input.Password/>
          </Form.Item>
          <Form.Item
              name="newPasswordConfirm"
              label="새 비밀번호 확인"
              dependencies={['password']}
              rules={[{required: true, message: '변경할 비밀번호를 한번 더 입력해 주세요.'},
                ({getFieldValue}) => ({
                  validator(_, value) {
                    if (!value || getFieldValue('password') === value) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error("비밀번호가 일치하지 않습니다."));
                  }
                })
              ]}
          >
            <Input.Password/>
          </Form.Item>
          <Form.Item
              name="confirmPassword"
              label="현재 비밀번호"
              rules={[{required: true, message: '현재 비밀번호를 입력해주세요.'},
                {min: 8, message: '비밀번호는 최소 8자 이상이어야 합니다.'},
                {pattern: /[!@#$%^&*]/, message: '비밀번호에는 특수문자(! @ # $ % ^ & *)가 포함되어야 합니다.'}
              ]}
          >
            <Input.Password/>
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">변경</Button>
          </Form.Item>
        </Form>
      </Modal>
  );
};

export default ChangePasswordModal;