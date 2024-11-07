import { FullscreenExitOutlined, FullscreenOutlined } from "@ant-design/icons";
import EnvUtils, { isPWA } from "../../utils/env-utils";
import { Button } from "antd";
import screenfull from "screenfull";
import { FunctionComponent, useEffect, useState } from "react";
import { FullScreenProps } from "./index.types";
import { getPageFullState, savePageFullState } from "../../cache";
import { getFullPath } from "../../utils/helpers";
import { useLocation } from "react-router";

type ArticleEditFullscreenButton = FullScreenProps & {
    fullScreenElement: HTMLDivElement;
    editorInstance: { width: (arg0: string) => void };
    onChange: (full: boolean) => void;
};

const ArticleEditFullscreenButton: FunctionComponent<ArticleEditFullscreenButton> = ({
    fullScreenElement,
    onExitFullScreen,
    onFullScreen,
    editorInstance,
    onChange,
}) => {
    const location = useLocation();
    const initFullScreen = getPageFullState(getFullPath(location));
    const [fullScreen, setFullScreen] = useState<boolean>(initFullScreen);

    useEffect(() => {
        if (isPWA()) {
            savePageFullState(getFullPath(location), fullScreen);
        }
    }, [fullScreen]);

    const toggleFullScreen = () => {
        if (fullScreen) {
            onfullscreenExit();
        } else {
            onfullscreen();
        }
    };

    const onfullscreen = () => {
        try {
            if (screenfull.isEnabled) {
                screenfull
                    .request(fullScreenElement)
                    .then(() => {
                        doFullState();
                    })
                    .catch((e) => {
                        console.error(e);
                        doFullState();
                    });
                screenfull.on("change", () => {
                    if (screenfull.isEnabled && !screenfull.isFullscreen) {
                        onfullscreenExit();
                    }
                });
            } else {
                doFullState();
            }
        } catch (e) {
            console.error(e);
            doFullState();
        } finally {
            onFullScreen();
        }
    };

    const doFullState = () => {
        setFullScreen(true);
        onChange(true);
        if (editorInstance) {
            editorInstance.width("100%");
        }
    };

    const onfullscreenExit = () => {
        setFullScreen(false);
        onChange(false);
        if (editorInstance) {
            editorInstance.width("100%");
        }
        onExitFullScreen();
        if (screenfull.isEnabled) {
            screenfull.exit().catch((e) => {
                console.error(e);
            });
        }
    };

    useEffect(() => {
        if (fullScreen && isPWA()) {
            onfullscreen();
        }
    });

    return (
        <Button
            type={"default"}
            icon={
                fullScreen ? (
                    <FullscreenExitOutlined style={{ fontSize: 24 }} />
                ) : (
                    <FullscreenOutlined style={{ fontSize: 24 }} />
                )
            }
            href={
                fullScreen
                    ? window.location.pathname + "#exitFullScreen"
                    : window.location.pathname + "#enterFullScreen"
            }
            style={{
                border: 0,
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                width: 46,
                minWidth: 46,
                borderRadius: 8,
                height: 46,
                fontSize: 24,
                cursor: "pointer",
                color: "rgb(102, 102, 102)",
                background: EnvUtils.isDarkMode() ? "#141414" : "white",
            }}
            onClick={(e) => {
                e.stopPropagation();
                e.preventDefault();
                toggleFullScreen();
            }}
        ></Button>
    );
};

export default ArticleEditFullscreenButton;
