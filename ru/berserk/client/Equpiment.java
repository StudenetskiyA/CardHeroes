package ru.berserk.client;

// Created by StudenetskiyA on 18.01.2017.

public class Equpiment extends Card {
    public boolean isTapped;
    public Player owner;

    public Equpiment(Card _card, Player _owner) {
        super(_card.cost, _card.name, _card.creatureType, _card.color, _card.type, _card.targetType, _card.tapTargetType, _card.text, _card.power, _card.hp);
        isTapped = false;
        owner = _owner;
    }

    void tap(int dmg) {
        //Animation
        boolean t=(dmg==1)? true:false;
        isTapped=t;
    }
}
