// sap.ui.define([
//     "sap/ui/core/mvc/Controller"
// ],
// function (Controller) {
//     "use strict";

//     return Controller.extend("mids.controller.View1", {
//         onInit: function () {

//         }
//     });
// });
sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/model/json/JSONModel",
    "sap/ui/export/Spreadsheet"
], function(Controller, JSONModel, Spreadsheet) {
    "use strict";

    return Controller.extend("mids.controller.View1", {
        onInit: function () {
            this.getView().setModel(new JSONModel({ mids: [] }), "mids");
        },

        onGeneratePress: function () {
            var iCount = this.getView().byId("inputMIDs").getValue();
            var oModel = this.getView().getModel("mids");

            // Call the backend service
            $.ajax({
                url: "/odata/v4/MasterIDService/GenerateIDs(count=" + iCount + ")",
                method: "GET",
                success: function (data) {
                    // Update the model with the new MIDs
                    console.log(data.value)
                    oModel.setProperty("/mids", data.value.map(function(mid) {
                        return { mid: mid.toString() };
                    }));
                },
                error: function (error) {
                    // Handle error
                    console.error(error);
                }
            });
        },

        onExportPress: function () {
            var oModel = this.getView().getModel("mids");
            var aMIDs = oModel.getProperty("/mids");

            var oSettings = {
                workbook: { columns: [{ label: 'MID', property: 'mid' }] },
                dataSource: aMIDs,
                fileName: "MIDs.xlsx"
            };

            var oSheet = new Spreadsheet(oSettings);
            oSheet.build().finally(function() {
                oSheet.destroy();
            });
        }
    });
});
