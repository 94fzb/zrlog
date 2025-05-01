import Divider from "antd/es/divider";
import Title from "antd/es/typography/Title";

const BaseTitle = ({title}: { title: string }) => {

    return <>
        <Title className="page-header" level={3}>
            {title}
        </Title>
        <Divider/>
    </>
}

export default BaseTitle;