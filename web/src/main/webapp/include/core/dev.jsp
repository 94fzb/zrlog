<%@ page session="false" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${dev}">
    <textarea id="dev" style="display:none">${requestScopeJsonString}</textarea>
    <script>
        function dev() {
            console.info("request scope data", JSON.parse(document.getElementById("dev").value));
        }

        dev();
    </script>
</c:if>