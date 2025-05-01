export default {
    async fetch(request, env, ctx) {
        const origin = request.headers.get("Origin") || "*";
        const isPreflight = request.method === "OPTIONS";

        // 通用 CORS headers（动态来源）
        const corsHeaders = {
            "Access-Control-Allow-Origin": origin,
            "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
            "Access-Control-Allow-Headers": "Content-Type, Authorization",
            "Access-Control-Allow-Credentials": "true"
        };

        // 处理预检请求（OPTIONS）
        if (isPreflight) {
            return new Response(null, {
                status: 204,
                headers: corsHeaders
            });
        }

        const lambdaUrl = "https://ii5uzqmg7ge56vhom6nxvh3giq0wldws.lambda-url.us-west-1.on.aws/";
        const url = new URL(request.url);
        const newUrl = lambdaUrl + url.pathname + url.search;

        const response = await fetch(newUrl, {
            method: request.method,
            headers: request.headers,
            body: request.body
        });

        return new Response(response.body, {
            status: response.status,
            headers: response.headers
        });
    }
};