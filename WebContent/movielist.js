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

function handleListResult(resultData, condition, page, limit, sort) {
    console.log("handleListResult: populating movielist table from resultData");

    let limit_list_body = jQuery("#limit_list");
    limit_list_body.append("<form action=\"movielist.html\" method=\"GET\">\n" +
        "    <p align=\"center\"><strong>Results per page: </strong>\n" +
        "        <input type=\"hidden\" name=\"genre\" value=\"" + condition + "\"/>\n" +
        "        <input type=\"hidden\" name=\"page\" value=\"" + page + "\"/>\n" +
        "        <input type=\"hidden\" name=\"sort\" value=\"" + sort + "\"/>\n" +
        "        <select name=\"limit\">\n" +
        "            <option value=\"10\" selected>10</option>\n" +
        "            <option value=\"25\">25</option>\n" +
        "            <option value=\"50\">50</option>\n" +
        "            <option value=\"100\">100</option>\n" +
        "        </select>\n" +
        "        <input type=\"submit\"/></p>\n" +
        "</form>" +
        "<script type=\"text/javascript\">" +
        "document.getElementById('limit').value = \"<?php echo $_GET['name'];?>\";" +
        "</script>");

    let sort_list_body = jQuery("#sort_list");
    sort_list_body.append("<form action=\"movielist.html\" method=\"GET\">\n" +
        "    <p align=\"center\"><strong>Sort by: </strong>\n" +
        "        <input type=\"hidden\" name=\"genre\" value=\"" + condition + "\"/>\n" +
        "        <input type=\"hidden\" name=\"page\" value=\"" + page + "\"/>\n" +
        "        <input type=\"hidden\" name=\"limit\" value=\"" + limit + "\"/>\n" +
        "        <select name=\"sort\">\n" +
        "            <option value=\"title_then_rating_ASC\" selected>title (then rating) ascending</option>\n" +
        "            <option value=\"title_then_rating_DESC\">rating (then title) descending</option>\n" +
        "            <option value=\"rating_then_title_ASC\">rating (then title) ascending </option>\n" +
        "            <option value=\"rating_then_title_DESC\">rating (then title) descending</option>\n" +
        "        </select>\n" +
        "        <input type=\"submit\"/></p>\n" +
        "</form>" +
        "<script type=\"text/javascript\">" +
        "document.getElementById('sort').value = \"<?php echo $_GET['name'];?>\";" +
        "</script>");

    // Populate the movielist table
    // Find the empty table body by id "movielist_table_body"
    let movielistTableBodyElement = jQuery("#movielist_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(limit, resultData.length); i++) {

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
        movielistTableBodyElement.append(rowHTML);
    }

    // makes the page list
    let pageBody = jQuery("#page_list_body");
    let pageText = "";

    // 10 in 'parseInt(pageNumber, 10)' is not limit number. It's for parseInt to convert to Integer properly
    if (parseInt(page) > 1) {
        pageText += "<span>" +
            '<a href=' + "movielist.html?genre=" + condition + "&page=" + (parseInt(page, 10) - 1).toString() +
            "&limit=" + limit + "&sort=" + sort + ">" +
            "<<< Previous       </a>" +
            "</span>";
    }
    // 10 in 'parseInt(pageNumber, 10)' is not limit number. It's for parseInt to convert to Integer properly
    if (resultData.length == limit) {
        pageText += "<span>" +
            '<a href=' + "movielist.html?genre=" + condition + "&page=" + (parseInt(page, 10) + 1).toString() +
            "&limit=" + limit + "&sort=" + sort + ">" +
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
let limit = getParameterByName('limit');
let sort = getParameterByName('sort');
// Makes the HTTP GET request and registers on success callback function handleListResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movielist?genre=" + genreName + "&page=" + pageNumber + "&limit=" + limit + '&sort=' + sort, // Setting request url, which is mapped by MovieListServlet in Stars.java
    success: (resultData) => handleListResult(resultData, genreName, pageNumber, limit, sort), // Setting callback function to handle data returned successfully by the MovieListServlet
});