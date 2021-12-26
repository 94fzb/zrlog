import {Tabs} from "antd";
import Title from "antd/lib/typography/Title";
import Row from "antd/es/grid/row";
import Col from "antd/es/grid/col";
import Divider from "antd/es/divider";
import Index from "../template";
import Constants, {getRes} from "../../utils/constants";
import BlogForm from "./BlogForm";
import BasicForm from "./BasicForm";
import OtherForm from "./OtherForm";
import UpgradeSettingForm from "./UpgradeSettingForm";


const {TabPane} = Tabs;

const activeKey = window.location.hash !== '' ? window.location.hash.substr(1) : "basic";

const WebSite = () => {

    const handleTabClick = (key: string) => {
        Constants.getHistory().push(`./website#${key}`)
    }

    return (
        <>
            <Title className='page-header' level={3}>{getRes()['admin.setting']}</Title>
            <Divider/>
            <Tabs defaultActiveKey={activeKey} onChange={e => handleTabClick(e)}>
                <TabPane tab="基本信息" key="basic">
                    <Row>
                        <Col md={12} xs={24}>
                            <BasicForm/>
                        </Col>
                    </Row>
                </TabPane>
                <TabPane tab="博客设置" key="blog">
                    <Row>
                        <Col md={12} xs={24}>
                            <BlogForm/>
                        </Col>
                    </Row>
                </TabPane>
                <TabPane tab={getRes()['admin.template.manage']} key="template">
                    <Index/>
                </TabPane>
                <TabPane tab="其他设置" key="other">
                    <Row>
                        <Col md={12} xs={24}>
                            <OtherForm/>
                        </Col>
                    </Row>
                </TabPane>
                <TabPane tab={getRes()['admin.upgrade.manage']} key="upgrade">
                    <UpgradeSettingForm/>
                </TabPane>
            </Tabs>
        </>
    )
}

export default WebSite;
