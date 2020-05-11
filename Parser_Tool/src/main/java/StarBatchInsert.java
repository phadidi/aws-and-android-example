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
        DomStarParser dpm = new DomStarParser();

        //call parser
        List<Star> stars = dpm.runStarParser();

        // build a hashMap of stars already in db
        Map<String,Integer> dbStars= new HashMap<String,Integer>();

        String getDBMovies = "select * from stars";
        try {
            PreparedStatement getMovies = conn.prepareStatement(getDBMovies);
            ResultSet rs = getMovies.executeQuery();

            while(rs.next()){
                String name = rs.getString("name");
                int birthYear = rs.getInt("birthYear");

                dbStars.put(name, birthYear);

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
        try {
            conn.setAutoCommit(false);


            psInsertRecord=conn.prepareStatement(sqlInsertRecord);

            for(int s = 0; s < stars.size(); s++)
            {
                String name = stars.get(s).getName();
                int byear = stars.get(s).getBirthYear();

                if (dbStars.get(name) != null && dbStars.get(name)==byear){
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
                psInsertRecord.addBatch();


                // reset id and reassign max_id
                max_id = sid;
                sid = "nm";
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


