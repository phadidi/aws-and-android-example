function handleGenreResult(resultData) {
    console.log("handleGenreResult: populating genre table from resultData");

    // Populate the genre table
    // Find the empty table body by id "genre_body"
    let genreBody = jQuery("#genre_body");

    let rowHTML = "";

    for (let i = 0; i < resultData.length; i++) {

        rowHTML += "<span>| " +
            '<a href=' + "movielist.html?genre=" + resultData[i]['genre'] + "&page=1&offset=10>"
            + resultData[i]['genre'] + " </a>" +
            "</span>";
    }
    rowHTML += "<span>|</span>";
    genreBody.append(rowHTML);
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/main", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleGenreResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});