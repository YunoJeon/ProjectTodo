import {Button, Col, Form, Input, message, Row, Upload} from "antd";
import api from "../services/api";
import React, {useState} from "react";
import {UploadOutlined} from "@ant-design/icons";
import {useNavigate} from "react-router-dom";

interface SignUpFormValues {
  email: string;
  name: string;
  password: string;
  confirmPassword: string;
  phone: string;
}

const SignUpPage: React.FC = () => {
  const [form] = Form.useForm();
  const [emailChecked, setEmailChecked] = useState(false);
  const [profileImage, setProfileImage] = useState<File | null>(null);
  const [fileList, setFileList] = useState<any[]>([]);
  const navigate = useNavigate();

  const beforeUpload = (file: File) => {

    const isValidSize = file.size < 5 * 1024 * 1024;
    if (!isValidSize) {
      message.error("파일 용량은 최대 5MB 를 초과할 수 없습니다.");
    }
    return isValidSize ? true : Upload.LIST_IGNORE;
  };

  const handleUploadChange = (info: any) => {
    const newFileList = info.fileList.slice(-1);
    setFileList(newFileList);

    if (newFileList.length > 0) {
      setProfileImage(newFileList[0].originFileObj);
    } else {
      setProfileImage(null);
    }
  };

  const checkEmailDuplication = async () => {

    try {
      const email = form.getFieldValue('email');
      if (!email) {
        message.error('이메일을 먼저 입력해주세요.');
        return;
      }
      const response = await api.get<boolean>(`/auth/check-email?email=${email}`);

      if (response.data) {
        message.error('이미 사용중인 이메일입니다.');
        setEmailChecked(false);
      } else {
        message.success('사용 가능한 이메일입니다.');
        setEmailChecked(true);
      }
    } catch (error) {
      console.error(error);
      message.error('이메일 중복 확인에 실패했습니다.');
      setEmailChecked(false);
    }
  };

  const onFinish = async (values: SignUpFormValues) => {

    if (!emailChecked) {
      message.error('이메일 중복확인이 되지 않았습니다.');
      return;
    }
    if (values.password !== values.confirmPassword) {
      message.error('비밀번호가 일치하지 않습니다.');
      return;
    }
    let imageUrl = "";
    if (profileImage) {
      try {
        const formData = new FormData();
        formData.append('image', profileImage);
        const imageResponse = await api.post<{ imageUrl: string }>
        ('/images/profile/upload', formData, {
          headers: {'Content-Type': 'multipart/form-data'}
        });
        imageUrl = imageResponse.data.imageUrl;
      } catch (error) {
        console.error(error);
        message.error('프로필 이미지 업로드에 실패했습니다.');
        return;
      }
    }

    api.post('/auth/sign-up', {
      email: values.email,
      password: values.password,
      name: values.name,
      phone: values.phone,
      profileImageUrl: imageUrl
    })
    .then(() => {
      message.success('회원가입에 완료되었습니다. 로그인 페이지로 이동합니다.');
      form.resetFields();
      setEmailChecked(false);
      setProfileImage(null);
      setFileList([]);
      navigate("/");
    })
    .catch((error) => {
      console.error(error);
      message.error('회원가입에 실패했습니다.');
    });
  };

  return (
      <div style={{maxWidth: 400, margin: '0 auto', padding: '2rem'}}>
        <h1>
          1분만에<br/>
          쉽고<br/>
          간편하게
        </h1>
        <Form
            form={form}
            layout="vertical"
            onFinish={onFinish}
            initialValues={{email: '', name: '', password: '', confirmPassword: '', phone: ''}}
        >
          <Form.Item
              label="이메일"
              name="email"
              rules={[
                {required: true, message: '이메일을 입력해주세요.'},
                {type: 'email', message: '올바른 이메일 형식을 입력해주세요.'}
              ]}
          >
            <Row gutter={8}>
              <Col span={16}>
                <Input/>
              </Col>
              <Col span={8}>
                <Button onClick={checkEmailDuplication}>중복 확인</Button>
              </Col>
            </Row>
          </Form.Item>
          <Form.Item
              label="이름"
              name="name"
              rules={[{required: true, message: '이름을 입력해주세요.'}]}
          >
            <Input/>
          </Form.Item>
          <Form.Item
              label="비밀번호"
              name="password"
              rules={[{required: true, message: '비밀번호를 입력해주세요.'},
                {min: 8, message: '비밀번호는 최소 8자 이상이어야 합니다.'},
                {pattern: /[!@#$%^&*]/, message: '비밀번호에는 특수문자(! @ # $ % ^ & *)가 포함되어야 합니다.'}
              ]}
          >
            <Input.Password/>
          </Form.Item>
          <Form.Item
              label="비밀번호 확인"
              name="confirmPassword"
              dependencies={['password']}
              hasFeedback
              rules={[
                {required: true, message: '비밀번호를 한번 더 입력해주세요.'},
                ({getFieldValue}) => ({
                  validator(_, value) {
                    if (!value || getFieldValue('password') === value) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('비밀번호가 일치하지 않습니다.'));
                  },
                }),
              ]}
          >
            <Input.Password/>
          </Form.Item>
          <Form.Item
              label="전화번호"
              name="phone"
              rules={[{required: true, message: '전화번호를 입력해주세요.'}]}
          >
            <Input/>
          </Form.Item>
          <Form.Item label="프로필 이미지">
            <Upload
                beforeUpload={beforeUpload}
                accept="image/*"
                fileList={fileList}
                onChange={handleUploadChange}
                multiple={false}
                customRequest={({onSuccess}) => {
                  setTimeout(() => {
                    onSuccess && onSuccess("ok");
                  }, 0)
                }}
            >
              <Button icon={<UploadOutlined/>}>이미지 선택 (최대 5MB)</Button>
            </Upload>
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">
              회원가입
            </Button>
          </Form.Item>
        </Form>
      </div>
  )
}

export default SignUpPage;