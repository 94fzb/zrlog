"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[594],{4553:function(e,t,n){var i=n(1413),a=n(5861),r=n(9439),l=n(7757),c=n.n(l),o=n(2745),s=n(6593),d=n(8727),u=n(8391),h=n(6146),f=n(1522),p=n(5333),v=n(3561),g=n(6155),m=n(8773),x=n(5536),y=n(3712);t.Z=function(e){var t=e.deleteApi,n=e.editBtnRender,l=e.addBtnRender,Z=e.columns,w=e.datasource,z=e.searchKey,k=e.deleteSuccessCallback,j=(0,m.s0)(),b=(0,m.TH)(),R=function(e,t,n){return S(e,t,n,-1)},S=function(e,t,n,i){var a={};e>1&&(a.page=e),10!=t&&1e6!=t&&(a.size=t),n&&n.trim().length>0&&(a.key=n.trim()),i>0&&(a[v.NJ]=i);var r=(0,f.bI)(a);return 0===r.length?b.pathname:b.pathname+"?"+r},M=function(e,t,n){j(R(e,t,n))},I=function(e,t,n){j(S(e,t,n,(new Date).getTime()))},H=(0,u.useState)({pagination:{page:null!==w&&void 0!==w&&w.page?w.page:1,key:null===w||void 0===w?void 0:w.key,size:null!==w&&void 0!==w&&w.size?null===w||void 0===w?void 0:w.size:10},query:null===w||void 0===w?void 0:w.key,tableLoaded:!0,rows:w?w.rows:[],tablePagination:{total:null===w||void 0===w?void 0:w.totalElements,current:null===w||void 0===w?void 0:w.page,pageSize:null===w||void 0===w?void 0:w.size,onChange:function(e,t){M(e,t,L.query)}}}),T=(0,r.Z)(H,2),L=T[0],q=T[1],B=o.ZP.useMessage(),C=(0,r.Z)(B,2),E=C[0],_=C[1],P=function(){var e=(0,a.Z)(c().mark((function e(t,n,i){var a;return c().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,h.Z.post(n+"?id="+i);case 2:if(!(a=e.sent).data.error){e.next=6;break}return E.error(a.data.message),e.abrupt("return");case 6:E.success((0,v.sR)().deleteSuccess),I(t.page,t.size,L.query);case 8:case"end":return e.stop()}}),e)})));return function(t,n,i){return e.apply(this,arguments)}}();(0,u.useEffect)((function(){z!==L.query&&(q((0,i.Z)((0,i.Z)({},L),{},{query:z})),M(1,L.pagination.size,z))}),[z]),(0,u.useEffect)((function(){q((function(e){return(0,i.Z)((0,i.Z)({},e),{},{rows:w?w.rows:[],pagination:{page:null!==w&&void 0!==w&&w.page?w.page:1,size:null!==w&&void 0!==w&&w.size?w.size:10},tablePagination:{current:null===w||void 0===w?void 0:w.page,pageSize:null===w||void 0===w?void 0:w.size,total:null===w||void 0===w?void 0:w.totalElements}})}))}),[w]);return(0,y.jsxs)(y.Fragment,{children:[_,l?l((function(){I(L.pagination.page,L.pagination.size,L.query)})):void 0,(0,y.jsx)(d.Z,{onChange:function(e){M(e.current?e.current:1,e.pageSize?e.pageSize:10,L.query)},style:{minHeight:512},columns:function(){var e=[];return e.push({title:"ID",dataIndex:"id",key:"id",width:80}),e.push({title:"",dataIndex:"id",key:"action",width:100,render:function(e,i){return e?(0,y.jsxs)(s.Z,{size:16,children:[(0,y.jsx)(p.Z,{title:(0,v.sR)().deleteTips,onConfirm:function(){return P(L.pagination,t,i.id).then((function(){k&&k(i.id)}))},children:(0,y.jsx)(g.Z,{style:{color:"red"}})}),n?n(e,i,(function(){I(L.pagination.page,L.pagination.size,L.query)})):null]}):null}}),Z.forEach((function(t){e.push(t)})),e}(),pagination:(0,i.Z)((0,i.Z)({hideOnSinglePage:!0},L.tablePagination),{},{itemRender:function(e,t,n){return(0,y.jsx)(x.rU,{to:R(e,null!==w&&void 0!==w&&w.size?w.size:10,L.query),children:n},e)}}),dataSource:L.rows,scroll:{x:"90vw"}})]})}},3594:function(e,t,n){n.r(t),n.d(t,{default:function(){return b}});var i=n(9439),a=n(1264),r=n(3806),l=n(575),c=n(1996),o=n(6593),s=n(535),d=n(7489),u=n(3342),h=n(8881),f=n(1474),p=n(3561),v=n(8391),g=n(4553),m=n(5536),x=n(8106),y=n(7462),Z={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M832 464h-68V240c0-70.7-57.3-128-128-128H388c-70.7 0-128 57.3-128 128v224h-68c-17.7 0-32 14.3-32 32v384c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V496c0-17.7-14.3-32-32-32zM332 240c0-30.9 25.1-56 56-56h248c30.9 0 56 25.1 56 56v224H332V240zm460 600H232V536h560v304zM484 701v53c0 4.4 3.6 8 8 8h40c4.4 0 8-3.6 8-8v-53a48.01 48.01 0 10-56 0z"}}]},name:"lock",theme:"outlined"},w=n(9345),z=function(e,t){return v.createElement(w.Z,(0,y.Z)({},e,{ref:t,icon:Z}))};var k=v.forwardRef(z),j=n(3712),b=function(e){var t=e.data,n=function(e){var t=(0,j.jsx)(l.Z,{icon:(0,j.jsx)(a.Z,{}),closable:!1,color:(0,p.RQ)(),children:e});return(0,j.jsx)("span",{style:{display:"inline-block"},children:t},"all-"+e)},y=function(e,t){return(0,j.jsxs)("span",{style:{display:"flex",gap:4},children:[e.privacy&&(0,j.jsx)(k,{style:{color:(0,p.RQ)()}}),e.rubbish&&(0,j.jsx)("span",{children:"[\u8349\u7a3f]"}),t]})},Z=(0,v.useState)(t.key?t.key:""),w=(0,i.Z)(Z,2),z=w[0],b=w[1];return(0,j.jsxs)(j.Fragment,{children:[(0,j.jsxs)(s.Z,{gutter:[8,8],style:{paddingTop:20},children:[(0,j.jsx)(d.Z,{md:14,xxl:18,sm:6,span:24,children:(0,j.jsx)(h.Z,{className:"page-header",style:{marginTop:0,marginBottom:0},level:3,children:(0,p.sR)().blogManage})}),(0,j.jsx)(d.Z,{md:10,xxl:6,sm:18,children:(0,j.jsx)(u.Z,{placeholder:(0,p.sR)().searchTip,onSearch:function(e){b(e)},defaultValue:t.key,enterButton:(0,p.sR)().search,style:{maxWidth:"240px",float:"right"}})})]}),(0,j.jsx)(f.Z,{}),(0,j.jsx)(g.Z,{datasource:t,columns:[{title:"\u6807\u9898",dataIndex:"title",key:"title",ellipsis:{showTitle:!1},width:300,render:function(e,t){var n=(0,j.jsx)(c.Z,{placement:"top",title:(0,j.jsxs)("div",{children:["\u70b9\u51fb\u67e5\u770b\u300a",(0,j.jsx)("span",{dangerouslySetInnerHTML:{__html:e}}),"\u300b"]}),children:(0,j.jsx)("div",{style:{overflow:"hidden",textOverflow:"ellipsis"},dangerouslySetInnerHTML:{__html:e}})});return t.url.includes("previewMode")?y(t,(0,j.jsx)(m.rU,{to:t.url,children:n})):y(t,(0,j.jsx)("a",{rel:"noopener noreferrer",target:"_blank",href:t.url,children:n}))}},{title:"\u6807\u7b7e",dataIndex:"keywords",key:"keywords",width:150,render:function(e){return e?(0,j.jsx)(o.Z,{size:[0,8],wrap:!0,children:e.split(",").map(n)}):null}},{title:"\u5206\u7c7b",key:"typeName",dataIndex:"typeName",width:100},{title:"\u6d4f\u89c8\u91cf",key:"click",dataIndex:"click",width:80},{title:(0,p.sR)().commentAble,key:"canComment",dataIndex:"canComment",render:function(e){return e?"\u662f":"\u5426"},width:80},{title:"\u8bc4\u8bba\u91cf",key:"commentSize",dataIndex:"commentSize",width:80},{title:"\u521b\u5efa\u65f6\u95f4",key:"releaseTime",dataIndex:"releaseTime",width:120},{title:"\u6700\u540e\u66f4\u65b0\u65f6\u95f4",key:"lastUpdateDate",dataIndex:"lastUpdateDate",width:120}],editBtnRender:function(e){return(0,j.jsx)(m.rU,{to:"/article-edit?id="+e,children:(0,j.jsx)(r.Z,{style:{color:(0,p.RQ)()}})})},deleteSuccessCallback:function(e){(0,x.rn)("/article-edit?id="+e)},deleteApi:"/api/admin/article/delete",searchKey:z})]})}},6155:function(e,t,n){n.d(t,{Z:function(){return o}});var i=n(7462),a=n(8391),r={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M360 184h-8c4.4 0 8-3.6 8-8v8h304v-8c0 4.4 3.6 8 8 8h-8v72h72v-80c0-35.3-28.7-64-64-64H352c-35.3 0-64 28.7-64 64v80h72v-72zm504 72H160c-17.7 0-32 14.3-32 32v32c0 4.4 3.6 8 8 8h60.4l24.7 523c1.6 34.1 29.8 61 63.9 61h454c34.2 0 62.3-26.8 63.9-61l24.7-523H888c4.4 0 8-3.6 8-8v-32c0-17.7-14.3-32-32-32zM731.3 840H292.7l-24.2-512h487l-24.2 512z"}}]},name:"delete",theme:"outlined"},l=n(9345),c=function(e,t){return a.createElement(l.Z,(0,i.Z)({},e,{ref:t,icon:r}))};var o=a.forwardRef(c)},3806:function(e,t,n){n.d(t,{Z:function(){return o}});var i=n(7462),a=n(8391),r={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M257.7 752c2 0 4-.2 6-.5L431.9 722c2-.4 3.9-1.3 5.3-2.8l423.9-423.9a9.96 9.96 0 000-14.1L694.9 114.9c-1.9-1.9-4.4-2.9-7.1-2.9s-5.2 1-7.1 2.9L256.8 538.8c-1.5 1.5-2.4 3.3-2.8 5.3l-29.5 168.2a33.5 33.5 0 009.4 29.8c6.6 6.4 14.9 9.9 23.8 9.9zm67.4-174.4L687.8 215l73.3 73.3-362.7 362.6-88.9 15.7 15.6-89zM880 836H144c-17.7 0-32 14.3-32 32v36c0 4.4 3.6 8 8 8h784c4.4 0 8-3.6 8-8v-36c0-17.7-14.3-32-32-32z"}}]},name:"edit",theme:"outlined"},l=n(9345),c=function(e,t){return a.createElement(l.Z,(0,i.Z)({},e,{ref:t,icon:r}))};var o=a.forwardRef(c)},1264:function(e,t,n){n.d(t,{Z:function(){return o}});var i=n(7462),a=n(8391),r={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M938 458.8l-29.6-312.6c-1.5-16.2-14.4-29-30.6-30.6L565.2 86h-.4c-3.2 0-5.7 1-7.6 2.9L88.9 557.2a9.96 9.96 0 000 14.1l363.8 363.8c1.9 1.9 4.4 2.9 7.1 2.9s5.2-1 7.1-2.9l468.3-468.3c2-2.1 3-5 2.8-8zM459.7 834.7L189.3 564.3 589 164.6 836 188l23.4 247-399.7 399.7zM680 256c-48.5 0-88 39.5-88 88s39.5 88 88 88 88-39.5 88-88-39.5-88-88-88zm0 120c-17.7 0-32-14.3-32-32s14.3-32 32-32 32 14.3 32 32-14.3 32-32 32z"}}]},name:"tag",theme:"outlined"},l=n(9345),c=function(e,t){return a.createElement(l.Z,(0,i.Z)({},e,{ref:t,icon:r}))};var o=a.forwardRef(c)}}]);