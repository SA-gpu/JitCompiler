/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilercons;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author tahah
 */
class LexicalAnalyzer {

    public static String[] classPart;
    public static String[] valuePart;
    public static String[] lineNumber;
    public static String indetifierRE = "[^_\\w ? ^_$\\w ? ^$_\\w]$";
    public static String ReserveWordRE = "^[a-zA-Z_$][\\w_|\\w$]*$";
    public static String FloatRE = "(\"[+-]?[0-9]{0,}\\\\.[0-9]{0,}([eE][+-]?[0-9]{0,})?\")";
    public static String IntRE = "^[\\+\\-]?[\\d]+$";
    public static String StringRE = "(?:(\"|\')(.*?[^\\\\])\")";
    public static String CharRE = "(?:(\"|\')(.*?[^\\\\])\")";

    public static int index = 0;
    public static String input[];
    public static String Lno[];

    public static String Simulate(String input) {

        String myInput = input;
        ArrayList<TokenClass> list = CharacterBreaker(myInput);
        classPart = new String[list.size()];
        valuePart = new String[list.size()];
        lineNumber = new String[list.size()];

        int counter = 0;
        StringBuilder sb = new StringBuilder();
        for (TokenClass forWord : list) {
            GenerateToken(forWord);
            sb.append(String.format("(%s,%s,%s)\n", forWord.token, forWord.getWord(), forWord.getLineNo()));
            classPart[counter] = forWord.token;
            valuePart[counter] = forWord.word;
            lineNumber[counter++] = String.valueOf(forWord.line);

        }

//        System.out.println("ths is class prt ");
//        int i;
//        for(i=0;i<classPart.length;i++){
//            System.out.println(classPart[i]);
//        }
        return sb.toString();

    }

    static void DisplayClassPart() {
        for (int i = 0; i < classPart.length; i++) {
            System.out.println(classPart[i]);
        }
//        for(String x:classPart){
//            System.out.println(x);
//        }

    }

    private static ArrayList<TokenClass> CharacterBreaker(String input) {
        ArrayList<TokenClass> HoldWord = new ArrayList<TokenClass>();
        StringBuilder tempWord = new StringBuilder();
        int line = 1;

        char HoldChar;
        boolean isString = false;
        for (int counter = 0; counter < input.length(); counter++) {
            HoldChar = input.charAt(counter);
            // check for identifier
            if ((HoldChar >= 'A' && HoldChar <= 'Z') || (HoldChar >= 'a' && HoldChar <= 'z')
                    || HoldChar == '_' || HoldChar == '$') {
                if (((MatchesRE(tempWord.toString(), indetifierRE)) && !isString)) {
                    HoldWord.add(new TokenClass(tempWord.toString(), line));
                    tempWord.setLength(0);

                }
                tempWord.append(HoldChar);

            }// yeh if end ho raha h 
            // check for number
            else if (HoldChar >= '0' && HoldChar <= '9') {
                if (!tempWord.toString().equals("") && !isString) {
                    if (!(tempWord.charAt(tempWord.length() - 1) >= '0' && tempWord.charAt(tempWord.length() - 1) <= '9')) {
                        HoldWord.add(new TokenClass(tempWord.toString(), line));
                        tempWord.setLength(0);
                    }
                }
                tempWord.append(HoldChar);

            } // else if khatm 
            // check for arthmetic operators
            else if (HoldChar == '+' || HoldChar == '-' || HoldChar == '*') {
                if (!tempWord.toString().equals("") && !isString) {
                    HoldWord.add(new TokenClass(tempWord.toString(), line));
                    tempWord.setLength(0);
                }
                if (tempWord.toString().equals("") && !isString) {
                    if (tempWord.toString().equals("")) {
                        tempWord.append(HoldChar); // add karwa dia 
                        if ((HoldChar == '+' && (input.charAt(counter + 1) == '+' || input.charAt(counter + 1) == '='))
                                || (HoldChar == '-' && (input.charAt(counter + 1) == '-' || input.charAt(counter + 1) == '='))
                                || (HoldChar == '=' && input.charAt(counter + 1) == '*')
                                || (HoldChar == '=' && input.charAt(counter + 1) == '/')
                                || (HoldChar == '=' && input.charAt(counter + 1) == '%')) {
                            HoldChar = input.charAt(counter + 1);
                            tempWord.append(HoldChar);
                            counter++;
                        }
                    }

                    if (!tempWord.toString().equals("") && isString == false) {
                        HoldWord.add(new TokenClass(tempWord.toString(), line));
                        tempWord.setLength(0);
                    }
                } else {
                    tempWord.append(HoldChar);
                }

            } // comment 
            else if (HoldChar == '#') {
                if (isString) {
                    tempWord.append(HoldChar);
                } else if (input.indexOf('\r', counter) != -1) {
                    counter = input.indexOf('\r', counter) - 1;
                } else {
                    counter = input.length() - 1;
                }
            } // check for space and tabs
            else if (HoldChar == ' '
                    || HoldChar == '\r'
                    || HoldChar == '\t') {
                if (!tempWord.toString().equals("") && !isString) {
                    HoldWord.add(new TokenClass(tempWord.toString(), line));
                    tempWord.setLength(0);

                }
                if (isString) {
                    tempWord.append(HoldChar);
                }

            } // else if khtm 
            // check for new line 
            else if (HoldChar == '\n') {
                if (!tempWord.toString().equals("") && !isString) {
                    HoldWord.add(new TokenClass(tempWord.toString(), line));
                    tempWord.setLength(0);

                }
                line++;
            } // for \\ : and ;
            else if (HoldChar == '\\' || HoldChar == ':' || HoldChar == ';') {
                if (!isString && !tempWord.toString().equals("")) {
                    HoldWord.add(new TokenClass(tempWord.toString(), line));
                    tempWord.setLength(0);
                }
                tempWord.append(HoldChar);
            } // . k liye 
            else if (HoldChar == '.'||HoldChar == '&'||HoldChar == '|') {
                if (!tempWord.toString().equals("") && !isString) {
                    HoldWord.add(new TokenClass(tempWord.toString(), line));
                    tempWord.setLength(0);
                }
                if (tempWord.toString().equals("") && !isString) {
                    if (tempWord.toString().equals("")) {
                        tempWord.append(HoldChar);
                        if ((HoldChar == input.charAt(counter + 1))) {
                            HoldChar = input.charAt(counter + 1);
                            tempWord.append(HoldChar);
                            counter++;
                        }

                    }
                    if (!tempWord.toString().equals("") && isString == false) {
                        HoldWord.add(new TokenClass(tempWord.toString(), line));
                        tempWord.setLength(0);

                    }
                } else {
                    tempWord.append(HoldChar);
                }
            } // for ! < > = / % 
            else if (HoldChar == '!'
                    || HoldChar == '<'
                    || HoldChar == '>'
                    || HoldChar == '='
                    || HoldChar == '/'
                    || HoldChar == '%') {
                if (!tempWord.toString().equals("")) {
                    HoldWord.add(new TokenClass(tempWord.toString(), line));
                    tempWord.setLength(0);
                }

                if (tempWord.toString().equals("") && !isString) {
                    if (tempWord.toString().equals("")) {
                        tempWord.append(HoldChar);
                        if (input.charAt(counter + 1) == '=') {
                            HoldChar = input.charAt(counter + 1);
                            tempWord.append(HoldChar);
                            counter++;
                        }
                    }

                    if (!tempWord.toString().equals("") && isString == false) {
                        HoldWord.add(new TokenClass(tempWord.toString(), line));
                        tempWord.setLength(0);
                    }
                } else {
                    tempWord.append(HoldChar);
                }
            } else if (HoldChar == '('
                    || HoldChar == ')'
                    || HoldChar == '{'
                    || HoldChar == '}'
                    || HoldChar == '['
                    || HoldChar == ']') {
                if (isString) {
                    tempWord.append(HoldChar);
                } else {
                    if (!tempWord.toString().equals("")) {
                        HoldWord.add(new TokenClass(tempWord.toString(), line));
                        tempWord.setLength(0);
                    }
                    HoldWord.add(new TokenClass(String.valueOf(HoldChar), line));
                }

            } else if (HoldChar == '"'||HoldChar == '\'') {
                if (isString == false) {
                    isString = true;
                    if (!tempWord.toString().equals("")) {
                        HoldWord.add(new TokenClass(tempWord.toString(), line));
                        tempWord.setLength(0);
                    }
                    tempWord.append(HoldChar);
                } else {
                    tempWord.append(HoldChar);
                    HoldWord.add(new TokenClass(tempWord.toString(), line));
                    tempWord.setLength(0);
                    isString = false;
                }
            } else if ((HoldChar > ' ' && HoldChar <= 'A')||(HoldChar > '[' && HoldChar <= '`')&&(HoldChar > '}' && HoldChar <= '~')  ) {
                if (!isString && !tempWord.toString().equals("")) {
                    HoldWord.add(new TokenClass(tempWord.toString(), line));
                    tempWord.setLength(0);
                }
                tempWord.append(HoldChar);
            } // for anything else
            else {
                HoldWord.add(new TokenClass(String.valueOf(HoldChar), line));
                tempWord.setLength(0);
            }

        }// loop khatm

        if (!tempWord.toString().equals("")) {
            HoldWord.add(new TokenClass(tempWord.toString(), line));
            tempWord.setLength(0);
        }

        return HoldWord;
    }

    private static void GenerateToken(TokenClass input) {
        String word = input.getWord();
        if (MatchesRE(word, ReserveWordRE)) {
            switch (word) {
                case "Start":
                    input.token = "Start";
                    break;
                case "Model":
                    input.token = "Model";
                    break;
                case "Main_Model":
                    input.token = "Main_Model";
                    break;
                case "Stop":
                    input.token = "Stop";
                    break;
                case "loop":
                    input.token = "loop";
                    break;
                case "if":
                    input.token = "if";
                    break;
                case "call":
                    input.token = "call";
                    break;
                case "or":
                    input.token = "or";
                    break;
                case "orIf":
                    input.token = "orIf";
                    break;
                case "var":
                    input.token = "DT";
                    break;
                case "void":
                    input.token = "void";
                    break;
                case "num":
                    input.token = "DT";
                    break;
                case "numf":
                    input.token = "DT";
                    break;
                case "word":
                    input.token = "DT";
                    break;
                case "sentence":
                    input.token = "DT";
                    break;
                case "$":
                    input.token = "$";
                    break;
                case "new":
                    input.token = "new";
                    break;
                case "break":
                    input.token = "break";
                    break;
                case "continue":
                    input.token = "continue";
                    break;
                case "case":
                    input.token = "case";
                    break;
                case "Open":
                    input.token = "Open";
                    break;
                case "Secrete":
                    input.token = "Secrete";
                    break;
                case "enum":
                    input.token = "DT";
                    break;
                case "return":
                    input.token = "return";
                    break;
                default:
                    input.token = "ID";
                    break;

            }
        } else if (word.equals("+") || word.equals("-")) {
            input.token = "PlusMinus";
        } else if (word.equals("*") || word.equals("/") || word.equals("%")) {
            input.token = "MDM";
        } else if (MatchesRE(word, IntRE)) {
            input.token = "IntConst";
        } else if (MatchesRE(word, FloatRE)) {
//            if (MatchesRE(word, "^[\\.]")) {
//                //input.word = Regex.Replace(word, "^[\\.]", "0.");
//            }
//            if (MatchesRE(word, "[\\.]$")) {
////                input.word = Replace(word, "[\\.]$", ".0");
//            }
            //input.token = "float_const";
            input.token = "FloatConst";
        } else if (MatchesRE(word, StringRE)) {
            input.token = "StringConst";
        }
         else if (isChar(word)) {
            input.token = "CharConst";
        }
//        else if (MatchesRE(word, CharRE)) {
//            input.token = "CharConst";
//        }
        else if (word.equals("=")
                || word.equals("=+")
                || word.equals("=-")
                || word.equals("=*")
                || word.equals("=/")
                || word.equals("=%")) {
            if (word.equals("=")) {
                input.token = word;
            } else {
                input.token = "AssignOpr";
            }
        } else if (word.equals("++") || word.equals("--")) {
            input.token = word;
        } 
//           else if (word.equals("==") || word.equals("!=")) {
//            input.token = "EqualOpr";
//        } 
         else if (word.equals("==") || word.equals("!=")||word.equals("<") || word.equals(">") || word.equals("<=") || word.equals(">=")) {
            input.token = "RelOpr";
        } else if (word.equals("&") || word.equals("!")) {
            input.token = word;
        } else if (word.equals("&&") || word.equals("||")) {
            input.token = "LogOpr";
        } else if (word.equals("(") || word.equals(")")
                || word.equals("{") || word.equals("}")
                || word.equals("[") || word.equals("]")) {
            input.token = word;
        } else if (word.equals(".") || word.equals(",") || word.equals(":") || word.equals(";")) {
            input.token = word;
        } else {
            input.token = "Invalid Token";
        }

    }
    
    public static boolean isChar(String input) {
        int state = 0;
        int ist = 0;
        int fstate = 4;
        char[] ch = input.toCharArray();

        for (int i = 0; i < ch.length; i++) {
            if (ist == 0 && state == 0 && ch[i] == '\'') {
                state = 1;
            } else if (ist == 0 && state == 1 && ch[i] != '\\') {
                state = 2;
            } else if (ist == 0 && state == 1 && ch[i] == '\\') {
                state = 3;
            } else if (ist == 0 && state == 3 && (ch[i] == '\\'
                    || ch[i] == '\'' || ch[i] == '\"'
                    || ch[i] == '0' || ch[i] == 'a'
                    || ch[i] == 'b' || ch[i] == 'f'
                    || ch[i] == 'n' || ch[i] == 'r'
                    || ch[i] == 't' || ch[i] == 'v'
                    || ch[i] == 'u' || ch[i] == 'U'
                    || ch[i] == 'x')) {
                state = 2;
            } else if (ist == 0 && state == 2 && ch[i] == '\'') {
                state = 4;
            } else if (ist == 0 && state == 2 && ch[i] != '\'') {
                state = 5;
            }
        }

        if (state == fstate) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean MatchesRE(String word, String re) {
        Pattern in = Pattern.compile(re);
        Matcher m1 = in.matcher(word);
        if (m1.matches() == false) {
            return false;
        }
        return true;
    }

    static void SyantaxAnalyzer() {
        input = new String[classPart.length];
        for (int i = 0; i < classPart.length; i++) {
            input[i] = classPart[i];
//            System.out.println(input[i]);
        }
        Lno = new String[lineNumber.length];
        for (int i = 0; i < lineNumber.length; i++) {
            Lno[i] = lineNumber[i];
//            System.out.println(Lno[i]);
        }

        if (S()) {
//            if (input.charAt(index) == '$') {
//                return true;
//            }
            System.out.println("Parsed successfully");
        } else {
            System.out.println("There is something wrong");
        }

    }

    private static boolean ClassMain() {
      //  if (input[index].equals("Model")) {
            if (input[index].equals("Main_Model")) {
                index++;
                if (input[index].equals("ID")) {
                    index++;
                    if (input[index].equals("{")) {
                        index++;
                        if (Body1()) {
                            if (Start()) {
                                if (input[index].equals("}")) {
                                    index++;
                                    return true;
                                }
                           }
                        }
                    }
                }
            }else if(input[index].equals("$")){
                return true;
            }
     //   }
        System.out.println("Error at Class_Main Line number " + Lno[index]);
        return false;
    }

    private static boolean Body1() {
        if (input[index].equals("DT") || input[index].equals("ID")  
                || input[index].equals("Open") || input[index].equals("Secrete")) {
            
            if (input[index].equals("DT")){
            if (Dec()) {
                if(Body1()){
                return true;
            }
            }
            } else if (input[index].equals("Open")||input[index].equals("Secrete")){
                if (Func()) {
                    if(Body1()){
                return true;
            }
            }
            }
            else if (input[index].equals("ID")){
                if(Dec_Obj()){
                    if(Body1()){
                return true;
                }
            }
        }
        }else if(input[index].equals("}")||input[index].equals("Start")){
            return true;
        }
        
        System.out.println("error at  Body1 line no " + Lno[index]);
        return false;
    }

    private static boolean Dec() {
       // if (input[index].equals("DT")) {
            
            if (input[index].equals("DT")) {
                index++;
                if (input[index].equals("ID")) {
                    index++;
                    if (ArrDash()) {
                        if (Init()) {
                            if (List()) {
                                return true;
                            }
                        }
                    }
                }
           }
     //   }
        System.out.println("Error at Dec Line number " + Lno[index]);
        return false;
    }

    private static boolean Func() {
        if (input[index].equals("Open") || input[index].equals("Secrete")) {

            if (Access_Mod()) {
                if (RET()) {
                    if (input[index].equals("ID")) {
                        index++;
                        if (input[index].equals("(")) {
                            index++;
                            if (ARG()) {
                                if (input[index].equals(")")) {
                                    index++;
                                    if (input[index].equals("{")) {
                                        index++;
                                        if (MST()) {
                                           if (Return()) {
                                            if (input[index].equals("}")) {
                                                index++;
                                                return true;
                                            }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Error at Func Line number " + Lno[index]);
        return false;
    }

    private static boolean Obj_dec() {
      //  if (input[index].equals("ID")) {

            if (input[index].equals("ID")) {
                index++;
                 if (input[index].equals("=")) {
                     index++;
                      if (input[index].equals("new")) {
                          index++;
                           if (input[index].equals("ID")) {
                               index++;                             
                                if (input[index].equals("(")) {
                                    index++;
                                    if(Parameter()){
                                         if (input[index].equals(")")) {
                                             index++;
                                              if (input[index].equals(";")) {
                                                  index++;
                                                  return true;          
                                            }        
                                        }
                                    }                  
                                }
                            }   
                        }   
                    }
                }
        //    }
        System.out.println("Error at Obj_dec Line number " + Lno[index]);
        return false;
    }

    private static boolean Start() {
      //  if (input[index].equals("Start")) {
            if (input[index].equals("Start")) {
                index++;
                if (input[index].equals("{")) {
                    index++;
                    if (MST()) {
                        if (input[index].equals("}")) {
                            index++;
                            if (input[index].equals("Stop")) {
                                index++;
                                return true;
                            }
                        }
                    }
                }
            }
       // }
        System.out.println("Error at  Start Line number " + Lno[index]);
        return false;
    }

    private static boolean SST() {
        if (input[index].equals("if") || input[index].equals("loop") || input[index].equals("DT") 
                || input[index].equals("ID") || input[index].equals("call")|| input[index].equals("Open")
                || input[index].equals("Secrete")) {
            
            if (input[index].equals("if")){
            if (IF()) {
                return true;
            } 
            }else 
                if (input[index].equals("loop")){ 
            if (Loop()) {
                return true;
            }
            }else 
                if (input[index].equals("DT")){
                if (Dec()) {
                return true;
            } 
            }else 
                if (input[index].equals("call")){
                if (Call()) {
                if (input[index].equals(";")) {
                    index++;
                    return true;      
                }
                   }
            }else if (input[index].equals("ID")){
                if (Dec_Obj()) {
                return true;
            } 
            }
                else 
                if (input[index].equals("Open")||input[index].equals("Secrete")){
                if (Func()) {
                return true;
            }
            }
       }
        System.out.println("Error at SST Line number " + Lno[index]);
        return false;
    }

    private static boolean MST() {
                if (input[index].equals("if") || input[index].equals("loop") || input[index].equals("DT") 
                || input[index].equals("ID") || input[index].equals("call")|| input[index].equals("Open")
                || input[index].equals("Secrete")) { // yaha per follow aw ga phele 

                if (SST()) {
                    if (MST()) {
                            return true;
                    }
                }
        } else if(input[index].equals("}")||input[index].equals("return")){
                    return true;
                }
        System.out.println("Error at  MST Line number " + Lno[index]);
        return false;
    }

    private static boolean IF() {
        //if (input[index].equals("if")) {    
            if (input[index].equals("if")) {
                index++;
                if (input[index].equals("(")) {
                    index++;
                    if (OE()) {
                        if (input[index].equals(")")) {
                            index++;
                            if (input[index].equals("{")) {
                                index++;
                                if (MST()) {
                                    if (input[index].equals("}")) {
                                        index++;
                                        if (OR()) {
                                            return true;
                                        }
                                    }
                                }
                           }
                        }
                    }
                }
            }
       // }
        System.out.println("Error at IF Line number " + Lno[index]);
        return false;
    }

    private static boolean Loop() {
       // if (input[index].equals("loop")) {
            
            if (input[index].equals("loop")) {
                index++;
                if (input[index].equals("(")) {
                    index++;
                    if (OE()) {
                        if (input[index].equals(",")) {
                            index++;
                            if (OE()) {
                                if (input[index].equals(")")) {
                                    index++;
                                    if (input[index].equals("{")) {
                                        index++;
                                        if (MST()) { 
                                            if (input[index].equals("}")) {
                                                index++;
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        //}
        System.out.println("Error at Loop Line number " + Lno[index]);
        return false;
    }

    private static boolean Call() {
       // if (input[index].equals("call")) {

            if (input[index].equals("call")) {
                index++;
                if (input[index].equals("ID")) {
                    index++;
                    III();
                    return true;
                }
            }
     //   }
        System.out.println("Error at Call Line number " + Lno[index]);
        return false;
    }

    private static boolean Return() {
      //  if (input[index].equals("return")) {
            if (input[index].equals("return")) {
                index++;
                if (OE()) {
                    if (input[index].equals(";")) {
                        index++;
                        return true;
                    }
           //     }
            }
        }else if(input[index].equals("}")){
               return true; 
            }
        System.out.println("Error at Return Line number " + Lno[index]);
        return false;
    }

    private static boolean Arr() {
        // is m kch garber lag rahi h 
        if ( input[index].equals("[")) {
            if (input[index].equals("[")) {
                index++;
                if (Index()) {
                    if (input[index].equals("]")) {
                        index++;
                        return true;
                    }
                }     // aur null
            }    
        }
        System.out.println("Error at  Arr Line number " + Lno[index]);
        return false;
    }

    private static boolean Init() {
        if (input[index].equals("=") ) {
            if (input[index].equals("=")) {
                index++;
                if (Xdash()) {
                    return true;
                }
            }
        }
        else if(input[index].equals(",")||input[index].equals(";")){
            return true;
        }
        System.out.println("Error at Init Line number " + Lno[index]);
        return false;
    }

    private static boolean List() {
        if (input[index].equals(",") || input[index].equals(";")) {
            if (input[index].equals(",")) {
                index++;
                if (input[index].equals("ID")) {
                    index++;
                    if (ArrDash()) {
                        if (Init()) {
                            if (List()) {
                                return true;
                            }
                        }
                    }
                }
            } else if (input[index].equals(";")) {
                index++;
                return true;
            }
        }
        System.out.println("Error at List Line number " + Lno[index]);
        return false;
    }

    private static boolean Index() {
     
 if (input[index].equals("!") || input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("StringConst")
  || input[index].equals("FloatConst") || input[index].equals("ID") || input[index].equals("(") || input[index].equals("call")) {
				
            if (OE()) {
                return true;
            }
        }
        else  if (input[index].equals("]")){
            return true;
        }
        System.out.println("Error at Index Line number " + Lno[index]);
        return false;
    }

    private static boolean Xdash() {
        if (input[index].equals("ID") || input[index].equals("{") || input[index].equals("IntConst") || input[index].equals("CharConst")
                || input[index].equals("FloatConst") || input[index].equals("StringConst") || input[index].equals("call")) {

            if (input[index].equals("ID")) {
                index++;
                if (A()) {
                    return true;
                }
            }
            else if (input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("FloatConst") || input[index].equals("StringConst")) { 
                if(Constant()){
                return true;
            }}
            else if (input[index].equals("{")) {
                index++;
                if(Y()){
                    if (input[index].equals("}")) {
                        index++;
                        return true;
                    }
                }
            }else if(Call()){
                return true;
            }
        }
        System.out.println("Error at Xdash Line number " + Lno[index]);
        return false;
    }

    private static boolean OE() {
        if (input[index].equals("!") || input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("StringConst")
         || input[index].equals("FloatConst") || input[index].equals("ID") || input[index].equals("(") || input[index].equals("call")) {

            if (input[index].equals("!")) {
                index++;
                if (F()) {
                    if (TDash()) {
                        if (Edash()) {
                            if (REdash()) {
                                if (AEdash()) {
                                    if (OEdash()) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("FloatConst") || input[index].equals("StringConst")) {
            if(Constant()){
                if (TDash()) {
                    if (Edash()) {
                        if (REdash()) {
                            if (AEdash()) {
                                if (OEdash()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            } else if (input[index].equals("ID")) {
                index++;
                if (AAA()) {
                    if (TDash()) {
                        if (Edash()) {
                            if (REdash()) {
                                if (AEdash()) {
                                    if (OEdash()) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (input[index].equals("(")) {
                index++;
                if (OE()) {
                    if (input[index].equals(")")) {
                        index++;
                        if (TDash()) {
                            if (Edash()) {
                                if (REdash()) {
                                    if (AEdash()) {
                                        if (OEdash()) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else if (input[index].equals("call")) {
            if (Call()) {
                if (AAA()) {
                    if (TDash()) {
                        if (Edash()) {
                            if (REdash()) {
                                if (AEdash()) {
                                    if (OEdash()) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        }
        System.out.println("Error at OE Line number " + Lno[index]);
        return false;
    }

    private static boolean Constant() {
        if (input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("FloatConst") || input[index].equals("StringConst")) {
            if (input[index].equals("IntConst")) {
                index++;
                return true;
            } else if (input[index].equals("FloatConst")) {
                index++;
                return true;
            } else if (input[index].equals("CharConst")) {
                index++;
                return true;
            } else if (input[index].equals("StringConst")) {
                index++;
                return true;
            }
        }
        System.out.println("Error at Constant Line number " + Lno[index]);
        return false;
    }

    private static boolean A() {
        if (input[index].equals("=")) {
            if (input[index].equals("=")) {
                index++;
                if (Xdash()) {
                        return true;
                }
            }
        }
        else if(input[index].equals(",")||input[index].equals(";")){
            return true;
        }
        System.out.println("Error at Line A number " + Lno[index]);
        return false;
    }

    private static boolean Access_Mod() {
        if (input[index].equals("Open") || input[index].equals("Secrete")) {

            if (input[index].equals("Open")) {
                index++;
                return true;
            } 
            else if (input[index].equals("Secrete")) {
                index++;
                return true;
            }
        }
        System.out.println("Error at Access_Mod Line number " + Lno[index]);
        return false;
    }

    private static boolean RET() {
        if (input[index].equals("void") || input[index].equals("DT") || input[index].equals("ID")) {
            if (input[index].equals("void")) {
                index++;
                return true;
            }
            else if (input[index].equals("DT") || input[index].equals("ID")) {
                if(HH()){
                     if(JJ())   
                 return true;
            }
            }
        }
        System.out.println("Error at RET Line number " + Lno[index]);
        return false;
    }

    private static boolean ARG() {
        if (input[index].equals("DT")||input[index].equals("ID")||input[index].equals("void")) {
            
        if(RET()){
            if(CC()){
                return true;
            }
        }
        }
        else if (input[index].equals(")")) {
            return true;
        }          
        System.out.println("Error at ARG Line number " + Lno[index]);
        return false;
    }

    private static boolean ARG2() {
        if (input[index].equals(",")) {
        index++;
        if (RET()) {
            if (input[index].equals("ID")) {
                index++;
                if (ARG2()) {
                    return true;
                }
            }
        }
        }
        else if (input[index].equals(")")) {
            return true;
        }
        System.out.println("Error at ARG2 Line number " + Lno[index]);
        return false;
    }

    private static boolean Parameter() {
        if (input[index].equals("!") || input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("StringConst")
    || input[index].equals("FloatConst") || input[index].equals("ID") || input[index].equals("(") || input[index].equals("call")) {
            if (OE()) {
                if (Para()) {
                    return true;
                }
            }
        }
        else if (input[index].equals(")")) {
            return true;
        }
        System.out.println("Error at Parameter line number " + Lno[index]);
        return false;
    }

    private static boolean Para() {
        if (input[index].equals(",")) {
            if (input[index].equals(",")) {
                index++;
                if (OE()) {
                    if (Para()) {
                        return true;
                    }
                }
            }
        }        
        else if (input[index].equals(")")) {
            return true;
        }
        System.out.println("Eror at line Para number " + Lno[index]);
        return false;
    }
    
    private static boolean OR() {
        if (input[index].equals("or")||input[index].equals("orIf")) {
           
            if (input[index].equals("or")) {
                index++;
                if (input[index].equals("{")) {
                    index++;
                    if (MST()) {
                        if (input[index].equals("}")) {
                            index++;
                            return true;
                        }
                    }
                }
            } else if (input[index].equals("orIf")) {
                
              //  if (input[index].equals("orIf")) {
                    index++;
                    if (input[index].equals("(")) {
                        index++;
                        if (OE()) {
                            if (input[index].equals(")")) {
                                index++;
                                if (input[index].equals("{")) {
                                    index++;
                                    if (MST()) {
                                        if (input[index].equals("}")) {
                                            index++;                                         
                                                return true;                                           
                                        }
                                    }
                                }
                            }
                        }
                    }
            //    }
            }
        }
        // first of sst 
        else  if (input[index].equals("if") || input[index].equals("loop") || input[index].equals("DT") 
                || input[index].equals("ID") || input[index].equals("call")|| input[index].equals("Open")
                || input[index].equals("Secrete")|| input[index].equals("return")|| input[index].equals("}")) {
            return true;
        }
//        // first of MSt
//        else  if (input[index].equals("if") || input[index].equals("loop") || input[index].equals("DT") 
//                || input[index].equals("ID") || input[index].equals("call")|| input[index].equals("Open")
//                || input[index].equals("Secrete")|| input[index].equals("return")) {
//            return true;
//        }
        System.out.println("Error at OR Line number " + Lno[index]);
        return false;
    }

    private static boolean F() {
       if (input[index].equals("!") || input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("StringConst")
         || input[index].equals("FloatConst") || input[index].equals("ID") || input[index].equals("(") || input[index].equals("call")) {

            if (input[index].equals("!")) {
                index++;
                if (F()) {
                    return true;
                }
            } else if (input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("FloatConst") || input[index].equals("StringConst")) {
            if (Constant()) {
                return true;
            }
            } else if (input[index].equals("(")) {
                index++;
                if (OE()) {
                    if (input[index].equals(")")) {
                        index++;
                        return true;
                    }
                }
            } else if (input[index].equals("ID")) {
                index++;
                if (AAA()) {
                    return true;
                }
            }else if (input[index].equals("call")) {
            if (Call()) {
                if (AAA()) {
                    return true;
                }
            }
        }else if (input[index].equals("++")||input[index].equals("--")) {
            if (Inc_Dec()) {
                    return true;
            }
        }
    
       }
       
        System.out.println("Error at F Line number " + Lno[index]);
        return false;
    }

    private static boolean TDash() {
        if (input[index].equals("MDM") ) {

            if (input[index].equals("MDM")) {
                index++;
                if (F()) {
                    if (TDash()) {
                        return true;
                    }
                }
            }
        }
        else if( input[index].equals("PlusMinus") || input[index].equals("RelOpr") || input[index].equals("&&") || input[index].equals("||")
               ||input[index].equals("}") || input[index].equals(",") || input[index].equals(")") || input[index].equals(";") || input[index].equals("]")){
            return true;
        }
        System.out.println("Error at TDash Line number " + Lno[index]);
        return false;
    }

    private static boolean Edash() {
        if (input[index].equals("PlusMinus")) {

            if (input[index].equals("PlusMinus")) {
                index++;
                if (T()) {
                    if (Edash()) {
                        return true;
                    }
                }
            }
        }
        else if (input[index].equals("RelOpr")||input[index].equals("&&")||input[index].equals("||")||input[index].equals("}")
                ||input[index].equals(",")||input[index].equals(")")||input[index].equals(";")||input[index].equals("]")) {
            return true;
        }
        System.out.println("Error at Edash Line number " + Lno[index]);
        return false;
    }

    private static boolean REdash() {
           if (input[index].equals("RelOpr")) {
        
            if (input[index].equals("RelOpr")) {
                index++;
                if (E()) {
                    if (REdash()) {
                        return true;
                    }
                }
            }
        }
        else if (input[index].equals("&&") || input[index].equals("||") ||input[index].equals("}")|| input[index].equals(",") || input[index].equals(")") || input[index].equals(";") || input[index].equals("]")) {
            return true;
           }
        System.out.println("Error at REdash Line number " + Lno[index]);
        return false;
    }

    private static boolean AEdash() {
        if (input[index].equals("&&") ) {

            if (input[index].equals("&&")) {
                index++;
                if (RE()) {
                    if (AEdash()) {
                        return true;
                    }
                }
            }
        }
        else if (input[index].equals("||")||input[index].equals("}")||input[index].equals(",")||input[index].equals(")")||input[index].equals(";")||input[index].equals("]") ) {
            return true;      
        }
        System.out.println("Error at AEdash Line number " + Lno[index]);
        return false;
    }

    private static boolean OEdash() {
        if (input[index].equals("||")) {
            if (input[index].equals("||")) {
                index++;
                if (AE()) {
                    if (OEdash()) {
                        return true;
                    }
                }
            }
        }
        else if (input[index].equals(",")||input[index].equals("}")||input[index].equals(")")||input[index].equals(";")||input[index].equals("]")) {
           return true; 
        }
        System.out.println("Error at OEdash Line number " + Lno[index]);

        return false;
    }

    private static boolean AAA() {
        if (input[index].equals(".") || input[index].equals("[")) {
           
            if (input[index].equals(".")) {
                index++;
                if (AAAdash()) {
                    return true;
                }
            } else if(input[index].equals("[")){
            if (Arr()) {
                if (AAA()) {
                    return true;
                }
            }
        }
    } else if( input[index].equals("MDM") || input[index].equals("PlusMinus") || input[index].equals("RelOpr") || input[index].equals("&&")
           || input[index].equals("||") || input[index].equals(",") ||input[index].equals("}")|| input[index].equals(")") || input[index].equals(";")|| input[index].equals("]")){
            return true;
        }
        System.out.println("Error at AAA Line number " + Lno[index]);
        return false;
    }

    private static boolean AE() {
      if (input[index].equals("!") || input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("StringConst")
         || input[index].equals("FloatConst") || input[index].equals("ID") || input[index].equals("(") || input[index].equals("call")) {

            if (input[index].equals("!")) {
                index++;
                if (F()) {
                    if (TDash()) {
                        if (Edash()) {
                            if (REdash()) {
                                if (AEdash()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            } else if (input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("FloatConst") || input[index].equals("StringConst")) {
            if (Constant()) {
                if (TDash()) {
                    if (Edash()) {
                        if (REdash()) {
                            if (AEdash()) {
                                return true;
                            }
                        }
                    }
                }
            }
            } else if (input[index].equals("ID")) {
                index++;
                if (AAA()) {
                    if (TDash()) {
                        if (Edash()) {
                            if (REdash()) {
                                if (AEdash()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            } else if (input[index].equals("(")) {
                index++;
                if (OE()) {
                    if (input[index].equals(")")) {
                        index++;
                        if (TDash()) {
                            if (Edash()) {
                                if (REdash()) {
                                    if (AEdash()) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (input[index].equals("call")) { 
            if (Call()) {
                if (AAA()) {
                    if (TDash()) {
                        if (Edash()) {
                            if (REdash()) {
                                if (AEdash()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
        System.out.println("Error at AE Line number " + Lno[index]);
        return false;
    }

    private static boolean E() {
        if (input[index].equals("!") || input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("StringConst")
         || input[index].equals("FloatConst") || input[index].equals("ID") || input[index].equals("(") || input[index].equals("call")) {

            if (input[index].equals("!")) {
                index++;
                if (F()) {
                    if (TDash()) {
                        if (Edash()) {
                            return true;
                        }
                    }
                }
            } else if (input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("FloatConst") || input[index].equals("StringConst")) { 
            if (Constant()) {
                if (TDash()) {
                    if (Edash()) {
                        return true;
                    }
                }
            }
            } else if (input[index].equals("ID")) {
                index++;
                if (AAA()) {
                    if (TDash()) {
                        if (Edash()) {
                            return true;
                        }
                    }
                }
            } else if (input[index].equals("(")) {
                index++;
                if (OE()) {
                    if (input[index].equals(")")) {
                        index++;
                        if (TDash()) {
                            if (Edash()) {
                                return true;
                            }
                        }
                    }
                }
            }else if (input[index].equals("call")) {
            if (Call()) {
                if (AAA()) {
                    if (TDash()) {
                        if (Edash()) {
                            return true;
                        }
                    }
                }
            }
        }
    }
        System.out.println("Error at E Line number " + Lno[index]);
        return false;
    }

    private static boolean T() {
     if (input[index].equals("!") || input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("StringConst")
         || input[index].equals("FloatConst") || input[index].equals("ID") || input[index].equals("(") || input[index].equals("call")) {

            if (input[index].equals("!")) {
                index++;
                if (F()) {
                    if (TDash()) {
                        return true;
                    }
                }
            } else if (input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("FloatConst") || input[index].equals("StringConst")) {
            if (Constant()) {
                if (TDash()) {
                    return true;
                }
            }
            } else if (input[index].equals("ID")) {
                index++;
                if (AAA()) {
                    if (TDash()) {
                        return true;
                    }
                }
            } else if (input[index].equals("(")) {
                index++;
                if (OE()) {
                    if (input[index].equals(")")) {
                        index++;
                        if (TDash()) {
                            return true;
                        }
                    }
                }
            }else if (input[index].equals("call")) {
            if (Call()) {
                if (AAA()) {
                    if (TDash()) {
                        return true;
                    }
                }
            }
        }
    }
        System.out.println("Error at T Line number " + Lno[index]);
        return false;
    }

    private static boolean Class() {
      //  if (input[index].equals("Model")) {
            if (input[index].equals("Model")) {
                index++;
                if (input[index].equals("ID")) {
                    index++;
                    if (input[index].equals("{")) {
                        index++;
                        if (Body1()) {
                            if (input[index].equals("}")) {
                                index++;
                                return true;
                            }
                        }
                    }
                }
            }
      //  }
        System.out.println("Error at   Class line number " + Lno[index]);
        return false;
    }

    private static boolean RE() {
   if (input[index].equals("!") || input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("StringConst")
         || input[index].equals("FloatConst") || input[index].equals("ID") || input[index].equals("(") || input[index].equals("call")) {

            if (input[index].equals("!")) {
                index++;
                if (F()) {
                    if (TDash()) {
                        if (Edash()) {
                            if (REdash()) {
                                return true;
                            }
                        }
                    }
                }
            } else if (input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("FloatConst") || input[index].equals("StringConst")) {
            if (Constant()) {
                if (TDash()) {
                    if (Edash()) {
                        if (REdash()) {
                            return true;
                        }
                    }
                }
            }  
            } else if (input[index].equals("ID")) {
                index++;
                if (AAA()) {
                    if (TDash()) {
                        if (Edash()) {
                            if (REdash()) {
                                return true;
                            }
                        }
                    }
                }

            } else if (input[index].equals("(")) {
                index++;
                if (OE()) {
                    if (input[index].equals(")")) {
                        index++;
                        if (TDash()) {
                            if (Edash()) {
                                if (REdash()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            } else if (input[index].equals("call")) {
            if (Call()) {
                if (AAA()) {
                    if (TDash()) {
                        if (Edash()) {
                            if (REdash()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
   }
        System.out.println("Eror at RE line number " + Lno[index]);
        return false;

    }

    private static boolean AAAdash() {
        if (input[index].equals("ID") || input[index].equals("call")) {

            if (input[index].equals("ID")) {
                index++;
                if (AAA()) {
                    return true;
                }

            } else if (input[index].equals("call")) {
                if (Call()) {
                    if (AAA()) {
                        return true;
                    }
                }
            }
        }
        System.out.println("Eror at AAAdash line number " + Lno[index]);
        return false;
    }

    private static boolean S() {
      if (input[index].equals("Model")||input[index].equals("Main_Model")) { 
        if(Classes()){
            if(ClassMain()){
                return true;
            }
        }
      }
        System.out.println("Error at S line number " + Lno[index]);
        return false;
    }

    private static boolean Classes() {
        if (input[index].equals("Model")) {      
        if(Class()){
           if(Classes()){
                return true;
            }
        }
        }else  if (input[index].equals("Main_Model")) {           
             return true;
        }
         
          System.out.println("Eror at classes line number " + Lno[index]);
        return false;
        }

    private static boolean Dec_Obj() {
      //   if (input[index].equals("ID")) {    
        if (input[index].equals("ID")) {
            index++;
           if(Declaration())
               return true;   
        }
     //    }
        System.out.println("Eror at Dec_obj line number " + Lno[index]);
        return false;
    }

    private static boolean CC() {
    if (input[index].equals("ID")) {

        if(input[index].equals("ID")){
            index++;
            if(ARG2()){
                return true;
            }
        }
    }
       System.out.println("Eror at cc line number " + Lno[index]);
        return false;
    
    }

    private static boolean Declaration() {
        if (input[index].equals(",")||input[index].equals(";")||input[index].equals("[")||input[index].equals("=")||input[index].equals("ID")) {
        
        if (input[index].equals("[")||input[index].equals("=")||input[index].equals(",")||input[index].equals(";")) {               
        if(Init1()){
            return true;
        }
    }
        else if (input[index].equals("ID")) {
             if(Obj_dec()){
            return true;
            }
        }
    }
       
        
        System.out.println("Eror at declartaion line number " + Lno[index]);
        return false;
    
    }

    private static boolean Init1() {
       if (input[index].equals("[")||input[index].equals("=")||input[index].equals(",")||input[index].equals(";")) {            
        if(ArrDash()){
            if(Init()){
                if(List()){
                    return true;
                }
            }
            }
        }      
    System.out.println("Eror at init1 line number " + Lno[index]);
        return false;
    }

    private static boolean ArrDash() {
        if(input[index].equals("[")){                    
            if(Arr()){
            return true;
        }
    }
         else if(input[index].equals("=")||input[index].equals(",")||input[index].equals(";")){
            return true;            
        }
        System.out.println("Eror at arrdash line number " + Lno[index]);
        return false;
    }

    private static boolean Y() {
        
 if (input[index].equals("!") || input[index].equals("IntConst") || input[index].equals("CharConst") || input[index].equals("StringConst")
                || input[index].equals("FloatConst") || input[index].equals("ID") || input[index].equals("(") || input[index].equals("call")) {
				
        if(OE()){
            if(Ydash()){
                return true;
            }
        }
 }
         System.out.println("Eror at y line number " + Lno[index]);
        return false;
    }

    private static boolean Ydash() {
        if (input[index].equals(",")) {     
        if (input[index].equals(",")) {
            index++;
            if(OE()){
                if(Ydash()){
                    return true;
                }
            }        
        }
        }
        else if (input[index].equals("}")) {
            return true;
            
        }
        
     System.out.println("Eror at Ydash line number " + Lno[index]);
        return false;
    }

    private static boolean Inc_Dec() {
        
        return true;
    }

    private static boolean HH() {
        if (input[index].equals("DT") || input[index].equals("ID")){
           if (input[index].equals("DT")){
               index++;
               return true;
           }
           else if(input[index].equals("ID")){
               index++;
               return true;
           }
        }
        System.out.println("Eror at HH line number " + Lno[index]);
        return false;
    }

    private static boolean JJ() {
        if (input[index].equals("[")){
               index++;
            if (input[index].equals("]")){
               index++;   
               return true;
           }
        }
        else if (input[index].equals("ID")){
            return true;
        }
        System.out.println("Eror at JJ line number " + Lno[index]);
        return false;  
    }

    private static boolean III() {
      if (input[index].equals(".")||input[index].equals("(")){
          
            if (input[index].equals(".")){
                index++;
                if (input[index].equals("ID"))
                    index++;
                    if(III())
                    return true;
            }
        else if (input[index].equals("(")){
                if (KKK())
                    return true;
            }
        }
        System.out.println("Eror at III line number " + Lno[index]);
        return false;
    
    }

    private static boolean KKK() {
       if (input[index].equals("(")){
                if (input[index].equals("("))
                    index++;
                 if (Parameter());
                    if (input[index].equals(")"))
                        index++;
                    if(KK())
                        return true;
       }
        System.out.println("Eror at KKK line number " + Lno[index]);
        return false;
    }

    private static boolean KK() {
            if (input[index].equals(".")){
                index++;
                if(Call())
                    return true;
            }
   else if (input[index].equals(";")||input[index].equals(".")||input[index].equals("[")
           ||input[index].equals("MDM")||input[index].equals("PlusMinus")||
            input[index].equals("RelOpr")||input[index].equals("&&")||input[index].equals("||")
           ||input[index].equals(",")||input[index].equals("]")
            ||input[index].equals("}")||input[index].equals(")")){
                return true;
            }
        System.out.println("Eror at KK line number " + Lno[index]);
        return false;      
    }

}
