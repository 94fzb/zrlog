<%@ page session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<style>
    .search input.btn {
        width: 60px;
        height: 34px;
        padding: 0px;
        border: 0 none;
        vertical-align: middle;
        outline: none;
        background-color: #E4E4E4;
    }
</style>
<section id="primary">
    <div id="content" role="main">
        <article id="post-0" class="post no-results not-found">
            <header class="entry-header">
                <h1 class="entry-title">未找到</h1>
            </header>
            <!-- .entry-header -->
            <div class="widget search">
                <p>抱歉，没有符合您搜索条件的结果。请换其它关键词再试。</p>
                <form method="post"  action="${rurl }post/search">
                    <input type="text" class="field" name="key" id="s" placeholder="${_res.searchTip}"/>
                    <input type="submit" class="btn" name="submit" value="${_res.search}"/>
                </form>
            </div>
        </article>
    </div>
</section>