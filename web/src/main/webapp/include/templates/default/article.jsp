<%@ page session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<article>
    <h1 class="post-title">${log.title}</h1>
    <div class="meta">
        <p class="category">
            <a rel="tag" style="padding-right: 3px" href="${log.typeUrl}">${log.typeName}</a>
        </p>
        <p class="published">/
            <time style="padding-left: 3px">${log.releaseTime.year+1900}-${log.releaseTime.month+1}-${log.releaseTime.date}</time>
        </p>
    </div>
    <style>
        .prettyprint .linenums li:before {
            content: "  ";
        }
    </style>
    <div class="content markdown-body editormd-preview-container" style="padding:0">
        ${log.content }
    </div>
    <p style="color:#D4D4D4;padding-top: 20px;padding-bottom: 6px;">${_res.reprint}
        <a title="${log.title }" href="${log.url}"><SPAN style="color: rgb(51, 102, 255);" span="">${log.noSchemeUrl}</SPAN></a>
    </p>
    <div class="pager-nav">
        <a title="${log.nextLog.title}" href="${log.nextLog.url}"><p
                class="next">${_res.nextArticle}：${log.nextLog.title}</p></a>
        <a title="${log.lastLog.title}" href="${log.lastLog.url}"><p
                class="prev">${_res.lastArticle}：${log.lastLog.title}</p></a>
    </div>
</article>