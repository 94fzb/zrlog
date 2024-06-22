<aside>
    <#if _res.widgetAd?has_content>
        <div class="widget" style="padding-bottom: 15px">
            ${_res.widgetAd}
        </div>
    </#if>

    <form class="widget search search_input" style="margin-bottom: .5rem" action="${searchUrl}" method="post">
        <input type="text" size="15" name="key" placeholder="${_res.searchTip}" value='${key!""}' class="inputtext"/>
        <input type="submit" class="btn" value="${_res.search}"/>
    </form>

    <#if init.plugins?has_content>
        <#list init.plugins as plugin>
            <#if plugin.isSystem == false>
            <#--不显示非系统插件-->
            <#else>
                <#switch plugin.pluginName>
                    <#case "types">
                        <div class="widget">
                            <h3>${_res.category}</h3>
                            <div class="list">
                                <ul class="category_list">
                                    <#list init.types as type>
                                        <li><a href="${type.url}">${type.typeName} (${type.typeamount})</a></li>
                                    </#list>
                                </ul>
                            </div>
                        </div>
                        <#break>
                    <#case "links">
                        <div class="widget">
                            <h3>${_res.link}</h3>
                            <ul>
                                <#list init.links as link>
                                    <li><a href="${link.url}" title="${link.alt}" target="_blank">${link.linkName}</a>
                                    </li>
                                </#list>
                            </ul>
                        </div>
                        <#break>
                    <#case "archives">
                        <div class="widget">
                            <h3>${_res.archive}</h3>
                            <ul>
                                <#list init.archiveList as archive>
                                    <li><a href="${archive.url}" rel="nofollow">${archive.text} (${archive.count})</a>
                                    </li>
                                </#list>
                            </ul>
                        </div>
                        <#break>
                    <#case "tags">
                        <div class="widget">
                            <h3>${_res.tag}</h3>
                            <div class="taglist">
                                <#list init.tags as tag>
                                    <a href="${tag.url}" class="size${tag.keycode % 6}">${tag.text}</a>
                                </#list>
                            </div>
                        </div>
                        <#break>
                </#switch>
            </#if>
        </#list>
    </#if>
</aside>
