import {useEffect, useState} from "react";
import axios from "axios";


function Iframe(props: any) {
    return (<div dangerouslySetInnerHTML={{__html: props.iframe ? props.iframe : ""}}/>);
}

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
    const iframe = '<iframe width="100%" style="border: 0" height="1200px" src=' + url + '>';

    return (
        <Iframe iframe={iframe}/>
    )
}

export default TemplateCenter;
