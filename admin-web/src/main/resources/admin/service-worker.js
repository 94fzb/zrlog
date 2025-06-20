const CACHE_NAME = 'my-cache-v6';
const urlsToCache = [
    '___FILES___'
];
self.addEventListener('install', event => {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => cache.addAll(urlsToCache))
    );
});

self.addEventListener('fetch', event => {
    const request = event.request;
    // 跳过不支持的协议
    if (!request.url.startsWith('http')) {
        return;
    }
    if (request.method !== "GET") {
        return;
    }
    // ✅ 不缓存 /api 接口，直接走网络
    if (new URL(request.url).pathname.startsWith('/api')) {
        return;
    }

    event.respondWith(
        caches.open(CACHE_NAME).then(async cache => {
            const cachedResponse = await cache.match(request);

            // 触发后台更新
            const fetchPromise = fetch(request, {
                redirect: 'follow',
            }).then(networkResponse => {
                // 只有有效响应才更新缓存
                if (networkResponse && networkResponse.status === 200) {
                    cache.put(request, networkResponse.clone());
                }
                return networkResponse;
            }).catch(() => {
                // 网络失败也不抛错
                return null;
            });

            // 返回缓存内容（如果有），否则等网络返回
            return cachedResponse || fetchPromise.then(response => {
                return response || caches.match('/admin/offline');
            });
        })
    );
});

self.addEventListener('activate', event => {
    const cacheWhitelist = [CACHE_NAME];
    event.waitUntil(
        caches.keys().then(cacheNames => {
            return Promise.all(
                cacheNames.map(cacheName => {
                    if (cacheWhitelist.indexOf(cacheName) === -1) {
                        return caches.delete(cacheName);
                    }
                })
            );
        })
    );
});