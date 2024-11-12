"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[981],{5092:function(e,n,t){var r=t(1413),i=t(5861),a=t(9439),o=t(7757),l=t.n(o),s=t(8374),u=t(4576),d=t(5870),c=t(7313),f=t(8475),h=t(2294),p=t(3983),m=t(8783),g=t(8676),v=t(8467),x=t(2135),Z=t(6417);n.Z=function(e){var n=e.deleteApi,t=e.editBtnRender,o=e.addBtnRender,b=e.columns,y=e.datasource,j=e.defaultPageSize,w=e.searchKey,k=e.deleteSuccessCallback,C=e.hideId,z=e.offline,R=(0,v.s0)(),S=(0,v.TH)(),I=function(e,n,t){return H(e,n,t,-1)},H=function(e,n,t,r){var i={};e>1&&(i.page=e),n!=j&&1e6!=n&&(i.size=n),t&&t.trim().length>0&&(i.key=t.trim()),r>0&&(i[m.NJ]=r);var a=(0,h.bI)(i);return 0===a.length?S.pathname:S.pathname+"?"+a},M=function(e,n,t){R(I(e,n,t))},B=function(e,n,t){R(H(e,n,t,(new Date).getTime()))},W=(0,c.useState)({pagination:{page:null!==y&&void 0!==y&&y.page?y.page:1,key:null===y||void 0===y?void 0:y.key,size:null!==y&&void 0!==y&&y.size?null===y||void 0===y?void 0:y.size:j},query:null===y||void 0===y?void 0:y.key,tableLoaded:!0,rows:y?y.rows:[],tablePagination:{total:null===y||void 0===y?void 0:y.totalElements,current:null===y||void 0===y?void 0:y.page,pageSize:null===y||void 0===y?void 0:y.size,onChange:function(e,n){M(e,n,E.query)}}}),q=(0,a.Z)(W,2),E=q[0],N=q[1],P=s.ZP.useMessage({maxCount:3}),V=(0,a.Z)(P,2),F=V[0],T=V[1],D=function(){var e=(0,i.Z)(l().mark((function e(n,t,r){var i;return l().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,f.Z.post(t+"?id="+r);case 2:if(!(i=e.sent).data.error){e.next=6;break}return F.error(i.data.message),e.abrupt("return",!1);case 6:if(0!==i.data.error){e.next=10;break}return F.success((0,m.sR)().deleteSuccess),B(n.page,n.size,E.query),e.abrupt("return",!0);case 10:return e.abrupt("return",!1);case 11:case"end":return e.stop()}}),e)})));return function(n,t,r){return e.apply(this,arguments)}}();(0,c.useEffect)((function(){w!==E.query&&(N((0,r.Z)((0,r.Z)({},E),{},{query:w})),M(1,E.pagination.size,w))}),[w]),(0,c.useEffect)((function(){N((function(e){return(0,r.Z)((0,r.Z)({},e),{},{rows:y?y.rows:[],pagination:{page:null!==y&&void 0!==y&&y.page?y.page:1,size:null!==y&&void 0!==y&&y.size?y.size:10},tablePagination:{current:null===y||void 0===y?void 0:y.page,pageSize:null===y||void 0===y?void 0:y.size,total:null===y||void 0===y?void 0:y.totalElements}})}))}),[y]);return(0,Z.jsxs)(Z.Fragment,{children:[T,o?o((function(){B(E.pagination.page,E.pagination.size,E.query)})):void 0,(0,Z.jsx)(d.Z,{onChange:function(e){M(e.current?e.current:1,e.pageSize?e.pageSize:10,E.query)},style:{minHeight:512},columns:function(){var e=[];return null!==C&&void 0!==C&&C||e.push({title:"ID",dataIndex:"id",key:"id",fixed:!0,width:64,render:function(e){return(0,Z.jsx)("span",{style:{maxWidth:64},children:e})}}),e.push({title:"",dataIndex:"id",key:"action",fixed:!0,width:80,render:function(e,r){return e?(0,Z.jsxs)(u.Z,{size:16,children:[(0,Z.jsx)(x.rU,{to:"#delete-"+r.id,onClick:function(e){e.preventDefault(),e.stopPropagation()},children:(0,Z.jsx)(p.Z,{disabled:z,title:(0,m.sR)().deleteTips,onConfirm:(0,i.Z)(l().mark((function e(){return l().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,D(E.pagination,n,r.id);case 2:e.sent&&k&&k(r.id);case 4:case"end":return e.stop()}}),e)}))),children:(0,Z.jsx)(g.Z,{style:{color:"red"}})})}),t?t(e,r,(function(){B(E.pagination.page,E.pagination.size,E.query)})):null]}):null}}),b.forEach((function(n){e.push(n)})),e}(),pagination:(0,r.Z)((0,r.Z)({hideOnSinglePage:!0},E.tablePagination),{},{itemRender:function(e,n,t){return(0,Z.jsx)(x.rU,{to:I(e,null!==y&&void 0!==y&&y.size?y.size:10,E.query),children:t},e)}}),dataSource:E.rows,scroll:{x:"90vw"}})]})}},3087:function(e,n,t){t.r(n),t.d(n,{default:function(){return I}});var r=t(3781),i=t(7785),a=t(5092),o=t(8783),l=t(1413),s=t(5861),u=t(9439),d=t(7757),c=t.n(d),f=t(7313),h=t(8374),p=t(2834),m=t(5615),g=t(1618),v=t(9624),x=t(1409),Z=t(7696),b=t(4268),y=t(8475),j=t(6417),w={labelCol:{span:4},wrapperCol:{span:20}},k=function(e){var n=e.addSuccessCall,t=e.offline,r=(0,f.useState)(!1),i=(0,u.Z)(r,2),a=i[0],d=i[1],k=(0,f.useState)(),C=(0,u.Z)(k,2),z=C[0],R=C[1],S=h.ZP.useMessage({maxCount:3}),I=(0,u.Z)(S,2),H=I[0],M=I[1];return(0,j.jsxs)(j.Fragment,{children:[M,(0,j.jsx)(p.ZP,{type:"primary",disabled:t,onClick:function(){return d(!0)},style:{marginBottom:8},children:(0,o.sR)().add}),(0,j.jsx)(m.Z,{title:(0,o.sR)().add,open:a,onOk:function(){y.Z.post("/api/admin/nav/add",z).then(function(){var e=(0,s.Z)(c().mark((function e(t){var r;return c().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(r=t.data).error){e.next=5;break}return e.next=4,H.error(r.message);case 4:return e.abrupt("return");case 5:0===r.error&&(d(!1),n());case 6:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}())},onCancel:function(){return d(!1)},children:(0,j.jsxs)(g.Z,(0,l.Z)((0,l.Z)({onValuesChange:function(e,n){R(n)}},w),{},{children:[(0,j.jsx)(b.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,o.sR)()["admin.link.manage"],style:{marginBottom:8},name:"url",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(b.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:"\u5bfc\u822a\u540d\u79f0",style:{marginBottom:8},name:"navName",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(b.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,o.sR)().order,style:{marginBottom:8},name:"sort",rules:[{required:!0,message:""}],children:(0,j.jsx)(Z.Z,{})})})})]}))})]})},C=t(5366),z=t(2135),R={labelCol:{span:4},wrapperCol:{span:20}},S=function(e){var n=e.record,t=e.editSuccessCall,r=e.offline,i=(0,f.useState)(!1),a=(0,u.Z)(i,2),d=a[0],p=a[1],w=(0,f.useState)(n),k=(0,u.Z)(w,2),S=k[0],I=k[1],H=h.ZP.useMessage({maxCount:3}),M=(0,u.Z)(H,2),B=M[0],W=M[1];return(0,f.useEffect)((function(){I(n)}),[n]),(0,j.jsxs)(j.Fragment,{children:[W,(0,j.jsx)(z.rU,{to:"#edit-"+n.id,onClick:function(e){e.preventDefault(),e.stopPropagation(),r||p(!0)},children:(0,j.jsx)(C.Z,{style:{marginBottom:8,color:(0,o.RQ)()}})}),(0,j.jsx)(m.Z,{title:(0,o.sR)().edit,open:d,onOk:function(){y.Z.post("/api/admin/nav/update",S).then(function(){var e=(0,s.Z)(c().mark((function e(n){var r;return c().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(r=n.data).error){e.next=5;break}return e.next=4,B.error(r.message);case 4:return e.abrupt("return");case 5:0===r.error&&(p(!1),t&&t());case 6:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}())},onCancel:function(){return p(!1)},children:(0,j.jsxs)(g.Z,(0,l.Z)((0,l.Z)({initialValues:S,onValuesChange:function(e,n){I(n)}},R),{},{children:[(0,j.jsx)(g.Z.Item,{name:"id",style:{display:"none"},children:(0,j.jsx)(x.Z,{hidden:!0})}),(0,j.jsx)(b.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,o.sR)()["admin.link.manage"],style:{marginBottom:8},name:"url",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(b.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:"\u5bfc\u822a\u540d\u79f0",style:{marginBottom:8},name:"navName",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(b.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,o.sR)().order,style:{marginBottom:8},name:"sort",rules:[{required:!0,message:""}],children:(0,j.jsx)(Z.Z,{})})})})]}))})]})},I=function(e){var n=e.data,t=e.offline;return(0,j.jsxs)(j.Fragment,{children:[(0,j.jsx)(r.Z,{className:"page-header",level:3,children:(0,o.sR)()["admin.nav.manage"]}),(0,j.jsx)(i.Z,{}),(0,j.jsx)(a.Z,{defaultPageSize:10,offline:t,hideId:!0,columns:[{title:(0,o.sR)()["admin.nav.manage"],dataIndex:"url",width:240,key:"url",render:function(e,n){return(0,j.jsx)("a",{style:{display:"inline"},rel:"noopener noreferrer",target:"_blank",href:n.jumpUrl,children:e})}},{title:"\u5bfc\u822a\u540d\u79f0",dataIndex:"navName",key:"navName",width:240},{title:(0,o.sR)().order,key:"sort",dataIndex:"sort",width:60}],addBtnRender:function(e){return(0,j.jsx)(k,{offline:t,addSuccessCall:e})},editBtnRender:function(e,n,r){return(0,j.jsx)(S,{offline:t,record:n,editSuccessCall:r})},datasource:n,deleteApi:"/api/admin/nav/delete"})]})}},5977:function(e,n){n.Z={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M360 184h-8c4.4 0 8-3.6 8-8v8h304v-8c0 4.4 3.6 8 8 8h-8v72h72v-80c0-35.3-28.7-64-64-64H352c-35.3 0-64 28.7-64 64v80h72v-72zm504 72H160c-17.7 0-32 14.3-32 32v32c0 4.4 3.6 8 8 8h60.4l24.7 523c1.6 34.1 29.8 61 63.9 61h454c34.2 0 62.3-26.8 63.9-61l24.7-523H888c4.4 0 8-3.6 8-8v-32c0-17.7-14.3-32-32-32zM731.3 840H292.7l-24.2-512h487l-24.2 512z"}}]},name:"delete",theme:"outlined"}},4143:function(e,n){n.Z={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z"}},{tag:"path",attrs:{d:"M623.6 316.7C593.6 290.4 554 276 512 276s-81.6 14.5-111.6 40.7C369.2 344 352 380.7 352 420v7.6c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8V420c0-44.1 43.1-80 96-80s96 35.9 96 80c0 31.1-22 59.6-56.1 72.7-21.2 8.1-39.2 22.3-52.1 40.9-13.1 19-19.9 41.8-19.9 64.9V620c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8v-22.7a48.3 48.3 0 0130.9-44.8c59-22.7 97.1-74.7 97.1-132.5.1-39.3-17.1-76-48.3-103.3zM472 732a40 40 0 1080 0 40 40 0 10-80 0z"}}]},name:"question-circle",theme:"outlined"}},8676:function(e,n,t){var r=t(7460),i=t(7313),a=t(5977),o=t(8813),l=function(e,n){return i.createElement(o.Z,(0,r.Z)({},e,{ref:n,icon:a.Z}))},s=i.forwardRef(l);n.Z=s},5366:function(e,n,t){var r=t(7460),i=t(7313),a=t(5656),o=t(8813),l=function(e,n){return i.createElement(o.Z,(0,r.Z)({},e,{ref:n,icon:a.Z}))},s=i.forwardRef(l);n.Z=s},4669:function(e,n,t){t.d(n,{Z:function(){return d}});var r=t(7762),i=function(e){return"object"==typeof e&&null!=e&&1===e.nodeType},a=function(e,n){return(!n||"hidden"!==e)&&"visible"!==e&&"clip"!==e},o=function(e,n){if(e.clientHeight<e.scrollHeight||e.clientWidth<e.scrollWidth){var t=getComputedStyle(e,null);return a(t.overflowY,n)||a(t.overflowX,n)||function(e){var n=function(e){if(!e.ownerDocument||!e.ownerDocument.defaultView)return null;try{return e.ownerDocument.defaultView.frameElement}catch(e){return null}}(e);return!!n&&(n.clientHeight<e.scrollHeight||n.clientWidth<e.scrollWidth)}(e)}return!1},l=function(e,n,t,r,i,a,o,l){return a<e&&o>n||a>e&&o<n?0:a<=e&&l<=t||o>=n&&l>=t?a-e-r:o>n&&l<t||a<e&&l>t?o-n+i:0},s=function(e){var n=e.parentElement;return null==n?e.getRootNode().host||null:n},u=function(e,n){var t,r,a,u;if("undefined"==typeof document)return[];var d=n.scrollMode,c=n.block,f=n.inline,h=n.boundary,p=n.skipOverflowHiddenElements,m="function"==typeof h?h:function(e){return e!==h};if(!i(e))throw new TypeError("Invalid target");for(var g=document.scrollingElement||document.documentElement,v=[],x=e;i(x)&&m(x);){if((x=s(x))===g){v.push(x);break}null!=x&&x===document.body&&o(x)&&!o(document.documentElement)||null!=x&&o(x,p)&&v.push(x)}for(var Z=null!=(r=null==(t=window.visualViewport)?void 0:t.width)?r:innerWidth,b=null!=(u=null==(a=window.visualViewport)?void 0:a.height)?u:innerHeight,y=window,j=y.scrollX,w=y.scrollY,k=e.getBoundingClientRect(),C=k.height,z=k.width,R=k.top,S=k.right,I=k.bottom,H=k.left,M="start"===c||"nearest"===c?R:"end"===c?I:R+C/2,B="center"===f?H+z/2:"end"===f?S:H,W=[],q=0;q<v.length;q++){var E=v[q],N=E.getBoundingClientRect(),P=N.height,V=N.width,F=N.top,T=N.right,D=N.bottom,O=N.left;if("if-needed"===d&&R>=0&&H>=0&&I<=b&&S<=Z&&R>=F&&I<=D&&H>=O&&S<=T)return W;var _=getComputedStyle(E),L=parseInt(_.borderLeftWidth,10),U=parseInt(_.borderTopWidth,10),A=parseInt(_.borderRightWidth,10),X=parseInt(_.borderBottomWidth,10),Y=0,J=0,K="offsetWidth"in E?E.offsetWidth-E.clientWidth-L-A:0,Q="offsetHeight"in E?E.offsetHeight-E.clientHeight-U-X:0,G="offsetWidth"in E?0===E.offsetWidth?0:V/E.offsetWidth:0,$="offsetHeight"in E?0===E.offsetHeight?0:P/E.offsetHeight:0;if(g===E)Y="start"===c?M:"end"===c?M-b:"nearest"===c?l(w,w+b,b,U,X,w+M,w+M+C,C):M-b/2,J="start"===f?B:"center"===f?B-Z/2:"end"===f?B-Z:l(j,j+Z,Z,L,A,j+B,j+B+z,z),Y=Math.max(0,Y+w),J=Math.max(0,J+j);else{Y="start"===c?M-F-U:"end"===c?M-D+X+Q:"nearest"===c?l(F,D,P,U,X+Q,M,M+C,C):M-(F+P/2)+Q/2,J="start"===f?B-O-L:"center"===f?B-(O+V/2)+K/2:"end"===f?B-T+A+K:l(O,T,V,L,A+K,B,B+z,z);var ee=E.scrollLeft,ne=E.scrollTop;M+=ne-(Y=Math.max(0,Math.min(ne+Y/$,E.scrollHeight-P/$+Q))),B+=ee-(J=Math.max(0,Math.min(ee+J/G,E.scrollWidth-V/G+K)))}W.push({el:E,top:Y,left:J})}return W};function d(e,n){if(e.isConnected&&function(e){for(var n=e;n&&n.parentNode;){if(n.parentNode===document)return!0;n=n.parentNode instanceof ShadowRoot?n.parentNode.host:n.parentNode}return!1}(e)){var t=function(e){var n=window.getComputedStyle(e);return{top:parseFloat(n.scrollMarginTop)||0,right:parseFloat(n.scrollMarginRight)||0,bottom:parseFloat(n.scrollMarginBottom)||0,left:parseFloat(n.scrollMarginLeft)||0}}(e);if(function(e){return"object"==typeof e&&"function"==typeof e.behavior}(n))return n.behavior(u(e,n));var i,a="boolean"==typeof n||null==n?void 0:n.behavior,o=(0,r.Z)(u(e,function(e){return!1===e?{block:"end",inline:"nearest"}:function(e){return e===Object(e)&&0!==Object.keys(e).length}(e)?e:{block:"start",inline:"nearest"}}(n)));try{for(o.s();!(i=o.n()).done;){var l=i.value,s=l.el,d=l.top,c=l.left,f=d-t.top+t.bottom,h=c-t.left+t.right;s.scroll({top:f,left:h,behavior:a})}}catch(p){o.e(p)}finally{o.f()}}}}}]);