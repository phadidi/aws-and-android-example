# cs122b-spring20-team-13 Project 1
This is a submission for Project 1 with Java Servlet Pages for a list of Top 20 Movies using a given database, plus descriptions of each movie and star when the apropriate hyperlink is clicked on. There is also a createtable.sql file included for providing the format of the 'moviedb' database so that it can be populated for the webpages' use.


- Demo Video: https://www.youtube.com/watch?v=8MUr2trU-Zw

## Getting Started
Make sure to initialize a MySQL database with the included createtable.sql and populate it accordingly. This requires a list of movies, stars, genres, user ratings, and the necessary relationships between them.

### Installing and Starting Project 1
For AWS, Project 1 has been tested to run on Ubuntu 16.04.6 LTS using an Apache Tomcat 8.5.53 server instance with Maven capabilities. For the UCI Grader, the link http://ec2-3-86-227-50.compute-1.amazonaws.com:8080/cs122b-spring20-team-13/ should be open based on the security group IP requirements. The finalized Project 1 build should be found in the master branch with this file.

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

## Project 1 Structure
### Movie List Servlet
The class file MovieListServlet.java functions as the home page of Project 1, displaying the top 20 rated movies when the user either first runs the project or when the 'Return to Movie List' hyperlink is clicked on. For each movie it shows the title, director, year, up to three genres, up to three stars, and rating score. Each movie has a hyperlink to a Single Movie Servlet page based on that movie's id, and each star has a hyperlink to a Single Movie Servlet page based on that star's name.
### Single Movie Servlet
Based on a given movie id, the class file SingleMovieServlet.java loads the the title, year, director, all featured stars, all associated genres, and rating score of the corresponding movie. Every star name is a hyperlink to a single star page and 'Return to Movie List' has a hyperlink back to the Movie List Servlet homepage.
### Single Star Servlet
Based on a given star name, the class file SingleStarServlet.java loads the the name, birth year (if available), director, and all featured movies. Every movie name is a hyperlink to a single movie page and 'Return to Movie List' has a hyperlink back to the Movie List Servlet homepage.
### pom.xml
The pom.xml file contains a list of artifacts to use, including mysql-connector-java and javax.servlet, along with directions to handle the Maven war packaging and the necessary maven-compiler-plugin and maven-war-plugin.

## Contributions
Parsa Hadidi:
- Set up the Class structures and provided AWS guidance. Worked on queries for the Top 20 Movie List.

Duy Nguyen: 
- Perfecting the majority of MySQL queries and the web servlet format using single star example as a basis. Provided the demo video for submission.

Both worked on debugging and finalizing every java class (SingleMovie, SingleStar, MovieList) as well as preparing README.md.
