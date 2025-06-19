import {AxiosInstance} from "axios";

export const getCsrData = async (uri: string, axiosInstance: AxiosInstance) => {
    const {data} = await axiosInstance.get("/api/admin" + uri.replace(".html", ""));
    return data;
}