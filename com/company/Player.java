package com.company;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by samsung on 30.12.2016.
 */
public class Player extends Card{
    int damage;
   // public int hp;
    String playerName;
    public int totalCoin;
    public int untappedCoin;
    public Deck deck;
    public ArrayList<Card> cardInHand;
   // public String heroName;

    public Player(Deck _deck, Board _board){
        super(_board,0,"Player",1,0,"",0,0);
        deck=_deck;
        cardInHand = new ArrayList<Card>();
    }

    public void newTurn(){
       // System.out.println("New turn("+board.turnCount+")");
        board.turnCount++;
        Main.gameLog.setText(Main.gameLog.getText()+"<html>Ход номер "+board.turnCount+", игрок "+playerName+"<br>");

        //Get coin
        if (totalCoin<10) totalCoin++;
        //Untap
        untappedCoin=totalCoin;
        for (Creature creature:board.playerCreature//BAD
             ) {
            creature.isSummonedJust=false;
        }
        //Draw
        drawCard();
    }

    void playCard(Card _card){
      //  Card _card=cardInHand.get(cardN);
        if (untappedCoin>=_card.cost){
            untappedCoin-=_card.cost;
            //put on table or cast spell
            if (_card.type==1){
                //release text on spell
            }
            else if (_card.type==2){
                //creature
                board.addCreatureToPlayerBoard(_card);
            }
            //remove from hand
            cardInHand.remove(_card);
        }
        else{
            Main.gameLog.setText(Main.gameLog.getText()+"Не хватает монет.<br>");
        }
    }
    void drawCard(){
        cardInHand.add(deck.drawTopDeck());
        Main.gameLog.setText(Main.gameLog.getText()+"Игрок "+playerName+" берет карту.<br>");
    }

    public void takeDamage(int dmg){
        if (hp>damage+dmg){
            damage+=dmg;
        }
        else {
            System.out.println("Player lose game.");
            //TODO Lose play
        }
    }

    public String handToString(){
        String tmp="";
        for (Card card: cardInHand
             ) {
            tmp+=card.name;
            tmp+=",";
        }
        return tmp;
    }
}
