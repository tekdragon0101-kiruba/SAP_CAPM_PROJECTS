sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/model/json/JSONModel",
    "sap/ui/export/Spreadsheet"
], function(Controller, JSONModel, Spreadsheet) {
    "use strict";

    return Controller.extend("mids.controller.View1", {
        onInit: function () {
            this.getView().setModel(new JSONModel({ mids: [] }), "mids");
            var oModel = this.getView().getModel("mids");
            oModel.setSizeLimit(1000); // set the size limit to display in page
        },

        onGeneratePress: function () {
            var iCount = this.getView().byId("inputMIDs").getValue();
            var oModel = this.getView().getModel("mids");
            var oController = this;  // Store reference to the controller
        
            // Call the backend service
            $.ajax({
                url: "/odata/v4/MasterIDService/GenerateIDs(count=" + parseInt(iCount) + ")",
                method: "GET",
                success: function (data) {
                    // Update the model with the new MIDs
                    console.log(data.value)
                    oModel.setProperty("/mids", data.value.map(function(mids) {
                        return { mids: mids.toString()};
                    }));  
                    // Get the table control
                    var oTable = oController.byId("midsTable");  // Use the stored reference
        
                    // Set the model for the table
                    oTable.setModel(oModel);  
                },
                error: function (error) {
                    // Handle error
                    console.error(error);
                     // Display a user-friendly message
                    sap.m.MessageToast.show("Failed to retrieve data. Please contact support.");
                }
            });
        },

        onExportPress: function () {
            var oModel = this.getView().getModel("mids");
            var aMIDs = oModel.getProperty("/mids");
        
            // Check if there is any data in the table
            if (aMIDs.length === 0) {
                // Show a message to the user
                sap.m.MessageToast.show("No data available to export.");
            } else {
                // Proceed with the export
                var oSettings = {
                    workbook: { columns: [{ label: 'MIDs', property: 'mids' }] },
                    dataSource: aMIDs,
                    fileName: "MIDs.xlsx"
                };
        
                var oSheet = new Spreadsheet(oSettings);
                oSheet.build().finally(function() {
                    oSheet.destroy();
                });
            }
        }        
    });
});
