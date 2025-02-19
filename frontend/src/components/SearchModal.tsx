import React, {useEffect, useRef, useState} from "react";
import {useNavigate} from "react-router-dom";
import {Button, Input, InputRef, Modal} from "antd";
import {SearchOutlined} from "@ant-design/icons";

const SearchModal: React.FC<{ visible: boolean; onClose: () => void }> = ({visible, onClose}) => {
  const [keyword, setKeyword] = useState('');
  const navigate = useNavigate();

  const handleSearch = () => {
    if (keyword.trim()) {
      navigate(`/search?q=${encodeURIComponent(keyword)}`);
      onClose();
    }
  };

  const inputRef = useRef<InputRef>(null);

  useEffect(() => {
    if (visible) {
      setTimeout(() => inputRef.current?.focus(), 100);
    }
  }, [visible]);

  return (
      <Modal
          title="검색"
          open={visible}
          onCancel={onClose}
          footer={null}
          centered
      >
        <Input
            ref={inputRef}
            placeholder="찾고자 하는 📝 할일 및 📁 프로젝트명을 입력해 주세요"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onPressEnter={handleSearch}
        />
        <Button
            type="primary"
            icon={<SearchOutlined/>}
            onClick={handleSearch}
            style={{marginTop: 10, width: "100%"}}
        >
          검색
        </Button>
      </Modal>
  );
};

export default SearchModal;