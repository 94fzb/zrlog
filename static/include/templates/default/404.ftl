<section id="primary">
    <div id="content" role="main">
        <article id="post-0" class="post no-results not-found">
            <header class="entry-header">
                <h1 class="entry-title">${_res.notFound}</h1>
            </header>
            <div class="widget search">
                <p>抱歉，没有符合您搜索条件的结果。请换其它关键词再试。</p>
                <form method="post" id="searchform" action="${rurl}/search">
                    <input
                            type="text" class="field" name="key" id="s" placeholder="${_res.searchTip}"/>
                    <input type="submit" class="btn" name="submit"
                           id="searchsubmit" value="${_res.search}"/>
                </form>
            </div>
        </article>
    </div>
</section>
