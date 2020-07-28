package com.hullabaloo.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class AppWindow extends JFrame{
    public AppWindow (String appTitle, int appHeight, int appWidth)throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(appTitle);
        setMinimumSize(new Dimension(appHeight, appWidth));
        setLocationRelativeTo(null);
        setResizable(true);
        setVisible(true);
    }
}