import React from "react";

import {BaseResourceComponent} from "./base-resource-component";
import Result from "antd/es/result";
import Button from "antd/es/button";

class UnknownErrorPage extends BaseResourceComponent {

    getSecondTitle() {
        return this.props.message;
    }

    render() {
        return (
            <Result
                status="500"
                title="500"
                subTitle={this.getSecondTitle()}
                extra={<Button type="primary" href={window.location.href}>重新加载</Button>}
            />
        )
    }
}

export default UnknownErrorPage;