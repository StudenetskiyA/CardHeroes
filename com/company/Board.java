package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by samsung on 30.12.2016.
 */
public class Board {
    public static List<ArrayList<Creature>> creature;

    public static int turnCount = 0;

    public Board() {
    }

    public static int getPl(String _name) {
        if (_name.equals(Main.players[0].playerName)) return 0;
        else if (_name.equals(Main.players[1].playerName)) return 1;
        else {
            System.out.println("Error - Unknow player.");
            return -1;
        }
    }

    public static void battlecryNoTarget(Creature _card,Player _owner){
       }

    public static void battlecryTarget(Creature _creature) {
        Main.isMyTurn = Main.playerStatus.choiseTarget;
        Main.activatedAbility.creature = _creature;
        Main.activatedAbility.targetType= _creature.targetType;
        Main.activatedAbility.tapTargetType= _creature.tapTargetType;
        Main.activatedAbility.creatureTap=false;
    }


    public static void addCreatureToBoard(Card _creature, Player _player) {
        Creature summonCreature = new Creature(_creature, _player);
        if (_creature.text.contains("Рывок.")) {
            summonCreature.isSummonedJust = false;
        }
        if (_creature.text.contains("Уникальность.")) {
            for (int i = Board.creature.get(_player.numberPlayer).size() - 1; i >= 0; i--) {
                if (Board.creature.get(_player.numberPlayer).get(i).name.equals(_creature.name)){
                    Board.creature.get(_player.numberPlayer).get(i).die();
                }
            }
        }
        int np = _player.numberPlayer;
        creature.get(np).add(summonCreature);
        Main.printToView("Вызов существа " + summonCreature.name);
        if (_creature.text.contains("Наймт:")) {
            battlecryTarget(summonCreature);
        }
        if (_creature.text.contains("Найм:")) {
            battlecryNoTarget(summonCreature,_player);
        }
    }

    public static int opponentN(Player pl) {
        if (pl.numberPlayer == 0) return 1;
        else return 0;
    }

    public static Player opponent(Player pl) {
        if (pl.numberPlayer == 0) return Main.players[1];
        else return Main.players[0];
    }

    public static void putCardToGraveyard(Card _card, Player _owner) {
        _owner.graveyard.add(_card);
    }

    public static void removeCreatureFromPlayerBoard(Creature _creature) {
        // int n=creature.get(_creature.owner.numberPlayer).indexOf(_creature);
        //move

        creature.get(_creature.owner.numberPlayer).remove(_creature);//TODO May be BUG!

        _creature = null;
    }
}
