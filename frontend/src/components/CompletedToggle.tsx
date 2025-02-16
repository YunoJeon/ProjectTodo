import React from "react";

interface CompletedToggleProps {
  isCompleted: boolean;
  onChange: (isCompleted: boolean) => void;
}

const CompletedToggle: React.FC<CompletedToggleProps> = ({isCompleted, onChange}) => {

  return (
      <div onClick={() => onChange(!isCompleted)} style={{cursor: 'pointer'}}>
        {isCompleted ? "✅" : "☑️"}
      </div>
  );
};

export default CompletedToggle;