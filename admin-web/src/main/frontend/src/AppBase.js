import React from 'react';
import {Route, Switch} from "react-router";
import {BaseResourceComponent} from "./components/base-resource-component";
import Login from "./components/login/login";
import IndexLayout from "./layout/index-layout";


class AppBase extends BaseResourceComponent {

    render() {
        return (
            <Switch>
                <Route path="/admin/login" component={Login}/>
                <Route exact path="/admin/*" component={IndexLayout}/>
                <Route component={IndexLayout}/>
            </Switch>
        );
    }
}

export default AppBase;
