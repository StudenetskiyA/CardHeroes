package com.company;

import java.util.ArrayList;

/**
 * Created by samsung on 30.12.2016.
 */
public class Player extends Card{
    public int numberPlayer;
    public int damage;
    String playerName;
    public int totalCoin;
    public int untappedCoin;
    public Deck deck;
    public ArrayList<Card> cardInHand;
    public ArrayList<Card> graveyard;

    public Player(Deck _deck, Board _board,String _playerName,int _n, int _hp){
        super(0,"Тарна",1,0,0,"",0,_hp);
        deck=_deck;
        playerName=_playerName;
        cardInHand = new ArrayList<Card>();
        graveyard = new ArrayList<Card>();
        numberPlayer=_n;
    }

    public void endTurn(){
    if (Board.isActiveFirst){
        Board.isActiveFirst=false;
        System.out.println("$NEWTURN(" +Board.secondPlayer.playerName+")");
        Client.writeLine("$NEWTURN(" +Board.secondPlayer.playerName+")");
    }
    else{
        Board.isActiveFirst=true;
        System.out.println("$NEWTURN(" +Board.firstPlayer.playerName+")");
        Client.writeLine("$NEWTURN(" +Board.firstPlayer.playerName+")");
    }
    }

    public void newTurn(){
        Board.turnCount++;
        Main.printToView("Ход номер "+Board.turnCount+", игрок "+playerName);

        //Get coin
        if (totalCoin<10) totalCoin++;
        //Untap
        untappedCoin=totalCoin;

        for (Creature creature:Board.creature.get(numberPlayer)
             ) {
            creature.isSummonedJust=false;
            creature.isTapped=false;
        }
        //Draw
        if (Board.turnCount!=1) drawCard();//First player not draw card in first turn. It's rule.
    }

    void playCard(Card _card, Creature _targetCreature, Player _targetPlayer){
        int num = cardInHand.indexOf(_card);
        if (num==-1) return;

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
                if (_card.text.contains(("Излечить вашего героя на "))){
                    int dmg = getNumericAfterText(_card.text,"Излечить вашего героя на ");
                    heal(dmg);
                    Main.printToView(this.playerName+" излечил "+dmg+" урона.");
                }
                Board.putCardToGraveyard(_card,this);
            }
            else if (_card.type==2){
                //creature
                Board.addCreatureToBoard(_card,this);
            }
            //remove from hand
            cardInHand.remove(_card);
        }
        else{
            Main.printToView("Не хватает монет.");
        }
    }
    void drawCard(){
        if (deck.haveTopDeck())
        cardInHand.add(deck.drawTopDeck());
        else {
            Main.printToView("Deck of "+playerName+" is empty.");
        }
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
    public void heal(int dmg){
        damage-=dmg;
        if (damage<0) damage=0;
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
