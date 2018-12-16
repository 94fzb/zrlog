<div class="page-header">
    <h3>
        ${_res.dashboard}
    </h3>
</div>
<div class="alert alert-primary">
    <i class="fa fa-info"></i>
    <span style="padding-left: 5px">${_res['admin.index.welcomeTips']}</span>
    <a class="d-none d-sm-block" target="_blank" href="http://blog.zrlog.com/post/feedback" style="float: right"><i
                class="fa fa-support"></i><span style="padding-left: 5px"> ${_res['suggestAndFeedback']}</span> </a>
</div>
<style>
    .table td, .table th {
        word-break: break-all;
    }
</style>
<div class="row">
    <div class="col-md-7 col-xs-12 col-sm-12">
        <div class="widget-box">
            <div class="widget-header">
                <h5>
                    <i class="fa fa-server pr5"></i>${_res.serverInfo}
                </h5>
            </div>
            <div class="widget-body">
                <div class="widget-main">
                    <table class="table table-striped table-bordered table-hover" style="overflow: auto;">
                        <thead>
                        <tr>
                            <th class="hidden-480">${_res.key}</th>
                            <th>
                                ${_res.value}
                            </th>
                        </tr>
                        </thead>

                        <tbody>
                        <tr>
                            <td>
                                运行环境
                            </td>
                            <td>
                                <i class="fa fa-${system['docker']!''}"></i>${system['java.vm.name']} (${system['java.runtime.version']}
                                )
                            </td>
                        </tr>
                        <tr>
                            <td>
                                JavaEE 容器信息
                            </td>
                            <td>${system['server.info']}</td>
                        </tr>
                        <tr>
                            <td>
                                运行路径
                            </td>
                            <td>${system['zrlog.runtime.path']}</td>
                        </tr>
                        <tr>
                            <td>
                                操作系统
                            </td>
                            <td><i class="fa fa-${system['os.type']} pr5"></i> ${system['os.name']}
                                    - ${system['os.arch']}
                                    - ${system['os.version']}</td>
                        </tr>
                        <tr>
                            <td>
                                系统时区 - 地域/语言
                            </td>
                            <td>${system['user.timezone']} - ${system['user.country']}/${system['user.language']}</td>
                        </tr>
                        <tr>
                            <td>
                                数据库版本
                            </td>
                            <td>${system['dbServer.version']}</td>
                        </tr>
                        <tr>
                            <td>
                                系统编码
                            </td>
                            <td>${system['file.encoding']}</td>
                        </tr>
                        <tr>
                            <td>
                                程序版本
                            </td>
                            <td>${zrlog.version} - ${zrlog.buildId} (${zrlog.buildTime})</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-5 col-xs-12 col-sm-12">
        <div class="widget-box">
            <div class="widget-header">
                <h5>
                    <i class="fa fa-pie-chart pr5"></i>
                    ${_res['admin.index.outline']}
                </h5>
            </div>
            <div class="widget-body">
                <div class="widget-main">
                    <div class="row">
                        <div class="col-md-6 col-sm-12 col-xs-12">
                            <div class="tile-stats">
                                <div class="icon"><i class="fa fa-comments-o"></i></div>
                                <div class="count">${toDayCommCount}</div>
                                <h3>今天评论</h3>
                            </div>
                        </div>
                        <div class="col-md-6 col-sm-12 col-xs-12">
                            <div class="tile-stats">
                                <div class="icon"><i class="fa fa-comments-o"></i></div>
                                <div class="count">${commCount }</div>
                                <h3>评论总数</h3>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 col-sm-12 col-xs-12">
                            <div class="tile-stats">
                                <div class="icon"><i class="fa fa-newspaper-o"></i></div>
                                <div class="count">${articleCount }</div>
                                <h3>文章总数</h3>
                            </div>
                        </div>
                        <div class="col-md-6 col-sm-12 col-xs-12">
                            <div class="tile-stats">
                                <div class="icon"><i class="fa fa-eye"></i></div>
                                <div class="count">${clickCount }</div>
                                <h3>文章浏览总数</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
${pageEndTag}