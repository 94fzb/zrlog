import axios from "axios";
import {ssData} from "./index";

let ssRendered :boolean = false;

export const getCsrData = async (uri: string) => {
    // 仅客户端渲染时，第一次渲染时生效
    if(!ssRendered && ssData && ssData.pageData){
        ssRendered = true;
        return ssData.pageData;
    }
    const {data} = await axios.get("/api/admin" + uri);
    return data.data;
}