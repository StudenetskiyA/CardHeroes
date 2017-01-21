package com.company;

import java.util.ArrayList;

/**
 * Created by StudenetskiyA on 30.12.2016.
 */
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
    Equpiment equpiment[];//0-armor,1-amulet,2-weapon
    Boolean bbshield=false;//Bjornbon shield
    private static int tempX;//For card with X, for correct minus cost

    Player(Card _card, Deck _deck, String _playerName, int _n) {
        super(0, _card.name, "", 1, 0, _card.targetType, 0, _card.text, 0, _card.hp);
        deck = _deck;
        playerName = _playerName;
        cardInHand = new ArrayList<>();
        graveyard = new ArrayList<>();
        numberPlayer = _n;
        equpiment = new Equpiment[3];
        equpiment[0] = null;
        equpiment[1] = null;
        equpiment[2] = null;
    }

    Player(Deck _deck, String _heroName, String _playerName, int _n, int _hp) {
        super(0, _heroName, "", 1, 0, 0, 0, "", 0, _hp);
        deck = _deck;
        playerName = _playerName;
        cardInHand = new ArrayList<>();
        graveyard = new ArrayList<>();
        numberPlayer = _n;
        equpiment = new Equpiment[3];
        equpiment[0] = null;
        equpiment[1] = null;
        equpiment[2] = null;
    }

    void endTurn() {
        totalCoin -= temporaryCoin;
        bbshield=false;
        if (untappedCoin > totalCoin) untappedCoin = totalCoin;
        temporaryCoin = 0;
        //Creature effects until eot
        if (!Board.creature.get(0).isEmpty()) {
            for (int i = Board.creature.get(0).size() - 1; i >= 0; i--)
                Board.creature.get(0).get(i).effects.EOT();
        }
        if (!Board.creature.get(1).isEmpty()) {
            for (int i = Board.creature.get(1).size() - 1; i >= 0; i--)
                Board.creature.get(1).get(i).effects.EOT();
        }
      //  this.myTurn = false;
        Board.opponent(this).newTurn();
    }

    void newTurn() {
        Board.turnCount++;
      //  this.myTurn = true;
        Main.printToView(1,"Ход номер " + Board.turnCount + ", игрок " + playerName);
        isTapped = false;
        //Get coin
        if (totalCoin < 10) totalCoin++;
        //Untap
        untappedCoin = totalCoin;

        if (equpiment[0] != null) equpiment[0].isTapped = false;
        if (equpiment[1] != null) equpiment[1].isTapped = false;
        if (equpiment[2] != null) equpiment[2].isTapped = false;

        for (int i = Board.creature.get(numberPlayer).size() - 1; i >= 0; i--) {
            //untap
            Board.creature.get(numberPlayer).get(i).isSummonedJust = false;
            Board.creature.get(numberPlayer).get(i).isTapped = false;
            //poison
            if ((Board.creature.get(numberPlayer).get(i).effects.poison != 0) && (!Board.creature.get(numberPlayer).get(i).text.contains("Защита от отравления.")))
                Board.creature.get(numberPlayer).get(i).takeDamage(Board.creature.get(numberPlayer).get(i).effects.poison, Creature.DamageSource.poison);
            //armor
            Board.creature.get(numberPlayer).get(i).currentArmor = Board.creature.get(numberPlayer).get(i).maxArmor;
            //for rage
            Board.creature.get(numberPlayer).get(i).takedDamageThisTurn = false;
            Board.creature.get(numberPlayer).get(i).attackThisTurn = false;
            Board.creature.get(numberPlayer).get(i).blockThisTurn = false;
        }
        //Draw
        if (Board.turnCount != 1) drawCard();//First player not draw card in first turn. It's rule.
    }

    void playCardX(Card _card, Creature _targetCreature, Player _targetPlayer, int x) {
        int num = cardInHand.indexOf(_card);
        if (num == -1) return;
        Main.printToView(0,"X = " + x + ".");
        _card.text = _card.text.replace("ХХХ", String.valueOf(x));
        System.out.println("text after replace:" + _card.text);
        tempX=x;
        playCard(_card, _targetCreature, _targetPlayer);
        tempX=0;
    }

    void playCard(Card _card, Creature _targetCreature, Player _targetPlayer) {
        int num = cardInHand.indexOf(_card);
        if (num == -1) return;
        int effectiveCost = _card.cost;
        if (tempX!=0) effectiveCost+=tempX;
            //Gnome cost less
        if (_card.creatureType.equals("Гном")) {
            int runopevecFounded = 0;
            for (int i = 0; i < Board.creature.get(numberPlayer).size(); i++) {
                if (Board.creature.get(numberPlayer).get(i).name.equals("Рунопевец")) runopevecFounded++;
            }
            effectiveCost -= runopevecFounded;
        }

        if (untappedCoin >= effectiveCost) {
            untappedCoin -= effectiveCost;
            Main.printToView(0,"Розыгрышь карты " + _card.name + ".");
            //put on table or cast spell
            if (_card.type == 1) {
                //release text on spell
                //check target
                if (_targetPlayer != null) {
                    _card.playOnPlayer(this, _targetPlayer);
                }
                if (_targetCreature != null) {
                    _card.playOnCreature(this, _targetCreature);
                }
                //No target
                if ((_targetCreature == null) && (_targetPlayer == null))
                    ability(_card, this, null, null, _card.text);
                //and after play
                Board.putCardToGraveyard(_card, this);
            } else if (_card.type == 2) {
                //creature
                Board.addCreatureToBoard(_card, this);
            } else if (_card.type == 3) {
                Main.printToView(0,name + " экипировал " + _card.name + ".");
                if (_card.creatureType.equals("Броня")) {
                    if (this.equpiment[0] != null) Board.putCardToGraveyard(this.equpiment[0], this);
                    this.equpiment[0] = new Equpiment(_card, this);
                } else if (_card.creatureType.equals("Амулет")) {
                    if (this.equpiment[1] != null) Board.putCardToGraveyard(this.equpiment[1], this);
                    this.equpiment[1] = new Equpiment(_card, this);
                } else if (_card.creatureType.equals("Оружие")) {
                    if (this.equpiment[2] != null) Board.putCardToGraveyard(this.equpiment[2], this);
                    this.equpiment[2] = new Equpiment(_card, this);
                }
            }
            //remove from hand
            cardInHand.remove(_card);
        } else {
            Main.printToView(0,"Не хватает монет.");
        }
    }

    void drawCard() {
        if (deck.haveTopDeck())
            cardInHand.add(deck.drawTopDeck());
        else {
            Main.printToView(0,"Deck of " + playerName + " is empty.");
        }
    }

    void drawSpecialCard(Card c) {
        cardInHand.add(c);
        deck.cards.remove(c);
        deck.suffleDeck(Main.sufflingConst);
    }

    void takeDamage(int dmg) {
        //equpiment[1]
        if (equpiment[1] != null) {
            if (equpiment[1].name.equals("Браслет подчинения")) {
                //Плащ исхара
                if (dmg!=1)
                Main.printToView(0,"Браслет подчинения свел атаку к 1.");
                dmg = 1;

            }
        }
        //equpiment[0]
        if (equpiment[0] != null) {
            if (equpiment[0].name.equals("Плащ Исхара")) {
                //Плащ исхара
                int tmp = dmg;
                dmg -= equpiment[0].hp;
                equpiment[0].hp -= tmp;
                if (dmg < 0) dmg = 0;
                Main.printToView(0,"Плащ Исхара предотвратил " + (tmp - dmg) + " урона.");
                if (equpiment[0].hp <= 0) {
                    Board.putCardToGraveyard(equpiment[0], this);
                    equpiment[0] = null;
                }
            }
        }
        if (hp > damage + dmg) {
            damage += dmg;
            if (dmg!=0)
            Main.printToView(0,this.name+" получет "+dmg +" урона.");
        } else {
            System.out.println("Player lose game.");
            Main.printToView(0,playerName + " проиграл игру.");
            //TODO Lose play
        }
    }

    void heal(int dmg) {
        if (equpiment[1].name.equals("Браслет подчинения")) {
            Main.printToView(0,name + " не может быть излечен.");
        } else {
            damage -= dmg;
            if (damage < 0) damage = 0;
        }
    }

    void abilityNoTarget() {
        String txt = this.text.substring(this.text.indexOf("ТАП:") + "ТАП:0".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАП:"))+1);
        System.out.println("ТАП HERO: " + txt);
        isTapped = true;
        Card.ability(this, this, null, null, txt);
    }

    void ability(Creature _cr, Player _pl) {
        String txt = this.text.substring(this.text.indexOf("ТАПТ:") + "ТАПТ:".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАПТ:") + 1));
        System.out.println("ТАПТ HERO: " + txt);
        isTapped = true;
        Card.ability(this, this, _cr, _pl, txt);
    }

}
