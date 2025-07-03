const CACHE_NAME = "my-cache-v6";
const urlsToCache = [];
self.addEventListener("install", (event) => {
    self.skipWaiting();
    event.waitUntil(caches.open(CACHE_NAME).then((cache) => cache.addAll(urlsToCache)));
});

self.addEventListener("activate", (event) => {
    const cacheWhitelist = [CACHE_NAME];
    event.waitUntil(
        caches
            .keys()
            .then((cacheNames) => {
                return Promise.all(
                    cacheNames.map((cacheName) => {
                        if (cacheWhitelist.indexOf(cacheName) === -1) {
                            return caches.delete(cacheName);
                        }
                    })
                );
            })
            .then(() => {
                // 🚨 关键：确保 Service Worker 立即接管所有客户端（包括当前页面）
                console.log("[SW] Service Worker 已激活并接管控制权。");
                return self.clients.claim();
            })
    );
});

self.addEventListener("fetch", (event) => {
    const request = event.request;
    // 跳过不支持的协议
    if (!request.url.startsWith("http")) {
        return;
    }
    if (request.method !== "GET") {
        return;
    }
    // ✅ 不缓存 /api 接口，直接走网络
    if (new URL(request.url).pathname.startsWith("/api")) {
        return;
    }

    if (new URL(request.url).pathname.endsWith("/admin/logout")) {
        return;
    }

    if (new URL(request.url).pathname.includes("/admin/plugins/")) {
        return;
    }

    function normalizedRequest() {
        const url = new URL(request.url);

        if (url.pathname.endsWith("/index.html")) {
            url.pathname = url.pathname.replace(/index\.html$/, ""); // 去掉末尾的 index.html
            return new Request(url.toString(), {
                method: "GET",
                headers: request.headers,
                credentials: request.credentials,
                redirect: "follow",
            });
        }

        return request;
    }

    event.respondWith(
        caches.open(CACHE_NAME).then(async (cache) => {
            const newRequest = normalizedRequest();

            const cachedResponse = await cache.match(newRequest);

            // 触发后台更新
            const fetchPromise = fetch(newRequest, {
                redirect: "follow",
            })
                .then((networkResponse) => {
                    // 只有有效响应才更新缓存
                    if (networkResponse && networkResponse.status === 200) {
                        cache.put(request, networkResponse.clone());
                    }
                    return new Response(networkResponse.body, {
                        status: networkResponse.status,
                        statusText: networkResponse.statusText,
                        headers: networkResponse.headers,
                    });
                })
                .catch(() => {
                    // 网络失败也不抛错
                    return null;
                });

            // 返回缓存内容（如果有），否则等网络返回

            if (cachedResponse) {
                // 当从缓存读取到响应并准备返回给浏览器时
                return new Response(cachedResponse.body, {
                    status: cachedResponse.status,
                    statusText: cachedResponse.statusText,
                    headers: cachedResponse.headers,
                });
            }
            return fetchPromise
                .then(async (response) => {
                    if (response) {
                        return response;
                    }
                    return caches.match("/admin/offline");
                })
                .then((response) => {
                    if (response) {
                        return new Response(response.body, {
                            status: response.status,
                            statusText: response.statusText,
                            headers: response.headers,
                        });
                    }
                });
        })
    );
});
