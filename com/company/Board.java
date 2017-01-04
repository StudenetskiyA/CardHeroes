package com.company;

import java.util.ArrayList;

/**
 * Created by samsung on 30.12.2016.
 */
public class Board {
    public ArrayList<Creature> playerCreature;
    public ArrayList<Creature> enemyCreature;

    public int turnCount=0;

    public Board(){
        playerCreature = new ArrayList<Creature>();
        enemyCreature = new ArrayList<Creature>();
    }

    public void addCreatureToBoard(Card _creature, Player _player){
        Creature summonCreature = new Creature(_creature,this,_player);
        if (_creature.text.contains("%Рывок%")){
            summonCreature.isSummonedJust=false;
        }
        playerCreature.add(summonCreature);
        Main.gameLog.setText(Main.gameLog.getText()+"Вызов существа "+summonCreature.name+".<br>");
    }

    public void removeCreatureFromPlayerBoard(Creature _creature){
        playerCreature.remove(_creature);//TODO Check it
    }
}
