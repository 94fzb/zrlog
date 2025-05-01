import {getCacheByKey} from "../utils/cache";
import {BasicUserInfo} from "../type";
import {FunctionComponent, PropsWithChildren} from "react";

type SsDate = {
    data?: any;
    resourceInfo?: Record<string, never>;
    user: BasicUserInfo | null;
    key: string;
    pageBuildId: string;
};

export const ssKeyStorageKey = "ss_key";

export const getSsDate = (): SsDate => {
    //@ts-ignore
    return window['__SS_DATA__'];
}

const SsData: FunctionComponent<PropsWithChildren> = ({children}) => {

    const initSsData = (): SsDate => {
        const ssDataStr = document.getElementById("__SS_DATA__")?.innerText;
        let tSData;
        // @ts-ignore
        if (ssDataStr?.length > 0) {
            tSData = JSON.parse(ssDataStr as string) as SsDate;
        } else {
            tSData = {key: "", data: undefined, resourceInfo: {}, user: null, pageBuildId: ""}
        }

        if (tSData.key === "" || tSData.key === null || tSData.key === undefined) {
            const ssKey = localStorage.getItem(ssKeyStorageKey);
            if (ssKey) {
                tSData.key = ssKey;
            }
        }
        return tSData;
    }
    //@ts-ignore
    window['__SS_DATA__'] = initSsData();

    const ssData = getSsDate();

    if (ssData.user === undefined || ssData.user === null) {
        if (ssData.key !== "") {
            ssData.user = getCacheByKey("/user");
        }
    }

    return <>
        {children}
    </>

}

export default SsData