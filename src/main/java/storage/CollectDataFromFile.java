package storage;

import java.io.*;
import java.util.Collections;
import java.util.List;

public class CollectDataFromFile {

    public static List<String> tokenList = null;

    public static List<String> CollectDataFromFile(String filePath){

        FileReader fileReader = null;

        try {
            File file = new File(filePath);
            if(!file.exists()){
                return Collections.emptyList();
            }
            fileReader = new FileReader(filePath);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert fileReader != null;
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String value = null;

        while(true){

            try {
                if ((value = bufferedReader.readLine()) == null) {
                    break;
                }

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            stringBuilder.append(value);
        }

        String fileData = stringBuilder.toString();
        BreakdownQuery breakdownQuery = new BreakdownQuery(fileData);
        List<String> tokens = breakdownQuery.splitQueryIntoTokens();

        CollectDataFromFile.tokenList = tokens;
        return tokens;
    }

    public static List<String> getSubTokensList(List<String> tokens, int startIndex, int endIndex){
        if(tokens == null || startIndex<0 || endIndex<0 || startIndex>=endIndex || endIndex > tokens.size()){
            return Collections.emptyList();
        }
        return tokens.subList(startIndex, endIndex);
    }
    public static List<String> getNextTokenList(List<String> tokens){
        if(tokens == null || tokens.size() == 1){
            return Collections.emptyList();
        }
        return tokens.subList(1, tokens.size());
    }

    public static int getIndexFromFront(List<String> tokens, String value){
        if(tokens == null){
            return -1;
        }
        int index = 0;

        for( index = 0; index < tokens.size(); index++){
            if(tokens.get(index).equalsIgnoreCase(value)){
                return index;
            }
        }
        return -1;
    }

    public static int getIndexFromReverse(List<String> tokens, String value){
        if(tokens == null){
            return -1;
        }
        int index = 0;

        for( index = tokens.size()-1; index >= 0; index--){
            if(tokens.get(index).equalsIgnoreCase(value)){
                return index;
            }
        }
        return -1;
    }

    public static List<String> getNextTokenList(){
        return getNextTokenList(CollectDataFromFile.tokenList);
    }

    public static boolean isKeyword(String valToCheck){
        String compareVal = "{[,:'";
        return CollectDataFromFile.tokenList != null && !compareVal.contains(valToCheck);
    }

    public static String getNextKeyword(){
        while (true){
            String keyword = getFirstToken();
            if(keyword == null){
                return null;
            }
            CollectDataFromFile.tokenList = CollectDataFromFile.getNextTokenList();
            if(isKeyword(keyword)){
                return keyword;
            }
        }
    }
    private static String getFirstToken(){
        if(tokenList == null || tokenList.isEmpty()){
            return null;
        }
        return CollectDataFromFile.tokenList.get(0);
    }
}
