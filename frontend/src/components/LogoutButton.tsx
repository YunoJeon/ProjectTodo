import {useNavigate} from "react-router-dom";
import api from "../services/api";
import {Button, message} from "antd";
import {useAuth} from "../context/AuthContext";
import React from "react";

const LogoutButton: React.FC = () => {
  const navigate = useNavigate();
  const {setIsLoggedIn} = useAuth();

  const handleLogout = async () => {
    try {
      await api.post('/auth/sign-out');
      localStorage.removeItem('accessToken');
      message.success('로그아웃 되었습니다.');
      setIsLoggedIn(false);
      navigate('/');
    } catch (error) {
      console.error(error);
      message.error('로그아웃에 실패했습니다.');
    }
  };

  return (
      <Button type="text" danger onClick={handleLogout}>
        로그아웃
      </Button>
  );
};

export default LogoutButton;