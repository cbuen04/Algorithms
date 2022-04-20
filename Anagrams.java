import java.util.*;

/*
    This class takes in an input from the user containing words and determines
    how many groups of anagrams they contain if any.
    @author Charly Bueno
 */
public class Anagrams {

    private static List<String> words = new ArrayList<>();
    private static int numOfWords = 0;
    private static int lenOfWords = 0;

    public static void main(String [] args) throws Exception {

        // must begin reading the data
        Scanner input = new Scanner(System.in);

        // first line contains n and k separated by space where n is number of words per line and k is length of letters
        //      Potential edge cases, each word is not the same size
        //System.out.println("enter number of words followed by length separated by one space");
        String sizes = input.nextLine();
        String[] vals = sizes.split(" ");
        try {
            numOfWords = Integer.parseInt(vals[0]);
            lenOfWords = Integer.parseInt(vals[1]);
        }
        catch (Exception e){
            throw new Exception("numbers were in an improper format");
        }

        // next read each line preferably load into a list maybe
        while(numOfWords > 0){
            String word = input.nextLine();
            if(word.length() != lenOfWords){
                throw new Exception("Error word is not of the correct length");
            }
            else{
                //sort words before adding to the list
                char[] w = word.toCharArray();
                Arrays.sort(w);
                word = new String(w);
                words.add(word);
            }
            numOfWords--;
        }

        // iterate through the list and check for anagram groups
        int groups = anagramGroups();
        System.out.println(groups);
    }

    public Anagrams(){

    }

    /*
     * This method scans a list and determines if there are anagrams in the given list
     * @return
     */
    public static int anagramGroups(){
        HashMap<String, Integer> mappedWords = new HashMap<>();
        int groups = 0;
        for (String word : words) {
            if(mappedWords.containsKey(word)){
                //retrieve the current count of the word
                int presentCount = mappedWords.get(word);
                //add the new number of occurrences of the anagram into the map
                mappedWords.replace(word, presentCount + 1);
            }
            else{
                mappedWords.put(word, 1);
            }
        }

        for (Map.Entry<String, Integer> pair: mappedWords.entrySet()) {
            if(pair.getValue() == 1){
                continue;
            }
            else{
                groups++;
            }
        }

        return groups;
    }


}
