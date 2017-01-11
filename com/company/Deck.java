package com.company;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by samsung on 30.12.2016.
 */
public class Deck {
    public ArrayList<Card> cards = new ArrayList<>();

    public String name;

    public Deck(String _name){
        name=_name;
    }

    public int getCardExpiried(){
        return cards.size();
    }

    public boolean haveTopDeck(){
        if (cards.size()==0) return false;
        else return true;
    }
    public Card drawTopDeck(){
        Card tmp=cards.get(cards.size()-1);
        cards.remove(cards.size()-1);
        return tmp;
    }

    public void putOnBottomDeck(Card _card){
        cards.add(0,_card);
    }

    class intI{
        int n;
        int index;
        intI(int _n,int _index){
            n=_n;
            index=_index;
        }
    }
    public void suffleDeck(int n){
        //Until server know nothing
        for (int i=0;i<cards.size();i++){
            byte[] b = (cards.get(i).name+i+n).getBytes();
            try {
                byte[] hash = MessageDigest.getInstance("MD5").digest(b);
                String a=DatatypeConverter.printHexBinary(hash);
                cards.get(i).hash=a;
               // System.out.println(cards.get(i).name+"/"+a);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(cards, Comparator.comparing(o -> o.hash));
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
