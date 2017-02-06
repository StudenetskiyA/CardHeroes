package com.company;

import java.util.ArrayList;
import java.util.List;

//Created by StudenetskiyA on 30.12.2016.

public class Board {
    static List<ArrayList<Creature>> creature;
    static int turnCount = 0;

    public Board() {
    }

    static int opponentN(Player pl) {
        if (pl.numberPlayer == 0) return 1;
        else return 0;
    }

    public static Player opponent(Player pl) {
        if (pl.numberPlayer == 0) return Main.players[1];
        else return Main.players[0];
    }

    static void putCardToGraveyard(Card _card, Player _owner) {
        _owner.graveyard.add(_card);
    }

    static int getDiedCreatureLeftCount(int playerNum, int nc){
        int count=0;
        for (int i=0;i<nc;i++){
            if (creature.get(playerNum).get(i).isDie()) count++;
        }
        return count;
    }
}
