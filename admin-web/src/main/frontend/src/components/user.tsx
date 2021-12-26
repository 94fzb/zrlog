import {useEffect, useState} from "react";
import Title from "antd/es/typography/Title";
import Divider from "antd/es/divider";
import Form from "antd/es/form";
import {Button, Input} from "antd";
import Dragger from "antd/es/upload/Dragger";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import {message} from "antd/es";
import Image from "antd/es/image";
import Constants, {getRes} from "../utils/constants";
import axios from "axios";
import {UploadChangeParam} from "antd/es/upload";

const layout = {
    labelCol: {span: 8},
    wrapperCol: {span: 16},
};

type BasicUserInfo = {
    userName: string,
    header: string,
    email: string,
    _loading: boolean,
}

const User = () => {

    const [userInfo, setUserInfo] = useState<BasicUserInfo>({
        userName: "",
        header: "",
        email: "",
        _loading: true,
    })

    const setValue = (changedValues: any) => {
        setUserInfo(changedValues);
    }

    const onUploadChange = (info: UploadChangeParam) => {
        const {status} = info.file;
        if (status === 'done') {
            userInfo.header = info.file.response.data.url;
            setUserInfo(userInfo);
        } else if (status === 'error') {
            message.error(`${info.file.name} file upload failed.`);
        }
    };

    const onFinish = () => {
        axios.post("/api/admin/user/update", userInfo).then(({data}) => {
            if (data.error) {
                message.error(data.message);
            } else {
                message.info(data.message);
            }
        })
    }

    useEffect(() => {
        axios.get("/api/admin/user/basicInfo").then(({data}) => {
            setUserInfo({...userInfo, ...data.data, _loading: false})
        })
    }, [])

    if (userInfo._loading) {
        return <></>
    }

    return (
        <>
            <Title className='page-header' level={3}>{getRes()['admin.user.info']}</Title>
            <Divider/>
            <Row>
                <Col md={12} xs={24}>
                    <Form onFinish={() => onFinish()}
                          initialValues={userInfo}
                          onValuesChange={(_k, v) => setValue(v)}
                          {...layout}>
                        <Form.Item
                            label={getRes().userName}
                            name="userName"
                            rules={[{required: true}]}
                        >
                            <Input/>
                        </Form.Item>

                        <Form.Item
                            name="email"
                            label={getRes().email}
                        >
                            <Input type={"email"}/>
                        </Form.Item>

                        <Form.Item
                            label={getRes().headPortrait}
                            rules={[{required: true}]}
                        >
                            <Dragger style={{width: "128px", height: "128px"}} multiple={false}
                                     onChange={(e) => onUploadChange(e)}
                                     name="imgFile"
                                     action="/api/admin/upload?dir=image">
                                <Image fallback={Constants.getFillBackImg()} preview={false}
                                       height={128}
                                       width={128}
                                       src={userInfo.header}/>
                            </Dragger>
                        </Form.Item>
                        <Divider/>
                        <Form.Item>
                            <Button type="primary" htmlType='submit'>
                                {getRes().submit}
                            </Button>
                        </Form.Item>
                    </Form>
                </Col>
            </Row>
        </>
    )
}

export default User;
