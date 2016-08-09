<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
request.setAttribute("url", request.getScheme()+"://"+request.getHeader("host")+request.getContextPath());
%>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${init.webSite.title} - ${_res.login}</title>
    <link href="${url}/assets/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="${url}/assets/css/font-awesome.min.css" rel="stylesheet">
    <!-- Custom Theme Style -->
    <link href="${url}/assets/css/custom.min.css" rel="stylesheet">
    <link rel="shortcut icon" href="f${url}/favicon.ico" />
  </head>

  <body class="login">
    <div>
      <div class="login_wrapper">
        <div class="animate form login_form">
          <section class="login_content">
            <form action="${url}/admin/login" method="post">
              <input type="hidden" name="redirectFrom" value="${param.redirectFrom}">
              <h1>${_res.userNameAndPassword}</h1>
              <div>
                <input name="userName" type="text" class="form-control" placeholder="${_res.userName}" required="" />
              </div>
              <div>
                <input name="password" type="password" class="form-control" placeholder="${_res.password}" required="" />
              </div>
              <div class="alert alert-danger" <c:if test="${empty errorMsg}">style="display: none;"</c:if> >
                  ${errorMsg}
              </div>
              <div>
                <button class="btn btn-default submit" type="submit">
                <i class="fa fa-sign-in"></i>
                ${_res.login}
                </button>
              </div>
              <div class="separator">
                <div class="clearfix"></div>
                <br />
                <div>
                  <p>©2016 All Rights Reserved. ${init.webSite.title}</p>
                </div>
              </div>
            </form>
          </section>
        </div>
      </div>
    </div>
  </body>
</html>
