package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.company.Main.main;

/**
 * Created by StudenetskiyA on 02.02.2017.
 */
public class UserChoice {
    int width;
    int height;
    BufferedImage topImage;
    JLabel yesChoice;
    JLabel noChoice;
    boolean choice;
    boolean userChoiced=false;
    Graphics g;

    UserChoice(Main.ViewField _vf, String _topImage, int _width, int _height, String _yesText, String _noText){
        try {
            topImage = ImageIO.read(Main.class.getResourceAsStream(_topImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
        width=_width;
        height=_height;
        yesChoice= new JLabel();
        yesChoice.setText(_yesText);
        yesChoice.setSize(_width,_height/4);
        _vf.add(yesChoice,0);
        yesChoice.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                choice=true;
                userChoiced=true;
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
        noChoice.setSize(_width,_height/4);
        _vf.add(noChoice,0);
        noChoice.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                choice=false;
                userChoiced=true;
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

    void show(){
        g.drawImage(topImage,main.getWidth()/2-width/2,main.getHeight()/2-height/2,width,height,null);
        yesChoice.setLocation(main.getWidth()/2-width*2,main.getHeight()/2+height);
        noChoice.setLocation(main.getWidth()/2+width*2,main.getHeight()/2+height);
    }

}
