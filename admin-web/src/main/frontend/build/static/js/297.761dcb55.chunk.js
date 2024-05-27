"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[297],{1890:function(e,a,t){t.r(a);var n=t(1413),s=t(9439),r=t(8391),E=t(9012),c=t(7372),i=t(2805),N=t(535),l=t(8881),d=t(1474),_=t(7849),M=t(7741),o=t(6479),u=t(2503),S=t(5068),T=t(6146),U=t(3561),A=t(3712),O={labelCol:{span:8},wrapperCol:{span:16}};a.default=function(e){for(var a=e.data,t={},f=0,I=Object.entries(a.config);f<I.length;f++){var p=(0,s.Z)(I[f],2),C=p[0],R=p[1];t[C]=R.value}var h=(0,r.useState)({config:a.config,dataMap:t}),Z=(0,s.Z)(h,2),P=Z[0],F=Z[1],L=E.Z.useApp().message,m=function(e,a){return"file"===a.type?(0,A.jsx)(A.Fragment,{children:(0,A.jsx)(o.Z,{style:{width:"128px"},multiple:!1,onChange:function(a){return function(e,a){var t=e.file.status;"done"===t?(P.dataMap[a]=e.file.response.data.url,F((0,n.Z)((0,n.Z)({},P),{},{dataMap:P.dataMap}))):"error"===t&&L.error("".concat(e.file.name," file upload failed."))}(a,e)},name:"imgFile",action:"/api/admin/upload?dir=image",children:(0,A.jsx)(M.Z,{style:{borderRadius:8},preview:!1,width:128,src:P.dataMap[e]})})}):"textarea"===a.htmlElementType?(0,A.jsx)(u.Z,{rows:5,placeholder:a.placeholder}):"hidden"===a.type?(0,A.jsx)(c.Z,{hidden:!0}):(0,A.jsx)(c.Z,{type:a.type,placeholder:a.placeholder})};return(0,A.jsxs)(A.Fragment,{children:[(0,A.jsx)(l.Z,{className:"page-header",level:3,children:(0,U.sR)().templateConfig}),(0,A.jsx)(d.Z,{}),(0,A.jsx)(N.Z,{children:(0,A.jsx)(S.Z,{md:12,xs:24,children:(0,A.jsxs)(i.Z,(0,n.Z)((0,n.Z)({onFinish:function(){T.Z.post("/api/admin/template/config",P.dataMap).then((function(e){var a=e.data;a.error?L.error(a.message):L.success(a.message)}))},initialValues:P.dataMap,onValuesChange:function(e,a){return t=a,void F((0,n.Z)((0,n.Z)({},P),{},{dataMap:t}));var t}},O),{},{children:[function(){for(var e=[],a=0,t=Object.entries(P.config);a<t.length;a++){var n=(0,s.Z)(t[a],2),r=n[0],E=n[1],c=(0,A.jsx)(i.Z.Item,{label:E.label,name:r,style:{display:"hidden"===E.type?"none":""},children:m(r,E)},r);e.push(c)}return e}(),(0,A.jsx)(d.Z,{}),(0,A.jsx)(_.ZP,{type:"primary",htmlType:"submit",children:(0,U.sR)().submit})]}))})})]})}},4142:function(e,a,t){t.d(a,{Z:function(){return i}});var n=t(7462),s=t(8391),r={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M765.7 486.8L314.9 134.7A7.97 7.97 0 00302 141v77.3c0 4.9 2.3 9.6 6.1 12.6l360 281.1-360 281.1c-3.9 3-6.1 7.7-6.1 12.6V883c0 6.7 7.7 10.4 12.9 6.3l450.8-352.1a31.96 31.96 0 000-50.4z"}}]},name:"right",theme:"outlined"},E=t(7839),c=function(e,a){return s.createElement(E.Z,(0,n.Z)({},e,{ref:a,icon:r}))};var i=s.forwardRef(c)},4536:function(e,a){var t={MAC_ENTER:3,BACKSPACE:8,TAB:9,NUM_CENTER:12,ENTER:13,SHIFT:16,CTRL:17,ALT:18,PAUSE:19,CAPS_LOCK:20,ESC:27,SPACE:32,PAGE_UP:33,PAGE_DOWN:34,END:35,HOME:36,LEFT:37,UP:38,RIGHT:39,DOWN:40,PRINT_SCREEN:44,INSERT:45,DELETE:46,ZERO:48,ONE:49,TWO:50,THREE:51,FOUR:52,FIVE:53,SIX:54,SEVEN:55,EIGHT:56,NINE:57,QUESTION_MARK:63,A:65,B:66,C:67,D:68,E:69,F:70,G:71,H:72,I:73,J:74,K:75,L:76,M:77,N:78,O:79,P:80,Q:81,R:82,S:83,T:84,U:85,V:86,W:87,X:88,Y:89,Z:90,META:91,WIN_KEY_RIGHT:92,CONTEXT_MENU:93,NUM_ZERO:96,NUM_ONE:97,NUM_TWO:98,NUM_THREE:99,NUM_FOUR:100,NUM_FIVE:101,NUM_SIX:102,NUM_SEVEN:103,NUM_EIGHT:104,NUM_NINE:105,NUM_MULTIPLY:106,NUM_PLUS:107,NUM_MINUS:109,NUM_PERIOD:110,NUM_DIVISION:111,F1:112,F2:113,F3:114,F4:115,F5:116,F6:117,F7:118,F8:119,F9:120,F10:121,F11:122,F12:123,NUMLOCK:144,SEMICOLON:186,DASH:189,EQUALS:187,COMMA:188,PERIOD:190,SLASH:191,APOSTROPHE:192,SINGLE_QUOTE:222,OPEN_SQUARE_BRACKET:219,BACKSLASH:220,CLOSE_SQUARE_BRACKET:221,WIN_KEY:224,MAC_FF_META:224,WIN_IME:229,isTextModifyingKeyEvent:function(e){var a=e.keyCode;if(e.altKey&&!e.ctrlKey||e.metaKey||a>=t.F1&&a<=t.F12)return!1;switch(a){case t.ALT:case t.CAPS_LOCK:case t.CONTEXT_MENU:case t.CTRL:case t.DOWN:case t.END:case t.ESC:case t.HOME:case t.INSERT:case t.LEFT:case t.MAC_FF_META:case t.META:case t.NUMLOCK:case t.NUM_CENTER:case t.PAGE_DOWN:case t.PAGE_UP:case t.PAUSE:case t.PRINT_SCREEN:case t.RIGHT:case t.SHIFT:case t.UP:case t.WIN_KEY:case t.WIN_KEY_RIGHT:return!1;default:return!0}},isCharacterKey:function(e){if(e>=t.ZERO&&e<=t.NINE)return!0;if(e>=t.NUM_ZERO&&e<=t.NUM_MULTIPLY)return!0;if(e>=t.A&&e<=t.Z)return!0;if(-1!==window.navigator.userAgent.indexOf("WebKit")&&0===e)return!0;switch(e){case t.SPACE:case t.QUESTION_MARK:case t.NUM_PLUS:case t.NUM_MINUS:case t.NUM_PERIOD:case t.NUM_DIVISION:case t.SEMICOLON:case t.DASH:case t.EQUALS:case t.COMMA:case t.PERIOD:case t.SLASH:case t.APOSTROPHE:case t.SINGLE_QUOTE:case t.OPEN_SQUARE_BRACKET:case t.BACKSLASH:case t.CLOSE_SQUARE_BRACKET:return!0;default:return!1}}};a.Z=t}}]);