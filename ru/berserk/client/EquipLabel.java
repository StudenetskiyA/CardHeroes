package ru.berserk.client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static ru.berserk.client.Main.main;

// Created by StudenetskiyA on 21.02.2017.

public class EquipLabel extends JLabel {
    static final int BorderOval=4;
    static final Color NumberColor=Color.red;
    static final Color NumberBackColor=Color.gray;
    MyFunction.ClickImage tapClick = new MyFunction.ClickImage();

    public BufferedImage image;
    Equpiment equip;

    Color borderInactiveColor=Color.CYAN;
    Color borderTappedColor = Color.gray;
    Color borderActiveColor = Color.green;
    Color borderAlreadyAttackColor = Color.blue;

    void setAll(Equpiment _eq, int _width,int _height){
        equip=_eq;
        setSize(_width,_height);
        try {
            String im = "cards/equip/"+equip.image.substring(0,equip.image.length()-4)+".png";
            //String im = "cards/equip/Молот прародителя.png";

            image = ImageIO.read(new File(im));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    EquipLabel(){
        super();
    }

    static int plusSize(){
        return BorderOval+4+BorderOval/2;
    }

    void drawImage(Graphics g, boolean isMyHero) {
        if (isVisible()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(image, getX(), getY(), getWidth() + 1, getHeight() + 1, null);
            if (!equip.isTapped) g2.setColor(borderActiveColor);
            else g2.setColor(borderTappedColor);

            g2.setStroke(new BasicStroke(BorderOval));
            g2.drawRoundRect(getX() - BorderOval / 2, getY() - BorderOval / 2, getWidth() + BorderOval, getHeight() + BorderOval, BorderOval, BorderOval);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(getX() - BorderOval / 2 - 2, getY() - BorderOval / 2 - 2, getWidth() + BorderOval + 4, getHeight() + BorderOval + 4, BorderOval, BorderOval);
            g2.drawRoundRect(getX() - BorderOval / 2 + 2, getY() - BorderOval / 2 + 2, getWidth() + BorderOval - 4, getHeight() + BorderOval - 4, BorderOval, BorderOval);
            //TODO Draw heart
            g2.setColor(NumberBackColor);

            //20 for 688 - for Serif
            int fs = main.getWidth() * 20 / 890;
            Font font = new Font("Serif", Font.BOLD, fs);
            int down = getCenterY() + getHeight() / 2 + BorderOval / 2 + 1;
            int up = getCenterY() - getHeight() / 2 - BorderOval / 2 - 1;

            Rectangle att = new Rectangle(getX() , down - getWidth() / 2, getWidth(), getWidth() / 2);
            g2.setColor(Color.white);
            if (equip.hp!=0)
            drawCenteredString(g2, String.valueOf(equip.hp), att, font);

            if (isMyHero) {
                if (equip.text.contains("ТАП")) {
                    g2.setColor(Color.BLACK);
                    g2.drawRect(getCenterX() + getWidth() / 4, getCenterY() - getHeight() / 2, getWidth() / 4, getWidth() / 4);
                    g2.setColor(NumberBackColor);
                    g2.fillRect(getCenterX() + getWidth() / 4, getCenterY() - getHeight() / 2, getWidth() / 4, getWidth() / 4);
                    try {
                        tapClick.image = ImageIO.read(new File("icons/effects/tap.png"));
                        tapClick.LSD(g2, getCenterX() + getWidth() / 4, getCenterY() - getHeight() / 2, getWidth() / 4, getWidth() / 4);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = (int)rect.getX()+(rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = (int)rect.getY()+((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    int getCenterX(){
        return getX()+getWidth()/2;
    }

    int getCenterY(){
        return getY()+getHeight()/2;
    }

}
