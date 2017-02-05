package com.company;

import java.util.*;

//Created by StudenetskiyA on 30.12.2016.

//This class is depricated. In future, pllayer knows nothing about yourself deck.
public class Deck {
    public ArrayList<Card> cards = new ArrayList<>();

    public String name;

    public Deck(String _name){
        name=_name;
    }

    public int getCardExpiried(){
        return cards.size();
    }

    public void removeCardFromDeckByName(String name){
        for (Card c:cards){
            if (c.name.equals(name)){
                cards.remove(c);
                break;
            }
        }
    }

    public String deckToString(){
        String tmp="";
        for (Card card: cards
                ) {
            tmp+=card.name;
            tmp+=",";
        }
        return tmp;
    }

}
