<#include "header.ftl">
<#if data?has_content>
    <#if tipsType?has_content>
        <div class="category-title">
            <h3>${tipsType}目录：${tipsName}</h3>
            <h3>以下是与${tipsType} “${tipsName}” 相关联的文章</h3>
        </div>
    </#if>
    <#if data?has_content>
        <#list data.rows as log>
            <article class="entry markdown-body">
                <#if log.thumbnail?has_content>
                    <img width="760px" onerror="this.style.display='none'" alt="${log.title}" src="${log.thumbnail}"/>
                </#if>
                <h2 class="post-title"><a rel="bookmark" href="${log.url}">${log.title}</a></h2>
                <div class="content"><p>${log.digest!''}</p></div>
                <div class="meta" style="display: flex;justify-content: space-between">
                    <div style="display:flex;justify-content: flex-start;gap: .4rem">
                        <span class="category">
                            <a href="${log.typeUrl}">${log.typeName}</a>
                        </span>
                        <span>/</span>
                        <span class="published">${log.releaseTime?split("T")[0]}</span>
                    </div>
                    <p class="commentlink">
                        <#if log.canComment>
                            <a href="${log.url}#comment" class="comments_invite">
                                ${_res.commentView} [${log.commentSize}]
                            </a>
                        </#if>
                    </p>
                </div>
            </article>
        </#list>
    </#if>
<#else>
    <#assign pageLevel = 1>
    <#include "404.ftl">
</#if>

<#include "pager.ftl">
<#include "plugin.ftl">
<#include "footer.ftl">