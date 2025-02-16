import React from "react";
import fullStar from "../assets/full-star.png";
import emptyStar from "../assets/empty-star.png";
import InfiniteLoader from "react-window-infinite-loader";
import moment from "moment";
import {Typography} from "antd";
import {FixedSizeList as List, ListChildComponentProps} from "react-window";

interface Todo {
  id: number;
  title: string;
  todoCategory: string;
  isCompleted: boolean;
  isPriority: boolean;
  dueDate: string;
}

interface TodoListVirtualProps {
  todos: Todo[];
  loadMore: () => Promise<void>;
  hasMore: boolean;
  onTodoClick: (id: number) => void;
}

const Row: React.FC<ListChildComponentProps<{
  todos: Todo[];
  onTodoClick: (id: number) => void
}>> = ({index, style, data}) => {

  const todo = data.todos[index];
  if (!todo) {
    return <div style={style}>Loading...</div>
  }
  const categoryIcon = todo.todoCategory === "INDIVIDUAL" ? "😁" : todo.todoCategory === "WORK" ? "💼" : "";
  const priorityIcon = todo.isPriority ? fullStar : emptyStar;
  const dueMoment = todo.dueDate ? moment(todo.dueDate) : null;
  const formattedDueDate = dueMoment ? (
      <span>
        <span style={{fontWeight: "bold", color: "green"}}>{dueMoment.format("ddd")}</span>{" "}
        {dueMoment.format("YY년 M월 D일 H시 m분")}
      </span>
  ) : "";
  const statusText = todo.isCompleted ? "완료" : "진행중";

  return (
      <div style={{...style, padding: "8px 0", borderBottom: "1px solid #ccc", cursor: "pointer"}}
           onClick={() => data.onTodoClick(todo.id)}>
        <div style={{display: "flex", flexDirection: "column", width: "100%"}}>
          <div style={{display: "flex", justifyContent: "space-between", alignItems: "center"}}>
            <div style={{display: "flex", alignItems: "center"}}>
              <span style={{marginRight: 8}}>{categoryIcon}</span>
              <Typography.Text style={{fontSize: "16px", fontWeight: 500}}>
                {todo.title}
              </Typography.Text>
            </div>
            <img src={priorityIcon} alt="priority" style={{width: 20, height: 20}}/>
          </div>
          <div style={{display: "flex", justifyContent: "space-between", marginTop: 4}}>
            <span style={{color: "#999"}}>{formattedDueDate}</span>
            <span style={{color: "#999"}}>{statusText}</span>
          </div>
        </div>
      </div>
  );
};

const TodoListVirtualized: React.FC<TodoListVirtualProps> = ({
                                                               todos,
                                                               loadMore,
                                                               hasMore,
                                                               onTodoClick
                                                             }) => {
  const isItemLoaded = (index: number) => !hasMore || index < todos.length;
  return (
      <InfiniteLoader
          isItemLoaded={isItemLoaded}
          itemCount={hasMore ? todos.length + 1 : todos.length}
          loadMoreItems={loadMore}
      >
        {({onItemsRendered, ref}) => (
            <List
                height={400}
                itemCount={hasMore ? todos.length + 1  : todos.length}
                itemSize={80}
                width="100%"
                onItemsRendered={onItemsRendered}
                ref={ref}
                itemData={{todos, onTodoClick}}
                overscanCount={2}
            >
              {(props: ListChildComponentProps<{
                todos: Todo[];
                onTodoClick: (id: number) => void
              }>) =>
                  <Row {...props}/>}
            </List>
        )}
      </InfiniteLoader>
  );
};

export default TodoListVirtualized;