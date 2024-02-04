import axios from "axios";
import {IndexData} from "./type";

const getStatisticsInfo = async () => await axios.get("/api/admin/statisticsInfo");
const getServerInfo = async () => await axios.get("/api/admin/serverInfo");

export const getCsrData = async (uri: string) => {

    console.info("uri=" + uri);
    if (uri === "" || uri === "index") {
        const [serverResponse, statisticsResponse] = await Promise.all([getServerInfo(), getStatisticsInfo()]);
        return {statisticsInfo: statisticsResponse.data.data, serverInfo: serverResponse.data.data} as IndexData;
    }
}