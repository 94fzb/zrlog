import Result, { ExceptionStatusType } from "antd/es/result";

const UnknownErrorPage = ({ code, data }: { code: ExceptionStatusType; data: Record<string, any> }) => {
    return <Result status={code} title={code} subTitle={data["message"]} />;
};

export default UnknownErrorPage;
