import {FunctionComponent, lazy, useEffect, useState} from "react";
import {useAxiosBaseInstance} from "../AppBase";
import {BasicUserInfo} from "../type";
import {ssData} from "../index";
import {Spin} from "antd";

type AdminDashBroadPageProps = {
    offline: boolean,
}

const AsyncAdminDashboardRouter = lazy(() => import("components/admin-dashboard-router"));


const AdminDashboardPage: FunctionComponent<AdminDashBroadPageProps> = ({offline}) => {

    const axiosBaseInstance = useAxiosBaseInstance();

    const [userInfo, setUserInfo] = useState<BasicUserInfo | undefined>(ssData?.user);

    useEffect(() => {
        if (userInfo === undefined) {
            axiosBaseInstance.get(`/api/admin/user?t=${new Date().getTime()}`).then(({data}) => {
                if (ssData) {
                    if (data.data.key) {
                        ssData.key = data.data.key
                    }
                    ssData.user = data.data
                }
                setUserInfo(data.data);
            });
        }
    }, []);


    if (userInfo === undefined || userInfo === null) {
        return <Spin fullscreen={true} delay={1000}/>
    }

    return <AsyncAdminDashboardRouter offline={offline} userInfo={userInfo}/>
}

export default AdminDashboardPage;