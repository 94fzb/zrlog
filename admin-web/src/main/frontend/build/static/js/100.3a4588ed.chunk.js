"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[100],{5092:function(e,n,t){var r=t(1413),a=t(5861),i=t(9439),s=t(7757),l=t.n(s),o=t(8374),u=t(4576),d=t(6153),c=t(7313),f=t(8475),m=t(2294),p=t(3983),h=t(8783),x=t(7515),g=t(8467),Z=t(2135),v=t(6417);n.Z=function(e){var n=e.deleteApi,t=e.editBtnRender,s=e.addBtnRender,j=e.columns,y=e.datasource,k=e.defaultPageSize,b=e.searchKey,w=e.deleteSuccessCallback,z=e.hideId,C=e.offline,I=(0,g.s0)(),R=(0,g.TH)(),S=function(e,n,t){return q(e,n,t,-1)},q=function(e,n,t,r){var a={};e>1&&(a.page=e),n!=k&&1e6!=n&&(a.size=n),t&&t.trim().length>0&&(a.key=t.trim()),r>0&&(a[h.NJ]=r);var i=(0,m.bI)(a);return 0===i.length?R.pathname:R.pathname+"?"+i},B=function(e,n,t){I(S(e,n,t))},P=function(e,n,t){I(q(e,n,t,(new Date).getTime()))},E=(0,c.useState)({pagination:{page:null!==y&&void 0!==y&&y.page?y.page:1,key:null===y||void 0===y?void 0:y.key,size:null!==y&&void 0!==y&&y.size?null===y||void 0===y?void 0:y.size:k},query:null===y||void 0===y?void 0:y.key,tableLoaded:!0,rows:y?y.rows:[],tablePagination:{total:null===y||void 0===y?void 0:y.totalElements,current:null===y||void 0===y?void 0:y.page,pageSize:null===y||void 0===y?void 0:y.size,onChange:function(e,n){B(e,n,M.query)}}}),H=(0,i.Z)(E,2),M=H[0],_=H[1],L=o.ZP.useMessage({maxCount:3}),N=(0,i.Z)(L,2),D=N[0],F=N[1],T=function(){var e=(0,a.Z)(l().mark((function e(n,t,r){var a;return l().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,f.Z.post(t+"?id="+r);case 2:if(!(a=e.sent).data.error){e.next=6;break}return D.error(a.data.message),e.abrupt("return",!1);case 6:if(0!==a.data.error){e.next=10;break}return D.success((0,h.sR)().deleteSuccess),P(n.page,n.size,M.query),e.abrupt("return",!0);case 10:return e.abrupt("return",!1);case 11:case"end":return e.stop()}}),e)})));return function(n,t,r){return e.apply(this,arguments)}}();(0,c.useEffect)((function(){b!==M.query&&(_((0,r.Z)((0,r.Z)({},M),{},{query:b})),B(1,M.pagination.size,b))}),[b]),(0,c.useEffect)((function(){_((function(e){return(0,r.Z)((0,r.Z)({},e),{},{rows:y?y.rows:[],pagination:{page:null!==y&&void 0!==y&&y.page?y.page:1,size:null!==y&&void 0!==y&&y.size?y.size:10},tablePagination:{current:null===y||void 0===y?void 0:y.page,pageSize:null===y||void 0===y?void 0:y.size,total:null===y||void 0===y?void 0:y.totalElements}})}))}),[y]);return(0,v.jsxs)(v.Fragment,{children:[F,s?s((function(){P(M.pagination.page,M.pagination.size,M.query)})):void 0,(0,v.jsx)(d.Z,{onChange:function(e){B(e.current?e.current:1,e.pageSize?e.pageSize:10,M.query)},style:{minHeight:512},columns:function(){var e=[];return null!==z&&void 0!==z&&z||e.push({title:"ID",dataIndex:"id",key:"id",fixed:!0,width:64,render:function(e){return(0,v.jsx)("span",{style:{maxWidth:64},children:e})}}),e.push({title:"",dataIndex:"id",key:"action",fixed:!0,width:80,render:function(e,r){return e?(0,v.jsxs)(u.Z,{size:16,children:[(0,v.jsx)(Z.rU,{to:"#delete-"+r.id,onClick:function(e){e.preventDefault(),e.stopPropagation()},children:(0,v.jsx)(p.Z,{disabled:C,title:(0,h.sR)().deleteTips,onConfirm:(0,a.Z)(l().mark((function e(){return l().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,T(M.pagination,n,r.id);case 2:e.sent&&w&&w(r.id);case 4:case"end":return e.stop()}}),e)}))),children:(0,v.jsx)(x.Z,{style:{color:"red"}})})}),t?t(e,r,(function(){P(M.pagination.page,M.pagination.size,M.query)})):null]}):null}}),j.forEach((function(n){e.push(n)})),e}(),pagination:(0,r.Z)((0,r.Z)({hideOnSinglePage:!0},M.tablePagination),{},{itemRender:function(e,n,t){return(0,v.jsx)(Z.rU,{to:S(e,null!==y&&void 0!==y&&y.size?y.size:10,M.query),children:t},e)}}),dataSource:M.rows,scroll:{x:"90vw"}})]})}},1100:function(e,n,t){t.r(n),t.d(n,{default:function(){return q}});var r=t(3781),a=t(7785),i=t(5092),s=t(8783),l=t(1413),o=t(5861),u=t(9439),d=t(7757),c=t.n(d),f=t(7313),m=t(8374),p=t(5615),h=t(9418),x=t(2736),g=t(9624),Z=t(3671),v=t(4268),j=t(3348),y=t(2019),k=t(2135),b=t(8475),w=t(6417),z={labelCol:{span:4},wrapperCol:{span:20}},C=function(e){var n=e.record,t=e.editSuccessCall,r=e.offline,a=(0,f.useState)(!1),i=(0,u.Z)(a,2),d=i[0],C=i[1],I=(0,f.useState)(n),R=(0,u.Z)(I,2),S=R[0],q=R[1],B=m.ZP.useMessage({maxCount:3}),P=(0,u.Z)(B,2),E=P[0],H=P[1];(0,f.useEffect)((function(){q(n)}),[n]);return(0,w.jsxs)(w.Fragment,{children:[H,(0,w.jsx)(k.rU,{to:"#edit-"+n.id,onClick:function(e){e.preventDefault(),e.stopPropagation(),r||C(!0)},children:(0,w.jsx)(y.Z,{style:{marginBottom:8,color:(0,s.RQ)()}})}),(0,w.jsx)(p.Z,{title:(0,s.sR)().edit,open:d,onOk:function(){b.Z.post("/api/admin/link/update",S).then(function(){var e=(0,o.Z)(c().mark((function e(n){var r;return c().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(r=n.data).error){e.next=5;break}return e.next=4,E.error(r.message);case 4:return e.abrupt("return");case 5:0===r.error&&(C(!1),t&&t());case 6:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}())},onCancel:function(){return C(!1)},children:(0,w.jsxs)(h.Z,(0,l.Z)((0,l.Z)({initialValues:S,onValuesChange:function(e,n){q(n)}},z),{},{children:[(0,w.jsx)(h.Z.Item,{name:"id",style:{display:"none"},children:(0,w.jsx)(x.Z,{hidden:!0})}),(0,w.jsx)(v.Z,{children:(0,w.jsx)(g.Z,{span:24,children:(0,w.jsx)(h.Z.Item,{label:(0,s.sR)()["admin.link.manage"],style:{marginBottom:8},name:"url",rules:[{required:!0,message:""}],children:(0,w.jsx)(x.Z,{})})})}),(0,w.jsx)(v.Z,{children:(0,w.jsx)(g.Z,{span:24,children:(0,w.jsx)(h.Z.Item,{label:"\u7f51\u7ad9\u540d\u79f0",style:{marginBottom:8},name:"linkName",rules:[{required:!0,message:""}],children:(0,w.jsx)(x.Z,{})})})}),(0,w.jsx)(v.Z,{children:(0,w.jsx)(g.Z,{span:24,children:(0,w.jsx)(h.Z.Item,{label:"\u63cf\u8ff0",style:{marginBottom:8},name:"alt",rules:[{required:!0,message:""}],children:(0,w.jsx)(j.Z,{})})})}),(0,w.jsx)(v.Z,{children:(0,w.jsx)(g.Z,{span:24,children:(0,w.jsx)(h.Z.Item,{label:(0,s.sR)().order,style:{marginBottom:8},name:"sort",rules:[{required:!0,message:""}],children:(0,w.jsx)(Z.Z,{})})})})]}))})]})},I=t(2834),R={labelCol:{span:4},wrapperCol:{span:20}},S=function(e){var n=e.addSuccessCall,t=e.offline,r=(0,f.useState)(!1),a=(0,u.Z)(r,2),i=a[0],d=a[1],y=(0,f.useState)(),k=(0,u.Z)(y,2),z=k[0],C=k[1],S=m.ZP.useMessage({maxCount:3}),q=(0,u.Z)(S,2),B=q[0],P=q[1];return(0,w.jsxs)(w.Fragment,{children:[P,(0,w.jsx)(I.ZP,{type:"primary",disabled:t,onClick:function(){return d(!0)},style:{marginBottom:8},children:(0,s.sR)().add}),(0,w.jsx)(p.Z,{title:(0,s.sR)().add,open:i,onOk:function(){b.Z.post("/api/admin/link/add",z).then(function(){var e=(0,o.Z)(c().mark((function e(t){var r;return c().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(r=t.data).error){e.next=5;break}return e.next=4,B.error(r.message);case 4:return e.abrupt("return");case 5:0===r.error&&(d(!1),n());case 6:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}())},onCancel:function(){return d(!1)},children:(0,w.jsxs)(h.Z,(0,l.Z)((0,l.Z)({onValuesChange:function(e,n){C(n)}},R),{},{children:[(0,w.jsx)(v.Z,{children:(0,w.jsx)(g.Z,{span:24,children:(0,w.jsx)(h.Z.Item,{label:(0,s.sR)()["admin.link.manage"],style:{marginBottom:8},name:"url",rules:[{required:!0,message:""}],children:(0,w.jsx)(x.Z,{})})})}),(0,w.jsx)(v.Z,{children:(0,w.jsx)(g.Z,{span:24,children:(0,w.jsx)(h.Z.Item,{label:"\u7f51\u7ad9\u540d\u79f0",style:{marginBottom:8},name:"linkName",rules:[{required:!0,message:""}],children:(0,w.jsx)(x.Z,{})})})}),(0,w.jsx)(v.Z,{children:(0,w.jsx)(g.Z,{span:24,children:(0,w.jsx)(h.Z.Item,{label:"\u63cf\u8ff0",style:{marginBottom:8},name:"alt",rules:[{required:!0,message:""}],children:(0,w.jsx)(j.Z,{})})})}),(0,w.jsx)(v.Z,{children:(0,w.jsx)(g.Z,{span:24,children:(0,w.jsx)(h.Z.Item,{label:(0,s.sR)().order,style:{marginBottom:8},name:"sort",rules:[{required:!0,message:""}],children:(0,w.jsx)(Z.Z,{})})})})]}))})]})},q=function(e){var n=e.data,t=e.offline;return(0,w.jsxs)(w.Fragment,{children:[(0,w.jsx)(r.Z,{className:"page-header",level:3,children:(0,s.sR)()["admin.link.manage"]}),(0,w.jsx)(a.Z,{}),(0,w.jsx)(i.Z,{defaultPageSize:10,offline:t,hideId:!0,columns:[{title:(0,s.sR)()["admin.link.manage"],dataIndex:"url",key:"url",width:240,render:function(e){return(0,w.jsx)("a",{style:{display:"inline"},rel:"noopener noreferrer",target:"_blank",href:e,children:e})}},{title:"\u7f51\u7ad9\u540d\u79f0",key:"linkName",dataIndex:"linkName",width:240,render:function(e){return(0,w.jsx)("span",{dangerouslySetInnerHTML:{__html:e}})}},{title:"\u63cf\u8ff0",key:"alt",dataIndex:"alt",width:240},{title:(0,s.sR)().order,key:"sort",dataIndex:"sort",width:60}],addBtnRender:function(e){return(0,w.jsx)(S,{offline:t,addSuccessCall:e})},datasource:n,editBtnRender:function(e,n,r){return(0,w.jsx)(C,{offline:t,record:n,editSuccessCall:r})},deleteApi:"/api/admin/link/delete"})]})}},7515:function(e,n,t){t.d(n,{Z:function(){return o}});var r=t(7460),a=t(7313),i={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M360 184h-8c4.4 0 8-3.6 8-8v8h304v-8c0 4.4 3.6 8 8 8h-8v72h72v-80c0-35.3-28.7-64-64-64H352c-35.3 0-64 28.7-64 64v80h72v-72zm504 72H160c-17.7 0-32 14.3-32 32v32c0 4.4 3.6 8 8 8h60.4l24.7 523c1.6 34.1 29.8 61 63.9 61h454c34.2 0 62.3-26.8 63.9-61l24.7-523H888c4.4 0 8-3.6 8-8v-32c0-17.7-14.3-32-32-32zM731.3 840H292.7l-24.2-512h487l-24.2 512z"}}]},name:"delete",theme:"outlined"},s=t(8813),l=function(e,n){return a.createElement(s.Z,(0,r.Z)({},e,{ref:n,icon:i}))};var o=a.forwardRef(l)},2019:function(e,n,t){t.d(n,{Z:function(){return o}});var r=t(7460),a=t(7313),i={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M257.7 752c2 0 4-.2 6-.5L431.9 722c2-.4 3.9-1.3 5.3-2.8l423.9-423.9a9.96 9.96 0 000-14.1L694.9 114.9c-1.9-1.9-4.4-2.9-7.1-2.9s-5.2 1-7.1 2.9L256.8 538.8c-1.5 1.5-2.4 3.3-2.8 5.3l-29.5 168.2a33.5 33.5 0 009.4 29.8c6.6 6.4 14.9 9.9 23.8 9.9zm67.4-174.4L687.8 215l73.3 73.3-362.7 362.6-88.9 15.7 15.6-89zM880 836H144c-17.7 0-32 14.3-32 32v36c0 4.4 3.6 8 8 8h784c4.4 0 8-3.6 8-8v-36c0-17.7-14.3-32-32-32z"}}]},name:"edit",theme:"outlined"},s=t(8813),l=function(e,n){return a.createElement(s.Z,(0,r.Z)({},e,{ref:n,icon:i}))};var o=a.forwardRef(l)}}]);