import axios from "axios";

export const getCsrData = async (uri: string) => {
    const ssrData = document.getElementById("__SSR_DATE__")?.innerText;
    if(ssrData){
        document.getElementById("__SSR_DATE__")?.remove();
        return JSON.parse(ssrData).data;
    }

    const {data} = await axios.get("/api/admin" + uri);
    return data.data;
}