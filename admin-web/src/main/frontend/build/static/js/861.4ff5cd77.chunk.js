"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[861],{77861:function(e,a,t){t.r(a);var n=t(15861),r=t(1413),i=t(29439),s=t(87757),l=t.n(s),d=t(47313),o=t(18374),p=t(51409),c=t(66050),u=t(91618),f=t(68197),h=t(43781),m=t(87785),x=t(22834),Z=t(21794),g=t(3733),y=t(33348),j=t(26297),b=t(38475),v=t(88783),M=t(60396),w=t(82294),C=t(46417),k={labelCol:{span:8},wrapperCol:{span:16}},E=function(e){for(var a={},t=0,n=Object.entries(e.config);t<n.length;t++){var r=(0,i.Z)(n[t],2),s=r[0],l=r[1];a[s]=l.value}return a};a.default=function(e){var a=e.data,t=e.offline,s=E(a),T=(0,d.useState)({config:a.config,dataMap:s}),F=(0,i.Z)(T,2),_=F[0],O=F[1],P=o.ZP.useMessage({maxCount:3}),R=(0,i.Z)(P,2),I=R[0],S=R[1],V=function(e,a){return"file"===a.type?(0,C.jsx)(C.Fragment,{children:(0,C.jsx)(g.Z,{style:{width:"128px",height:"128px"},multiple:!1,onChange:function(a){return function(e,a){var t=e.file.status;"done"===t?(_.dataMap[a]=e.file.response.data.url,O((0,r.Z)((0,r.Z)({},_),{},{dataMap:_.dataMap}))):"error"===t&&I.error("".concat(e.file.name," file upload failed."))}(a,e)},name:"imgFile",action:"/api/admin/upload?dir=image",children:(0,C.jsx)(Z.Z,{style:{borderRadius:8},preview:!1,height:128,width:128,src:_.dataMap[e]})})}):"switch"===a.htmlElementType?(0,C.jsx)(M.Z,{size:"small"}):"textarea"===a.htmlElementType||"large-textarea"===a.htmlElementType?(0,C.jsx)(y.Z,{rows:"large-textarea"===a.htmlElementType?20:5,placeholder:a.placeholder}):"hidden"===a.type?(0,C.jsx)(p.Z,{hidden:!0}):"colorPicker"===a.htmlElementType?(0,C.jsxs)("div",{style:{display:"flex",justifyContent:"flex-start",alignItems:"center"},children:[(0,C.jsx)(c.Z,{value:_.dataMap[e],onChange:function(a){_.dataMap[e]=a.toHexString(),O((0,r.Z)((0,r.Z)({},_),{},{dataMap:_.dataMap}))},presets:[{defaultOpen:!0,label:"\u9884\u8bbe",colors:w.bE}]}),(0,C.jsx)("span",{style:{paddingLeft:8},children:_.dataMap[e]})]}):(0,C.jsx)(p.Z,{type:a.type,placeholder:a.placeholder})};return(0,d.useEffect)((function(){O({config:a.config,dataMap:E(a)})}),[a]),(0,C.jsxs)(C.Fragment,{children:[S,(0,C.jsx)(h.Z,{className:"page-header",level:3,children:(0,v.sR)().templateConfig}),(0,C.jsx)(m.Z,{}),(0,C.jsx)(f.Z,{children:(0,C.jsx)(j.Z,{xs:24,style:{maxWidth:600},children:(0,C.jsxs)(u.Z,(0,r.Z)((0,r.Z)({onFinish:function(){b.Z.post("/api/admin/template/config",_.dataMap).then(function(){var e=(0,n.Z)(l().mark((function e(a){var t;return l().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(t=a.data).error){e.next=6;break}return e.next=4,I.error(t.message);case 4:e.next=9;break;case 6:if(0!==t.error){e.next=9;break}return e.next=9,I.success(t.message);case 9:case"end":return e.stop()}}),e)})));return function(a){return e.apply(this,arguments)}}())},initialValues:_.dataMap,onValuesChange:function(e,a){return t=a,void O((0,r.Z)((0,r.Z)({},_),{},{dataMap:t}));var t}},k),{},{children:[function(){for(var e=[],a=0,t=Object.entries(_.config);a<t.length;a++){var n=(0,i.Z)(t[a],2),r=n[0],s=n[1],l=(0,C.jsx)(u.Z.Item,{label:s.label,name:r,style:{display:"hidden"===s.type?"none":""},children:V(r,s)},r);e.push(l)}return e}(),(0,C.jsx)(m.Z,{}),(0,C.jsx)(x.ZP,{disabled:t,type:"primary",htmlType:"submit",children:(0,v.sR)().submit})]}))})})]})}}}]);