"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[719],{7719:function(e,n,t){t.r(n),t.d(n,{default:function(){return I}});var i=t(15861),r=t(29439),a=t(4942),o=t(1413),l=t(87757),f=t.n(l),s=t(58467),u=t(47313),c=t(38475),d=function(){var e=(0,i.Z)(f().mark((function e(n){var t,i;return f().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,c.Z.get("/api/admin"+n);case 2:return t=e.sent,i=t.data,e.abrupt("return",i);case 5:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}(),x=t(30695),j=t(88433),h=t(83069),p=t(82294),g=t(88783),b=t(57743),m=t(46417),S=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(752),t.e(772),t.e(121),t.e(444),t.e(399),t.e(72)]).then(t.bind(t,49072))})),w=(0,u.lazy)((function(){return t.e(26).then(t.bind(t,84026))})),y=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(642),t.e(939),t.e(711)]).then(t.bind(t,95711))})),v=(0,u.lazy)((function(){return t.e(700).then(t.bind(t,76700))})),Z=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(772),t.e(858),t.e(896)]).then(t.bind(t,1896))})),R=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(752),t.e(772),t.e(121),t.e(19),t.e(788),t.e(944)]).then(t.bind(t,54944))})),k=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(642),t.e(939),t.e(34)]).then(t.bind(t,93271))})),q=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(642),t.e(939),t.e(19),t.e(610)]).then(t.bind(t,61100))})),A=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(642),t.e(939),t.e(19),t.e(538)]).then(t.bind(t,13087))})),W=(0,u.lazy)((function(){return t.e(271).then(t.bind(t,6271))})),z=(0,u.lazy)((function(){return Promise.resolve().then(t.bind(t,19495))})),_=(0,u.lazy)((function(){return t.e(799).then(t.bind(t,31799))})),P=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(752),t.e(121),t.e(19),t.e(788),t.e(861)]).then(t.bind(t,77861))})),E=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(91)]).then(t.bind(t,49752))})),O=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(376),t.e(642),t.e(939),t.e(9)]).then(t.bind(t,92009))})),D=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(752),t.e(121),t.e(986)]).then(t.bind(t,25731))})),T=(0,u.lazy)((function(){return Promise.resolve().then(t.bind(t,80522))})),U=(0,u.lazy)((function(){return Promise.all([t.e(736),t.e(609),t.e(642),t.e(752),t.e(444),t.e(379),t.e(423)]).then(t.bind(t,90423))})),C=function(e){var n=(0,g.sR)().websiteTitle+" - "+(0,g.sR)()["admin.management"];e?(0,b.Ee)()?window.document.title=e.replace(" - "+n,""):window.document.title=e:window.document.title=n},I=function(e){var n=e.offline,t=(0,s.TH)(),l=(0,b.Ee)()?(0,h.rb)():null,c=(0,h.Rt)(l||(0,p.a9)(t)),g=t.pathname+t.search,I=(0,o.Z)((0,o.Z)({},(0,h.or)()),{},(0,a.Z)({},g,null===j.qj||void 0===j.qj?void 0:j.qj.pageData)),J=(0,u.useRef)(j.qj&&j.qj.pageData),N=(0,u.useState)({currentUri:g,axiosRequesting:!1,offline:n,fullScreen:c,data:I}),M=(0,r.Z)(N,2),V=M[0],F=M[1],K=function(){var e=(0,h.uz)(t);return void 0!==V.data[e]&&null!==V.data[e]?V.data[e]:void 0},$=function(){var e=(0,i.Z)(f().mark((function e(n,t){var i,r,a,l;return f().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,d(n);case 2:if(i=e.sent,r=i.data,a=i.documentTitle,l=V.data,C(a),!(0,p.h$)(l[n],r)){e.next=10;break}return console.info(n+" cache hits"),F((function(e){return(0,o.Z)((0,o.Z)({},e),{},{axiosRequesting:!1})})),e.abrupt("return");case 10:l[n]=r,(0,h.g0)(l),F((function(e){return{offline:e.offline,firstRender:!1,axiosRequesting:!1,currentUri:n,data:l,fullScreen:(0,h.Rt)((0,p.a9)(t))}}));case 13:case"end":return e.stop()}}),e)})));return function(n,t){return e.apply(this,arguments)}}();return(0,u.useEffect)((function(){var e=(0,h.uz)(t);if(K()){if(J.current)return void(J.current=!1);F((function(n){return{currentUri:e,axiosRequesting:!0,fullScreen:(0,h.Rt)((0,p.a9)(t)),data:n.data,offline:n.offline}}))}$(e,t).then((function(){})).catch((function(){F((function(n){return{offline:n.offline,currentUri:e,axiosRequesting:!1,data:n.data,fullScreen:(0,h.Rt)((0,p.a9)(t))}}))}))}),[t.pathname,t.search]),(0,u.useEffect)((function(){F((function(e){return(0,o.Z)((0,o.Z)({},e),{},{offline:n})}))}),[n]),(0,m.jsxs)(s.Z5,{children:[(0,m.jsx)(s.AW,{path:"index",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(Z,{data:K()})})})}),(0,m.jsx)(s.AW,{path:"",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(Z,{data:K()})})})}),(0,m.jsx)(s.AW,{path:"comment",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(y,{offline:n,data:K()})})})}),(0,m.jsx)(s.AW,{path:"plugin",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(v,{})})})}),(0,m.jsx)(s.AW,{path:"website",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(R,{offline:V.offline,data:K()})})})}),(0,m.jsx)(s.AW,{path:"website/admin",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(R,{offline:V.offline,data:K()})})})}),(0,m.jsx)(s.AW,{path:"website/template",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(R,{offline:V.offline,data:K()})})})}),(0,m.jsx)(s.AW,{path:"website/other",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(R,{offline:V.offline,data:K()})})})}),(0,m.jsx)(s.AW,{path:"website/blog",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(R,{offline:V.offline,data:K()})})})}),(0,m.jsx)(s.AW,{path:"website/upgrade",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(R,{offline:V.offline,data:K()})})})}),(0,m.jsx)(s.AW,{path:"article-type",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(k,{offline:V.offline,data:K()})})})}),(0,m.jsx)(s.AW,{path:"link",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(q,{offline:V.offline,data:K()})})})}),(0,m.jsx)(s.AW,{path:"nav",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(A,{offline:V.offline,data:K()})})})}),(0,m.jsx)(s.AW,{path:"article",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(O,{offline:V.offline,data:K()})})})}),(0,m.jsx)(s.AW,{path:"article-edit",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,fullScreen:V.fullScreen,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(S,{fullScreen:V.fullScreen,deleteStateCacheOnDestroy:function(){!function(){var e=V.data;e[(0,h.uz)(t)]=void 0,F((function(n){return(0,o.Z)((0,o.Z)({},n),{},{data:e})}))}()},offline:V.offline,onFullScreen:function(){F((function(e){return(0,h.ag)((0,p.a9)(t),!0),(0,o.Z)((0,o.Z)({},e),{},{fullScreen:!0})}))},data:K(),onExitFullScreen:function(){F((function(e){return(0,h.ag)((0,p.a9)(t),!1),(0,o.Z)((0,o.Z)({},e),{},{fullScreen:!1})}))}})})})}),(0,m.jsx)(s.AW,{path:"user",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(D,{offline:V.offline,data:K()})})})}),(0,m.jsx)(s.AW,{path:"template-center",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(_,{data:K()})})})}),(0,m.jsx)(s.AW,{path:"user-update-password",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(E,{offline:V.offline})})})}),(0,m.jsx)(s.AW,{path:"upgrade",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(z,{offline:V.offline,data:K()},K().preUpgradeKey)})})}),(0,m.jsx)(s.AW,{path:"template-config",element:(0,m.jsx)(U,{offline:V.offline,loading:V.axiosRequesting,children:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(P,{offline:V.offline,data:K()})})})}),(0,m.jsx)(s.AW,{path:"403",element:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(T,{data:K(),code:403})})}),(0,m.jsx)(s.AW,{path:"500",element:K()&&(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(T,{data:K(),code:500})})}),(0,m.jsx)(s.AW,{path:"offline",element:(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(w,{})})}),(0,m.jsx)(s.AW,{path:"*",element:(0,m.jsx)(u.Suspense,{fallback:(0,m.jsx)(x.Z,{}),children:(0,m.jsx)(W,{})})})]})}},83069:function(e,n,t){t.d(n,{MW:function(){return x},Rt:function(){return y},ag:function(){return w},fM:function(){return b},g0:function(){return p},or:function(){return h},qD:function(){return v},rb:function(){return Z},rn:function(){return m},se:function(){return g},uz:function(){return j}});var i=t(37532),r=t(88433),a=t(57743),o=t(82294),l=t(88783);function f(e,n){var t=(new TextEncoder).encode(e),r=(new TextEncoder).encode(n),a=new Uint8Array(32);return a.set(r.subarray(0,32)),function(e){for(var n="",t=8192,i=0;i<e.length;i+=t){var r=e.subarray(i,i+t);n+=String.fromCharCode.apply(null,r)}return btoa(n)}(new i.ModeOfOperation.ctr(a).encrypt(t))}function s(e,n){var t=(new TextEncoder).encode(n),r=new Uint8Array(32);r.set(t.subarray(0,32));var a=function(e){for(var n=atob(e),t=n.length,i=new Uint8Array(t),r=0;r<t;r++)i[r]=n.charCodeAt(r);return i}(e),o=new i.ModeOfOperation.ctr(r).decrypt(a);return(new TextDecoder).decode(o)}var u=function(){return r.qj&&r.qj.key&&r.qj&&r.qj.key?r.qj.key:"__DEV__DEV__DEV_"},c=function(){return window.location.host+"_encrypt_page_data"};function d(e,n){var t=arguments.length>2&&void 0!==arguments[2]?arguments[2]:" ";if(e.length>=n)return e.substring(0,n);var i=n-e.length,r=t.repeat(i);return e+r}var x=function(e){m(j(e))},j=function(e){return e.pathname+(0,o.iO)(e.search,l.NJ)},h=function(){var e=localStorage.getItem(c());try{if(e&&e.length>0)return JSON.parse(s(e,d(u(),24)))}catch(n){console.error(n)}return{}},p=function(e){try{localStorage.setItem(c(),f(JSON.stringify(e),d(u(),24)))}catch(n){console.error(n)}},g=function(e,n){var t=h();t[e]=n,p(t)},b=function(e){return h()[e]},m=function(e){var n=h();delete n[e],p(n)},S=function(e){return(0,a.Ee)()?e+"_page_fullScreen_pwa":e+"_page_fullScreen_normal"},w=function(e,n){var t=h();t[S(e)]=n,p(t)},y=function(e){return!0===h()[S(e)]},v=function(e){var n=h();n.lastOpenedPage=e,p(n)},Z=function(){return h().lastOpenedPage}},30695:function(e,n,t){t.d(n,{Z:function(){return c}});var i=t(97460),r=t(47313),a=t(71100),o=t(9126),l=function(e,n){return r.createElement(o.Z,(0,i.Z)({},e,{ref:n,icon:a.Z}))};var f=r.forwardRef(l),s=t(74447),u=t(46417),c=function(){return(0,u.jsx)(s.Z,{delay:200,style:{position:"fixed",top:80,right:24},indicator:(0,u.jsx)(f,{style:{fontSize:24},spin:!0})})}},82294:function(e,n,t){t.d(n,{YK:function(){return d},a9:function(){return s},bE:function(){return x},bI:function(){return a},h$:function(){return l},i2:function(){return c},iO:function(){return f},pj:function(){return u}});t(47313);var i=t(3598),r=t(46417),a=function(e){return Object.keys(e).reduce((function(n,t){return void 0===e[t]?n.push("".concat(t,"=")):n.push(t+"="+encodeURIComponent(e[t])),n}),[]).join("&")};function o(e){if(null===e||"object"!==typeof e)return e;if(e instanceof Date)return e.toISOString();if(e instanceof RegExp)return e.toString();if(Array.isArray(e))return e.map(o);var n={};return Object.keys(e).sort().forEach((function(t){n[t]=o(e[t])})),n}function l(e,n){var t=o(e),i=o(n);return JSON.stringify(t)===JSON.stringify(i)}function f(e,n){var t=e.startsWith("?"),i=new URLSearchParams(t?e.substring(1):e);i.delete(n);var r=i.toString();return r?t?"?".concat(r):r:""}var s=function(e){return e.search.length<=0?e.pathname:e.pathname+e.search},u=function(e){window.onbeforeunloadTips=e,window.onbeforeunload=function(){return e}},c=function(){window.onbeforeunload=null,window.onbeforeunloadTips=null},d=function(e,n){null!==window.onbeforeunload&&(n.warning({title:"\u63d0\u793a",icon:(0,r.jsx)(i.Z,{}),content:window.onbeforeunloadTips}),e.preventDefault())},x=["rgb(22, 119, 255)","rgb(114, 46, 209)","rgb(19, 194, 194)","rgb(82, 196, 26)","rgb(235, 47, 150)","rgb(245, 34, 45)","rgb(250, 140, 22)","rgb(250, 219, 20)","rgb(250, 84, 28)","rgb(47, 84, 235)","rgb(250, 173, 20)","rgb(160, 217, 17)","rgb(0, 0, 0)"]}}]);