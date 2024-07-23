"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[225],{4553:function(e,n,t){var i=t(1413),a=t(5861),r=t(9439),o=t(7757),u=t.n(o),s=t(2745),l=t(6593),d=t(8727),c=t(8391),f=t(6146),p=t(1522),m=t(5333),v=t(3561),g=t(6155),h=t(8773),x=t(5536),y=t(3712);n.Z=function(e){var n=e.deleteApi,t=e.editBtnRender,o=e.addBtnRender,z=e.columns,Z=e.datasource,k=e.searchKey,w=e.deleteSuccessCallback,b=e.hideId,I=e.offline,j=(0,h.s0)(),R=(0,h.TH)(),S=function(e,n,t){return q(e,n,t,-1)},q=function(e,n,t,i){var a={};e>1&&(a.page=e),10!=n&&1e6!=n&&(a.size=n),t&&t.trim().length>0&&(a.key=t.trim()),i>0&&(a[v.NJ]=i);var r=(0,p.bI)(a);return 0===r.length?R.pathname:R.pathname+"?"+r},C=function(e,n,t){j(S(e,n,t))},H=function(e,n,t){j(q(e,n,t,(new Date).getTime()))},P=(0,c.useState)({pagination:{page:null!==Z&&void 0!==Z&&Z.page?Z.page:1,key:null===Z||void 0===Z?void 0:Z.key,size:null!==Z&&void 0!==Z&&Z.size?null===Z||void 0===Z?void 0:Z.size:10},query:null===Z||void 0===Z?void 0:Z.key,tableLoaded:!0,rows:Z?Z.rows:[],tablePagination:{total:null===Z||void 0===Z?void 0:Z.totalElements,current:null===Z||void 0===Z?void 0:Z.page,pageSize:null===Z||void 0===Z?void 0:Z.size,onChange:function(e,n){C(e,n,M.query)}}}),E=(0,r.Z)(P,2),M=E[0],N=E[1],T=s.ZP.useMessage(),_=(0,r.Z)(T,2),B=_[0],D=_[1],A=function(){var e=(0,a.Z)(u().mark((function e(n,t,i){var a;return u().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,f.Z.post(t+"?id="+i);case 2:if(!(a=e.sent).data.error){e.next=6;break}return B.error(a.data.message),e.abrupt("return");case 6:B.success((0,v.sR)().deleteSuccess),H(n.page,n.size,M.query);case 8:case"end":return e.stop()}}),e)})));return function(n,t,i){return e.apply(this,arguments)}}();(0,c.useEffect)((function(){k!==M.query&&(N((0,i.Z)((0,i.Z)({},M),{},{query:k})),C(1,M.pagination.size,k))}),[k]),(0,c.useEffect)((function(){N((function(e){return(0,i.Z)((0,i.Z)({},e),{},{rows:Z?Z.rows:[],pagination:{page:null!==Z&&void 0!==Z&&Z.page?Z.page:1,size:null!==Z&&void 0!==Z&&Z.size?Z.size:10},tablePagination:{current:null===Z||void 0===Z?void 0:Z.page,pageSize:null===Z||void 0===Z?void 0:Z.size,total:null===Z||void 0===Z?void 0:Z.totalElements}})}))}),[Z]);return(0,y.jsxs)(y.Fragment,{children:[D,o?o((function(){H(M.pagination.page,M.pagination.size,M.query)})):void 0,(0,y.jsx)(d.Z,{onChange:function(e){C(e.current?e.current:1,e.pageSize?e.pageSize:10,M.query)},style:{minHeight:512},columns:function(){var e=[];return null!==b&&void 0!==b&&b||e.push({title:"ID",dataIndex:"id",key:"id",fixed:!0,width:64,render:function(e){return(0,y.jsx)("span",{style:{maxWidth:64},children:e})}}),e.push({title:"",dataIndex:"id",key:"action",fixed:!0,width:80,render:function(e,i){return e?(0,y.jsxs)(l.Z,{size:16,children:[(0,y.jsx)(x.rU,{to:"#delete-"+i.id,onClick:function(e){e.preventDefault(),e.stopPropagation()},children:(0,y.jsx)(m.Z,{disabled:I,title:(0,v.sR)().deleteTips,onConfirm:function(){return A(M.pagination,n,i.id).then((function(){w&&w(i.id)}))},children:(0,y.jsx)(g.Z,{style:{color:"red"}})})}),t?t(e,i,(function(){H(M.pagination.page,M.pagination.size,M.query)})):null]}):null}}),z.forEach((function(n){e.push(n)})),e}(),pagination:(0,i.Z)((0,i.Z)({hideOnSinglePage:!0},M.tablePagination),{},{itemRender:function(e,n,t){return(0,y.jsx)(x.rU,{to:S(e,null!==Z&&void 0!==Z&&Z.size?Z.size:10,M.query),children:t},e)}}),dataSource:M.rows,scroll:{x:"90vw"}})]})}},8225:function(e,n,t){t.r(n);var i=t(8881),a=t(1474),r=t(3561),o=t(4553),u=t(517),s=t(3712);n.default=function(e){var n=e.data,t=e.offline;return(0,s.jsxs)(s.Fragment,{children:[(0,s.jsx)(i.Z,{className:"page-header",level:3,children:(0,r.sR)()["admin.comment.manage"]}),(0,s.jsx)(a.Z,{}),(0,s.jsx)(o.Z,{offline:t,datasource:n,columns:[{title:(0,r.sR)().content,dataIndex:"userComment",key:"userComment",width:600,render:function(e){return(0,s.jsx)(u.Z,{autoSize:{minRows:1,maxRows:6},style:{border:"none",minWidth:300},readOnly:!0,value:e})}},{title:(0,r.sR)().nickName,dataIndex:"userName",key:"userName"},{title:"\u8bc4\u8bba\u8005\u4e3b\u9875",key:"userHome",dataIndex:"userHome"},{title:"IP",key:"userIp",dataIndex:"userIp"},{title:(0,r.sR)().email,key:"userMail",dataIndex:"userMail"},{title:"\u8bc4\u8bba\u65f6\u95f4",key:"commTime",dataIndex:"commTime"}],deleteApi:"/api/admin/comment/delete"})]})}},6155:function(e,n,t){t.d(n,{Z:function(){return s}});var i=t(7462),a=t(8391),r={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M360 184h-8c4.4 0 8-3.6 8-8v8h304v-8c0 4.4 3.6 8 8 8h-8v72h72v-80c0-35.3-28.7-64-64-64H352c-35.3 0-64 28.7-64 64v80h72v-72zm504 72H160c-17.7 0-32 14.3-32 32v32c0 4.4 3.6 8 8 8h60.4l24.7 523c1.6 34.1 29.8 61 63.9 61h454c34.2 0 62.3-26.8 63.9-61l24.7-523H888c4.4 0 8-3.6 8-8v-32c0-17.7-14.3-32-32-32zM731.3 840H292.7l-24.2-512h487l-24.2 512z"}}]},name:"delete",theme:"outlined"},o=t(9345),u=function(e,n){return a.createElement(o.Z,(0,i.Z)({},e,{ref:n,icon:r}))};var s=a.forwardRef(u)}}]);