import {FunctionComponent, lazy, useEffect, useState} from "react";
import {useAxiosBaseInstance} from "../AppBase";
import {BasicUserInfo} from "../type";
import {ssData} from "../index";
import {Spin} from "antd";
import {getCacheByKey} from "../cache";

type AdminDashBroadPageProps = {
    offline: boolean,
}

const AsyncAdminDashboardRouter = lazy(() => import("components/admin-dashboard-router"));


const AdminDashboardPage: FunctionComponent<AdminDashBroadPageProps> = ({offline}) => {

    const axiosBaseInstance = useAxiosBaseInstance();

    let initUserInfo = ssData?.user;

    if (offline) {
        initUserInfo = getCacheByKey("/user");
    }

    const [userInfo, setUserInfo] = useState<BasicUserInfo | undefined>(initUserInfo);

    const needFetch = userInfo === undefined || userInfo === null;

    useEffect(() => {
        if (needFetch) {
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