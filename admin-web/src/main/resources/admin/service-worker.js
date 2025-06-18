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
    if(request.method !== "GET") {
        return;
    }

    event.respondWith(
        fetch(request)
            .then(response => {
                // 请求成功，将响应添加到缓存中
                if (response && response.status === 200) {
                    const responseToCache = response.clone();
                    caches.open(CACHE_NAME)
                        .then(cache => cache.put(request, responseToCache));
                }
                return response;
            })
            .catch(() => {
                // 请求失败，尝试从缓存中获取响应
                return caches.match(request)
                    .then(cachedResponse => {
                        if (cachedResponse) {
                            return cachedResponse;
                        }
                        // 如果没有缓存的响应，返回离线页面
                        return caches.match('/admin/offline');
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