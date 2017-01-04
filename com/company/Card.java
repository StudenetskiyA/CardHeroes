package com.company;

/**
 * Created by samsung on 30.12.2016.
 */
public class Card {
    Board board;
    public int cost;
    public String name;
    public String text;
    public String image;
    public int color;
    public int type;//1 for spell, 2 for creature
    public int power;//only for creature, ignore for other
    public int hp;//only for creature and hero, its maximum health, not current

    public Card(Board _board,int _cost, String _name, int _color, int _type, String _text, int _power,int _hp){
        board=_board;
        name=_name;
        text=_text;
        cost=_cost;
        image=_name+".jpg";
        color=_color;
        type=_type;
        power=_power;
        hp=_hp;
    }

    public int getNumericAfterText(String fromText, String afterText){
        int begin = fromText.indexOf(afterText);
        int end1 = text.indexOf(" ",begin+afterText.length()+1);
        if (end1==-1) end1=1000;
        int end2 = text.indexOf(".",begin+afterText.length()+1);
        if (end2==-1) end2=1000;
        int end = Math.min(end1,end2);
        String dmg = fromText.substring(begin+afterText.length()+1,end);
        int numdmg=0;
        try{
            numdmg = Integer.parseInt(dmg);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return numdmg;
    }

    public void playOnCreature(Creature creature){
        if (text.contains("Ранить выбранное существо на")){
            int dmg = getNumericAfterText(text,"Ранить выбранное существо на");
            creature.takeDamage(dmg);
            Main.printToView(creature.name+" получил "+dmg+" урона.");
        }
    }

    public void playOnPlayer(Player player){
        if (text.contains("Ранить выбранного героя на")){
            int dmg = getNumericAfterText(text,"Ранить выбранного героя на");
            player.takeDamage(dmg);
            Main.printToView(player.playerName+" получил "+dmg+" урона.");
        }

    }
}
