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
public class TokenSyn {
    
    public String ClassPart;
    public String ValuePart;
    public int lineNo ;

    public TokenSyn(String ClassPart, String ValuePart, int lineNo) {
        this.ClassPart = ClassPart;
        this.ValuePart = ValuePart;
        this.lineNo = lineNo;
    }

    public String getClassPart() {
        return ClassPart;
    }

    public void setClassPart(String ClassPart) {
        this.ClassPart = ClassPart;
    }

    public String getValuePart() {
        return ValuePart;
    }

    public void setValuePart(String ValuePart) {
        this.ValuePart = ValuePart;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }
    

    
}
