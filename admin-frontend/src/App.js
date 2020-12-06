import React from 'react';
import './App.css';
import {Route, Switch} from "react-router-dom";
import {Router} from "react-router";
import {createBrowserHistory} from 'history';
import {ConfigProvider} from "antd";
import zh_CN from 'antd/es/locale/zh_CN';
import Loadable from "react-loadable";
import UnknownErrorPage from "./components/unknown-error-page";

const history = createBrowserHistory();

export const MyLoadingComponent = ({isLoading, error}) => {
    if (isLoading) {
        return <div/>;
    } else if (error) {
        console.info(error);
        return <UnknownErrorPage message={error.toString()}/>;
    } else {
        return null;
    }
};

const AsyncIndexLayout = Loadable({
    loader: () => import('./layout/index-layout'),
    loading: MyLoadingComponent
});

const AsyncLoginLayout = Loadable({
    loader: () => import('./components/login/login'),
    loading: MyLoadingComponent
});

function App() {
    return (
        <ConfigProvider locale={zh_CN}>
            <Router history={history}>
                <Switch>
                    <Route path="/admin/login" component={AsyncLoginLayout}/>
                    <Route exact path="/admin/*" component={AsyncIndexLayout}/>
                    <Route component={AsyncLoginLayout}/>
                </Switch>
            </Router>
        </ConfigProvider>
    );
}

export default App;
