export const mapToQueryString = (map: Record<string, string | boolean | number | undefined>): string => {
    return Object.keys(map).reduce(function (a, k) {
        if (map[k] === undefined) {
            // @ts-ignore
            a.push(`${k}=`);
        } else {
            // @ts-ignore
            a.push(k + '=' + encodeURIComponent(map[k]));
        }
        return a;
    }, []).join('&');
}

export const reloadTitle = (secondTitle: string, res: Record<string, never>) => {
    document.title = [secondTitle, res['admin.management'], res.websiteTitle].filter(Boolean).join(" | ");
}