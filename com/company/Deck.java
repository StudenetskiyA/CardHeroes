package com.company;

import java.util.ArrayList;

/**
 * Created by samsung on 30.12.2016.
 */
public class Deck {
    public ArrayList<Card> cards;

    public Deck(ArrayList<Card> _cards){
        cards=_cards;
    }

    public int getCardExpiried(){
        return 0;
    }

    public Card drawTopDeck(){
        Card tmp=cards.get(cards.size()-1);
        cards.remove(cards.size()-1);
        return tmp;
    }

    public void suffleDeck(){

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
