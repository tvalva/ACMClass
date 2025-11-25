package edu.odu.class_apps;
import java.util.HashMap;
import java.util.Map;

public class WordCounter 
{
    private final String[] wordBuff;

    public Map<String, Integer> wordCounts;

    // constructor
    public WordCounter(String[] wordData)
    {
        this.wordBuff = wordData;
    }    

    public void countAndPrint() 
    {
        //make this a public class variable of ProcessFile()
        wordCounts = new HashMap<>();

        for (String wordOcc : wordBuff) 
        {
            if (wordOcc == null) 
               continue;

            String word = wordOcc.toLowerCase();
            //only take words that are 4 or more characters
            if (word.length() >= 4)
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
        }

     }//end countAndPrint 

}//end WordCounter
