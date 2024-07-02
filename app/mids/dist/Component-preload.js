//@ui5-bundle mids/Component-preload.js
sap.ui.require.preload({
	"mids/Component.js":function(){
sap.ui.define(["sap/ui/core/UIComponent","sap/ui/Device","mids/model/models"],function(e,i,t){"use strict";return e.extend("mids.Component",{metadata:{manifest:"json"},init:function(){e.prototype.init.apply(this,arguments);this.getRouter().initialize();this.setModel(t.createDeviceModel(),"device")}})});
},
	"mids/controller/App.controller.js":function(){
sap.ui.define(["sap/ui/core/mvc/Controller"],function(n){"use strict";return n.extend("mids.controller.App",{onInit:function(){}})});
},
	"mids/controller/View1.controller.js":function(){
sap.ui.define(["sap/ui/core/mvc/Controller","sap/ui/model/json/JSONModel","sap/ui/export/Spreadsheet"],function(e,t,s){"use strict";return e.extend("mids.controller.View1",{onInit:function(){this.getView().setModel(new t({mids:[]}),"mids");var e=this.getView().getModel("mids");e.setSizeLimit(1e3)},onGeneratePress:function(){var e=parseInt(this.getView().byId("inputMIDs").getValue());var t=this.getView().getModel("mids");var s=this;$.ajax({url:"/odata/v4/MasterIDService/GenerateIDs(count="+e+")",method:"GET",success:function(e){console.log(e.value);t.setProperty("/mids",e.value.map(function(e){return{mids:e.toString()}}));var o=s.byId("midsTable");o.setModel(t)},error:function(e){console.error(e);sap.m.MessageToast.show("Failed to retrieve data. Please contact support.")}})},onExportPress:function(){var e=this.getView().getModel("mids");var t=e.getProperty("/mids");if(t.length===0){sap.m.MessageToast.show("No data available to export.")}else{var o={workbook:{columns:[{label:"MIDs",property:"mids"}]},dataSource:t,fileName:"MIDs.xlsx"};var i=new s(o);i.build().finally(function(){i.destroy()})}}})});
},
	"mids/i18n/i18n.properties":'# This is the resource bundle for mids\n\n#Texts for manifest.json\n\n#XTIT: Application name\nappTitle=MasterID Generator\n\n#YDES: Application description\nappDescription=An SAP Fiori application.\n#XTIT: Main view title\ntitle=MasterID Generator\n\n#XFLD,60\nflpTitle=MasterIDs Generation\n',
	"mids/manifest.json":'{"_version":"1.59.0","sap.app":{"id":"mids","type":"application","i18n":"i18n/i18n.properties","applicationVersion":{"version":"0.0.1"},"title":"{{appTitle}}","description":"{{appDescription}}","resources":"resources.json","sourceTemplate":{"id":"@sap/generator-fiori:basic","version":"1.14.0","toolsId":"8161d87b-6d8f-4441-bc14-4fcacf5ef6f7"},"dataSources":{"mainService":{"uri":"odata/v4/MasterIDService/","type":"OData","settings":{"annotations":[],"odataVersion":"4.0"}}},"crossNavigation":{"inbounds":{"MasterIDs-Generate":{"semanticObject":"MasterIDs","action":"Generate","title":"{{flpTitle}}","signature":{"parameters":{},"additionalParameters":"allowed"}}}}},"sap.ui":{"technology":"UI5","icons":{"icon":"","favIcon":"","phone":"","phone@2":"","tablet":"","tablet@2":""},"deviceTypes":{"desktop":true,"tablet":true,"phone":true}},"sap.ui5":{"flexEnabled":true,"dependencies":{"minUI5Version":"1.125.1","libs":{"sap.m":{},"sap.ui.core":{},"sap.f":{},"sap.suite.ui.generic.template":{},"sap.ui.comp":{},"sap.ui.generic.app":{},"sap.ui.table":{},"sap.ushell":{}}},"contentDensities":{"compact":true,"cozy":true},"models":{"i18n":{"type":"sap.ui.model.resource.ResourceModel","settings":{"bundleName":"mids.i18n.i18n"}},"":{"dataSource":"mainService","preload":true,"settings":{"operationMode":"Server","autoExpandSelect":true,"earlyRequests":true}}},"resources":{"css":[{"uri":"css/style.css"}]},"routing":{"config":{"routerClass":"sap.m.routing.Router","viewType":"XML","async":true,"viewPath":"mids.view","controlAggregation":"pages","controlId":"app","clearControlAggregation":false},"routes":[{"name":"RouteView1","pattern":":?query:","target":["TargetView1"]}],"targets":{"TargetView1":{"viewType":"XML","transition":"slide","clearControlAggregation":false,"viewId":"View1","viewName":"View1"}}},"rootView":{"viewName":"mids.view.App","type":"XML","async":true,"id":"App"}},"sap.cloud":{"public":true,"service":"mids_generation.service"}}',
	"mids/model/models.js":function(){
sap.ui.define(["sap/ui/model/json/JSONModel","sap/ui/Device"],function(e,n){"use strict";return{createDeviceModel:function(){var i=new e(n);i.setDefaultBindingMode("OneWay");return i}}});
},
	"mids/view/App.view.xml":'<mvc:View controllerName="mids.controller.App"\n    xmlns:html="http://www.w3.org/1999/xhtml"\n    xmlns:mvc="sap.ui.core.mvc" displayBlock="true"\n    xmlns="sap.m"><App id="app"></App></mvc:View>\n',
	"mids/view/View1.view.xml":'<mvc:View\n    controllerName="mids.controller.View1"\n    xmlns="sap.m"\n    xmlns:mvc="sap.ui.core.mvc"\n    xmlns:core="sap.ui.core"\n    xmlns:tnt="sap.tnt"\n    displayBlock="true"><Page id="page" title="Generate MIDs"><content><VBox id="_IDGenVBox1" class="sapUiSmallMargin"><HBox id="_IDGenHBox1" justifyContent="SpaceBetween" alignItems="End"><VBox id="_IDGenVBox2"><Label id="_IDGenLabel1" text="Enter the Number of MIDs to be generated" labelFor="inputMIDs"/><Input id="inputMIDs" value="" width="auto" placeholder="e.g. 6"/></VBox><VBox id="_IDGenVBox3"><Button id="_IDGenButton1" text="Go" press="onGeneratePress"/><Button id="_IDGenButton2" text="Export" press="onExportPress"/></VBox></HBox><Table id="midsTable" items="{path: \'/mids\'}" visible="{= ${/mids}.length > 0 }"><columns><Column id="_IDGenColumn1"><Text id="_IDGenText1" text="MIDs"/></Column></columns><items><ColumnListItem id="_IDGenColumnListItem1"><cells><ObjectIdentifier id="_IDGenObjectIdentifier1" text="{mids}"/></cells></ColumnListItem></items></Table></VBox></content></Page></mvc:View>\n'
});
//# sourceMappingURL=Component-preload.js.map
