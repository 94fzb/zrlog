<%@ page session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
</div>
<div class="breadcrumb"></div>
<div class="bottom">
    <div class="inner">
        <footer>
            <div class="footer-left mission"><p>${_res.footerSlogan}</p></div>
            <div class="footer-right">
                <p><cite>${_res['copyrightCurrentYear']}&nbsp;&nbsp;${website.title}</cite></p>
                <p>Powered by <a href="http://zrlog.com" target="_blank">zrlog</a>, <span title="涉水轻舟">Themed by <a href="http://sheshui.me/about" target="_blank">Robin L.</a></span></p>
                <p>${website.icp}</p>
            </div>
            <div class="clearfix"></div>
        </footer>
    </div>
</div>
</div>
<div style="display:none">${website.webCm}</div>
</body>
</html>

