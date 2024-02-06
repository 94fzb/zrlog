import axios from "axios";

export const getCsrData = async (uri: string) => {
    const {data} = await axios.get("/api/admin" + uri);
    return data.data;
}