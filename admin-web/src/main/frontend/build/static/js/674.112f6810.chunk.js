"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[674],{65092:function(e,t,n){var r=n(1413),i=n(15861),a=n(29439),o=n(87757),l=n.n(o),s=n(18374),u=n(14576),d=n(15870),c=n(47313),f=n(38475),p=n(82294),h=n(23983),m=n(88783),g=n(98676),v=n(58467),x=n(2135),Z=n(46417);t.Z=function(e){var t=e.deleteApi,n=e.editBtnRender,o=e.addBtnRender,y=e.columns,b=e.datasource,j=e.defaultPageSize,w=e.searchKey,k=e.deleteSuccessCallback,C=e.hideId,z=e.offline,R=(0,v.s0)(),S=(0,v.TH)(),I=function(e,t,n){return M(e,t,n,-1)},M=function(e,t,n,r){var i={};e>1&&(i.page=e),t!=j&&1e6!=t&&(i.size=t),n&&n.trim().length>0&&(i.key=n.trim()),r>0&&(i[m.NJ]=r);var a=(0,p.bI)(i);return 0===a.length?S.pathname:S.pathname+"?"+a},B=function(e,t,n){R(I(e,t,n))},W=function(e,t,n){R(M(e,t,n,(new Date).getTime()))},q=(0,c.useState)({pagination:{page:null!==b&&void 0!==b&&b.page?b.page:1,key:null===b||void 0===b?void 0:b.key,size:null!==b&&void 0!==b&&b.size?null===b||void 0===b?void 0:b.size:j},query:null===b||void 0===b?void 0:b.key,tableLoaded:!0,rows:b?b.rows:[],tablePagination:{total:null===b||void 0===b?void 0:b.totalElements,current:null===b||void 0===b?void 0:b.page,pageSize:null===b||void 0===b?void 0:b.size,onChange:function(e,t){B(e,t,E.query)}}}),H=(0,a.Z)(q,2),E=H[0],N=H[1],P=s.ZP.useMessage({maxCount:3}),T=(0,a.Z)(P,2),V=T[0],F=T[1],D=function(){var e=(0,i.Z)(l().mark((function e(t,n,r){var i;return l().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,f.Z.post(n+"?id="+r);case 2:if(!(i=e.sent).data.error){e.next=6;break}return V.error(i.data.message),e.abrupt("return",!1);case 6:if(0!==i.data.error){e.next=10;break}return V.success((0,m.sR)().deleteSuccess),W(t.page,t.size,E.query),e.abrupt("return",!0);case 10:return e.abrupt("return",!1);case 11:case"end":return e.stop()}}),e)})));return function(t,n,r){return e.apply(this,arguments)}}();(0,c.useEffect)((function(){w!==E.query&&(N((0,r.Z)((0,r.Z)({},E),{},{query:w})),B(1,E.pagination.size,w))}),[w]),(0,c.useEffect)((function(){N((function(e){return(0,r.Z)((0,r.Z)({},e),{},{rows:b?b.rows:[],pagination:{page:null!==b&&void 0!==b&&b.page?b.page:1,size:null!==b&&void 0!==b&&b.size?b.size:10},tablePagination:{current:null===b||void 0===b?void 0:b.page,pageSize:null===b||void 0===b?void 0:b.size,total:null===b||void 0===b?void 0:b.totalElements}})}))}),[b]);return(0,Z.jsxs)(Z.Fragment,{children:[F,o?o((function(){W(E.pagination.page,E.pagination.size,E.query)})):void 0,(0,Z.jsx)(d.Z,{onChange:function(e){B(e.current?e.current:1,e.pageSize?e.pageSize:10,E.query)},style:{minHeight:512},columns:function(){var e=[];return null!==C&&void 0!==C&&C||e.push({title:"ID",dataIndex:"id",key:"id",fixed:!0,width:64,render:function(e){return(0,Z.jsx)("span",{style:{maxWidth:64},children:e})}}),e.push({title:"",dataIndex:"id",key:"action",fixed:!0,width:80,render:function(e,r){return e?(0,Z.jsxs)(u.Z,{size:16,children:[(0,Z.jsx)(x.rU,{to:"#delete-"+r.id,onClick:function(e){e.preventDefault(),e.stopPropagation()},children:(0,Z.jsx)(h.Z,{disabled:z,title:(0,m.sR)().deleteTips,onConfirm:(0,i.Z)(l().mark((function e(){return l().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,D(E.pagination,t,r.id);case 2:e.sent&&k&&k(r.id);case 4:case"end":return e.stop()}}),e)}))),children:(0,Z.jsx)(g.Z,{style:{color:"red"}})})}),n?n(e,r,(function(){W(E.pagination.page,E.pagination.size,E.query)})):null]}):null}}),y.forEach((function(t){e.push(t)})),e}(),pagination:(0,r.Z)((0,r.Z)({hideOnSinglePage:!0},E.tablePagination),{},{itemRender:function(e,t,n){return(0,Z.jsx)(x.rU,{to:I(e,null!==b&&void 0!==b&&b.size?b.size:10,E.query),children:n},e)}}),dataSource:E.rows,scroll:{x:"90vw"}})]})}},93271:function(e,t,n){n.r(t),n.d(t,{default:function(){return I}});var r=n(43781),i=n(87785),a=n(88783),o=n(65092),l=n(1413),s=n(15861),u=n(29439),d=n(87757),c=n.n(d),f=n(47313),p=n(18374),h=n(78939),m=n(55615),g=n(91618),v=n(59624),x=n(51409),Z=n(84268),y=n(33348),b=n(38475),j=n(46417),w={labelCol:{span:4},wrapperCol:{span:20}},k=function(e){var t=e.addSuccessCall,n=e.offline,r=(0,f.useState)(!1),i=(0,u.Z)(r,2),o=i[0],d=i[1],k=(0,f.useState)(),C=(0,u.Z)(k,2),z=C[0],R=C[1],S=p.ZP.useMessage({maxCount:3}),I=(0,u.Z)(S,2),M=I[0],B=I[1];return(0,j.jsxs)(j.Fragment,{children:[B,(0,j.jsx)(h.ZP,{type:"primary",disabled:n,onClick:function(){return d(!0)},style:{marginBottom:8},children:(0,a.sR)().add}),(0,j.jsx)(m.Z,{title:(0,a.sR)().add,open:o,onOk:function(){b.Z.post("/api/admin/type/add",z).then(function(){var e=(0,s.Z)(c().mark((function e(n){var r;return c().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(r=n.data).error){e.next=5;break}return e.next=4,M.error(r.message);case 4:return e.abrupt("return");case 5:0===r.error&&(d(!1),t());case 6:case"end":return e.stop()}}),e)})));return function(t){return e.apply(this,arguments)}}())},onCancel:function(){return d(!1)},children:(0,j.jsxs)(g.Z,(0,l.Z)((0,l.Z)({onValuesChange:function(e,t){R(t)}},w),{},{children:[(0,j.jsx)(Z.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,a.sR)()["admin.type.manage"],style:{marginBottom:8},name:"typeName",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(Z.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,a.sR)().alias,style:{marginBottom:8},name:"alias",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(Z.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:"\u7b80\u4ecb",style:{marginBottom:8},name:"remark",rules:[{required:!0,message:""}],children:(0,j.jsx)(y.Z,{})})})})]}))})]})},C=n(95366),z=n(2135),R={labelCol:{span:4},wrapperCol:{span:20}},S=function(e){var t=e.record,n=e.editSuccessCall,r=e.offline,i=(0,f.useState)(!1),o=(0,u.Z)(i,2),d=o[0],h=o[1],w=(0,f.useState)(t),k=(0,u.Z)(w,2),S=k[0],I=k[1],M=p.ZP.useMessage({maxCount:3}),B=(0,u.Z)(M,2),W=B[0],q=B[1];return(0,f.useEffect)((function(){I(t)}),[t]),(0,j.jsxs)(j.Fragment,{children:[q,(0,j.jsx)(z.rU,{to:"#edit-"+t.id,onClick:function(e){e.preventDefault(),e.stopPropagation(),r||h(!0)},children:(0,j.jsx)(C.Z,{style:{marginBottom:8,color:(0,a.RQ)()}})}),(0,j.jsx)(m.Z,{title:(0,a.sR)().edit,open:d,onOk:function(){b.Z.post("/api/admin/type/update",S).then(function(){var e=(0,s.Z)(c().mark((function e(t){var r;return c().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(r=t.data).error){e.next=5;break}return e.next=4,W.error(r.message);case 4:return e.abrupt("return");case 5:0===r.error&&(h(!1),n&&n());case 6:case"end":return e.stop()}}),e)})));return function(t){return e.apply(this,arguments)}}())},onCancel:function(){return h(!1)},children:(0,j.jsxs)(g.Z,(0,l.Z)((0,l.Z)({initialValues:S,onValuesChange:function(e,t){I(t)}},R),{},{children:[(0,j.jsx)(g.Z.Item,{name:"id",style:{display:"none"},children:(0,j.jsx)(x.Z,{hidden:!0})}),(0,j.jsx)(Z.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,a.sR)()["admin.type.manage"],style:{marginBottom:8},name:"typeName",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(Z.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,a.sR)().alias,style:{marginBottom:8},name:"alias",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(Z.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:"\u7b80\u4ecb",style:{marginBottom:8},name:"remark",rules:[{required:!0,message:""}],children:(0,j.jsx)(y.Z,{})})})})]}))})]})},I=function(e){var t=e.data,n=e.offline;return(0,j.jsxs)(j.Fragment,{children:[(0,j.jsx)(r.Z,{className:"page-header",level:3,children:(0,a.sR)()["admin.type.manage"]}),(0,j.jsx)(i.Z,{}),(0,j.jsx)(o.Z,{defaultPageSize:10,offline:n,hideId:!0,columns:[{title:(0,a.sR)()["admin.type.manage"],dataIndex:"typeName",key:"typeName",width:240,render:function(e,t){return(0,j.jsx)("a",{rel:"noopener noreferrer",target:"_blank",href:t.url,children:e})}},{title:(0,a.sR)().alias,dataIndex:"alias",key:"alias",width:120},{title:"\u7b80\u4ecb",key:"remark",dataIndex:"remark",width:240,render:function(e){return(0,j.jsx)("span",{dangerouslySetInnerHTML:{__html:e}})}},{title:"\u6587\u7ae0\u6570\u91cf",dataIndex:"amount",key:"amount",width:80}],addBtnRender:function(e){return(0,j.jsx)(k,{offline:n,addSuccessCall:e})},editBtnRender:function(e,t,r){return(0,j.jsx)(S,{offline:n,record:t,editSuccessCall:r})},datasource:t,deleteApi:"/api/admin/type/delete"})]})}},84143:function(e,t){t.Z={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z"}},{tag:"path",attrs:{d:"M623.6 316.7C593.6 290.4 554 276 512 276s-81.6 14.5-111.6 40.7C369.2 344 352 380.7 352 420v7.6c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8V420c0-44.1 43.1-80 96-80s96 35.9 96 80c0 31.1-22 59.6-56.1 72.7-21.2 8.1-39.2 22.3-52.1 40.9-13.1 19-19.9 41.8-19.9 64.9V620c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8v-22.7a48.3 48.3 0 0130.9-44.8c59-22.7 97.1-74.7 97.1-132.5.1-39.3-17.1-76-48.3-103.3zM472 732a40 40 0 1080 0 40 40 0 10-80 0z"}}]},name:"question-circle",theme:"outlined"}},95366:function(e,t,n){var r=n(97460),i=n(47313),a=n(95656),o=n(9126),l=function(e,t){return i.createElement(o.Z,(0,r.Z)({},e,{ref:t,icon:a.Z}))},s=i.forwardRef(l);t.Z=s},34669:function(e,t,n){n.d(t,{Z:function(){return d}});var r=n(37762),i=function(e){return"object"==typeof e&&null!=e&&1===e.nodeType},a=function(e,t){return(!t||"hidden"!==e)&&"visible"!==e&&"clip"!==e},o=function(e,t){if(e.clientHeight<e.scrollHeight||e.clientWidth<e.scrollWidth){var n=getComputedStyle(e,null);return a(n.overflowY,t)||a(n.overflowX,t)||function(e){var t=function(e){if(!e.ownerDocument||!e.ownerDocument.defaultView)return null;try{return e.ownerDocument.defaultView.frameElement}catch(e){return null}}(e);return!!t&&(t.clientHeight<e.scrollHeight||t.clientWidth<e.scrollWidth)}(e)}return!1},l=function(e,t,n,r,i,a,o,l){return a<e&&o>t||a>e&&o<t?0:a<=e&&l<=n||o>=t&&l>=n?a-e-r:o>t&&l<n||a<e&&l>n?o-t+i:0},s=function(e){var t=e.parentElement;return null==t?e.getRootNode().host||null:t},u=function(e,t){var n,r,a,u;if("undefined"==typeof document)return[];var d=t.scrollMode,c=t.block,f=t.inline,p=t.boundary,h=t.skipOverflowHiddenElements,m="function"==typeof p?p:function(e){return e!==p};if(!i(e))throw new TypeError("Invalid target");for(var g=document.scrollingElement||document.documentElement,v=[],x=e;i(x)&&m(x);){if((x=s(x))===g){v.push(x);break}null!=x&&x===document.body&&o(x)&&!o(document.documentElement)||null!=x&&o(x,h)&&v.push(x)}for(var Z=null!=(r=null==(n=window.visualViewport)?void 0:n.width)?r:innerWidth,y=null!=(u=null==(a=window.visualViewport)?void 0:a.height)?u:innerHeight,b=window,j=b.scrollX,w=b.scrollY,k=e.getBoundingClientRect(),C=k.height,z=k.width,R=k.top,S=k.right,I=k.bottom,M=k.left,B="start"===c||"nearest"===c?R:"end"===c?I:R+C/2,W="center"===f?M+z/2:"end"===f?S:M,q=[],H=0;H<v.length;H++){var E=v[H],N=E.getBoundingClientRect(),P=N.height,T=N.width,V=N.top,F=N.right,D=N.bottom,_=N.left;if("if-needed"===d&&R>=0&&M>=0&&I<=y&&S<=Z&&R>=V&&I<=D&&M>=_&&S<=F)return q;var O=getComputedStyle(E),L=parseInt(O.borderLeftWidth,10),U=parseInt(O.borderTopWidth,10),A=parseInt(O.borderRightWidth,10),X=parseInt(O.borderBottomWidth,10),Y=0,J=0,K="offsetWidth"in E?E.offsetWidth-E.clientWidth-L-A:0,Q="offsetHeight"in E?E.offsetHeight-E.clientHeight-U-X:0,G="offsetWidth"in E?0===E.offsetWidth?0:T/E.offsetWidth:0,$="offsetHeight"in E?0===E.offsetHeight?0:P/E.offsetHeight:0;if(g===E)Y="start"===c?B:"end"===c?B-y:"nearest"===c?l(w,w+y,y,U,X,w+B,w+B+C,C):B-y/2,J="start"===f?W:"center"===f?W-Z/2:"end"===f?W-Z:l(j,j+Z,Z,L,A,j+W,j+W+z,z),Y=Math.max(0,Y+w),J=Math.max(0,J+j);else{Y="start"===c?B-V-U:"end"===c?B-D+X+Q:"nearest"===c?l(V,D,P,U,X+Q,B,B+C,C):B-(V+P/2)+Q/2,J="start"===f?W-_-L:"center"===f?W-(_+T/2)+K/2:"end"===f?W-F+A+K:l(_,F,T,L,A+K,W,W+z,z);var ee=E.scrollLeft,te=E.scrollTop;B+=te-(Y=Math.max(0,Math.min(te+Y/$,E.scrollHeight-P/$+Q))),W+=ee-(J=Math.max(0,Math.min(ee+J/G,E.scrollWidth-T/G+K)))}q.push({el:E,top:Y,left:J})}return q};function d(e,t){if(e.isConnected&&function(e){for(var t=e;t&&t.parentNode;){if(t.parentNode===document)return!0;t=t.parentNode instanceof ShadowRoot?t.parentNode.host:t.parentNode}return!1}(e)){var n=function(e){var t=window.getComputedStyle(e);return{top:parseFloat(t.scrollMarginTop)||0,right:parseFloat(t.scrollMarginRight)||0,bottom:parseFloat(t.scrollMarginBottom)||0,left:parseFloat(t.scrollMarginLeft)||0}}(e);if(function(e){return"object"==typeof e&&"function"==typeof e.behavior}(t))return t.behavior(u(e,t));var i,a="boolean"==typeof t||null==t?void 0:t.behavior,o=(0,r.Z)(u(e,function(e){return!1===e?{block:"end",inline:"nearest"}:function(e){return e===Object(e)&&0!==Object.keys(e).length}(e)?e:{block:"start",inline:"nearest"}}(t)));try{for(o.s();!(i=o.n()).done;){var l=i.value,s=l.el,d=l.top,c=l.left,f=d-n.top+n.bottom,p=c-n.left+n.right;s.scroll({top:f,left:p,behavior:a})}}catch(h){o.e(h)}finally{o.f()}}}}}]);