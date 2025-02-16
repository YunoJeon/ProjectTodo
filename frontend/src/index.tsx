import React from 'react';
import ReactDOM from 'react-dom/client';
import {ConfigProvider} from 'antd';
import koKR from 'antd/lib/locale/ko_KR';
import 'antd/dist/reset.css';
import {AuthProvider} from "./context/AuthContext";
import AppRoutes from "./routes/AppRoutes";

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);
root.render(
    <React.StrictMode>
      <AuthProvider>
        <ConfigProvider locale={koKR}>
        <AppRoutes />
        </ConfigProvider>
      </AuthProvider>
    </React.StrictMode>
);