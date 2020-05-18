function handleGenreResult(resultData) {
    console.log("handleGenreResult: populating genre table from resultData");

    let genreBody = jQuery("#genre_body");

    let rowHTML = "";

    for (let i = 0; i < resultData.length; i++) {

        rowHTML += "<span>| " +
            '<a href=' + "movielist.html?page=1&limit=10&sort=title_asc_rating_asc" + "&genre=" + resultData[i]['genre'] + ">"
            + resultData[i]['genre'] + " </a>" +
            "</span>";
    }
    rowHTML += "<span>|</span>";
    genreBody.append(rowHTML);

    let alphabet = "1 2 3 4 5 6 7 8 9 0 A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
    let alphabet_list = alphabet.split(" ");
    let letter_string = "";
    for (let i = 0; i < alphabet_list.length; i++) {
        letter_string += "<span>| <a href='movielist.html?page=1&limit=10&sort=title_asc_rating_asc&letter=" + alphabet_list[i] + "'>"
            + alphabet_list[i] + " </a>" + "</span>";
    }

    letter_string += "<span>| <a href='movielist.html?page=1&limit=10&sort=title_asc_rating_asc&letter=non_alphanumeric'>"
        + "Non-Alphanumeric" + " </a>" + "</span>";

    letter_string += "<span>|</span>";
    let letterBody = jQuery("#letter_body");

    letterBody.append(letter_string);

    let searchBody = jQuery("#search_body");

    searchBody.append("<form action=\"movielist.html\" method=\"GET\">\n" +
        "        <input type=\"hidden\" name=\"page\" value=\"1\"/>\n" +
        "        <input type=\"hidden\" name=\"sort\" value=\"title_asc_rating_asc\"/>\n" +
        "        <input type=\"hidden\" name=\"limit\" value=\"10\"/>\n" +
        "<label>" +
        "<input name=\"search_title\" placeholder=\"Enter a title\" type=\"text\" autocomplete=\"on\">" +
        "</label>" + "<br>" +
        "<label>" +
        "<input name=\"search_year\" placeholder=\"Enter a release year\" type=\"text\" autocomplete=\"on\">" +
        "</label>" + "<br>" +
        "<label>" +
        "<input name=\"search_director\" placeholder=\"Enter a director's name\" type=\"text\" autocomplete=\"on\">" +
        "</label>" + "<br>" +
        "<label>" +
        "<input name=\"search_star\" placeholder=\"Enter a star's name\" type=\"text\" autocomplete=\"on\">" +
        "</label>" + "<br>" +
        "        <p align='center'><input type=\"submit\"/></p>\n" +
        "</form>");
}

$('#search_body').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});

function handleLookup(query, doneCallback) {
    // TODO: merge the autocomplete example code with our existing code
    console.log("autocomplete initiated")
    console.log("sending AJAX request to backend Java Servlet")

    // TODO: if you want to check past query results first, you can do it here

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/main",
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("lookup ajax error")
            console.log(errorData)
        }
    })
}

function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log(jsonData)

    // TODO: if you want to cache the result into a global variable you can do it here

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}

function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    console.log("you select " + suggestion["title"] + " with ID " + suggestion["id"])
}

/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
}

// bind pressing enter key to a handler function
$('#search_body').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#search_body').val())
    }
})

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