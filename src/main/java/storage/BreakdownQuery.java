package storage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class BreakdownQuery {

    private static String queryString = CONSTANTS.EMPTY_STRING;
    private static String regex = "[^\\s\"']+|\"[^\"]*\"|'[^']*'";

    public BreakdownQuery(String queryString){
        this.queryString = queryString;
    }

    public List<String> splitQueryIntoTokens(){
        
        List<String> tokensList = new ArrayList<>();
        Pattern pattern =  Pattern.compile(regex);
        Matcher matcher = pattern.matcher(queryString);

        while(matcher.find()){
            
            String token = matcher.group();
            tokensList.add(token);
        }

        tokensList = refactorNullOrEmpty(tokensList);
        tokensList = refactorCommaValues(tokensList);

        return tokensList;

    }

    public void printTokens(List<String> tokens){

        int count = 1;
        for(String token : tokens) {
            System.out.println((count++)+ CONSTANTS.DOUBLE_TAB + " \"" + token+"\"");
        }
    }

    private List<String> refactorNullOrEmpty(List<String> tokens){

        List<String> processedTokens = new ArrayList<>();

        for(String token : tokens){

            if(token.length() > 0) {
                token = token.trim();

                if(token.length() > 0){
                    processedTokens.add(token);
                }
            }
        }

        return processedTokens;
    }

    private List<String> refactorCommaValues(List<String> tokens){

        List<String> processedTokens = new ArrayList<>();

        for(String token : tokens){
            processedTokens.addAll(removeSpecialCharacter(token));
        }
        return processedTokens;
    }

    private List<String> removeSpecialCharacter(String token){

        List<String> processed = new ArrayList<>();

        if(token.length()<=1){

            processed.add(token);
            return processed;
        }

        int startIndex = 0;
        int lastIndex = token.length()-1;

        if(token.charAt(startIndex)==',' || token.charAt(startIndex) == '(' || token.charAt(startIndex) == ')' || token.charAt(startIndex) == '\''){

            processed.add(Character.toString(token.charAt(startIndex)));
            startIndex = 1;
        }
        if(token.charAt(lastIndex)==',' || token.charAt(lastIndex) == '(' || token.charAt(lastIndex) == ')' || token.charAt(lastIndex) == '\'' || token.charAt(lastIndex) == ';'){

            processed.addAll(removeSpecialCharacter(token.substring(startIndex, lastIndex)));
            processed.add(Character.toString(token.charAt(lastIndex)));
        }
        else{

            processed.add(token.substring(startIndex, lastIndex+1));
        }

        return processed;
    }

}
