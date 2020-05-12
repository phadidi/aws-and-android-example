


public class EncryptMain {

    public static void main(String[] args ) throws Exception {
        UpdateCustomerPassword ue = new UpdateCustomerPassword();
        ue.encryptCustomer();

        UpdateEmployeePassword uem = new UpdateEmployeePassword();
        uem.encryptEmployee();
    }
}
