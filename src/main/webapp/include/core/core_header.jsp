<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	// 模板地址
	String templateUrl = request.getScheme() + "://" + request.getHeader("host") +request.getContextPath()+request.getAttribute("template");
	request.setAttribute("url", templateUrl);
	request.setAttribute("templateUrl", templateUrl);
	request.setAttribute("rurl",request.getScheme() + "://" + request.getHeader("host")+ request.getContextPath()+"/");
	String suffix="";
	if(request.getContextPath()+((Map<String,Object>)(((Map<String,Object>)request.getAttribute("init")).get("webSite"))).get("pseudo_static")!=null){
		suffix=".html";
	}
	Map<String,Object> webSite=((Map<String,Object>)((Map<String,Object>)request.getAttribute("init")).get("webSite"));
	request.setAttribute("suffix", suffix);
	request.setAttribute("webs", webSite);
	String title=webSite.get("title")+" - "+webSite.get("second_title");
	if(request.getAttribute("log")!=null){
		title=((Map<String,Object>)request.getAttribute("log")).get("title")+" - " +title;
	}
	request.setAttribute("title", title);
%>
