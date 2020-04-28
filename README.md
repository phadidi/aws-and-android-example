# cs122b-spring20-team-13 Project 2
This is a submission for Project 2 with Java Servlet, Javascript, and HTML pages for a Fabflix website where a customer can log in, search from a list of movies by title, stars, genres, and other criteria, add a selection of movies to a shopping cart, and checkout a cart with a credit card payment. There is also a createtable.sql file included for providing the format of the 'moviedb' database so that it can be populated for the webpages' use.

- Demo Video: https://youtu.be/T7ij7OAysGY

## Getting Started
Make sure to initialize a MySQL database with the included createtable.sql and populate it accordingly. This requires a list of movies, stars, genres, customers, customer ratings, creditcards, and the necessary relationships between them.

### Installing and Starting Project 2
For AWS, Project 1 has been tested to run on Ubuntu 16.04.6 LTS using an Apache Tomcat 8.5.53 server instance with Maven capabilities. For the UCI Grader, the link http://ec2-52-55-11-244.compute-1.amazonaws.com:8080/cs122b-spring20-team-13/ should be open based on the security group IP requirements. The finalized Project 2 build should be found in the master branch with this file.

If you want to run from your own AWS instance machine, make sure to clone the project with the following command:

``` git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-13.git```

Create a new user for MySQL on your AWS machine with the following credentials:

```username: mytestuser```

```password: mypassword```

Make sure this user has all access and run the createtable.sql and movie-data.sql scripts to create and populate your database.

Then to prepare your war file, run the following commands:

```mvn package```
```cp ./target/*.war /home/ubuntu/tomcat/webapps```

Make sure you have Tomcat running. Then, go to your Tomcat manager page. You should see a newly deployed war file named "cs122b-spring20-team-13." Click on the name to open the Fabflix Movie List page.

## Project 2 Structure
### Movie List Servlet
The class file MovieListServlet.java displays 10, 25, 50, or 100 movies per page based on the given search query. For each movie it shows the title, director, year, up to three genres, up to three stars ordered by descending order of movies appeared in, and rating score. Each movie has a hyperlink to a Single Movie Servlet page based on that movie's id plus a nearby button to add that movie to a customer's shopping cart, and each star has a hyperlink to a Single Movie Servlet page based on that star's name.
### Single Movie Servlet
Based on a given movie id, the class file SingleMovieServlet.java loads the the title, year, director, all featured stars, all associated genres, and rating score of the corresponding movie. Every star name is a hyperlink to a single star page, 'Add to Cart' adds a movie to a customer's shopping cart, and 'Return to Movie List' has a hyperlink back to the Movie List Servlet page.
### Single Star Servlet
Based on a given star id, the class file SingleStarServlet.java loads the the name, birth year (if available), director, and all featured movies. Every movie name is a hyperlink to a single movie page and 'Return to Movie List' has a hyperlink back to the Movie List Servlet page.
### Login Servlet
If the user has not logged in yet, the LoginFilter redirects to the login page until a valid email address and password are given. Once confirmed, the servlet then takes the user to the Main Page Servlet homepage.
### Main Page Servlet
The homepage after a user has logged in successfully, displaying the options to display movies by genre, the first letter in the title, or an advanced query based on title, release year, director, and/or stars.
### Cart Servlet
The Cart page loads and displays the cart attribute of the currently logged in Customer object with the option to checkout the cart using a payment method.
### Payment Servlet
The Payment page takes a Customer's first and last names with a credit card number to check out the cart.
### Confirmation Servlet
The Confirmation page is redirected to after Payment Servlet to confirm a purchase to a customer before deciding to complete it or not.
### pom.xml
The pom.xml file contains a list of artifacts to use, including mysql-connector-java and javax.servlet, along with directions to handle the Maven war packaging and the necessary maven-compiler-plugin and maven-war-plugin.
### Our Substring Design
For searching, we simnply used LIKE "%some pattern provided by user%" to match with any value that has that pattern somewhere in the string value. For example, "%vid m%" would match the field "David Mirkin." Similarly, for matching beginning letters like 'A', we used LIKE "A%". This pattern will match anything with A as the first letter in the string value.

## Contributions
Parsa Hadidi:
- Built basic class structure using course examples, worked on queries for data retrieval, added the ability to add to cart from movielist, created basis for Customer object class, and prepared README.md.

Duy Nguyen: 
- Perfected the majority of MySQL queries, web servlet classes, and HTML/Javascript format.

Both worked on debugging and finalizing every java class and HTML/Javascript page.
