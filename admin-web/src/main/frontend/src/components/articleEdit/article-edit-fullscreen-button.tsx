import { FullscreenExitOutlined, FullscreenOutlined } from "@ant-design/icons";
import EnvUtils, { isPWA } from "../../utils/env-utils";
import { Button } from "antd";
import screenfull from "screenfull";
import { FunctionComponent, useEffect } from "react";
import { FullScreenProps } from "./index.types";

type ArticleEditFullscreenButton = FullScreenProps & {
    fullScreenElement: HTMLDivElement;
};

const ArticleEditFullscreenButton: FunctionComponent<ArticleEditFullscreenButton> = ({
    fullScreenElement,
    onExitFullScreen,
    onFullScreen,
    fullScreen,
}) => {
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
                        //ignore
                    })
                    .catch((e) => {
                        console.error(e);
                    });
                screenfull.on("change", () => {
                    if (screenfull.isEnabled && !screenfull.isFullscreen) {
                        onfullscreenExit();
                    }
                });
            }
        } catch (e) {
            console.error(e);
        } finally {
            onFullScreen();
        }
    };

    const onfullscreenExit = () => {
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
            className={"editor-icon"}
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
                width: 47,
                minWidth: 47,
                borderRadius: 8,
                height: 47,
                fontSize: 24,
                cursor: "pointer",
                color: "rgb(102, 102, 102)",
                background: EnvUtils.isDarkMode() ? "#141414" : "white",
            }}
            onClick={(e) => {
                toggleFullScreen();
                e.stopPropagation();
                e.preventDefault();
            }}
        ></Button>
    );
};

export default ArticleEditFullscreenButton;
