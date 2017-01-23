package com.company;

import java.awt.*;
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
    Equpiment equpiment[];//0-armor,1-amulet,2-weapon,3-event

    Boolean bbshield = false;//Bjornbon shield
    private static int tempX;//For card with X, for correct minus cost

    Player(Card _card, Deck _deck, String _playerName, int _n) {
        super(0, _card.name, "", 1, 0, _card.targetType, 0, _card.text, 0, _card.hp);
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

    void endTurn() {
        totalCoin -= temporaryCoin;

        if (Board.opponent(this).bbshield) Board.opponent(this).bbshield = false;

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

    void upkeep(){
        boolean someFounded=false;
        if (numberPlayer==0) {//Until not have ability at begin of opponent turn!
            System.out.println("Upkeep");
            for (int i = Board.creature.get(numberPlayer).size() - 1; i >= 0; i--) {
                //Creature ability at begin turn
                if (Board.creature.get(numberPlayer).get(i).text.contains("В начале вашего хода: ") || Board.creature.get(numberPlayer).get(i).text.contains("В начале хода: ")) {
                    //Check of correct target
                    //TODO For Ambrador ok, when you add new card with on begin turn - fix here!
                    if ((Board.creature.get(numberPlayer).size() > 1) && (!Board.creature.get(numberPlayer).get(i).effects.upkeepPlayed)) {
                        //Begin choise target for ability
                        Main.isMyTurn = Main.playerStatus.choiseTarget;
                        Card.ActivatedAbility.creature = Board.creature.get(numberPlayer).get(i);
                        Card.ActivatedAbility.targetType = Board.creature.get(numberPlayer).get(i).targetType;
                        Card.ActivatedAbility.creatureTap = false;
                        ActivatedAbility.onUpkeepPlayed = true;
                        Board.creature.get(numberPlayer).get(i).effects.upkeepPlayed = true;
                        someFounded = true;
                        Main.printToView(0, "Амбрадор заставляет вернуть другое существо.");
                        break;
                    }
                }
            }

            if (!someFounded) {
                //queue realize
                System.out.println("Queue size= " + Board.newTurnQueue.size());

                while (Board.newTurnQueue.size() > 0) {
                    if (Main.iUndestand) {
                        NewTurnQueue.QueueEvent s = Board.newTurnQueue.pull();

                        if (s.whatToDo.contains("poison")) {
                          //  int dmg = MyFunction.getNumericAfterText(s.whatToDo, "poison");
                            if (Board.creature.get(numberPlayer).contains(s.whoCalled)) {
                                int nc = Board.creature.get(numberPlayer).indexOf(s.whoCalled);
                                int dmg=s.whoCalled.effects.poison;
                                //s.whoCalled.takeDamage(s.whoCalled.effects.poison, Creature.DamageSource.poison, false);
                                System.out.println("$TAKEPOISONDAMAGE(" + playerName + "," + nc + ","+dmg+")");
                                Client.writeLine("$TAKEPOISONDAMAGE(" + playerName + "," + nc + ","+dmg+")");
                            }
                        }
                        if (s.whatToDo.contains("target")) {
                            if (Board.creature.get(numberPlayer).contains(s.targetCr)) {
                                //it may be die! nc=-1
                                int nc = Board.creature.get(numberPlayer).indexOf(s.whoCalled);
                                int tr= Board.creature.get(numberPlayer).indexOf(s.targetCr);
                                System.out.println("$CRYTARGET(" + playerName + "," + nc + ",0," + tr + ")");
                                Client.writeLine("$CRYTARGET(" + playerName + "," + nc + ",0," + tr + ")");
                                //here must be wait for client write and undestand command.
                                Main.iUndestand = false;
                            }
                        }
                    }
                }
                Main.isMyTurn = Main.playerStatus.MyTurn;
            }
        }
    }

    void newTurn() {
        Board.turnCount++;
        Main.printToView(0, "Ход номер " + Board.turnCount + ", игрок " + playerName);
        if (numberPlayer == 0)
            Main.printToView(1, Color.GREEN, "Ваш ход");
        else Main.printToView(1, Color.RED, "Ход противника");

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
            //armor
            Board.creature.get(numberPlayer).get(i).currentArmor = Board.creature.get(numberPlayer).get(i).maxArmor;
            //for rage
            Board.creature.get(numberPlayer).get(i).takedDamageThisTurn = false;
            Board.creature.get(numberPlayer).get(i).attackThisTurn = false;
            Board.creature.get(numberPlayer).get(i).blockThisTurn = false;
            //poison, here creature may die, check it for after.
            if ((Board.creature.get(numberPlayer).get(i).effects.poison != 0) && (!Board.creature.get(numberPlayer).get(i).text.contains("Защита от отравления.")))
                Board.newTurnQueue.push(new NewTurnQueue.QueueEvent(Board.creature.get(numberPlayer).get(i),"poison",Board.creature.get(numberPlayer).get(i)));
               // Board.creature.get(numberPlayer).get(i).takeDamage(Board.creature.get(numberPlayer).get(i).effects.poison, Creature.DamageSource.poison);
        }

        //Upkeep
        upkeep();
        //Draw
        if (Board.turnCount != 1) drawCard();//First player not draw card in first turn. It's rule.
    }

    void playCardX(Card _card, Creature _targetCreature, Player _targetPlayer, int x) {
        int num = cardInHand.indexOf(_card);
        if (num == -1) return;
        Main.printToView(0, "X = " + x + ".");
        _card.text = _card.text.replace("ХХХ", String.valueOf(x));
        System.out.println("text after replace:" + _card.text);
        tempX = x;
        playCard(_card, _targetCreature, _targetPlayer);
        tempX = 0;
    }

    void playCard(Card _card, Creature _targetCreature, Player _targetPlayer) {
        int num = cardInHand.indexOf(_card);
        if (num == -1) return;
        int effectiveCost = _card.cost;
        if (tempX != 0) effectiveCost += tempX;
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
            Main.printToView(0, "Розыгрышь карты " + _card.name + ".");
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
                Main.printToView(0, name + " экипировал " + _card.name + ".");
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
            } else if (_card.type == 4) {
                Main.printToView(0, name + " экипировал " + _card.name + ".");
                if (this.equpiment[3] != null) Board.putCardToGraveyard(this.equpiment[3], this);
                this.equpiment[3] = new Equpiment(_card, this);
            }
            //remove from hand
            cardInHand.remove(_card);
        } else {
            Main.printToView(0, "Не хватает монет.");
        }
    }

    void drawCard() {
        if (deck.haveTopDeck())
            cardInHand.add(0, deck.drawTopDeck());
        else {
            Main.printToView(0, "Deck of " + playerName + " is empty.");
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
                if (dmg != 1)
                    Main.printToView(0, "Браслет подчинения свел атаку к 1.");
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
                Main.printToView(0, "Плащ Исхара предотвратил " + (tmp - dmg) + " урона.");
                if (equpiment[0].hp <= 0) {
                    Board.putCardToGraveyard(equpiment[0], this);
                    equpiment[0] = null;
                }
            }
        }
        if (hp > damage + dmg) {
            damage += dmg;
            if (dmg != 0)
                Main.printToView(0, this.name + " получет " + dmg + " урона.");
        } else {
            System.out.println("Player lose game.");
            Main.printToView(0, playerName + " проиграл игру.");
            //TODO Lose play
        }
    }

    void heal(int dmg) {
        if (equpiment[1].name.equals("Браслет подчинения")) {
            Main.printToView(0, name + " не может быть излечен.");
        } else {
            damage -= dmg;
            if (damage < 0) damage = 0;
        }
    }

    void abilityNoTarget() {
        String txt = this.text.substring(this.text.indexOf("ТАП:") + "ТАП:0".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАП:")) + 1);
        System.out.println("ТАП HERO: " + txt);
        isTapped = true;
        Card.ability(this, this, null, null, txt);
    }

    void ability(Creature _cr, Player _pl) {
        String txt = this.text.substring(this.text.indexOf("ТАПТ: ") + "ТАПТ: ".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАПТ: ") + 1));
        System.out.println("ТАПТ HERO: " + txt);
        isTapped = true;
        Card.ability(this, this, _cr, _pl, txt);
    }

}
