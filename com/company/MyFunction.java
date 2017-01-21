package com.company;

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

    public static int getNumericAfterText(String fromText, String afterText) {
        int begin = fromText.indexOf(afterText);
        int end1 = fromText.indexOf(" ", begin + afterText.length() + 1);
        if (end1 == -1) end1 = 1000;
        int end2 = fromText.indexOf(".", begin + afterText.length() + 1);
        if (end2 == -1) end2 = 1000;
        int end = Math.min(end1, end2);
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

    public static BufferedImage tapImage(BufferedImage src) {
        double rotationRequired = Math.toRadians(90);
        AffineTransform tx = new AffineTransform();
        tx.translate(0.5 * src.getHeight(), 0.5 * src.getWidth());
        tx.rotate(rotationRequired);
        tx.translate(-0.5 * src.getWidth(), -0.5 * src.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(src, null);
    }
}
