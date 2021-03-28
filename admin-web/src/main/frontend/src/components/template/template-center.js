import React from "react";
import {BaseResourceComponent} from "../base-resource-component";


function Iframe(props) {
    return (<div dangerouslySetInnerHTML={{__html: props.iframe ? props.iframe : ""}}/>);
}

class TemplateCenter extends BaseResourceComponent {

    getSecondTitle() {
        return this.state.res['templateCenter'];
    }

    render() {
        const downloadUrl = this.state.res.templateDownloadFromUrl;
        // eslint-disable-next-line no-template-curly-in-string
        const iframe = '<iframe width="100%" style="border: 0" height="1200px" src=' + downloadUrl + '>';

        return (
            <Iframe iframe={iframe}/>
        )
    }
}

export default TemplateCenter;