package ru.berserk.client;

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
        fromText = fromText.substring(fromText.indexOf(beforeText) + 1, fromText.indexOf(")"));
        String[] par = fromText.split(",");
        for (int i = 0; i < par.length; i++)
            rtrn.add(par[i]);
        return rtrn;
    }

    public static int getEquipNumByType(String creatureType) {
        switch(creatureType) {
            case "Оружие":
                return 2;
            case "Броня":
                return 0;
            case "Амулет":
                return 1;
            case "Событие":
                return 3;
        }
        return -1;
    }

    enum DamageSource {fight, spell, poison, ability, scoot}
    enum EffectPlayer{
        bbShield(1), bonusToShoot(2);

        private final int value;

        EffectPlayer(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static EffectPlayer fromInteger(int x) {
            switch(x) {
                case 1:
                    return bbShield;
                case 2:
                    return bonusToShoot;
            }
            return null;
        }
    }
    enum Effect{
        poison(1), vulnerability(2),turnToDie(3), die(4), bonusPowerUEOT(5), bonusPower(6), bonusTougnessUEOT(7), bonusTougness(8),
        bonusArmor(9), cantattackandblock(10), controlChanged(11), notOpenAtBeginNextTurn(12);

        private final int value;

        Effect(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Effect fromInteger(int x) {
            switch(x) {
                case 1:
                    return poison;
                case 2:
                    return vulnerability;
                case 3:
                    return turnToDie;
                case 4:
                    return die;
                case 5:
                    return bonusPowerUEOT;
                case 6:
                    return bonusPower;
                case 7:
                    return bonusTougnessUEOT;
                case 8:
                    return bonusTougness;
                case 9:
                    return bonusArmor;
                case 10:
                    return cantattackandblock;
                case 11:
                    return controlChanged;
                case 12:
                    return notOpenAtBeginNextTurn;
            }
            return null;
        }
    }
    enum MessageType{ simpleText, choiceTarget, error, win, loose}
    enum Target {myPlayer,myCreature,enemyPlayer,enemyCreature,myEquip,enemyEquip,myEvent,enemyEvent}

    static class ActivatedAbility {

        static int nonCreatureTargetType;
        static int nonCreatureTargetCost;
        static Creature creature;
        static boolean creatureTap;
        static WhatAbility whatAbility=WhatAbility.nothing;
        static int whatEquip;
        static int heroAbilityCost = 0;
        static int heroAbilityN=0;

        public static boolean isThatAbility(WhatAbility ab){
            if (ab==whatAbility) return true;
            return false;
        }
        public static boolean isNothingOrDeath(){
            //if (whatAbility==WhatAbility.nothing) return true;
            if (whatAbility==WhatAbility.onCryAbility) return true;
            if (whatAbility==WhatAbility.onDeathPlayed) return true;
            if (whatAbility==WhatAbility.onUpkeepPlayed) return true;
            if (whatAbility==WhatAbility.onOtherDeathPlayed) return true;
            if (whatAbility==WhatAbility.onTapAbility) return true;
            return false;
        }
        enum WhatAbility {
            heroAbility(1), weaponAbility(2), toHandAbility(3), onUpkeepPlayed(4), onDeathPlayed(5), onOtherDeathPlayed(6),
            spellAbility(7), onCryAbility(8), equipAbility(9), nothing(0), onTapAbility(10);

            private final int value;

            WhatAbility(int value) {
                this.value = value;
            }

            public int getValue() {
                return value;
            }

            public static WhatAbility fromInteger(int x) {
                switch (x) {
                    case 0:
                        return nothing;
                    case 1:
                        return heroAbility;
                    case 2:
                        return weaponAbility;
                    case 3:
                        return toHandAbility;
                    case 4:
                        return onUpkeepPlayed;
                    case 5:
                        return onDeathPlayed;
                    case 6:
                        return onOtherDeathPlayed;
                    case 7:
                        return spellAbility;
                    case 8:
                        return onCryAbility;
                    case 9:
                        return equipAbility;
                    case 10:
                        return onTapAbility;
                }
                return null;
            }
        }
    }

    public static boolean canTarget(Target target,int targetType){
        //10 my hero or my creature, not self
        //12 my creature, not self
        //13 any creature, not self
        //21 any equip
        //22 any event
        if (target==Target.myPlayer)
        {
            if (targetType==2 || targetType==3 || targetType==9 || targetType==10 ) return true;
        }
        else if (target==Target.myCreature)
        {
            if (targetType==1 || targetType==3 || targetType==7 || targetType==9 || targetType==10 || targetType==12 || targetType==13) return true;
        }
        else if (target==Target.enemyPlayer)
        {
            if (targetType==2 || targetType==3 || targetType==5 || targetType==6) return true;
        }
        else if (target==Target.enemyCreature)
        {
            if (targetType==1 || targetType==3 || targetType==4 || targetType==6) return true;
        }
        else if (target==Target.myEquip || target==Target.enemyEquip)
        {
            if (targetType==21) return true;
        }
        else if (target==Target.myEvent || target==Target.enemyEvent)
        {
            if (targetType==22) return true;
        }
        if (targetType==0) return true;
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

    public static String getTextBetweenSymbol(String fromText, String afterText, String symbol){
        return fromText.substring(fromText.indexOf(afterText)+afterText.length(),fromText.indexOf(symbol,fromText.indexOf(afterText)+afterText.length()));
    }

    public static String textNotInTake(String fromText){
        if (!fromText.contains("'")) return fromText;
        String tmp = getTextBetweenSymbol(fromText,fromText.substring(0,fromText.indexOf("'")+1),"'");
        tmp=fromText.substring(0,fromText.indexOf(tmp))+fromText.substring(fromText.indexOf(tmp)+1+tmp.length(),fromText.length());
        return tmp;
    }

    public static boolean canTargetComplex(int targetType){
        boolean canTarget=false;
        if (Board.creature.get(0).size() > 0 && MyFunction.canTarget(MyFunction.Target.myCreature,targetType)) canTarget=true;
        if (Board.creature.get(1).size() > 0 && MyFunction.canTarget(MyFunction.Target.enemyCreature,targetType)) canTarget=true;
        if (MyFunction.canTarget(MyFunction.Target.enemyPlayer,targetType)) canTarget=true;//Both players always stay on board
        if (MyFunction.canTarget(MyFunction.Target.myPlayer,targetType)) canTarget=true;
        return canTarget;
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
