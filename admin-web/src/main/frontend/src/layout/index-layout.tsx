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
    SoundOutlined,
    UserOutlined
} from '@ant-design/icons';
import {Badge, Col, Layout, Menu, Row, Spin, Typography} from 'antd';
import {Link} from 'react-router-dom'

import Button from "antd/es/button";
import Dropdown from "antd/es/dropdown";
import Divider from "antd/es/divider";
import Image from "antd/es/image";
import AdminLoginedRouter from "../routers/admin-logined-router";
import Constants, {getRes} from "../utils/constants";
import {useEffect, useState} from "react";
import axios from "axios";
import "./index-layout.less";

const {Header, Content, Footer, Sider} = Layout;
const {SubMenu} = Menu;
const {Text} = Typography;

function getDefaultOpenKeys() {
    if (window.location.pathname === './link' || window.location.pathname === './nav' || window.location.pathname === './blog/type') {
        return "more";
    }
    return "";
}

const defaultOpenKeys = getDefaultOpenKeys();

type BasicInfo = {
    userName: string,
    header: string
}

type IndexLayoutState = {
    basicInfoLoading: boolean,
    basicInfo: BasicInfo,
    upgrade: boolean,
    newVersion: string,
    versionType: string
}

const IndexLayout = () => {

    const [layoutState, setLayoutState] = useState<IndexLayoutState>({
        basicInfoLoading: false,
        basicInfo: {
            userName: "",
            header: ""
        },
        upgrade: false,
        newVersion: "",
        versionType: "",
    })

    const loadInfo = () => {
        axios.get("/api/admin/user/basicInfo").then(({data}) => {
            if (data.data.lastVersion.version) {
                setLayoutState({
                    ...layoutState,
                    basicInfoLoading: false,
                    basicInfo: data.data,
                    upgrade: data.data.lastVersion.upgrade,
                    newVersion: data.data.lastVersion.version.version,
                    versionType: data.data.lastVersion.version.type
                })
            } else {
                setLayoutState({
                    ...layoutState,
                    basicInfoLoading: false,
                    basicInfo: data.data,
                })
            }
        })
    }

    useEffect(() => {
        loadInfo();
    }, [])


    const adminSettings = (res: Record<string, never>) => {
        return (
            <Menu className="userInfoMenu">
                <Menu.Item key="0" hidden={!layoutState.upgrade}>
                    <Link to="./upgrade">
                        <Badge dot={layoutState.upgrade}>
                            <SoundOutlined/>
                            <Text style={{paddingLeft: "12px"}}>{res['newVersion']} -
                                ({layoutState.newVersion}#{layoutState.versionType})</Text>
                        </Badge>
                    </Link>
                </Menu.Item>
                <Menu.Item key="2">
                    <Link to="./user">
                        <UserOutlined/>
                        <Text style={{paddingLeft: "5px"}}>{res['admin.user.info']}</Text>
                    </Link>
                </Menu.Item>
                <Menu.Item key="1">
                    <Link to="./user-update-password">
                        <KeyOutlined/>
                        <Text style={{paddingLeft: "5px"}}>{res['admin.changePwd']}</Text>
                    </Link>
                </Menu.Item>
                <Divider style={{marginTop: "5px", marginBottom: "5px"}}/>
                <Menu.Item key="3">
                    <a href="./admin/logout">
                        <LogoutOutlined/>
                        <Text style={{paddingLeft: "5px"}}>{res['admin.user.logout']}</Text>
                    </a>
                </Menu.Item>
            </Menu>
        );
    }

    const getSelectKey = () => {
        return "." + window.location.pathname.substring(window.location.pathname.lastIndexOf("/"));
    }

    if (layoutState.basicInfoLoading) {
        return <Spin style={{height: "100vh"}}/>
    }


    return (
        <>
            <div style={{height: "100vh"}}>
                <Header>
                    <a href={document.baseURI} id='logo' target="_blank" title={getRes()['websiteTitle']}
                       rel="noopener noreferrer">
                        <HomeOutlined/>
                    </a>
                    <Dropdown overlay={adminSettings(getRes())}
                              overlayStyle={{float: "right"}}>
                        <div className='userMenu' hidden={layoutState.basicInfoLoading}>
                            <Button size='large' type='text'
                                    style={{
                                        color: "#ffffff", paddingLeft: 8,
                                        height: "64px", float: "right"
                                    }}>
                                <Badge dot={layoutState.upgrade}>
                                    <Text style={{
                                        color: "#ffffff"
                                    }}>{layoutState.basicInfo.userName}</Text>
                                </Badge>
                                <DownOutlined/>
                            </Button>
                            <Image preview={false}
                                   fallback={Constants.getFillBackImg()}
                                   className="userAvatarImg"
                                   src={layoutState.basicInfo.header}
                                   style={{float: "right", cursor: "pointer"}}/>
                        </div>
                    </Dropdown>
                </Header>
                <Row>
                    <Col style={{minHeight: "100vh"}} id='sider'>
                        <Sider width={70} style={{minHeight: "100vh"}}>
                            <Menu defaultOpenKeys={[defaultOpenKeys]} selectedKeys={[getSelectKey()]}
                                  defaultSelectedKeys={[getSelectKey()]}
                                  mode="vertical">
                                <Menu.Item key="./index">
                                    <Link to='./index'>
                                        <DashboardOutlined/>
                                        <span className='menu-title'>{getRes().dashboard}</span>
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="./article-edit">
                                    <Link to='./article-edit'>
                                        <FormOutlined/>
                                        <span className='menu-title'>{getRes()['admin.log.edit']}</span>
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="./article">
                                    <Link to='./article'>
                                        <ContainerOutlined/>
                                        <span className='menu-title'>{getRes()['blogManage']}</span>
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="./comment">
                                    <Link to='./comment'>
                                        <CommentOutlined/>
                                        <span className='menu-title'>{getRes()['admin.comment.manage']}</span>
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="./plugin">
                                    <Link to='./plugin'>
                                        <ApiOutlined/>
                                        <span className='menu-title'>{getRes()['admin.plugin.manage']}</span>
                                    </Link>
                                </Menu.Item>
                                <Menu.Item key="./website">
                                    <Link to='./website'>
                                        <SettingOutlined/>
                                        <span className='menu-title'>{getRes()['admin.setting']}</span>
                                    </Link>
                                </Menu.Item>
                                <SubMenu key='more' icon={<AppstoreOutlined/>}
                                         className='menu-title'
                                         title={getRes()['admin.more']}>
                                    <Menu.Item style={{width: 120}} key="./article-type">
                                        <Link to='./article-type'>
                                            <span>{getRes()['admin.type.manage']}</span>
                                        </Link>
                                    </Menu.Item>
                                    <Menu.Item style={{width: 120}} key="./link">
                                        <Link to='./link'>
                                            <span>{getRes()['admin.link.manage']}</span>
                                        </Link>
                                    </Menu.Item>
                                    <Menu.Item style={{width: 120}} key="./nav">
                                        <Link to='./nav'>
                                            <span>{getRes()['admin.nav.manage']}</span>
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
                                             dangerouslySetInnerHTML={{__html: getRes().copyrightTips + '. All Rights Reserved.'}}/>
                                    </Col>
                                    <Col xs={0} md={12}>
                                        <Text style={{float: 'right'}}>
                                            Version {getRes().currentVersion}
                                        </Text>
                                    </Col>
                                </Row>
                            </Footer>
                        </Layout>
                    </Col>
                </Row>
            </div>
        </>
    )
}

export default IndexLayout;
