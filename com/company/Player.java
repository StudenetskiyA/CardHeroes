package com.company;

import java.util.ArrayList;

//Created by StudenetskiyA on 30.12.2016.

public class Player extends Card {

    int numberPlayer;
    int damage;
    String playerName;
    int totalCoin;
    int untappedCoin;
    int temporaryCoin = 0;
    boolean isTapped = false;
    public Deck deck;
    ArrayList<Card> cardInHand;
    ArrayList<Card> graveyard;
    Equpiment equpiment[];//0-armor,1-amulet,2-weapon,3-event

    Effects effects = new Effects(this);

    public class Effects {
        Player whis;
        String additionalText = "";
        boolean bbShield = false;

        Effects(Player _pl) {
            whis = _pl;
        }

        boolean getBBShield() {
            return bbShield;
        }

        //#TakeCreatureEffect(Player, CreatureNumOnBoard,Effect,EffectCount)
        public void takeEffect(MyFunction.EffectPlayer ef, int n) {
            switch (ef) {
                case bbShield: {
                    if (n==1) {
                        bbShield = true;
                        Main.printToView(0, whis.name + " получает щит Бьернбона.");
                    }
                    else bbShield=false;

                    break;
                }
            }
        }
    }


    Player(Card _card, Deck _deck, String _playerName, int _n) {
        super(0, _card.name, _card.creatureType, 1, 0, _card.targetType, _card.tapTargetType, _card.text, 0, _card.hp);
        deck = _deck;
        isTapped = false;
        playerName = _playerName;
        cardInHand = new ArrayList<>();
        graveyard = new ArrayList<>();
        numberPlayer = _n;
        equpiment = new Equpiment[4];
        equpiment[0] = null;
        equpiment[1] = null;
        equpiment[2] = null;
        equpiment[3] = null;
    }

    Player(Deck _deck, String _heroName, String _playerName, int _n, int _hp) {
        super(0, _heroName, "", 1, 0, 0, 0, "", 0, _hp);
        deck = _deck;
        isTapped = false;
        playerName = _playerName;
        cardInHand = new ArrayList<>();
        graveyard = new ArrayList<>();
        numberPlayer = _n;
        equpiment = new Equpiment[4];
        equpiment[0] = null;
        equpiment[1] = null;
        equpiment[2] = null;
        equpiment[3] = null;
    }

    void takeDamage(int dmg) {
        this.damage += dmg;
        if (this.damage < 0) this.damage = 0;
    }

    void tap(int dmg) {
        //Animation
        boolean t = (dmg == 1) ? true : false;
        this.isTapped = t;
    }

    void untapAll() {
        Board.turnCount++;

        isTapped = false;

        if (equpiment[0] != null) equpiment[0].isTapped = false;
        if (equpiment[1] != null) equpiment[1].isTapped = false;
        if (equpiment[2] != null) equpiment[2].isTapped = false;

        for (int i = Board.creature.get(numberPlayer).size() - 1; i >= 0; i--) {
            //untap
            Board.creature.get(numberPlayer).get(i).isSummonedJust = false;
            Board.creature.get(numberPlayer).get(i).isTapped = false;
            //armor
            Board.creature.get(numberPlayer).get(i).currentArmor = Board.creature.get(numberPlayer).get(i).maxArmor;
            //for rage
            Board.creature.get(numberPlayer).get(i).takedDamageThisTurn = false;
            Board.creature.get(numberPlayer).get(i).attackThisTurn = false;
            Board.creature.get(numberPlayer).get(i).blockThisTurn = false;

        }
    }

}
