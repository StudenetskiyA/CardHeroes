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


    public Creature(Card _card, Board _board, Player _owner){
        super(_board,_card.cost,_card.name,_card.color,_card.type,_card.text,_card.power,_card.hp);
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
                tapCreature();
                target.takeDamage(power);
                Main.printToView("Существо "+name+" атакует героя.");
                int num = board.creature.get(owner.numberPlayer).indexOf(this);
        System.out.println("$ATTACKPLAYER(" +owner.playerName+","+ num +")");
        Client.sendToServer("$ATTACKPLAYER(" +owner.playerName+","+ num +")");
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
        board.removeCreatureFromPlayerBoard(this);
        owner.graveyard.add(this);
    }
}
