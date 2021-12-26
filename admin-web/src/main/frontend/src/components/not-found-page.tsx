import Result from "antd/es/result";
import Button from "antd/es/button";
import {Link} from "react-router-dom";
import {getRes} from "../utils/constants";

const NotFoundPage = () => {

    return (
        <Result
            status="404"
            title="404"
            subTitle={getRes().notFound}
            extra={<Button type="primary"><Link target='_top' to='./index'>Back Home</Link></Button>}
        />
    )
}

export default NotFoundPage;