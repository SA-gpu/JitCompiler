/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilercons;

/**
 *
 * @author tahah
 */
class TokenClass {
    public int line ;
    public String word;
    public String token;

 

    public TokenClass( String word, int line) {
        this.word = word;
        this.line = line;
    }

    TokenClass() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getLineNo() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    
}
