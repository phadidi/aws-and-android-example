import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class EncryptMain {

    public static void main(String[] args ) throws Exception {
        UpdateCustomerPassword ue = new UpdateCustomerPassword();
        ue.encryptCustomer();

        UpdateEmployeePassword uem = new UpdateEmployeePassword();
        uem.encryptEmployee();
    }
}
