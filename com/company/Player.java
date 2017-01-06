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
        super(_board,0,"Тарна",1,0,"",0,_hp);
        deck=_deck;
        playerName=_playerName;
        cardInHand = new ArrayList<Card>();
        graveyard = new ArrayList<Card>();
        numberPlayer=_n;
    }

    public void endTurn(){
        newTurn();
//    if (board.isActiveFirst){
//        board.isActiveFirst=false;
//        board.secondPlayer.newTurn();
//    }
//    else{
//        board.isActiveFirst=true;
//        board.secondPlayer.newTurn();
//        while (true){
//            String fromServer = Client.readFromServer();
//            if (fromServer!=null) Main.printToView(fromServer);
//        }
//    }
    }

    public void newTurn(){
        board.turnCount++;
        Main.printToView("Ход номер "+board.turnCount+", игрок "+playerName);

        //Get coin
        if (totalCoin<10) totalCoin++;
        //Untap
        untappedCoin=totalCoin;

        for (Creature creature:board.creature.get(numberPlayer)
             ) {
            creature.isSummonedJust=false;
            creature.isTapped=false;
        }
        //Draw
        drawCard();
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
                    int t=0;
                    if (_targetPlayer.playerName.equals(playerName)) t=2;
                    else t=1;
                    System.out.println("$PLAYCARD(" +playerName+","+ num + ",-1,"+t+")");
                    Client.sendToServer("$PLAYCARD(" + playerName+","+num + ",-1,"+t+")");
                }
                if (_targetCreature != null) {
                    _card.playOnCreature(_targetCreature);
                    int n= board.creature.get(numberPlayer).indexOf(_targetCreature);
                    System.out.println("$PLAYCARD(" +playerName+","+ num + ","+n+",-1)");
                    Client.sendToServer("$PLAYCARD(" +playerName+","+ num + ","+n+",-1)");
                }
            }
            else if (_card.type==2){
                //creature
                board.addCreatureToBoard(_card,this);
                System.out.println("$PLAYCARD(" +playerName+","+ num + "," + "-1,-1)");
                Client.sendToServer("$PLAYCARD(" +playerName+","+ num + "," + "-1,-1)");
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
            Main.printToView("Deck is empty.");
        }
        Main.printToView("Игрок "+playerName+" берет карту.");
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
