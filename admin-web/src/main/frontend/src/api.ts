import axios from "axios";
import {ssData} from "./index";

let ssPageData:undefined;

export const getCsrData = async (uri: string) => {
    // @ts-ignore
    if(ssPageData === undefined && ssData && ssData.pageData){
        ssPageData = ssData.pageData;
        return ssData.pageData;
    }
    const {data} = await axios.get("/api/admin" + uri);
    return data.data;
}