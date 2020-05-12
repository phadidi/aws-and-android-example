import java.sql.*;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * This program demonstrates how to get structural information of
 * a database.
 *
 * @author www.codejava.net
 */
public class ReadDatabaseStructureExample {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/moviedb";
        String username = "mytestuser";
        String password = "mypassword";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {

            DatabaseMetaData meta = conn.getMetaData();

            String catalog = null, schemaPattern = null, tableNamePattern = null;
            String[] types = {"TABLE"};

            ResultSet rsTables = meta.getTables(catalog, schemaPattern, tableNamePattern, types);


            while (rsTables.next()) {
                String tableName = rsTables.getString(3);
                System.out.println("\n=== TABLE: " + tableName);

                String columnNamePattern = null;
                ResultSet rsColumns = meta.getColumns(catalog, schemaPattern, tableName, columnNamePattern);

                ResultSet rsPK = meta.getPrimaryKeys(catalog, schemaPattern, tableName);

                while (rsColumns.next()) {
                    String columnName = rsColumns.getString("COLUMN_NAME");
                    String columnType = rsColumns.getString("TYPE_NAME");
                    int columnSize = rsColumns.getInt("COLUMN_SIZE");
                    System.out.println("\t" + columnName + " - " + columnType + "(" + columnSize + ")");
                }

                while (rsPK.next()) {
                    String primaryKeyColumn = rsPK.getString("COLUMN_NAME");
                    System.out.println("\tPrimary Key Column: " + primaryKeyColumn);
                }

            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

    }
}