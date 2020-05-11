let dashboard_newStar_form = $("#dashboard_newStar_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleStarResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle add_movie response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the employee to _dashboard_main.html
    if (resultDataJson["status"] === "success") {
        // TODO: change this to the appropriate employee page after adding a star to the database
        alert("You added a star to the database");
        window.location.replace("_dashboard_star.html");
    } else {
        // If add_movie fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#newStar_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitStarForm(formSubmitEvent) {
    console.log("submit add star form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/_dashboard_star", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: dashboard_newStar_form.serialize(),
            success: handleStarResult
        }
    );
}

// Bind the submit action of the form to a handler function
dashboard_newStar_form.submit(submitStarForm);