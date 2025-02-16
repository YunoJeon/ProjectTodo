import {Button, Form, Input, message} from "antd";
import React from "react";
import {useNavigate} from "react-router-dom";
import api from "../services/api";
import {useAuth} from "../context/AuthContext";

interface LoginFormValues {
  email: string;
  password: string;
}

const LoginPage: React.FC = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const {setIsLoggedIn} = useAuth();

  const onFinish = (values: LoginFormValues) => {
    api.post('/auth/sign-in', values)
        .then(response => {
      const tokens = response.data;
      localStorage.setItem('accessToken', tokens.accessToken);
      message.success('로그인에 성공했습니다.');
      setIsLoggedIn(true);
      navigate('/dashboard')
    })
    .catch(error => {
      console.error(error);
      message.error('로그인에 실패했습니다.');
    });
  };

  return (
      <div style={{maxWidth: 400, margin: '0 auto', padding: '2rem'}}>
        <h1>
          To - Do 서비스는<br/>
          로그인 후 이용<br/>
          가능합니다.
        </h1>
        <Form
            form={form}
            layout="vertical"
            onFinish={onFinish}
            initialValues={{email: '', password: ''}}
        >
          <Form.Item
              label="이메일"
              name="email"
              rules={[
                {required: true, message: '이메일을 입력해주세요.'},
                {type: "email", message: '올바른 이메일 형식을 입력해주세요.'}
              ]}
          >
            <Input/>
          </Form.Item>
          <Form.Item
              label="비밀번호"
              name="password"
              rules={[{required: true, message: '비밀번호를 입력해 주세요.'}]}
          >
            <Input.Password/>
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">
              로그인
            </Button>
          </Form.Item>
        </Form>
        <div style={{textAlign: 'center', marginTop: '1rem'}}>
          <Button type="link" onClick={() => navigate('/sign-up')}>
            쉽고 빠른 회원가입
          </Button>
        </div>
      </div>
  )
};

export default LoginPage;