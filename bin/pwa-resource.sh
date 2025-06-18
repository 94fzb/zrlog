cd admin-web/src/main/frontend/public/
find vendors -type f > pwa-resource.txt
echo  "admin/index
api/admin/index
admin/article-edit
api/admin/article-edit
admin/article
admin/article-type
api/admin/article-type
api/admin/article
admin/offline
api/admin/offline
admin/website
api/admin/website
admin/website/blog
api/admin/website/blog
admin/website/admin
api/admin/website/admin
admin/website/template
api/admin/website/template
admin/website/other
api/admin/website/other
admin/website/upgrade
api/admin/website/upgrade
admin/template-config?shortTemplate=default
api/admin/template-config?shortTemplate=default
admin/type
api/admin/type
admin/link
api/admin/link
admin/nav
api/admin/nav
admin/comment
api/admin/comment
admin/user
api/admin/user
admin/user-update-password
api/admin/user-update-password
admin/plugin
api/admin/plugin
admin/upgrade
api/admin/upgrade
admin/system
admin/403
admin/404
admin/500
admin/login
api/admin/system" > pwa-page.txt