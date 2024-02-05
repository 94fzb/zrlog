import { LoadingOutlined } from "@ant-design/icons";
import { Spin } from "antd";

const MyLoadingComponent = () => {
    return (
        <Spin
            delay={200}
            style={{ position: "fixed", top: 80, right: 24 }}
            indicator={<LoadingOutlined style={{ fontSize: 24 }} spin />}
        />
    );
};

export default MyLoadingComponent;
