import React, {useEffect, useState} from "react";
import api from "../services/api";
import {Button, List, message, Modal, Select, Space, Spin, Typography} from "antd";
import {Option} from "antd/es/mentions";

interface Collaborator {
  collaboratorId: number;
  name: string;
  roleType: string;
  confirmType: string;
}

interface CollaboratorModalProps {
  projectId: number;
  visible: boolean;
  onClose: () => void;
}

const CollaboratorModal: React.FC<CollaboratorModalProps> = ({projectId, visible, onClose}) => {
  const [collaborators, setCollaborators] = useState<Collaborator[]>([]);
  const [loading, setLoading] = useState(true);
  const [editedRoles, setEditedRoles] = useState<Record<number, string>>({});

  const fetchCollaborators = () => {

    setLoading(true);
    api.get<Collaborator[]>(`/projects/${projectId}/collaborators`)
    .then(response => {
      setCollaborators(response.data);

      const roles: Record<number, string> = {};
      response.data.forEach((item) => {
        roles[item.collaboratorId] = item.roleType;
      });
      setEditedRoles(roles);
    })
    .catch(error => {
      console.error("협업자 목록 조회 실패", error);
      message.error("협업자 목록을 불러오지 못했습니다.");
    })
    .finally(() => {
      setLoading(false);
    });
  };

  useEffect(() => {
    if (visible) {
      fetchCollaborators();
    }
  }, [visible, projectId]);

  const handleRoleChange = (collaboratorId: number, newRole: string) => {
    setEditedRoles((prev) => ({...prev, [collaboratorId]: newRole}));
  };

  const handleSave = (collaboratorId: number) => {
    const newRole = editedRoles[collaboratorId];
    api.put(`/projects/${projectId}/collaborators/${collaboratorId}`, newRole)
    .then((response) => {
      message.success("권한이 수정되었습니다.");
      setCollaborators(response.data);
      const updatedRoles: Record<number, string> = {};
      response.data.forEach((item: Collaborator) => {
        updatedRoles[item.collaboratorId] = item.roleType;
      });
      setEditedRoles(updatedRoles);
    })
    .catch((error) => {
      if (error.response && error.status === 403) {
        console.error("협업자 권한 수정 실패", error);
        message.error("수정 권한이 없습니다.");
      } else {
        message.error("협업자 권한 수정에 실패하였습니다.");
      }
    });
  };

  const handleDelete = (collaboratorId: number) => {
    api.delete(`/projects/${projectId}/collaborators/${collaboratorId}`)
    .then((response) => {
      message.success("협업자가 제외되었습니다.");
      setCollaborators(response.data);
      const updatedRoles: Record<number, string> = {};
      response.data.forEach((item: Collaborator) => {
        updatedRoles[item.collaboratorId] = item.roleType;
      });
      setEditedRoles(updatedRoles);
    })
    .catch((error) => {
      if (error.response && error.status === 403) {
        console.error("협업자 제외 실패", error);
        message.error("수정 권한이 없습니다.");
      } else {
        message.error("협업자 제외에 실패하였습니다.");
      }
    });
  };

  return (
      <Modal
          open={visible}
          title="협업자 목록"
          onCancel={onClose}
          footer={null}
      >
        {loading ? (
            <Spin style={{display: "block", textAlign: "center", marginTop: "2rem"}}/>
        ) : collaborators.length === 0 ? (
            <Typography.Text>협업자가 없습니다.</Typography.Text>
        ) : (
            <List
                dataSource={collaborators}
                renderItem={(item) => (
                    <List.Item key={item.collaboratorId}>
                      <Space style={{width: "100%", justifyContent: "space-between"}}>
                        <div>
                          <Typography.Text strong>{item.name}</Typography.Text>
                          <Typography.Text style={{marginLeft: 8}} type="secondary">
                            {item.confirmType === "TRUE" ? "참여자" : "초대됨"}
                          </Typography.Text>
                        </div>
                        <div>
                          <Select
                              value={editedRoles[item.collaboratorId]}
                              style={{width: 120}}
                              onChange={(value) => handleRoleChange(item.collaboratorId, value)}
                          >
                            <Option value="EDITOR">✏️ EDITOR</Option>
                            <Option value="VIEWER">👀 VIEWER</Option>
                          </Select>
                          <Button
                              type="primary"
                              onClick={() => handleSave(item.collaboratorId)}
                              style={{marginLeft: 8}}
                          >
                            수정
                          </Button>
                          <Button
                              type="default"
                              danger
                              onClick={() => handleDelete(item.collaboratorId)}
                              style={{marginLeft: 8}}
                          >
                            제외
                          </Button>
                        </div>
                      </Space>
                    </List.Item>
                )}
            />
        )}
      </Modal>
  );
};

export default CollaboratorModal;