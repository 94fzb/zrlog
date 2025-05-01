export default {
    async fetch(request, env) {
        const url = new URL(request.url);
        const method = request.method;

        if(url.pathname === "/_region") {
            const region = request.cf.region || request.cf.city || "unknown";
            return new Response(`Worker running in region: ${region}`);
        }

        if (method === 'POST' && url.pathname === "/" + env.DB_NAME) {
            const authHeader = request.headers.get('Authorization');

            if (!authHeader || !this.isValidApiKey(authHeader,env)) {
                return new Response("Unauthorized", { status: 401 });
            }

            let body;
            try {
                body = await request.json();
            } catch {
                return new Response("Invalid JSON body", { status: 400 });
            }

            const { sql, params } = body;

            if (!sql) {
                return new Response("Missing 'sql' in request body", { status: 400 });
            }

            // 如果没有传 params，默认空数组
            const queryParams = Array.isArray(params) ? params : [];

            return await this.executeSql(env.DB, sql, queryParams);
        }

        return new Response('Not Found', { status: 404 });
    },

    isValidApiKey(authHeader,env) {
        const validApiKey = `${env.DB_USER}:${env.DB_PASSWORD}`;
        return authHeader === `Bearer ${validApiKey}`;
    },

    async executeSql(db, query, params) {
        try {
            // 用 prepare 预编译，绑定参数后执行
            const stmt = await db.prepare(query);
            const result = await stmt.bind(...params).all();

            return new Response(JSON.stringify(result), {
                status: 200,
                headers: { "Content-Type": "application/json" },
            });
        } catch (err) {
            return new Response(`Error: ${err.message}`, { status: 500 });
        }
    },
};