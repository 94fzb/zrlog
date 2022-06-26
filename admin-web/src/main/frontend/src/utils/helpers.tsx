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
