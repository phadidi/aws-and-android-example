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
function handleMovieListResult(resultData) {
    console.log("handleMovieListResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieListTableBodyElement = jQuery("select m.id, m.title, m.year, m.director, \n" +
        "(group_concat(distinct g.name separator ',')) as genres,\n" +
        "(group_concat(distinct s.name  separator ','))  as stars, \t\n" +
        "r.rating \n" +
        "from movies m, genres g,  genres_in_movies gim, stars s, stars_in_movies sim, ratings r\n" +
        "where m.id=gim.movieId and \n" +
        "gim.genreId = g.Id and\n" +
        "m.id=sim.movieId and\n" +
        "sim.starId=s.id and \n" +
        "m.id = r.movieId\n" +
        "group by m.title, m.year, m.director, r.rating\n" +
        "order by r.rating DESC\n" +
        "limit 20;");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="movie-list.html?id=' + resultData[i]['id'] + '">'
            + resultData[i]["title"] +     // display title for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["genres"] + "</th>";
        rowHTML += "<th>" + resultData[i]["stars"] + "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieListTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleMovieListResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/stars", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});