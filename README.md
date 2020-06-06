# cs122b-spring20-team-13 Project 5
This is a submission for Project 5 with Connection Pooling, MySQL Master-Slave Instances, Load Balancing, and Search Performance Measurements.

## Getting Started
Make sure to initialize a MySQL database with the included createtable.sql and populate it accordingly. This requires a list of movies, stars, genres, customers, customer ratings, creditcards, and the necessary relationships between them.

### Installing and Starting Project 5
For AWS, Project 5 has been tested to run on Ubuntu 16.04.6 LTS using an Apache Tomcat 8.5.53 server instance with Maven capabilities.

If you want to run from your own AWS instance machine, make sure to clone the project with the following command:

``` git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-13.git```

Create a new user for MySQL on your AWS machine with the following credentials:

```username: mytestuser```

```password: mypassword```

Make sure this user has all access and run the createtable.sql scripts to create your database.

Before going to the next step, remember to include your ReCaptchaConstant class which defines the Secret Key to your Fabflix src folder!!!!!

Then to prepare your war file, run the following commands:

```mvn package```
```cp ./target/*.war /home/ubuntu/tomcat/webapps```

Make sure you have Tomcat running. Then, go to your Tomcat manager page. You should see a newly deployed war file named "cs122b-spring20-team-13." Click on the name to open the Fabflix Movie List page.


- #### General
    - ##### Team#: 13
    
    - ##### Names: Parsa Hadidi, Duy Nguyen
    
    - ##### Project 5 Video Demo Link: https://youtu.be/bBNyrIYkMK8

    - ##### Instruction of deployment: same as other projects but LoginFilter is disabled

    - ##### Collaborations and Work Distribution:
    Duy Nguyen: Set up AWS for master, slave, load balancer, GCP, JMeter test, parser script
    Parsa Hadidi: Added Connection Pooling, timer code and write file


- #### Connection Pooling
    - ##### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    Cart Servlet, Confirmation Servlet, Dashboard Login Servlet, Form ReCaptcha, Login Servlet, Main Dashboard Servlet, Main Page, Menu Dashboard Servlet, Movie Suggestion, Payment Servlet, Single Movie Servlet, Single Star Servlet, Star Dashboard Servlet    
    - ##### Explain how Connection Pooling is utilized in the Fabflix code.
    An initial context is established each time one of the servlets is called. Then a check is performed if a context already exists, if so the current context is used instead. The context stores the data source connection so it can be reused multiple times for one client.
    - ##### Explain how Connection Pooling works with two backend SQL.
    Connection Pooling is enabled within the WebApp Project, which is deployed on both Master and Slave instances

- #### Master/Slave
    - ##### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
    Added new db resource named masterdb to context.xml in META-INF | 
    Changed datasource to masterdb in ConfirmationServlet, MainDashboardServlet, and StarDashboardServlet since they make write requests to SQL

    - ##### How read/write requests were routed to Master/Slave SQL?
    In order to ensure all write requests were routed to master instead of slave, we hard coded every servlets that does an executeUpdate() to the MySQL to look at the datasource with the Master's public IP
    All read requests can go to either Master or Slave.
    

- #### JMeter TS/TJ Time Logs
    - ##### Instructions of how to use the `log_processing.py` script to process the JMeter logs.
    log_processing.py is located in the logs/ folder
    To run log_processing.py, go to this folder from terminal and use command "python log_processing.py file_name.txt"
    Replace "filename" with the name of the log files you want to parse
    Here are the names of the log files (single-http-1-thread.txt, single-http-10-thread.txt, single-https-10-thread.txt, single-http-10-thread-noCP.txt, scaled-http-1-thread.txt, scaled-http-10-thread.txt, scaled-http-10-thread-noCP.txt)

- #### JMeter TS/TJ Time Measurement Report (screenshots are found in the path stated in the table)

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | (img/single-http-1-thread.png)   | 80                     | 2.4301980389856168152               | 2.3689721355034065731     | Interestingly, lower Avg. Query Time than in Scaled 1 thread case. TS and TJ are higher than ones see int Scaled 1 thread though.           |
| Case 2: HTTP/10 threads                        | (img/single-http-10-thread.png)   | 120                   | 2.3183667638152916091               | 2.2919555378501139664     | TS and TJ are faster, but Avg. Query Time is higher.           |
| Case 3: HTTPS/10 threads                       | (img/single-https-10-thread.png)   | 339                  | 2.2634320087055264104               | 2.2394041945495839485     | TS and TJ are faster, but not by any significance (could be caused by any factor). However, Avg. Query Time is longer. I think it has to do with the security aspect of HTTPS, which can cause longer initial connection time.           |
| Case 4: HTTP/10 threads/No connection pooling  | (img/single-http-10-thread-noCP.png)   | 250              | 2.1876663951551855725               | 2.1673469939439820031     | Interestingly, TS and TJ are faster without connection pooling; however, Avg. Query Time takes longer than one in HTTP 10 thread.          |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | (img/scaled-http-1-thread.png)   | 81                     | 1.8507945480696443852               | 1.8075041650264951354     | Has the fastest TS and TJ compared to the other test cases. Avg. Query time is also very short.           |
| Case 2: HTTP/10 threads                        | (img/scaled-http-10-thread.png)   | 100                   | 2.3360979882664647533               | 2.3080447146101441547     | TS and TJ are slightly higher than scaled 1 thread. Avg. Query time sees an increase from 81 to 100.           |
| Case 3: HTTP/10 threads/No connection pooling  | (img/scaled-http-10-thread-noCP.png)   | 112              | 2.3611917891748670506               | 2.3383591339894023164     | With no connection pooling, TS, TJ and Avg. Query Time sees longer runtime as expected.           |


## Project Structure

### *NEW SEARCH IMPLEMENTATIONs* ###
Now support FULLTEXT search for movie titles
New main search bar - supports autocomplete suggestions

### PreparedStatements
For Webapps: MovieListServlet, PaymentServlet, MainDashboardServlet, StarDashboardServlet, LoginServlet
For Parser: MovieBatchInsert, StarBatchInsert
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
### Movie Suggestion
The Movie Suggestion servlet uses sql queries to perform autocomplete searches whose results are visible on the main page.
### pom.xml
The pom.xml file contains a list of artifacts to use, including mysql-connector-java and javax.servlet, along with directions to handle the Maven war packaging and the necessary maven-compiler-plugin and maven-war-plugin.
### Our Substring Design
For searching, we simnply used LIKE "%some pattern provided by user%" to match with any value that has that pattern somewhere in the string value. For example, "%vid m%" would match the field "David Mirkin." Similarly, for matching beginning letters like 'A', we used LIKE "A%". This pattern will match anything with A as the first letter in the string value.


### Parser_Tool
Has multiple classes responsible for parsing movies, actors, and movie-actor relations in respective XML files and inserting them into the database if not existed already. Contains a MainParser class that calls all of these classes and run them in a main(). REMEMBER TO INCLUDE YOUR XML FILES IN YOUR PROJECT FOLDER

## Android App Structure

### Login
Front end login page that communicates to webapp backend
### Autocomplete Search
Front end autocomplete search that communicates to webapp backend
### Movielist
Front end search results that are retrieved from webapp backend - can cycle betweeb pages using next/prev button
### Single Movie
Front end single movie page that is retrieved from webapp backend

#### Inconsistencies Report:
not all movies have a t tag for titles. 
Genre value has leading and trailing spaces and mismatching capitalization. 
Some movie ids are notated with a filmed tag instead of an fid tag

matched Animation(db) with Cartoon(xml) / 
matched Sci-Fi(db) with science fiction(xml) / 
matched Romance(db) with Romantic(xml). 

Some genres are not listed on the standord website but exists in xml
same as BioG, Fant.

Decided to only stick to movies that have categories matched with code listed on stanford's category table.

Full Report here:
https://docs.google.com/document/d/1cx6k42_QxoWLpvTqp1aHjCpX1WMZW0Tzqum50D1mLGs/edit?usp=sharing

#### Optimization methods
We used batch insertion and hashmaps of existing data from database to ensure optimization of the parser. At a given point when we need to compare to something to the database, we would refer to a HashMap that contains the necessary information. Using batch inserts, we can treat multiple insert queries as one transactions, effectively reducing the runtime.

### Password_Encrypt
Another Java program that has 2 classes: 1 for encrypting employees password in DB and the other for customers. The EncryptMain class calls these two and run it in a main().

## Contributions
Parsa Hadidi:
- Added Connection Pooling, timer code and write file

Duy Nguyen: 
- Set up AWS for master, slave, load balancer, GCP, JMeter test, parser script

Both worked on debugging and finalizing every java class and HTML/Javascript page.