<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"+"manage/";

%>

<jsp:include page="../include/menu.jsp"/>
						<div class="page-header">
							<h1>
								控制台
								<small>
									<i class="icon-double-angle-right"></i>
									 查看
								</small>
							</h1>
						</div><!-- /.page-header -->
						<div class="col-xs-12 col-sm-10 col-sm-offset-1">
								<div class="timeline-container">
									<div class="timeline-label">
										<span class="label label-grey arrowed-in-right label-lg">
											<b>May 17</b>
										</span>
									</div>

									<div class="timeline-items">
										<div class="timeline-item clearfix">
											<div class="timeline-info">
												<i class="timeline-indicator icon-leaf btn btn-primary no-hover green"></i>
											</div>

											<div class="widget-box transparent">
												<div class="widget-header widget-header-small">
													<h5 class="smaller">Lorum Ipsum</h5>

													<span class="widget-toolbar no-border">
														<i class="icon-time bigger-110"></i>
														10:22
													</span>

													<span class="widget-toolbar">
														<a data-action="reload" href="#">
															<i class="icon-refresh"></i>
														</a>

														<a data-action="collapse" href="#">
															<i class="icon-chevron-up"></i>
														</a>
													</span>
												</div>

												<div class="widget-body">
													<div class="widget-main">
														Anim pariatur cliche reprehenderit, enim eiusmod
														<span class="blue bolder">high life</span>
														accusamus terry richardson ad squid …
													</div>
												</div>
											</div>
										</div>
									</div><!-- /.timeline-items -->
								</div><!-- /.timeline-container -->
							</div>
<jsp:include page="../include/footer.jsp"/>
