import ReactDOM from "react-dom";
import "./index.css";
import * as serviceWorker from "./serviceWorker";
import zh_CN from "antd/es/locale/zh_CN";
import { ConfigProvider } from "antd";
import ThemeIndex from "./theme-index";
import { BrowserRouter } from "react-router-dom";

export const basePath = "/admin/";

ReactDOM.render(
    <ConfigProvider locale={zh_CN}>
        <BrowserRouter basename={basePath}>
            <ThemeIndex />
        </BrowserRouter>
    </ConfigProvider>,
    document.getElementById("root")
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
