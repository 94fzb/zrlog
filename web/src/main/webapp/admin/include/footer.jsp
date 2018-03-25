<%@ page session="false" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
</div>
</div>
<c:if test="${currentPage != 'admin/article_edit'}">
    <footer>
        <strong>${_res.copyright} <a href="https://www.zrlog.com"> ZrLog . </a></strong>
        All rights reserved.
        <div class="pull-right">
            <strong>Version</strong> ${zrlog.version}
        </div>
    </footer>
</c:if>
</div>
</div>
<jsp:include page="simple_footer.jsp"/>