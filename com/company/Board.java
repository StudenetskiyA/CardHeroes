package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by samsung on 30.12.2016.
 */
public class Board {
    public static List<ArrayList<Creature>> creature;
    public static Player firstPlayer;
    public static Player secondPlayer;
    public static boolean isActiveFirst;

    public static int turnCount = 0;

    public Board() {
        // firstPlayer = _f;
        // secondPlayer = _s;
        creature = new ArrayList<ArrayList<Creature>>(2);
        creature.add(new ArrayList<Creature>());
        creature.add(new ArrayList<Creature>());
        isActiveFirst = true;
    }

    public static void battlecry(Creature _creature) {
        String txt = _creature.text.substring(_creature.text.indexOf("Найм:") + "Найм:".length() + 1, _creature.text.indexOf(".", _creature.text.indexOf("Найм:") + 1));
      //  System.out.println("Найм:" + txt);
//I want to switch, but Java doesn't have it((

        if (txt.contains("Возьмите карту")) {
            _creature.owner.drawCard();
        } else if ((txt.contains("Выстрел по существу на ") ) || (txt.contains("Выстрел на "))) {//TODO if no target and many other
            Main.isMyTurn= Main.playerStatus.choiseTarget;
            Main.activatedAbilityCreature=_creature;
        }
    }


    public static void addCreatureToBoard(Card _creature, Player _player) {
        Creature summonCreature = new Creature(_creature, _player);
        if (_creature.text.contains("Рывок.")) {
            summonCreature.isSummonedJust = false;
        }
        int np = _player.numberPlayer;
        creature.get(np).add(summonCreature);
        Main.printToView("Вызов существа " + summonCreature.name);
        if (_creature.text.contains("Найм:")) {
            battlecry(summonCreature);
        }
    }

    public static int opponentN(Player pl){
        if (pl.numberPlayer==0) return 1;
        else return 0;
    }

    public static Player opponent(Player pl) {
        if (pl == firstPlayer) return secondPlayer;
        else return firstPlayer;
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
