/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData, condition, page, limit, sort, searchTitle, searchYear, searchDirector, searchStar) {

    console.log("handleResult: populating star info from resultData");

    let conditionURL = "";
    if (searchTitle) {
        let title_split = searchTitle.split(" ");
        searchTitle = title_split.join('+');
        conditionURL += "&search_title=" + searchTitle;
    }
    if (searchYear) {
        conditionURL += "&search_year=" + searchYear;
    }
    if (searchDirector) {
        let director_split = searchDirector.split(" ");
        searchDirector = director_split.join('+');
        conditionURL += "&search_director=" + searchDirector;
    }
    if (searchStar) {
        let star_split = searchStar.split(" ");
        searchStar = star_split.join('+');
        conditionURL += "&search_star=" + searchStar;
    }

    if (condition) {
        conditionURL += "&genre=" + condition;
    }
    conditionURL += "&limit=" + limit + "&page=" + page + "&sort=" + sort;

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
        "<p>Date Of Birth: " + resultData[0]["star_dob"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + '<a href=' + "single-movie.html?id=" + resultData[i]['movie_id'] + conditionURL + ">"
            + resultData[i]["movie_title"] + '</a>' + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }

    let returnLink = jQuery("#return_link");
    //TODO: check if this returns to movielist correctly, and fix it if not so!
    returnLink.append("<p align=\"center\"><a href=\"movielist.html?" + conditionURL + "\"><strong>Return to Movie List</strong></a></p>");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');
let genreName = getParameterByName('genre');
let pageNumber = getParameterByName('page');
let limit = getParameterByName('limit');
let sort = getParameterByName('sort');
let searchTitle = getParameterByName('search_title');
let searchYear = getParameterByName('search_year');
let searchDirector = getParameterByName('search_director');
let searchStar = getParameterByName('search_star');
let url_string = "api/single-star?id=" + starId + "&page=" + pageNumber + "&limit=" + limit + '&sort=' + sort; // Setting request url;
if (genreName) {
    url_string += "&genre=" + genreName; //appending genre query if genre is defined
}
if (searchTitle) {
    url_string += "&search_title=" + searchTitle; //appending search title query if genre is defined
}
if (searchYear) {
    url_string += "&search_year=" + searchYear; //appending search title query if genre is defined
}
if (searchDirector) {
    url_string += "&search_director=" + searchDirector; //appending search title query if genre is defined
}
if (searchStar) {
    url_string += "&search_star=" + searchStar; //appending search title query if genre is defined
}

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: url_string, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData, genreName, pageNumber, limit, sort, searchTitle, searchYear, searchDirector, searchStar) // Setting callback function to handle data returned successfully by the SingleStarServlet
});