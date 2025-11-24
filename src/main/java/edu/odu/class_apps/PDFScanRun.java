package edu.odu.class_apps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.odu.cs.cs350.categorization.trainingData.TrainingData;
/**
 * PDFScanRun.java - tests the PDF word extractor
 *
 */
public class PDFScanRun
{
    public static void main( String[] args )
    {
       List<String> catFiles = new ArrayList<>();
       
         // this try block reads the odu training directory listing and places each entry into a ArrayList
        InputStream inData = TrainingData.class.getResourceAsStream(TrainingData.resourcePath+TrainingData.directory);
        if (inData == null) 
        {
            System.out.println("\nFailed to read training data: resource not found");
        } 
        else 
        {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inData))) 
            {
                String entryLine;
                
                //read the training data directory and populate an array list            
                while ((entryLine = reader.readLine()) != null) 
                {
                    catFiles.add(entryLine);
                    //System.out.println("\n"+catFiles.get(idx++) );
                }
            }     
            catch( IOException e )
            {
                    System.out.println("\nFailed to read training data: " + e.getMessage());
            }    
        }//end else

        //This section gets an entry in the ArrayList of PDF files and extracts the data
        String resourceFile;
        String dataLine = catFiles.get(0); //start with the first entry
        String Category = dataLine.split("/")[0];
        String currCat;

        //instantiate the fille processor, set the initial category
        ProcessFile fileProc  = new ProcessFile();
        fileProc.InitializeARRF();
        fileProc.currCategory = Category;

        fileProc.ApplyStems();


        //spin through the list of files until the category changes
        for (int entryCount = 0; entryCount < catFiles.size(); entryCount++)
        {
            dataLine = catFiles.get(entryCount);
            currCat  = dataLine.split("/")[0];

            //check for category change
            if (currCat == null ? Category != null : !currCat.equals(Category)) 
            {
                dataLine = catFiles.get(entryCount); 
                Category = dataLine.split("/")[0];
                //currCat  = dataLine.split("/")[0];
                fileProc.currCategory = Category;
                fileProc.wordCounts.clear();
                fileProc.stemCounts.clear();
            }    

            //get the file indicated in the directory and process it 
            resourceFile = TrainingData.resourcePath + dataLine;
            fileProc.resourceFile = resourceFile;
            fileProc.ProcessDocText();
        }// end for
         
         //create the ARRF file entries for this category REFACTOR: UNCOMMENT
         fileProc.CreateARRFCategoryEntries();
    } //end main
  
 }//end PDFScanRun