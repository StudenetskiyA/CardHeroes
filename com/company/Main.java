package com.company;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
         Board board =new Board();

        Card simpleCard = new Card(board);
        simpleCard.name="1";
        simpleCard.cost=1;
        Card simpleCard2 = new Card(board);
        simpleCard2.name="2";
        simpleCard2.cost=2;

        Card simpleCard3 = new Card(board);
        simpleCard3.name="3";
        simpleCard3.cost=1;
        simpleCard3.type=2;
        simpleCard3.power=1;
        simpleCard3.hp=2;

        ArrayList<Card> simpleDeckCards = new ArrayList<Card>();
        simpleDeckCards.add(simpleCard);
        simpleDeckCards.add(simpleCard2);
        simpleDeckCards.add(simpleCard3);

        Deck simpleDeck = new Deck(simpleDeckCards);
        Player player = new Player(simpleDeck,board);
        player.hp=30;
        Player enemy = new Player(simpleDeck,board);
        enemy.hp=30;
        
        player.newTurn();
        //System.out.println("PlayerCard:"+player.handToString());
        //System.out.println("InDeckCard:"+player.deck.deckToString());
        System.out.println("HPD:"+player.damage+"/"+enemy.damage+","+"Board:"+player.board.boardToString());
        System.out.println("Draw");
        player.drawCard();
        //System.out.println("PlayerCard:"+player.handToString());
        //System.out.println("InDeckCard:"+player.deck.deckToString());
        System.out.println("Play card 3");
        player.playCard(0);
        System.out.println("Board:"+player.board.boardToString());
//        board.playerCreature.get(0).takeDamage(1);
//        System.out.println("Board:"+player.board.boardToString());
//        board.playerCreature.get(0).takeDamage(1);
//        System.out.println("Board:"+player.board.boardToString());
        player.newTurn();
        board.playerCreature.get(0).attackPlayer(enemy);
        System.out.println("HPD:"+player.damage+"/"+enemy.damage+","+"Board:"+player.board.boardToString());
        System.out.println("PlayerCard:"+player.handToString());
        System.out.println("InDeckCard:"+player.deck.deckToString());
    }
}
