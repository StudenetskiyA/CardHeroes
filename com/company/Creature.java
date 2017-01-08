package com.company;

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

    public void attackCreature(Creature target){
                tapCreature();
                //TODO firststrike
                //TODO Block Check
                target.takeDamage(power);
                takeDamage(target.power);
                Main.printToView(name+" атакует: "+target.name+".");
    }

    public void attackPlayer(Player target){
                tapCreature();
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
