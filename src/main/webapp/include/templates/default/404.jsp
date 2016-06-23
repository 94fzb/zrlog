<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<section id="primary">
    <div id="content" role="main">
        <article id="post-0" class="post no-results not-found">
            <header class="entry-header">
                <h1 class="entry-title">未找到</h1>
            </header>
            <!-- .entry-header -->
            <div class="widget search">
                <p>抱歉，没有符合您搜索条件的结果。请换其它关键词再试。</p>
                <form method="post" id="searchform" action="${rurl }post/search">
                    <label for="s" class="assistive-text">搜索</label> <input
                        type="text" class="field" name="key" id="s" placeholder="搜索" />
                    <input type="submit" class="submit" name="submit"
                        id="searchsubmit" value="搜索" />
                </form>
            </div>
            <!-- .entry-content -->
        </article>
    </div>
    <!-- #content -->
</section>