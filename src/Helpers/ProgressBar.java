/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helpers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressBar extends JFrame {

    public JProgressBar progressBar;
    public JLabel napis;
    public JLabel napis2;
    public JPanel panel;

    public ProgressBar() {
        super("loading");
        setSize(300, 50);
        this.setUndecorated(true);
        //this.setLocation(220, 210);
        this.setLocationRelativeTo(null);
        panel = new javax.swing.JPanel();
        Container content = getContentPane();
        

panel.setBackground(new java.awt.Color(204, 255, 204));

panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        content.setLayout(new BorderLayout());        
        napis = new JLabel("Wczytuje dane ");
        napis2 = new JLabel("Proszę czekać ...");
        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(35000);
        progressBar.setStringPainted(true);
        progressBar.setBorder(null);
        content.add(panel);
        panel.add(napis, BorderLayout.NORTH);
        
        panel.add(progressBar, BorderLayout.SOUTH);
        setVisible(false);
    }

    void updateProgress(final int newValue) {
        progressBar.setValue(newValue);
    }

    public void setValue(final int j) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateProgress(j);
            }
        });
    }
}