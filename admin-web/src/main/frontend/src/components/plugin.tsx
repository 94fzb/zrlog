const Plugin = () => {
    const pluginUrl = document.baseURI + "admin/plugins/"
    return (
        <iframe width="100%" style={{border: 0}} height={1200} src={pluginUrl}/>
    )
}

export default Plugin;