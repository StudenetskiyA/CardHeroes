package ru.berserk.client;

import java.util.ArrayList;
import java.util.List;

//Created by StudenetskiyA on 30.12.2016.

public class Board {
    static List<ArrayList<Creature>> creature;
    static int turnCount = 0;

    public Board() {
    }

    public static Equpiment getEqupimentByID(String id){
        for (int i=0;i<Main.players[0].equpiment.length;i++){
            if (Main.players[0].equpiment[i]!=null && Main.players[0].equpiment[i].id.equals(id))
                return Main.players[0].equpiment[i];
        }
        for (int i=0;i<Main.players[1].equpiment.length;i++){
            if (Main.players[1].equpiment[i]!=null && Main.players[1].equpiment[i].id.equals(id))
                return Main.players[1].equpiment[i];
        }
        System.out.println("Not found equpiment by ID");
        return null;
    }

    public static Creature getCreatureByID(int playerNum, String id){
        for (int i=0;i<Board.creature.get(playerNum).size();i++){
            if (creature.get(playerNum).get(i).id.equals(id)) return creature.get(playerNum).get(i);
        }
        System.out.println("Not found creature by ID");
        return null;
    }

    public static Creature getCreatureByID(String id){
        for (int i=0;i<Board.creature.get(0).size();i++){
            if (creature.get(0).get(i).id.equals(id)) return creature.get(0).get(i);
        }
        for (int i=0;i<Board.creature.get(1).size();i++){
            if (creature.get(1).get(i).id.equals(id)) return creature.get(1).get(i);
        }
        System.out.println("Not found creature by ID");
        return null;
    }

    static int opponentN(Player pl) {
        if (pl.numberPlayer == 0) return 1;
        else return 0;
    }

    public static Player opponent(Player pl) {
        if (pl.numberPlayer == 0) return Main.players[1];
        else return Main.players[0];
    }

    static int getDiedCreatureLeftCount(int playerNum, int nc){
        int count=0;
        for (int i=0;i<nc;i++){
            if (creature.get(playerNum).get(i).isDie()) count++;
        }
        return count;
    }
}
