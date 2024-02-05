<article class="markdown-body ">
    <h2 class="post-title" style="padding-top: .5rem">${log.title}</h2>
    <div class="meta" style="display: flex;padding-bottom: .5rem">
        <div style="display:flex;justify-content: flex-start;gap: .4rem">
            <span class="category">
                <a href="${log.typeUrl}" rel="tag">${log.typeName}</a>
            </span>
            <span>/</span>
            <span class="published">
                ${log.releaseTime?split("T")[0]}
            </span>
        </div>
    </div>
    <div class="content">
        ${log.content!''}
    </div>
    <p style="color:#D4D4D4">${_res.reprint!''}
        <a title="${log.title}" href="${log.noSchemeUrl}"><SPAN style="color: rgb(51, 102, 255);" span="">${log.noSchemeUrl}</SPAN></a>
    </p>
    <div class="pager-nav">
        <a title="${log.nextLog.title}" href="${log.nextLog.url}"><p>${_res.nextArticle}：${log.nextLog.title}</p></a>
        <a title="${log.lastLog.title}" href="${log.lastLog.url}"><p>${_res.lastArticle}：${log.lastLog.title}</p></a>
    </div>
    ${_res.detailAd!''}
</article>
