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
let add_to_cart = $("#add_to_cart");

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
    if(searchTitle){
        let title_split = searchTitle.split(" ");
        searchTitle = title_split.join('+');
        conditionURL += "&search_title=" + searchTitle;
    }
    if(searchYear){
        conditionURL += "&search_year=" + searchYear;
    }
    if(searchDirector){
        let director_split = searchDirector.split(" ");
        searchDirector = director_split.join('+');
        conditionURL += "&search_director=" + searchDirector;
    }
    if(searchStar){
        let star_split = searchStar.split(" ");
        searchStar = star_split.join('+');
        conditionURL += "&search_star=" + searchStar;
    }

    if(condition){
        conditionURL += "&genre=" + condition;
    }
    conditionURL += "&limit=" + limit + "&page=" + page + "&sort=" + sort;

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
        movieInfo.append('<a href=' + "single-star.html?id=" + starEntrySplit[1] + conditionURL + ">"
            + starEntrySplit[0] +
            '</a>'); // hyperlink star name [1] to star id [0]
        if (i < starsSplit.length - 1) // add commas before the last entry
            movieInfo.append(", ");
    }
    movieInfo.append("</p>" + "<p>Rating: " + resultData[0]["rating"] + "</p>");
    movieInfo.append("<p>Price: "+ resultData[0]["price"] + "</p>")

    let returnLink = jQuery("#return_link");
    //TODO: check if this returns to movielist correctly, and fix it if not so!
    returnLink.append("<p align=\"center\"><a href=\"movielist.html?" + conditionURL + "\"><strong>Return to Movie List</strong></a></p>");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

function redirectToCart(resultDataString) {
    console.log("handle cart redirect");
    window.location.replace("cart.html");
}

function submitAddToCart(formSubmitEvent) {
    console.log("add '" + movieId + "' to cart");
    //alert("you have added this to cart");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/single-movie", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: add_to_cart.serialize(),
            success: alert("Successfully added to Cart")
        }
    );
    // test if redirect works here
    // window.location.replace("cart.html");
}

let genreName = getParameterByName('genre');
let pageNumber = getParameterByName('page');
let limit = getParameterByName('limit');
let sort = getParameterByName('sort');
let searchTitle = getParameterByName('search_title');
let searchYear = getParameterByName('search_year');
let searchDirector = getParameterByName('search_director');
let searchStar = getParameterByName('search_star');
let url_string = "api/single-movie?id=" + movieId + "&page=" + pageNumber + "&limit=" + limit + '&sort=' + sort; // Setting request url;
if(genreName) {
    url_string += "&genre=" + genreName; //appending genre query if genre is defined
}
if(searchTitle){
    url_string += "&search_title=" + searchTitle; //appending search title query if genre is defined
}
if(searchYear){
    url_string += "&search_year=" + searchYear; //appending search title query if genre is defined
}
if(searchDirector){
    url_string += "&search_director=" + searchDirector; //appending search title query if genre is defined
}
if(searchStar){
    url_string += "&search_star=" + searchStar; //appending search title query if genre is defined
}

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData, genreName, pageNumber, limit, sort, searchTitle, searchYear, searchDirector, searchStar) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

// TODO: determine if jQuery for POST is necessary, and how it would work
add_to_cart.submit(submitAddToCart)