package edu.odu.class_apps;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import edu.odu.cs.cs350.categorization.trainingData.TrainingData;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stemmers.SnowballStemmer;
import weka.filters.unsupervised.attribute.StringToNominal;
import weka.filters.Filter;

/* ProcessFile.java - processes a PDF file to extract words and count occurrences
 */

public class ProcessFile
{
    //public class variables
    public String resourceFile = ""; //TrainingData.resourcePath + dataLine;
    public Map<String, Integer> wordCounts;
    public Map<String, Integer> stemCounts;
    public String pdfWords;
    public String currCategory;
    public String outputFile;
    public int IOMode;
    public InputStream pdfInStream;
     
    //private class variables
    private String[] wordTokens;
    private String[] cleanTokens;
    private InputStream pdfIn;
    private Attribute nameAttr;
    private Attribute occurrenceAttr;
    private Attribute categoryAttr;
    private ArrayList<String> categories;
    private Instances dataset;
    
    //class constructor
    public ProcessFile()
    {
        this.wordCounts = new HashMap<>();
        this.stemCounts = new HashMap<>();
    }
    
    public void ProcessDocText()
    {
        //might need a mode here, train versus classify
        if (IOMode == 0) //training mode
            pdfIn = TrainingData.class.getResourceAsStream(resourceFile);
        else //classification mode
            pdfIn = pdfInStream;
    
        //use the adobe library functions to extract text from the pdf
        ExtractText();
       
        //create an array of words only for classification processing
        // "[^A-Za-z]+" regex, words no digits
        // "\\p{L}" unicode version for non-english alphabets
        wordTokens  = pdfWords.split("[^A-Za-z]+");
        cleanTokens = new String[wordTokens.length];

        //use the StopList class
        StopList stopList = new StopList();

        //apply the stop words
        int inx = 0;
        for (String word : wordTokens) 
        {      
            //if there's a word, and it's not in the stop list, append to the clean list
            if (!word.isEmpty() && !stopList.testWord(word)) 
            {
                cleanTokens[inx++]= word;
            }
        } //end for each word 
      
        //create a hashmap of word counts
        CountAndHash();
        
    }// end ProcessDocText
    
    public void CountAndHash()   
    {
        for (String wordOcc : cleanTokens) 
        {
            if (wordOcc == null) 
               continue;

            String word = wordOcc.toLowerCase(); // optional normalization
            wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
        }//end for

}//end countAndHash

  // Public method to extract text, pdfbox throws a lot of warnings
   public boolean ExtractText() 
   { 
      try
        {   
            PDDocument doc = PDDocument.load(this.pdfIn);
            PDFTextStripper stripper = new PDFTextStripper();  
            pdfWords = stripper.getText(doc);
            pdfIn.close();
            return(true);
        } 
        catch (IOException e) 
        {
            System.err.println("Failed to extract text: " + e.getMessage()); 
            return(false);
        }
   
     }//end ExtractText

 public boolean CreateARRFCategoryEntries()
 {
    //create word stems
    if (!ApplyStems())
    {
        System.err.println("Failed to apply stems to words.");
        return(false);
    }

    for (HashMap.Entry<String, Integer> entry : stemCounts.entrySet()) 
	{
        DenseInstance instance = new DenseInstance(dataset.numAttributes());
        dataset.setClassIndex(dataset.numAttributes() - 1); 

        // Populate dataset - this would have to be done for each category
        instance.setValue(nameAttr, entry.getKey());
        instance.setValue(occurrenceAttr, entry.getValue());
        instance.setValue(categoryAttr, currCategory);
        dataset.add(instance);
    }//end for
 
    return true;
  }//end hashmaptoarf

    //method to create the ARRF file 
    public boolean SaveARFFFile()
    {  
        // Save to ARFF
        ArffSaver saver = new ArffSaver();
        saver.setInstances(dataset);
        try 
        {
            //saver.setFile(new File("ACMoutput.arrf"));
            saver.setFile(new File(outputFile));
            saver.writeBatch(); 
        } 
        catch (IOException e) 
        {
            System.err.println("Failed to creat file :"+outputFile+" " + e.getMessage()); 
            return(false);
        }
 
        System.out.println("ARFF file created: " + outputFile);
        return(true);

  }//end SaveARFFFile

  //method to apply word stems
  public boolean ApplyStems()
  {
    // Create a Snowball stemmer
    //weka.core.stemmers.SnowballStemmer stemmer = new weka.core.stemmers.SnowballStemmer();
    //had to download the stemmer and place it in the resource dir in this project
    //added maven dependency for weka-stemmers in the pom.xml that points directly to it

    SnowballStemmer stemmer = new weka.core.stemmers.SnowballStemmer();

    /** testing for available stemmers
    Enumeration<String> options = weka.core.stemmers.SnowballStemmer.listStemmers();
    weka.core.stemmers.SnowballStemmer.listStemmers();
    while (options.hasMoreElements()) 
        {
            String name = options.nextElement();
            System.out.println("Available stemmer: " + name);
        }
     */

    stemmer.setStemmer("porter"); 
    
    for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) 
    {
        String word = entry.getKey();
        String stemmedWord = stemmer.stem(word);
        int count = entry.getValue();
    
        // Update stemCounts map
        stemCounts.put(stemmedWord, stemCounts.getOrDefault(stemmedWord, 0) + count);
     }//end for     

     return true;
    }// end ApplyStems


//initialize the ARRF dataset and attributes
public boolean InitializeARRF()
{
    ArrayList<Attribute> attributes = new ArrayList<>();

    // String attribute for name
    nameAttr       = new Attribute("name", (List<String>) null);
    occurrenceAttr = new Attribute("occurrence");

    //array list of categories
    categories = new ArrayList<>();
    categories.add("Applied_computing");
    categories.add("Computer_systems_organization");
    categories.add("Computing_and_methodologies");
    categories.add("Data");
    categories.add("General_and_reference");
    categories.add("Hardware");
    categories.add("Information_systems");
    categories.add("Mathematics_of_computing");   
    categories.add("Social_and_professional_topics");
    categories.add("Software_and_its_engineering");
    categories.add("Theory_of_computation");

    //set the attributes
    categoryAttr = new Attribute("category", categories);
    attributes.add(nameAttr);
    attributes.add(occurrenceAttr);
    attributes.add(categoryAttr);
    
    //initialize the dataset
    dataset = new Instances("ACM_Classifier", attributes, stemCounts.size());
 
    return true;
}//end InitializeARRF


public boolean TrainAndSaveModel()
{
    try
    {
        //check the absolute path
        String input ="ACMoutput.arff";
        Path path = Paths.get(input);
        if (!path.toFile().exists())
        {
            System.out.println("The specified path does not exist.");
            System.exit(0);
        }
        else
        {
            System.out.println("Absolute path: " + path.toAbsolutePath());
        }
                
        //load the dataset using the absolute path
        DataSource source = new DataSource(path.toAbsolutePath().toString());
        Instances  data   = source.getDataSet();

        //check that data loaded
        if (data != null)
        {
            System.out.println("Dataset loaded with " + data.numInstances() + " instances.");
        }
        else
        {
            System.out.println("Failed to load dataset.");
            return false;
        }
    
        // Set the class index (the attribute to predict)
        if (data.classIndex() == -1) 
        {
            data.setClassIndex(data.numAttributes() - 1); // usually last attribute
        }

        //Apply StringToNominal filter before training
        StringToNominal filter = new StringToNominal();
        filter.setAttributeRange("first-last"); // convert all string attributes
        filter.setInputFormat(data);
        Instances filteredData = Filter.useFilter(data, filter);

        //Choose and train a classifier
        Classifier classifier = new J48(); // you can swap in NaiveBayes, RandomForest, etc.
        classifier.buildClassifier(filteredData);

        //Save the trained model to a .model file
        ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("ACMmydataset.model"));
        out.writeObject(classifier);
        out.close();
    }
    catch (Exception e)
    {
        System.out.println("Failed to load dataset: " + e.getMessage());
        return false;
    }
    System.out.println("Model saved successfully");
    return true;
  }// end TrainAndSaveModel

public boolean ClassifyWithModel() 
{
    try
    {
         // 1. Load the ARFF file containing the instances to classify
         DataSource source = new DataSource("individual.arff");
         Instances data = source.getDataSet();

         if (data == null) 
         {
             System.out.println("Failed to load dataset for classification.");
             return false;
         }

        //apply filters
        StringToNominal filter = new StringToNominal();
        filter.setAttributeRange("first-last"); // convert all string attributes
        filter.setInputFormat(data);
        Instances filteredData = Filter.useFilter(data, filter);


         // Set the class index (same as training ARFF)
         if (filteredData.classIndex() == -1) 
         {
             filteredData.setClassIndex(data.numAttributes() - 1);
         }

         //Load the trained model
         ObjectInputStream in = new ObjectInputStream(new FileInputStream("ACMmydataset.model"));
         Classifier cls = (Classifier) in.readObject();
         in.close();

         // Classify each instance
         for (int i = 0; i < data.numInstances(); i++) 
         {
             double labelIndex = cls.classifyInstance(filteredData.instance(i));
             String predictedClass = filteredData.classAttribute().value((int) labelIndex);
             System.out.println("Instance " + i + " classified as: " + predictedClass);
         }
    }
    catch (Exception e) 
    {
        System.out.println("Classification failed: " + e.getMessage());
        return false;
    }
    return true;
}

}// end class ProcessFile 