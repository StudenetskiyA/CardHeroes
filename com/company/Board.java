package com.company;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samsung on 30.12.2016.
 */
public class Board {
    public List<ArrayList<Creature>> creature;
    public Player firstPlayer;
    public Player secondPlayer;
    public boolean isActiveFirst;

    public int turnCount=0;

    public Board(Player _f, Player _s){
        firstPlayer = _f;
        secondPlayer = _s;
        creature = new ArrayList<ArrayList<Creature>> (2);
        creature.add(new ArrayList<Creature>());
        creature.add(new ArrayList<Creature>());
     }

    public void addCreatureToBoard(Card _creature, Player _player){
        Creature summonCreature = new Creature(_creature,this,_player);
        if (_creature.text.contains("%Рывок%")){
            summonCreature.isSummonedJust=false;
        }
        int np = _player.numberPlayer;
        creature.get(np).add(summonCreature);
       Main.printToView("Вызов существа "+summonCreature.name);
    }

    public void removeCreatureFromPlayerBoard(Creature _creature){
        creature.get(_creature.owner.numberPlayer).remove(_creature);//TODO Check it
    }
}
