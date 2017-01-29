package com.company;

import java.util.ArrayList;
import java.util.List;

import static com.company.Main.main;

/**
 * Created by StudenetiskiyA on 30.12.2016.
 */
public class Board {
    static List<ArrayList<Creature>> creature;
  ///  public static NewTurnQueue newTurnQueue = new NewTurnQueue();

    static int turnCount = 0;

    public Board() {
    }

    static int getPlayerNumByName(String _name) {
        if (_name.equals(Main.players[0].playerName)) return 0;
        else if (_name.equals(Main.players[1].playerName)) return 1;
        else {
            System.out.println("Error - Unknown player.");
            return -1;
        }
    }
    static Player getPlayerByName(String _name) {
        if (_name.equals(Main.players[0].playerName)) return Main.players[0];
        else if (_name.equals(Main.players[1].playerName)) return Main.players[1];
        else {
            System.out.println("Error - Unknown player.");
            return null;
        }
    }
    static void addCreatureToBoard(Card _creature, Player _player) {
        Creature summonCreature = new Creature(_creature, _player);


        int np = _player.numberPlayer;
        creature.get(np).add(summonCreature);
        Main.gameQueue.push(new GameQueue.QueueEvent("Summon",summonCreature,0));

        if (_creature.text.contains("Уникальность.")) {
            for (int i = Board.creature.get(_player.numberPlayer).size() - 1; i >= 0; i--) {
                if (Board.creature.get(_player.numberPlayer).get(i).name.equals(_creature.name)){
                    Board.creature.get(_player.numberPlayer).get(i).die();
                    break;
                }
            }
        }
        if (summonCreature.getTougness()<=0){
            summonCreature.die();
        }
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

}
