import Title from "antd/lib/typography/Title";
import Divider from "antd/es/divider";
import Form from "antd/es/form";
import TextArea from "antd/es/input/TextArea";
import Button from "antd/es/button";
import {getRes, resourceKey} from "../../utils/constants";
import {useEffect, useState} from "react";
import axios from "axios";
import {message} from "antd";

const layout = {
    labelCol: {span: 8},
    wrapperCol: {span: 16},
};


const OtherForm = () => {

    const [form, setForm] = useState<any>(undefined)

    const websiteFormFinish = (changedValues: any) => {
        axios.post("/api/admin/website/other", changedValues).then(({data}) => {
            if (!data.error) {
                message.info(data.message).then(() => {
                    sessionStorage.removeItem(resourceKey);
                    window.location.reload();
                });

            }
        });
    }

    useEffect(() => {
        axios.get("/api/admin/website/settings").then(({data}) => {
            if (data.data.basic != null) {
                setForm(data.data.other);
            }
        })
    }, [])

    if (form === undefined) {
        return <></>
    }

    return (
        <>
            <Title level={4}>ICP，网站统计等信息</Title>
            <Divider/>
            <Form {...layout}
                  initialValues={form}
                  onValuesChange={(_k, v) => setForm(v)}
                  onFinish={k => websiteFormFinish(k)}>
                <Form.Item name='icp' label='ICP备案信息'>
                    <TextArea/>
                </Form.Item>
                <Form.Item name='webCm' label='网站统计'>
                    <TextArea rows={7}/>
                </Form.Item>
                <Divider/>
                <Button type='primary'
                        htmlType='submit'>{getRes().submit}</Button>
            </Form>
        </>
    )
}

export default OtherForm