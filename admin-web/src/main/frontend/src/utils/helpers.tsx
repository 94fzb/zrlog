import * as H from "history";
import React from "react";
import { ExclamationCircleOutlined } from "@ant-design/icons";
import { HookAPI } from "antd/es/modal/useModal";

export const mapToQueryString = (map: Record<string, string[] | string | boolean | number | undefined>): string => {
    return Object.keys(map)
        .reduce(function (a, k) {
            if (map[k] === undefined) {
                // @ts-ignore
                a.push(`${k}=`);
            } else {
                // @ts-ignore
                a.push(k + "=" + encodeURIComponent(map[k]));
            }
            return a;
        }, [])
        .join("&");
};

export const parseQueryParamsToMap = (queryString: string): Map<string, string> => {
    // 创建 URLSearchParams 对象
    const params = new URLSearchParams(queryString);
    // 将 URLSearchParams 转换为 Map
    const map = new Map<string, string>();

    // 使用 forEach 遍历每个键值对
    params.forEach((value, key) => {
        map.set(key, value);
    });
    return map;
};

function sortKeysWithSpecialHandling(obj: any): any {
    if (obj === null || typeof obj !== "object") {
        return obj;
    }

    if (obj instanceof Date) {
        return obj.toISOString(); // 转换 Date 为字符串
    }

    if (obj instanceof RegExp) {
        return obj.toString(); // 转换 RegExp 为字符串
    }

    if (Array.isArray(obj)) {
        return obj.map(sortKeysWithSpecialHandling);
    }

    const sortedObj: any = {};
    Object.keys(obj)
        .sort()
        .forEach((key) => {
            sortedObj[key] = sortKeysWithSpecialHandling(obj[key]);
        });

    return sortedObj;
}

export function deepEqualWithSpecialJSON(obj1: any, obj2: any): boolean {
    const sortedObj1 = sortKeysWithSpecialHandling(obj1);
    const sortedObj2 = sortKeysWithSpecialHandling(obj2);

    return JSON.stringify(sortedObj1) === JSON.stringify(sortedObj2);
}

export function removeQueryParam(search: string, key: string) {
    // 检查是否以 '?' 开头，并处理查询字符串
    const hasQuestionMark = search.startsWith("?");
    const params = new URLSearchParams(hasQuestionMark ? search.substring(1) : search);

    key.split(",").map((k: string) => {
        // 删除特定的查询参数
        params.delete(k);
    });
    // 构建新的查询字符串
    const newSearch = params.toString();

    // 返回结果，根据原始输入决定是否添加 '?'
    return newSearch ? (hasQuestionMark ? `?${newSearch}` : newSearch) : "";
}

export const getFullPath = (location: H.Location) => {
    if (location.search.length <= 0) {
        return location.pathname;
    }
    return location.pathname + location.search;
};

export const getExitTips = () => {
    //@ts-ignore
    return window.onbeforeunloadTips;
};

export const enableExitTips = (str: string) => {
    //@ts-ignore
    window.onbeforeunloadTips = str;
    window.onbeforeunload = function () {
        return str;
    };
};

export const disableExitTips = () => {
    window.onbeforeunload = null;
    //@ts-ignore
    window.onbeforeunloadTips = null;
};

export const tryBlock = (e: React.MouseEvent, modal: HookAPI) => {
    if (window.onbeforeunload !== null) {
        modal.warning({
            title: "提示",
            icon: <ExclamationCircleOutlined />,
            content: getExitTips(),
        });
        e.preventDefault();
    }
};

export const colorPickerBgColors = [
    "rgb(22, 119, 255)",
    "rgb(114, 46, 209)",
    "rgb(19, 194, 194)",
    "rgb(82, 196, 26)",
    "rgb(235, 47, 150)",
    "rgb(245, 34, 45)",
    "rgb(250, 140, 22)",
    "rgb(250, 219, 20)",
    "rgb(250, 84, 28)",
    "rgb(47, 84, 235)",
    "rgb(250, 173, 20)",
    "rgb(160, 217, 17)",
    "rgb(0, 0, 0)",
];

export const getContextPath = () => {
    return new URL(document.baseURI).pathname;
};
