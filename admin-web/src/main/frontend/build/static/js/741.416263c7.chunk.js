"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[741],{4553:function(e,n,t){var r=t(1413),i=t(5861),a=t(9439),o=t(7757),l=t.n(o),s=t(2745),u=t(6593),d=t(8727),c=t(8391),f=t(6146),h=t(1522),p=t(5333),m=t(3561),g=t(6155),v=t(8773),Z=t(5536),x=t(3712);n.Z=function(e){var n=e.deleteApi,t=e.editBtnRender,o=e.addBtnRender,y=e.columns,j=e.datasource,b=e.searchKey,w=e.deleteSuccessCallback,k=(0,v.s0)(),C=(0,v.TH)(),z=function(e,n,t){return R(e,n,t,-1)},R=function(e,n,t,r){var i={};e>1&&(i.page=e),10!=n&&1e6!=n&&(i.size=n),t&&t.trim().length>0&&(i.key=t.trim()),r>0&&(i[m.NJ]=r);var a=(0,h.bI)(i);return 0===a.length?C.pathname:C.pathname+"?"+a},S=function(e,n,t){k(z(e,n,t))},I=function(e,n,t){k(R(e,n,t,(new Date).getTime()))},B=(0,c.useState)({pagination:{page:null!==j&&void 0!==j&&j.page?j.page:1,key:null===j||void 0===j?void 0:j.key,size:null!==j&&void 0!==j&&j.size?null===j||void 0===j?void 0:j.size:10},query:null===j||void 0===j?void 0:j.key,tableLoaded:!0,rows:j?j.rows:[],tablePagination:{total:null===j||void 0===j?void 0:j.totalElements,current:null===j||void 0===j?void 0:j.page,pageSize:null===j||void 0===j?void 0:j.size,onChange:function(e,n){S(e,n,W.query)}}}),H=(0,a.Z)(B,2),W=H[0],q=H[1],M=s.ZP.useMessage(),E=(0,a.Z)(M,2),N=E[0],F=E[1],L=function(){var e=(0,i.Z)(l().mark((function e(n,t,r){var i;return l().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,f.Z.post(t+"?id="+r);case 2:if(!(i=e.sent).data.error){e.next=6;break}return N.error(i.data.message),e.abrupt("return");case 6:N.success((0,m.sR)().deleteSuccess),I(n.page,n.size,W.query);case 8:case"end":return e.stop()}}),e)})));return function(n,t,r){return e.apply(this,arguments)}}();(0,c.useEffect)((function(){b!==W.query&&(q((0,r.Z)((0,r.Z)({},W),{},{query:b})),S(1,W.pagination.size,b))}),[b]),(0,c.useEffect)((function(){q((function(e){return(0,r.Z)((0,r.Z)({},e),{},{rows:j?j.rows:[],pagination:{page:null!==j&&void 0!==j&&j.page?j.page:1,size:null!==j&&void 0!==j&&j.size?j.size:10},tablePagination:{current:null===j||void 0===j?void 0:j.page,pageSize:null===j||void 0===j?void 0:j.size,total:null===j||void 0===j?void 0:j.totalElements}})}))}),[j]);return(0,x.jsxs)(x.Fragment,{children:[F,o?o((function(){I(W.pagination.page,W.pagination.size,W.query)})):void 0,(0,x.jsx)(d.Z,{onChange:function(e){S(e.current?e.current:1,e.pageSize?e.pageSize:10,W.query)},style:{minHeight:512},columns:function(){var e=[];return e.push({title:"ID",dataIndex:"id",key:"id",width:80}),e.push({title:"",dataIndex:"id",key:"action",width:100,render:function(e,r){return e?(0,x.jsxs)(u.Z,{size:16,children:[(0,x.jsx)(p.Z,{title:(0,m.sR)().deleteTips,onConfirm:function(){return L(W.pagination,n,r.id).then((function(){w&&w(r.id)}))},children:(0,x.jsx)(g.Z,{style:{color:"red"}})}),t?t(e,r,(function(){I(W.pagination.page,W.pagination.size,W.query)})):null]}):null}}),y.forEach((function(n){e.push(n)})),e}(),pagination:(0,r.Z)((0,r.Z)({hideOnSinglePage:!0},W.tablePagination),{},{itemRender:function(e,n,t){return(0,x.jsx)(Z.rU,{to:z(e,null!==j&&void 0!==j&&j.size?j.size:10,W.query),children:t},e)}}),dataSource:W.rows,scroll:{x:"90vw"}})]})}},9789:function(e,n,t){t.r(n),t.d(n,{default:function(){return I}});var r=t(8881),i=t(1474),a=t(4553),o=t(3561),l=t(1413),s=t(5861),u=t(9439),d=t(7757),c=t.n(d),f=t(8391),h=t(9012),p=t(7849),m=t(7396),g=t(3615),v=t(7489),Z=t(7372),x=t(5113),y=t(8288),j=t(6146),b=t(3712),w={labelCol:{span:4},wrapperCol:{span:20}},k=function(e){var n=e.addSuccessCall,t=(0,f.useState)(!1),r=(0,u.Z)(t,2),i=r[0],a=r[1],d=(0,f.useState)(),k=(0,u.Z)(d,2),C=k[0],z=k[1],R=h.Z.useApp().message;return(0,b.jsxs)(b.Fragment,{children:[(0,b.jsx)(p.ZP,{type:"primary",onClick:function(){return a(!0)},style:{marginBottom:8},children:(0,o.sR)().add}),(0,b.jsx)(m.Z,{title:(0,o.sR)().add,open:i,onOk:function(){j.Z.post("/api/admin/nav/add",C).then(function(){var e=(0,s.Z)(c().mark((function e(t){var r;return c().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(r=t.data).error){e.next=5;break}return e.next=4,R.error(r.message);case 4:return e.abrupt("return");case 5:a(!1),n();case 7:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}())},onCancel:function(){return a(!1)},children:(0,b.jsxs)(g.Z,(0,l.Z)((0,l.Z)({onValuesChange:function(e,n){z(n)}},w),{},{children:[(0,b.jsx)(y.Z,{children:(0,b.jsx)(v.Z,{span:24,children:(0,b.jsx)(g.Z.Item,{label:(0,o.sR)()["admin.link.manage"],style:{marginBottom:8},name:"url",rules:[{required:!0,message:""}],children:(0,b.jsx)(Z.Z,{})})})}),(0,b.jsx)(y.Z,{children:(0,b.jsx)(v.Z,{span:24,children:(0,b.jsx)(g.Z.Item,{label:"\u5bfc\u822a\u540d\u79f0",style:{marginBottom:8},name:"navName",rules:[{required:!0,message:""}],children:(0,b.jsx)(Z.Z,{})})})}),(0,b.jsx)(y.Z,{children:(0,b.jsx)(v.Z,{span:24,children:(0,b.jsx)(g.Z.Item,{label:"\u6392\u5e8f",style:{marginBottom:8},name:"sort",rules:[{required:!0,message:""}],children:(0,b.jsx)(x.Z,{})})})})]}))})]})},C=t(3806),z=t(5536),R={labelCol:{span:4},wrapperCol:{span:20}},S=function(e){var n=e.record,t=e.editSuccessCall,r=(0,f.useState)(!1),i=(0,u.Z)(r,2),a=i[0],d=i[1],p=(0,f.useState)(n),w=(0,u.Z)(p,2),k=w[0],S=w[1],I=h.Z.useApp().message;return(0,b.jsxs)(b.Fragment,{children:[(0,b.jsx)(z.rU,{to:"#edit-"+n.id,onClick:function(e){e.preventDefault(),e.stopPropagation(),d(!0)},children:(0,b.jsx)(C.Z,{style:{marginBottom:8,color:(0,o.RQ)()}})}),(0,b.jsx)(m.Z,{title:(0,o.sR)().edit,open:a,onOk:function(){j.Z.post("/api/admin/nav/update",k).then(function(){var e=(0,s.Z)(c().mark((function e(n){var r;return c().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(r=n.data).error){e.next=5;break}return e.next=4,I.error(r.message);case 4:return e.abrupt("return");case 5:d(!1),t&&t();case 7:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}())},onCancel:function(){return d(!1)},children:(0,b.jsxs)(g.Z,(0,l.Z)((0,l.Z)({initialValues:n,onValuesChange:function(e,n){S(n)}},R),{},{children:[(0,b.jsx)(g.Z.Item,{name:"id",style:{display:"none"},children:(0,b.jsx)(Z.Z,{hidden:!0})}),(0,b.jsx)(y.Z,{children:(0,b.jsx)(v.Z,{span:24,children:(0,b.jsx)(g.Z.Item,{label:(0,o.sR)()["admin.link.manage"],style:{marginBottom:8},name:"url",rules:[{required:!0,message:""}],children:(0,b.jsx)(Z.Z,{})})})}),(0,b.jsx)(y.Z,{children:(0,b.jsx)(v.Z,{span:24,children:(0,b.jsx)(g.Z.Item,{label:"\u5bfc\u822a\u540d\u79f0",style:{marginBottom:8},name:"navName",rules:[{required:!0,message:""}],children:(0,b.jsx)(Z.Z,{})})})}),(0,b.jsx)(y.Z,{children:(0,b.jsx)(v.Z,{span:24,children:(0,b.jsx)(g.Z.Item,{label:"\u6392\u5e8f",style:{marginBottom:8},name:"sort",rules:[{required:!0,message:""}],children:(0,b.jsx)(x.Z,{})})})})]}))})]})},I=function(e){var n=e.data;return(0,b.jsxs)(b.Fragment,{children:[(0,b.jsx)(r.Z,{className:"page-header",level:3,children:(0,o.sR)()["admin.nav.manage"]}),(0,b.jsx)(i.Z,{}),(0,b.jsx)(a.Z,{columns:[{title:(0,o.sR)()["admin.link.manage"],dataIndex:"url",width:240,key:"url",render:function(e,n){return(0,b.jsx)("a",{style:{display:"inline"},rel:"noopener noreferrer",target:"_blank",href:n.jumpUrl,children:e})}},{title:"\u5bfc\u822a\u540d\u79f0",dataIndex:"navName",key:"navName",width:240},{title:"\u6392\u5e8f",key:"sort",dataIndex:"sort",width:60}],addBtnRender:function(e){return(0,b.jsx)(k,{addSuccessCall:e})},editBtnRender:function(e,n,t){return(0,b.jsx)(S,{record:n,editSuccessCall:t})},datasource:n,deleteApi:"/api/admin/nav/delete"})]})}},3806:function(e,n,t){t.d(n,{Z:function(){return s}});var r=t(7462),i=t(8391),a={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M257.7 752c2 0 4-.2 6-.5L431.9 722c2-.4 3.9-1.3 5.3-2.8l423.9-423.9a9.96 9.96 0 000-14.1L694.9 114.9c-1.9-1.9-4.4-2.9-7.1-2.9s-5.2 1-7.1 2.9L256.8 538.8c-1.5 1.5-2.4 3.3-2.8 5.3l-29.5 168.2a33.5 33.5 0 009.4 29.8c6.6 6.4 14.9 9.9 23.8 9.9zm67.4-174.4L687.8 215l73.3 73.3-362.7 362.6-88.9 15.7 15.6-89zM880 836H144c-17.7 0-32 14.3-32 32v36c0 4.4 3.6 8 8 8h784c4.4 0 8-3.6 8-8v-36c0-17.7-14.3-32-32-32z"}}]},name:"edit",theme:"outlined"},o=t(9345),l=function(e,n){return i.createElement(o.Z,(0,r.Z)({},e,{ref:n,icon:a}))};var s=i.forwardRef(l)},6626:function(e,n,t){t.d(n,{Z:function(){return d}});var r=t(7762),i=function(e){return"object"==typeof e&&null!=e&&1===e.nodeType},a=function(e,n){return(!n||"hidden"!==e)&&"visible"!==e&&"clip"!==e},o=function(e,n){if(e.clientHeight<e.scrollHeight||e.clientWidth<e.scrollWidth){var t=getComputedStyle(e,null);return a(t.overflowY,n)||a(t.overflowX,n)||function(e){var n=function(e){if(!e.ownerDocument||!e.ownerDocument.defaultView)return null;try{return e.ownerDocument.defaultView.frameElement}catch(e){return null}}(e);return!!n&&(n.clientHeight<e.scrollHeight||n.clientWidth<e.scrollWidth)}(e)}return!1},l=function(e,n,t,r,i,a,o,l){return a<e&&o>n||a>e&&o<n?0:a<=e&&l<=t||o>=n&&l>=t?a-e-r:o>n&&l<t||a<e&&l>t?o-n+i:0},s=function(e){var n=e.parentElement;return null==n?e.getRootNode().host||null:n},u=function(e,n){var t,r,a,u;if("undefined"==typeof document)return[];var d=n.scrollMode,c=n.block,f=n.inline,h=n.boundary,p=n.skipOverflowHiddenElements,m="function"==typeof h?h:function(e){return e!==h};if(!i(e))throw new TypeError("Invalid target");for(var g=document.scrollingElement||document.documentElement,v=[],Z=e;i(Z)&&m(Z);){if((Z=s(Z))===g){v.push(Z);break}null!=Z&&Z===document.body&&o(Z)&&!o(document.documentElement)||null!=Z&&o(Z,p)&&v.push(Z)}for(var x=null!=(r=null==(t=window.visualViewport)?void 0:t.width)?r:innerWidth,y=null!=(u=null==(a=window.visualViewport)?void 0:a.height)?u:innerHeight,j=window,b=j.scrollX,w=j.scrollY,k=e.getBoundingClientRect(),C=k.height,z=k.width,R=k.top,S=k.right,I=k.bottom,B=k.left,H="start"===c||"nearest"===c?R:"end"===c?I:R+C/2,W="center"===f?B+z/2:"end"===f?S:B,q=[],M=0;M<v.length;M++){var E=v[M],N=E.getBoundingClientRect(),F=N.height,L=N.width,T=N.top,P=N.right,V=N.bottom,D=N.left;if("if-needed"===d&&R>=0&&B>=0&&I<=y&&S<=x&&R>=T&&I<=V&&B>=D&&S<=P)return q;var O=getComputedStyle(E),_=parseInt(O.borderLeftWidth,10),A=parseInt(O.borderTopWidth,10),U=parseInt(O.borderRightWidth,10),X=parseInt(O.borderBottomWidth,10),Y=0,J=0,K="offsetWidth"in E?E.offsetWidth-E.clientWidth-_-U:0,Q="offsetHeight"in E?E.offsetHeight-E.clientHeight-A-X:0,G="offsetWidth"in E?0===E.offsetWidth?0:L/E.offsetWidth:0,$="offsetHeight"in E?0===E.offsetHeight?0:F/E.offsetHeight:0;if(g===E)Y="start"===c?H:"end"===c?H-y:"nearest"===c?l(w,w+y,y,A,X,w+H,w+H+C,C):H-y/2,J="start"===f?W:"center"===f?W-x/2:"end"===f?W-x:l(b,b+x,x,_,U,b+W,b+W+z,z),Y=Math.max(0,Y+w),J=Math.max(0,J+b);else{Y="start"===c?H-T-A:"end"===c?H-V+X+Q:"nearest"===c?l(T,V,F,A,X+Q,H,H+C,C):H-(T+F/2)+Q/2,J="start"===f?W-D-_:"center"===f?W-(D+L/2)+K/2:"end"===f?W-P+U+K:l(D,P,L,_,U+K,W,W+z,z);var ee=E.scrollLeft,ne=E.scrollTop;H+=ne-(Y=Math.max(0,Math.min(ne+Y/$,E.scrollHeight-F/$+Q))),W+=ee-(J=Math.max(0,Math.min(ee+J/G,E.scrollWidth-L/G+K)))}q.push({el:E,top:Y,left:J})}return q};function d(e,n){if(e.isConnected&&function(e){for(var n=e;n&&n.parentNode;){if(n.parentNode===document)return!0;n=n.parentNode instanceof ShadowRoot?n.parentNode.host:n.parentNode}return!1}(e)){var t=function(e){var n=window.getComputedStyle(e);return{top:parseFloat(n.scrollMarginTop)||0,right:parseFloat(n.scrollMarginRight)||0,bottom:parseFloat(n.scrollMarginBottom)||0,left:parseFloat(n.scrollMarginLeft)||0}}(e);if(function(e){return"object"==typeof e&&"function"==typeof e.behavior}(n))return n.behavior(u(e,n));var i,a="boolean"==typeof n||null==n?void 0:n.behavior,o=(0,r.Z)(u(e,function(e){return!1===e?{block:"end",inline:"nearest"}:function(e){return e===Object(e)&&0!==Object.keys(e).length}(e)?e:{block:"start",inline:"nearest"}}(n)));try{for(o.s();!(i=o.n()).done;){var l=i.value,s=l.el,d=l.top,c=l.left,f=d-t.top+t.bottom,h=c-t.left+t.right;s.scroll({top:f,left:h,behavior:a})}}catch(p){o.e(p)}finally{o.f()}}}}}]);