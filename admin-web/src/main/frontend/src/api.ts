import {AxiosInstance} from "axios";
import {getRes, setRes} from "./utils/constants";

export const getCsrData = async (uri: string, axiosInstance: AxiosInstance) => {
    const {data} = await axiosInstance.get("/api/admin" + uri.replace(".html", ""));
    if (data.pageBuildId !== undefined) {
        const res = getRes();
        if (res) {
            res['pageBuildId'] = data.pageBuildId as string as never;
            setRes(res);
        }
    }
    return data;
}