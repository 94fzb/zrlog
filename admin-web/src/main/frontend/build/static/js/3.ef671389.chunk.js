"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[3],{9003:function(e,n,a){a.r(n),a.d(n,{default:function(){return ce}});var t=a(1),r=a(3781),s=a(4268),i=a(6297),l=a(7785),c=a(9439),o=a(5413),d=a(48),u=a(1741),h=a(9893),m=a(7460),p=a(7313),x={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M912 190h-69.9c-9.8 0-19.1 4.5-25.1 12.2L404.7 724.5 207 474a32 32 0 00-25.1-12.2H112c-6.7 0-10.4 7.7-6.3 12.9l273.9 347c12.8 16.2 37.4 16.2 50.3 0l488.4-618.9c4.1-5.1.4-12.8-6.3-12.8z"}}]},name:"check",theme:"outlined"},f=a(9126),Z=function(e,n){return p.createElement(f.Z,(0,m.Z)({},e,{ref:n,icon:x}))};var v=p.forwardRef(Z),g=a(7515),j={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M624 706.3h-74.1V464c0-4.4-3.6-8-8-8h-60c-4.4 0-8 3.6-8 8v242.3H400c-6.7 0-10.4 7.7-6.3 12.9l112 141.7a8 8 0 0012.6 0l112-141.7c4.1-5.2.4-12.9-6.3-12.9z"}},{tag:"path",attrs:{d:"M811.4 366.7C765.6 245.9 648.9 160 512.2 160S258.8 245.8 213 366.6C127.3 389.1 64 467.2 64 560c0 110.5 89.5 200 199.9 200H304c4.4 0 8-3.6 8-8v-60c0-4.4-3.6-8-8-8h-40.1c-33.7 0-65.4-13.4-89-37.7-23.5-24.2-36-56.8-34.9-90.6.9-26.4 9.9-51.2 26.2-72.1 16.7-21.3 40.1-36.8 66.1-43.7l37.9-9.9 13.9-36.6c8.6-22.8 20.6-44.1 35.7-63.4a245.6 245.6 0 0152.4-49.9c41.1-28.9 89.5-44.2 140-44.2s98.9 15.3 140 44.2c19.9 14 37.5 30.8 52.4 49.9 15.1 19.3 27.1 40.7 35.7 63.4l13.8 36.5 37.8 10C846.1 454.5 884 503.8 884 560c0 33.1-12.9 64.3-36.3 87.7a123.07 123.07 0 01-87.6 36.3H720c-4.4 0-8 3.6-8 8v60c0 4.4 3.6 8 8 8h40.1C870.5 760 960 670.5 960 560c0-92.7-63.1-170.7-148.6-193.3z"}}]},name:"cloud-download",theme:"outlined"},b=function(e,n){return p.createElement(f.Z,(0,m.Z)({},e,{ref:n,icon:j}))};var w=p.forwardRef(b),y=a(6909),_=a(8939),C=a(8783),R=a(8475),k=a(2135),z=a(3983),I=a(6417),P=function(e){var n=e.data,a=(0,p.useState)(n),t=(0,c.Z)(a,2),m=t[0],x=t[1],f=function(){R.Z.get("/api/admin/template").then((function(e){var n=e.data;x(n.data)}))};(0,p.useEffect)((function(){x(n)}),[n]);var Z=function(e){var n=[];return n.push((0,I.jsx)("div",{onClick:function(){return n=e.shortTemplate,void R.Z.post("/api/admin/template/preview?shortTemplate="+n).then((function(){window.open(document.baseURI,"_blank"),f()}));var n},children:(0,I.jsx)(u.Z,{},"preview")}),(0,I.jsx)(k.rU,{to:"/template-config?shortTemplate="+e.shortTemplate,children:(0,I.jsx)(h.Z,{},"setting")}),(0,I.jsx)("div",{onClick:function(){return n=e.shortTemplate,void R.Z.post("/api/admin/template/apply?shortTemplate="+n).then((function(){f()}));var n},children:(0,I.jsx)(v,{})})),e.deleteAble&&n.push((0,I.jsx)(z.Z,{title:(0,C.sR)().deleteTips,onConfirm:function(){var n;n=e.shortTemplate,R.Z.post("/api/admin/template/delete?shortTemplate="+n).then((function(){f()}))},children:(0,I.jsx)(g.Z,{},"delete")})),n};return(0,I.jsxs)(I.Fragment,{children:[(0,I.jsx)(r.Z,{level:4,children:(0,C.sR)()["admin.template.manage"]}),(0,I.jsx)(l.Z,{}),(0,I.jsx)(s.Z,{gutter:[16,16],children:m.map((function(e){return(0,I.jsx)(i.Z,{md:6,xxl:4,xs:24,children:(0,I.jsx)(o.Z.Ribbon,{text:e.use?(0,C.sR)()["admin.theme.inUse"]:e.preview?(0,C.sR)()["admin.theme.inPreview"]:"",style:{fontSize:16,display:e.use||e.preview?"":"none"},children:(0,I.jsx)(d.Z,{cover:(0,I.jsx)("img",{style:{width:"100%",minHeight:250},alt:e.name,title:e.name,src:e.adminPreviewImage}),actions:Z(e),children:(0,I.jsx)(y.Z,{title:e.name,description:e.digest})})})})}))}),(0,I.jsx)(l.Z,{}),(0,I.jsx)(k.rU,{to:"/template-center?host=".concat(window.location.host),children:(0,I.jsx)(_.ZP,{icon:(0,I.jsx)(w,{}),type:"primary",children:(0,C.sR)()["admin.theme.download"]})})]})},H=a(5861),M=a(1413),U=a(7757),T=a.n(U),V=a(9418),E=a(2736),S=a(396),W=a(8374),F={labelCol:{span:8},wrapperCol:{span:16}},L=function(e){var n=e.data,a=e.offline,t=(0,p.useState)(n),s=(0,c.Z)(t,2),i=s[0],o=s[1],d=W.ZP.useMessage({maxCount:3}),u=(0,c.Z)(d,2),h=u[0],m=u[1];return(0,p.useEffect)((function(){o(n)}),[n]),(0,I.jsxs)(I.Fragment,{children:[m,(0,I.jsxs)(V.Z,(0,M.Z)((0,M.Z)({},F),{},{initialValues:i,onValuesChange:function(e,n){return o((0,M.Z)((0,M.Z)({},i),n))},onFinish:function(e){return n=e,void R.Z.post("/api/admin/website/blog",(0,M.Z)((0,M.Z)({},i),n)).then(function(){var e=(0,H.Z)(T().mark((function e(n){var a;return T().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(a=n.data).error){e.next=5;break}return e.next=4,h.error(a.message);case 4:return e.abrupt("return");case 5:if(0!==a.error){e.next=10;break}return e.next=8,h.success(a.message);case 8:(0,C.YB)(),window.location.reload();case 10:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}());var n},children:[(0,I.jsx)(r.Z,{level:4,children:"\u535a\u5ba2\u8bbe\u7f6e"}),(0,I.jsx)(l.Z,{}),(0,I.jsx)(V.Z.Item,{name:"host",label:"\u535a\u5ba2\u57df\u540d\uff08Host\uff09",children:(0,I.jsx)(E.Z,{style:{maxWidth:"300px"},placeholder:"\u7559\u7a7a\uff0c\u7a0b\u5e8f\u5c06\u8bfb\u53d6\u63a5\u6536\u5230\u7684 Host \u5b57\u6bb5"})}),(0,I.jsx)(V.Z.Item,{valuePropName:"checked",name:"generator_html_status",label:"\u9759\u6001\u5316\u6587\u7ae0\u9875",children:(0,I.jsx)(S.Z,{size:"small"})}),(0,I.jsx)(V.Z.Item,{valuePropName:"checked",name:"disable_comment_status",label:"\u5173\u95ed\u8bc4\u8bba",children:(0,I.jsx)(S.Z,{size:"small"})}),(0,I.jsx)(V.Z.Item,{valuePropName:"checked",name:"article_thumbnail_status",label:"\u6587\u7ae0\u5c01\u9762",children:(0,I.jsx)(S.Z,{size:"small"})}),(0,I.jsx)(l.Z,{}),(0,I.jsx)(_.ZP,{type:"primary",disabled:a,htmlType:"submit",children:(0,C.sR)().submit})]}))]})},B=a(3348),N=a(392),A=a(6446),O=a(9e3),Y=a(9634),D=function(e){var n=e.onChange,a=e.url,t=function(){var e=(0,H.Z)(T().mark((function e(a){return T().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.abrupt("return",new Promise((function(e,t){var r=new FileReader;r.readAsDataURL(a),r.onload=function(){var e=r.result;n&&n(e),t()}})));case 1:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}();return(0,I.jsxs)(Y.Z,{accept:"image/*",height:64,beforeUpload:function(e){return t(e)},style:{overflow:"hidden",height:64,width:64},children:[(void 0===a||null===a||""===a)&&(0,I.jsx)("p",{className:"ant-upload-drag-icon",style:{padding:"18px 0",margin:0,display:"flex",justifyContent:"center",alignItems:"center"},children:(0,I.jsx)(N.Z,{style:{fontSize:"28px"}})}),null!=a&&""!==a&&(0,I.jsxs)("div",{style:{position:"relative"},children:[(0,I.jsx)(O.Z,{style:{borderRadius:8,position:"relative"},preview:!1,src:a,wrapperStyle:{position:"relative"}}),(0,I.jsx)("div",{style:{position:"absolute",right:0,top:0,borderRadius:"0 8px",padding:4,background:(0,C.RQ)()+"5e",color:"white",fontSize:16},onClick:function(e){n&&n(null),e.stopPropagation()},children:(0,I.jsx)(A.Z,{})})]})]})},q={labelCol:{span:8},wrapperCol:{span:16}},Q=function(e){var n=e.data,a=e.offline,t=(0,p.useState)(n),s=(0,c.Z)(t,2),i=s[0],o=s[1],d=W.ZP.useMessage({maxCount:3}),u=(0,c.Z)(d,2),h=u[0],m=u[1];return(0,p.useEffect)((function(){o(n)}),[n]),(0,I.jsxs)(I.Fragment,{children:[m,(0,I.jsx)(r.Z,{level:4,children:"\u8ba4\u771f\u8f93\u5165\uff0c\u6709\u52a9\u4e8e\u7f51\u7ad9\u88ab\u6536\u5f55"}),(0,I.jsx)(l.Z,{}),(0,I.jsxs)(V.Z,(0,M.Z)((0,M.Z)({},q),{},{initialValues:i,onValuesChange:function(e,n){return o((0,M.Z)((0,M.Z)({},i),n))},onFinish:function(e){return n=e,void R.Z.post("/api/admin/website/basic",(0,M.Z)((0,M.Z)({},i),n)).then(function(){var e=(0,H.Z)(T().mark((function e(n){var a;return T().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(a=n.data).error){e.next=5;break}return e.next=4,h.error(a.message);case 4:return e.abrupt("return");case 5:if(0!==a.error){e.next=10;break}return e.next=8,h.success(a.message);case 8:(0,C.YB)(),window.location.reload();case 10:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}());var n},children:[(0,I.jsx)(V.Z.Item,{name:"title",label:"\u7f51\u7ad9\u6807\u9898",rules:[{required:!0}],children:(0,I.jsx)(E.Z,{placeholder:"\u8bf7\u8f93\u5165\u7f51\u7ad9\u6807\u9898",showCount:!0,maxLength:30})}),(0,I.jsx)(V.Z.Item,{name:"second_title",label:"\u7f51\u7ad9\u526f\u6807\u9898",children:(0,I.jsx)(E.Z,{placeholder:"\u8bf7\u8f93\u5165\u7f51\u7ad9\u526f\u6807\u9898",showCount:!0,maxLength:30})}),(0,I.jsx)(V.Z.Item,{name:"keywords",label:"\u7f51\u7ad9\u5173\u952e\u8bcd",children:(0,I.jsx)(E.Z,{showCount:!0,placeholder:"\u8bf7\u8f93\u5165\u7f51\u7ad9\u5173\u952e\u8bcd",maxLength:40})}),(0,I.jsx)(V.Z.Item,{name:"description",label:"\u7f51\u7ad9\u63cf\u8ff0",children:(0,I.jsx)(B.Z,{showCount:!0,rows:5,maxLength:160})}),(0,I.jsx)(V.Z.Item,{name:"favicon_ico_base64",label:"".concat((0,C.sR)().favicon),children:(0,I.jsx)(D,{url:i.favicon_ico_base64,onChange:function(e){o((0,M.Z)((0,M.Z)({},i),{},{favicon_ico_base64:e||""}))}})}),(0,I.jsx)(l.Z,{}),(0,I.jsx)(_.ZP,{disabled:a,type:"primary",htmlType:"submit",children:(0,C.sR)().submit})]}))]})},K={labelCol:{span:8},wrapperCol:{span:16}},G=function(e){var n=e.data,a=e.offline,t=(0,p.useState)(n),s=(0,c.Z)(t,2),i=s[0],o=s[1],d=W.ZP.useMessage({maxCount:3}),u=(0,c.Z)(d,2),h=u[0],m=u[1];return(0,p.useEffect)((function(){o(n)}),[n]),(0,I.jsxs)(I.Fragment,{children:[m,(0,I.jsx)(r.Z,{level:4,children:"ICP\uff0c\u7f51\u7ad9\u7edf\u8ba1\u7b49\u4fe1\u606f"}),(0,I.jsx)(l.Z,{}),(0,I.jsxs)(V.Z,(0,M.Z)((0,M.Z)({},K),{},{initialValues:i,onValuesChange:function(e,n){return o((0,M.Z)((0,M.Z)({},i),n))},onFinish:function(e){return n=e,void R.Z.post("/api/admin/website/other",(0,M.Z)((0,M.Z)({},i),n)).then(function(){var e=(0,H.Z)(T().mark((function e(n){var a;return T().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(a=n.data).error){e.next=5;break}return e.next=4,h.error(a.message);case 4:return e.abrupt("return");case 5:if(0!==a.error){e.next=8;break}return e.next=8,h.success(a.message);case 8:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}());var n},children:[(0,I.jsx)(V.Z.Item,{name:"icp",label:"ICP\u5907\u6848\u4fe1\u606f",children:(0,I.jsx)(B.Z,{})}),(0,I.jsx)(V.Z.Item,{name:"webCm",label:"\u7f51\u7ad9\u7edf\u8ba1",children:(0,I.jsx)(B.Z,{rows:7})}),(0,I.jsx)(V.Z.Item,{name:"robotRuleContent",label:"robots.txt",children:(0,I.jsx)(B.Z,{rows:7,placeholder:"User-agent: *\nDisallow: "})}),(0,I.jsx)(l.Z,{}),(0,I.jsx)(_.ZP,{disabled:a,type:"primary",htmlType:"submit",children:(0,C.sR)().submit})]}))]})},J=a(4351),X=a(2375),$=a(8467),ee={labelCol:{span:8},wrapperCol:{span:16}},ne=function(e){var n=e.data,a=e.offline,t=(0,p.useState)(!1),r=(0,c.Z)(t,2),o=r[0],d=r[1],u=X.Z.useApp().modal,h=W.ZP.useMessage({maxCount:3}),m=(0,c.Z)(h,2),x=m[0],f=m[1],Z=(0,p.useState)(n),v=(0,c.Z)(Z,2),g=v[0],j=v[1],b=(0,$.s0)(),w=function(){var e=(0,H.Z)(T().mark((function e(){var n,a,t;return T().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!o){e.next=2;break}return e.abrupt("return");case 2:return d(!0),e.prev=3,e.next=6,R.Z.get("/api/admin/upgrade");case 6:if(n=e.sent,!(a=n.data).data.upgrade){e.next=13;break}t="".concat((0,C.sR)().newVersion," - #").concat(a.data.version.type),u.info({title:t,content:(0,I.jsx)("div",{dangerouslySetInnerHTML:{__html:a.data.version.changeLog}}),closable:!0,okText:(0,C.sR)().doUpgrade,onOk:function(){b("/upgrade")}}),e.next=17;break;case 13:if(0!==a.error){e.next=17;break}return d(!1),e.next=17,x.info((0,C.sR)().notFoundNewVersion);case 17:return e.prev=17,d(!1),e.finish(17);case 20:case"end":return e.stop()}}),e,null,[[3,,17,20]])})));return function(){return e.apply(this,arguments)}}();return(0,p.useEffect)((function(){j(n)}),[n]),(0,I.jsxs)("div",{style:{maxWidth:600},children:[f,(0,I.jsx)(s.Z,{children:(0,I.jsx)(i.Z,{xs:24,children:(0,I.jsx)(_.ZP,{type:"dashed",disabled:a,loading:o,onClick:w,style:{float:"right"},children:(0,C.sR)().checkUpgrade})})}),(0,I.jsx)(s.Z,{children:(0,I.jsx)(i.Z,{xs:24,children:(0,I.jsxs)(V.Z,(0,M.Z)((0,M.Z)({},ee),{},{initialValues:g,onValuesChange:function(e,n){return j((0,M.Z)((0,M.Z)({},g),n))},onFinish:function(e){return n=e,void R.Z.post("/api/admin/website/upgrade",(0,M.Z)((0,M.Z)({},g),n)).then((function(e){var n=e.data;n.error?x.error(n.message).then():x.success(n.message).then((function(){}))}));var n},children:[(0,I.jsx)(V.Z.Item,{name:"autoUpgradeVersion",label:(0,C.sR)()["admin.upgrade.autoCheckCycle"],children:(0,I.jsxs)(J.Z,{style:{maxWidth:"120px"},children:[(0,I.jsx)(J.Z.Option,{value:86400,children:(0,C.sR)()["admin.upgrade.cycle.oneDay"]},"86400"),(0,I.jsx)(J.Z.Option,{value:604800,children:(0,C.sR)()["admin.upgrade.cycle.oneWeek"]},"604800"),(0,I.jsx)(J.Z.Option,{value:1296e3,children:(0,C.sR)()["admin.upgrade.cycle.halfMonth"]},"1296000"),(0,I.jsx)(J.Z.Option,{value:-1,children:(0,C.sR)()["admin.upgrade.cycle.never"]},"-1")]})}),(0,I.jsx)(V.Z.Item,{valuePropName:"checked",name:"upgradePreview",label:(0,C.sR)()["admin.upgrade.canPreview"],children:(0,I.jsx)(S.Z,{size:"small"})}),(0,I.jsx)(l.Z,{}),(0,I.jsx)(_.ZP,{disabled:a,type:"primary",htmlType:"submit",children:(0,C.sR)().submit})]}))})})]})},ae=a(6050),te=a(8433),re=a(2294),se={labelCol:{span:8},wrapperCol:{span:16}},ie=J.Z.Option,le=function(e){var n=e.data,a=e.offline,t=(0,p.useState)(n),s=(0,c.Z)(t,2),i=s[0],o=s[1],d=W.ZP.useMessage({maxCount:3}),u=(0,c.Z)(d,2),h=u[0],m=u[1];return(0,p.useEffect)((function(){o(n)}),[n]),(0,I.jsxs)(I.Fragment,{children:[m,(0,I.jsxs)(V.Z,(0,M.Z)((0,M.Z)({},se),{},{initialValues:i,onValuesChange:function(e,n){return o((0,M.Z)((0,M.Z)({},i),n))},onFinish:function(e){return n=e,void R.Z.post("/api/admin/website/admin",(0,M.Z)((0,M.Z)({},i),n)).then(function(){var e=(0,H.Z)(T().mark((function e(n){var a;return T().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(a=n.data).error){e.next=5;break}return e.next=4,h.error(a.message);case 4:return e.abrupt("return");case 5:if(0!==a.error){e.next=10;break}return e.next=8,h.success(a.message);case 8:(0,C.YB)(),window.location.reload();case 10:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}());var n},children:[(0,I.jsx)(r.Z,{level:4,children:"\u7ba1\u7406\u754c\u9762\u8bbe\u7f6e"}),(0,I.jsx)(l.Z,{}),(0,I.jsx)(V.Z.Item,{name:"admin_static_resource_base_url",label:"\u7ba1\u7406\u9875\u9759\u6001\u8d44\u6e90\uff08URL\uff09",children:(0,I.jsx)(E.Z,{style:{maxWidth:"300px"},placeholder:"\u7559\u7a7a\uff0c\u53ca\u7981\u7528"})}),(0,I.jsx)(V.Z.Item,{name:"session_timeout",label:"\u7ba1\u7406\u754c\u9762\u4f1a\u8bdd\u8d85\u65f6",rules:[{required:!0}],children:(0,I.jsx)(E.Z,{suffix:"\u5206\u949f",style:{maxWidth:"120px"},max:99999,type:"number",min:5,placeholder:""})}),(0,I.jsx)(V.Z.Item,{name:"language",label:(0,C.sR)().language,children:(0,I.jsxs)(J.Z,{style:{maxWidth:"120px"},children:[(0,I.jsx)(ie,{value:"zh_CN",children:(0,C.sR)().languageChinese}),(0,I.jsx)(ie,{value:"en_US",children:(0,C.sR)().languageEnglish})]})}),(0,I.jsx)(V.Z.Item,{valuePropName:"checked",name:"admin_darkMode",label:(0,C.sR)()["admin.dark.mode"],children:(0,I.jsx)(S.Z,{size:"small"})}),(0,I.jsx)(V.Z.Item,{name:"admin_article_page_size",label:(0,C.sR)().admin_article_page_size,children:(0,I.jsx)(J.Z,{style:{maxWidth:"120px"},options:[{value:10,label:"10 "+(0,te.UY)()},{value:20,label:"20 "+(0,te.UY)()},{value:50,label:"50 "+(0,te.UY)()},{value:100,label:"100 "+(0,te.UY)()}]})}),(0,I.jsx)(V.Z.Item,{label:(0,C.sR)()["admin.color.primary"],children:(0,I.jsxs)("div",{style:{display:"flex",justifyContent:"flex-start",alignItems:"center"},children:[(0,I.jsx)(ae.Z,{value:i.admin_color_primary,onChange:function(e){o((0,M.Z)((0,M.Z)({},i),{},{admin_color_primary:e.toHexString()}))},presets:[{defaultOpen:!0,label:"\u9884\u8bbe",colors:re.bE}]}),(0,I.jsx)("span",{style:{paddingLeft:8},children:i.admin_color_primary})]})}),(0,I.jsx)(V.Z.Item,{name:"favicon_png_pwa_192_base64",label:"".concat((0,C.sR)().favicon," PWA (192px)"),children:(0,I.jsx)(D,{url:i.favicon_png_pwa_192_base64,onChange:function(e){o((0,M.Z)((0,M.Z)({},i),{},{favicon_png_pwa_192_base64:e||""}))}})}),(0,I.jsx)(V.Z.Item,{name:"favicon_png_pwa_512_base64",label:"".concat((0,C.sR)().favicon," PWA (512px)"),children:(0,I.jsx)(D,{url:i.favicon_png_pwa_512_base64,onChange:function(e){o((0,M.Z)((0,M.Z)({},i),{},{favicon_png_pwa_512_base64:e||""}))}})}),(0,I.jsx)(r.Z,{level:4,children:"\u6587\u7ae0/\u7f16\u8f91\u5668\u8bbe\u7f6e"}),(0,I.jsx)(l.Z,{}),(0,I.jsx)(V.Z.Item,{name:"article_auto_digest_length",label:"\u6587\u7ae0\u81ea\u52a8\u6458\u8981\u6700\u5927\u957f\u5ea6",children:(0,I.jsx)(E.Z,{suffix:"\u5b57",style:{maxWidth:"120px"},max:99999,type:"number",min:-1,placeholder:""})}),(0,I.jsx)(l.Z,{}),(0,I.jsx)(_.ZP,{disabled:a,type:"primary",htmlType:"submit",children:(0,C.sR)().submit})]}))]})},ce=function(e){var n=e.data,a=e.offline,c=window.location.pathname.replace("/admin/website","").replace("/","");""===c&&(c="basic");var o=function(e,n){var a="basic"===e?"/website":"/website/"+e;return(0,I.jsx)(k.rU,{to:a,replace:!0,style:{color:c===e?(0,C.RQ)():"inherit"},children:n})},d=function(){return"basic"===c?(0,I.jsx)(s.Z,{children:(0,I.jsx)(i.Z,{xs:24,style:{maxWidth:600},children:(0,I.jsx)(Q,{offline:a,data:n})})}):"blog"===c?(0,I.jsx)(s.Z,{children:(0,I.jsx)(i.Z,{xs:24,style:{maxWidth:600},children:(0,I.jsx)(L,{offline:a,data:n})})}):"admin"===c?(0,I.jsx)(s.Z,{children:(0,I.jsx)(i.Z,{xs:24,style:{maxWidth:600},children:(0,I.jsx)(le,{offline:a,data:n})})}):"template"===c?(0,I.jsx)(P,{data:n}):"other"===c?(0,I.jsx)(s.Z,{children:(0,I.jsx)(i.Z,{xs:24,style:{maxWidth:600},children:(0,I.jsx)(G,{offline:a,data:n})})}):"upgrade"===c?(0,I.jsx)(ne,{offline:a,data:n}):(0,I.jsx)(I.Fragment,{})};return(0,I.jsxs)(I.Fragment,{children:[(0,I.jsx)(r.Z,{className:"page-header",level:3,children:(0,C.sR)()["admin.setting"]}),(0,I.jsx)(l.Z,{}),(0,I.jsx)(t.Z,{activeKey:c,items:[{key:"basic",label:o("basic",(0,C.sR)()["admin.basic.manage"]),children:d()},{key:"blog",label:o("blog",(0,C.sR)()["admin.blog.manage"]),children:d()},{key:"admin",label:o("admin",(0,C.sR)()["admin.admin.manage"]),children:d()},{key:"template",label:o("template",(0,C.sR)()["admin.template.manage"]),children:d()},{key:"other",label:o("other",(0,C.sR)()["admin.other.manage"]),children:d()},{key:"upgrade",label:o("upgrade",(0,C.sR)()["admin.upgrade.manage"]),children:d()}]})]})}},392:function(e,n,a){a.d(n,{Z:function(){return c}});var t=a(7460),r=a(7313),s={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M864 248H728l-32.4-90.8a32.07 32.07 0 00-30.2-21.2H358.6c-13.5 0-25.6 8.5-30.1 21.2L296 248H160c-44.2 0-80 35.8-80 80v456c0 44.2 35.8 80 80 80h704c44.2 0 80-35.8 80-80V328c0-44.2-35.8-80-80-80zm8 536c0 4.4-3.6 8-8 8H160c-4.4 0-8-3.6-8-8V328c0-4.4 3.6-8 8-8h186.7l17.1-47.8 22.9-64.2h250.5l22.9 64.2 17.1 47.8H864c4.4 0 8 3.6 8 8v456zM512 384c-88.4 0-160 71.6-160 160s71.6 160 160 160 160-71.6 160-160-71.6-160-160-160zm0 256c-53 0-96-43-96-96s43-96 96-96 96 43 96 96-43 96-96 96z"}}]},name:"camera",theme:"outlined"},i=a(9126),l=function(e,n){return r.createElement(i.Z,(0,t.Z)({},e,{ref:n,icon:s}))};var c=r.forwardRef(l)},6446:function(e,n,a){a.d(n,{Z:function(){return c}});var t=a(7460),r=a(7313),s={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M864 256H736v-80c0-35.3-28.7-64-64-64H352c-35.3 0-64 28.7-64 64v80H160c-17.7 0-32 14.3-32 32v32c0 4.4 3.6 8 8 8h60.4l24.7 523c1.6 34.1 29.8 61 63.9 61h454c34.2 0 62.3-26.8 63.9-61l24.7-523H888c4.4 0 8-3.6 8-8v-32c0-17.7-14.3-32-32-32zm-200 0H360v-72h304v72z"}}]},name:"delete",theme:"filled"},i=a(9126),l=function(e,n){return r.createElement(i.Z,(0,t.Z)({},e,{ref:n,icon:s}))};var c=r.forwardRef(l)},7515:function(e,n,a){a.d(n,{Z:function(){return c}});var t=a(7460),r=a(7313),s={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M360 184h-8c4.4 0 8-3.6 8-8v8h304v-8c0 4.4 3.6 8 8 8h-8v72h72v-80c0-35.3-28.7-64-64-64H352c-35.3 0-64 28.7-64 64v80h72v-72zm504 72H160c-17.7 0-32 14.3-32 32v32c0 4.4 3.6 8 8 8h60.4l24.7 523c1.6 34.1 29.8 61 63.9 61h454c34.2 0 62.3-26.8 63.9-61l24.7-523H888c4.4 0 8-3.6 8-8v-32c0-17.7-14.3-32-32-32zM731.3 840H292.7l-24.2-512h487l-24.2 512z"}}]},name:"delete",theme:"outlined"},i=a(9126),l=function(e,n){return r.createElement(i.Z,(0,t.Z)({},e,{ref:n,icon:s}))};var c=r.forwardRef(l)},1741:function(e,n,a){a.d(n,{Z:function(){return c}});var t=a(7460),r=a(7313),s={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M942.2 486.2C847.4 286.5 704.1 186 512 186c-192.2 0-335.4 100.5-430.2 300.3a60.3 60.3 0 000 51.5C176.6 737.5 319.9 838 512 838c192.2 0 335.4-100.5 430.2-300.3 7.7-16.2 7.7-35 0-51.5zM512 766c-161.3 0-279.4-81.8-362.7-254C232.6 339.8 350.7 258 512 258c161.3 0 279.4 81.8 362.7 254C791.5 684.2 673.4 766 512 766zm-4-430c-97.2 0-176 78.8-176 176s78.8 176 176 176 176-78.8 176-176-78.8-176-176-176zm0 288c-61.9 0-112-50.1-112-112s50.1-112 112-112 112 50.1 112 112-50.1 112-112 112z"}}]},name:"eye",theme:"outlined"},i=a(9126),l=function(e,n){return r.createElement(i.Z,(0,t.Z)({},e,{ref:n,icon:s}))};var c=r.forwardRef(l)},9893:function(e,n,a){a.d(n,{Z:function(){return c}});var t=a(7460),r=a(7313),s={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M924.8 625.7l-65.5-56c3.1-19 4.7-38.4 4.7-57.8s-1.6-38.8-4.7-57.8l65.5-56a32.03 32.03 0 009.3-35.2l-.9-2.6a443.74 443.74 0 00-79.7-137.9l-1.8-2.1a32.12 32.12 0 00-35.1-9.5l-81.3 28.9c-30-24.6-63.5-44-99.7-57.6l-15.7-85a32.05 32.05 0 00-25.8-25.7l-2.7-.5c-52.1-9.4-106.9-9.4-159 0l-2.7.5a32.05 32.05 0 00-25.8 25.7l-15.8 85.4a351.86 351.86 0 00-99 57.4l-81.9-29.1a32 32 0 00-35.1 9.5l-1.8 2.1a446.02 446.02 0 00-79.7 137.9l-.9 2.6c-4.5 12.5-.8 26.5 9.3 35.2l66.3 56.6c-3.1 18.8-4.6 38-4.6 57.1 0 19.2 1.5 38.4 4.6 57.1L99 625.5a32.03 32.03 0 00-9.3 35.2l.9 2.6c18.1 50.4 44.9 96.9 79.7 137.9l1.8 2.1a32.12 32.12 0 0035.1 9.5l81.9-29.1c29.8 24.5 63.1 43.9 99 57.4l15.8 85.4a32.05 32.05 0 0025.8 25.7l2.7.5a449.4 449.4 0 00159 0l2.7-.5a32.05 32.05 0 0025.8-25.7l15.7-85a350 350 0 0099.7-57.6l81.3 28.9a32 32 0 0035.1-9.5l1.8-2.1c34.8-41.1 61.6-87.5 79.7-137.9l.9-2.6c4.5-12.3.8-26.3-9.3-35zM788.3 465.9c2.5 15.1 3.8 30.6 3.8 46.1s-1.3 31-3.8 46.1l-6.6 40.1 74.7 63.9a370.03 370.03 0 01-42.6 73.6L721 702.8l-31.4 25.8c-23.9 19.6-50.5 35-79.3 45.8l-38.1 14.3-17.9 97a377.5 377.5 0 01-85 0l-17.9-97.2-37.8-14.5c-28.5-10.8-55-26.2-78.7-45.7l-31.4-25.9-93.4 33.2c-17-22.9-31.2-47.6-42.6-73.6l75.5-64.5-6.5-40c-2.4-14.9-3.7-30.3-3.7-45.5 0-15.3 1.2-30.6 3.7-45.5l6.5-40-75.5-64.5c11.3-26.1 25.6-50.7 42.6-73.6l93.4 33.2 31.4-25.9c23.7-19.5 50.2-34.9 78.7-45.7l37.9-14.3 17.9-97.2c28.1-3.2 56.8-3.2 85 0l17.9 97 38.1 14.3c28.7 10.8 55.4 26.2 79.3 45.8l31.4 25.8 92.8-32.9c17 22.9 31.2 47.6 42.6 73.6L781.8 426l6.5 39.9zM512 326c-97.2 0-176 78.8-176 176s78.8 176 176 176 176-78.8 176-176-78.8-176-176-176zm79.2 255.2A111.6 111.6 0 01512 614c-29.9 0-58-11.7-79.2-32.8A111.6 111.6 0 01400 502c0-29.9 11.7-58 32.8-79.2C454 401.6 482.1 390 512 390c29.9 0 58 11.6 79.2 32.8A111.6 111.6 0 01624 502c0 29.9-11.7 58-32.8 79.2z"}}]},name:"setting",theme:"outlined"},i=a(9126),l=function(e,n){return r.createElement(i.Z,(0,t.Z)({},e,{ref:n,icon:s}))};var c=r.forwardRef(l)}}]);