sap.ui.define(["sap/ui/core/mvc/Controller","sap/ui/model/json/JSONModel","sap/ui/export/Spreadsheet"],function(e,t,o){"use strict";return e.extend("mids.controller.View1",{onInit:function(){this.getView().setModel(new t({mids:[]}),"mids")},onGeneratePress:function(){var e=this.getView().byId("inputMIDs").getValue();var t=this.getView().getModel("mids");var o=this;$.ajax({url:"/odata/v4/MasterIDService/GenerateIDs(count="+e+")",method:"GET",success:function(e){console.log(e.value);t.setProperty("/mids",e.value.map(function(e){return{mids:e.toString()}}));var r=o.byId("midsTable");r.setModel(t)},error:function(e){console.error(e)}})},onExportPress:function(){var e=this.getView().getModel("mids");var t=e.getProperty("/mids");var r={workbook:{columns:[{label:"MIDs",property:"mids"}]},dataSource:t,fileName:"MIDs.xlsx"};var s=new o(r);s.build().finally(function(){s.destroy()})}})});
//# sourceMappingURL=View1.controller.js.map