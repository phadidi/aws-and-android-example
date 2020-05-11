import java.sql.*;
import java.util.*;


public class StarBatchInsert {

    public void insertStars() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        Connection conn = null;

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String jdbcURL="jdbc:mysql://localhost:3306/moviedb";

        try {
            conn = DriverManager.getConnection(jdbcURL,"mytestuser", "mypassword");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement psInsertRecord=null;
        String sqlInsertRecord=null;

        int[] iNoRows=null;

        //create an instance
        DomCastParser dpc = new DomCastParser();

        //call parser
        List<StarInMovie> casts = dpc.runCastParser();

        //create an instance
        DomStarParser dps = new DomStarParser();

        //call parser
        List<Star> stars = dps.runStarParser();

        //create an instance
        DomMovieParser dpm = new DomMovieParser();

        //call parser
        List<Movie> movies = dpm.runMovieParser();

        // build a hashMap of movies already in db
        Map<List<String>, List<String>> dbMovies= new HashMap<List<String>, List<String>>();

        String getDBMovies = "select * from movies";
        try {
            PreparedStatement getMovies = conn.prepareStatement(getDBMovies);
            ResultSet rs = getMovies.executeQuery();

            while(rs.next()){
                String id = rs.getString("id");
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");

                List<String> key = new ArrayList<>();
                key.add(title);
                key.add(year);

                List<String> yearDirector = new ArrayList<>();
                yearDirector.add(id);
                yearDirector.add(year);
                yearDirector.add(director);

                dbMovies.put(key, yearDirector);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        // build a hashMap of stars already in db
        Map<String,String> dbStars= new HashMap<String,String>();

        String getDBStars = "select * from stars";
        try {
            PreparedStatement getMovies = conn.prepareStatement(getDBStars);
            ResultSet rs = getMovies.executeQuery();

            while(rs.next()){
                String name = rs.getString("name");
                String starId = rs.getString("id");

                dbStars.put(name, starId);

            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        Map<String,String> dbCasts= new HashMap<String,String>();

        String getDBCasts = "select * from stars_in_movies";
        try {
            PreparedStatement getCasts = conn.prepareStatement(getDBCasts);
            ResultSet rs = getCasts.executeQuery();

            while(rs.next()){
                String starId = rs.getString("starId");
                String movieId = rs.getString("movieId");

                dbCasts.put(starId, movieId);

            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        // getting max(id) from stars
        String getStarId = "select max(id) as id from stars";
        String sid = "nm";
        String max_id = "";
        try {
            PreparedStatement getId = conn.prepareStatement(getStarId);
            ResultSet rs = getId.executeQuery();

            while(rs.next()){
                max_id = rs.getString("id");
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        sqlInsertRecord="insert into stars values(?,?,?)";
        String sqlInsertSim="insert into stars_in_movies values(?,?)";
        try {
            conn.setAutoCommit(false);


            psInsertRecord=conn.prepareStatement(sqlInsertRecord);
            PreparedStatement psInsertSim=conn.prepareStatement(sqlInsertSim);
            for(int s = 0; s < stars.size(); s++)
            {
                String name = stars.get(s).getName();
                int byear = stars.get(s).getBirthYear();

                if (dbStars.get(name) != null){
                    continue;
                }

                // increment id
                int id_num = Integer.parseInt(max_id.substring(2),10);
                id_num+=1;
                String id_s = Integer.toString(id_num);
                if(id_s.length() < 7){
                    for(int i = 0; i < (7 - id_s.length()); i++){
                        sid += "0"; // add back prefix 0
                    }
                }
                sid += id_s; // update id string with new numeric values
                //

                psInsertRecord.setString(1, sid);
                psInsertRecord.setString(2, name);
                if(byear == 0){
                    psInsertRecord.setNull(3, Types.NULL);
                }else {
                    psInsertRecord.setInt(3, byear);
                }
                dbStars.put(name,sid);
                psInsertRecord.addBatch();


                // reset id and reassign max_id
                max_id = sid;
                sid = "nm";
            }

            for(int c = 0; c < casts.size(); c++) {
                String mId = casts.get(c).getMovieId();
                String sname = casts.get(c).getStarName();

                // if the star in cast parsing does not exist in stars, then skip
                if(dbStars.get(sname) == null){
                    continue;
                }
                // TO DO: Match mId to id of Movie Object, gets the equivalent ID that is in database, gets the id of stars in db, then insert
                int m = matchStarsandMovies(mId, movies);
                String movieId = "";
                String starId = "";
                List<String> k = new ArrayList<>();
                k.add(movies.get(m).getTitle());
                k.add(Integer.toString(movies.get(m).getYear()));
                if(dbMovies.get(k) != null
                        && dbMovies.get(k).get(1).compareTo(Integer.toString(movies.get(m).getYear())) == 0
                        && dbMovies.get(k).get(2).compareTo(movies.get(m).getDirector()) == 0){
                    movieId = dbMovies.get(k).get(0);
                    starId = dbStars.get(sname);

                    psInsertSim.setString(1, starId);
                    psInsertSim.setString(2, movieId);
                    psInsertSim.addBatch();
                }
            }

            iNoRows=psInsertRecord.executeBatch();
            psInsertSim.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if(psInsertRecord!=null) psInsertRecord.close();
            if(conn!=null) conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public int matchStarsandMovies(String sfid, List<Movie> movies){
        int index = 0;
        for(int m = 0; m < movies.size(); m++){
            if(movies.get(m).getId().compareTo(sfid) == 0){
                index = m;
                break;
            }
        }
        return index;
    }

}


