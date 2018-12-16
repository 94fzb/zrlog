<%@ page language="java" session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<article>
    <div class="post" style="padding-bottom: 20px">
        <h1 class="post-title">${log.title}</h1>
        <div class="meta">
            <p class="category">${_res['category']}：<a href="${log.typeUrl}" rel="tag">${log.typeName}</a></p>
            <p style="padding: 0 5px 0 5px">/</p>
            <p class="published">${_res['date']}：
                <time datetime="${log.releaseTime}">${log.releaseTime.year+1900}-${log.releaseTime.month+1}-${log.releaseTime.date}</time>
            </p>
        </div>
        <div class="content markdown-body editormd-preview-container" style="padding:0">
            ${log.content }
        </div>
    </div>
    <p style="color:#D4D4D4">最后更新：${log.lastUpdateDate}</p>
    <p style="color:#D4D4D4">—— 原创内容，转载请注明：[ 文章转载自：${website.title} &nbsp; <a href="${baseUrl}"
                                                                              style="color:#D4D4D4">${baseUrl}</a> ] ——
    </p>
</article>