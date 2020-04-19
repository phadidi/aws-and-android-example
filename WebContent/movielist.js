/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

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

function handleListResult(resultData, condition, page, offsetNo) {
    console.log("handleListResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#star_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(10, resultData.length); i++) {

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
        for (let j = 0; j <  genreCount; j++) {
            rowHTML += genresSplit[j];
            if (j < genreCount - 1) // add commas before the last entry
                rowHTML += ", ";
        }
        rowHTML += "</th>";

        rowHTML += "<th>";
        let starsSplit = resultData[i]["starNamesAndIds"].split(',');
        let starCount = Math.min(3, starsSplit.length);
        for (let j = 0; j <  starCount; j++) {
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
        starTableBodyElement.append(rowHTML);
    }

    // makes the page list
    let pageBody = jQuery("#page_list_body");
    let pageText = "";

    if(parseInt(pageNumber, offsetNo) > 1){
        pageText += "<span>" +
            '<a href=' + "movielist.html?genre=" + genreName + "&page=" + (parseInt(pageNumber, offsetNo) - 1).toString() + ">" +
            "<<< Previous       </a>" +
            "</span>";
    }
    if(resultData.length == offsetNo){
        pageText += "<span>" +
            '<a href=' + "movielist.html?genre=" + genreName + "&page=" + (parseInt(pageNumber, offsetNo) + 1).toString() + ">" +
            "Next >>></a>" +
            "</span>";
        pageBody.append(pageText);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
let genreName = getParameterByName('genre');
let pageNumber = getParameterByName('page');
// Makes the HTTP GET request and registers on success callback function handleStarResult
let offsetNumber = getParameterByName('offset');
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movielist?genre=" + genreName + "&page=" + pageNumber + "&offset=" + offsetNumber.toString(), // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleListResult(resultData, genreName, pageNumber, offsetNumber), // Setting callback function to handle data returned successfully by the StarsServlet
});