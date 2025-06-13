import Result from "antd/es/result";
import Button from "antd/es/button";
import { useNavigate } from "react-router-dom";
import { getRes } from "../utils/constants";

const NotFoundPage = () => {
    const navigate = useNavigate();

    return (
        <Result
            status="404"
            title="404"
            subTitle={getRes().notFound}
            extra={
                <Button type="primary" onClick={() => navigate(-1)}>
                    {getRes()["goBack"]}
                </Button>
            }
        />
    );
};

export default NotFoundPage;
