package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.company.Main.main;

// Created by StudenetskiyA on 02.02.2017.

public class UserChoice {
    BufferedImage topImage;
    JLabel yesChoice;
    JLabel noChoice;
    JLabel topText;
    boolean choice;
    boolean userChoiced=false;

    UserChoice(Main.ViewField _vf, String _topImage, String _topText, String _yesText, String _noText){
        try {
            topImage = ImageIO.read(Main.class.getResourceAsStream("cards/"+_topImage+".jpg"));
        } catch (IOException e) {
            System.out.println("Can't load "+_topImage);
           // e.printStackTrace();
        }
        topText = new JLabel();
        topText.setText(_topText);
        topText.setForeground(Color.cyan);
        topText.setBackground(Color.gray);
        _vf.add(topText);
        yesChoice= new JLabel();
        yesChoice.setText(_yesText);
        yesChoice.setForeground(Color.cyan);
        yesChoice.setBackground(Color.gray);
       // yesChoice.setLocation();
        _vf.add(yesChoice,0);
        yesChoice.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                _vf.remove(yesChoice);
                _vf.remove(noChoice);
                _vf.remove(topText);
                main.userChoiceShow=false;
                Client.writeLine("$CHOICEYESNO(1)");
                System.out.println("Yes clicked");
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        noChoice= new JLabel();
        noChoice.setText(_noText);
        noChoice.setForeground(Color.cyan);
        _vf.add(noChoice,0);
        noChoice.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                _vf.remove(yesChoice);
                _vf.remove(noChoice);
                _vf.remove(topText);
                main.userChoiceShow=false;
                Client.writeLine("$CHOICEYESNO(0)");
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    void show(Graphics g, int _width, int _height){
        topText.setSize(_width*3/2,_height/8);
        topText.setLocation(main.getWidth()/2-_width*2/3,main.getHeight()/2-_height/2-_height/8);
        yesChoice.setSize(_width,_height/8);
        noChoice.setSize(_width,_height/8);
        yesChoice.setLocation(main.getWidth()/2-_width*2/3,main.getHeight()/2+_height/2);
        noChoice.setLocation(main.getWidth()/2+_width/3,main.getHeight()/2+_height/2);
        g.drawImage(topImage,main.getWidth()/2-_width/2,main.getHeight()/2-_height/2,_width,_height,null);
    }

}
