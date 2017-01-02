package com.company;

/**
 * Created by samsung on 30.12.2016.
 */
public class Creature extends Card {
    public int power;
    public int tougness;
    int damage;

    public boolean isTapped;
    public boolean isSummonedJust;

    public Creature(Card _card, Board _board){
        super(_board);
    power = _card.power;
    tougness = _card.hp;
    image = _card.image;
    cost = _card.cost;
    isTapped=false;
    isSummonedJust=true;
    name= _card.name;
    }

    void tapCreature(){
        isTapped=true;
    }

    public void attackCreature(Creature target){
        if (!isSummonedJust) {
            if (!isTapped){
                tapCreature();
                //TODO firststrike
                target.takeDamage(power);
                takeDamage(target.power);
            }
            else {
                System.out.println("Tapped creature can't attack.");
            }
        }
        else {
            System.out.println("This creature just enter board.");
        }
    }

    public void attackPlayer(Player target){
        if (!isSummonedJust) {
            if (!isTapped){
                tapCreature();
                target.takeDamage(power);
            }
            else {
                System.out.println("Tapped creature can't attack.");
            }
        }
        else {
            System.out.println("This creature just enter board.");
        }
    }

    public void takeDamage(int dmg){
        if (tougness>damage+dmg){
            damage+=dmg;
        }
        else {
            board.removeCreatureFromPlayerBoard(this);
        }
    }
}
