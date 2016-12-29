package com.company;

import java.util.ArrayList;

/**
 * Created by samsung on 30.12.2016.
 */
public class Board {
    public ArrayList<Creature> playerCreature;
    public ArrayList<Creature> enemyCreature;

    public int turnCount=1;

    public Board(){
        playerCreature = new ArrayList<Creature>();
        enemyCreature = new ArrayList<Creature>();
    }

    public void addCreatureToPlayerBoard(Card _creature){
        Creature summonCreature = new Creature(_creature,this);
        if (_creature.text.contains("%Рывок%")){
            summonCreature.isSummonedJust=false;
        }

        playerCreature.add(summonCreature);

    }

    public void removeCreatureFromPlayerBoard(Creature _creature){
        playerCreature.remove(_creature);//TODO Check it
    }

    public String boardToString(){
        String tmp="PlayerCreature:";
        for (Creature card: playerCreature
                ) {
            tmp+=card.name+"("+card.power+","+card.tougness+","+card.damage+","+card.isTapped+","+card.isSummonedJust+")";
            tmp+=",";
        }
        return tmp;
    }
}
