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

function handleListResult(resultData, condition, page, limit, sort, searchTitle, searchYear, searchDirector, searchStar, searchLetter) {
    console.log("handleListResult: populating movielist table from resultData");

    let conditionString = "";
    let conditionURL = "";

    if (searchLetter) {
        conditionString += "<input type=\"hidden\" name=\"letter\" value=\"" + searchLetter + "\"/>\n";
        conditionURL += "&search_title=" + searchLetter;
    }
    if (searchTitle) {
        conditionString += "<input type=\"hidden\" name=\"search_title\" value=\"" + searchTitle + "\"/>\n";
        let title_split = searchTitle.split(" ");
        searchTitle = title_split.join('+');
        conditionURL += "&search_title=" + searchTitle;
    }
    if (searchYear) {
        conditionString += "<input type=\"hidden\" name=\"search_year\" value=\"" + searchYear + "\"/>\n";
        conditionURL += "&search_year=" + searchYear;
    }
    if (searchDirector) {
        conditionString += "<input type=\"hidden\" name=\"search_director\" value=\"" + searchDirector + "\"/>\n";
        let director_split = searchDirector.split(" ");
        searchDirector = director_split.join('+');
        conditionURL += "&search_director=" + searchDirector;
    }
    if (searchStar) {
        conditionString += "<input type=\"hidden\" name=\"search_star\" value=\"" + searchStar + "\"/>\n";
        let star_split = searchStar.split(" ");
        searchStar = star_split.join('+');
        conditionURL += "&search_star=" + searchStar;
    }

    if (condition) {
        conditionString += "<input type=\"hidden\" name=\"genre\" value=\"" + condition + "\"/>\n";
        conditionURL += "&genre=" + condition;
    }
    let conditionPage = conditionURL;
    conditionURL += "&limit=" + limit + "&page=" + page + "&sort=" + sort;

    let limit_list_body = jQuery("#limit_list");
    limit_list_body.append("<form id=\"limit_form\" action=\"movielist.html\" method=\"GET\">\n" +
        "    <p align=\"center\"><strong>Results per page: </strong>\n" +
        "        <input type=\"hidden\" name=\"page\" value=\"" + page + "\"/>\n" +
        "        <input type=\"hidden\" name=\"sort\" value=\"" + sort + "\"/>\n" +
        "        <select name=\"limit\">\n" +
        "            <option value=\"10\" selected>10</option>\n" +
        "            <option value=\"25\">25</option>\n" +
        "            <option value=\"50\">50</option>\n" +
        "            <option value=\"100\">100</option>\n" +
        "        </select>\n" +
        conditionString +
        "        <input type=\"submit\"/></p>\n" +
        "</form>" +
        "<script type=\"text/javascript\">" +
        "document.getElementById('limit').value = \"<?php echo $_GET['name'];?>\";" +
        "</script>");

    let sort_list_body = jQuery("#sort_list");
    sort_list_body.append("<form id=\"sort_form\" action=\"movielist.html\" method=\"GET\">\n" +
        "    <p align=\"center\"><strong>Sort by: </strong>\n" +
        "        <input type=\"hidden\" name=\"page\" value=\"" + page + "\"/>\n" +
        "        <input type=\"hidden\" name=\"limit\" value=\"" + limit + "\"/>\n" +
        "        <select name=\"sort\">\n" +
        "            <option value=\"title_asc_rating_asc\" selected>title (asc) rating (asc)</option>\n" +
        "            <option value=\"title_desc_rating_desc\">title (desc) rating (desc)</option>\n" +
        "            <option value=\"rating_asc_title_asc\">rating (asc) title (asc) </option>\n" +
        "            <option value=\"rating_desc_title_DESC\">rating (desc) title (desc)</option>\n" +
        "            <option value=\"rating_asc_title_desc\">rating (asc) title (desc)</option>\n" +
        "            <option value=\"rating_desc_title_asc\">rating (desc) title (asc)</option>\n" +
        "            <option value=\"title_asc_rating_desc\">title (asc) rating (desc)</option>\n" +
        "            <option value=\"title_desc_rating_asc\">title (desc) rating (asc)</option>\n" +
        "        </select>\n" +
        conditionString +
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
        // TODO: check if page and limit are passed in as String or int, then parse accordingly
        rowHTML += "<th>" + (i * Integer.parseInt(page) * Integer.parseInt(limit) + 1).toString() + "</th>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href=' + "single-movie.html?id=" + resultData[i]['id'] + conditionURL + ">"
            + resultData[i]["title"] +     // display title for the link text
            '</a>' + "<br>" +
            " <button id=\"add_to_cart\" action=\"cart.html\" method=\"post\" " +
            "onclick=\"submitAddToCart('" + resultData[i]['id'] + "')\">" +
            "Add to Cart</button>" +
            "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        //rowHTML += "<th>" + resultData[i]["genres"] + "</th>";
        rowHTML += "<th>";
        let genresSplit = resultData[i]["genres"].split(',');
        let genreCount = Math.min(3, genresSplit.length);
        for (let j = 0; j < genreCount; j++) {
            rowHTML += "<a href='movielist.html?page=1&limit=10&sort=title_asc_rating_asc&genre=" + genresSplit[j]
                + "'>" + genresSplit[j] + "</a>";
            if (j < genreCount - 1) // add commas before the last entry
                rowHTML += ", ";
        }
        rowHTML += "</th>";

        rowHTML += "<th>";
        let starsSplit = resultData[i]["starNamesAndIds"].split(',');
        let starCount = Math.min(3, starsSplit.length);
        for (let j = 0; j < starCount; j++) {
            let starEntrySplit = starsSplit[j].split('_');
            rowHTML += '<a href=' + "single-star.html?id=" + starEntrySplit[1] + conditionURL + ">"
                + starEntrySplit[0] +
                '</a>'; // hyperlink star name [1] to star id [0]
            if (j < starCount - 1) // add commas before the last entry
                rowHTML += ", ";
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "<th>$" + resultData[i]["price"] + "</th>";
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
            '<a href=' + "movielist.html?" + conditionPage + "&page=" + (parseInt(page, 10) - 1).toString() +
            "&limit=" + limit + "&sort=" + sort + ">" +
            "<<< Previous       </a>" +
            "</span>";
    }
    // 10 in 'parseInt(pageNumber, 10)' is not limit number. It's for parseInt to convert to Integer properly
    if (resultData.length == limit) {
        pageText += "<span>" +
            '<a href=' + "movielist.html?" + conditionPage + "&page=" + (parseInt(page, 10) + 1).toString() +
            "&limit=" + limit + "&sort=" + sort + ">" +
            "Next >>></a>" +
            "</span>";
        pageBody.append(pageText);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
let searchLetter = getParameterByName('letter');
let genreName = getParameterByName('genre');
let pageNumber = getParameterByName('page');
let limit = getParameterByName('limit');
let sort = getParameterByName('sort');
let searchTitle = getParameterByName('search_title');
let searchYear = getParameterByName('search_year');
let searchDirector = getParameterByName('search_director');
let searchStar = getParameterByName('search_star');
let url_string = "api/movielist?page=" + pageNumber + "&limit=" + limit + '&sort=' + sort; // Setting request url;
if (searchLetter) {
    url_string += "&letter=" + searchLetter; //appending genre query if genre is defined
}
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

function redirectToCart(resultDataString) {
    console.log("handle cart redirect");
    window.location.replace("cart.html");
}

function submitAddToCart(id) {
    console.log("add '" + id + "' to cart");
    //alert("you have added this to cart");
    //add_to_cart.movieId = id;
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    //formSubmitEvent.preventDefault();

    $.ajax(
        "api/movielist", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: id,
            success: alert("Successfully added to Cart")
        }
    );
    // test if redirect works here
    // window.location.replace("cart.html");
}


// Makes the HTTP GET request and registers on success callback function handleListResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: url_string,
    success: (resultData) => handleListResult(resultData, genreName, pageNumber, limit, sort, searchTitle, searchYear, searchDirector, searchStar, searchLetter) // Setting callback function to handle data returned successfully by the MovieListServlet
});

//add_to_cart.submit(submitAddToCart)