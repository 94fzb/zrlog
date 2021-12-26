const iframe = '<iframe width="100%" style="border: 0" height="1200px" src={document.baseURI + "/admin/plugins/"}>';

function Iframe(props: any) {
    return (<div dangerouslySetInnerHTML={{__html: props.iframe ? props.iframe : ""}}/>);
}

const Plugin = () => {

    return (
        <Iframe iframe={iframe}/>
    )
}

export default Plugin;