import React from "react";
import {BaseResourceComponent} from "../base-resource-component";
import axios from "axios";


function Iframe(props) {
    return (<div dangerouslySetInnerHTML={{__html: props.iframe ? props.iframe : ""}}/>);
}

class TemplateCenter extends BaseResourceComponent {

    getSecondTitle() {
        return this.state.res['templateCenter'];
    }

    componentDidMount() {
        axios.get("/api/admin/template/downloadUrl").then(({data}) => {
            this.setState({
                url: data.data.url
            })
        })
    }

    render() {
        if(!this.state.url){
	   return (<div/>);
	}
        // eslint-disable-next-line no-template-curly-in-string
        const iframe = '<iframe width="100%" style="border: 0" height="1200px" src=' + this.state.url + '>';

        return (
            <Iframe iframe={iframe}/>
        )
    }
}

export default TemplateCenter;
