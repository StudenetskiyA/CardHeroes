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
}
