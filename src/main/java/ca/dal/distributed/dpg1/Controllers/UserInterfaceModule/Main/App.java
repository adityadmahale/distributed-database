package ca.dal.distributed.dpg1.Controllers.UserInterfaceModule.Main;

import java.io.Console;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import ca.dal.distributed.dpg1.Controllers.ERDModule.Exceptions.ERDGeneratorException;
import ca.dal.distributed.dpg1.Controllers.ERDModule.Main.ERDGenerator;
import ca.dal.distributed.dpg1.Controllers.ERDModule.Main.ERDGeneratorMain;

/**
 * Hello world!
 */
public class App {

        static ERDGeneratorMain erdGenerator = new ERDGenerator();

        public static void main(String[] args) throws NoSuchAlgorithmException, IOException, ERDGeneratorException {
                
                Scanner c = new Scanner(System.in);
                String userName;
                String pass, passConfirm, securityAnswer;
                char[] ch, ch1;
                boolean loginValidation;
                String Query = "temp";
                Login chk_login = new Login();
                Register reg = new Register();
                Scanner console = new Scanner(System.in);

                System.out.println("*Hostname");
                System.out.println("Welcome");
                int access_option = 0, db_option = 0;

                while (access_option != 3) {
                        System.out.println("Select one of the below options:");
                        System.out.println("\nEnter 1 for login");
                        System.out.println("\nEnter 2 for Register");
                        System.out.println("\nEnter 3 to Exit");
                        access_option = console.nextInt();
                        switch (access_option) {
                                case 1:
                                        System.out.println("\nEnter your name: ");
                                        userName = c.nextLine();
                                        System.out.println("\nEnter password: ");
//                                        ch = c.readPassword();
                                        pass=c.nextLine();
//                                        pass = String.valueOf(ch);
                                        System.out.println("\nAnswer the following Security Question:");
                                        System.out.println("\nWhat primary school did you attend?");
                                        securityAnswer = c.nextLine();
                                        loginValidation = chk_login.checkLogin(userName, pass, securityAnswer);
                                        if (loginValidation) {
                                                System.out.println("\nSucessfully Logged in as:" + userName);
                                        } else {
                                                System.out.println("\nInvalid Credentials");
                                                break;
                                        }
                                        while (db_option != 5) {
                                                System.out.println("\nSelect one of the below options:");
                                                System.out.println("\nEnter 1 for Write Queries");
                                                System.out.println("\nEnter 2 for Export");
                                                System.out.println("\nEnter 3 for Data Model");
                                                System.out.println("\nEnter 4 for Analytics");
                                                System.out.println("\nEnter 5 to Exit");
                                                db_option = console.nextInt();
                                                switch (db_option) {
                                                        // Write Queries Function
                                                        case 1:
                                                                System.out.println(
                                                                                "\nEnter Query or Transaction, Enter x to Exit:");
                                                                while (!Query.equals("x")) {
                                                                                Query = c.nextLine();
                                                                                if (!Query.equals("x")) {
                                                                                System.out.println(Query);
                                                                        }
                                                                }
                                                                break;
                                                        // Export Query Function        
                                                        case 2:
                                                                System.out.println("\nExport Function Exectution");
                                                                break;
                                                        // ERD Function                
                                                        case 3:
                                                                System.out.println("\nEnter Database Name to generate ERD");
                                                                Query = c.nextLine();
                                                                System.out.println("\nERD:");
                                                                erdGenerator.generateERD(Query);
                                                                break;
                                                        // Analytics Function                        
                                                        case 4:
                                                                System.out.println("\nAnalytics Function Exectution");
                                                                break;
                                                        // Exit        
                                                        case 5:
                                                                System.out.println("\nExiting......");
                                                                break;
                                                        default:
                                                                System.out.println("\nInvalid input");
                                                }
                                                System.out.print("\n");
                                        }
                                        break;
                                case 2:
                                        System.out.println("\nset your name: ");
                                        userName = c.nextLine();
                                        System.out.println("\nset your password: ");
//                                        ch = c.nextLine();
                                        pass =c.nextLine();
//                                        pass = String.valueOf(ch);
                                        System.out.println("\nEnter password once again: ");
//                                        ch1 = c.readPassword();
                                        passConfirm=c.nextLine();
//                                        passConfirm = String.valueOf(ch1);
                                        System.out.println(pass);
                                        System.out.println(passConfirm);
                                        while (!pass.equals(passConfirm)) {
                                                System.out.println("\nPasswords did not matched");
                                                System.out.println("\nset your password: ");
//                                                ch = c.readPassword();
                                                 pass=c.nextLine();
//                                                pass = String.valueOf(ch);
                                                System.out.println("\nEnter password once again: ");
//                                                ch = c.readPassword();
                                                passConfirm=c.nextLine();
//                                                passConfirm = String.valueOf(ch);
                                        }
                                        System.out.println("\nAnswer the following Security Question:");
                                        System.out.println("\nWhat primary school did you attend?");
                                        securityAnswer = c.nextLine();
                                        reg.setRegister(userName, pass, securityAnswer);
                                        System.out.println("\nSucessfully Registered, Please Login now...");
                                        break;
                                case 3:
                                        System.out.println("\nExiting......");
                                        break;
                                default:
                                        System.out.println("\nInvalid input");
                        }
                        System.out.println();
                }

        }
}
