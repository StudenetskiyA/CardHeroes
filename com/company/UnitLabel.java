package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.company.Main.main;

/**
 * Created by StudenetskiyA on 29.01.2017.
 */
public class UnitLabel extends JLabel {
    static final int BorderOval=4;
    static final Color NumberColor=Color.red;
    static final Color NumberBackColor=Color.gray;

    public BufferedImage image;
    Creature creature;
    Color borderInactiveColor=Color.gray;
    Color borderActiveColor = Color.green;

    void setAll(Creature _creature, int _width,int _height){
        creature=_creature;
        setSize(_width,_height);
        try {
//            image = ImageIO.read(Main.class.getResourceAsStream(creature.image));
            image = ImageIO.read(Main.class.getResourceAsStream("cards/small/Вестник смерти.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    UnitLabel(){
        super();
    }

    static int plusSize(){
        return BorderOval+4+BorderOval/2;
    }

    void drawImage(Graphics g){
        if (isVisible()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(image, getX(), getY(), getWidth()+1, getHeight()+1, null);
            if (creature.isSummonedJust)
            g2.setColor(borderInactiveColor);
            else if (!creature.isSummonedJust && !creature.isTapped) g2.setColor(borderInactiveColor);
            else //Think about it
                g2.setColor(borderInactiveColor);

            g2.setStroke(new BasicStroke(BorderOval));
            g2.drawOval(getX()-BorderOval/2,getY()-BorderOval/2,getWidth()+BorderOval,getHeight()+BorderOval);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(getX()-BorderOval/2-2,getY()-BorderOval/2-2,getWidth()+BorderOval+4,getHeight()+BorderOval+4);
            g2.drawOval(getX()-BorderOval/2+2,getY()-BorderOval/2+2,getWidth()+BorderOval-4,getHeight()+BorderOval-4);
            int down=getCenterY()+getHeight()/2+BorderOval/2+1;
            int up=getCenterY()-getHeight()/2-BorderOval/2-1;
            g2.setColor(Color.BLACK);
            g2.drawRect(getCenterX()-getWidth()/4-getWidth()/3-1,down-getWidth()/3-1,getWidth()/3+1,getWidth()/3+1);
            g2.drawRect(getCenterX()+getWidth()/4-1,down-getWidth()/3-1,getWidth()/3+1,getWidth()/3+1);
            g2.setColor(NumberBackColor);
            g2.fillRect(getCenterX()-getWidth()/4-getWidth()/3,down-getWidth()/3,getWidth()/3,getWidth()/3);
            g2.fillRect(getCenterX()+getWidth()/4,down-getWidth()/3,getWidth()/3,getWidth()/3);

            //20 for 688 - for Serif
            int fs=main.getWidth()*20/890;
            Font font = new Font("Serif", Font.BOLD, fs);
            Rectangle att = new Rectangle(getCenterX()-getWidth()/4-getWidth()/3,down-getWidth()/3,getWidth()/3,getWidth()/3);
            setColorBonusOrMinus(g2,creature.getBonusOrMinusPower());
            System.out.println("BMpower="+creature.getBonusOrMinusPower());
            drawCenteredString(g2,String.valueOf(creature.getPower()),att,font);
             att = new Rectangle(getCenterX()+getWidth()/4,down-getWidth()/3,getWidth()/3,getWidth()/3);
            setColorBonusOrMinus(g2,creature.getBonusOrMinusTougness());
            drawCenteredString(g2,String.valueOf(creature.getTougness()),att,font);
            if (creature.text.contains("ТАП")) {
                g2.drawRect(getCenterX()+getWidth()/4-1,up,getWidth()/3+1,getWidth()/3+1);
                g2.fillRect(getCenterX()+getWidth()/4,up,getWidth()/3,getWidth()/3);
                try {
                    BufferedImage tap = ImageIO.read(Main.class.getResourceAsStream("icons/effects/tap.png"));
                    g2.drawImage(tap, getCenterX() + getWidth() / 4, up, getWidth() / 3, getWidth() / 3, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            int effectsX=getCenterX()-getWidth()/6;
            int effectsY=down-2*getWidth()/3;
            if (creature.effects.poison!=0) {
                try {
                    BufferedImage tap = ImageIO.read(Main.class.getResourceAsStream("icons/effects/poison"+creature.effects.poison+".png"));
                    g2.drawImage(tap, effectsX, effectsY, getWidth() / 3, getWidth() / 3, null);
                } catch (IOException e) {
                    e.printStackTrace();
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

    void setColorBonusOrMinus(Graphics g,int n){
        if (n>0) g.setColor(Color.green);
        else if (n<0) g.setColor(Color.RED);
        else g.setColor(Color.white);
    }
}