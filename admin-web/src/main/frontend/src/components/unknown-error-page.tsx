import Result from "antd/es/result";
import Button from "antd/es/button";

const UnknownErrorPage = ({message}: { message: string }) => {

    return (
        <Result
            status="500"
            title="500"
            subTitle={message}
            extra={<Button type="primary" href={window.location.href}>重新加载</Button>}
        />
    )
}

export default UnknownErrorPage;