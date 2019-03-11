/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snakezinha;

import java.awt.Dimension;
import javax.swing.JFrame;

/**
 *
 * @author USER
 */
public class Mainzinho {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Cobrinha");
        frame.setContentPane(new Painelzinho());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setPreferredSize(new Dimension(Painelzinho.WIDTH, Painelzinho.HEIGHT));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true); 
    }
    
}
