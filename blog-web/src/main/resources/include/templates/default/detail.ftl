<#include "header.ftl">
<section>
<#if log??>
    <#include "article.ftl">
    <#include "comment.ftl">
<#else>
    <#assign pageLevel = 1>
    <#include "404.ftl">
</#if>
</section>
<#include "plugin.ftl">
<#include "footer.ftl">
