import React, {useEffect, useRef, useState} from "react";
import api from "../services/api";
import {Avatar, Button, Card, message, Spin, Typography} from "antd";
import moment from "moment";
import ChangePasswordModal from "../components/ChangePasswordModal";

interface UserResponseDto {
  id: number;
  email: string;
  phone: string;
  name: string;
  profileImageUrl: string;
  createdAt: string;
}

const MyInfoPage: React.FC = () => {
  const [userInfo, setUserInfo] = useState<UserResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [changePasswordVisible, setChangePasswordVisible] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    api.get('/users/me')
    .then(response => {
      setUserInfo(response.data);
    })
    .catch(error => {
      console.error("내 정보 조회 실패", error);
    })
    .finally(() => setLoading(false));
  }, []);

  const handleImageChangeClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (files && files.length > 0) {
      const file = files[0];
      const formData = new FormData();
      formData.append('image', file);

      api.post<{ profileImageUrl: string }>("/users/me/profile", formData, {
        headers: {"Content-Type": "multipart/form-data"}
      })
      .then(response => {
        message.success("프로필 이미지가 변경되었습니다.");

        if (userInfo) {
          setUserInfo({...userInfo, profileImageUrl: response.data.profileImageUrl});
        }
      })
      .catch(error => {
        console.error("프로필 이미지 변경 실패", error);
        message.error("프로필 이미지 변경에 실패하였습니다.");
      });
    }
  };

  if (loading) {
    return <Spin style={{display: "block", textAlign: "center", marginTop: "2rem"}}/>;
  }

  return (
      <Card title="내 정보" style={{maxWidth: 500, margin: "2rem auto"}}>
        {userInfo ? (
            <div style={{textAlign: "center"}}>
              <div onClick={handleImageChangeClick}
                   style={{display: "inline-block", cursor: "pointer"}}>
                <Avatar src={`http://localhost:8080${userInfo.profileImageUrl}`} size={200}/>
              </div>
              <input
                  type="file"
                  ref={fileInputRef}
                  style={{display: "none"}}
                  accept="image/*"
                  onChange={handleFileChange}
              />
              <Typography.Title level={2} style={{marginTop: 16}}>{userInfo.name}</Typography.Title>
              <div style={{fontWeight: "bold"}}>
                <Typography.Paragraph style={{fontSize: "16px"}}>📧
                  이메일: {userInfo.email}</Typography.Paragraph>
                <Typography.Paragraph style={{fontSize: "16px"}}>📱
                  전화번호: {userInfo.phone}</Typography.Paragraph>
                <Typography.Paragraph style={{fontSize: "16px"}}>📆
                  가입일: {moment(userInfo.createdAt).format("YY년 M월 D일 H시 m분")}</Typography.Paragraph>
              </div>
              <Button
                  type="primary"
                  style={{marginTop: "1rem"}}
                  onClick={() => setChangePasswordVisible(true)}
              >
                비밀번호 변경
              </Button>
            </div>
        ) : (
            <Typography.Text>정보를 불러올 수 없습니다.</Typography.Text>
        )}

        <ChangePasswordModal
            visible={changePasswordVisible}
            onClose={() => setChangePasswordVisible(false)}
        />
      </Card>
  );
};

export default MyInfoPage;