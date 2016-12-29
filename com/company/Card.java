package com.company;

/**
 * Created by samsung on 30.12.2016.
 */
public class Card {
    Board board;
    public int cost;
    public String name;
    public String text;
    public int color;
    public int type;//1 for spell
    public int power;//only for creature, ignore for other
    public int hp;//only for creature and hero

    public Card(Board _board){
        board=_board;
    }
}
