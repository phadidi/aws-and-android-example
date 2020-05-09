let payment_form = $("#payment_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handlePaymentResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);


    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        alert(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitPaymentForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: handlePaymentResult
        }
    );
}

// Bind the submit action of the form to a handler function
payment_form.submit(submitPaymentForm);


function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

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
    let cartTableBodyElement = jQuery("#order_body");

    for (let i = 0; i < resultData.length; i++) {
        //console.log(resultData.length);
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + (i + 1).toString() + "</th>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href=' + "single-movie.html?id=" + resultData[i]['id'] + ">"
            + resultData[i]["title"] +     // display title for the link text
            '</a>' +
            "</th>";

        rowHTML += "<th>" + resultData[i]["Quantity"] + "</th>";
        rowHTML += "<th>$" + (parseInt(resultData[i]["Quantity"], 10) * 10.99).toString() + "</th>";
        rowHTML += "</tr>";
        total += parseInt(resultData[i]["Quantity"], 10);

        // quantityMap.set(resultData[i]['id'], resultData[i]['Quantity']);

        // Append the row created to the table body, which will refresh the page
        cartTableBodyElement.append(rowHTML);
    }
    total = total * 10.99;

    let totalBody = jQuery("#total_body");
    totalBody.append("<strong>" + "Total = $" + total.toString() + "</strong>");
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/payment",
    success: (resultData) => handleSessionData(resultData)
});


