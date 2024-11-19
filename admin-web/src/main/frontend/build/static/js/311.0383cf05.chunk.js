"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[311],{5092:function(e,n,t){var r=t(1413),i=t(5861),a=t(9439),o=t(7757),l=t.n(o),s=t(8374),u=t(4576),d=t(6153),c=t(7313),f=t(8475),p=t(2294),h=t(3983),m=t(8783),g=t(7515),v=t(8467),x=t(2135),Z=t(6417);n.Z=function(e){var n=e.deleteApi,t=e.editBtnRender,o=e.addBtnRender,y=e.columns,b=e.datasource,j=e.defaultPageSize,w=e.searchKey,k=e.deleteSuccessCallback,C=e.hideId,z=e.offline,R=(0,v.s0)(),S=(0,v.TH)(),I=function(e,n,t){return B(e,n,t,-1)},B=function(e,n,t,r){var i={};e>1&&(i.page=e),n!=j&&1e6!=n&&(i.size=n),t&&t.trim().length>0&&(i.key=t.trim()),r>0&&(i[m.NJ]=r);var a=(0,p.bI)(i);return 0===a.length?S.pathname:S.pathname+"?"+a},H=function(e,n,t){R(I(e,n,t))},M=function(e,n,t){R(B(e,n,t,(new Date).getTime()))},W=(0,c.useState)({pagination:{page:null!==b&&void 0!==b&&b.page?b.page:1,key:null===b||void 0===b?void 0:b.key,size:null!==b&&void 0!==b&&b.size?null===b||void 0===b?void 0:b.size:j},query:null===b||void 0===b?void 0:b.key,tableLoaded:!0,rows:b?b.rows:[],tablePagination:{total:null===b||void 0===b?void 0:b.totalElements,current:null===b||void 0===b?void 0:b.page,pageSize:null===b||void 0===b?void 0:b.size,onChange:function(e,n){H(e,n,E.query)}}}),q=(0,a.Z)(W,2),E=q[0],N=q[1],P=s.ZP.useMessage({maxCount:3}),L=(0,a.Z)(P,2),T=L[0],F=L[1],D=function(){var e=(0,i.Z)(l().mark((function e(n,t,r){var i;return l().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,f.Z.post(t+"?id="+r);case 2:if(!(i=e.sent).data.error){e.next=6;break}return T.error(i.data.message),e.abrupt("return",!1);case 6:if(0!==i.data.error){e.next=10;break}return T.success((0,m.sR)().deleteSuccess),M(n.page,n.size,E.query),e.abrupt("return",!0);case 10:return e.abrupt("return",!1);case 11:case"end":return e.stop()}}),e)})));return function(n,t,r){return e.apply(this,arguments)}}();(0,c.useEffect)((function(){w!==E.query&&(N((0,r.Z)((0,r.Z)({},E),{},{query:w})),H(1,E.pagination.size,w))}),[w]),(0,c.useEffect)((function(){N((function(e){return(0,r.Z)((0,r.Z)({},e),{},{rows:b?b.rows:[],pagination:{page:null!==b&&void 0!==b&&b.page?b.page:1,size:null!==b&&void 0!==b&&b.size?b.size:10},tablePagination:{current:null===b||void 0===b?void 0:b.page,pageSize:null===b||void 0===b?void 0:b.size,total:null===b||void 0===b?void 0:b.totalElements}})}))}),[b]);return(0,Z.jsxs)(Z.Fragment,{children:[F,o?o((function(){M(E.pagination.page,E.pagination.size,E.query)})):void 0,(0,Z.jsx)(d.Z,{onChange:function(e){H(e.current?e.current:1,e.pageSize?e.pageSize:10,E.query)},style:{minHeight:512},columns:function(){var e=[];return null!==C&&void 0!==C&&C||e.push({title:"ID",dataIndex:"id",key:"id",fixed:!0,width:64,render:function(e){return(0,Z.jsx)("span",{style:{maxWidth:64},children:e})}}),e.push({title:"",dataIndex:"id",key:"action",fixed:!0,width:80,render:function(e,r){return e?(0,Z.jsxs)(u.Z,{size:16,children:[(0,Z.jsx)(x.rU,{to:"#delete-"+r.id,onClick:function(e){e.preventDefault(),e.stopPropagation()},children:(0,Z.jsx)(h.Z,{disabled:z,title:(0,m.sR)().deleteTips,onConfirm:(0,i.Z)(l().mark((function e(){return l().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,D(E.pagination,n,r.id);case 2:e.sent&&k&&k(r.id);case 4:case"end":return e.stop()}}),e)}))),children:(0,Z.jsx)(g.Z,{style:{color:"red"}})})}),t?t(e,r,(function(){M(E.pagination.page,E.pagination.size,E.query)})):null]}):null}}),y.forEach((function(n){e.push(n)})),e}(),pagination:(0,r.Z)((0,r.Z)({hideOnSinglePage:!0},E.tablePagination),{},{itemRender:function(e,n,t){return(0,Z.jsx)(x.rU,{to:I(e,null!==b&&void 0!==b&&b.size?b.size:10,E.query),children:t},e)}}),dataSource:E.rows,scroll:{x:"90vw"}})]})}},3271:function(e,n,t){t.r(n),t.d(n,{default:function(){return I}});var r=t(3781),i=t(7785),a=t(8783),o=t(5092),l=t(1413),s=t(5861),u=t(9439),d=t(7757),c=t.n(d),f=t(7313),p=t(8374),h=t(2834),m=t(5615),g=t(9418),v=t(9624),x=t(2736),Z=t(4268),y=t(3348),b=t(8475),j=t(6417),w={labelCol:{span:4},wrapperCol:{span:20}},k=function(e){var n=e.addSuccessCall,t=e.offline,r=(0,f.useState)(!1),i=(0,u.Z)(r,2),o=i[0],d=i[1],k=(0,f.useState)(),C=(0,u.Z)(k,2),z=C[0],R=C[1],S=p.ZP.useMessage({maxCount:3}),I=(0,u.Z)(S,2),B=I[0],H=I[1];return(0,j.jsxs)(j.Fragment,{children:[H,(0,j.jsx)(h.ZP,{type:"primary",disabled:t,onClick:function(){return d(!0)},style:{marginBottom:8},children:(0,a.sR)().add}),(0,j.jsx)(m.Z,{title:(0,a.sR)().add,open:o,onOk:function(){b.Z.post("/api/admin/type/add",z).then(function(){var e=(0,s.Z)(c().mark((function e(t){var r;return c().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(r=t.data).error){e.next=5;break}return e.next=4,B.error(r.message);case 4:return e.abrupt("return");case 5:0===r.error&&(d(!1),n());case 6:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}())},onCancel:function(){return d(!1)},children:(0,j.jsxs)(g.Z,(0,l.Z)((0,l.Z)({onValuesChange:function(e,n){R(n)}},w),{},{children:[(0,j.jsx)(Z.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,a.sR)()["admin.type.manage"],style:{marginBottom:8},name:"typeName",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(Z.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,a.sR)().alias,style:{marginBottom:8},name:"alias",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(Z.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:"\u7b80\u4ecb",style:{marginBottom:8},name:"remark",rules:[{required:!0,message:""}],children:(0,j.jsx)(y.Z,{})})})})]}))})]})},C=t(2019),z=t(2135),R={labelCol:{span:4},wrapperCol:{span:20}},S=function(e){var n=e.record,t=e.editSuccessCall,r=e.offline,i=(0,f.useState)(!1),o=(0,u.Z)(i,2),d=o[0],h=o[1],w=(0,f.useState)(n),k=(0,u.Z)(w,2),S=k[0],I=k[1],B=p.ZP.useMessage({maxCount:3}),H=(0,u.Z)(B,2),M=H[0],W=H[1];return(0,f.useEffect)((function(){I(n)}),[n]),(0,j.jsxs)(j.Fragment,{children:[W,(0,j.jsx)(z.rU,{to:"#edit-"+n.id,onClick:function(e){e.preventDefault(),e.stopPropagation(),r||h(!0)},children:(0,j.jsx)(C.Z,{style:{marginBottom:8,color:(0,a.RQ)()}})}),(0,j.jsx)(m.Z,{title:(0,a.sR)().edit,open:d,onOk:function(){b.Z.post("/api/admin/type/update",S).then(function(){var e=(0,s.Z)(c().mark((function e(n){var r;return c().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(r=n.data).error){e.next=5;break}return e.next=4,M.error(r.message);case 4:return e.abrupt("return");case 5:0===r.error&&(h(!1),t&&t());case 6:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}())},onCancel:function(){return h(!1)},children:(0,j.jsxs)(g.Z,(0,l.Z)((0,l.Z)({initialValues:S,onValuesChange:function(e,n){I(n)}},R),{},{children:[(0,j.jsx)(g.Z.Item,{name:"id",style:{display:"none"},children:(0,j.jsx)(x.Z,{hidden:!0})}),(0,j.jsx)(Z.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,a.sR)()["admin.type.manage"],style:{marginBottom:8},name:"typeName",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(Z.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,a.sR)().alias,style:{marginBottom:8},name:"alias",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(Z.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:"\u7b80\u4ecb",style:{marginBottom:8},name:"remark",rules:[{required:!0,message:""}],children:(0,j.jsx)(y.Z,{})})})})]}))})]})},I=function(e){var n=e.data,t=e.offline;return(0,j.jsxs)(j.Fragment,{children:[(0,j.jsx)(r.Z,{className:"page-header",level:3,children:(0,a.sR)()["admin.type.manage"]}),(0,j.jsx)(i.Z,{}),(0,j.jsx)(o.Z,{defaultPageSize:10,offline:t,hideId:!0,columns:[{title:(0,a.sR)()["admin.type.manage"],dataIndex:"typeName",key:"typeName",width:240,render:function(e,n){return(0,j.jsx)("a",{rel:"noopener noreferrer",target:"_blank",href:n.url,children:e})}},{title:(0,a.sR)().alias,dataIndex:"alias",key:"alias",width:120},{title:"\u7b80\u4ecb",key:"remark",dataIndex:"remark",width:240,render:function(e){return(0,j.jsx)("span",{dangerouslySetInnerHTML:{__html:e}})}},{title:"\u6587\u7ae0\u6570\u91cf",dataIndex:"amount",key:"amount",width:80}],addBtnRender:function(e){return(0,j.jsx)(k,{offline:t,addSuccessCall:e})},editBtnRender:function(e,n,r){return(0,j.jsx)(S,{offline:t,record:n,editSuccessCall:r})},datasource:n,deleteApi:"/api/admin/type/delete"})]})}},2019:function(e,n,t){t.d(n,{Z:function(){return s}});var r=t(7460),i=t(7313),a={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M257.7 752c2 0 4-.2 6-.5L431.9 722c2-.4 3.9-1.3 5.3-2.8l423.9-423.9a9.96 9.96 0 000-14.1L694.9 114.9c-1.9-1.9-4.4-2.9-7.1-2.9s-5.2 1-7.1 2.9L256.8 538.8c-1.5 1.5-2.4 3.3-2.8 5.3l-29.5 168.2a33.5 33.5 0 009.4 29.8c6.6 6.4 14.9 9.9 23.8 9.9zm67.4-174.4L687.8 215l73.3 73.3-362.7 362.6-88.9 15.7 15.6-89zM880 836H144c-17.7 0-32 14.3-32 32v36c0 4.4 3.6 8 8 8h784c4.4 0 8-3.6 8-8v-36c0-17.7-14.3-32-32-32z"}}]},name:"edit",theme:"outlined"},o=t(9126),l=function(e,n){return i.createElement(o.Z,(0,r.Z)({},e,{ref:n,icon:a}))};var s=i.forwardRef(l)},4669:function(e,n,t){t.d(n,{Z:function(){return d}});var r=t(7762),i=function(e){return"object"==typeof e&&null!=e&&1===e.nodeType},a=function(e,n){return(!n||"hidden"!==e)&&"visible"!==e&&"clip"!==e},o=function(e,n){if(e.clientHeight<e.scrollHeight||e.clientWidth<e.scrollWidth){var t=getComputedStyle(e,null);return a(t.overflowY,n)||a(t.overflowX,n)||function(e){var n=function(e){if(!e.ownerDocument||!e.ownerDocument.defaultView)return null;try{return e.ownerDocument.defaultView.frameElement}catch(e){return null}}(e);return!!n&&(n.clientHeight<e.scrollHeight||n.clientWidth<e.scrollWidth)}(e)}return!1},l=function(e,n,t,r,i,a,o,l){return a<e&&o>n||a>e&&o<n?0:a<=e&&l<=t||o>=n&&l>=t?a-e-r:o>n&&l<t||a<e&&l>t?o-n+i:0},s=function(e){var n=e.parentElement;return null==n?e.getRootNode().host||null:n},u=function(e,n){var t,r,a,u;if("undefined"==typeof document)return[];var d=n.scrollMode,c=n.block,f=n.inline,p=n.boundary,h=n.skipOverflowHiddenElements,m="function"==typeof p?p:function(e){return e!==p};if(!i(e))throw new TypeError("Invalid target");for(var g=document.scrollingElement||document.documentElement,v=[],x=e;i(x)&&m(x);){if((x=s(x))===g){v.push(x);break}null!=x&&x===document.body&&o(x)&&!o(document.documentElement)||null!=x&&o(x,h)&&v.push(x)}for(var Z=null!=(r=null==(t=window.visualViewport)?void 0:t.width)?r:innerWidth,y=null!=(u=null==(a=window.visualViewport)?void 0:a.height)?u:innerHeight,b=window,j=b.scrollX,w=b.scrollY,k=e.getBoundingClientRect(),C=k.height,z=k.width,R=k.top,S=k.right,I=k.bottom,B=k.left,H="start"===c||"nearest"===c?R:"end"===c?I:R+C/2,M="center"===f?B+z/2:"end"===f?S:B,W=[],q=0;q<v.length;q++){var E=v[q],N=E.getBoundingClientRect(),P=N.height,L=N.width,T=N.top,F=N.right,D=N.bottom,V=N.left;if("if-needed"===d&&R>=0&&B>=0&&I<=y&&S<=Z&&R>=T&&I<=D&&B>=V&&S<=F)return W;var _=getComputedStyle(E),O=parseInt(_.borderLeftWidth,10),U=parseInt(_.borderTopWidth,10),A=parseInt(_.borderRightWidth,10),X=parseInt(_.borderBottomWidth,10),Y=0,J=0,K="offsetWidth"in E?E.offsetWidth-E.clientWidth-O-A:0,Q="offsetHeight"in E?E.offsetHeight-E.clientHeight-U-X:0,G="offsetWidth"in E?0===E.offsetWidth?0:L/E.offsetWidth:0,$="offsetHeight"in E?0===E.offsetHeight?0:P/E.offsetHeight:0;if(g===E)Y="start"===c?H:"end"===c?H-y:"nearest"===c?l(w,w+y,y,U,X,w+H,w+H+C,C):H-y/2,J="start"===f?M:"center"===f?M-Z/2:"end"===f?M-Z:l(j,j+Z,Z,O,A,j+M,j+M+z,z),Y=Math.max(0,Y+w),J=Math.max(0,J+j);else{Y="start"===c?H-T-U:"end"===c?H-D+X+Q:"nearest"===c?l(T,D,P,U,X+Q,H,H+C,C):H-(T+P/2)+Q/2,J="start"===f?M-V-O:"center"===f?M-(V+L/2)+K/2:"end"===f?M-F+A+K:l(V,F,L,O,A+K,M,M+z,z);var ee=E.scrollLeft,ne=E.scrollTop;H+=ne-(Y=Math.max(0,Math.min(ne+Y/$,E.scrollHeight-P/$+Q))),M+=ee-(J=Math.max(0,Math.min(ee+J/G,E.scrollWidth-L/G+K)))}W.push({el:E,top:Y,left:J})}return W};function d(e,n){if(e.isConnected&&function(e){for(var n=e;n&&n.parentNode;){if(n.parentNode===document)return!0;n=n.parentNode instanceof ShadowRoot?n.parentNode.host:n.parentNode}return!1}(e)){var t=function(e){var n=window.getComputedStyle(e);return{top:parseFloat(n.scrollMarginTop)||0,right:parseFloat(n.scrollMarginRight)||0,bottom:parseFloat(n.scrollMarginBottom)||0,left:parseFloat(n.scrollMarginLeft)||0}}(e);if(function(e){return"object"==typeof e&&"function"==typeof e.behavior}(n))return n.behavior(u(e,n));var i,a="boolean"==typeof n||null==n?void 0:n.behavior,o=(0,r.Z)(u(e,function(e){return!1===e?{block:"end",inline:"nearest"}:function(e){return e===Object(e)&&0!==Object.keys(e).length}(e)?e:{block:"start",inline:"nearest"}}(n)));try{for(o.s();!(i=o.n()).done;){var l=i.value,s=l.el,d=l.top,c=l.left,f=d-t.top+t.bottom,p=c-t.left+t.right;s.scroll({top:f,left:p,behavior:a})}}catch(h){o.e(h)}finally{o.f()}}}}}]);