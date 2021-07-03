import React from "react";

import {BaseResourceComponent} from "../base-resource-component";
import Spin from "antd/lib/spin";
import {Form, Input, Row} from "antd";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Button from "antd/es/button";
import Image from "antd/es/image";
import Dragger from "antd/es/upload/Dragger";
import {message} from "antd/es";
import axios from "axios";
import TextArea from "antd/es/input/TextArea";
import Col from "antd/es/grid/col";

const layout = {
    labelCol: {span: 8},
    wrapperCol: {span: 16},
};

class TemplateConfig extends BaseResourceComponent {

    configFrom = React.createRef();

    initState() {
        return {
            "loading": true
        }
    }

    setValue(changedValues) {
        let allValues = {...this.state.dataMap, ...changedValues};
        this.configFrom.current.setFieldsValue(allValues);
        this.setState({
            dataMap: allValues
        })
    }

    onUploadChange(info, key) {
        const {status} = info.file;
        if (status === 'done') {
            this.state.dataMap[key] = info.file.response.data.url;
            this.setValue(null, this.state.dataMap);
        } else if (status === 'error') {
            message.error(`${info.file.name} file upload failed.`);
        }
    };


    getInput(key, value) {
        if (value.type === 'file') {
            return (<>
                <Dragger style={{width: "164px"}} multiple={false}
                         onChange={(e) => this.onUploadChange(e, key)}
                         name="imgFile"
                         action="/api/admin/upload?dir=image">
                    <Image preview={false} width={128} src={value.value}/>
                </Dragger>
            </>);
        } else if (value.htmlElementType === 'textarea') {
            return <TextArea rows={5} type={value.type} placeholder={value.placeholder}/>;
        } else if (value.type === 'hidden') {
            return <Input hidden={true}/>;
        }
        return <Input type={value.type} placeholder={value.placeholder}/>;
    }

    componentDidMount() {
        super.componentDidMount();

        const query = new URLSearchParams(this.props.location.search);
        const template = query.get("template");

        this.getAxios().get("/api/admin/template/configParams?template=" + template).then(({data}) => {
            const formInputs = [];
            const dataMap = {};
            for (const [key, value] of Object.entries(data.data.config)) {
                const input = <Form.Item
                    label={value.label}
                    name={key}
                    key={key}
                    style={{display: (value.type === 'hidden') ? "none" : ""}}
                >
                    {this.getInput(key, value)}
                </Form.Item>
                formInputs.push(input);
                dataMap[key] = value.value;
            }
            this.setState({
                formInputs: formInputs,
                dataMap: dataMap,
                loading: false
            })
            //console.info(dataMap);
            this.setValue(dataMap);
        });
    }

    getSecondTitle() {
        return this.state.res['templateConfig'];
    }

    onFinish() {
        axios.post("/api/admin/template/config", this.state.dataMap).then(({data}) => {
            if (data.error) {
                message.error(data.message);
            } else {
                message.info(data.message);
            }
            //this.fetchData();
        })
    }

    render() {
        return (
            <Spin delay={this.getSpinDelayTime()} spinning={this.state.loading}>
                <Title className='page-header' level={3}>{this.getSecondTitle()}</Title>
                <Divider/>
                <Row>
                    <Col md={12} xs={24}>
                        <Form ref={this.configFrom}
                              onFinish={(values) => this.onFinish(values)}
                              onValuesChange={(k, v) => this.setValue(k, v)}
                              {...layout}>
                            {this.state.formInputs}
                            <Divider/>
                            <Button type='primary' enterbutton='true'
                                    htmlType='submit'>{this.state.res['submit']}</Button>
                        </Form>
                    </Col>
                </Row>
            </Spin>
        );
    }
}

export default TemplateConfig;
