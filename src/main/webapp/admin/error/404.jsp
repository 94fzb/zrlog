<%@ page language="java" session="false" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../include/menu.jsp"/>
<div class="row">
    <div class="col-xs-12">
        <div class="error-container">
            <div class="well">
                <h1 class="grey lighter smaller">
                    <span class="blue bigger-125">
                        <i class="fa fa-sitemap"></i>
                        404
                    </span>
                    ${_res['error.pageNotPage']}
                </h1>
                <hr>
                <div class="space"></div>
                <div class="center">
                    <a class="btn btn-info" href="javascript:history.go(-1);">
                        <i class="fa fa-arrow-left"></i>
                        ${_res.goBack}
                    </a>
                    <a class="btn btn-primary" href="#">
                        <i class="fa fa-dashboard"></i>
                        ${_res.dashboard}
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="../include/footer.jsp"/>