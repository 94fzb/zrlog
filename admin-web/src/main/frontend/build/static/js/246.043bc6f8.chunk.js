"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[246],{62246:function(e,n,t){t.r(n);var r=t(15861),i=t(29439),a=t(4942),o=t(1413),l=t(87757),f=t.n(l),s=t(58467),u=t(47313),c=t(67317),d=t(30695),x=t(88433),j=t(83069),h=t(82294),p=t(88783),g=t(57743),b=t(46417),m=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(772),t.e(752),t.e(121),t.e(444),t.e(399),t.e(72)]).then(t.bind(t,49072))})),S=(0,u.lazy)((function(){return t.e(26).then(t.bind(t,84026))})),y=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(642),t.e(939),t.e(711)]).then(t.bind(t,95711))})),v=(0,u.lazy)((function(){return t.e(700).then(t.bind(t,76700))})),w=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(772),t.e(829)]).then(t.bind(t,29829))})),Z=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(772),t.e(752),t.e(121),t.e(19),t.e(788),t.e(944)]).then(t.bind(t,54944))})),R=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(642),t.e(939),t.e(34)]).then(t.bind(t,93271))})),k=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(642),t.e(939),t.e(19),t.e(610)]).then(t.bind(t,61100))})),q=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(642),t.e(939),t.e(19),t.e(538)]).then(t.bind(t,13087))})),A=(0,u.lazy)((function(){return t.e(271).then(t.bind(t,6271))})),W=(0,u.lazy)((function(){return Promise.resolve().then(t.bind(t,19495))})),z=(0,u.lazy)((function(){return t.e(799).then(t.bind(t,31799))})),_=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(752),t.e(121),t.e(19),t.e(788),t.e(861)]).then(t.bind(t,77861))})),P=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(91)]).then(t.bind(t,49752))})),E=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(642),t.e(939),t.e(9)]).then(t.bind(t,92009))})),O=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(752),t.e(121),t.e(986)]).then(t.bind(t,25731))})),U=(0,u.lazy)((function(){return Promise.resolve().then(t.bind(t,80522))})),D=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(772),t.e(726),t.e(305)]).then(t.bind(t,13305))})),T=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(642),t.e(752),t.e(444),t.e(59),t.e(423)]).then(t.bind(t,90423))})),C=function(e){var n=(0,p.sR)().websiteTitle+" - "+(0,p.sR)()["admin.management"];e?(0,g.Ee)()?window.document.title=e.replace(" - "+n,""):window.document.title=e:window.document.title=n};n.default=function(e){var n=e.offline,t=(0,s.TH)(),l=(0,g.Ee)()?(0,j.rb)():null,p=(0,j.Rt)(l||(0,h.a9)(t)),I=t.pathname+t.search,N=(0,o.Z)((0,o.Z)({},(0,j.or)()),{},(0,a.Z)({},I,null===x.qj||void 0===x.qj?void 0:x.qj.pageData)),J=(0,u.useRef)(x.qj&&x.qj.pageData),M=(0,u.useState)({currentUri:I,axiosRequesting:!1,offline:n,fullScreen:p,data:N}),L=(0,i.Z)(M,2),V=L[0],F=L[1],K=function(){var e=(0,j.uz)(t);return void 0!==V.data[e]&&null!==V.data[e]?V.data[e]:void 0},$=function(){var e=(0,r.Z)(f().mark((function e(n,t){var r,i,a,l;return f().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,(0,c.L)(n);case 2:if(r=e.sent,i=r.data,a=r.documentTitle,l=V.data,C(a),!(0,h.h$)(l[n],i)){e.next=10;break}return console.info(n+" cache hits"),F((function(e){return(0,o.Z)((0,o.Z)({},e),{},{axiosRequesting:!1})})),e.abrupt("return");case 10:l[n]=i,(0,j.g0)(l),F((function(e){return{offline:e.offline,firstRender:!1,axiosRequesting:!1,currentUri:n,data:l,fullScreen:(0,j.Rt)((0,h.a9)(t))}}));case 13:case"end":return e.stop()}}),e)})));return function(n,t){return e.apply(this,arguments)}}();return(0,u.useEffect)((function(){var e=(0,j.uz)(t);if(K()){if(J.current)return void(J.current=!1);F((function(n){return{currentUri:e,axiosRequesting:!0,fullScreen:(0,j.Rt)((0,h.a9)(t)),data:n.data,offline:n.offline}}))}$(e,t).then((function(){})).catch((function(){F((function(n){return{offline:n.offline,currentUri:e,axiosRequesting:!1,data:n.data,fullScreen:(0,j.Rt)((0,h.a9)(t))}}))}))}),[t.pathname,t.search]),(0,u.useEffect)((function(){F((function(e){return(0,o.Z)((0,o.Z)({},e),{},{offline:n})}))}),[n]),(0,b.jsxs)(s.Z5,{children:[(0,b.jsx)(s.AW,{path:"index",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(w,{data:K()})})})}),(0,b.jsx)(s.AW,{path:"",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(w,{data:K()})})})}),(0,b.jsx)(s.AW,{path:"comment",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(y,{offline:n,data:K()})})})}),(0,b.jsx)(s.AW,{path:"plugin",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(v,{})})})}),(0,b.jsx)(s.AW,{path:"website",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(Z,{offline:V.offline,data:K()})})})}),(0,b.jsx)(s.AW,{path:"website/admin",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(Z,{offline:V.offline,data:K()})})})}),(0,b.jsx)(s.AW,{path:"website/template",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(Z,{offline:V.offline,data:K()})})})}),(0,b.jsx)(s.AW,{path:"website/other",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(Z,{offline:V.offline,data:K()})})})}),(0,b.jsx)(s.AW,{path:"website/blog",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(Z,{offline:V.offline,data:K()})})})}),(0,b.jsx)(s.AW,{path:"website/upgrade",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(Z,{offline:V.offline,data:K()})})})}),(0,b.jsx)(s.AW,{path:"article-type",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(R,{offline:V.offline,data:K()})})})}),(0,b.jsx)(s.AW,{path:"link",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(k,{offline:V.offline,data:K()})})})}),(0,b.jsx)(s.AW,{path:"nav",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(q,{offline:V.offline,data:K()})})})}),(0,b.jsx)(s.AW,{path:"article",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(E,{offline:V.offline,data:K()})})})}),(0,b.jsx)(s.AW,{path:"article-edit",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,fullScreen:V.fullScreen,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(m,{fullScreen:V.fullScreen,deleteStateCacheOnDestroy:function(){!function(){var e=V.data;e[(0,j.uz)(t)]=void 0,F((function(n){return(0,o.Z)((0,o.Z)({},n),{},{data:e})}))}()},offline:V.offline,onFullScreen:function(){F((function(e){return(0,j.ag)((0,h.a9)(t),!0),(0,o.Z)((0,o.Z)({},e),{},{fullScreen:!0})}))},data:K(),onExitFullScreen:function(){F((function(e){return(0,j.ag)((0,h.a9)(t),!1),(0,o.Z)((0,o.Z)({},e),{},{fullScreen:!1})}))}})})})}),(0,b.jsx)(s.AW,{path:"user",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(O,{offline:V.offline,data:K()})})})}),(0,b.jsx)(s.AW,{path:"template-center",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(z,{data:K()})})})}),(0,b.jsx)(s.AW,{path:"user-update-password",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(P,{offline:V.offline})})})}),(0,b.jsx)(s.AW,{path:"upgrade",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(W,{offline:V.offline,data:K()},K().preUpgradeKey)})})}),(0,b.jsx)(s.AW,{path:"template-config",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(_,{offline:V.offline,data:K()})})})}),(0,b.jsx)(s.AW,{path:"403",element:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(U,{data:K(),code:403})})}),(0,b.jsx)(s.AW,{path:"500",element:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(U,{data:K(),code:500})})}),(0,b.jsx)(s.AW,{path:"offline",element:(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(S,{})})}),(0,b.jsx)(s.AW,{path:"system",element:(0,b.jsx)(T,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(D,{offline:V.offline,data:K()})})})}),(0,b.jsx)(s.AW,{path:"*",element:(0,b.jsx)(u.Suspense,{fallback:(0,b.jsx)(d.Z,{}),children:(0,b.jsx)(A,{})})})]})}},67317:function(e,n,t){t.d(n,{L:function(){return l}});var r=t(15861),i=t(87757),a=t.n(i),o=t(38475),l=function(){var e=(0,r.Z)(a().mark((function e(n){var t,r;return a().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,o.Z.get("/api/admin"+n);case 2:return t=e.sent,r=t.data,e.abrupt("return",r);case 5:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}()},83069:function(e,n,t){t.d(n,{MW:function(){return x},Rt:function(){return w},ag:function(){return v},fM:function(){return m},g0:function(){return g},or:function(){return h},qD:function(){return Z},rb:function(){return R},sR:function(){return S},se:function(){return b},t2:function(){return p},uz:function(){return j}});var r=t(37532),i=t(88433),a=t(57743),o=t(82294),l=t(88783);function f(e,n){var t=(new TextEncoder).encode(e),i=(new TextEncoder).encode(n),a=new Uint8Array(32);return a.set(i.subarray(0,32)),function(e){for(var n="",t=8192,r=0;r<e.length;r+=t){var i=e.subarray(r,r+t);n+=String.fromCharCode.apply(null,i)}return btoa(n)}(new r.ModeOfOperation.ctr(a).encrypt(t))}function s(e,n){var t=(new TextEncoder).encode(n),i=new Uint8Array(32);i.set(t.subarray(0,32));var a=function(e){for(var n=atob(e),t=n.length,r=new Uint8Array(t),i=0;i<t;i++)r[i]=n.charCodeAt(i);return r}(e),o=new r.ModeOfOperation.ctr(i).decrypt(a);return(new TextDecoder).decode(o)}var u=function(){return i.qj&&i.qj.key&&i.qj&&i.qj.key?i.qj.key:"__DEV__DEV__DEV_"},c=function(){return window.location.host+"_encrypt_page_data"};function d(e,n){var t=arguments.length>2&&void 0!==arguments[2]?arguments[2]:" ";if(e.length>=n)return e.substring(0,n);var r=n-e.length,i=t.repeat(r);return e+i}var x=function(e){S(j(e))},j=function(e){return e.pathname+(0,o.iO)(e.search,l.NJ)},h=function(){var e=localStorage.getItem(c());try{if(e&&e.length>0)return JSON.parse(s(e,d(u(),24)))}catch(n){console.error(n)}return{}},p=function(){try{localStorage.removeItem(c())}catch(e){console.error(e)}},g=function(e){try{localStorage.setItem(c(),f(JSON.stringify(e),d(u(),24)))}catch(n){console.error(n)}},b=function(e,n){var t=h();t[e]=n,g(t)},m=function(e){return h()[e]},S=function(e){var n=h();delete n[e],g(n)},y=function(e){return(0,a.Ee)()?e+"_page_fullScreen_pwa":e+"_page_fullScreen_normal"},v=function(e,n){var t=h();t[y(e)]=n,g(t)},w=function(e){return!0===h()[y(e)]},Z=function(e){var n=h();n.lastOpenedPage=e,g(n)},R=function(){return h().lastOpenedPage}},30695:function(e,n,t){t.d(n,{Z:function(){return c}});var r=t(97460),i=t(47313),a=t(71100),o=t(9126),l=function(e,n){return i.createElement(o.Z,(0,r.Z)({},e,{ref:n,icon:a.Z}))};var f=i.forwardRef(l),s=t(74447),u=t(46417),c=function(){return(0,u.jsx)(s.Z,{delay:200,style:{position:"fixed",top:80,right:24},indicator:(0,u.jsx)(f,{style:{fontSize:24},spin:!0})})}},82294:function(e,n,t){t.d(n,{YK:function(){return x},a9:function(){return u},bE:function(){return j},bI:function(){return a},eN:function(){return o},h$:function(){return f},i2:function(){return d},iO:function(){return s},pj:function(){return c}});t(47313);var r=t(3598),i=t(46417),a=function(e){return Object.keys(e).reduce((function(n,t){return void 0===e[t]?n.push("".concat(t,"=")):n.push(t+"="+encodeURIComponent(e[t])),n}),[]).join("&")},o=function(e){var n=new URLSearchParams(e),t=new Map;return n.forEach((function(e,n){t.set(n,e)})),t};function l(e){if(null===e||"object"!==typeof e)return e;if(e instanceof Date)return e.toISOString();if(e instanceof RegExp)return e.toString();if(Array.isArray(e))return e.map(l);var n={};return Object.keys(e).sort().forEach((function(t){n[t]=l(e[t])})),n}function f(e,n){var t=l(e),r=l(n);return JSON.stringify(t)===JSON.stringify(r)}function s(e,n){var t=e.startsWith("?"),r=new URLSearchParams(t?e.substring(1):e);r.delete(n);var i=r.toString();return i?t?"?".concat(i):i:""}var u=function(e){return e.search.length<=0?e.pathname:e.pathname+e.search},c=function(e){window.onbeforeunloadTips=e,window.onbeforeunload=function(){return e}},d=function(){window.onbeforeunload=null,window.onbeforeunloadTips=null},x=function(e,n){null!==window.onbeforeunload&&(n.warning({title:"\u63d0\u793a",icon:(0,i.jsx)(r.Z,{}),content:window.onbeforeunloadTips}),e.preventDefault())},j=["rgb(22, 119, 255)","rgb(114, 46, 209)","rgb(19, 194, 194)","rgb(82, 196, 26)","rgb(235, 47, 150)","rgb(245, 34, 45)","rgb(250, 140, 22)","rgb(250, 219, 20)","rgb(250, 84, 28)","rgb(47, 84, 235)","rgb(250, 173, 20)","rgb(160, 217, 17)","rgb(0, 0, 0)"]}}]);