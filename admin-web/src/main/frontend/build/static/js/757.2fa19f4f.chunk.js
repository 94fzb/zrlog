"use strict";(self.webpackChunkadmin_web_frontend=self.webpackChunkadmin_web_frontend||[]).push([[757],{4553:function(e,n,t){var r=t(1413),a=t(5861),i=t(9439),s=t(7757),o=t.n(s),l=t(2745),c=t(6593),u=t(886),d=t(8391),p=t(6146),f=t(1522),m=t(5333),g=t(3561),h=t(6155),Z=t(8773),x=t(5536),v=t(3712);n.Z=function(e){var n=e.deleteApi,t=e.editBtnRender,s=e.addBtnRender,y=e.columns,j=e.datasource,b=e.searchKey,w=e.deleteSuccessCallback,k=(0,Z.s0)(),C=(0,Z.TH)(),z=function(e,n,t){return I(e,n,t,-1)},I=function(e,n,t,r){var a={};e>1&&(a.page=e),10!=n&&1e6!=n&&(a.size=n),t&&t.trim().length>0&&(a.key=t.trim()),r>0&&(a[g.NJ]=r);var i=(0,f.bI)(a);return 0===i.length?C.pathname:C.pathname+"?"+i},P=function(e,n,t){k(z(e,n,t))},S=function(e,n,t){k(I(e,n,t,(new Date).getTime()))},O=(0,d.useState)({pagination:{page:null!==j&&void 0!==j&&j.page?j.page:1,key:null===j||void 0===j?void 0:j.key,size:null!==j&&void 0!==j&&j.size?null===j||void 0===j?void 0:j.size:10},query:null===j||void 0===j?void 0:j.key,tableLoaded:!0,rows:j?j.rows:[],tablePagination:{total:null===j||void 0===j?void 0:j.totalElements,current:null===j||void 0===j?void 0:j.page,pageSize:null===j||void 0===j?void 0:j.size,onChange:function(e,n){P(e,n,R.query)}}}),q=(0,i.Z)(O,2),R=q[0],B=q[1],E=l.ZP.useMessage(),N=(0,i.Z)(E,2),A=N[0],_=N[1],F=function(){var e=(0,a.Z)(o().mark((function e(n,t,r){var a;return o().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,p.Z.post(t+"?id="+r);case 2:if(!(a=e.sent).data.error){e.next=6;break}return A.error(a.data.message),e.abrupt("return");case 6:A.info("\u5220\u9664\u6210\u529f"),S(n.page,n.size,R.query);case 8:case"end":return e.stop()}}),e)})));return function(n,t,r){return e.apply(this,arguments)}}();(0,d.useEffect)((function(){b!==R.query&&(B((0,r.Z)((0,r.Z)({},R),{},{query:b})),P(1,R.pagination.size,b))}),[b]),(0,d.useEffect)((function(){B((function(e){return(0,r.Z)((0,r.Z)({},e),{},{rows:j?j.rows:[],pagination:{page:null!==j&&void 0!==j&&j.page?j.page:1,size:null!==j&&void 0!==j&&j.size?j.size:10},tablePagination:{current:null===j||void 0===j?void 0:j.page,pageSize:null===j||void 0===j?void 0:j.size,total:null===j||void 0===j?void 0:j.totalElements}})}))}),[j]);return(0,v.jsxs)(v.Fragment,{children:[_,s?s((function(){S(R.pagination.page,R.pagination.size,R.query)})):void 0,(0,v.jsx)(u.Z,{onChange:function(e){P(e.current?e.current:1,e.pageSize?e.pageSize:10,R.query)},style:{minHeight:512},columns:function(){var e=[];return e.push({title:"ID",dataIndex:"id",key:"id",width:80}),e.push({title:"",dataIndex:"id",key:"action",width:100,render:function(e,r){return e?(0,v.jsxs)(c.Z,{size:16,children:[(0,v.jsx)(m.Z,{title:(0,g.sR)().deleteTips,onConfirm:function(){return F(R.pagination,n,r.id).then((function(){w&&w(r.id)}))},children:(0,v.jsx)(h.Z,{style:{color:"red"}})}),t?t(e,r,(function(){S(R.pagination.page,R.pagination.size,R.query)})):null]}):null}}),y.forEach((function(n){e.push(n)})),e}(),pagination:(0,r.Z)((0,r.Z)({hideOnSinglePage:!0},R.tablePagination),{},{itemRender:function(e,n,t){return(0,v.jsx)(x.rU,{to:z(e,null!==j&&void 0!==j&&j.size?j.size:10,R.query),children:t},e)}}),dataSource:R.rows,scroll:{x:"90vw"}})]})}},3757:function(e,n,t){t.r(n),t.d(n,{default:function(){return S}});var r=t(8881),a=t(1474),i=t(3561),s=t(4553),o=t(1413),l=t(5861),c=t(9439),u=t(7757),d=t.n(u),p=t(8391),f=t(9012),m=t(7849),g=t(7396),h=t(2805),Z=t(7489),x=t(7372),v=t(8288),y=t(2503),j=t(6146),b=t(3712),w={labelCol:{span:4},wrapperCol:{span:20}},k=function(e){var n=e.addSuccessCall,t=(0,p.useState)(!1),r=(0,c.Z)(t,2),a=r[0],s=r[1],u=(0,p.useState)(),k=(0,c.Z)(u,2),C=k[0],z=k[1],I=f.Z.useApp().message;return(0,b.jsxs)(b.Fragment,{children:[(0,b.jsx)(m.ZP,{type:"primary",onClick:function(){return s(!0)},style:{marginBottom:8},children:(0,i.sR)().add}),(0,b.jsx)(g.Z,{title:(0,i.sR)().add,open:a,onOk:function(){j.Z.post("/api/admin/type/add",C).then(function(){var e=(0,l.Z)(d().mark((function e(t){var r;return d().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(r=t.data).error){e.next=5;break}return e.next=4,I.error(r.message);case 4:return e.abrupt("return");case 5:s(!1),n();case 7:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}())},onCancel:function(){return s(!1)},children:(0,b.jsxs)(h.Z,(0,o.Z)((0,o.Z)({onValuesChange:function(e,n){z(n)}},w),{},{children:[(0,b.jsx)(v.Z,{children:(0,b.jsx)(Z.Z,{span:24,children:(0,b.jsx)(h.Z.Item,{label:(0,i.sR)()["admin.type.manage"],style:{marginBottom:8},name:"typeName",rules:[{required:!0,message:""}],children:(0,b.jsx)(x.Z,{})})})}),(0,b.jsx)(v.Z,{children:(0,b.jsx)(Z.Z,{span:24,children:(0,b.jsx)(h.Z.Item,{label:"\u522b\u540d",style:{marginBottom:8},name:"alias",rules:[{required:!0,message:""}],children:(0,b.jsx)(x.Z,{})})})}),(0,b.jsx)(v.Z,{children:(0,b.jsx)(Z.Z,{span:24,children:(0,b.jsx)(h.Z.Item,{label:"\u7b80\u4ecb",style:{marginBottom:8},name:"remark",rules:[{required:!0,message:""}],children:(0,b.jsx)(y.Z,{})})})})]}))})]})},C=t(3806),z=t(5536),I={labelCol:{span:4},wrapperCol:{span:20}},P=function(e){var n=e.record,t=e.editSuccessCall,r=(0,p.useState)(!1),a=(0,c.Z)(r,2),s=a[0],u=a[1],m=(0,p.useState)(n),w=(0,c.Z)(m,2),k=w[0],P=w[1],S=f.Z.useApp().message;return(0,b.jsxs)(b.Fragment,{children:[(0,b.jsx)(z.rU,{to:"#edit-"+n.id,onClick:function(e){e.preventDefault(),e.stopPropagation(),u(!0)},children:(0,b.jsx)(C.Z,{style:{marginBottom:8,color:(0,i.RQ)()}})}),(0,b.jsx)(g.Z,{title:(0,i.sR)().edit,open:s,onOk:function(){j.Z.post("/api/admin/type/update",k).then(function(){var e=(0,l.Z)(d().mark((function e(n){var r;return d().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!(r=n.data).error){e.next=5;break}return e.next=4,S.error(r.message);case 4:return e.abrupt("return");case 5:u(!1),t&&t();case 7:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}())},onCancel:function(){return u(!1)},children:(0,b.jsxs)(h.Z,(0,o.Z)((0,o.Z)({initialValues:n,onValuesChange:function(e,n){P(n)}},I),{},{children:[(0,b.jsx)(h.Z.Item,{name:"id",style:{display:"none"},children:(0,b.jsx)(x.Z,{hidden:!0})}),(0,b.jsx)(v.Z,{children:(0,b.jsx)(Z.Z,{span:24,children:(0,b.jsx)(h.Z.Item,{label:(0,i.sR)()["admin.type.manage"],style:{marginBottom:8},name:"typeName",rules:[{required:!0,message:""}],children:(0,b.jsx)(x.Z,{})})})}),(0,b.jsx)(v.Z,{children:(0,b.jsx)(Z.Z,{span:24,children:(0,b.jsx)(h.Z.Item,{label:"\u522b\u540d",style:{marginBottom:8},name:"alias",rules:[{required:!0,message:""}],children:(0,b.jsx)(x.Z,{})})})}),(0,b.jsx)(v.Z,{children:(0,b.jsx)(Z.Z,{span:24,children:(0,b.jsx)(h.Z.Item,{label:"\u7b80\u4ecb",style:{marginBottom:8},name:"remark",rules:[{required:!0,message:""}],children:(0,b.jsx)(y.Z,{})})})})]}))})]})},S=function(e){var n=e.data;return(0,b.jsxs)(b.Fragment,{children:[(0,b.jsx)(r.Z,{className:"page-header",level:3,children:(0,i.sR)()["admin.type.manage"]}),(0,b.jsx)(a.Z,{}),(0,b.jsx)(s.Z,{columns:[{title:(0,i.sR)()["admin.type.manage"],dataIndex:"typeName",key:"typeName",width:240,render:function(e,n){return(0,b.jsx)("a",{rel:"noopener noreferrer",target:"_blank",href:n.url,children:e})}},{title:"\u522b\u540d",dataIndex:"alias",key:"alias",width:120},{title:"\u7b80\u4ecb",key:"remark",dataIndex:"remark",width:240},{title:"\u6587\u7ae0\u6570\u91cf",dataIndex:"amount",key:"amount",width:80}],addBtnRender:function(e){return(0,b.jsx)(k,{addSuccessCall:e})},editBtnRender:function(e,n,t){return(0,b.jsx)(P,{record:n,editSuccessCall:t})},datasource:n,deleteApi:"/api/admin/type/delete"})]})}},3806:function(e,n,t){t.d(n,{Z:function(){return l}});var r=t(7462),a=t(8391),i={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M257.7 752c2 0 4-.2 6-.5L431.9 722c2-.4 3.9-1.3 5.3-2.8l423.9-423.9a9.96 9.96 0 000-14.1L694.9 114.9c-1.9-1.9-4.4-2.9-7.1-2.9s-5.2 1-7.1 2.9L256.8 538.8c-1.5 1.5-2.4 3.3-2.8 5.3l-29.5 168.2a33.5 33.5 0 009.4 29.8c6.6 6.4 14.9 9.9 23.8 9.9zm67.4-174.4L687.8 215l73.3 73.3-362.7 362.6-88.9 15.7 15.6-89zM880 836H144c-17.7 0-32 14.3-32 32v36c0 4.4 3.6 8 8 8h784c4.4 0 8-3.6 8-8v-36c0-17.7-14.3-32-32-32z"}}]},name:"edit",theme:"outlined"},s=t(9345),o=function(e,n){return a.createElement(s.Z,(0,r.Z)({},e,{ref:n,icon:i}))};var l=a.forwardRef(o)},7396:function(e,n,t){t.d(n,{Z:function(){return b}});var r=t(7841),a=t(9022),i=t(1630),s=t(9439),o=t(8391),l=t(1549),c=t.n(l),u=t(4765),d=t(2466),p=t(3710),f=t(2123),m=t(5580),g=t(6584),h=t(9532),Z=function(e,n){var t={};for(var r in e)Object.prototype.hasOwnProperty.call(e,r)&&n.indexOf(r)<0&&(t[r]=e[r]);if(null!=e&&"function"===typeof Object.getOwnPropertySymbols){var a=0;for(r=Object.getOwnPropertySymbols(e);a<r.length;a++)n.indexOf(r[a])<0&&Object.prototype.propertyIsEnumerable.call(e,r[a])&&(t[r[a]]=e[r[a]])}return t},x=(0,d.i)((function(e){var n=e.prefixCls,t=e.className,r=e.closeIcon,a=e.closable,i=e.type,l=e.title,d=e.children,x=e.footer,v=Z(e,["prefixCls","className","closeIcon","closable","type","title","children","footer"]),y=o.useContext(p.E_).getPrefixCls,j=y(),b=n||y("modal"),w=(0,h.Z)(j),k=(0,g.ZP)(b,w),C=(0,s.Z)(k,3),z=C[0],I=C[1],P=C[2],S="".concat(b,"-confirm"),O={};return O=i?{closable:null!==a&&void 0!==a&&a,title:"",footer:"",children:o.createElement(f.O,Object.assign({},e,{prefixCls:b,confirmPrefixCls:S,rootPrefixCls:j,content:d}))}:{closable:null===a||void 0===a||a,title:l,footer:null!==x&&o.createElement(m.$,Object.assign({},e)),children:d},z(o.createElement(u.s,Object.assign({prefixCls:b,className:c()(I,"".concat(b,"-pure-panel"),i&&S,i&&"".concat(S,"-").concat(i),t,P,w)},v,{closeIcon:(0,m.b)(b,r),closable:a},O)))})),v=t(9117);function y(e){return(0,r.ZP)((0,r.uW)(e))}var j=i.Z;j.useModal=v.Z,j.info=function(e){return(0,r.ZP)((0,r.cw)(e))},j.success=function(e){return(0,r.ZP)((0,r.vq)(e))},j.error=function(e){return(0,r.ZP)((0,r.AQ)(e))},j.warning=y,j.warn=y,j.confirm=function(e){return(0,r.ZP)((0,r.Au)(e))},j.destroyAll=function(){for(;a.Z.length;){var e=a.Z.pop();e&&e()}},j.config=r.ai,j._InternalPanelDoNotUseOrYouWillBeFired=x;var b=j}}]);