package com.company;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by samsung on 30.12.2016.
 */
public class Player extends Card{
    public int damage;
    String playerName;
    public int totalCoin;
    public int untappedCoin;
    public Deck deck;
    public ArrayList<Card> cardInHand;

    public Player(Deck _deck, Board _board,String _playerName,int _hp){
        super(_board,0,"Тарна",1,0,"",0,_hp);
        deck=_deck;
        playerName=_playerName;
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
            creature.isTapped=false;
        }
        //Draw
        drawCard();
    }

    void playCard(Card _card, Creature _targetCreature, Player _targetPlayer){
      //  Card _card=cardInHand.get(cardN);
        if (untappedCoin>=_card.cost){
            untappedCoin-=_card.cost;
            //put on table or cast spell
            if (_card.type==1) {
                //release text on spell
                //check target
                if (_targetPlayer != null) {
                    _card.playOnPlayer(_targetPlayer);
                }
                if (_targetCreature != null) {
                    _card.playOnCreature(_targetCreature);
                }
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
