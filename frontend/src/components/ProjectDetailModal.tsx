import React, {useEffect, useState} from "react";
import api from "../services/api";
import {message, Modal, Spin, Typography} from "antd";
import ProjectUpdateFormProps from "./ProjectUpdateFormProps";

interface ProjectDetail {
  id: number;
  name: string;
  description: string;
}

interface ProjectDetailModalProps {
  projectId: string | null;
  visible: boolean;
  onClose: () => void;
  onProjectUpdated: () => void;
}

const ProjectDetailModal: React.FC<ProjectDetailModalProps> = ({
                                                           projectId,
                                                           visible,
                                                           onClose,
                                                           onProjectUpdated
                                                         }) => {
  const [project, setProject] = useState<ProjectDetail | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (projectId) {
      setLoading(true);
      api.get<ProjectDetail>(`/projects/${projectId}`)
      .then(response => setProject(response.data))
      .catch(error => {
        console.error("프로젝트 상세 조회 실패", error);
        message.error("프로젝트 정보를 불러오지 못했습니다.");
      })
      .finally(() => setLoading(false));
    }
  }, [projectId]);

  const handleSubmit = (values: any) => {
    api.put(`/projects/${projectId}`, values)
    .then(() => {
      message.success("프로젝트가 성공적으로 수정되었습니다.")
      onClose();
      onProjectUpdated();
    })
    .catch(error => {
      console.error("프로젝트 수정 실패", error);
      message.error("프로젝트 수정에 실패했습니다.");
    });
  };

  return (
      <Modal open={visible} title={project ? project.name : "프로젝트 상세"} onCancel={onClose}
             footer={null}>
        {loading ? (
            <Spin style={{display: "block", textAlign: "center", margin: "2rem"}}/>
        ) : project ? (
            <ProjectUpdateFormProps initialValues={project} onSubmit={handleSubmit}/>
        ) : (
            <Typography.Text>프로젝트를 찾을 수 없습니다.</Typography.Text>
        )}
      </Modal>
  );
};

export default ProjectDetailModal;