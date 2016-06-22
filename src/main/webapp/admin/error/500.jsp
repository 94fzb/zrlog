<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../include/menu.jsp"/>
<div class="row">
    <div class="col-xs-12">
        <!-- PAGE CONTENT BEGINS -->

        <div class="error-container">
            <div class="well">
                <h1 class="grey lighter smaller">
                    <span class="blue bigger-125">
                        <i class="icon-sitemap"></i>
                        404
                    </span>
                    ${_res['error.pageNotPage']}
                </h1>

                <hr>
                <div class="space"></div>
                <div class="center">
                    <a class="btn btn-grey" href="javascript:history.go(-1);">
                        <i class="icon-arrow-left"></i>
                        ${_res.goBack}
                    </a>
                    <a class="btn btn-primary" href="#">
                        <i class="icon-dashboard"></i>
                        ${_res.dashboard}
                    </a>
                </div>
            </div>
        </div><!-- PAGE CONTENT ENDS -->
    </div><!-- /.col -->
</div><!-- /.row -->
<jsp:include page="../include/footer.jsp"/>