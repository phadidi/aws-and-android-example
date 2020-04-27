

function handleSessionData(resultData) {
    //console.log("handle session response");
    //console.log(resultDataJson);
    //console.log(resultDataJson["sessionID"]);

    // show the session information
    //$("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    //$("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);

    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */

    let total = 0;
    let confirmOrderBody = jQuery("#confirm_order_body");

    for (let i = 0; i < resultData.length; i++) {
        //console.log(resultData.length);
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["idsales"] + "</th>";
        rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML +=

        rowHTML += "<th>" + resultData[i]["quantity"] + "</th>";
        rowHTML += "<th>$" + (parseInt(resultData[i]["quantity"], 10) * 10.99).toString() + "</th>";
        rowHTML += "<th>" + resultData[i]["saleDate"] + "</th>";
        rowHTML += "</tr>";
        total += parseInt(resultData[i]["quantity"], 10);


        // Append the row created to the table body, which will refresh the page
        confirmOrderBody.append(rowHTML);
    }
    total = total * 10.99;

    let totalBody = jQuery("#total_body");
    totalBody.append("<strong>" + "Total = $" + total.toString() + "</strong>" );
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/confirmation",
    success: (resultData) => handleSessionData(resultData)
});


