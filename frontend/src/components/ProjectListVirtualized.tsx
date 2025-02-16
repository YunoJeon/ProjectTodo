import React from "react";
import {Card, Typography} from "antd";
import {FixedSizeList as List, ListChildComponentProps} from "react-window";
import InfiniteLoader from "react-window-infinite-loader";

interface Project {
  id: number;
  name: string;
}

interface ProjectListVirtualizedProps {
  projects: Project[];
  onProjectClick: (id: number) => void;
  loadMore: () => Promise<void>;
  hasMore: boolean;
}

const Row: React.FC<ListChildComponentProps<{
  projects: Project[],
  onProjectClick: (id: number) => void
}>> = ({index, style, data}) => {

  const project = data.projects[index];
  if (!project) return <div style={style}>Loading...</div>;

  return (
      <div style={{...style, padding: "8px", borderBottom: "1px solid #ddd", cursor: "pointer"}}
           onClick={() => data.onProjectClick?.(project.id)}>
        <Card style={{
          width: "100%",
          borderBottom: "0 2px 4px rgba(0, 0, 0, 0.1)",
          borderRadius: "8px",
          transition: "all 0.2s ease-in-out"
        }}>
          <Typography.Title level={4} style={{marginBottom: 0, textAlign: "center"}}>
            {project.name}
          </Typography.Title>
        </Card>
      </div>
  );
};

const ProjectListVirtualized: React.FC<ProjectListVirtualizedProps> = ({
                                                                         projects,
                                                                         onProjectClick,
                                                                         loadMore,
                                                                         hasMore
                                                                       }) => {
  const isItemLoaded = (index: number) => !hasMore || index < projects.length;

  return (
      <InfiniteLoader
          isItemLoaded={isItemLoaded}
          itemCount={hasMore ? projects.length + 1 : projects.length}
          loadMoreItems={loadMore}
      >
        {({onItemsRendered, ref}) => (
            <List
                height={400}
                itemCount={hasMore ? projects.length + 1 : projects.length}
                itemSize={100}
                width="100%"
                onItemsRendered={onItemsRendered}
                ref={ref}
                itemData={{projects, onProjectClick}}
                overscanCount={2}
            >
              {(props: ListChildComponentProps<{
                projects: Project[],
                onProjectClick: (id: number) => void
              }>) => <Row {...props}/>}
            </List>
        )}
      </InfiniteLoader>
  )
}

export default ProjectListVirtualized;