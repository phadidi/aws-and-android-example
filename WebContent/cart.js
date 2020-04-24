let cart = $("#cart");

/**
 * Handle the data returned by CartServlet
 * @param resultDataString jsonObject, consists of session info
 */
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
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        //rowHTML += "<th>" + resultData[i]["genres"] + "</th>";
        rowHTML += "<th>";
        let genresSplit = resultData[i]["genres"].split(',');
        let genreCount = Math.min(3, genresSplit.length);
        for (let j = 0; j < genreCount; j++) {
            rowHTML += genresSplit[j];
            if (j < genreCount - 1) // add commas before the last entry
                rowHTML += ", ";
        }
        rowHTML += "</th>";

        rowHTML += "<th>";
        let starsSplit = resultData[i]["starNamesAndIds"].split(',');
        let starCount = Math.min(3, starsSplit.length);
        for (let j = 0; j < starCount; j++) {
            // TODO: tie star ID to star Names using SQL query for future html queries
            let starEntrySplit = starsSplit[j].split('_');
            rowHTML += '<a href=' + "single-star.html?id=" + starEntrySplit[1] + ">"
                + starEntrySplit[0] +
                '</a>'; // hyperlink star name [1] to star id [0]
            if (j < starCount - 1) // add commas before the last entry
                rowHTML += ", ";
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        cartTableBodyElement.append(rowHTML);
    }
}

/**
 * Handle the items in item list
 * @param resultDataString jsonObject, needs to be parsed to html
 */

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/cart",
    success: (resultData) => handleSessionData(resultData)
});
