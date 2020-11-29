import React from 'react'
import {
    AppstoreOutlined,
    CommentOutlined,
    ContainerOutlined,
    DashboardOutlined,
    DownOutlined,
    FormOutlined,
    HomeOutlined,
    KeyOutlined,
    LogoutOutlined,
    QuestionCircleOutlined,
    SettingOutlined,
    UserOutlined
} from '@ant-design/icons';
import {Col, Layout, Menu, Row, Spin, Typography} from 'antd';
import {Link, Route, Switch} from 'react-router-dom'
import {Index} from "../components";
import {BaseResourceComponent} from "../components/base-resource-component";
import {ArticleEdit} from "../components/article-edit";
import {Article} from "../components/article";
import {Comment} from "../components/comment";
import {Plugin} from "../components/plugin";
import {Website} from "../components/website";
import {Nav} from "../components/nav";
import {BLink} from "../components/link";
import {Type} from "../components/type";
import {NotFoundPage} from "../components/not-found-page";
import Button from "antd/es/button";
import Dropdown from "antd/es/dropdown";
import Divider from "antd/es/divider";
import Image from "antd/es/image";
import {User} from "../components/user";
import {UserUpdatePassword} from "../components/user-update-password";
import * as axios from "axios";
import {TemplateCenter} from "../components/template-center";
import {Upgrade} from "../components/upgrade";


const {Header, Content, Footer, Sider} = Layout;
const {SubMenu} = Menu;
const {Title, Text} = Typography;

function getDefaultOpenKeys() {
    if (window.location.pathname === '/admin/link' || window.location.pathname === '/admin/nav' || window.location.pathname === '/admin/blog/type') {
        return "more";
    }
    return;
}

const defaultOpenKeys = getDefaultOpenKeys();

export class IndexLayout extends BaseResourceComponent {

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
                <Layout style={{minHeight: '100vh'}}>
                    <Sider
                        collapsed={this.state.collapsed}
                        onCollapse={this.onCollapse}
                        width={70}
                    >
                        <a href="/" target="_blank" title={this.state.res['websiteTitle']} rel="noopener noreferrer">
                            <Title id='logo'>
                                <HomeOutlined/>
                            </Title>
                        </a>
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
                                    <QuestionCircleOutlined/>
                                    <span>{this.state.res['admin.plugin.manage']}</span>
                                </Link>
                            </Menu.Item>
                            <Menu.Item key="/admin/website">
                                <Link to='/admin/website'>
                                    <SettingOutlined/>
                                    <span>{this.state.res['admin.setting']}</span>
                                </Link>
                            </Menu.Item>
                            <SubMenu key='more' inlineCollapsed={false} icon={<AppstoreOutlined/>}
                                     title={this.state.res['admin.more']}>
                                <Menu.Item key="/admin/article-type">
                                    <Link to='/admin/article-type'>
                                        <span>{this.state.res['admin.type.manage']}</span>
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="/admin/link">
                                    <Link to='/admin/link'>
                                        <span>{this.state.res['admin.link.manage']}</span>
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="/admin/nav">
                                    <Link to='/admin/nav'>
                                        <span>{this.state.res['admin.nav.manage']}</span>
                                    </Link>
                                </Menu.Item>
                            </SubMenu>
                        </Menu>
                    </Sider>
                    <Layout>
                        <Header>
                            <Row>
                                <Col span={24}>
                                    <Dropdown overlay={this.adminSettings(this.state.res)}
                                              overlayStyle={{float: "right"}}>
                                        <Button size='large' type='text' style={{height: "64px", float: "right"}}>
                                            <Image preview={false} className="userAvatarImg"
                                                   src={this.state.basicInfo.header}/>
                                            <Text style={{paddingLeft: "10px"}}>{this.state.basicInfo.userName}</Text>
                                            <DownOutlined/>
                                        </Button>
                                    </Dropdown>
                                </Col>
                            </Row>
                        </Header>
                        <Content style={{margin: '4px'}}>
                            <Switch>
                                <Route path="/admin/index" component={Index}/>
                                <Route path="/admin/article-edit" component={ArticleEdit}/>
                                <Route path="/admin/comment" component={Comment}/>
                                <Route path="/admin/plugin" component={Plugin}/>
                                <Route path="/admin/website" component={Website}/>
                                <Route path="/admin/article-type" component={Type}/>
                                <Route path="/admin/link" component={BLink}/>
                                <Route path="/admin/nav" component={Nav}/>
                                <Route path="/admin/article" component={Article}/>
                                <Route path="/admin/user" component={User}/>
                                <Route path="/admin/template-center" component={TemplateCenter}/>
                                <Route path="/admin/user-update-password" component={UserUpdatePassword}/>
                                <Route path="/admin/upgrade" component={Upgrade}/>
                                <Route component={NotFoundPage}/>
                            </Switch>
                        </Content>
                        <Footer>
                            <Row>
                                <Col xs={24} md={12}>
                                    <Text>
                                        {this.state.res.copyrightTips}. All Rights Reserved.
                                    </Text>
                                </Col>
                                <Col xs={0} md={12}>
                                    <Text style={{float: 'right'}}>
                                        Version {this.state.res.currentVersion}
                                    </Text>
                                </Col>
                            </Row>
                        </Footer>
                    </Layout>
                </Layout>
            </Spin>
        )
    }
}