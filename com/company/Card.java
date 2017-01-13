package com.company;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by samsung on 30.12.2016.
 */
public class Card {
    // Board board;
    public int cost;
    public String name;
    public String text;
    public String image;
    public int color;
    public int type;//1 for spell, 2 for creature
    public int targetType;//1 for creatures, 2 for heroes
    public int power;//only for creature, ignore for other
    public int hp;//only for creature and hero, its maximum health, not current
    public String hash;

    public Card(Card _card) {
        name = _card.name;
        text = _card.text;
        cost = _card.cost;
        image = _card.image;
        color = _card.color;
        type = _card.type;
        power = _card.power;
        hp = _card.hp;
        targetType = _card.targetType;
    }

    public Card(int _cost, String _name, int _color, int _type, int _targetType, String _text, int _power, int _hp) {
        //   board=_board;
        name = _name;
        text = _text;
        cost = _cost;
        image = _name + ".jpg";
        color = _color;
        type = _type;
        power = _power;
        hp = _hp;
        targetType = _targetType;
    }


    public static ArrayList<String> getTextBetween(String fromText) {
        ArrayList<String> rtrn = new ArrayList<String>();
        String beforeText = "(";
        fromText = fromText.substring(fromText.indexOf(beforeText) + 1, fromText.length() - 1);
        String[] par = fromText.split(",");
        for (int i = 0; i < par.length; i++)
            rtrn.add(par[i]);
        return rtrn;
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

    public int getNumericAfterText(String fromText, String afterText) {
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

    public void playOnCreature(Creature creature) {
        if (text.contains("Ранить выбранное существо на ")) {
            int dmg = getNumericAfterText(text, "Ранить выбранное существо на ");
            creature.takeDamage(dmg);
            Main.printToView(creature.name + " получил " + dmg + " урона.");
        }
    }

    public void playOnPlayer(Player _player) {
        if (text.contains("Ранить выбранного героя на ")) {
            int dmg = getNumericAfterText(text, "Ранить выбранного героя на ");
            _player.takeDamage(dmg);
            Main.printToView(_player.playerName + " получил " + dmg + " урона.");
        }

    }

    public static Card getCardByName(String name) {
        if (name.equals("Раскат грома"))
            return new Card(1, "Раскат грома", 1, 1, 1, "Ранить выбранное существо на 3.", 0, 0);
        else if (name.equals("Гьерхор"))
            return new Card(1, "Гьерхор", 1, 2, 0, "", 2, 2);
        else if (name.equals("Найтин"))
            return new Card(2, "Найтин", 2, 2, 0, "Направленный удар. Рывок.", 2, 2);
        else if (name.equals("Кригторн"))
            return new Card(2, "Кригторн", 2, 2, 0, "Первый удар. Рывок.", 2, 1);
        else if (name.equals("Гном"))
            return new Card(2, "Гном", 1, 2, 0, "", 3, 3);
        else if (name.equals("Поглощение души"))
            return new Card(3, "Поглощение душ", 1, 1, 2, "Ранить выбранного героя на 3. Излечить вашего героя на 3.", 0, 0);
        else if (name.equals("Эльф-дозорный"))
            return new Card(4, "Эльф-дозорный", 1, 2, 0, "Найм: Возьмите карту.", 2, 5);
        else if (name.equals("Послушник"))
            return new Card(5, "Послушник", 1, 2, 1, "Найм: Выстрел по существу на 4.", 2, 3);
        else if (name.equals("Гном-лучник"))
            return new Card(3, "Гном-лучник", 1, 2, 3, "Защита от выстрелов. Найм: Выстрел на 2.", 2, 3);
        else if (name.equals("Лучник Захры"))
            return new Card(4, "Лучник Захры", 1, 2, 3, "Защита от заклинаний. Найм: Выстрел на 2.", 4, 2);
        else if (name.equals("Цепная молния"))
            return new Card(6, "Цепная молния", 1, 1, 0, "Ранить каждое существо противника на 3.", 0, 0);
        else {
            System.out.println("Ошибка - Неопознанная карта.");
            return null;
        }
    }
}
