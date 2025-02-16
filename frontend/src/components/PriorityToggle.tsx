import React from "react";
import fullStar from "../assets/full-star.png";
import emptyStar from "../assets/empty-star.png";

interface PriorityToggleProps {
  isPriority: boolean;
  onChange: (isPriority: boolean) => void;
}

const PriorityToggle: React.FC<PriorityToggleProps> = ({isPriority, onChange}) => {

  return (
      <div onClick={() => onChange(!isPriority)} style={{cursor: 'pointer'}}>
        <img
          src={isPriority ? fullStar : emptyStar}
          alt="priority"
          style={{width: 20, height: 20}}
          />
      </div>
  );
};

export default PriorityToggle;