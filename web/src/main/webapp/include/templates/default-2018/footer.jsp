<%@ page language="java" session="false" import="java.util.*" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

</div>
<div class="bottom">
    <div class="inner">
        <footer>
            <div class="footer-left mission"><p>${_res.footerSlogan}</p></div>
            <div class="footer-right copyright">
                <p><cite>${_res['copyrightCurrentYear']}&nbsp; <a href="${rurl}">${website.title}</a></cite></p>
                <p>Powered by <a href="http://zrlog.com" target="_blank">zrlog</a>,
                    <span title="涉水轻舟">Themed by <a href="http://sheshui.me/about" target="_blank">Robin L.</a></span>
                </p>
            </div>
            <div class="clearfix"></div>
        </footer>
    </div>
</div>
</div> <!-- //page -->
<script src="${url}/js/sheshui.js?20180210"></script>
<div style="display:none">${website.webCm}</div>
</body>
</html>
