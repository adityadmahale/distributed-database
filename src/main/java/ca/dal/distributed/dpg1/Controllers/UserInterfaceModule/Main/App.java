package ca.dal.distributed.dpg1.Controllers.UserInterfaceModule.Main;

import java.io.Console;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App {

        public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
                Console c = System.console();
                String userName;
                String pass, passConfirm, securityAnswer;
                char[] ch, ch1;
                boolean loginValidation;

                List<String> getQueries = new ArrayList<String>();
                String Query = "temp";

                Login chk_login = new Login();
                Register reg = new Register();

                Scanner console = new Scanner(System.in);
                // int db_option = 0;

                System.out.println("*Hostname");
                System.out.println("Welcome");
                int access_option = 0, db_option = 0;

                while (access_option != 3) {
                        System.out.println("Select one of the below options:");
                        System.out.println("Enter 1 for login");
                        System.out.println("Enter 2 for Register");
                        System.out.println("Enter 3 to Exit");
                        access_option = console.nextInt();
                        switch (access_option) {
                                case 1:
                                        System.out.println("Enter your name: ");
                                        userName = c.readLine();
                                        System.out.println("Enter password: ");
                                        ch = c.readPassword();
                                        pass = String.valueOf(ch);
                                        System.out.println("Answer the following Security Question:");
                                        System.out.println("What primary school did you attend?");
                                        securityAnswer = c.readLine();
                                        loginValidation = chk_login.checkLogin(userName, pass, securityAnswer);
                                        if (loginValidation) {
                                                System.out.println("Sucessfully Logged in as:" + userName);
                                        } else {
                                                System.out.println("Invalid Credentials");
                                                break;
                                        }
                                        while (db_option != 5) {
                                                System.out.println("Select one of the below options:");
                                                System.out.println("Enter 1 for Write Queries");
                                                System.out.println("Enter 2 for Export");
                                                System.out.println("Enter 3 for Data Model");
                                                System.out.println("Enter 4 for Analytics");
                                                System.out.println("Enter 5 to Exit");
                                                db_option = console.nextInt();
                                                switch (db_option) {
                                                        case 1:
                                                                System.out.println(
                                                                                "Enter Query or Transaction, Enter x to Exit:");
                                                                while (!Query.equals("x")) {
                                                                                Query = c.readLine();
                                                                                if (!Query.equals("x")) {
                                                                                getQueries.add(Query);
                                                                        }
                                                                }
                                                                System.out.println("Your Entered Queries are:");
                                                                for (int i = 0; i < getQueries.size(); i++) {
                                                                        System.out.println(getQueries.get(i));
                                                                }
                                                                break;
                                                        case 2:
                                                                System.out.println("Export Function Exectution");
                                                                break;
                                                        case 3:
                                                                System.out.println("Data Model Function Exectution");
                                                                break;
                                                        case 4:
                                                                System.out.println("Analytics Function Exectution");
                                                                break;
                                                        case 5:
                                                                System.out.println("Exiting......");
                                                                break;
                                                        default:
                                                                System.out.println("Invalid input");
                                                }
                                                System.out.print("\n");
                                        }
                                        break;
                                case 2:
                                        System.out.println("set your name: ");
                                        userName = c.readLine();
                                        System.out.println("set your password: ");
                                        ch = c.readPassword();
                                        pass = String.valueOf(ch);
                                        System.out.println("Enter password once again: ");
                                        ch1 = c.readPassword();
                                        passConfirm = String.valueOf(ch1);
                                        System.out.println(pass);
                                        System.out.println(passConfirm);
                                        while (!pass.equals(passConfirm)) {
                                                System.out.println("Passwords did not matched");
                                                System.out.println("set your password: ");
                                                ch = c.readPassword();
                                                pass = String.valueOf(ch);
                                                System.out.println("Enter password once again: ");
                                                ch = c.readPassword();
                                                passConfirm = String.valueOf(ch);
                                        }
                                        System.out.println("Answer the following Security Question:");
                                        System.out.println("What primary school did you attend?");
                                        securityAnswer = c.readLine();
                                        reg.setRegister(userName, pass, securityAnswer);
                                        System.out.println("Sucessfully Registered, Please Login now...");
                                        break;
                                case 3:
                                        System.out.println("Exiting......");
                                        break;
                                default:
                                        System.out.println("Invalid input");
                        }
                        System.out.println();
                }

        }
}
