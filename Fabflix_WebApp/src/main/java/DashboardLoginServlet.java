package main.java;

import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "DashboardLoginServlet", urlPatterns = "/api/_dashboard")
public class DashboardLoginServlet extends HttpServlet {
    public String getServletInfo() {
        return "Dashboard Login Servlet handles the login of an employee to access the dashboard";
    }


    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */

    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            out.println("<html>");
            out.println("<head><title>Error</title></head>");
            out.println("<body>");
            out.println("<p>recaptcha verification error</p>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</body>");
            out.println("</html>");

            out.close();
            return;
        }

        try {
            // the following few lines are for connection pooling
            // Obtain our environment naming context

            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            if (ds == null)
                out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");

            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Get a connection from dataSource
            //Connection dbcon = dataSource.getConnection();

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
            String resultEmail = "";
            String resultPassword = "";
            String resultFullname = "";

            // Declare our statement
            PreparedStatement statementLogin = dbcon.prepareStatement("select * from employees where email = ?;");
            statementLogin.setString(1, email);
            ResultSet rs = statementLogin.executeQuery();
            while (rs.next()) {
                resultEmail = rs.getString("email");
                resultPassword = rs.getString("password");
                resultFullname = rs.getString("fullname");

            }

            JsonObject responseJsonObject = new JsonObject();

            if (email.equals(resultEmail)) {
                if (new StrongPasswordEncryptor().checkPassword(password, resultPassword)) {
                    // Login success: set this user into the session
                    Employee currentEmployee = new Employee(resultEmail, resultPassword, resultFullname);
                    request.getSession().setAttribute("employee", currentEmployee);

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                } else {
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "employee " + email + " doesn't exist");
            }
            response.getWriter().write(responseJsonObject.toString());
            rs.close();
            statementLogin.close();
            dbcon.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();
    }
}
