import {FunctionComponent, lazy, useEffect, useState} from "react";
import {jumpToLoginPage, useAxiosBaseInstance} from "../AppBase";
import {BasicUserInfo} from "../type";
import {ssData} from "../index";
import {Spin} from "antd";
import {addToCache} from "../cache";
import {getCsrData} from "../api";
import {useNavigate} from "react-router-dom";

type AdminDashBroadPageProps = {
    offline: boolean,
}

const AsyncAdminDashboardRouter = lazy(() => import("components/admin-dashboard-router"));


const AdminDashboardPage: FunctionComponent<AdminDashBroadPageProps> = ({offline}) => {

    const axiosBaseInstance = useAxiosBaseInstance();

    const navigate = useNavigate();

    const [userInfo, setUserInfo] = useState<BasicUserInfo | undefined>(ssData?.user);

    useEffect(() => {
        if (offline) {
            return;
        }
        getCsrData(`/user?_t=${new Date().getTime()}`, axiosBaseInstance).then(({data}) => {
            if (data && data.error === 0) {
                if (ssData) {
                    if (data.key) {
                        ssData.key = data.key
                    }
                    ssData.user = data
                }
                setUserInfo(data);
                addToCache("/user", data);
            } else {
                jumpToLoginPage(navigate)
            }
        });
    }, []);


    if (userInfo === undefined || userInfo === null) {
        return <Spin fullscreen={true} delay={1000}/>
    }

    return <AsyncAdminDashboardRouter offline={offline} userInfo={userInfo}/>
}

export default AdminDashboardPage;