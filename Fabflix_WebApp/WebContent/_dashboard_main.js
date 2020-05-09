let dashboard_newMovie_form = $("#dashboard_newMovie_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleMovieResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle add_movie response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the employee to _dashboard_main.html
    if (resultDataJson["status"] === "success") {
        // TODO: change this to the appropriate employee page after adding a movie to the database
        alert("You added a movie to the database");
        window.location.replace("_dashboard_main.html");
    } else {
        // If add_movie fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#newMovie_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitMovieForm(formSubmitEvent) {
    console.log("submit add_movie form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/_dashboard_main", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: dashboard_newMovie_form.serialize(),
            success: handleMovieResult
        }
    );
}

// Bind the submit action of the form to a handler function
dashboard_newMovie_form.submit(submitMovieForm);