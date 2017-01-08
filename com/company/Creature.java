package com.company;

import java.util.ArrayList;

/**
 * Created by samsung on 30.12.2016.
 */
public class Creature extends Card {
    public int power;
    public int tougness;
    public boolean isTapped;
    public boolean isSummonedJust;
    public Player owner;
    int damage;


    public Creature(Card _card, Player _owner){
        super(_card.cost,_card.name,_card.color,_card.type,_card.text,_card.power,_card.hp);
    power = _card.power;
    tougness = _card.hp;
    image = _card.image;
    cost = _card.cost;
    isTapped=false;
    isSummonedJust=true;
    name= _card.name;
    owner=_owner;
    }

    void tapCreature(){
        isTapped=true;
    }

    ArrayList<Creature> canAnyoneBlock(Creature target){
        int pl;
        if (owner.numberPlayer==0) pl=1;
        else pl=0;

        //get list of opponent creature
        ArrayList<Creature> crt = new ArrayList<>(Board.creature.get(pl));
        //delete from it tapped
        for (int i=0;i<crt.size();i++){
        //for (Creature cr:creature){
            if (crt.get(i).isTapped) crt.remove(crt.get(i));
        }
        //delete from it target
        if (crt.contains(target)) crt.remove(target);
    return crt;
    }

    public void attackCreature(Creature target){
                tapCreature();
                //TODO Block Check
        ArrayList<Creature> blocker = canAnyoneBlock(target);
        if (blocker.size()!=0){
            int nc = Board.creature.get(owner.numberPlayer).indexOf(this);
            int nt = Board.creature.get(Board.opponent(owner).numberPlayer).indexOf(target);
            System.out.println("$CHOISEBLOCKER(" +Board.opponent(owner).playerName+","+nc+","+nt+")");
            Client.writeLine("$CHOISEBLOCKER(" +Board.opponent(owner).playerName+","+nc+","+nt+")");
            for (Creature cr: blocker){
                Main.printToView(cr.name+" can block!");
            }
        }

                //TODO firststrike
                target.takeDamage(power);
                takeDamage(target.power);
                Main.printToView(name+" атакует: "+target.name+".");
    }

    public void attackPlayer(Player target){
                tapCreature();
                //TODO Block Check
        ArrayList<Creature> blocker = canAnyoneBlock(null);
        if (blocker.size()!=0){
            int nc = Board.creature.get(owner.numberPlayer).indexOf(this);
            int nt = -1;
            System.out.println("$CHOISEBLOCKER(" +Board.opponent(owner).playerName+","+nc+","+nt+")");
            Client.writeLine("$CHOISEBLOCKER(" +Board.opponent(owner).playerName+","+nc+","+nt+")");

            for (Creature cr: blocker){
                Main.printToView(cr.name+" can block!");
            }
        }
                target.takeDamage(power);
                Main.printToView("Существо "+name+" атакует героя.");
    }

    public void takeDamage(int dmg){
        if (tougness>damage+dmg){
            damage+=dmg;
        }
        else {
           die();
        }
    }

    public void die(){
        Board.removeCreatureFromPlayerBoard(this);
        Board.putCardToGraveyard(this, this.owner);
    }
}
