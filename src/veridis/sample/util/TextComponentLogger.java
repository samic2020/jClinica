package veridis.sample.util;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public class TextComponentLogger implements ActivityLogger{
    private final JTextComponent target;
    public TextComponentLogger(JTextComponent target) {
        this.target = target;
        target.setEditable(false);

    }

    public void logAction(final String message){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                target.setText(String.format(message,"%s%s%n", 
                                             target.getText()
                                             ));
                target.setCaretPosition(target.getDocument().getLength());
            }
        });
    }
}