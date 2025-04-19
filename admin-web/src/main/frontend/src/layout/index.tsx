import { HomeOutlined, MenuFoldOutlined, MenuUnfoldOutlined } from "@ant-design/icons";
import { Button, Col, FloatButton, Layout, Row } from "antd";

import { getRes } from "../utils/constants";
import { FunctionComponent, PropsWithChildren, useEffect, useState } from "react";
import EnvUtils from "../utils/env-utils";
import UserInfo from "./user-info";
import { getColorPrimary } from "../utils/constants";
import SliderMenu from "./slider";
import { BasicUserInfo } from "../type";
import { ssData } from "../index";
import axios from "axios";
import MyLoadingComponent from "../components/my-loading-component";
import PWAHandler from "../PWAHandler";
import StyledIndexLayout from "./styled-index-layout";
import type { ScreenMap } from "antd/es/_util/responsiveObserver";
import useBreakpoint from "antd/es/grid/hooks/useBreakpoint";
import { addToCache, getCacheByKey } from "../cache";

const { Header, Content, Sider } = Layout;

type AdminManageLayoutProps = PropsWithChildren & {
    loading: boolean;
    fullScreen?: boolean;
    offline: boolean;
};

const AdminManageLayout: FunctionComponent<AdminManageLayoutProps> = ({ offline, children, loading, fullScreen }) => {
    const [userInfo, setUser] = useState<BasicUserInfo | undefined>(ssData?.user);
    const screens = useBreakpoint();

    const sliderStateKey = "sliderOpen";

    const mobileDevice = 576;

    const needCollSlider = (s: ScreenMap) => {
        if (window.innerWidth < mobileDevice) {
            const state = getCacheByKey(sliderStateKey);
            return state === undefined || state === null || state;
        }
        return s.xs === true;
    };
    const [showSliderBtn, setShowSliderBtn] = useState<boolean>(window.innerWidth < mobileDevice);
    const defaultHiddenSlider = needCollSlider(screens);
    const [hiddenSlider, setHiddenSlider] = useState(defaultHiddenSlider);

    useEffect(() => {
        if (userInfo === undefined) {
            axios.get("/api/admin/user").then(({ data }) => {
                setUser(data.data);
            });
        }
    }, []);

    const getMainHeight = () => {
        return "calc(100vh - 64px)";
    };

    useEffect(() => {
        setHiddenSlider(needCollSlider(screens));
        setShowSliderBtn(screens.xs === true);
    }, [screens]);

    if (screens.xs === undefined) {
        return <></>;
    }

    const getMainButton = () => {
        const home = (
            <a
                href={getRes()["homeUrl"] + "?spm=admin&buildId=" + getRes()["buildId"]}
                id="logo"
                target="_blank"
                title={getRes()["websiteTitle"]}
                rel="noopener noreferrer"
            >
                <HomeOutlined />
            </a>
        );
        if (showSliderBtn) {
            return (
                <div style={{ display: "flex", alignItems: "center", justifyContent: "flex-start" }}>
                    <div
                        style={{
                            textAlign: "center",
                            width: 70,
                            height: "100%",
                            cursor: "pointer",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                        }}
                        onClick={() => {
                            const newState = !hiddenSlider;
                            addToCache(sliderStateKey, newState);
                            setHiddenSlider(newState);
                        }}
                    >
                        <Button type="primary">{hiddenSlider ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}</Button>
                    </div>
                    {home}
                </div>
            );
        }
        return home;
    };

    return (
        <PWAHandler>
            <StyledIndexLayout>
                <Header
                    style={{
                        display: fullScreen ? "none" : "flex",
                        justifyContent: "space-between",
                        backgroundColor: EnvUtils.isDarkMode() ? "#1f1f1f" : "#011529",
                    }}
                >
                    {getMainButton()}
                    {offline && (
                        <span
                            style={{
                                display: "inline-block",
                                textAlign: "center",
                                fontSize: 20,
                                paddingLeft: 24,
                                userSelect: "none",
                                color: getColorPrimary(),
                            }}
                        >
                            {getRes()["admin.offline.desc"]}
                        </span>
                    )}
                    {userInfo && <UserInfo offline={offline} data={userInfo} />}
                </Header>
                <Row
                    style={{
                        transition: "all .2s ease",
                        position: "relative",
                        minHeight: getMainHeight(),
                    }}
                >
                    <Sider
                        width={70}
                        style={{
                            opacity: fullScreen || hiddenSlider ? 0 : 1,
                            position: "absolute",
                            left: hiddenSlider ? "-70px" : "0", // 动画控制显示隐藏
                            height: "100%",
                            transform: fullScreen || hiddenSlider ? "translateX(-100%)" : "translateX(0)",
                            backgroundColor: EnvUtils.isDarkMode() ? "#1f1f1f" : "#001529",
                        }}
                    >
                        <SliderMenu />
                    </Sider>
                    <Col
                        style={{
                            flex: 1,
                            width: 100,
                            minHeight: fullScreen ? 0 : 1,
                            transition: "margin-left .2s ease", // 动画完成后调整布局
                            marginLeft: hiddenSlider || fullScreen ? 0 : 70,
                        }}
                    >
                        <Layout style={{ minHeight: getMainHeight(), overflow: fullScreen ? "hidden" : "auto" }}>
                            <Content
                                style={{
                                    paddingRight: fullScreen ? 0 : 12,
                                    paddingLeft: fullScreen ? 0 : 12,
                                    paddingBottom: fullScreen ? 0 : 12,
                                }}
                            >
                                {loading && <MyLoadingComponent />}
                                {children}
                            </Content>
                        </Layout>
                    </Col>
                </Row>
                <FloatButton.BackTop />
            </StyledIndexLayout>
        </PWAHandler>
    );
};

export default AdminManageLayout;
