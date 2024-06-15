import Result, { ExceptionStatusType } from "antd/es/result";
import { CSSProperties } from "react";

const UnknownErrorPage = ({
    code,
    data,
    style,
}: {
    code: ExceptionStatusType;
    data: Record<string, any>;
    style?: CSSProperties;
}) => {
    return <Result status={code} title={code} subTitle={data["message"]} style={style} />;
};

export default UnknownErrorPage;
