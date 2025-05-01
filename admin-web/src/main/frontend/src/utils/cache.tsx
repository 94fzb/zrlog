// @ts-ignore
import { ModeOfOperation } from "aes-js";
import { isPWA } from "./env-utils";
import { removeQueryParam } from "./helpers";
import { cacheIgnoreReloadKeys } from "./constants";
import * as H from "history";
import { getSsDate, ssKeyStorageKey } from "../base/SsData";

// AES加密函数
function aesEncrypt(text: string, key: string): string {
    // 将输入的字符串转换为 UTF-8 编码的字节数组
    const textBytes = new TextEncoder().encode(text);
    // 将密钥转换为 UTF-8 编码的字节数组，并确保长度为 16、24 或 32 字节
    const keyBytes = new TextEncoder().encode(key);
    const paddedKey = new Uint8Array(32);
    paddedKey.set(keyBytes.subarray(0, 32));

    // 使用 AES-CTR 模式进行加密
    const aesCtr = new ModeOfOperation.ctr(paddedKey);
    const encryptedBytes = aesCtr.encrypt(textBytes);

    // 将加密后的字节数组转换为 Base64 编码的字符串
    return uint8ArrayToBase64(encryptedBytes);
}

// 将 Uint8Array 转换为 Base64 编码的字符串
function uint8ArrayToBase64(uint8Array: Uint8Array): string {
    let binary = "";
    const chunkSize = 8192; // 分块大小
    for (let i = 0; i < uint8Array.length; i += chunkSize) {
        const chunk = uint8Array.subarray(i, i + chunkSize);
        // @ts-ignore
        binary += String.fromCharCode.apply(null, chunk);
    }
    return btoa(binary);
}

// AES解密函数
function aesDecrypt(encryptedBase64: string, key: string): string {
    // 将密钥转换为 UTF-8 编码的字节数组，并确保长度为 16、24 或 32 字节
    const keyBytes = new TextEncoder().encode(key);
    const paddedKey = new Uint8Array(32);
    paddedKey.set(keyBytes.subarray(0, 32));

    // 将 Base64 编码的字符串转换为加密后的字节数组
    const encryptedBytes = base64ToUint8Array(encryptedBase64);

    // 使用 AES-CTR 模式进行解密
    const aesCtr = new ModeOfOperation.ctr(paddedKey);
    const decryptedBytes = aesCtr.decrypt(encryptedBytes);

    // 将解密后的字节数组转换为 UTF-8 编码的字符串
    return new TextDecoder().decode(decryptedBytes);
}

// 将 Base64 编码的字符串转换为 Uint8Array
function base64ToUint8Array(base64: string): Uint8Array {
    const binaryString = atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
        bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes;
}

const getKey = () => {
    return getSsDate() && getSsDate().key ? getSsDate().key : "__DEV__DEV__DEV_";
};

const getCacheKey = () => {
    return window.location.host + "_encrypt_page_data";
};

function padStringToLength(str: string, length: number, paddingChar: string = " "): string {
    if (str.length >= length) {
        return str.substring(0, length);
    } else {
        const paddingLength = length - str.length;
        const padding = paddingChar.repeat(paddingLength);
        return str + padding;
    }
}

export const removePageCacheByLocation = (location: H.Location) => {
    removeCacheDataByKey(getPageDataCacheKey(location));
};

export const getPageDataCacheKey = (location: H.Location) => {
    return getPageDataCacheKeyByPath(location.pathname, location.search);
};

export const getPageBuildId = () => {
    return getSsDate().pageBuildId !== undefined ? (getSsDate().pageBuildId as string as never) : "";
};

export const getPageDataCacheKeyByPath = (pathname: string, search: string) => {
    let realApiKey = pathname.replace(".html", "");
    // / = /index page
    if (realApiKey === "/") {
        realApiKey = "/index";
    }
    return realApiKey + removeQueryParam(search, cacheIgnoreReloadKeys);
};

export const getCachedData = (): Record<string, any> => {
    const tempData = localStorage.getItem(getCacheKey());
    try {
        if (tempData && tempData.length > 0) {
            return JSON.parse(aesDecrypt(tempData, padStringToLength(getKey(), 24)));
        }
    } catch (e) {
        console.error(e);
    }
    return {};
};

export const removeAllCaches = () => {
    try {
        localStorage.removeItem(getCacheKey());
        localStorage.removeItem(ssKeyStorageKey);
    } catch (e) {
        console.error(e);
    }
};

export const putCache = (cache: Record<string, any>) => {
    try {
        //console.info(cache);
        localStorage.setItem(getCacheKey(), aesEncrypt(JSON.stringify(cache), padStringToLength(getKey(), 24)));
    } catch (e) {
        console.error(e);
    }
};

export const addToCache = (key: string, obj: any) => {
    const record = getCachedData();
    record[key] = obj;
    putCache(record);
};

export const getCacheByKey = (key: string) => {
    const record = getCachedData();
    return record[key];
};

export const removeCacheDataByKey = (key: string) => {
    const data: Record<string, any> = getCachedData();
    //console.info("deleted -> " + key + ":" + JSON.stringify(data[key]));
    delete data[key];
    putCache(data);
};

const buildPageFullStateKey = (key: string) => {
    if (isPWA()) {
        return key + "_page_fullScreen_pwa";
    }
    return key + "_page_fullScreen_normal";
};

export const savePageFullState = (key: string, full: boolean) => {
    const record = getCachedData();
    record[buildPageFullStateKey(key)] = full;
    putCache(record);
};

export const getPageFullState = (key: string): boolean => {
    const record = getCachedData();
    return record[buildPageFullStateKey(key)] === true;
};

// Function to save the last opened page to cache
export const saveLastOpenedPage = (url: string): void => {
    const record = getCachedData();
    record["lastOpenedPage"] = url;
    putCache(record);
};

// Function to get the last opened page from cache
export const getLastOpenedPage = (): string | null => {
    return getCachedData()["lastOpenedPage"];
};
