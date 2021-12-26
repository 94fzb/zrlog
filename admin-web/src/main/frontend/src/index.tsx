import ReactDOM from 'react-dom';
import './index.css';
import * as serviceWorker from './serviceWorker';
import {Router} from "react-router";
import zh_CN from "antd/es/locale/zh_CN";
import {ConfigProvider} from "antd";
import ThemeIndex from "./theme-index";
import Constants from "./utils/constants";


ReactDOM.render(
    <ConfigProvider locale={zh_CN}>
        <Router history={Constants.getHistory()}>
            <ThemeIndex/>
        </Router>
    </ConfigProvider>,
    document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
