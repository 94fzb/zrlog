"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[639],{6226:function(e,t){t.Z={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M482 152h60q8 0 8 8v704q0 8-8 8h-60q-8 0-8-8V160q0-8 8-8z"}},{tag:"path",attrs:{d:"M192 474h672q8 0 8 8v60q0 8-8 8H160q-8 0-8-8v-60q0-8 8-8z"}}]},name:"plus",theme:"outlined"}},9650:function(e,t,n){n.d(t,{Z:function(){return it}});var r=n(7462),a=n(4942),o=n(1413),i=n(9439),c=n(1002),u=n(4925),l=n(8803),s=n.n(l),f=n(7850),d=n(8391),v=(0,d.createContext)(null),m=n(3433),p=n(8293),b=n(4287),h=n(6676),y=n(389),g=function(e){var t=e.activeTabOffset,n=e.horizontal,r=e.rtl,a=e.indicator,o=void 0===a?{}:a,c=o.size,u=o.align,l=void 0===u?"center":u,s=(0,d.useState)(),f=(0,i.Z)(s,2),v=f[0],m=f[1],p=(0,d.useRef)(),b=d.useCallback((function(e){return"function"===typeof c?c(e):"number"===typeof c?c:e}),[c]);function h(){y.Z.cancel(p.current)}return(0,d.useEffect)((function(){var e={};if(t)if(n){e.width=b(t.width);var a=r?"right":"left";"start"===l&&(e[a]=t[a]),"center"===l&&(e[a]=t[a]+t.width/2,e.transform=r?"translateX(50%)":"translateX(-50%)"),"end"===l&&(e[a]=t[a]+t.width,e.transform="translateX(-100%)")}else e.height=b(t.height),"start"===l&&(e.top=t.top),"center"===l&&(e.top=t.top+t.height/2,e.transform="translateY(-50%)"),"end"===l&&(e.top=t.top+t.height,e.transform="translateY(-100%)");return h(),p.current=(0,y.Z)((function(){m(e)})),h}),[t,n,r,l,b]),{style:v}},Z={width:0,height:0,left:0,top:0};function E(e,t){var n=d.useRef(e),r=d.useState({}),a=(0,i.Z)(r,2)[1];return[n.current,function(e){var r="function"===typeof e?e(n.current):e;r!==n.current&&t(r,n.current),n.current=r,a({})}]}var k=Math.pow(.995,20);var w=n(9501);function C(e){var t=(0,d.useState)(0),n=(0,i.Z)(t,2),r=n[0],a=n[1],o=(0,d.useRef)(0),c=(0,d.useRef)();return c.current=e,(0,w.o)((function(){var e;null===(e=c.current)||void 0===e||e.call(c)}),[r]),function(){o.current===r&&(o.current+=1,a(o.current))}}var x={width:0,height:0,left:0,top:0,right:0};function S(e){var t;return e instanceof Map?(t={},e.forEach((function(e,n){t[n]=e}))):t=e,JSON.stringify(t)}function R(e){return String(e).replace(/"/g,"TABS_DQ")}function N(e,t,n,r){return!(!n||r||!1===e||void 0===e&&(!1===t||null===t))}var P=d.forwardRef((function(e,t){var n=e.prefixCls,r=e.editable,a=e.locale,o=e.style;return r&&!1!==r.showAdd?d.createElement("button",{ref:t,type:"button",className:"".concat(n,"-nav-add"),style:o,"aria-label":(null===a||void 0===a?void 0:a.addAriaLabel)||"Add tab",onClick:function(e){r.onEdit("add",{event:e})}},r.addIcon||"+"):null})),T=P;var A=d.forwardRef((function(e,t){var n,r=e.position,a=e.prefixCls,o=e.extra;if(!o)return null;var i={};return"object"!==(0,c.Z)(o)||d.isValidElement(o)?i.right=o:i=o,"right"===r&&(n=i.right),"left"===r&&(n=i.left),n?d.createElement("div",{className:"".concat(a,"-extra-content"),ref:t},n):null})),L=n(8555),M=n(5846),I=n(4536),B=d.forwardRef((function(e,t){var n=e.prefixCls,o=e.id,c=e.tabs,u=e.locale,l=e.mobile,f=e.more,v=void 0===f?{}:f,m=e.style,p=e.className,b=e.editable,h=e.tabBarGutter,y=e.rtl,g=e.removeAriaLabel,Z=e.onTabClick,E=e.getPopupContainer,k=e.popupClassName,w=(0,d.useState)(!1),C=(0,i.Z)(w,2),x=C[0],S=C[1],R=(0,d.useState)(null),P=(0,i.Z)(R,2),A=P[0],B=P[1],j=v.icon,D=void 0===j?"More":j,z="".concat(o,"-more-popup"),O="".concat(n,"-dropdown"),q=null!==A?"".concat(z,"-").concat(A):null,K=null===u||void 0===u?void 0:u.dropdownAriaLabel;var V=d.createElement(M.ZP,{onClick:function(e){var t=e.key,n=e.domEvent;Z(t,n),S(!1)},prefixCls:"".concat(O,"-menu"),id:z,tabIndex:-1,role:"listbox","aria-activedescendant":q,selectedKeys:[A],"aria-label":void 0!==K?K:"expanded dropdown"},c.map((function(e){var t=e.closable,n=e.disabled,r=e.closeIcon,a=e.key,i=e.label,c=N(t,r,b,n);return d.createElement(M.sN,{key:a,id:"".concat(z,"-").concat(a),role:"option","aria-controls":o&&"".concat(o,"-panel-").concat(a),disabled:n},d.createElement("span",null,i),c&&d.createElement("button",{type:"button","aria-label":g||"remove",tabIndex:0,className:"".concat(O,"-menu-item-remove"),onClick:function(e){e.stopPropagation(),function(e,t){e.preventDefault(),e.stopPropagation(),b.onEdit("remove",{key:t,event:e})}(e,a)}},r||b.removeIcon||"\xd7"))})));function _(e){for(var t=c.filter((function(e){return!e.disabled})),n=t.findIndex((function(e){return e.key===A}))||0,r=t.length,a=0;a<r;a+=1){var o=t[n=(n+e+r)%r];if(!o.disabled)return void B(o.key)}}(0,d.useEffect)((function(){var e=document.getElementById(q);e&&e.scrollIntoView&&e.scrollIntoView(!1)}),[A]),(0,d.useEffect)((function(){x||B(null)}),[x]);var W=(0,a.Z)({},y?"marginRight":"marginLeft",h);c.length||(W.visibility="hidden",W.order=1);var F=s()((0,a.Z)({},"".concat(O,"-rtl"),y)),X=l?null:d.createElement(L.Z,(0,r.Z)({prefixCls:O,overlay:V,visible:!!c.length&&x,onVisibleChange:S,overlayClassName:s()(F,k),mouseEnterDelay:.1,mouseLeaveDelay:.1,getPopupContainer:E},v),d.createElement("button",{type:"button",className:"".concat(n,"-nav-more"),style:W,tabIndex:-1,"aria-hidden":"true","aria-haspopup":"listbox","aria-controls":z,id:"".concat(o,"-more"),"aria-expanded":x,onKeyDown:function(e){var t=e.which;if(x)switch(t){case I.Z.UP:_(-1),e.preventDefault();break;case I.Z.DOWN:_(1),e.preventDefault();break;case I.Z.ESC:S(!1);break;case I.Z.SPACE:case I.Z.ENTER:null!==A&&Z(A,e)}else[I.Z.DOWN,I.Z.SPACE,I.Z.ENTER].includes(t)&&(S(!0),e.preventDefault())}},D));return d.createElement("div",{className:s()("".concat(n,"-nav-operations"),p),style:m,ref:t},X,d.createElement(T,{prefixCls:n,locale:u,editable:b}))})),j=d.memo(B,(function(e,t){return t.tabMoving})),D=function(e){var t=e.prefixCls,n=e.id,r=e.active,o=e.tab,i=o.key,c=o.label,u=o.disabled,l=o.closeIcon,f=o.icon,v=e.closable,m=e.renderWrapper,p=e.removeAriaLabel,b=e.editable,h=e.onClick,y=e.onFocus,g=e.style,Z="".concat(t,"-tab"),E=N(v,l,b,u);function k(e){u||h(e)}var w=d.useMemo((function(){return f&&"string"===typeof c?d.createElement("span",null,c):c}),[c,f]),C=d.createElement("div",{key:i,"data-node-key":R(i),className:s()(Z,(0,a.Z)((0,a.Z)((0,a.Z)({},"".concat(Z,"-with-remove"),E),"".concat(Z,"-active"),r),"".concat(Z,"-disabled"),u)),style:g,onClick:k},d.createElement("div",{role:"tab","aria-selected":r,id:n&&"".concat(n,"-tab-").concat(i),className:"".concat(Z,"-btn"),"aria-controls":n&&"".concat(n,"-panel-").concat(i),"aria-disabled":u,tabIndex:u?null:0,onClick:function(e){e.stopPropagation(),k(e)},onKeyDown:function(e){[I.Z.SPACE,I.Z.ENTER].includes(e.which)&&(e.preventDefault(),k(e))},onFocus:y},f&&d.createElement("span",{className:"".concat(Z,"-icon")},f),c&&w),E&&d.createElement("button",{type:"button","aria-label":p||"remove",tabIndex:0,className:"".concat(Z,"-remove"),onClick:function(e){var t;e.stopPropagation(),(t=e).preventDefault(),t.stopPropagation(),b.onEdit("remove",{key:i,event:t})}},l||b.removeIcon||"\xd7"));return m?m(C):C},z=function(e){var t=e.current||{},n=t.offsetWidth,r=void 0===n?0:n,a=t.offsetHeight,o=void 0===a?0:a;if(e.current){var i=e.current.getBoundingClientRect(),c=i.width,u=i.height;if(Math.abs(c-r)<1)return[c,u]}return[r,o]},O=function(e,t){return e[t?0:1]},q=d.forwardRef((function(e,t){var n=e.className,c=e.style,u=e.id,l=e.animated,f=e.activeKey,y=e.rtl,w=e.extra,N=e.editable,P=e.locale,L=e.tabPosition,M=e.tabBarGutter,I=e.children,B=e.onTabClick,q=e.onTabScroll,K=e.indicator,V=d.useContext(v),_=V.prefixCls,W=V.tabs,F=(0,d.useRef)(null),X=(0,d.useRef)(null),G=(0,d.useRef)(null),H=(0,d.useRef)(null),Y=(0,d.useRef)(null),U=(0,d.useRef)(null),J=(0,d.useRef)(null),Q="top"===L||"bottom"===L,$=E(0,(function(e,t){Q&&q&&q({direction:e>t?"left":"right"})})),ee=(0,i.Z)($,2),te=ee[0],ne=ee[1],re=E(0,(function(e,t){!Q&&q&&q({direction:e>t?"top":"bottom"})})),ae=(0,i.Z)(re,2),oe=ae[0],ie=ae[1],ce=(0,d.useState)([0,0]),ue=(0,i.Z)(ce,2),le=ue[0],se=ue[1],fe=(0,d.useState)([0,0]),de=(0,i.Z)(fe,2),ve=de[0],me=de[1],pe=(0,d.useState)([0,0]),be=(0,i.Z)(pe,2),he=be[0],ye=be[1],ge=(0,d.useState)([0,0]),Ze=(0,i.Z)(ge,2),Ee=Ze[0],ke=Ze[1],we=function(e){var t=(0,d.useRef)([]),n=(0,d.useState)({}),r=(0,i.Z)(n,2)[1],a=(0,d.useRef)("function"===typeof e?e():e),o=C((function(){var e=a.current;t.current.forEach((function(t){e=t(e)})),t.current=[],a.current=e,r({})}));return[a.current,function(e){t.current.push(e),o()}]}(new Map),Ce=(0,i.Z)(we,2),xe=Ce[0],Se=Ce[1],Re=function(e,t,n){return(0,d.useMemo)((function(){for(var n,r=new Map,a=t.get(null===(n=e[0])||void 0===n?void 0:n.key)||Z,i=a.left+a.width,c=0;c<e.length;c+=1){var u,l=e[c].key,s=t.get(l);s||(s=t.get(null===(u=e[c-1])||void 0===u?void 0:u.key)||Z);var f=r.get(l)||(0,o.Z)({},s);f.right=i-f.left-f.width,r.set(l,f)}return r}),[e.map((function(e){return e.key})).join("_"),t,n])}(W,xe,ve[0]),Ne=O(le,Q),Pe=O(ve,Q),Te=O(he,Q),Ae=O(Ee,Q),Le=Ne<Pe+Te,Me=Le?Ne-Ae:Ne-Te,Ie="".concat(_,"-nav-operations-hidden"),Be=0,je=0;function De(e){return e<Be?Be:e>je?je:e}Q&&y?(Be=0,je=Math.max(0,Pe-Me)):(Be=Math.min(0,Me-Pe),je=0);var ze=(0,d.useRef)(null),Oe=(0,d.useState)(),qe=(0,i.Z)(Oe,2),Ke=qe[0],Ve=qe[1];function _e(){Ve(Date.now())}function We(){ze.current&&clearTimeout(ze.current)}!function(e,t){var n=(0,d.useState)(),r=(0,i.Z)(n,2),a=r[0],o=r[1],c=(0,d.useState)(0),u=(0,i.Z)(c,2),l=u[0],s=u[1],f=(0,d.useState)(0),v=(0,i.Z)(f,2),m=v[0],p=v[1],b=(0,d.useState)(),h=(0,i.Z)(b,2),y=h[0],g=h[1],Z=(0,d.useRef)(),E=(0,d.useRef)(),w=(0,d.useRef)(null);w.current={onTouchStart:function(e){var t=e.touches[0],n=t.screenX,r=t.screenY;o({x:n,y:r}),window.clearInterval(Z.current)},onTouchMove:function(e){if(a){e.preventDefault();var n=e.touches[0],r=n.screenX,i=n.screenY;o({x:r,y:i});var c=r-a.x,u=i-a.y;t(c,u);var f=Date.now();s(f),p(f-l),g({x:c,y:u})}},onTouchEnd:function(){if(a&&(o(null),g(null),y)){var e=y.x/m,n=y.y/m,r=Math.abs(e),i=Math.abs(n);if(Math.max(r,i)<.1)return;var c=e,u=n;Z.current=window.setInterval((function(){Math.abs(c)<.01&&Math.abs(u)<.01?window.clearInterval(Z.current):t(20*(c*=k),20*(u*=k))}),20)}},onWheel:function(e){var n=e.deltaX,r=e.deltaY,a=0,o=Math.abs(n),i=Math.abs(r);o===i?a="x"===E.current?n:r:o>i?(a=n,E.current="x"):(a=r,E.current="y"),t(-a,-a)&&e.preventDefault()}},d.useEffect((function(){function t(e){w.current.onTouchMove(e)}function n(e){w.current.onTouchEnd(e)}return document.addEventListener("touchmove",t,{passive:!1}),document.addEventListener("touchend",n,{passive:!0}),e.current.addEventListener("touchstart",(function(e){w.current.onTouchStart(e)}),{passive:!0}),e.current.addEventListener("wheel",(function(e){w.current.onWheel(e)}),{passive:!1}),function(){document.removeEventListener("touchmove",t),document.removeEventListener("touchend",n)}}),[])}(H,(function(e,t){function n(e,t){e((function(e){return De(e+t)}))}return!!Le&&(Q?n(ne,e):n(ie,t),We(),_e(),!0)})),(0,d.useEffect)((function(){return We(),Ke&&(ze.current=setTimeout((function(){Ve(0)}),100)),We}),[Ke]);var Fe=function(e,t,n,r,a,o,i){var c,u,l,s=i.tabs,f=i.tabPosition,v=i.rtl;return["top","bottom"].includes(f)?(c="width",u=v?"right":"left",l=Math.abs(n)):(c="height",u="top",l=-n),(0,d.useMemo)((function(){if(!s.length)return[0,0];for(var n=s.length,r=n,a=0;a<n;a+=1){var o=e.get(s[a].key)||x;if(o[u]+o[c]>l+t){r=a-1;break}}for(var i=0,f=n-1;f>=0;f-=1)if((e.get(s[f].key)||x)[u]<l){i=f+1;break}return i>=r?[0,0]:[i,r]}),[e,t,r,a,o,l,f,s.map((function(e){return e.key})).join("_"),v])}(Re,Me,Q?te:oe,Pe,Te,Ae,(0,o.Z)((0,o.Z)({},e),{},{tabs:W})),Xe=(0,i.Z)(Fe,2),Ge=Xe[0],He=Xe[1],Ye=(0,b.Z)((function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:f,t=Re.get(e)||{width:0,height:0,left:0,right:0,top:0};if(Q){var n=te;y?t.right<te?n=t.right:t.right+t.width>te+Me&&(n=t.right+t.width-Me):t.left<-te?n=-t.left:t.left+t.width>-te+Me&&(n=-(t.left+t.width-Me)),ie(0),ne(De(n))}else{var r=oe;t.top<-oe?r=-t.top:t.top+t.height>-oe+Me&&(r=-(t.top+t.height-Me)),ne(0),ie(De(r))}})),Ue={};"top"===L||"bottom"===L?Ue[y?"marginRight":"marginLeft"]=M:Ue.marginTop=M;var Je=W.map((function(e,t){var n=e.key;return d.createElement(D,{id:u,prefixCls:_,key:n,tab:e,style:0===t?void 0:Ue,closable:e.closable,editable:N,active:n===f,renderWrapper:I,removeAriaLabel:null===P||void 0===P?void 0:P.removeAriaLabel,onClick:function(e){B(n,e)},onFocus:function(){Ye(n),_e(),H.current&&(y||(H.current.scrollLeft=0),H.current.scrollTop=0)}})})),Qe=function(){return Se((function(){var e,t=new Map,n=null===(e=Y.current)||void 0===e?void 0:e.getBoundingClientRect();return W.forEach((function(e){var r,a=e.key,o=null===(r=Y.current)||void 0===r?void 0:r.querySelector('[data-node-key="'.concat(R(a),'"]'));if(o){var c=function(e,t){var n=e.offsetWidth,r=e.offsetHeight,a=e.offsetTop,o=e.offsetLeft,i=e.getBoundingClientRect(),c=i.width,u=i.height,l=i.x,s=i.y;return Math.abs(c-n)<1?[c,u,l-t.x,s-t.y]:[n,r,o,a]}(o,n),u=(0,i.Z)(c,4),l=u[0],s=u[1],f=u[2],d=u[3];t.set(a,{width:l,height:s,left:f,top:d})}})),t}))};(0,d.useEffect)((function(){Qe()}),[W.map((function(e){return e.key})).join("_")]);var $e=C((function(){var e=z(F),t=z(X),n=z(G);se([e[0]-t[0]-n[0],e[1]-t[1]-n[1]]);var r=z(J);ye(r);var a=z(U);ke(a);var o=z(Y);me([o[0]-r[0],o[1]-r[1]]),Qe()})),et=W.slice(0,Ge),tt=W.slice(He+1),nt=[].concat((0,m.Z)(et),(0,m.Z)(tt)),rt=Re.get(f),at=g({activeTabOffset:rt,horizontal:Q,indicator:K,rtl:y}).style;(0,d.useEffect)((function(){Ye()}),[f,Be,je,S(rt),S(Re),Q]),(0,d.useEffect)((function(){$e()}),[y]);var ot,it,ct,ut,lt=!!nt.length,st="".concat(_,"-nav-wrap");return Q?y?(it=te>0,ot=te!==je):(ot=te<0,it=te!==Be):(ct=oe<0,ut=oe!==Be),d.createElement(p.Z,{onResize:$e},d.createElement("div",{ref:(0,h.x1)(t,F),role:"tablist",className:s()("".concat(_,"-nav"),n),style:c,onKeyDown:function(){_e()}},d.createElement(A,{ref:X,position:"left",extra:w,prefixCls:_}),d.createElement(p.Z,{onResize:$e},d.createElement("div",{className:s()(st,(0,a.Z)((0,a.Z)((0,a.Z)((0,a.Z)({},"".concat(st,"-ping-left"),ot),"".concat(st,"-ping-right"),it),"".concat(st,"-ping-top"),ct),"".concat(st,"-ping-bottom"),ut)),ref:H},d.createElement(p.Z,{onResize:$e},d.createElement("div",{ref:Y,className:"".concat(_,"-nav-list"),style:{transform:"translate(".concat(te,"px, ").concat(oe,"px)"),transition:Ke?"none":void 0}},Je,d.createElement(T,{ref:J,prefixCls:_,locale:P,editable:N,style:(0,o.Z)((0,o.Z)({},0===Je.length?void 0:Ue),{},{visibility:lt?"hidden":null})}),d.createElement("div",{className:s()("".concat(_,"-ink-bar"),(0,a.Z)({},"".concat(_,"-ink-bar-animated"),l.inkBar)),style:at}))))),d.createElement(j,(0,r.Z)({},e,{removeAriaLabel:null===P||void 0===P?void 0:P.removeAriaLabel,ref:U,prefixCls:_,tabs:nt,className:!lt&&Ie,tabMoving:!!Ke})),d.createElement(A,{ref:G,position:"right",extra:w,prefixCls:_})))})),K=q,V=d.forwardRef((function(e,t){var n=e.prefixCls,r=e.className,a=e.style,o=e.id,i=e.active,c=e.tabKey,u=e.children;return d.createElement("div",{id:o&&"".concat(o,"-panel-").concat(c),role:"tabpanel",tabIndex:i?0:-1,"aria-labelledby":o&&"".concat(o,"-tab-").concat(c),"aria-hidden":!i,style:a,className:s()(n,i&&"".concat(n,"-active"),r),ref:t},u)}));var _=V,W=["renderTabBar"],F=["label","key"];var X=function(e){var t=e.renderTabBar,n=(0,u.Z)(e,W),a=d.useContext(v).tabs;return t?t((0,o.Z)((0,o.Z)({},n),{},{panes:a.map((function(e){var t=e.label,n=e.key,a=(0,u.Z)(e,F);return d.createElement(_,(0,r.Z)({tab:t,key:n,tabKey:n},a))}))}),K):d.createElement(K,n)},G=n(7650);function H(e){return H="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"==typeof Symbol&&e.constructor===Symbol&&e!==Symbol.prototype?"symbol":typeof e},H(e)}var Y=n(4271);function U(e,t){"function"===typeof e?e(t):"object"===H(e)&&e&&"current"in e&&(e.current=t)}var J=d.createContext({});var Q=n(5671),$=n(3144),ee=n(136),te=n(7277),ne=function(e){(0,ee.Z)(n,e);var t=(0,te.Z)(n);function n(){return(0,Q.Z)(this,n),t.apply(this,arguments)}return(0,$.Z)(n,[{key:"render",value:function(){return this.props.children}}]),n}(d.Component),re=ne;function ae(e,t){(null==t||t>e.length)&&(t=e.length);for(var n=0,r=new Array(t);n<t;n++)r[n]=e[n];return r}function oe(e,t){return function(e){if(Array.isArray(e))return e}(e)||function(e,t){var n=null==e?null:"undefined"!==typeof Symbol&&e[Symbol.iterator]||e["@@iterator"];if(null!=n){var r,a,o=[],i=!0,c=!1;try{for(n=n.call(e);!(i=(r=n.next()).done)&&(o.push(r.value),!t||o.length!==t);i=!0);}catch(u){c=!0,a=u}finally{try{i||null==n.return||n.return()}finally{if(c)throw a}}return o}}(e,t)||function(e,t){if(e){if("string"===typeof e)return ae(e,t);var n=Object.prototype.toString.call(e).slice(8,-1);return"Object"===n&&e.constructor&&(n=e.constructor.name),"Map"===n||"Set"===n?Array.from(e):"Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)?ae(e,t):void 0}}(e,t)||function(){throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}()}function ie(e){var t=d.useRef(!1),n=oe(d.useState(e),2),r=n[0],a=n[1];return d.useEffect((function(){return t.current=!1,function(){t.current=!0}}),[]),[r,function(e,n){n&&t.current||a(e)}]}var ce="none",ue="appear",le="enter",se="leave",fe="none",de="prepare",ve="start",me="active",pe="end",be="prepared";function he(){return!("undefined"===typeof window||!window.document||!window.document.createElement)}function ye(e,t){var n={};return n[e.toLowerCase()]=t.toLowerCase(),n["Webkit".concat(e)]="webkit".concat(t),n["Moz".concat(e)]="moz".concat(t),n["ms".concat(e)]="MS".concat(t),n["O".concat(e)]="o".concat(t.toLowerCase()),n}var ge=function(e,t){var n={animationend:ye("Animation","AnimationEnd"),transitionend:ye("Transition","TransitionEnd")};return e&&("AnimationEvent"in t||delete n.animationend.animation,"TransitionEvent"in t||delete n.transitionend.transition),n}(he(),"undefined"!==typeof window?window:{}),Ze={};if(he()){var Ee=document.createElement("div");Ze=Ee.style}var ke={};function we(e){if(ke[e])return ke[e];var t=ge[e];if(t)for(var n=Object.keys(t),r=n.length,a=0;a<r;a+=1){var o=n[a];if(Object.prototype.hasOwnProperty.call(t,o)&&o in Ze)return ke[e]=t[o],ke[e]}return""}var Ce=we("animationend"),xe=we("transitionend"),Se=!(!Ce||!xe),Re=Ce||"animationend",Ne=xe||"transitionend";function Pe(e,t){return e?"object"===(0,c.Z)(e)?e[t.replace(/-\w/g,(function(e){return e[1].toUpperCase()}))]:"".concat(e,"-").concat(t):null}var Te=he()?d.useLayoutEffect:d.useEffect,Ae=function(e){return+setTimeout(e,16)},Le=function(e){return clearTimeout(e)};"undefined"!==typeof window&&"requestAnimationFrame"in window&&(Ae=function(e){return window.requestAnimationFrame(e)},Le=function(e){return window.cancelAnimationFrame(e)});var Me=0,Ie=new Map;function Be(e){Ie.delete(e)}function je(e){var t=arguments.length>1&&void 0!==arguments[1]?arguments[1]:1,n=Me+=1;function r(t){if(0===t)Be(n),e();else{var a=Ae((function(){r(t-1)}));Ie.set(n,a)}}return r(t),n}je.cancel=function(e){var t=Ie.get(e);return Be(t),Le(t)};var De=[de,ve,me,pe],ze=[de,be];function Oe(e){return e===me||e===pe}var qe=function(e,t,n){var r=ie(fe),a=(0,i.Z)(r,2),o=a[0],c=a[1],u=function(){var e=d.useRef(null);function t(){je.cancel(e.current)}return d.useEffect((function(){return function(){t()}}),[]),[function n(r){var a=arguments.length>1&&void 0!==arguments[1]?arguments[1]:2;t();var o=je((function(){a<=1?r({isCanceled:function(){return o!==e.current}}):n(r,a-1)}));e.current=o},t]}(),l=(0,i.Z)(u,2),s=l[0],f=l[1];var v=t?ze:De;return Te((function(){if(o!==fe&&o!==pe){var e=v.indexOf(o),t=v[e+1],r=n(o);false===r?c(t,!0):t&&s((function(e){function n(){e.isCanceled()||c(t,!0)}!0===r?n():Promise.resolve(r).then(n)}))}}),[e,o]),d.useEffect((function(){return function(){f()}}),[]),[function(){c(de,!0)},o]};function Ke(e,t,n,r){var c=r.motionEnter,u=void 0===c||c,l=r.motionAppear,s=void 0===l||l,f=r.motionLeave,v=void 0===f||f,m=r.motionDeadline,p=r.motionLeaveImmediately,b=r.onAppearPrepare,h=r.onEnterPrepare,y=r.onLeavePrepare,g=r.onAppearStart,Z=r.onEnterStart,E=r.onLeaveStart,k=r.onAppearActive,w=r.onEnterActive,C=r.onLeaveActive,x=r.onAppearEnd,S=r.onEnterEnd,R=r.onLeaveEnd,N=r.onVisibleChanged,P=ie(),T=(0,i.Z)(P,2),A=T[0],L=T[1],M=ie(ce),I=(0,i.Z)(M,2),B=I[0],j=I[1],D=ie(null),z=(0,i.Z)(D,2),O=z[0],q=z[1],K=(0,d.useRef)(!1),V=(0,d.useRef)(null);function _(){return n()}var W=(0,d.useRef)(!1);function F(){j(ce,!0),q(null,!0)}function X(e){var t=_();if(!e||e.deadline||e.target===t){var n,r=W.current;B===ue&&r?n=null===x||void 0===x?void 0:x(t,e):B===le&&r?n=null===S||void 0===S?void 0:S(t,e):B===se&&r&&(n=null===R||void 0===R?void 0:R(t,e)),B!==ce&&r&&!1!==n&&F()}}var G=function(e){var t=(0,d.useRef)(),n=(0,d.useRef)(e);n.current=e;var r=d.useCallback((function(e){n.current(e)}),[]);function a(e){e&&(e.removeEventListener(Ne,r),e.removeEventListener(Re,r))}return d.useEffect((function(){return function(){a(t.current)}}),[]),[function(e){t.current&&t.current!==e&&a(t.current),e&&e!==t.current&&(e.addEventListener(Ne,r),e.addEventListener(Re,r),t.current=e)},a]}(X),H=(0,i.Z)(G,1)[0],Y=function(e){var t,n,r;switch(e){case ue:return t={},(0,a.Z)(t,de,b),(0,a.Z)(t,ve,g),(0,a.Z)(t,me,k),t;case le:return n={},(0,a.Z)(n,de,h),(0,a.Z)(n,ve,Z),(0,a.Z)(n,me,w),n;case se:return r={},(0,a.Z)(r,de,y),(0,a.Z)(r,ve,E),(0,a.Z)(r,me,C),r;default:return{}}},U=d.useMemo((function(){return Y(B)}),[B]),J=qe(B,!e,(function(e){if(e===de){var t=U.prepare;return!!t&&t(_())}var n;ee in U&&q((null===(n=U[ee])||void 0===n?void 0:n.call(U,_(),null))||null);return ee===me&&(H(_()),m>0&&(clearTimeout(V.current),V.current=setTimeout((function(){X({deadline:!0})}),m))),ee===be&&F(),true})),Q=(0,i.Z)(J,2),$=Q[0],ee=Q[1],te=Oe(ee);W.current=te,Te((function(){L(t);var n,r=K.current;K.current=!0,!r&&t&&s&&(n=ue),r&&t&&u&&(n=le),(r&&!t&&v||!r&&p&&!t&&v)&&(n=se);var a=Y(n);n&&(e||a.prepare)?(j(n),$()):j(ce)}),[t]),(0,d.useEffect)((function(){(B===ue&&!s||B===le&&!u||B===se&&!v)&&j(ce)}),[s,u,v]),(0,d.useEffect)((function(){return function(){K.current=!1,clearTimeout(V.current)}}),[]);var ne=d.useRef(!1);(0,d.useEffect)((function(){A&&(ne.current=!0),void 0!==A&&B===ce&&((ne.current||A)&&(null===N||void 0===N||N(A)),ne.current=!0)}),[A,B]);var re=O;return U.prepare&&ee===ve&&(re=(0,o.Z)({transition:"none"},re)),[B,ee,re,null!==A&&void 0!==A?A:t]}var Ve=function(e){var t=e;"object"===(0,c.Z)(e)&&(t=e.transitionSupport);var n=d.forwardRef((function(e,n){var r=e.visible,c=void 0===r||r,u=e.removeOnLeave,l=void 0===u||u,f=e.forceRender,v=e.children,m=e.motionName,p=e.leavedClassName,b=e.eventProps,h=function(e,n){return!(!e.motionName||!t||!1===n)}(e,d.useContext(J).motion),y=(0,d.useRef)(),g=(0,d.useRef)();var Z=Ke(h,c,(function(){try{return y.current instanceof HTMLElement?y.current:(e=g.current)instanceof HTMLElement?e:G.findDOMNode(e)}catch(t){return null}var e}),e),E=(0,i.Z)(Z,4),k=E[0],w=E[1],C=E[2],x=E[3],S=d.useRef(x);x&&(S.current=!0);var R,N=d.useCallback((function(e){y.current=e,U(n,e)}),[n]),P=(0,o.Z)((0,o.Z)({},b),{},{visible:c});if(v)if(k===ce)R=x?v((0,o.Z)({},P),N):!l&&S.current&&p?v((0,o.Z)((0,o.Z)({},P),{},{className:p}),N):f||!l&&!p?v((0,o.Z)((0,o.Z)({},P),{},{style:{display:"none"}}),N):null;else{var T,A;w===de?A="prepare":Oe(w)?A="active":w===ve&&(A="start");var L=Pe(m,"".concat(k,"-").concat(A));R=v((0,o.Z)((0,o.Z)({},P),{},{className:s()(Pe(m,k),(T={},(0,a.Z)(T,L,L&&A),(0,a.Z)(T,m,"string"===typeof m),T)),style:C}),N)}else R=null;d.isValidElement(R)&&function(e){var t,n,r=(0,Y.isMemo)(e)?e.type.type:e.type;return!("function"===typeof r&&!(null===(t=r.prototype)||void 0===t?void 0:t.render))&&!("function"===typeof e&&!(null===(n=e.prototype)||void 0===n?void 0:n.render))}(R)&&(R.ref||(R=d.cloneElement(R,{ref:N})));return d.createElement(re,{ref:g},R)}));return n.displayName="CSSMotion",n}(Se),_e=n(7326),We="add",Fe="keep",Xe="remove",Ge="removed";function He(e){var t;return t=e&&"object"===(0,c.Z)(e)&&"key"in e?e:{key:e},(0,o.Z)((0,o.Z)({},t),{},{key:String(t.key)})}function Ye(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:[];return e.map(He)}function Ue(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:[],t=arguments.length>1&&void 0!==arguments[1]?arguments[1]:[],n=[],r=0,a=t.length,i=Ye(e),c=Ye(t);i.forEach((function(e){for(var t=!1,i=r;i<a;i+=1){var u=c[i];if(u.key===e.key){r<i&&(n=n.concat(c.slice(r,i).map((function(e){return(0,o.Z)((0,o.Z)({},e),{},{status:We})}))),r=i),n.push((0,o.Z)((0,o.Z)({},u),{},{status:Fe})),r+=1,t=!0;break}}t||n.push((0,o.Z)((0,o.Z)({},e),{},{status:Xe}))})),r<a&&(n=n.concat(c.slice(r).map((function(e){return(0,o.Z)((0,o.Z)({},e),{},{status:We})}))));var u={};n.forEach((function(e){var t=e.key;u[t]=(u[t]||0)+1}));var l=Object.keys(u).filter((function(e){return u[e]>1}));return l.forEach((function(e){(n=n.filter((function(t){var n=t.key,r=t.status;return n!==e||r!==Xe}))).forEach((function(t){t.key===e&&(t.status=Fe)}))})),n}var Je=["component","children","onVisibleChanged","onAllRemoved"],Qe=["status"],$e=["eventProps","visible","children","motionName","motionAppear","motionEnter","motionLeave","motionLeaveImmediately","motionDeadline","removeOnLeave","leavedClassName","onAppearStart","onAppearActive","onAppearEnd","onEnterStart","onEnterActive","onEnterEnd","onLeaveStart","onLeaveActive","onLeaveEnd"];!function(e){var t=arguments.length>1&&void 0!==arguments[1]?arguments[1]:Ve,n=function(e){(0,ee.Z)(i,e);var n=(0,te.Z)(i);function i(){var e;(0,Q.Z)(this,i);for(var t=arguments.length,r=new Array(t),c=0;c<t;c++)r[c]=arguments[c];return e=n.call.apply(n,[this].concat(r)),(0,a.Z)((0,_e.Z)(e),"state",{keyEntities:[]}),(0,a.Z)((0,_e.Z)(e),"removeKey",(function(t){var n=e.state.keyEntities.map((function(e){return e.key!==t?e:(0,o.Z)((0,o.Z)({},e),{},{status:Ge})}));return e.setState({keyEntities:n}),n.filter((function(e){return e.status!==Ge})).length})),e}return(0,$.Z)(i,[{key:"render",value:function(){var e=this,n=this.state.keyEntities,a=this.props,o=a.component,i=a.children,c=a.onVisibleChanged,l=a.onAllRemoved,s=(0,u.Z)(a,Je),f=o||d.Fragment,v={};return $e.forEach((function(e){v[e]=s[e],delete s[e]})),delete s.keys,d.createElement(f,s,n.map((function(n){var a=n.status,o=(0,u.Z)(n,Qe),s=a===We||a===Fe;return d.createElement(t,(0,r.Z)({},v,{key:o.key,visible:s,eventProps:o,onVisibleChanged:function(t){(null===c||void 0===c||c(t,{key:o.key}),t)||0===e.removeKey(o.key)&&l&&l()}}),i)})))}}],[{key:"getDerivedStateFromProps",value:function(e,t){var n=e.keys,r=t.keyEntities,a=Ye(n);return{keyEntities:Ue(r,a).filter((function(e){var t=r.find((function(t){var n=t.key;return e.key===n}));return!t||t.status!==Ge||e.status!==Xe}))}}}]),i}(d.Component);(0,a.Z)(n,"defaultProps",{component:"div"})}(Se);var et=Ve,tt=["key","forceRender","style","className","destroyInactiveTabPane"],nt=function(e){var t=e.id,n=e.activeKey,i=e.animated,c=e.tabPosition,l=e.destroyInactiveTabPane,f=d.useContext(v),m=f.prefixCls,p=f.tabs,b=i.tabPane,h="".concat(m,"-tabpane");return d.createElement("div",{className:s()("".concat(m,"-content-holder"))},d.createElement("div",{className:s()("".concat(m,"-content"),"".concat(m,"-content-").concat(c),(0,a.Z)({},"".concat(m,"-content-animated"),b))},p.map((function(e){var a=e.key,c=e.forceRender,f=e.style,v=e.className,m=e.destroyInactiveTabPane,p=(0,u.Z)(e,tt),y=a===n;return d.createElement(et,(0,r.Z)({key:a,visible:y,forceRender:c,removeOnLeave:!(!l&&!m),leavedClassName:"".concat(h,"-hidden")},i.tabPaneMotion),(function(e,n){var i=e.style,c=e.className;return d.createElement(_,(0,r.Z)({},p,{prefixCls:h,id:t,tabKey:a,animated:b,active:y,style:(0,o.Z)((0,o.Z)({},f),i),className:s()(v,c),ref:n}))}))}))))};n(3932);var rt=["id","prefixCls","className","items","direction","activeKey","defaultActiveKey","editable","animated","tabPosition","tabBarGutter","tabBarStyle","tabBarExtraContent","locale","more","destroyInactiveTabPane","renderTabBar","onChange","onTabClick","onTabScroll","getPopupContainer","popupClassName","indicator"],at=0,ot=d.forwardRef((function(e,t){var n=e.id,l=e.prefixCls,m=void 0===l?"rc-tabs":l,p=e.className,b=e.items,h=e.direction,y=e.activeKey,g=e.defaultActiveKey,Z=e.editable,E=e.animated,k=e.tabPosition,w=void 0===k?"top":k,C=e.tabBarGutter,x=e.tabBarStyle,S=e.tabBarExtraContent,R=e.locale,N=e.more,P=e.destroyInactiveTabPane,T=e.renderTabBar,A=e.onChange,L=e.onTabClick,M=e.onTabScroll,I=e.getPopupContainer,B=e.popupClassName,j=e.indicator,D=(0,u.Z)(e,rt),z=d.useMemo((function(){return(b||[]).filter((function(e){return e&&"object"===(0,c.Z)(e)&&"key"in e}))}),[b]),O="rtl"===h,q=function(){var e,t=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{inkBar:!0,tabPane:!1};return(e=!1===t?{inkBar:!1,tabPane:!1}:!0===t?{inkBar:!0,tabPane:!1}:(0,o.Z)({inkBar:!0},"object"===(0,c.Z)(t)?t:{})).tabPaneMotion&&void 0===e.tabPane&&(e.tabPane=!0),!e.tabPaneMotion&&e.tabPane&&(e.tabPane=!1),e}(E),K=(0,d.useState)(!1),V=(0,i.Z)(K,2),_=V[0],W=V[1];(0,d.useEffect)((function(){W(function(){if("undefined"===typeof navigator||"undefined"===typeof window)return!1;var e=navigator.userAgent||navigator.vendor||window.opera;return/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino|android|ipad|playbook|silk/i.test(e)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw-(n|u)|c55\/|capi|ccwa|cdm-|cell|chtm|cldc|cmd-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc-s|devi|dica|dmob|do(c|p)o|ds(12|-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(-|_)|g1 u|g560|gene|gf-5|g-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd-(m|p|t)|hei-|hi(pt|ta)|hp( i|ip)|hs-c|ht(c(-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i-(20|go|ma)|i230|iac( |-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|-[a-w])|libw|lynx|m1-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|-([1-8]|c))|phil|pire|pl(ay|uc)|pn-2|po(ck|rt|se)|prox|psio|pt-g|qa-a|qc(07|12|21|32|60|-[2-7]|i-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h-|oo|p-)|sdk\/|se(c(-|0|1)|47|mc|nd|ri)|sgh-|shar|sie(-|m)|sk-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h-|v-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl-|tdg-|tel(i|m)|tim-|t-mo|to(pl|sh)|ts(70|m-|m3|m5)|tx-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas-|your|zeto|zte-/i.test(null===e||void 0===e?void 0:e.substr(0,4))}())}),[]);var F=(0,f.Z)((function(){var e;return null===(e=z[0])||void 0===e?void 0:e.key}),{value:y,defaultValue:g}),G=(0,i.Z)(F,2),H=G[0],Y=G[1],U=(0,d.useState)((function(){return z.findIndex((function(e){return e.key===H}))})),J=(0,i.Z)(U,2),Q=J[0],$=J[1];(0,d.useEffect)((function(){var e,t=z.findIndex((function(e){return e.key===H}));-1===t&&(t=Math.max(0,Math.min(Q,z.length-1)),Y(null===(e=z[t])||void 0===e?void 0:e.key));$(t)}),[z.map((function(e){return e.key})).join("_"),H,Q]);var ee=(0,f.Z)(null,{value:n}),te=(0,i.Z)(ee,2),ne=te[0],re=te[1];(0,d.useEffect)((function(){n||(re("rc-tabs-".concat(at)),at+=1)}),[]);var ae={id:ne,activeKey:H,animated:q,tabPosition:w,rtl:O,mobile:_},oe=(0,o.Z)((0,o.Z)({},ae),{},{editable:Z,locale:R,more:N,tabBarGutter:C,onTabClick:function(e,t){null===L||void 0===L||L(e,t);var n=e!==H;Y(e),n&&(null===A||void 0===A||A(e))},onTabScroll:M,extra:S,style:x,panes:null,getPopupContainer:I,popupClassName:B,indicator:j});return d.createElement(v.Provider,{value:{tabs:z,prefixCls:m}},d.createElement("div",(0,r.Z)({ref:t,id:n,className:s()(m,"".concat(m,"-").concat(w),(0,a.Z)((0,a.Z)((0,a.Z)({},"".concat(m,"-mobile"),_),"".concat(m,"-editable"),Z),"".concat(m,"-rtl"),O),p)},D),d.createElement(X,(0,r.Z)({},oe,{renderTabBar:T})),d.createElement(nt,(0,r.Z)({destroyInactiveTabPane:P},ae,{animated:q}))))}));var it=ot}}]);