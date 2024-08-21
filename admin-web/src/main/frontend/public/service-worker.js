const CACHE_NAME = 'my-cache-v5';
const urlsToCache = [
    '/admin/index',
    '/api/admin/index',
    '/admin/article-edit',
    '/api/admin/article-edit',
    '/admin/article',
    '/admin/article-type',
    '/api/admin/article-type',
    '/api/admin/article',
    '/admin/offline',
    '/api/admin/offline',
    '/admin/website',
    '/api/admin/website',
    '/admin/website/blog',
    '/api/admin/website/blog',
    '/admin/website/admin',
    '/api/admin/website/admin',
    '/admin/website/template',
    '/api/admin/website/template',
    '/admin/website/other',
    '/api/admin/website/other',
    '/admin/website/upgrade',
    '/api/admin/website/upgrade',
    '/admin/template-config?shortTemplate=default',
    '/api/admin/template-config?shortTemplate=default',
    '/admin/type',
    '/api/admin/type',
    '/admin/link',
    '/api/admin/link',
    '/admin/nav',
    '/api/admin/nav',
    '/admin/comment',
    '/api/admin/comment',
    '/admin/user',
    '/api/admin/user',
    '/admin/user-update-password',
    '/api/admin/user-update-password',
    '/admin/plugin',
    '/api/admin/plugin',
    '/admin/upgrade',
    '/api/admin/upgrade',
    '___FILES___'
    // 其他需要缓存的静态资源路径
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