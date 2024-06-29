<article class="markdown-body">
    <h2 style="margin-bottom: 8px">${log.title}</h2>
    <div class="meta" style="padding-bottom: .5rem;border-top: none;padding-top: 0">
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
    <hr style="padding-top: 16px;"/>
    <#if log.tags?has_content>
    <div style="display: flex;gap: 8px;height: 60px;align-items: center">
        <#list log.tags as tag>
            <a href="${tag.url}"><span class="badge text-bg-primary" style="font-size: 14px">#${tag.name}</span></a>
        </#list>
    </div>
    </#if>
    <p>
        ${_res.reprint!''}
        <a title="${log.title}" href="${log.noSchemeUrl}" style="padding-left: 4px">
            ${log.noSchemeUrl}
        </a>
    </p>
    <div class="pager-nav">
        <a title="${log.nextLog.title}" href="${log.nextLog.url}"><p>${_res.nextArticle}：${log.nextLog.title}</p></a>
        <a title="${log.lastLog.title}" href="${log.lastLog.url}"><p>${_res.lastArticle}：${log.lastLog.title}</p></a>
    </div>
    ${_res.detailAd!''}
</article>
