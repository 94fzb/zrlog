"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[412],{2412:function(e,n,t){t.r(n),t.d(n,{default:function(){return T}});var i=t(4942),r=t(1413),a=t(9439),l=t(8773),f=t(8391),o=t(5861),s=t(7757),u=t.n(s),c=t(8198),d=function(){var e=(0,o.Z)(u().mark((function e(n){var t,i;return u().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,c.Z.get("/api/admin"+n);case 2:return t=e.sent,i=t.data,e.abrupt("return",i);case 5:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}(),x=t(2677),j=t(8214),h=t(8106),p=t(1522),g=t(3561),b=t(9551),m=t(3712),v=(0,f.lazy)((function(){return Promise.all([t.e(736),t.e(184),t.e(231),t.e(991),t.e(148),t.e(650),t.e(54),t.e(120),t.e(17),t.e(659)]).then(t.bind(t,9659))})),y=(0,f.lazy)((function(){return t.e(287).then(t.bind(t,5287))})),S=(0,f.lazy)((function(){return Promise.all([t.e(736),t.e(184),t.e(231),t.e(991),t.e(942),t.e(225)]).then(t.bind(t,8225))})),Z=(0,f.lazy)((function(){return t.e(165).then(t.bind(t,3165))})),R=(0,f.lazy)((function(){return Promise.all([t.e(736),t.e(184),t.e(231),t.e(991),t.e(942),t.e(650),t.e(871),t.e(713)]).then(t.bind(t,6713))})),k=(0,f.lazy)((function(){return Promise.all([t.e(736),t.e(184),t.e(231),t.e(991),t.e(148),t.e(650),t.e(902),t.e(54),t.e(109),t.e(944)]).then(t.bind(t,1944))})),q=(0,f.lazy)((function(){return Promise.all([t.e(736),t.e(184),t.e(231),t.e(991),t.e(942),t.e(778)]).then(t.bind(t,3757))})),w=(0,f.lazy)((function(){return Promise.all([t.e(736),t.e(184),t.e(231),t.e(991),t.e(942),t.e(902),t.e(974)]).then(t.bind(t,3974))})),A=(0,f.lazy)((function(){return Promise.all([t.e(736),t.e(184),t.e(231),t.e(991),t.e(942),t.e(902),t.e(789)]).then(t.bind(t,9789))})),W=(0,f.lazy)((function(){return t.e(644).then(t.bind(t,2644))})),z=(0,f.lazy)((function(){return Promise.resolve().then(t.bind(t,3250))})),_=(0,f.lazy)((function(){return t.e(608).then(t.bind(t,6608))})),P=(0,f.lazy)((function(){return Promise.all([t.e(736),t.e(184),t.e(991),t.e(148),t.e(902),t.e(54),t.e(109),t.e(890)]).then(t.bind(t,1890))})),O=(0,f.lazy)((function(){return Promise.all([t.e(736),t.e(130)]).then(t.bind(t,9939))})),E=(0,f.lazy)((function(){return Promise.all([t.e(736),t.e(184),t.e(231),t.e(991),t.e(942),t.e(594)]).then(t.bind(t,3594))})),U=(0,f.lazy)((function(){return Promise.all([t.e(736),t.e(148),t.e(54),t.e(859)]).then(t.bind(t,2491))})),C=(0,f.lazy)((function(){return Promise.resolve().then(t.bind(t,700))})),D=(0,f.lazy)((function(){return Promise.all([t.e(736),t.e(184),t.e(231),t.e(148),t.e(120),t.e(603)]).then(t.bind(t,2603))})),T=function(e){var n=e.offline,t=(0,l.TH)(),o=(0,b.E)()?(0,h.rb)():null,s=(0,h.Rt)(o||(0,p.a9)(t)),u=(0,f.useState)({firstRender:j.qj&&j.qj.pageData,currentUri:t.pathname+t.search,axiosRequesting:!1,offline:n,fullScreen:s,data:(0,r.Z)((0,r.Z)({},(0,h.or)()),{},(0,i.Z)({},t.pathname+t.search,null===j.qj||void 0===j.qj?void 0:j.qj.pageData))}),c=(0,a.Z)(u,2),T=c[0],I=c[1],J=function(){var e=t.pathname+(0,p.iO)(t.search,g.NJ);return void 0!==T.data[e]&&null!==T.data[e]?T.data[e]:void 0},M=function(e,n){d(e).then((function(t){var i=t.data,r=t.documentTitle,a=T.data;!function(e){var n=(0,g.sR)().websiteTitle+" - "+(0,g.sR)()["admin.management"];e?(0,b.E)()?window.document.title=e.replace(" - "+n,""):window.document.title=e:window.document.title=n}(r),(0,p.vZ)(a[e],i)?console.debug(e+" cache hits"):(a[e]=i,I((function(t){return{offline:t.offline,firstRender:!1,axiosRequesting:!1,currentUri:e,data:a,fullScreen:(0,h.Rt)((0,p.a9)(n))}})),(0,h.g0)(a))})).finally((function(){I((function(t){return{offline:t.offline,currentUri:e,firstRender:!1,axiosRequesting:!1,data:t.data,fullScreen:(0,h.Rt)((0,p.a9)(n))}}))}))};return(0,f.useEffect)((function(){var e=t.pathname+(0,p.iO)(t.search,g.NJ);if(J()){if(T.firstRender)return void I((function(n){return{currentUri:e,fullScreen:(0,h.Rt)((0,p.a9)(t)),firstRender:!1,axiosRequesting:!1,offline:n.offline,data:n.data}}));I((function(n){return{currentUri:e,axiosRequesting:!0,firstRender:!1,fullScreen:(0,h.Rt)((0,p.a9)(t)),data:n.data,offline:n.offline}}))}M(e,t)}),[t.pathname,t.search]),(0,f.useEffect)((function(){I((function(e){return(0,r.Z)((0,r.Z)({},e),{},{offline:n})}))}),[n]),(0,m.jsxs)(l.Z5,{children:[(0,m.jsx)(l.AW,{path:"index",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(R,{data:J()})})})}),(0,m.jsx)(l.AW,{path:"",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(R,{data:J()})})})}),(0,m.jsx)(l.AW,{path:"comment",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(S,{offline:n,data:J()})})})}),(0,m.jsx)(l.AW,{path:"plugin",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(Z,{})})})}),(0,m.jsx)(l.AW,{path:"website",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(k,{offline:T.offline,data:J()})})})}),(0,m.jsx)(l.AW,{path:"website/admin",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(k,{offline:T.offline,data:J()})})})}),(0,m.jsx)(l.AW,{path:"website/template",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(k,{offline:T.offline,data:J()})})})}),(0,m.jsx)(l.AW,{path:"website/other",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(k,{offline:T.offline,data:J()})})})}),(0,m.jsx)(l.AW,{path:"website/blog",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(k,{offline:T.offline,data:J()})})})}),(0,m.jsx)(l.AW,{path:"website/upgrade",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(k,{offline:T.offline,data:J()})})})}),(0,m.jsx)(l.AW,{path:"article-type",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(q,{offline:T.offline,data:J()})})})}),(0,m.jsx)(l.AW,{path:"link",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(w,{offline:T.offline,data:J()})})})}),(0,m.jsx)(l.AW,{path:"nav",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(A,{offline:T.offline,data:J()})})})}),(0,m.jsx)(l.AW,{path:"article",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(E,{offline:T.offline,data:J()})})})}),(0,m.jsx)(l.AW,{path:"article-edit",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,fullScreen:T.fullScreen,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(v,{offline:T.offline,onFullScreen:function(){I((function(e){return(0,r.Z)((0,r.Z)({},e),{},{fullScreen:!0})}))},data:J(),onExitFullScreen:function(){return I((function(e){return(0,r.Z)((0,r.Z)({},e),{},{fullScreen:!1})}))}})})})}),(0,m.jsx)(l.AW,{path:"user",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(U,{offline:T.offline,data:J()})})})}),(0,m.jsx)(l.AW,{path:"template-center",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(_,{data:J()})})})}),(0,m.jsx)(l.AW,{path:"user-update-password",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(O,{offline:T.offline})})})}),(0,m.jsx)(l.AW,{path:"upgrade",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(z,{offline:T.offline,data:J()},J().preUpgradeKey)})})}),(0,m.jsx)(l.AW,{path:"template-config",element:(0,m.jsx)(D,{offline:T.offline,loading:T.axiosRequesting,children:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(P,{offline:T.offline,data:J()})})})}),(0,m.jsx)(l.AW,{path:"403",element:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(C,{data:J(),code:403})})}),(0,m.jsx)(l.AW,{path:"500",element:J()&&(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(C,{data:J(),code:500})})}),(0,m.jsx)(l.AW,{path:"offline",element:(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(y,{})})}),(0,m.jsx)(l.AW,{path:"*",element:(0,m.jsx)(f.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(W,{})})})]})}},8106:function(e,n,t){t.d(n,{Rt:function(){return b},ag:function(){return g},fM:function(){return j},g0:function(){return d},or:function(){return c},qD:function(){return m},rb:function(){return v},rn:function(){return h},se:function(){return x}});var i=t(486),r=t(8214),a=t(9551);function l(e,n){var t=(new TextEncoder).encode(e),r=(new TextEncoder).encode(n),a=new Uint8Array(32);return a.set(r.subarray(0,32)),function(e){for(var n="",t=8192,i=0;i<e.length;i+=t){var r=e.subarray(i,i+t);n+=String.fromCharCode.apply(null,r)}return btoa(n)}(new i.ModeOfOperation.ctr(a).encrypt(t))}function f(e,n){var t=(new TextEncoder).encode(n),r=new Uint8Array(32);r.set(t.subarray(0,32));var a=function(e){for(var n=atob(e),t=n.length,i=new Uint8Array(t),r=0;r<t;r++)i[r]=n.charCodeAt(r);return i}(e),l=new i.ModeOfOperation.ctr(r).decrypt(a);return(new TextDecoder).decode(l)}var o=function(){return r.qj&&r.qj.key&&r.qj&&r.qj.key?r.qj.key:"__DEV__DEV__DEV_"},s=function(){return window.location.host+"_encrypt_page_data"};function u(e,n){var t=arguments.length>2&&void 0!==arguments[2]?arguments[2]:" ";if(e.length>=n)return e.substring(0,n);var i=n-e.length,r=t.repeat(i);return e+r}var c=function(){var e=localStorage.getItem(s());try{if(e&&e.length>0)return JSON.parse(f(e,u(o(),24)))}catch(n){console.error(n)}return{}},d=function(e){try{localStorage.setItem(s(),l(JSON.stringify(e),u(o(),24)))}catch(n){console.error(n)}},x=function(e,n){var t=c();t[e]=n,d(t)},j=function(e){return c()[e]},h=function(e){var n=c();console.info("deleted -> "+e+"=="+n[e]),delete n[e],d(n)},p=function(e){return(0,a.E)()?e+"_page_fullState_pwa":e+"_page_fullState"},g=function(e,n){var t=c();t[p(e)]=n,d(t)},b=function(e){return!0===c()[p(e)]},m=function(e){var n=c();n.lastOpenedPage=e,d(n)},v=function(){return c().lastOpenedPage}},2677:function(e,n,t){t.d(n,{Z:function(){return c}});var i=t(7462),r=t(8391),a={icon:{tag:"svg",attrs:{viewBox:"0 0 1024 1024",focusable:"false"},children:[{tag:"path",attrs:{d:"M988 548c-19.9 0-36-16.1-36-36 0-59.4-11.6-117-34.6-171.3a440.45 440.45 0 00-94.3-139.9 437.71 437.71 0 00-139.9-94.3C629 83.6 571.4 72 512 72c-19.9 0-36-16.1-36-36s16.1-36 36-36c69.1 0 136.2 13.5 199.3 40.3C772.3 66 827 103 874 150c47 47 83.9 101.8 109.7 162.7 26.7 63.1 40.2 130.2 40.2 199.3.1 19.9-16 36-35.9 36z"}}]},name:"loading",theme:"outlined"},l=t(9345),f=function(e,n){return r.createElement(l.Z,(0,i.Z)({},e,{ref:n,icon:a}))};var o=r.forwardRef(f),s=t(8095),u=t(3712),c=function(){return(0,u.jsx)(s.Z,{delay:200,style:{position:"fixed",top:80,right:24},indicator:(0,u.jsx)(o,{style:{fontSize:24},spin:!0})})}},1522:function(e,n,t){t.d(n,{a9:function(){return f},bI:function(){return i},iO:function(){return a},vZ:function(){return r}});var i=function(e){return Object.keys(e).reduce((function(n,t){return void 0===e[t]?n.push("".concat(t,"=")):n.push(t+"="+encodeURIComponent(e[t])),n}),[]).join("&")};function r(e,n){if(e===n)return!0;if("object"!==typeof e||null===e||"object"!==typeof n||null===n)return!1;var t=Object.keys(e),i=Object.keys(n);if(t.length!==i.length)return!1;for(var a=0,f=t;a<f.length;a++){var o=f[a],s=e[o],u=n[o],c=l(s)&&l(u);if(c&&!r(s,u)||!c&&s!==u)return!1}return!0}function a(e,n){var t=e.startsWith("?"),i=new URLSearchParams(t?e.substring(1):e);i.delete(n);var r=i.toString();return r?t?"?".concat(r):r:""}function l(e){return null!=e&&"object"===typeof e}var f=function(e){return e.search.length<=0?e.pathname:e.pathname+e.search}}}]);