package edu.odu.class_apps;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * PDFClassify.java - reads files from a directory and applis ACM classifications
 *
 */
public class PDFClassify
{
    public static void main( String[] args )
    {
        //get filepath from the command line '/src/main/resources'
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the file path dir/dir2/.../: ");
        String input = scanner.nextLine();

        //check for valid input
        Pattern pattern = Pattern.compile("^([^/]+/)+$");
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches())
        {
            System.out.println("The input is not a valid directory path. Use dir/dir2/.../");
            System.exit(0);
        }

        //check the absolute path
        Path path = Paths.get(input);
        if (!path.toFile().exists())
        {
            System.out.println("The specified path does not exist.");
            scanner.close();
            System.exit(0);
        }
        else
        {
           
        }
        scanner.close();
       
        dirReader dirPrc = new dirReader();
        dirPrc.directoryPath = path;
        if (!dirPrc.DirProcessList())
        {
            System.out.println("Failed to process directory");
            System.exit(0);
        }
     }//end main
 }//end PDFClassify class