import {BrowserRouter, Route, Routes} from "react-router-dom";
import React from "react";
import SignUpPage from "../pages/SignUpPage";
import ResponsiveLayout from "../components/ResponsiveLayout";
import LoginPage from "../pages/LoginPage";
import DashboardPage from "../pages/DashboardPage";
import ProjectTodosPage from "../pages/ProjectTodosPage";

const AppRoutes = () => {
  return (
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<ResponsiveLayout/>}>
            <Route index element={<LoginPage/>}/>
            <Route path="sign-up" element={<SignUpPage/>}/>
            <Route path="dashboard" element={<DashboardPage/>}/>
            <Route path="projects/:projectId/todos" element={<ProjectTodosPage/>}/>
          </Route>
        </Routes>
      </BrowserRouter>
  );
};

export default AppRoutes;