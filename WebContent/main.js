function handleGenreResult(resultData) {
    console.log("handleGenreResult: populating genre table from resultData");

    // Populate the genre table
    // Find the empty table body by id "genre_body"
    let genreBody = jQuery("#genre_body");

    let rowHTML = "";

    for (let i = 0; i < resultData.length; i++) {

        rowHTML += "<span>| " +
            '<a href=' + "movielist.html?page=1&limit=10&sort=title_then_rating_ASC" + "&genre=" + resultData[i]['genre'] + ">"
            + resultData[i]['genre'] + " </a>" +
            "</span>";
    }
    rowHTML += "<span>|</span>";
    genreBody.append(rowHTML);

    let searchBody = jQuery("#search_body");

    searchBody.append("<form action=\"movielist.html\" method=\"GET\">\n" +
        "        <input type=\"hidden\" name=\"page\" value=\"1\"/>\n" +
        "        <input type=\"hidden\" name=\"sort\" value=\"title_then_rating_ASC\"/>\n" +
        "        <input type=\"hidden\" name=\"limit\" value=\"10\"/>\n" +
        "<label>" +
        "<input name=\"search_title\" placeholder=\"Enter a title\" type=\"text\">" +
        "</label>" + "<br>" +
        "<label>" +
        "<input name=\"search_year\" placeholder=\"Enter a release year\" type=\"text\">" +
        "</label>" + "<br>" +
        "<label>" +
        "<input name=\"search_director\" placeholder=\"Enter a director's name\" type=\"text\">" +
        "</label>" + "<br>" +
        "<label>" +
        "<input name=\"search_star\" placeholder=\"Enter a star's name\" type=\"text\">" +
        "</label>" + "<br>" +
        "        <p align='center'><input type=\"submit\"/></p>\n" +
        "</form>");
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