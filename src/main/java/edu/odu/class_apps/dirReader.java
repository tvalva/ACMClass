package edu.odu.class_apps;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class dirReader 
{
  public Path directoryPath;
    
  public boolean DirProcessList() 
  { 
    Path dir = this.directoryPath;

    //instantiate the file processor in classification mode
    ProcessFile fileProc  = new ProcessFile();
    fileProc.IOMode = 1; 

        try (Stream<Path> stream = Files.list(dir)) 
        {
            stream.filter(Files::isRegularFile).forEach(path ->
            {     
                try (FileInputStream fis = new FileInputStream(path.toFile())) 
                { 
                   //process each file here
                    System.out.println("Processing file: " + path.getFileName());
                    
                    //initialize the ARRF file
                    if(!fileProc.InitializeARRF())
                    {
                        System.out.println("\nFailed to initialize ARFF file");
                        System.exit(0);
                    }

                    //set the input stream for the pdf file and process the text
                    fileProc.pdfInStream = fis;
                    fileProc.ProcessDocText();

                    //set the output filename and initial category
                    fileProc.outputFile   = "individual.arff";
                    fileProc.currCategory = "Uncategorized";
                    
                    if(!fileProc.CreateARRFCategoryEntries())
                    { 
                      System.out.println("\nFailed to create ARFF entries");
                      System.exit(0);
                    }

                    //save the ARFF file
                    if (!fileProc.SaveARFFFile())
                    {
                        System.out.println("\nFailed to save ARFF file");
                        System.exit(0);
                    } 

                    //classify the document using the saved model
                    if (!fileProc.ClassifyWithModel())
                    {
                        System.out.println("\nFailed to classify document");
                        System.exit(0);
                    }              

                    //reset any variables as needed before processing the next file 
                    fileProc.stemCounts.clear();
                    fileProc.wordCounts.clear();
                    fis.close(); 
                    Files.deleteIfExists(Paths.get("individual.arff"));

                } 
                catch (IOException e) 
                { 
                   System.out.println("error"+e.getMessage());
                    System.exit(0);
                }
            });
        }
        catch (IOException e)
        {
            System.out.println("error"+e.getMessage()); 
            return false;
        }
    return true; 
  }//end DirProcessList()
}// End of dirReader.java

