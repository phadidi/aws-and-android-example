import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JDBCMovieList implements Parameters {

    List<Movie> movieList = new ArrayList<Movie>();
    String currentMovie = "";

    public static String trimEnd( String s,  String suffix) {

        if (s.endsWith(suffix)) {

            return s.substring(0, s.length() - suffix.length());

        }
        return s;
    }

    public void createAndAddMovieObject(String rowTitle, int rowYear, String rowDirector, String rowStar, String rowGenre, float rowRating) {
        int size = movieList.size();
        if(size == 0 || !rowTitle.equals(movieList.get(size - 1).getTitle())) {
            movieList.add(new Movie(rowTitle, rowYear, rowDirector, rowRating));
            size = movieList.size();
        }

        int s = movieList.get(size - 1).addStar(rowStar);
        int g = movieList.get(size - 1).addGenre(rowGenre);
    }

    public static void main(String[] arg) throws Exception {

        // Incorporate mySQL driver
        Class.forName("com.mysql.jdbc.Driver").newInstance();

        // Connect to the test database
        Connection connection = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
                Parameters.username, Parameters.password);

        if (connection != null) {
            System.out.println("Connection established!!");
            System.out.println();
        }

        // Create an execute an SQL statement to select all of table"Stars" records
        Statement select = connection.createStatement();
        String query1 = "select  m.id from movies m\n" +
                "join ratings r on r.movieId = m.Id\n" +
                "order by r.rating desc \n" +
                "limit 20";


        ResultSet result1 = select.executeQuery(query1);

        // Get metadata from stars; print # of attributes in table
        System.out.println("The results of the query");
        ResultSetMetaData metadata = result1.getMetaData();
        System.out.println("There are " + metadata.getColumnCount() + " columns");

        JDBCMovieList test = new JDBCMovieList();
        String movieIdsConcat = "";
        while(result1.next()) {
            String id = result1.getString("id");
            System.out.println("Id = " + id);
            movieIdsConcat += "'" + id + "'" + ",";
        }
        System.out.println(movieIdsConcat);
        movieIdsConcat = trimEnd(movieIdsConcat, ",");
        System.out.println(movieIdsConcat);

        String query2 = "select  distinct gim.movieId, gim.genreId,  g.name from genres_in_movies gim, movies m, ratings r, genres g\n" +
                "where gim.movieId in ("+ movieIdsConcat + ")\n" +
                "and gim.movieId = m.id\n" +
                "and gim.movieId = r.movieId\n" +
                "and gim.genreId = g.id\n" +
                "order by r.rating desc;";

        ResultSet result2 = select.executeQuery(query2);

        while(result2.next()) {
            String movieId = result2.getString("movieId");
            String genreName = result2.getString("name");
            System.out.println("movieId = " + movieId + ", genreName = " + genreName);
        }

        String query3 = "select distinct sim.movieId, sim.starId,  s.id, s.name, s.birthYear\n" +
                "from stars_in_movies sim, movies m, ratings r, stars s\n" +
                "where sim.movieId in (" + movieIdsConcat + ")\n" +
                "and sim.movieId = m.id\n" +
                "and sim.movieId = r.movieId\n" +
                "and sim.starId = s.id\n" +
                "order by r.rating desc;";

        ResultSet result3 = select.executeQuery(query3);
        while(result3.next()) {
            String movieId = result3.getString("movieId");
            String starName = result3.getString("name");
            System.out.println("movieId = " + movieId + ", starName = " + starName);
        }

        // TODO: We have to instantiate our class objects for Movie List, and populate the data in an HTML webpage.

    }
}




//import java.sql.*;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class JDBCMovieList implements Parameters {
//
//    List<Movie> movieList = new ArrayList<Movie>();
//    String currentMovie = "";
//
//    public void createAndAddMovieObject(String rowTitle, int rowYear, String rowDirector, String rowStar, String rowGenre, float rowRating) {
//        int size = movieList.size();
//        if(size == 0 || !rowTitle.equals(movieList.get(size - 1).getTitle())) {
//            movieList.add(new Movie(rowTitle, rowYear, rowDirector, rowRating));
//            size = movieList.size();
//        }
//
//        int s = movieList.get(size - 1).addStar(rowStar);
//        int g = movieList.get(size - 1).addGenre(rowGenre);
//    }
//
//    public static void main(String[] arg) throws Exception {
//
//        // Incorporate mySQL driver
//        Class.forName("com.mysql.jdbc.Driver").newInstance();
//
//        // Connect to the test database
//        Connection connection = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
//                Parameters.username, Parameters.password);
//
//        if (connection != null) {
//            System.out.println("Connection established!!");
//            System.out.println();
//        }
//
//        // Create an execute an SQL statement to select all of table"Stars" records
//        Statement select = connection.createStatement();
//        String query = "SELECT * FROM top_20_movie_list ORDER BY top_20_movie_list.title;";
//        ResultSet result = select.executeQuery(query);
//
//        // Get metatdata from stars; print # of attributes in table
//        System.out.println("The results of the query");
//        ResultSetMetaData metadata = result.getMetaData();
//        System.out.println("There are " + metadata.getColumnCount() + " columns");
//
//        JDBCMovieList test = new JDBCMovieList();
//        while(result.next()) {
//            test.createAndAddMovieObject(result.getString("title"),
//                                        result.getInt("year"),
//                                        result.getString("director"),
//                                        result.getString("starName"),
//                                        result.getString("genreName"),
//                                        result.getFloat("rating"));
//        }
//
//        System.out.println(test.movieList.size());
//        for(Movie m : test.movieList) {
//            System.out.print(m.getTitle() + " | ");
//            System.out.print(m.getYear() + " | ");
//            System.out.print(m.getDirector() + " | ");
//            System.out.print(m.getStars() + " | ");
//            System.out.print(m.getGenres() + " | ");
//            System.out.println(m.getRating());
//        }
//    }
//}
