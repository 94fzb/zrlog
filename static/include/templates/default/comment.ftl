<#if log.canComment>
    <div id="comment-list" class="comment">
        <#if init.webSite.changyan_status == "on">
            <plugin name="changyan" view="widget" param="articleId=${log.logId!''}"></plugin>
        <#else>
            <#if log.comments?has_content>
                <h2>${_res.comments}</h2>
            </#if>
            <#list log.comments as comment>
                <ul class="comments">
                    <li><p>${comment.userComment}</p>
                        <p class="small"><a rel="nofollow">${comment.userName}</a> ${_res.on} ${comment.commTime}</p>
                    </li>
                </ul>
            </#list>
            <form action="/addComment" method="post" id="txpCommentInputForm">
                <input type="hidden" name="logId" value="${log.logId}">
                <h2>${_res.comment}</h2>
                <textarea required class="form-control" rows="15" cols="45" name="userComment"></textarea>
                <div class="input-group  mb-3" style="padding-top: 15px">
                    <div class="input-group-prepend">
                        <span class="input-group-text"  style="height: 42px;border-radius: 4px 0 0 4px">${_res.website}</span>
                    </div>
                    <input  required type="text" name="web" class="form-control"/>
                </div>
                <div class="row">
                    <div class="col-6">
                        <div class="input-group mb-3">
                            <div class="input-group-prepend" >
                                <span class="input-group-text" style="height: 42px;border-radius: 4px 0 0 4px">${_res.userName}</span>
                            </div>
                            <input required type="text" name="userName" class="form-control"/>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-6">
                        <div class="input-group  mb-3">
                            <div class="input-group-prepend">
                                <span class="input-group-text" style="height: 42px;border-radius: 4px 0 0 4px">${_res.email}</span>
                            </div>
                            <input required type="text" name="email" class="form-control"/>
                        </div>
                    </div>
                </div>
                <button type="submit" class="btn btn-outline-primary">${_res.submit}</button>
            </form>
        </#if>
    </div>
</#if>
