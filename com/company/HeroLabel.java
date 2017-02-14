package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.company.Main.main;
import static com.company.Main.players;

// Created by StudenetskiyA on 29.01.2017.

public class HeroLabel extends JLabel {
    static final int BorderOval=4;
    static final Color NumberColor=Color.red;
    static final Color NumberBackColor=Color.gray;
    MyFunction.ClickImage tapClick = new MyFunction.ClickImage();

    public BufferedImage image;
    Player player;

    Color borderInactiveColor=Color.CYAN;
    Color borderTappedColor = Color.gray;
    Color borderActiveColor = Color.green;
    Color borderAlreadyAttackColor = Color.blue;

    void setAll(Player _player, int _width,int _height){
        player=_player;
        setSize(_width,_height);
        try {
            String im = "cards/heroes/face/"+player.image.substring(0,player.image.length()-4)+".png";
//            String im2 = "cards/heroes/face/Свирепый резак.png";
//
//            if (im.equals(im2)){
//                System.out.println("Equal");
//            }

            image = ImageIO.read(Main.class.getResourceAsStream(im));
            // image = ImageIO.read(Main.class.getResourceAsStream("cards/heroes/face/Тиша.jpg"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    HeroLabel(){
        super();
    }

    static int plusSize(){
        return BorderOval+4+BorderOval/2;
    }

    void drawImage(Graphics g, boolean isMyHero) {
        if (isVisible()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(image, getX(), getY(), getWidth() + 1, getHeight() + 1, null);
            if (!player.isTapped) g2.setColor(borderActiveColor);
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

            Rectangle att = new Rectangle(getCenterX() , down - getWidth() / 3, getWidth() / 2, getWidth() / 3);
            setColorBonusOrMinus(g2,-player.damage);
            drawCenteredString(g2, String.valueOf(player.hp - player.damage), att, font);

            if (isMyHero) {
                if (player.text.contains("ТАП")) {
                    g2.setColor(Color.BLACK);
                    g2.drawRect(getCenterX() + getWidth() / 4, getCenterY() - getHeight() / 2, getWidth() / 4, getWidth() / 4);
                    g2.setColor(NumberBackColor);
                    g2.fillRect(getCenterX() + getWidth() / 4, getCenterY() - getHeight() / 2, getWidth() / 4, getWidth() / 4);
                    try {
                        tapClick.image = ImageIO.read(Main.class.getResourceAsStream("icons/effects/tap.png"));
                        tapClick.LSD(g2, getCenterX() + getWidth() / 4, getCenterY() - getHeight() / 2, getWidth() / 4, getWidth() / 4);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //TODO Calculate effects count.
            int effectsX=getCenterX()-getWidth()/2;
            int effectsY=down-2*getWidth()/3;
            int effectsFounded=0;
            if (player.effects.bbShield) {
                try {
                    BufferedImage tap = ImageIO.read(Main.class.getResourceAsStream("icons/effects/bbshield.png"));
                    g2.drawImage(tap, effectsX+effectsFounded*getWidth()/3, effectsY, getWidth() / 3, getWidth() / 3, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                effectsFounded++;
            }
//            if (creature.effects.turnToDie<=2) {
//                try {
//                    BufferedImage tap = ImageIO.read(Main.class.getResourceAsStream("icons/effects/dienear.png"));
//                    g2.drawImage(tap, effectsX+effectsFounded*getWidth()/3, effectsY, getWidth() / 3, getWidth() / 3, null);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                effectsFounded++;
//            }
//            if (creature.effects.vulnerability) {
//                try {
//                    BufferedImage tap = ImageIO.read(Main.class.getResourceAsStream("icons/effects/vulnerability.png"));
//                    g2.drawImage(tap, effectsX+effectsFounded*getWidth()/3, effectsY, getWidth() / 3, getWidth() / 3, null);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                effectsFounded++;
//            }
//            if (creature.effects.bonusArmor!=0) {
//                try {
//                    //TODO When we have more pictures, replace 3 for N.
//                    BufferedImage tap = ImageIO.read(Main.class.getResourceAsStream("icons/effects/bonusarmor3.png"));
//                    g2.drawImage(tap, effectsX+effectsFounded*getWidth()/3, effectsY, getWidth() / 3, getWidth() / 3, null);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                effectsFounded++;
//            }
//            if (!creature.effects.additionalText.equals("")) {
//                if (creature.effects.additionalText.contains("Не может атаковать. Не может блокировать.")) {
//                    try {
//                        BufferedImage tap = ImageIO.read(Main.class.getResourceAsStream("icons/effects/cantattactorblock.png"));
//                        g2.drawImage(tap, effectsX+effectsFounded*getWidth()/3, effectsY, getWidth() / 3, getWidth() / 3, null);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    effectsFounded++;
//                }
//                //Unknowed additional text
//                else {
//                    try {
//                        BufferedImage tap = ImageIO.read(Main.class.getResourceAsStream("icons/effects/additionaltext.png"));
//                        g2.drawImage(tap, effectsX + effectsFounded * getWidth() / 3, effectsY, getWidth() / 3, getWidth() / 3, null);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    effectsFounded++;
//                }
//            }
//
//            //TODO if effects more than can be placed on card
//        }
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

    void setColorBonusOrMinus(Graphics g,int n){
        if (n>0) g.setColor(Color.green);
        else if (n<0) g.setColor(Color.RED);
        else g.setColor(Color.white);
    }
}
