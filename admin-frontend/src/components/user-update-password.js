import React from "react";
import Spin from "antd/lib/spin";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import {BaseResourceComponent} from "./base-resource-component";
import Form from "antd/es/form";
import {Input} from "antd";
import Button from "antd/es/button";
import {message} from "antd/es";
import axios from "axios";

const layout = {
    labelCol: {span: 3},
    wrapperCol: {span: 8},
};


export class UserUpdatePassword extends BaseResourceComponent {

    pwdFrom = React.createRef();

    getSecondTitle() {
        return this.state.res['admin.changePwd'];
    }

    onFinish(allValues) {
        axios.post("/api/admin/user/updatePassword", JSON.stringify(allValues)).then(({data}) => {
            if (data.error) {
                message.error(data.message);
            } else {
                message.success(data.message);
            }
        });
    }

    render() {
        return (
            <Spin spinning={this.state.resLoading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <Form {...layout} ref={this.pwdFrom} onFinish={this.onFinish}>
                    <Form.Item name='oldPassword' label={this.state.res['admin.oldPassword']}
                               rules={[{required: true}]}>
                        <Input.Password/>
                    </Form.Item>
                    <Form.Item name='newPassword' label={this.state.res['admin.newPassword']}
                               rules={[{required: true}]}>
                        <Input.Password/>
                    </Form.Item>
                    <Divider/>
                    <Button type='primary' enterButton htmlType='submit'>{this.state.res.submit}</Button>
                </Form>
            </Spin>
        )
    }
}