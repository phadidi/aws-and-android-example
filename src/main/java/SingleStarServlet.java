import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


// this annotation maps this Java Servlet Class to a URL
@WebServlet("/starlist")

public class SingleStarServlet extends HttpServlet {
    private List<Movie> movieList;
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // change this to your own mysql username and password
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/fabflix_db";

        // set response mime type
        response.setContentType("text/html");

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            // create database connection
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // declare statement
            Statement statement = connection.createStatement();
            // prepare query
            String starName = request.getParameter("action");
            String query = "select s.name, s.birthYear,\n" +
                    "group_concat(distinct m.title separator ', ') as titles\n" +
                    "from stars s, movies m, stars_in_movies sim\n" +
                    "where s.id = sim.starId and m.id = sim.movieId\n" +
                    "and name = " + "'" + starName + "';";
            // execute query
            ResultSet resultSet = statement.executeQuery(query);

            out.println("<body>");
            out.println("<h1>Single Star Page</h1>");


            // add a row for every star result
            movieList = new ArrayList<Movie>();
            while (resultSet.next()) {
                // get a star from result set
                String name = resultSet.getString("name");
                String birthYear = resultSet.getString("birthYear");
                String titles = resultSet.getString("titles");
                //String movieIDs = resultSet.getString("movieIds");

                out.println("<p>Name: " + name + "</p>");
                if (birthYear == null)
                    out.println("<p>Birth Year: N/A</p>");
                else
                    out.println("<p>Birth Year: " + birthYear + "</p>");
                out.print("<p>Movies appeared in:</p>");
                String[] movieSplit = titles.split(",");
                //String[] movieIDSplit =  movieIDs.split(",");
                out.print("<ul>");
                //int midIndex = 0;

                for (String m : movieSplit) {
                    if (m.startsWith(" ")) {
                        m = m.substring(1, m.length());
                    }
                    // TODO: get the id for the corresponding movie, add distinct so there is only one result
                    String matchQuery = "select distinct m.id\n" +
                            "from stars s, movies m, stars_in_movies sim\n" +
                            "where s.id = sim.starId and m.id = sim.movieId\n" +
                            "and s.name = " + "'" + starName + "'" + " and m.title = " + "'" + m + "'" + ";";
                    String thisId = "";
                    // create database connection
                    Connection connectionM = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
                    // declare statement
                    Statement statementM = connectionM.createStatement();
                    ResultSet resultMovie = statementM.executeQuery(matchQuery);
                    while (resultMovie.next() && thisId.compareTo("") == 0) { // check to stop after first row
                        thisId = resultMovie.getString("id");
                    }
                    if (thisId.startsWith(" ")) {
                        thisId = thisId.substring(1, m.length());
                    }
                    //if (movieIDSplit[midIndex].startsWith(" ")) {
                    // movieIDSplit[midIndex] = movieIDSplit[midIndex].substring(1, movieIDSplit[midIndex].length());
                    //}
                    out.println("<li><a href='movie?action=" + "'" + thisId + "'" + "'>" + m + "</a></li>");
                    //midIndex++;
                    resultMovie.close();
                    statementM.close();
                    connectionM.close();
                }
                out.println("</ul>");

                out.println("<p><a href='/cs122b_spring20_team_13/'>Return to Movie List</a></p>");
            }

            out.println("</body>");

            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             *   there's no console to see the print messages.
             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
             *
             * To view the last n lines (for example, 100 lines) of messages you can use:
             *   tail -100 catalina.out
             * This can help you debug your program after deploying it on AWS.
             */
            e.printStackTrace();

            out.println("<body>");
            out.println("<p>");
            out.println("Exception in doGet: " + e.getMessage());
            out.println("</p>");
            out.print("</body>");
        }

        out.println("</html>");
        out.close();
    }

}
