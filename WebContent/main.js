
function handleStarResult(resultData) {
    console.log("handleStarResult: populating genre table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let genreBody = jQuery("#genre_body");

    let rowHTML = "";

    for (let i = 0; i < resultData.length; i++) {

        rowHTML += "<span>| " +
            '<a href=' + "movielist.html?genre=" + resultData[i]['genre'] + ">"
            + resultData[i]['genre'] + " </a>" +
            "</span>";
    }
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
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});