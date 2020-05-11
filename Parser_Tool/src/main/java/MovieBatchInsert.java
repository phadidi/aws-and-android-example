import java.sql.*;
import java.util.*;


public class MovieBatchInsert {

    public static void main (String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

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
        DomMovieParser dpm = new DomMovieParser();

        //call parser
        List<Movie> movies = dpm.runMovieParser();

        // build a hashMap of movies already in db
        Map<String, List<String>> dbMovies= new HashMap<String, List<String>>();

        String getDBMovies = "select * from movies";
        try {
            PreparedStatement getMovies = conn.prepareStatement(getDBMovies);
            ResultSet rs = getMovies.executeQuery();

            while(rs.next()){
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");

                List<String> yearDirector = new ArrayList<>();
                yearDirector.add(year);
                yearDirector.add(director);

                dbMovies.put(title, yearDirector);

            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        // getting max(id) from movies
        String getIdQuery = "select max(id) as id from movies;";
        String mid = "tt";
        String max_id = "";
        try {
            PreparedStatement getId = conn.prepareStatement(getIdQuery);
            ResultSet rs = getId.executeQuery();

            while(rs.next()){
                max_id = rs.getString("id");
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        sqlInsertRecord="insert into movies values(?,?,?,?)";
        try {
            conn.setAutoCommit(false);

            psInsertRecord=conn.prepareStatement(sqlInsertRecord);

            Iterator<Movie> it = movies.iterator();
            for(int m = 0; m < movies.size(); m++)
            {
                if (dbMovies.get(movies.get(m).getTitle()) != null){
                    //System.out.println(dbMovies.get(movies.get(m).getTitle()));
                    if(dbMovies.get(movies.get(m).getTitle()).get(0).compareTo(Integer.toString(it.next().getYear())) == 0 &&
                            dbMovies.get(movies.get(m).getTitle()).get(1).compareTo(it.next().getDirector()) == 0){
                        continue;
                    }
                }

                String t = movies.get(m).getTitle();
                int y = movies.get(m).getYear();
                String d = movies.get(m).getDirector();

                if(t.compareTo("Null")==0 || y==0 || d.compareTo("Null")==0) {
                    continue;
                }

                // increment id
                int id_num = Integer.parseInt(max_id.substring(2),10);
                id_num+=1;
                String id_s = Integer.toString(id_num);
                if(id_s.length() < 7){
                    for(int i = 0; i < (7 - id_s.length()); i++){
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

                // reset id and reassign max_id
                max_id = mid;
                mid = "tt";
            }

            iNoRows=psInsertRecord.executeBatch();
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

}


