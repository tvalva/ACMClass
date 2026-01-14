package edu.odu.class_apps;

/**
 * TrainerStub.java - stub class for the Trainer module
 *
 */
public class TrainerStub
{
    public static void main(String[] args)
    {
        System.out.println("This is a stub class for the Trainer module.");
        ProcessFile fileProc  = new ProcessFile();
        
        //set the classification method
        fileProc.classifyMethod = ProcessFile.METHOD_NAIVE_BAYES;

        //now train and save the model (this is really slow)
        if (!fileProc.TrainAndSaveModel())
        {
            System.out.println("\nFailed to train and save model");
            System.exit(0);
        }
        else
        {
            System.out.println("\nModel trained and saved successfully");
        }

    }//end main
}//end class TrainerStub