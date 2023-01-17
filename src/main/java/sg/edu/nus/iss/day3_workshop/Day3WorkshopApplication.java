package sg.edu.nus.iss.day3_workshop;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileWriter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// https://kodejava.org/how-do-i-create-and-write-data-into-text-file/
// https://makeinjava.com/create-new-file-folder-directory-java-example/
// https://www.geeksforgeeks.org/different-ways-reading-text-file-java/
// https://www.baeldung.com/java-write-to-file
// https://www.tutorialspoint.com/how-to-get-list-of-all-files-folders-from-a-folder-in-java
// https://www.java67.com/2015/06/how-to-write-to-file-in-java-using-bufferedwriter.html#:~:text=How%20to%20write%20to%20File%20in%20Java%20using,object%20as%20shown%20below%20%3A%20...%20More%20items

@SpringBootApplication
public class Day3WorkshopApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Day3WorkshopApplication.class, args);

        // File file = new File(dirPath + "\\write.txt");

        // try (Writer writer = new BufferedWriter(new FileWriter(file))) {
        // String contents = "The quick brown fox" +
        // System.getProperty("line.separator") + "jumps over the lazy dog.";

        // writer.write(contents);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        // Note: Double backquote is to avoid compiler interpret words
        // like \test as \t (ie. as a escape sequence)
        // create in root directory
        String dirPath = "\\data2";
        String stringLogin = "";

        File newDirectory = new File(dirPath);
        if (newDirectory.exists()) {
            System.out.print("Directory already exists.");
        } else {
            newDirectory.mkdir();
        }

        System.out.println("Welcome to your shopping cart");

        List<String> cartItems = new ArrayList<String>();

        Console cons = System.console();
        String input = "";
        while (!input.equals("quit")) {
            input = cons.readLine("What do you like to do? (type 'help' to show list of commands)");

            if (input.equals("help")) {
                System.out.println("'list' to show a list of items in shopping cart");
                System.out.println("login <name> to access your cart");
                System.out.println("add <item name>");
                System.out.println("delete <item number>");
                System.out.println("'quit' to exit program");
            }

            if (input.startsWith("login")) {
                input = input.replace(',', ' ');

                Scanner scan = new Scanner(input.substring(6));

                while (scan.hasNext()) {
                    stringLogin = scan.next();
                }

                File loginFile = new File(dirPath + File.separator + stringLogin);

                // Create new file under specified directory
                boolean isCreated = loginFile.createNewFile();
                if (isCreated) {
                    System.out.printf("\n2. Successfully created new file, path:%s", loginFile.getCanonicalPath());
                } else { // File may already exist
                    System.out.printf("\n2. Unable to create new file");
                }
            }

            if (input.equals("users")) {
                //Creating a File object for directory
                File directoryPath = new File(dirPath);

                //List of all files and directories
                String contents[] = directoryPath.list();
                System.out.println("List of files and directories in the specified directory:");
                for(int i=0; i<contents.length; i++) {
                    System.out.println(contents[i]);
                }
            }

            if (input.equals("list")) {
                cartItems = new ArrayList<String>();

                // File path is passed as parameter
                File file = new File(dirPath + File.separator + stringLogin);

                // Creating an object of BufferedReader class
                BufferedReader br = new BufferedReader(new FileReader(file));

                // Declaring a string variable
                String st;
                // Condition holds true till there is character in a string
                while ((st = br.readLine()) != null) {
                    // Print the string
                    System.out.println(st);

                    cartItems.add(st);
                }

                br.close();

                // if (cartItems.size() > 0) {
                //     // for (String item: cartItems)
                //     // System.out.printf("%s\n", item);

                //     for (int i = 0; i < cartItems.size(); i++)
                //         System.out.printf("%d: %s\n", i, cartItems.get(i));
                // } else {
                //     System.out.println("Your cart is empty");
                // }
            }

            
            if (input.startsWith("add")) {
                input = input.replace(',', ' ');

                String stringVal = "";
                Scanner scan = new Scanner(input.substring(4));

                FileWriter fileWriter = new FileWriter(dirPath + File.separator + stringLogin);
                PrintWriter printWriter = new PrintWriter(fileWriter);

                while(scan.hasNext()) {
                    stringVal = scan.next();
                    System.out.printf("%s\n", stringVal);
                    cartItems.add(stringVal);

                    printWriter.printf("%s\n", stringVal);    
                }
                printWriter.flush();
                fileWriter.flush();
                printWriter.close();
                fileWriter.close();
            }


            if (input.startsWith("delete")) {
                String stringVal = "";
                Scanner scan = new Scanner(input.substring(6));

                while(scan.hasNext()) {
                    stringVal = scan.next();

                    int listItemIndex = Integer.parseInt(stringVal);

                    if (listItemIndex < cartItems.size()) {
                        cartItems.remove(listItemIndex);
                    } else {
                        System.out.println("Incorrect item index");
                    }
                }

                FileWriter fileWriter = new FileWriter(dirPath + File.separator + stringLogin, false);
                BufferedWriter bwr = new BufferedWriter(fileWriter); 
                
                int listCount = 0;
                while (listCount < cartItems.size()) {
                    bwr.write(cartItems.get(listCount));
                    bwr.newLine();
                    listCount++;
                }                
                bwr.flush();
                fileWriter.flush();
                bwr.close();
                fileWriter.close();

                
                fileWriter.close();
            }

        }
    }

}
