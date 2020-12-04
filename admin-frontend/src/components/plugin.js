import React from "react";
import {BaseResourceComponent} from "./base-resource-component";

const iframe = '<iframe width="100%" style="border: 0" height="1200px" src="/admin/plugins/">';

function Iframe(props) {
    return (<div dangerouslySetInnerHTML={{__html: props.iframe ? props.iframe : ""}}/>);
}

class Plugin extends BaseResourceComponent {

    getSecondTitle() {
        return this.state.res['admin.plugin.manage'];
    }

    render() {
        return (
            <Iframe iframe={iframe}/>
        )
    }
}

export default Plugin;