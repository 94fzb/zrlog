import Result, { ExceptionStatusType } from "antd/es/result";
import Button from "antd/es/button";

const UnknownErrorPage = ({ code, data }: { code: ExceptionStatusType; data: Record<string, any> }) => {
    return (
        <Result
            status={code}
            title={code}
            subTitle={data["message"]}
            extra={
                <Button type="primary" href={window.location.href}>
                    重新加载
                </Button>
            }
        />
    );
};

export default UnknownErrorPage;
