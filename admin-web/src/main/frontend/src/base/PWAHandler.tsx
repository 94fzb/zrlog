import React, { PropsWithChildren, useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { getLastOpenedPage, saveLastOpenedPage } from "../utils/cache";
import { getFullPath } from "../utils/helpers";
import { isPWA } from "../utils/env-utils";

const PWAHandler: React.FC<PropsWithChildren> = ({ children }) => {
    const location = useLocation();
    const navigate = useNavigate();

    const defaultLoaded = sessionStorage.getItem("loaded") === "true" || !isPWA();
    const [loaded, setLoaded] = useState(defaultLoaded);

    // Effect for initial load to redirect to last opened page
    useEffect(() => {
        if (loaded) {
            return;
        }
        const lastOpenedPage = getLastOpenedPage();
        sessionStorage.setItem("loaded", "true");
        if (lastOpenedPage && lastOpenedPage !== getFullPath(location)) {
            console.log(getFullPath(location) + " redirecting to last opened page:", lastOpenedPage);
            navigate(lastOpenedPage);
            return;
        }
    }, []); // Empty dependency array to run only on mount

    // Effect to save the current page on location change
    useEffect(() => {
        if (!isPWA()) {
            return;
        }
        const fullPath = getFullPath(location);
        if (!loaded) {
            const lastOpenedPage = getLastOpenedPage();
            if (lastOpenedPage === undefined || lastOpenedPage === null) {
                setLoaded(true);
            } else if (lastOpenedPage === fullPath) {
                setLoaded(true);
            }
            return;
        }
        if (sessionStorage.getItem("loaded") !== "true") {
            return;
        }
        //console.log("This page is running as a PWA.");
        if (
            fullPath.startsWith("/500") ||
            fullPath.startsWith("/404") ||
            fullPath.startsWith("/403") ||
            fullPath.startsWith("/offline")
        ) {
            return;
        }
        saveLastOpenedPage(fullPath);
    }, [location]); // Run on location change

    if (!loaded) {
        return <></>;
    }

    return <>{children}</>;
};

export default PWAHandler;
