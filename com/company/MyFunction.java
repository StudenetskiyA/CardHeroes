package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by samsung on 21.01.2017.
 */
public class MyFunction {

    public static ArrayList<String> getTextBetween(String fromText) {
        ArrayList<String> rtrn = new ArrayList<String>();
        String beforeText = "(";
        fromText = fromText.substring(fromText.indexOf(beforeText) + 1, fromText.length() - 1);
        String[] par = fromText.split(",");
        for (int i = 0; i < par.length; i++)
            rtrn.add(par[i]);
        return rtrn;
    }

    enum Target {myPlayer,myCreature,enemyPlayer,enemyCreature}

    public static boolean canTarget(Target target,int targetType){
        //10 my hero or my creature, not self
        //12 my creature, not self
        if (target==Target.myPlayer)
        {
            if (targetType==2 || targetType==3 || targetType==9 || targetType==10 ) return true;
        }
        else if (target==Target.myCreature)
        {
            if (targetType==1 || targetType==3 || targetType==7 || targetType==9 || targetType==10 || targetType==12 ) return true;
        }
        else if (target==Target.enemyPlayer)
        {
            if (targetType==2 || targetType==3 || targetType==5 || targetType==6) return true;
        }
        else if (target==Target.enemyCreature)
        {
            if (targetType==1 || targetType==3 || targetType==4 || targetType==6) return true;
        }
        return false;
    }

    public static int getNumericAfterText(String fromText, String afterText) {
        int begin = fromText.indexOf(afterText);
        int end1 = fromText.indexOf(" ", begin + afterText.length() + 1);
        if (end1 == -1) end1 = 1000;
        int end2 = fromText.indexOf(".", begin + afterText.length() + 1);
        if (end2 == -1) end2 = 1000;
        int end3 = fromText.indexOf(",", begin + afterText.length() + 1);
        if (end3 == -1) end3 = 1000;
        int end = Math.min(end1, end2);
        end = Math.min(end, end3);
        if (end == 1000) end = fromText.length();
        String dmg = fromText.substring(begin + afterText.length(), end);
        int numdmg = 0;
        try {
            numdmg = Integer.parseInt(dmg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numdmg;
    }

    public static BufferedImage tapImageOnAngle(BufferedImage src,int angle) {
        double rotationRequired = angle;
        AffineTransform tx = new AffineTransform();
        tx.translate(0.5 * src.getHeight(), 0.5 * src.getWidth());
        tx.rotate(rotationRequired);
        tx.translate(-0.5 * src.getWidth(), -0.5 * src.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(src, null);
    }

    public static BufferedImage tapImage(BufferedImage src) {
        double rotationRequired = Math.toRadians(90);
        AffineTransform tx = new AffineTransform();
        tx.translate(0.5 * src.getHeight(), 0.5 * src.getWidth());
        tx.rotate(rotationRequired);
        tx.translate(-0.5 * src.getWidth(), -0.5 * src.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(src, null);
    }

    public static class ClickImage extends JLabel{
        public BufferedImage image;

        ClickImage(){
            super();
        }

        void drawImage(Graphics g){
            if (isVisible())
            g.drawImage(image,getX(),getY(),getWidth(),getHeight(),null);
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
                g.drawImage(tapImage(image),getX(),getY()+getHeight()/2-getWidth()/2, getHeight(),getWidth(),null);
        }
    }
}
