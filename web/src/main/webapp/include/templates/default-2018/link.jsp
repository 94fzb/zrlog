<%@ page language="java" session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp"/>
<div class="main">
    <section>
        <style>
            /*video播放进度条问题*/
            .markdown-body {
                word-wrap: normal
            }
        </style>
        <article>
            <div class="post">
                <h1 class="post-title" style="padding-bottom: 0"><i class="fa fa-link"
                                                                    style="padding-right: 10px"></i>${_res['link']}</h1>
                <div class="content markdown-body editormd-preview-container" style="padding:0">
                    <ul>
                        <c:forEach items="${init.links}" var="link">
                            <li style="padding-bottom: 15px"><a href="${link.url}">${link.linkName}<c:if test="${not empty link.alt}"> (${link.alt})</c:if> </a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </div>
        </article>
    </section>
<jsp:include page="footer.jsp"/>