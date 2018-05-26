<%@ page session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="comment">
    <!-- Duoshuo Comment BEGIN -->
    <div class="ds-thread" data-thread-key="${log.logId}"
         data-title="${log.title}"
         data-url="${rurl}post/${log.alias}"></div>
    <script type="text/javascript">
        var duoshuoQuery = {short_name: "${webs.duoshuo_short_name}"};
        (function () {
            var ds = document.createElement('script');
            ds.type = 'text/javascript';
            ds.async = true;
            ds.src = '//static.duoshuo.com/embed.js';
            ds.charset = 'UTF-8';
            (document.getElementsByTagName('head')[0]
                || document.getElementsByTagName('body')[0]).appendChild(ds);
        })();
    </script>
    <!-- Duoshuo Comment END -->
</div>
