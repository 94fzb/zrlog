<#if pager??>
    <nav>
        <ul class="pagination">
            <#if !pager.startPage>
                <li class="page-item"><a title="${_res.pageStart}" class="page-link"
                                         href="${pager.pageStartUrl}">${_res.pageStart}</a></li>
            </#if>
            <#list pager.pageList as page>
                <li class="page-item<#if page.current> active</#if>"><a class="page-link <#if page.current> page-active</#if>"
                                                                        href="${page.url}">${page.desc}</a></li>
            </#list>
            <#if !pager.endPage>
                <li class="page-item"><a title="${_res.pageEnd}" class="page-link"
                                         href="${pager.pageEndUrl}">${_res.pageEnd}</a></li>
            </#if>
        </ul>
    </nav>
</#if>