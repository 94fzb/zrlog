<#include "header.ftl">
    <section>
<#if data?has_content>
    <#if tipsType?has_content>
        <div style="padding-bottom: 12px">
            <h3>${tipsType}目录：${tipsName}</h3>
            <p style="font-size: 18px">以下是与${tipsType} “${tipsName}” 相关联的文章</p>
        </div>
    </#if>
    <#if data?has_content && data.rows?has_content>
        <#list data.rows as log>
            <article class="markdown-body">
                <#if log.thumbnail?has_content>
                    <img class="preview-img" onerror="this.style.display='none'" alt="${log.title}" src="${log.thumbnail}"/>
                </#if>
                <h2 style="margin-bottom: 8px;margin-top: 0"><a rel="bookmark" href="${log.url}">${log.title}</a></h2>
                <div class="content" style="padding-bottom: 12px">${log.digest!''}</div>
                <div class="meta">
                    <div style="display:flex;justify-content: flex-start;gap: .4rem">
                        <span class="category">
                            <a href="${log.typeUrl}">${log.typeName}</a>
                        </span>
                        <span>/</span>
                        <span class="published">${log.releaseTime?split("T")[0]}</span>
                    </div>
                    <#if log.canComment>
                        <a href="${log.url}#comment" class="comments_invite">
                            ${_res.commentView} [${log.commentSize}]
                        </a>
                    </#if>
                </div>
            </article>
        </#list>
    </#if>
<#else>
    <#assign pageLevel = 1>
    <#include "404.ftl">
</#if>

<#include "pager.ftl">
    </section>
<#include "plugin.ftl">
<#include "footer.ftl">