<section>
    <div role="main">
        <article class="post no-results not-found">
            <header class="entry-header">
                <h1 class="entry-title">${_res.notFound}</h1>
            </header>
            <div class="widget search">
                <p>抱歉，没有符合您搜索条件的结果。请换其它关键词再试。</p>
                <form method="post" action="${searchUrl}">
                    <input
                            type="text" value='${key!""}' class="field" name="key" placeholder="${_res.searchTip}"/>
                    <input type="submit" class="btn" name="submit"
                           value="${_res.search}"/>
                </form>
            </div>
        </article>
    </div>
</section>
