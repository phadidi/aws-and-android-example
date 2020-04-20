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

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "movie_info"
    let movieInfo = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfo.append("<p>Movie Title: " + resultData[0]["title"] + "</p>" +
        "<p>Release Year: " + resultData[0]["year"] + "</p>" +
        "<p>Director: " + resultData[0]["director"] + "</p>" +
        "<p>Genres: " + resultData[0]["genres"] + "</p>");
    // resultData here is a single movie, hence only a single resultData entry exists
    console.log("handleResult: populating movie table from resultData");


    let starsSplit = resultData[0]["stars"].split(',');
    movieInfo.append("<p>Stars: ");
    for (let i = 0; i < starsSplit.length; i++) {
        // TODO: tie star ID to star Names using SQL query for future html queries
        let starEntrySplit = starsSplit[i].split('_');
        movieInfo.append('<a href=' + "single-star.html?id=" + starEntrySplit[1] + ">"
            + starEntrySplit[0] +
            '</a>'); // hyperlink star name [1] to star id [0]
        if (i < starsSplit.length - 1) // add commas before the last entry
            movieInfo.append(", ");
    }
    movieInfo.append("</p>" + "<p>Rating: " + resultData[0]["rating"] + "</p>");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');
// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});