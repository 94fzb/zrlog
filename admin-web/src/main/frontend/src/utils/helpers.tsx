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

function isObject(object: any): boolean {
    return object != null && typeof object === "object";
}
