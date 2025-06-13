import Login from "./components/login/login";
import { Route, Routes } from "react-router-dom";
import IndexLayout from "./layout/index-layout";
import { getColorPrimary } from "./utils/constants";
import styled from "styled-components";

const AppBase = () => {
    const StyledLayout = styled("div")`
        .ant-menu-item {
            width: 100%;
        }

        #logo {
            height: 51px;
            padding-left: 23px;
            padding-right: 23px;
            overflow: hidden;
            font-size: 25px;
            line-height: 65px;
            color: #ffffff;
        }

        .ant-layout-content {
            padding-left: 12px;
            padding-right: 12px;
        }

        .ant-layout-footer {
            height: 52px;
            padding: 20px;
        }

        .ant-layout-footer-copyright {
            display: block;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .ant-layout-header {
            padding: 0;
        }

        li.ant-menu-item > a {
            color: #ffffff;
        }

        .ant-menu-inline,
        .ant-menu-vertical,
        .ant-menu-vertical-left {
            border: 0;
        }

        li.ant-menu-item-active > a:hover {
            color: #ffffff;
        }

        .ant-menu-item .anticon,
        .ant-menu-submenu-title .anticon {
            margin-top: 12px;
            display: block;
            font-size: 25px;
            margin-right: 0;
        }

        .ant-menu-item > a > span,
        .ant-menu-submenu-title > span {
            display: block;
            font-size: 12px;
            margin-right: 0;
            text-align: center !important;
        }

        .ant-menu-vertical > .ant-menu-item,
        .ant-menu-vertical-left > .ant-menu-item,
        .ant-menu-vertical-right > .ant-menu-item,
        .ant-menu-inline > .ant-menu-item,
        .ant-menu-vertical > .ant-menu-submenu > .ant-menu-submenu-title,
        .ant-menu-vertical-left > .ant-menu-submenu > .ant-menu-submenu-title,
        .ant-menu-vertical-right > .ant-menu-submenu > .ant-menu-submenu-title,
        .ant-menu-inline > .ant-menu-submenu > .ant-menu-submenu-title {
            height: 72px;
            padding: 0;
        }

        .ant-menu-sub > .ant-menu-item {
            font-size: 14px;
            height: 40px;
        }

        .ant-menu-submenu {
            height: 72px;
        }

        .ant-menu-submenu-title > i {
            display: none;
        }

        .ant-menu-submenu-title {
            margin: 0;
        }

        .ant-menu-submenu-title:hover {
            color: #ffffff;
        }

        h3.page-header {
            margin-top: 20px;
            border-left: 3px solid ${getColorPrimary()};
            padding-left: 5px;
            font-size: 1.75rem;
        }

        .userAvatarImg {
            width: 40px;
            height: 40px;
            border-radius: 50%;
        }

        .ant-menu-vertical .ant-menu-item:not(:last-child),
        .ant-menu-vertical-left .ant-menu-item:not(:last-child),
        .ant-menu-vertical-right .ant-menu-item:not(:last-child),
        .ant-menu-inline .ant-menu-item:not(:last-child) {
            margin-bottom: 0;
        }

        .ant-menu-vertical .ant-menu-item,
        .ant-menu-vertical-left .ant-menu-item,
        .ant-menu-vertical-right .ant-menu-item,
        .ant-menu-inline .ant-menu-item,
        .ant-menu-vertical .ant-menu-submenu-title,
        .ant-menu-vertical-left .ant-menu-submenu-title,
        .ant-menu-vertical-right .ant-menu-submenu-title,
        .ant-menu-inline .ant-menu-submenu-title {
            margin: 0;
        }

        .ant-upload-list {
            display: none;
        }

        .ant-menu-submenu-title {
            margin: 0;
            color: white;
            border-radius: 0;
            width: 100%;
        }

        ul {
            margin-bottom: 0;
        }

        .ant-menu-item .anticon,
        .ant-menu-submenu-title .anticon {
            font-size: 24px;
        }

        .ant-typography h3,
        h3.ant-typography {
            margin-bottom: 0.5em;
            font-weight: 600;
            font-size: 24px;
            line-height: 1.35;
        }

        .ant-typography h4,
        h4.ant-typography {
            margin-bottom: 0.5em;
            font-weight: 600;
            font-size: 20px;
            line-height: 1.4;
        }

        .ant-menu {
            text-align: center;
            box-shadow: none;
        }

        .ant-menu .menu-title {
            margin-left: 0 !important;
            font-size: 12px;
        }

        .ant-form-item-explain-error {
            color: #ff4d4f;
        }

        .ant-form-item-has-error .ant-radio-group {
            border: 1px solid #ff4d4f;
            border-radius: 6px;
        }

        .ant-upload {
            padding: 0 !important;
            box-sizing: content-box;
        }

        .ant-input-search-button {
            background-color: ${getColorPrimary()} !important;
        }

        .ant-input-search-button:hover {
            background-color: ${getColorPrimary()} !important;
        }

        .ant-input-search-button:active {
            background-color: ${getColorPrimary()} !important;
        }
    `;

    return (
        <StyledLayout>
            <Routes>
                <Route path={"login"} element={<Login />} />
                <Route path={"*"} element={<IndexLayout />} />
            </Routes>
        </StyledLayout>
    );
};

export default AppBase;
