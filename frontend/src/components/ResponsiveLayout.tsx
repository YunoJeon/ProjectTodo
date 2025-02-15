import {Button, Drawer, Layout, Menu} from "antd";
import {Link, Outlet} from "react-router-dom";
import {MenuOutlined} from "@ant-design/icons";
import "../style/ResponsiveLayout.css";
import React, {useState} from "react";
import LogoutButton from "./LogoutButton";
import {useAuth} from "../context/AuthContext";

const {Header, Content, Footer} = Layout;

const ResponsiveLayOut: React.FC = () => {
  const [drawerVisible, setDrawerVisible] = useState(false);
  const {isLoggedIn} = useAuth();

  const showDrawer = () => {
    setDrawerVisible(true);
  };

  const closeDrawer = () => {
    setDrawerVisible(false);
  };

  console.log("ResponseRayOut isLoggedIn : ", isLoggedIn);

  return (
      <Layout style={{minHeight: '100vh'}}>
        <Header
            style={{
              position: 'fixed',
              width: '100%',
              zIndex: 1,
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              padding: "0 16px"
            }}>
          <div className="logo">☑️ To - Do: 할일 정리 & 협업 도구</div>
          <Button
              type="text"
              icon={<MenuOutlined style={{color: "#fff", fontSize: "20px"}}/>}
              onClick={showDrawer}
          />
        </Header>
        <Drawer
            placement="right"
            closable
            open={drawerVisible}
            onClose={closeDrawer}
            className="custom-drawer"
        >
          <Menu mode="vertical" onClick={closeDrawer} style={{width: 200}}>
            {!isLoggedIn ? (
                <>
                  <Menu.Item key="login">
                    <Link to="/">로그인</Link>
                  </Menu.Item>
                  <Menu.Item key="sign-up">
                    <Link to="/sign-up">회원가입</Link>
                  </Menu.Item>
                </>
            ) : (
                <>
                  <Menu.Item key="dashboard">
                    <Link to="/dashboard">대시보드</Link>
                  </Menu.Item>
                  <Menu.Item key="logout" style={{display: "flex", alignItems: "center"}}>
                    <LogoutButton/>
                  </Menu.Item>
                </>
            )}
          </Menu>
        </Drawer>
        <Content style={{marginTop: 64, padding: '0 24px'}}>
          <div style={{background: '#fff', padding: 24, minHeight: 300}}>
            <Outlet/>
          </div>
        </Content>
        <Footer style={{textAlign: 'center'}}>Todo App ©2025</Footer>
      </Layout>
  );
};

export default ResponsiveLayOut;