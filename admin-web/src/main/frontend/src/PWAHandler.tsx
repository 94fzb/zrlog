import React, { PropsWithChildren, useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import * as H from "history";

// Function to check if the page is running as a PWA
const isPWA = (): boolean => {
    //@ts-ignore
    if (window.navigator.standalone) {
        return true;
    }
    return window.matchMedia("(display-mode: standalone)").matches;
};

// Function to save the last opened page to sessionStorage and localStorage
const saveLastOpenedPage = (url: string): void => {
    localStorage.setItem("lastOpenedPage", url);
};

// Function to get the last opened page from sessionStorage or localStorage
const getLastOpenedPage = (): string | null => {
    return localStorage.getItem("lastOpenedPage");
};

const getFullPath = (location: H.Location) => {
    if (location.search.length <= 0) {
        return location.pathname;
    }
    return location.pathname + location.search;
};

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
        if (lastOpenedPage && lastOpenedPage !== getFullPath(location)) {
            console.log("Redirecting to last opened page:", lastOpenedPage);
            saveLastOpenedPage(lastOpenedPage);
            navigate(lastOpenedPage);
            sessionStorage.setItem("loaded", "true");
        }
        setLoaded(true);
    }, []); // Empty dependency array to run only on mount

    // Effect to save the current page on location change
    useEffect(() => {
        if (isPWA()) {
            const fullPath = getFullPath(location);
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
        }
    }, [location]); // Run on location change

    if (!loaded) {
        return <></>;
    }

    return <>{children}</>;
};

export default PWAHandler;
