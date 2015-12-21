<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp"></jsp:include>
<c:set var="pageLevel" value="1" scope="request" />
<c:choose>
	<c:when test="${empty log}">
		<div id="main">
			<section id="primary">
				<div id="content" role="main">
					<article id="post-0" class="post no-results not-found">
						<header class="entry-header">
							<h1 class="entry-title">未找到</h1>
						</header>
						<!-- .entry-header -->

						<div class="entry-content">
							<p>抱歉，没有符合您搜索条件的结果。请换其它关键词再试。</p>
							<form method="post" id="searchform" action="${rurl }post/search">
								<label for="s" class="assistive-text">搜索</label> <input
									type="text" class="field" name="key" id="s" placeholder="搜索" />
								<input type="submit" class="submit" name="submit"
									id="searchsubmit" value="搜索" />
							</form>
						</div>
						<!-- .entry-content -->
					</article>
					<!-- #post-0 -->


				</div>
				<!-- #content -->
			</section>
			<!-- #primary -->
	</c:when>
	<c:otherwise>

		<DIV id="main">
			<DIV id="primary">
				<DIV id="content" role="main">
					<NAV id="nav-single">
						<H3 class="assistive-text">文章导航</H3>
						<span class="nav-previous"><a
							href="${rurl }post/${log.lastLog.alias}"
							title="${log.lastLog.title}" rel="prev"><span
								class="meta-nav">&larr;</span> 上一篇</a> </span> <span class="nav-next"><a
							href="${rurl }post/${log.nextLog.alias}"
							title="${log.nextLog.title}" rel="next">下一篇 <span
								class="meta-nav">&rarr;</span> </a> </span>
					</NAV>
					<ARTICLE
						class="post-33 post type-post status-publish format-standard hentry category-seo"
						id="post-33">
						<HEADER class="entry-header">
							<H1 class="entry-title">${log.title }</H1>
							<DIV class="entry-meta">
								<SPAN class="sep">发表于 </SPAN><A href="${rurl}post/${log.alias}"
									rel="bookmark"><time class="entry-date" pubdate=""
										datetime="${log.releaseTime}">${log.releaseTime.year+1900}年${log.releaseTime.month+1}月${log.releaseTime.date}日</time>
								</A>
							</DIV>

						</HEADER>

						<DIV class="entry-content">
							<P>${log.content}</P>
						</DIV>
						<FOOTER class="entry-meta">
							此条目是由 <A href="${rurl}${log.userName}">${log.userName} </A> 发表在 <A
								title="查看${log.typeName}中的全部文章"
								href="${rurl}post/sort/${log.typeAlias}" rel="category tag">${log.typeName}</A>
							分类目录的
							<c:if test="${not empty log.keywords}">，并贴了 <c:forTokens
									items="${log.keywords}" delims="," var="tag">
									<a href="${rurl}post/tag/${tag}" rel="tag">${tag}</a>&nbsp;</c:forTokens>
			         标签 </c:if>
							。
						</FOOTER>

					</ARTICLE>
<script type="text/javascript" >BAIDU_CLB_SLOT_ID = "903239";</script>
<script type="text/javascript" src="http://cbjs.baidu.com/js/o.js"></script>
<br/>
					<DIV id="copyinfo">
						<P>
							<A title="${log.title}" href="${rurl}post/${log.alias}"
								target="_blank"><SPAN style="color: rgb(51, 102, 255);"
								span="">${log.title}</SPAN> </A> 转载请注明作者和出处(${webs.title})，并添加本页链接。<BR>原文链接:
							<A title="${log.title }" href="${rurl}post/${log.alias}"><SPAN
								style="color: rgb(51, 102, 255);" span="">${rurl}post/${log.alias}</SPAN>
							</A>
						</P>
					</DIV>
					<!-- JiaThis Button BEGIN -->
					<div class="jiathis_style_32x32">
						<a class="jiathis_button_tsina"></a> <a class="jiathis_button_tqq"></a>
						<a class="jiathis_button_qzone"></a> <a
							class="jiathis_button_renren"></a> <a
							class="jiathis_button_kaixin001"></a> <a
							class="jiathis_button_cqq"></a> <a class="jiathis_button_xianguo"></a>
						<a class="jiathis_button_douban"></a> <a
							class="jiathis_button_fav"></a> <a class="jiathis_button_email"></a>
						<a href="http://www.jiathis.com/share?uid=1703951"
							class="jiathis jiathis_txt jiathis_separator jtico jtico_jiathis"
							target="_blank"></a> <a class="jiathis_counter_style"></a>
					</div>
					<script type="text/javascript">
			var jiathis_config={
				data_track_clickback:true,
				summary:"",
				appkey:{
					"tsina":"3945409817",
					"tqq":"801275436"
				},
				hideMore:false
			}
			</script>
					<script type="text/javascript"
						src="http://v3.jiathis.com/code/jia.js?uid=1703951"
						charset="utf-8"></script>
					<!-- JiaThis Button END -->
					<BR> <BR>
					<div id="wumiiDisplayDiv"></div>
					<div id="comments">


						<c:choose>
							<c:when test="${log.canComment}">
								<c:choose>
									<c:when test="${init.webSite.user_comment_pluginStatus}">
										 <jsp:include page="../../core/duoshuo_comment.jsp"></jsp:include>
									</c:when>
									<c:otherwise>
									<h2 id="comments-title">
												《<span>${log.title }</span>》上有 ${log.commentSize} 条评论
											</h2>
											<c:forEach var="comm" items="${requestScope.log.comments}">

											
											<ol class="commentlist">

												<li class="comment even thread-even depth-1"
													id="li-comment-${comm.cnt }">
													<article id="comment-${comm.cnt }" class="comment">
														<footer class="comment-meta">
															<div class="comment-author vcard">
																<img alt=''
																	src='http://0.gravatar.com/avatar/42fce334be587ecf8482176896bda897?s=68&amp;d=http%3A%2F%2F0.gravatar.com%2Favatar%2Fad516503a11cd5ca435acc9bb6523536%3Fs%3D68&amp;r=G'
																	class='avatar avatar-68 photo' height='68' width='68' /><span
																	class="fn"><a href='${comm.userHome }'
																	rel='external nofollow' class='url'>${comm.userName}</a>
																</span> 在 <a href="${rurl}/${log.aliasT}#comment-${comm.cnt }"><time
																		pubdate datetime="${comm.commTime}">${comm.commTime.year+1900
																		}年${comm.commTime.month+1}月${comm.commTime.date}日
																		${comm.commTime.hours} : ${comm.commTime.minutes}</time> </a> <span
																	class="says">说道：</span>
															</div>



														</footer>

														<div class="comment-content">
															<p>${comm.userComment}</p>
														</div>

														<div class="reply">
															<a class='comment-reply-link'
																href='${rurl}/${log.aliasT}?replytocom=${comm.cnt }#respond'
																onclick='return addComment.moveForm("comment-${comm.cnt }", "${comm.cnt }", "respond", "${log.aliasT}")'>回复
																<span>&darr;</span> </a>
														</div>

													</article></li>

											</ol>
										</c:forEach>
									
									
										<div id="respond" class="comment-respond">
											<form action="${rurl}post/addComment" method="post" id="commentform"
												class="comment-form">
												<input type="hidden" name="logId" value="${log.logId}" />
												<p class="comment-notes">
													必填项已用<span class="required">*</span>标注
												</p>
												<p class="comment-form-author">
													<label for="author">姓名 <span class="required">*</span>
													</label> <input id="author" name="userName" type="text" value=""
														size="30" aria-required='true' />
												</p>
												<p class="comment-form-comment">
													<label for="comment">评论</label>
													<textarea id="comment" name="userComment" cols="45" rows="8"
														aria-required="true"></textarea>

												</p>
												<input type='hidden' id='YXM_here' />
												<script type='text/javascript' charset='gbk' id='YXM_script'
													src='http://api.yinxiangma.com/api3/yzm.yinxiangma.php?pk=0bc4665eded8dfb776e37937a91785c0&v=YinXiangMaJAVASDK_4.0'>
												</script>
												<p class="form-submit">
													<input type="submit" id="sub" value="发表评论" /> <input
														type='hidden' name='comment_post_ID' value='22'
														id='comment_post_ID' /> <input type='hidden'
														name='comment_parent' id='comment_parent' value='0' />
												</p>
											</form>
										</div>
									</c:otherwise>
								</c:choose>

							</c:when>
							<c:otherwise>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
	</c:otherwise>
</c:choose>
<jsp:include page="plugs.jsp"></jsp:include>
<jsp:include page="footer.jsp"></jsp:include>