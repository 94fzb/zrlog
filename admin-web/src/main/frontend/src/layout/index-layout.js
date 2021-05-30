import React from 'react';
import {
    ApiOutlined,
    AppstoreOutlined,
    CommentOutlined,
    ContainerOutlined,
    DashboardOutlined,
    DownOutlined,
    FormOutlined,
    HomeOutlined,
    KeyOutlined,
    LogoutOutlined,
    SettingOutlined,
    UserOutlined
} from '@ant-design/icons';
import {Col, Layout, Menu, Row, Spin, Typography} from 'antd';
import {Link} from 'react-router-dom'

import Button from "antd/es/button";
import Dropdown from "antd/es/dropdown";
import Divider from "antd/es/divider";
import Image from "antd/es/image";
import * as axios from "axios";

import {BaseResourceComponent} from "../components/base-resource-component";
import AdminLoginedRouter from "../routers/admin-logined-router";
import Constants from "../utils/constants";

const {Header, Content, Footer, Sider} = Layout;
const {SubMenu} = Menu;
const {Text} = Typography;

function getDefaultOpenKeys() {
    if (window.location.pathname === '/admin/link' || window.location.pathname === '/admin/nav' || window.location.pathname === '/admin/blog/type') {
        return "more";
    }
    return "";
}

const defaultOpenKeys = getDefaultOpenKeys();

class IndexLayout extends BaseResourceComponent {

    initState() {
        return {
            basicInfoLoading: true,
            basicInfo: {}
        };
    }


    componentDidMount() {
        super.componentDidMount();
        axios.get("/api/admin/user/basicInfo").then(({data}) => {
            this.setState({
                basicInfo: data.data,
                basicInfoLoading: false,
            })
        })
    }

    handleMenuClick(e) {
        console.log('click', e);
    }

    adminSettings(res) {
        return (
            <Menu className="userInfoMenu">
                <Menu.Item key="2">
                    <Link to="/admin/user">
                        <UserOutlined/>
                        <Text style={{paddingLeft: "5px"}}>{res['admin.user.info']}</Text>
                    </Link>
                </Menu.Item>
                <Menu.Item key="1">
                    <Link to="/admin/user-update-password">
                        <KeyOutlined/>
                        <Text style={{paddingLeft: "5px"}}>{res['admin.changePwd']}</Text>
                    </Link>
                </Menu.Item>
                <Divider style={{marginTop: "5px", marginBottom: "5px"}}/>
                <Menu.Item key="3">
                    <a href="/admin/logout">
                        <LogoutOutlined/>
                        <Text style={{paddingLeft: "5px"}}>{res['admin.user.logout']}</Text>
                    </a>
                </Menu.Item>
            </Menu>
        );
    }

    render() {

        return (
            <Spin spinning={this.state.resLoading && this.state.basicInfoLoading}>
                <Header>
                    <a href="/" id='logo' target="_blank" title={this.state.res['websiteTitle']}
                       rel="noopener noreferrer">
                        <HomeOutlined/>
                    </a>
                    <Dropdown overlay={this.adminSettings(this.state.res)}
                              overlayStyle={{float: "right"}}>
                        <div className='userMenu'>

                            <Button size='large' type='text'
                                    style={{
                                        color: "#ffffff", paddingLeft: 8,
                                        height: "64px", float: "right"
                                    }}>
                                <Text style={{
                                    color: "#ffffff"
                                }}>{this.state.basicInfo.userName}</Text>
                                <DownOutlined/>
                            </Button>
                            <Image preview={false}
                                   fallback={Constants.getFillBackImg()}
                                   className="userAvatarImg"
                                   src={this.state.basicInfo.header}
                                   style={{float: "right", cursor: "pointer"}}/>
                        </div>
                    </Dropdown>
                </Header>
                <Row>
                    <Col style={{minHeight: "100vh"}} id='sider'>
                        <Sider width={70} style={{minHeight: "100vh"}}>
                            <Menu defaultOpenKeys={[defaultOpenKeys]} defaultSelectedKeys={[window.location.pathname]}
                                  mode="vertical">
                                <Menu.Item key="/admin/index">
                                    <Link to='/admin/index'>
                                        <DashboardOutlined/>
                                        <span>{this.state.res.dashboard}</span>
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="/admin/article-edit">
                                    <Link to='/admin/article-edit'>
                                        <FormOutlined/>
                                        <span>{this.state.res['admin.log.edit']}</span>
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="/admin/article">
                                    <Link to='/admin/article'>
                                        <ContainerOutlined/>
                                        <span>{this.state.res['blogManage']}</span>
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="/admin/comment">
                                    <Link to='/admin/comment'>
                                        <CommentOutlined/>
                                        <span>{this.state.res['admin.comment.manage']}</span>
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="/admin/plugin">
                                    <Link to='/admin/plugin'>
                                        <ApiOutlined/>
                                        <span>{this.state.res['admin.plugin.manage']}</span>
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="/admin/website">
                                    <Link to='/admin/website'>
                                        <SettingOutlined/>
                                        <span>{this.state.res['admin.setting']}</span>
                                    </Link>
                                </Menu.Item>
                                <SubMenu key='more' inlineCollapsed={false}
                                         icon={<AppstoreOutlined/>}
                                         title={this.state.res['admin.more']}>
                                    <Menu.Item style={{width: 120}} key="/admin/article-type">
                                        <Link to='/admin/article-type'>
                                            <span>{this.state.res['admin.type.manage']}</span>
                                        </Link>
                                    </Menu.Item>
                                    <Menu.Item style={{width: 120}} key="/admin/link">
                                        <Link to='/admin/link'>
                                            <span>{this.state.res['admin.link.manage']}</span>
                                        </Link>
                                    </Menu.Item>
                                    <Menu.Item style={{width: 120}} key="/admin/nav">
                                        <Link to='/admin/nav'>
                                            <span>{this.state.res['admin.nav.manage']}</span>
                                        </Link>
                                    </Menu.Item>
                                </SubMenu>
                            </Menu>
                        </Sider>
                    </Col>
                    <Col style={{flex: 1, width: 100}}>
                        <Layout style={{minHeight: "100vh"}}>
                            <Content>
                                <AdminLoginedRouter/>
                            </Content>
                            <Footer>
                                <Row>
                                    <Col xs={24} md={12}>
                                        <div className='ant-layout-footer-copyright'
                                             dangerouslySetInnerHTML={{__html: this.state.res.copyrightTips + '. All Rights Reserved.'}}/>
                                    </Col>
                                    <Col xs={0} md={12}>
                                        <Text style={{float: 'right'}}>
                                            Version {this.state.res.currentVersion}
                                        </Text>
                                    </Col>
                                </Row>
                            </Footer>
                        </Layout>
                    </Col>
                </Row>
            </Spin>
        )
    }
}

export default IndexLayout;
