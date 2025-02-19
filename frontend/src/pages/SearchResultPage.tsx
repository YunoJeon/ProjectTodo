import React, {useCallback, useEffect, useRef, useState} from "react";
import {useNavigate, useSearchParams} from "react-router-dom";
import api from "../services/api";
import {List, Spin, Typography} from "antd";
import TodoDetailModal from "../components/TodoDetailModal";

interface SearchResult {
  id: number;
  name: string;
  type: "TODO" | "PROJECT"
  isCompleted: boolean;
}

const SearchResultPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const keyword = searchParams.get("q") || "";
  const [results, setResults] = useState<SearchResult[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const observer = useRef<IntersectionObserver | null>(null);
  const [selectedTodoId, setSelectedTodoId] = useState<string | null>(null);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const navigate = useNavigate();

  const fetchSearchResults = async (reset = false) => {
    if (!keyword) return;

    setLoading(true);
    try {
      const response = await api.get<{
        list: SearchResult[]
        hasNextPage: boolean;
      }>("/search", {
        params: {q: keyword, page, pageSize: 10}
      });

      const newResults = response.data.list;

      setResults((prevResults) =>
          reset ? newResults : [...prevResults, ...newResults]
      );
      setHasMore(response.data.hasNextPage);
    } catch (error) {
      console.error("검색 실패", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSearchResults(true);
  }, [keyword]);

  useEffect(() => {
    if (page > 1) fetchSearchResults();
  }, [page]);

  const lastElementRef = useCallback(
      (node: HTMLDivElement | null) => {
        if (loading || !hasMore) return;
        if (observer.current) observer.current.disconnect();

        observer.current = new IntersectionObserver((entries) => {
          if (entries[0].isIntersecting) {
            setPage((prevPage) => prevPage + 1);
          }
        });

        if (node) observer.current.observe(node);
      }, [loading, hasMore]
  );

  return (
      <div style={{padding: "2rem"}}>
        <Typography.Title level={2}>
          "{keyword}" 검색결과
        </Typography.Title>
        {loading && page === 1 ? (
            <Spin style={{display: "block", textAlign: "center", marginBottom: "2rem"}}/>
        ) : results.length > 0 ? (
            <List
                bordered
                dataSource={results}
                renderItem={(item, index) => (
                    <List.Item
                        ref={index === results.length - 1 ? lastElementRef : null}
                        onClick={() => {
                          if (item.type === "TODO") {
                            setSelectedTodoId(item.id.toString());
                            setDetailModalVisible(true);
                          } else {
                            navigate(`/projects/${item.id}/todos`)
                          }
                        }}
                        style={{cursor: "pointer"}}
                    >
                      <Typography.Title level={5} style={{
                        fontWeight: "bold",
                        color: "#000",
                        margin: 0,
                        display: "flex",
                        alignItems: "center"
                      }}>
                        <span style={{
                          marginRight: "8px",
                          fontSize: "18px",
                          fontWeight: "bold",
                          color: item.type === "TODO" ? "#2e7d32" : "#1565c0",
                          background: item.type === "TODO" ? "#c8e6c9" : "#bbdefb",
                          padding: "4px 8px",
                          borderRadius: "20px",
                          display: "inline-block"
                        }}>
                        {item.type === "TODO" ? "📝 Todo" : "📁 Project"}
                        </span>
                        {item.name}
                      </Typography.Title>
                    </List.Item>
                )}
            />
        ) : (
            <Typography.Title level={4} style={{fontWeight: "bold", color: "#000"}}>검색 결과가
              없습니다.</Typography.Title>
        )}
        {loading && <Spin style={{display: "block", textAlign: "center", marginTop: "1rem"}}/>}

        <TodoDetailModal
            todoId={selectedTodoId}
            visible={detailModalVisible}
            onClose={() => setDetailModalVisible(false)}
            onTodoUpdated={() => fetchSearchResults(true)}
        />
      </div>
  );
};

export default SearchResultPage;