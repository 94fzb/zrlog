import React from "react";

import {BaseResourceComponent} from "./base-resource-component";
import Result from "antd/es/result";
import Button from "antd/es/button";
import {Link} from "react-router-dom";

class NotFoundPage extends BaseResourceComponent {

    getSecondTitle() {
        return this.state.res.notFound;
    }

    render() {
        return (
            <Result
                status="404"
                title="404"
                subTitle={this.getSecondTitle()}
                extra={<Button type="primary"><Link target='_top' to='/admin/index'>Back Home</Link></Button>}
            />
        )
    }
}

export default NotFoundPage;