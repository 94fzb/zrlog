<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
if(session.getAttribute("init")==null || request.getAttribute("logs")==null)
{
	request.getRequestDispatcher("/post?op=init").forward(request, response);
	return;	
}

%>
<jsp:include page="left.jsp"></jsp:include>
<jsp:include page="logs_list.jsp"></jsp:include>
<jsp:include page="footer.jsp"></jsp:include>
