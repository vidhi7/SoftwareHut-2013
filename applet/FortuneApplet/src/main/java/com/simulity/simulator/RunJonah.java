
package com.simulity.simulator;

import com.simulity.jonah.testhelp.JonahRunner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christopher Burke <christopher.burke@simulity.com>
 */
public class RunJonah extends Thread {
    /**
     * Launches the STK SIM Emulator 'Jonah'
     * @param args 
     */
    public static void main(String[] args) {
        new RunJonah().start();
    }
    
    
    public void run() {
        JonahRunner runner = new JonahRunner("com.simulity.javacard.fortuneapplet.FortuneApplet", null, true, "FS.bin");
        try {
            runner.go();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(RunJonah.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
