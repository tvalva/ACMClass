package edu.odu.class_apps;

import java.util.Arrays;
import java.util.List;

public class StopList 
{
     // populate stop word list
    public static final List<String> stopWords = Arrays.asList(
        "a","an","the","and","or","but","if","while","when","then","so","because","as","since",
        "until","unless","whether","both","either","neither","nor",
        "i","me","my","we","us","our","yours","you","your","he","him","his","she","her","hers",
        "it","its","they","them","their","this","that","these","those","each","every","any","some",
        "few","several","many",
        "be","am","is","are","was","were","been","being","have","has","had","do","does","did",
        "will","would","shall","should","can","could","may","might","must",
        "in","on","at","by","for","with","about","against","between","into","through","during",
        "before","after","above","below","to","from","of","up","down","over","under",
        "get","got","make","made","go","went","come","came","say","said","see","seen","know","known",
        "like","just","also","very","now","then","here","there","still","even","really",
        "one","two","three","four","five","six","seven","eight","nine","ten","first","second","third",
        "january","february","march","april","may","june","july","august","september","october","november","december",
        "k","m","kg","lb","g","cm","mm","s","ms","hr","hrs",
        ".",",",":",";","?","!","-","--","—","(",")","[","]","{","}","\"","'","`","@","#","$","%","&","*","+","=","/","\\",
        "http","https","www","com","net","org","edu","gov","email","mailto","username","userid",
        "click","link","subscribe","sign","login","logout","home","search","terms","privacy","copyright","contact",
        "follow","share","read","more","continue",
        "not","no","neither","nor","never","none",
        "abstract","introduction","conclusion","references","todo","fixme","note"
    );

    public List<String> stopWordsMutable;

    /** constructor - creates a mutable list from the static one */
    public StopList() 
    {
        // Mutable ArrayList copy you can modify at runtime
         stopWordsMutable = stopWords;
        
    }

    // dump the stop word list to console
    public void ShowList()
    {
        for (String word : stopWordsMutable) 
        {
            System.out.println(word);
        }
    }//end ShowList

    // test a word against the stop word list
     public boolean testWord(String inWord)
    {
        //run through the list and return true if a word matches
        for (String word : stopWordsMutable) 
        {
            if (inWord.equalsIgnoreCase(word))
            {
                //System.out.println(inWord+" is a stop word.");
                return(true);
            }

        }//end for
        return(false);
    }//end testWord

}//end class StopList
