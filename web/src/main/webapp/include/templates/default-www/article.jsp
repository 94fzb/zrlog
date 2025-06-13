<%@ page language="java" session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<article class="markdown-body ">
    <h2 class="post-title" style="padding-top: .5rem">${log.title}</h2>
    <div class="meta" style="display: flex;padding-bottom: 8px">
        <span class="category" style="padding-right: 5px">
            <a href="${log.typeUrl}" rel="tag">${log.typeName}</a>
        </span>
        /
        <span class="published" style="padding-left: 5px">
            ${log.releaseTime.year+1900}-${log.releaseTime.month+1}-${log.releaseTime.date}
        </span>
    </div>
    <div class="content">
        ${log.content }
    </div>
    <p style="color:#D4D4D4">${_res.reprint}
        <a title="${log.title }" href="${log.url}"><SPAN style="color: rgb(51, 102, 255);" span="">${log.url}</SPAN></a>
    </p>
    <div class="pager-nav">
        <a title="${log.nextLog.title}" href="${log.nextLog.url}"><p>${_res.nextArticle}：${log.nextLog.title}</p></a>
        <a title="${log.lastLog.title}" href="${log.lastLog.url}"><p>${_res.lastArticle}：${log.lastLog.title}</p></a>
    </div>
    ${_res.detailAd}
</article>