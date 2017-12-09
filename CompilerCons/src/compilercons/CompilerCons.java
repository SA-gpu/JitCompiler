/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilercons;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 *
 * @author tahah
 */
public class CompilerCons {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
       
        String input = ConvertFileToString("input.txt");
        String output= LexicalAnalyzer.Simulate(input);
        System.out.println(input);
        System.out.println();
        System.out.println();
        System.out.println("tokens");
        System.out.println(output);
        System.out.println("");
         LexicalAnalyzer.SyantaxAnalyzer();
       
       // LexicalAnalyzer.display();
           OutputStream outputStream= new FileOutputStream("output.txt");
        
                try(Writer file=new OutputStreamWriter(outputStream);){
            for(char line:output.toCharArray()){
               file.write(String.valueOf(line));
              //  System.out.print(String.valueOf(line));
                
            }
        }
    }

    private static String ConvertFileToString(String filename)throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    try{
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        
        while(line!=null){
            sb.append(line);
            sb.append("\n");
            line=br.readLine();
        }
        return sb.toString();
    }
    finally{
        br.close();
    }
    }
    
}
