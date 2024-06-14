"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[928],{1890:function(e,a,s){s.r(a);var n=s(1413),t=s(9439),E=s(8391),r=s(9012),c=s(7372),i=s(3615),N=s(535),l=s(8881),_=s(1474),M=s(7849),d=s(4581),o=s(5897),u=s(517),S=s(5068),T=s(6146),U=s(3561),A=s(3712),O={labelCol:{span:8},wrapperCol:{span:16}},I=function(e){for(var a={},s=0,n=Object.entries(e.config);s<n.length;s++){var E=(0,t.Z)(n[s],2),r=E[0],c=E[1];a[r]=c.value}return a};a.default=function(e){var a=e.data,s=I(a),f=(0,E.useState)({config:a.config,dataMap:s}),p=(0,t.Z)(f,2),C=p[0],R=p[1],h=r.Z.useApp().message,P=function(e,a){return"file"===a.type?(0,A.jsx)(A.Fragment,{children:(0,A.jsx)(o.Z,{style:{width:"128px",height:"128px"},multiple:!1,onChange:function(a){return function(e,a){var s=e.file.status;"done"===s?(C.dataMap[a]=e.file.response.data.url,R((0,n.Z)((0,n.Z)({},C),{},{dataMap:C.dataMap}))):"error"===s&&h.error("".concat(e.file.name," file upload failed."))}(a,e)},name:"imgFile",action:"/api/admin/upload?dir=image",children:(0,A.jsx)(d.Z,{style:{borderRadius:8},preview:!1,height:128,width:128,src:C.dataMap[e]})})}):"textarea"===a.htmlElementType?(0,A.jsx)(u.Z,{rows:5,placeholder:a.placeholder}):"hidden"===a.type?(0,A.jsx)(c.Z,{hidden:!0}):(0,A.jsx)(c.Z,{type:a.type,placeholder:a.placeholder})};return(0,E.useEffect)((function(){R({config:a.config,dataMap:I(a)})}),[a]),(0,A.jsxs)(A.Fragment,{children:[(0,A.jsx)(l.Z,{className:"page-header",level:3,children:(0,U.sR)().templateConfig}),(0,A.jsx)(_.Z,{}),(0,A.jsx)(N.Z,{children:(0,A.jsx)(S.Z,{xs:24,style:{maxWidth:600},children:(0,A.jsxs)(i.Z,(0,n.Z)((0,n.Z)({onFinish:function(){T.Z.post("/api/admin/template/config",C.dataMap).then((function(e){var a=e.data;a.error?h.error(a.message):h.success(a.message)}))},initialValues:C.dataMap,onValuesChange:function(e,a){return s=a,void R((0,n.Z)((0,n.Z)({},C),{},{dataMap:s}));var s}},O),{},{children:[function(){for(var e=[],a=0,s=Object.entries(C.config);a<s.length;a++){var n=(0,t.Z)(s[a],2),E=n[0],r=n[1],c=(0,A.jsx)(i.Z.Item,{label:r.label,name:E,style:{display:"hidden"===r.type?"none":""},children:P(E,r)},E);e.push(c)}return e}(),(0,A.jsx)(_.Z,{}),(0,A.jsx)(M.ZP,{type:"primary",htmlType:"submit",children:(0,U.sR)().submit})]}))})})]})}},4536:function(e,a){var s={MAC_ENTER:3,BACKSPACE:8,TAB:9,NUM_CENTER:12,ENTER:13,SHIFT:16,CTRL:17,ALT:18,PAUSE:19,CAPS_LOCK:20,ESC:27,SPACE:32,PAGE_UP:33,PAGE_DOWN:34,END:35,HOME:36,LEFT:37,UP:38,RIGHT:39,DOWN:40,PRINT_SCREEN:44,INSERT:45,DELETE:46,ZERO:48,ONE:49,TWO:50,THREE:51,FOUR:52,FIVE:53,SIX:54,SEVEN:55,EIGHT:56,NINE:57,QUESTION_MARK:63,A:65,B:66,C:67,D:68,E:69,F:70,G:71,H:72,I:73,J:74,K:75,L:76,M:77,N:78,O:79,P:80,Q:81,R:82,S:83,T:84,U:85,V:86,W:87,X:88,Y:89,Z:90,META:91,WIN_KEY_RIGHT:92,CONTEXT_MENU:93,NUM_ZERO:96,NUM_ONE:97,NUM_TWO:98,NUM_THREE:99,NUM_FOUR:100,NUM_FIVE:101,NUM_SIX:102,NUM_SEVEN:103,NUM_EIGHT:104,NUM_NINE:105,NUM_MULTIPLY:106,NUM_PLUS:107,NUM_MINUS:109,NUM_PERIOD:110,NUM_DIVISION:111,F1:112,F2:113,F3:114,F4:115,F5:116,F6:117,F7:118,F8:119,F9:120,F10:121,F11:122,F12:123,NUMLOCK:144,SEMICOLON:186,DASH:189,EQUALS:187,COMMA:188,PERIOD:190,SLASH:191,APOSTROPHE:192,SINGLE_QUOTE:222,OPEN_SQUARE_BRACKET:219,BACKSLASH:220,CLOSE_SQUARE_BRACKET:221,WIN_KEY:224,MAC_FF_META:224,WIN_IME:229,isTextModifyingKeyEvent:function(e){var a=e.keyCode;if(e.altKey&&!e.ctrlKey||e.metaKey||a>=s.F1&&a<=s.F12)return!1;switch(a){case s.ALT:case s.CAPS_LOCK:case s.CONTEXT_MENU:case s.CTRL:case s.DOWN:case s.END:case s.ESC:case s.HOME:case s.INSERT:case s.LEFT:case s.MAC_FF_META:case s.META:case s.NUMLOCK:case s.NUM_CENTER:case s.PAGE_DOWN:case s.PAGE_UP:case s.PAUSE:case s.PRINT_SCREEN:case s.RIGHT:case s.SHIFT:case s.UP:case s.WIN_KEY:case s.WIN_KEY_RIGHT:return!1;default:return!0}},isCharacterKey:function(e){if(e>=s.ZERO&&e<=s.NINE)return!0;if(e>=s.NUM_ZERO&&e<=s.NUM_MULTIPLY)return!0;if(e>=s.A&&e<=s.Z)return!0;if(-1!==window.navigator.userAgent.indexOf("WebKit")&&0===e)return!0;switch(e){case s.SPACE:case s.QUESTION_MARK:case s.NUM_PLUS:case s.NUM_MINUS:case s.NUM_PERIOD:case s.NUM_DIVISION:case s.SEMICOLON:case s.DASH:case s.EQUALS:case s.COMMA:case s.PERIOD:case s.SLASH:case s.APOSTROPHE:case s.SINGLE_QUOTE:case s.OPEN_SQUARE_BRACKET:case s.BACKSLASH:case s.CLOSE_SQUARE_BRACKET:return!0;default:return!1}}};a.Z=s}}]);