"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[538],{65092:function(e,n,t){var r=t(1413),i=t(15861),a=t(29439),o=t(87757),l=t.n(o),s=t(18374),u=t(14576),d=t(62146),c=t(47313),f=t(38475),h=t(82294),p=t(23983),m=t(88783),g=t(98676),v=t(58467),x=t(2135),Z=t(46417);n.Z=function(e){var n=e.deleteApi,t=e.editBtnRender,o=e.addBtnRender,b=e.columns,y=e.datasource,j=e.defaultPageSize,w=e.searchKey,C=e.deleteSuccessCallback,k=e.hideId,z=e.offline,S=(0,v.s0)(),R=(0,v.TH)(),M=function(e,n,t,r){return I(e,n,t,-1,r)},I=function(e,n,t,r,i){var a={};e>1&&(a.page=e),n!=j&&1e6!=n&&(a.size=n),t&&t.trim().length>0&&(a.key=t.trim()),r>0&&(a[m.NJ]=r),i.length>0&&(a.sort=i[0]);var o=(0,h.bI)(a);return console.info(o+"===<"),0===o.length?R.pathname:R.pathname+"?"+o},q=function(e,n,t,r){S(M(e,n,t,r))},B=function(e,n,t,r){S(I(e,n,t,(new Date).getTime(),r))},W=(0,c.useState)({pagination:{page:null!==y&&void 0!==y&&y.page?y.page:1,key:null===y||void 0===y?void 0:y.key,sort:null!==y&&void 0!==y&&y.sort?null===y||void 0===y?void 0:y.sort:[],size:null!==y&&void 0!==y&&y.size?null===y||void 0===y?void 0:y.size:j},query:null===y||void 0===y?void 0:y.key,tableLoaded:!0,rows:y?y.rows:[],tablePagination:{total:null===y||void 0===y?void 0:y.totalElements,current:null===y||void 0===y?void 0:y.page,pageSize:null===y||void 0===y?void 0:y.size,onChange:function(e,n){q(e,n,H.query,[])}}}),E=(0,a.Z)(W,2),H=E[0],N=E[1],P=s.ZP.useMessage({maxCount:3}),L=(0,a.Z)(P,2),V=L[0],D=L[1],F=function(){var e=(0,i.Z)(l().mark((function e(n,t,r){var i;return l().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,f.Z.post(t+"?id="+r);case 2:if(!(i=e.sent).data.error){e.next=6;break}return V.error(i.data.message),e.abrupt("return",!1);case 6:if(0!==i.data.error){e.next=10;break}return V.success((0,m.sR)().deleteSuccess),B(n.page,n.size,H.query,H.pagination.sort),e.abrupt("return",!0);case 10:return e.abrupt("return",!1);case 11:case"end":return e.stop()}}),e)})));return function(n,t,r){return e.apply(this,arguments)}}();(0,c.useEffect)((function(){w!==H.query&&(N((0,r.Z)((0,r.Z)({},H),{},{query:w})),q(1,H.pagination.size,w,H.pagination.sort))}),[w]),(0,c.useEffect)((function(){N((function(e){return(0,r.Z)((0,r.Z)({},e),{},{rows:y?y.rows:[],pagination:{page:null!==y&&void 0!==y&&y.page?y.page:1,size:null!==y&&void 0!==y&&y.size?y.size:10,sort:null!==y&&void 0!==y&&y.sort?y.sort:[]},tablePagination:{current:null===y||void 0===y?void 0:y.page,pageSize:null===y||void 0===y?void 0:y.size,total:null===y||void 0===y?void 0:y.totalElements}})}))}),[y]);return(0,Z.jsxs)(Z.Fragment,{children:[D,o?o((function(){B(H.pagination.page,H.pagination.size,H.query,H.pagination.sort)})):void 0,(0,Z.jsx)(d.Z,{onChange:function(e,n,t){var i=t&&t.field?[t.field+","+("descend"===t.order?"DESC":"ASC")]:[];i.length>0&&N((0,r.Z)((0,r.Z)({},H),{},{pagination:(0,r.Z)((0,r.Z)({},H.pagination),{},{sort:i})})),q(e.current?e.current:1,e.pageSize?e.pageSize:10,H.query,i)},style:{minHeight:512},columns:function(){var e=[];return null!==k&&void 0!==k&&k||e.push({title:"ID",dataIndex:"id",key:"id",fixed:!0,width:64,render:function(e){return(0,Z.jsx)("span",{style:{maxWidth:64},children:e})}}),e.push({title:"",dataIndex:"id",key:"action",fixed:!0,width:80,render:function(e,r){return e?(0,Z.jsxs)(u.Z,{size:16,children:[(0,Z.jsx)(x.rU,{to:"#delete-"+r.id,onClick:function(e){e.preventDefault(),e.stopPropagation()},children:(0,Z.jsx)(p.Z,{disabled:z,title:(0,m.sR)().deleteTips,onConfirm:(0,i.Z)(l().mark((function e(){return l().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,F(H.pagination,n,r.id);case 2:e.sent&&C&&C(r.id);case 4:case"end":return e.stop()}}),e)}))),children:(0,Z.jsx)(g.Z,{style:{color:"red"}})})}),t?t(e,r,(function(){B(H.pagination.page,H.pagination.size,H.query,H.pagination.sort)})):null]}):null}}),b.forEach((function(n){e.push(n)})),e}(),pagination:(0,r.Z)((0,r.Z)({hideOnSinglePage:!0},H.tablePagination),{},{itemRender:function(e,n,t){return(0,Z.jsx)(x.rU,{to:M(e,null!==y&&void 0!==y&&y.size?y.size:10,H.query,null!==y&&void 0!==y&&y.sort?y.sort:[]),children:t},e)}}),dataSource:H.rows,scroll:{x:"90vw"}})]})}},13087:function(e,n,t){t.r(n),t.d(n,{default:function(){return M}});var r=t(43781),i=t(87785),a=t(65092),o=t(88783),l=t(1413),s=t(15861),u=t(29439),d=t(87757),c=t.n(d),f=t(47313),h=t(18374),p=t(26303),m=t(55615),g=t(91618),v=t(59624),x=t(51409),Z=t(87696),b=t(66222),y=t(38475),j=t(46417),w={labelCol:{span:4},wrapperCol:{span:20}},C=function(e){var n=e.addSuccessCall,t=e.offline,r=(0,f.useState)(!1),i=(0,u.Z)(r,2),a=i[0],d=i[1],C=(0,f.useState)(),k=(0,u.Z)(C,2),z=k[0],S=k[1],R=h.ZP.useMessage({maxCount:3}),M=(0,u.Z)(R,2),I=M[0],q=M[1];return(0,j.jsxs)(j.Fragment,{children:[q,(0,j.jsx)(p.ZP,{type:"primary",disabled:t,onClick:function(){return d(!0)},style:{marginBottom:8},children:(0,o.sR)().add}),(0,j.jsx)(m.Z,{title:(0,o.sR)().add,open:a,onOk:function(){y.Z.post("/api/admin/nav/add",z).then(function(){var e=(0,s.Z)(c().mark((function e(t){var r;return c().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(r=t.data).error){e.next=5;break}return e.next=4,I.error(r.message);case 4:return e.abrupt("return");case 5:0===r.error&&(d(!1),n());case 6:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}())},onCancel:function(){return d(!1)},children:(0,j.jsxs)(g.Z,(0,l.Z)((0,l.Z)({onValuesChange:function(e,n){S(n)}},w),{},{children:[(0,j.jsx)(b.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,o.sR)()["admin.link.manage"],style:{marginBottom:8},name:"url",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(b.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:"\u5bfc\u822a\u540d\u79f0",style:{marginBottom:8},name:"navName",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(b.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,o.sR)().order,style:{marginBottom:8},name:"sort",rules:[{required:!0,message:""}],children:(0,j.jsx)(Z.Z,{})})})})]}))})]})},k=t(95366),z=t(2135),S={labelCol:{span:4},wrapperCol:{span:20}},R=function(e){var n=e.record,t=e.editSuccessCall,r=e.offline,i=(0,f.useState)(!1),a=(0,u.Z)(i,2),d=a[0],p=a[1],w=(0,f.useState)(n),C=(0,u.Z)(w,2),R=C[0],M=C[1],I=h.ZP.useMessage({maxCount:3}),q=(0,u.Z)(I,2),B=q[0],W=q[1];return(0,f.useEffect)((function(){M(n)}),[n]),(0,j.jsxs)(j.Fragment,{children:[W,(0,j.jsx)(z.rU,{to:"#edit-"+n.id,onClick:function(e){e.preventDefault(),e.stopPropagation(),r||p(!0)},children:(0,j.jsx)(k.Z,{style:{marginBottom:8,color:(0,o.RQ)()}})}),(0,j.jsx)(m.Z,{title:(0,o.sR)().edit,open:d,onOk:function(){y.Z.post("/api/admin/nav/update",R).then(function(){var e=(0,s.Z)(c().mark((function e(n){var r;return c().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(r=n.data).error){e.next=5;break}return e.next=4,B.error(r.message);case 4:return e.abrupt("return");case 5:0===r.error&&(p(!1),t&&t());case 6:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}())},onCancel:function(){return p(!1)},children:(0,j.jsxs)(g.Z,(0,l.Z)((0,l.Z)({initialValues:R,onValuesChange:function(e,n){M(n)}},S),{},{children:[(0,j.jsx)(g.Z.Item,{name:"id",style:{display:"none"},children:(0,j.jsx)(x.Z,{hidden:!0})}),(0,j.jsx)(b.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,o.sR)()["admin.link.manage"],style:{marginBottom:8},name:"url",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(b.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:"\u5bfc\u822a\u540d\u79f0",style:{marginBottom:8},name:"navName",rules:[{required:!0,message:""}],children:(0,j.jsx)(x.Z,{})})})}),(0,j.jsx)(b.Z,{children:(0,j.jsx)(v.Z,{span:24,children:(0,j.jsx)(g.Z.Item,{label:(0,o.sR)().order,style:{marginBottom:8},name:"sort",rules:[{required:!0,message:""}],children:(0,j.jsx)(Z.Z,{})})})})]}))})]})},M=function(e){var n=e.data,t=e.offline;return(0,j.jsxs)(j.Fragment,{children:[(0,j.jsx)(r.Z,{className:"page-header",level:3,children:(0,o.sR)()["admin.nav.manage"]}),(0,j.jsx)(i.Z,{}),(0,j.jsx)(a.Z,{defaultPageSize:10,offline:t,hideId:!0,columns:[{title:(0,o.sR)()["admin.nav.manage"],dataIndex:"url",width:240,key:"url",render:function(e,n){return(0,j.jsx)("a",{style:{display:"inline"},rel:"noopener noreferrer",target:"_blank",href:n.jumpUrl,children:e})}},{title:"\u5bfc\u822a\u540d\u79f0",dataIndex:"navName",key:"navName",width:240},{title:(0,o.sR)().order,key:"sort",dataIndex:"sort",width:60}],addBtnRender:function(e){return(0,j.jsx)(C,{offline:t,addSuccessCall:e})},editBtnRender:function(e,n,r){return(0,j.jsx)(R,{offline:t,record:n,editSuccessCall:r})},datasource:n,deleteApi:"/api/admin/nav/delete"})]})}},56997:function(e,n){n.Z={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M942.2 486.2Q889.47 375.11 816.7 305l-50.88 50.88C807.31 395.53 843.45 447.4 874.7 512 791.5 684.2 673.4 766 512 766q-72.67 0-133.87-22.38L323 798.75Q408 838 512 838q288.3 0 430.2-300.3a60.29 60.29 0 000-51.5zm-63.57-320.64L836 122.88a8 8 0 00-11.32 0L715.31 232.2Q624.86 186 512 186q-288.3 0-430.2 300.3a60.3 60.3 0 000 51.5q56.69 119.4 136.5 191.41L112.48 835a8 8 0 000 11.31L155.17 889a8 8 0 0011.31 0l712.15-712.12a8 8 0 000-11.32zM149.3 512C232.6 339.8 350.7 258 512 258c54.54 0 104.13 9.36 149.12 28.39l-70.3 70.3a176 176 0 00-238.13 238.13l-83.42 83.42C223.1 637.49 183.3 582.28 149.3 512zm246.7 0a112.11 112.11 0 01146.2-106.69L401.31 546.2A112 112 0 01396 512z"}},{tag:"path",attrs:{d:"M508 624c-3.46 0-6.87-.16-10.25-.47l-52.82 52.82a176.09 176.09 0 00227.42-227.42l-52.82 52.82c.31 3.38.47 6.79.47 10.25a111.94 111.94 0 01-112 112z"}}]},name:"eye-invisible",theme:"outlined"}},18822:function(e,n){n.Z={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M942.2 486.2C847.4 286.5 704.1 186 512 186c-192.2 0-335.4 100.5-430.2 300.3a60.3 60.3 0 000 51.5C176.6 737.5 319.9 838 512 838c192.2 0 335.4-100.5 430.2-300.3 7.7-16.2 7.7-35 0-51.5zM512 766c-161.3 0-279.4-81.8-362.7-254C232.6 339.8 350.7 258 512 258c161.3 0 279.4 81.8 362.7 254C791.5 684.2 673.4 766 512 766zm-4-430c-97.2 0-176 78.8-176 176s78.8 176 176 176 176-78.8 176-176-78.8-176-176-176zm0 288c-61.9 0-112-50.1-112-112s50.1-112 112-112 112 50.1 112 112-50.1 112-112 112z"}}]},name:"eye",theme:"outlined"}},84143:function(e,n){n.Z={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z"}},{tag:"path",attrs:{d:"M623.6 316.7C593.6 290.4 554 276 512 276s-81.6 14.5-111.6 40.7C369.2 344 352 380.7 352 420v7.6c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8V420c0-44.1 43.1-80 96-80s96 35.9 96 80c0 31.1-22 59.6-56.1 72.7-21.2 8.1-39.2 22.3-52.1 40.9-13.1 19-19.9 41.8-19.9 64.9V620c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8v-22.7a48.3 48.3 0 0130.9-44.8c59-22.7 97.1-74.7 97.1-132.5.1-39.3-17.1-76-48.3-103.3zM472 732a40 40 0 1080 0 40 40 0 10-80 0z"}}]},name:"question-circle",theme:"outlined"}},95366:function(e,n,t){var r=t(97460),i=t(47313),a=t(95656),o=t(9126),l=function(e,n){return i.createElement(o.Z,(0,r.Z)({},e,{ref:n,icon:a.Z}))},s=i.forwardRef(l);n.Z=s},34669:function(e,n,t){t.d(n,{Z:function(){return d}});var r=t(37762),i=function(e){return"object"==typeof e&&null!=e&&1===e.nodeType},a=function(e,n){return(!n||"hidden"!==e)&&"visible"!==e&&"clip"!==e},o=function(e,n){if(e.clientHeight<e.scrollHeight||e.clientWidth<e.scrollWidth){var t=getComputedStyle(e,null);return a(t.overflowY,n)||a(t.overflowX,n)||function(e){var n=function(e){if(!e.ownerDocument||!e.ownerDocument.defaultView)return null;try{return e.ownerDocument.defaultView.frameElement}catch(e){return null}}(e);return!!n&&(n.clientHeight<e.scrollHeight||n.clientWidth<e.scrollWidth)}(e)}return!1},l=function(e,n,t,r,i,a,o,l){return a<e&&o>n||a>e&&o<n?0:a<=e&&l<=t||o>=n&&l>=t?a-e-r:o>n&&l<t||a<e&&l>t?o-n+i:0},s=function(e){var n=e.parentElement;return null==n?e.getRootNode().host||null:n},u=function(e,n){var t,r,a,u;if("undefined"==typeof document)return[];var d=n.scrollMode,c=n.block,f=n.inline,h=n.boundary,p=n.skipOverflowHiddenElements,m="function"==typeof h?h:function(e){return e!==h};if(!i(e))throw new TypeError("Invalid target");for(var g=document.scrollingElement||document.documentElement,v=[],x=e;i(x)&&m(x);){if((x=s(x))===g){v.push(x);break}null!=x&&x===document.body&&o(x)&&!o(document.documentElement)||null!=x&&o(x,p)&&v.push(x)}for(var Z=null!=(r=null==(t=window.visualViewport)?void 0:t.width)?r:innerWidth,b=null!=(u=null==(a=window.visualViewport)?void 0:a.height)?u:innerHeight,y=window,j=y.scrollX,w=y.scrollY,C=e.getBoundingClientRect(),k=C.height,z=C.width,S=C.top,R=C.right,M=C.bottom,I=C.left,q="start"===c||"nearest"===c?S:"end"===c?M:S+k/2,B="center"===f?I+z/2:"end"===f?R:I,W=[],E=0;E<v.length;E++){var H=v[E],N=H.getBoundingClientRect(),P=N.height,L=N.width,V=N.top,D=N.right,F=N.bottom,T=N.left;if("if-needed"===d&&S>=0&&I>=0&&M<=b&&R<=Z&&S>=V&&M<=F&&I>=T&&R<=D)return W;var O=getComputedStyle(H),_=parseInt(O.borderLeftWidth,10),A=parseInt(O.borderTopWidth,10),Q=parseInt(O.borderRightWidth,10),U=parseInt(O.borderBottomWidth,10),X=0,Y=0,J="offsetWidth"in H?H.offsetWidth-H.clientWidth-_-Q:0,K="offsetHeight"in H?H.offsetHeight-H.clientHeight-A-U:0,G="offsetWidth"in H?0===H.offsetWidth?0:L/H.offsetWidth:0,$="offsetHeight"in H?0===H.offsetHeight?0:P/H.offsetHeight:0;if(g===H)X="start"===c?q:"end"===c?q-b:"nearest"===c?l(w,w+b,b,A,U,w+q,w+q+k,k):q-b/2,Y="start"===f?B:"center"===f?B-Z/2:"end"===f?B-Z:l(j,j+Z,Z,_,Q,j+B,j+B+z,z),X=Math.max(0,X+w),Y=Math.max(0,Y+j);else{X="start"===c?q-V-A:"end"===c?q-F+U+K:"nearest"===c?l(V,F,P,A,U+K,q,q+k,k):q-(V+P/2)+K/2,Y="start"===f?B-T-_:"center"===f?B-(T+L/2)+J/2:"end"===f?B-D+Q+J:l(T,D,L,_,Q+J,B,B+z,z);var ee=H.scrollLeft,ne=H.scrollTop;q+=ne-(X=Math.max(0,Math.min(ne+X/$,H.scrollHeight-P/$+K))),B+=ee-(Y=Math.max(0,Math.min(ee+Y/G,H.scrollWidth-L/G+J)))}W.push({el:H,top:X,left:Y})}return W};function d(e,n){if(e.isConnected&&function(e){for(var n=e;n&&n.parentNode;){if(n.parentNode===document)return!0;n=n.parentNode instanceof ShadowRoot?n.parentNode.host:n.parentNode}return!1}(e)){var t=function(e){var n=window.getComputedStyle(e);return{top:parseFloat(n.scrollMarginTop)||0,right:parseFloat(n.scrollMarginRight)||0,bottom:parseFloat(n.scrollMarginBottom)||0,left:parseFloat(n.scrollMarginLeft)||0}}(e);if(function(e){return"object"==typeof e&&"function"==typeof e.behavior}(n))return n.behavior(u(e,n));var i,a="boolean"==typeof n||null==n?void 0:n.behavior,o=(0,r.Z)(u(e,function(e){return!1===e?{block:"end",inline:"nearest"}:function(e){return e===Object(e)&&0!==Object.keys(e).length}(e)?e:{block:"start",inline:"nearest"}}(n)));try{for(o.s();!(i=o.n()).done;){var l=i.value,s=l.el,d=l.top,c=l.left,f=d-t.top+t.bottom,h=c-t.left+t.right;s.scroll({top:f,left:h,behavior:a})}}catch(p){o.e(p)}finally{o.f()}}}}}]);