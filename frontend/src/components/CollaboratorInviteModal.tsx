import {Button, Input, message, Modal, Select} from "antd";
import React, {useState} from "react";
import api from "../services/api";

const {Option} = Select;

interface CollaboratorInviteModalProps {
  projectId: number;
  visible: boolean;
  onClose: () => void;
  onCollaboratorAdded: () => void;
}

interface User {
  id: number
  name: string
  email: string
}

const CollaboratorInviteModal: React.FC<CollaboratorInviteModalProps> = ({
                                                                           projectId,
                                                                           visible,
                                                                           onClose,
                                                                           onCollaboratorAdded
                                                                         }) => {
  const [email, setEmail] = useState('');
  const [roleType, setRoleType] = useState<"VIEWER" | "EDITOR">("VIEWER");
  const [loading, setLoading] = useState(false);
  const [user, setUser] = useState<User | null>(null);

  const searchUserByEmail = async () => {
    if (!email.trim()) {
      message.warning("이메일을 입력해주세요.");
      return;
    }
    setLoading(true);
    setUser(null);

    try {
      const response = await api.get(`/users/info/${email}`);
      setUser(response.data);
    } catch (error) {
      console.error("사용자를 찾을 수 없습니다.", error);
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const inviteCollaborator = async () => {
    if (!user) {
      message.error("사용자를 먼저 검색해주세요.");
      return;
    }
    setLoading(true);

    try {
      await api.post(`/projects/${projectId}/collaborators`, {
        collaboratorId: user.id,
        roleType
      });
      message.success(`${user.name}님을 ${roleType}로 초대했습니다.`);
      onCollaboratorAdded();
      onClose();
    } catch (error) {
      message.error("초대에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
      <Modal open={visible} title="협업자 초대" onCancel={onClose} footer={null} centered>
        <Input
            placeholder="이메일 입력"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            onPressEnter={searchUserByEmail}/>
        <Button
            type="primary"
            onClick={searchUserByEmail}
            loading={loading}
            style={{marginTop: 10}}>
          사용자 검색
        </Button>

        {user && (
            <div
                style={{marginTop: 15, padding: "10px", border: "1px solid #ddd", borderRadius: 5}}>
              <p><strong>이름:</strong> {user.name}</p>
              <p><strong>이메일:</strong> {user.email}</p>
              <Select value={roleType} onChange={setRoleType} style={{width: "100%"}}>
                <Option value="VIEWER">👀 Viewer</Option>
                <Option value="EDITOR">✏️ Editor</Option>
              </Select>
              <Button type="primary" block onClick={inviteCollaborator} loading={loading}
                      style={{marginTop: 10}}>초대하기</Button>
            </div>
        )}
      </Modal>
  );
};

export default CollaboratorInviteModal;