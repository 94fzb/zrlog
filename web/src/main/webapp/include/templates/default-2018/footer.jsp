<%@ page language="java" session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

</div>
<div class="bottom">
    <div class="inner">
        <footer>
            <div class="footer-left mission"><p>${_res.footerSlogan}</p></div>
            <div class="footer-right copyright">
                <p>${_res['copyrightCurrentYear']}<a href="${rurl}" style="padding-left: 5px">${website.title}</a></p>
                <p style="padding-top: 10px">Powered by <a href="http://www.zrlog.com" target="_blank">ZrLog</a>,
                    Themed by <a href="http://sheshui.me/about" target="_blank">Robin L.</a>
                </p>
            </div>
        </footer>
    </div>
</div>
</div> <!-- //page -->
<script src="${templateUrl}/js/sheshui.js"></script>
<div style="display:none">${website.webCm}</div>
</body>
</html>
