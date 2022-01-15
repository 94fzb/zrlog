import {useEffect, useState} from "react";
import axios from "axios";

const TemplateCenter = () => {

    const [url, setUrl] = useState<string>("");

    useEffect(() => {
        axios.get("/api/admin/template/downloadUrl").then(({data}) => {
            setUrl(data.data.url)
        })
    }, [])

    if (url === "") {
        return (<div/>);
    }

    return (
        <iframe width="100%" style={{border: 0}} height={1200} src={url}/>
    )
}

export default TemplateCenter;
