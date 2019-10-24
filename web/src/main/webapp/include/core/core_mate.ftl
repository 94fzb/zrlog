<meta charset="utf-8"/>
<title>
<#if log ?? && log.title ??>${log.title!''} - </#if>${webs.title!''} - ${webs.second_title!''}</title>
<meta name="description" content="${webs.description!''}"/>
<#if log ?? && log.keywords ??>
<meta name="keywords" content="${log.keywords}"/>
<#else>
<meta name="keywords" content="${webs.keywords!''}"/>
</#if>
<link rel="shortcut icon" href="${rurl}favicon.ico"/>
