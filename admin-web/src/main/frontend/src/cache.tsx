// @ts-ignore
import { ModeOfOperation } from "aes-js";
import { ssData } from "./index";

// AES加密函数
function aesEncrypt(text: string, key: string): string {
    // 将输入的字符串转换为 UTF-8 编码的字节数组
    const textBytes = new TextEncoder().encode(text);
    // 将密钥转换为 UTF-8 编码的字节数组
    const keyBytes = new TextEncoder().encode(key);
    // 使用 AES-CTR 模式进行加密
    const aesCtr = new ModeOfOperation.ctr(keyBytes);
    const encryptedBytes = aesCtr.encrypt(textBytes);
    // 将加密后的字节数组转换为 Base64 编码的字符串
    const encryptedBase64 = btoa(String.fromCharCode.apply(null, encryptedBytes));
    return encryptedBase64;
}

// AES解密函数
function aesDecrypt(encryptedBase64: string, key: string): string {
    // 将密文的 Base64 编码的字符串转换为字节数组
    const encryptedBytes = new Uint8Array(
        atob(encryptedBase64)
            .split("")
            .map((char) => char.charCodeAt(0))
    );
    // 将密钥转换为 UTF-8 编码的字节数组
    const keyBytes = new TextEncoder().encode(key);
    // 使用 AES-CTR 模式进行解密
    const aesCtr = new ModeOfOperation.ctr(keyBytes);
    const decryptedBytes = aesCtr.decrypt(encryptedBytes);
    // 将解密后的字节数组转换为 UTF-8 编码的字符串
    const decryptedText = new TextDecoder().decode(decryptedBytes);
    return decryptedText;
}

const getKey = () => {
    const prod = ssData && ssData.key;
    return prod && ssData && ssData.key ? ssData.key : "__DEV__DEV__DEV_";
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

export const putCache = (cache: Record<string, any>) => {
    try {
        localStorage.setItem(getCacheKey(), aesEncrypt(JSON.stringify(cache), padStringToLength(getKey(), 24)));
    } catch (e) {
        console.error(e);
    }
};

export const deleteCacheDataByKey = (key: string) => {
    const data: Record<string, any> = getCachedData();
    delete data[key];
    console.info(data);
    putCache(data);
};
