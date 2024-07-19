import * as H from "history";

export const mapToQueryString = (map: Record<string, string | boolean | number | undefined>): string => {
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

export function deepEqual(obj1: any, obj2: any): boolean {
    if (obj1 === obj2) {
        return true;
    }

    if (typeof obj1 !== "object" || obj1 === null || typeof obj2 !== "object" || obj2 === null) {
        return false;
    }

    const keys1 = Object.keys(obj1);
    const keys2 = Object.keys(obj2);

    // 检查键的数量是否相同
    if (keys1.length !== keys2.length) {
        return false;
    }

    for (const key of keys1) {
        const val1 = obj1[key];
        const val2 = obj2[key];
        const areObjects = isObject(val1) && isObject(val2);

        if ((areObjects && !deepEqual(val1, val2)) || (!areObjects && val1 !== val2)) {
            return false;
        }
    }

    return true;
}

export function removeQueryParam(search: string, key: string) {
    // 检查是否以 '?' 开头，并处理查询字符串
    const hasQuestionMark = search.startsWith("?");
    const params = new URLSearchParams(hasQuestionMark ? search.substring(1) : search);

    // 删除特定的查询参数
    params.delete(key);

    // 构建新的查询字符串
    const newSearch = params.toString();

    // 返回结果，根据原始输入决定是否添加 '?'
    return newSearch ? (hasQuestionMark ? `?${newSearch}` : newSearch) : "";
}

function isObject(object: any): boolean {
    return object != null && typeof object === "object";
}

export const getFullPath = (location: H.Location) => {
    if (location.search.length <= 0) {
        return location.pathname;
    }
    return location.pathname + location.search;
};
