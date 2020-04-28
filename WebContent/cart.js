let updateCount = "";
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

    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
        //cartEvent.preventDefault();
        //TODO: load cart like movielist here

        // Populate the movielist table
        // Find the empty table body by id "movielist_table_body"
    let cartTableBodyElement = jQuery("#cart_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {
        //console.log(resultData.length);
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href=' + "single-movie.html?id=" + resultData[i]['id'] + ">"
            + resultData[i]["title"] +     // display title for the link text
            '</a>' +
            "</th>";

        rowHTML += "<th>" + "<form id='quantity_form' name='quantity_form' action='cart.html' method='GET'>" +
                    "<input type='text' id='" + resultData[i]['id'] + "' name='" + resultData[i]['id'] +  "'"
                    + " value='" + resultData[i]["Quantity"] + "'" + "/>" + "<input type='submit' value='Update' onclick='clickAlert()'/>" +
                    "</form>" +
                    "</th>";
        rowHTML += "</tr>";

        // quantityMap.set(resultData[i]['id'], resultData[i]['Quantity']);

        // Append the row created to the table body, which will refresh the page
        cartTableBodyElement.append(rowHTML);
    }
    let paymentButton = jQuery("#payment_button");
    paymentButton.append("<button onclick=\"window.location.href='payment.html" +
                        "'\">Go to Payment</button>");
}

function clickAlert() {
    alert("Quantity updated!");
}

let url_string = "api/cart";
let key_id = "";
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const keys = urlParams.keys();
for(const key of keys) {key_id = key;}

updateCount = getParameterByName(key_id);
if(updateCount){
    url_string += "?" + key_id + "=" + updateCount;
}

console.log(url_string);
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: url_string,
    success: (resultData) => handleSessionData(resultData)
});


