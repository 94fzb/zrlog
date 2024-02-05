<#include "header.ftl">
<#if log??>
    <#include "article.ftl">
    <#include "comment.ftl">
<#else>
    <#assign pageLevel = 1>
    <#include "404.ftl">
</#if>
<#include "plugin.ftl">
<#include "footer.ftl">
