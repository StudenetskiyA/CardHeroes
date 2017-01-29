package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
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
    Color borderColor=Color.gray;
    int centerX;
    int centerY;

    UnitLabel(String _image, int _width, int _height){
        super();
        setSize(_width,_height);
        try {
            image = ImageIO.read(Main.class.getResourceAsStream(_image));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void drawImage(Graphics g){
        if (isVisible()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(image, getX(), getY(), getWidth()+1, getHeight()+1, null);
            g2.setColor(borderColor);
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
            g2.drawRect(getCenterX()+getWidth()/4-1,up,getWidth()/3+1,getWidth()/3+1);
            g2.setColor(NumberBackColor);
            g2.fillRect(getCenterX()-getWidth()/4-getWidth()/3,down-getWidth()/3,getWidth()/3,getWidth()/3);
            g2.fillRect(getCenterX()+getWidth()/4,down-getWidth()/3,getWidth()/3,getWidth()/3);
            g2.fillRect(getCenterX()+getWidth()/4,up,getWidth()/3,getWidth()/3);

            g2.setColor(Color.white);
            //20 for 688 - for Serif
            //X for main.getHeight();
            int fs=main.getWidth()*20/890;
            Font font = new Font("Serif", Font.BOLD, fs);
            Rectangle att = new Rectangle(getCenterX()-getWidth()/4-getWidth()/3,down-getWidth()/3,getWidth()/3,getWidth()/3);
            drawCenteredString(g2,"3",att,font);
             att = new Rectangle(getCenterX()+getWidth()/4,down-getWidth()/3,getWidth()/3,getWidth()/3);
            drawCenteredString(g2,"3",att,font);
            try {
                BufferedImage tap = ImageIO.read(Main.class.getResourceAsStream("icons/effects/tap.png"));
                g2.drawImage(tap,getCenterX()+getWidth()/4,up,getWidth()/3,getWidth()/3,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int effectsX=getCenterX()-getWidth()/6;
            int effectsY=down-2*getWidth()/3;
            try {
                BufferedImage tap = ImageIO.read(Main.class.getResourceAsStream("icons/effects/poison1.png"));
                g2.drawImage(tap,effectsX,effectsY,getWidth()/3,getWidth()/3,null);
              //  tap = ImageIO.read(Main.class.getResourceAsStream("icons/effects/bonusarmor3.png"));
              //  g2.drawImage(tap,effectsX+getWidth()/3,effectsY,getWidth()/3,getWidth()/3,null);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    void drawImageTapped(Graphics g){
        if (isVisible()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(MyFunction.tapImage(image), getX(), getY(), getHeight()+1, getWidth()+1, null);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(BorderOval));
            g2.drawOval(getX()-BorderOval/2,getY()-BorderOval/2,getHeight()+BorderOval,getWidth()+BorderOval);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(getX()-BorderOval/2-2,getY()-BorderOval/2-2,getHeight()+BorderOval+4,getWidth()+BorderOval+4);
            g2.drawOval(getX()-BorderOval/2+2,getY()-BorderOval/2+2,getHeight()+BorderOval-4,getWidth()+BorderOval-4);
            int down=getY()+getWidth()+1;
            //int up=getCenterY()-getWidth()/2-BorderOval/2-1;
            g2.setColor(Color.BLACK);

            g2.drawRect(getCenterX()-getHeight()/4-getWidth()/3-1,down-getWidth()/3-1,getWidth()/3+1,getWidth()/3+1);
            g2.drawRect(getCenterX()-getHeight()/4-getWidth()/3-1,getY(),getWidth()/3+1,getWidth()/3+1);
            //g2.drawRect(getCenterX()+getWidth()/4-1,up,getWidth()/3+1,getWidth()/3+1);
            g2.setColor(NumberBackColor);
            g2.fillRect(getCenterX()-getHeight()/4-getWidth()/3,down-getWidth()/3,getWidth()/3,getWidth()/3);
            g2.fillRect(getCenterX()-getHeight()/4-getWidth()/3,getY()+1,getWidth()/3,getWidth()/3);
           // g2.fillRect(getCenterX()+getWidth()/4,up,getWidth()/3,getWidth()/3);

            g2.setColor(Color.white);
            //20 for 688 - for Serif
            //X for main.getHeight();
            int fs=main.getWidth()*20/890;
            Font font = new Font("Serif", Font.BOLD, fs);
            Rectangle att = new Rectangle(getCenterX()-getHeight()/4-getWidth()/3,down-getWidth()/3,getWidth()/3,getWidth()/3);
            drawCenteredString(g2,"3",att,font);
            att = new Rectangle(getCenterX()-getHeight()/4-getWidth()/3,getY()+1,getWidth()/3,getWidth()/3);
            drawCenteredString(g2,"3",att,font);
//            try {
//                BufferedImage tap = ImageIO.read(Main.class.getResourceAsStream("icons/effects/tap.png"));
//                g2.drawImage(tap,getCenterX()+getWidth()/4,up,getWidth()/3,getWidth()/3,null);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            int effectsX=getCenterX()-getHeight()/6;
            int effectsY=down-2*getWidth()/3;
            try {
                BufferedImage tap = ImageIO.read(Main.class.getResourceAsStream("icons/effects/poison1.png"));
                g2.drawImage(tap,effectsX,effectsY,getWidth()/3,getWidth()/3,null);
                //  tap = ImageIO.read(Main.class.getResourceAsStream("icons/effects/bonusarmor3.png"));
                //  g2.drawImage(tap,effectsX+getWidth()/3,effectsY,getWidth()/3,getWidth()/3,null);

            } catch (IOException e) {
                e.printStackTrace();
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

    void setBorderOvalColor(Color c){
        borderColor=c;
    }

    void LSD(Graphics g, int x,int y, int w, int h){//Location, Size, Draw! )))
        setLocation(x,y);
        setSize(w,h);
        drawImage(g);
    }

    void LSDtap(Graphics g, int x,int y, int w, int h){//Location, Size, Draw! )))
        setLocation(x,y);
        setSize(w,h);
        drawTapped(g);
    }

    void LSDiftap(Graphics g,boolean t, int x,int y, int w, int h){
        if (t) LSDtap(g,x,y,w,h);
        else LSD(g,x,y,w,h);
    }
    void drawTapped(Graphics g){
        if (isVisible())
            g.drawImage(MyFunction.tapImage(image),getX(),getY()+getHeight()/2-getWidth()/2, getHeight(),getWidth(),null);
    }
}
