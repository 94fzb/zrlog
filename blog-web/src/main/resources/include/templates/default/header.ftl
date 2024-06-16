<!DOCTYPE html>
<html lang="${lang}">
<head>
    <meta charset="utf-8"/>
    <title>${title!''}</title>
    <link rel="shortcut icon" type="image/x-icon" href="/favicon.ico"/>
    <meta name="description" content="${description!''}"/>
    <meta name="keywords" content="${keywords!''}"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"/>
    <link rel="stylesheet" type="text/css" href="${url}/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="${url}/css/editormd.css"/>
    <link rel="stylesheet" type="text/css" href="${url}/css/style_v3.css"/>

    <script src="${url}/js/bootstrap.bundle.min.js"></script>
    <script src="${url}/js/auto-theme.js"></script>
</head>
<body>
<nav class="navbar navbar-expand-lg sticky-top">
    <div class="container">
        <a <#if _res.navBarBrand?has_content>class='navbar-brand'</#if>
           href="${rurl}"><#if _res.navBarBrand?has_content>
                <b>${_res.navBarBrand}</b>
            </#if></a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto">
                <#list init.logNavs as lognav>
                    <li class="nav-item<#if lognav.current> active</#if>">
                        <a class="nav-link" href="${lognav.url}">${lognav.navName}</a>
                    </li>
                </#list>
            </ul>
        </div>
    </div>
</nav>
<div class="main clearfloat">
    <div class="container">
        <section>