import React from 'react';
import './App.css';
import {Route, Switch} from "react-router-dom";
import {Login} from "./components/login";
import {IndexLayout} from "./layout/index-layout";
import {Router} from "react-router";
import {createBrowserHistory} from 'history';
import {ConfigProvider} from "antd";
import zh_CN from 'antd/es/locale/zh_CN';

const history = createBrowserHistory();

function App() {
    return (
        <ConfigProvider locale={zh_CN}>
            <Router history={history}>
                <Switch>
                    <Route path="/admin/login" component={Login}/>
                    <Route exact path="/admin/*" component={IndexLayout}/>
                    <Route component={Login}/>
                </Switch>
            </Router>
        </ConfigProvider>
    );
}

export default App;
