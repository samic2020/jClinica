/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import javax.swing.JTextField;
import java.awt.event.*;

public class LimitedTextField extends JTextField{
    private byte maxLength=0;

    public LimitedTextField(int maxLength){
        super();
        this.maxLength= (byte)maxLength;
        this.addKeyListener(new LimitedKeyListener());
    }

    public void setMaxLength(int maxLength){
        this.maxLength= (byte)maxLength;
        update();
    }

    private void update(){
        if (getText().length()>maxLength){
            setText(getText().substring(0,maxLength));
            setCaretPosition(maxLength);
        }
    }

    public void setText(String arg0){
        super.setText(arg0);
        update();
    }

    public void paste(){
        super.paste();
        update();
    }

    //Classes Internas
    private class LimitedKeyListener extends KeyAdapter{
        private boolean backspace= false;

        public void keyPressed(KeyEvent e){
            backspace=(e.getKeyCode()==8);
        }

        public void keyTyped(KeyEvent e){
            if (    !backspace  &&
                    getText().length()>maxLength-1){
                e.consume();
            }
        }
    }
}
