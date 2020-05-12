import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MovieBatchInsert {

    public void insertMovieAndGenres() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        Connection conn = null;

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String jdbcURL = "jdbc:mysql://localhost:3306/moviedb";

        try {
            conn = DriverManager.getConnection(jdbcURL, "mytestuser", "mypassword");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement psInsertRecord = null;
        String sqlInsertRecord = null;

        int[] iNoRows = null;

        //create an instance
        DomMovieParser dpm = new DomMovieParser();

        //call parser
        List<Movie> movies = dpm.runMovieParser();

        // build a hashMap of movies already in db
        Map<List<String>, List<String>> dbMovies = new HashMap<List<String>, List<String>>();

        String getDBMovies = "select * from movies";
        try {
            PreparedStatement getMovies = conn.prepareStatement(getDBMovies);
            ResultSet rs = getMovies.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");

                List<String> key = new ArrayList<>();
                key.add(title);
                key.add(year);

                List<String> yearDirector = new ArrayList<>();
                yearDirector.add(year);
                yearDirector.add(director);

                dbMovies.put(key, yearDirector);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // getting max(id) from movies
        String getIdQuery = "select max(id) as id from movies;";
        String mid = "tt";
        String max_id = "";
        try {
            PreparedStatement getId = conn.prepareStatement(getIdQuery);
            ResultSet rs = getId.executeQuery();

            while (rs.next()) {
                max_id = rs.getString("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // getting max(id) from genres
        String getGenreId = "select max(id) as id from genres;";
        int genre_id = 0;
        try {
            PreparedStatement getGId = conn.prepareStatement(getGenreId);
            ResultSet rs = getGId.executeQuery();

            while (rs.next()) {
                genre_id = rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // getting max(id) from genres
        String getGenres = "select * from genres;";
        Map<String, Integer> genresMap = new HashMap<String, Integer>();
        try {
            PreparedStatement getG = conn.prepareStatement(getGenres);
            ResultSet rs = getG.executeQuery();

            while (rs.next()) {
                genresMap.put(rs.getString("name"), rs.getInt("id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        sqlInsertRecord = "insert into movies values(?,?,?,?)";
        String sqlInsertGenres = "insert into genres values(?,?)";
        String sqlInsertGim = "insert into genres_in_movies values(?,?)";
        try {
            conn.setAutoCommit(false);


            psInsertRecord = conn.prepareStatement(sqlInsertRecord);
            PreparedStatement psInsertGenres = conn.prepareStatement(sqlInsertGenres);
            PreparedStatement psInsertGim = conn.prepareStatement(sqlInsertGim);

            for (int m = 0; m < movies.size(); m++) {
                List<String> k = new ArrayList<>();
                k.add(movies.get(m).getTitle());
                k.add(Integer.toString(movies.get(m).getYear()));

                if (dbMovies.get(k) != null) {
                    //System.out.println(dbMovies.get(movies.get(m).getTitle()));
                    if (dbMovies.get(k).get(0).compareTo(Integer.toString(movies.get(m).getYear())) == 0 &&
                            dbMovies.get(k).get(1).compareTo(movies.get(m).getDirector()) == 0) {
                        continue;
                    }
                }

                String t = movies.get(m).getTitle();
                int y = movies.get(m).getYear();
                String d = movies.get(m).getDirector();
                String g = movies.get(m).getGenre();

                if (g.compareTo("Null") == 0 || g.compareTo("Unknown") == 0) {
                    continue;
                }

                if (genresMap.get(g) == null) {
                    genre_id += 1;
                    //System.out.print(genre_id);
                    //System.out.println(" " + g);
                    genresMap.put(g, genre_id);
                    psInsertGenres.setInt(1, genre_id);
                    psInsertGenres.setString(2, g);
                    psInsertGenres.addBatch();
                }

                if (t.compareTo("Null") == 0 || y == 0 || d.compareTo("Null") == 0) {
                    continue;
                }

                // increment id
                int id_num = Integer.parseInt(max_id.substring(2), 10);
                id_num += 1;
                String id_s = Integer.toString(id_num);
                if (id_s.length() < 7) {
                    for (int i = 0; i < (7 - id_s.length()); i++) {
                        mid += "0"; // add back prefix 0
                    }
                }
                mid += id_s; // update id string with new numeric values
                //

                psInsertRecord.setString(1, mid);
                psInsertRecord.setString(2, t);
                psInsertRecord.setInt(3, y);
                psInsertRecord.setString(4, d);
                psInsertRecord.addBatch();

                psInsertGim.setInt(1, genresMap.get(g));
                psInsertGim.setString(2, mid);
                psInsertGim.addBatch();

                // reset id and reassign max_id
                max_id = mid;
                mid = "tt";
            }

            iNoRows = psInsertRecord.executeBatch();
            psInsertGenres.executeBatch();
            psInsertGim.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (psInsertRecord != null) psInsertRecord.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


