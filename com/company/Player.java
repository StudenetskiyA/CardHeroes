package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import static com.company.Card.ActivatedAbility.WhatAbility.*;
import static com.company.Main.monitor;
import static com.company.Main.ready;

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
        Board.opponent(this).newTurn();
    }

    public ArrayList<Creature> crDied;
    public ArrayList<Creature> crCryed;
    public ArrayList<Creature> crUpkeeped;

    ArrayList<Creature> diedCreatureOnBoard() {
        ArrayList<Creature> r = new ArrayList<>();
        for (Creature c : Board.creature.get(numberPlayer)) {
            if (c.isDie()) {
                r.add(c);
            }
        }
        return r;
    }

    Creature searchWhenOtherDieAbility(Creature cr) {
        for (Creature p : Board.creature.get(numberPlayer)) {
            if (p.text.contains("При гибели другого вашего существа:") && p != cr && p.getTougness() > p.damage)
                return p;
            if (p.text.contains("При гибели в ваш ход другого вашего существа:") && p.owner.playerName.equals(Board.whichTurn) && p != cr && p.getTougness() > p.damage)
                return p;
        }
        return null;
    }

    void massDieCheckNeededTarget() {//if someone wants to choice target at death(self or other) - pause game
        crDied = new ArrayList<>(diedCreatureOnBoard());//died creature
        ListIterator<Creature> temp = crDied.listIterator();
        System.out.println("massDie, pl=" + playerName + ", found died " + crDied.size());

        while (temp.hasNext()) {
            Creature tmp = temp.next();
            //Creature ability at death
            Creature cr = searchWhenOtherDieAbility(tmp);//creature, who wants to other die(ex. Падальщик Пустоши)
            if (cr != null && crDied.size() > 0 && !cr.activatedAbilityPlayed) {
                System.out.println("Падальщик " + playerName);
                //CHECK EXIST TARGET
                if (MyFunction.canTargetComplex(cr)) {
                    Main.printToView(0, cr.name + " просит выбрать цель.");

                    if (numberPlayer == 0) {
                        Main.isMyTurn = Main.playerStatus.choiceTarget;
                    } else {
                        Main.isMyTurn = Main.playerStatus.EnemyChoiceTarget;
                    }

                    ActivatedAbility.creature = cr;
                    ActivatedAbility.whatAbility = onOtherDeathPlayed;
                    //pause until player choice target.
                    System.out.println("pause");
                    synchronized (Main.cretureDiedMonitor) {
                        try {
                            Main.cretureDiedMonitor.wait();
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                    System.out.println("resume");
                    ActivatedAbility.creature.activatedAbilityPlayed = true;//if you remove it, may play any times at turn.
                } else {
                    Main.printToView(0, "Целей для " + cr.name + " нет.");
                    cr.activatedAbilityPlayed = true;//If you can't target, after you can't play this ability
                }
            }
            if (tmp.text.contains("Гибель:")) {
                tmp.deathratleNoTarget(tmp, tmp.owner);
                tmp.effects.deathPlayed = true;
            }
            if (tmp.text.contains("Гибельт:") && !tmp.effects.deathPlayed) {
                //CHECK EXIST TARGET
                if (MyFunction.canTargetComplex(tmp)) {
                    Main.printToView(0, tmp.name + " просит выбрать цель.");

                    if (numberPlayer == 0) {
                        Main.isMyTurn = Main.playerStatus.choiceTarget;
                    } else {
                        Main.isMyTurn = Main.playerStatus.EnemyChoiceTarget;
                    }

                    ActivatedAbility.creature = new Creature(tmp);
                    ActivatedAbility.whatAbility = onDeathPlayed;
                    //pause until player choice target.
                    System.out.println("pause");
                    synchronized (Main.cretureDiedMonitor) {
                        try {
                            Main.cretureDiedMonitor.wait();
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                    System.out.println("resume");
                    tmp.effects.deathPlayed = true;
                } else if (tmp.targetType == 99) {
                    //Check n card
                    int n = cardInHand.size();
                    if (n > 1) {
                        Main.printToView(0, tmp.name + " просит сбросить карту.");//And other later, today only one
                        if (numberPlayer == 0) {
                            Main.isMyTurn = Main.playerStatus.choiceTarget;
                        } else {
                            Main.isMyTurn = Main.playerStatus.EnemyChoiceTarget;
                        }

                        ActivatedAbility.creature = new Creature(tmp);
                        ActivatedAbility.whatAbility = ActivatedAbility.WhatAbility.toHandAbility;
                        //pause until player choice target.
                        System.out.println("pause");
                        synchronized (Main.cretureDiedMonitor) {
                            try {
                                Main.cretureDiedMonitor.wait();
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                        }
                        System.out.println("resume");
                    }
                    if (n == 1) {
                        Main.printToView(0, tmp.name + " заставляет сбросить " + cardInHand.get(0));
                        Board.putCardToGraveyard(cardInHand.get(0), this);
                        cardInHand.remove(cardInHand.get(0));
                    }
                    if (n == 0) {
                        Main.printToView(0, tmp.name + " заставляет сбросить карту, но ее нет.");
                    }
                } else {
                    Main.printToView(0, "Целей для " + tmp.name + " нет.");
                    tmp.effects.deathPlayed = true;//If you can't target, after you can't play this ability
                }
            }
        }
    }

    void massUpkeepCheckNeededTarget() {//if someone wants to choice target at death(self or other) - pause game
        crUpkeeped = new ArrayList<>(Board.creature.get(numberPlayer));//died creature
        ListIterator<Creature> temp = crUpkeeped.listIterator();
        while (temp.hasNext()) {
            Creature tmp = temp.next();
            //Creature ability at upkeep
            if (tmp.text.contains("В начале хода") || tmp.text.contains("В начале вашего хода") && tmp.getTougness() > tmp.damage)
                if (Board.creature.get(numberPlayer).size() > 1 && !tmp.effects.upkeepPlayed) {
                    System.out.println("Амбрадоринг " + playerName);
                    //CHECK EXIST TARGET
                    Main.printToView(0, tmp.name + " просит выбрать цель.");

                    if (numberPlayer == 0) {
                        Main.isMyTurn = Main.playerStatus.choiceTarget;
                    } else {
                        Main.isMyTurn = Main.playerStatus.EnemyChoiceTarget;
                    }

                    ActivatedAbility.creature = tmp;
                    ActivatedAbility.whatAbility = onUpkeepPlayed;
                    //pause until player choice target.
                    System.out.println("pause");
                    synchronized (Main.cretureDiedMonitor) {
                        try {
                            Main.cretureDiedMonitor.wait();
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                    System.out.println("resume");
                    tmp.effects.upkeepPlayed = true;
                    break;//Upkeep played creature by creature.
                }
        }
    }

    void massSummonCheckNeededTarget() {//if someone wants to choice target at death(self or other) - pause game
        crCryed = new ArrayList<>(Board.creature.get(numberPlayer));//died creature
        ListIterator<Creature> temp = crCryed.listIterator();
        while (temp.hasNext()) {
            Creature tmp = temp.next();
            //Creature ability at enter to board
            if (tmp.text.contains("Найм:") && !tmp.effects.battlecryPlayed && tmp.getTougness() > tmp.damage) {
                tmp.battlecryNoTarget();
                tmp.effects.battlecryPlayed = true;
            }
            if (tmp.text.contains("Наймт:") && !tmp.effects.battlecryPlayed && tmp.getTougness() > tmp.damage)
                   //CHECK EXIST TARGET
                    if (MyFunction.canTargetComplex(tmp)) {
                        if (numberPlayer == 0) {
                            Main.isMyTurn = Main.playerStatus.choiceTarget;
                        } else {
                            Main.isMyTurn = Main.playerStatus.EnemyChoiceTarget;
                        }
                        Main.printToView(0, tmp.name + " просит выбрать цель.");

                        ActivatedAbility.creature = tmp;
                        ActivatedAbility.whatAbility = nothing;
                        //pause until player choice target.
                        System.out.println("pause");
                        synchronized (Main.cretureDiedMonitor) {
                            try {
                                Main.cretureDiedMonitor.wait();
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                        }
                        System.out.println("resume");
                        tmp.effects.battlecryPlayed = true;
                        break;//Cry played creature by creature.
                    } else {
                        Main.printToView(0, "Целей для " + tmp.name + " нет.");
                        tmp.effects.battlecryPlayed = true;//If you can't target, after you can't play this ability
                    }
        }
    }

    void newTurn() {
        Board.whichTurn=playerName;
        Board.turnCount++;
        Main.printToView(0, "Ход номер " + Board.turnCount + ", игрок " + playerName);
        if (numberPlayer == 0)
            Main.printToView(1, Color.GREEN, "Ваш ход");
        else Main.printToView(1, Color.RED, "Ход противника");

        //Tull-Bagar
        //TODO FIX null
        if (this.equpiment[3] != null && this.equpiment[3].name.equals("Пустошь Тул-Багара")) {
            Main.printToView(0, "Пустошь Тул-Багара ранит всех героев.");
            this.takeDamage(1);
            Board.opponent(this).takeDamage(1);
        }
        if (Board.opponent(this).equpiment[3] != null && Board.opponent(this).equpiment[3].name.equals("Пустошь Тул-Багара")) {
            Main.printToView(0, "Пустошь Тул-Багара ранит всех героев.");
            this.takeDamage(1);
            Board.opponent(this).takeDamage(1);
        }

        //Search for Ambrador
        if (Board.creature.get(numberPlayer).size() > 1) {
            for (Creature p : Board.creature.get(numberPlayer)) {
                if (p.text.contains("В начале хода") || p.text.contains("В начале вашего хода") && p.getTougness() > p.damage)
                    Main.gameQueue.push(new GameQueue.QueueEvent("Upkeep", p, 0));
            }
        }
        //Bogart and other
        Main.gameQueue.responseAllQueue();

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
                Board.creature.get(numberPlayer).get(i).takeDamage(Board.creature.get(numberPlayer).get(i).effects.poison, Creature.DamageSource.poison);
            Main.gameQueue.responseAllQueue();//poison queue
        }

        //Draw
        if (Board.turnCount != 1) drawCard();//First player not draw card in first turn. It's rule.
    }

    void playCardX(int num, Card _card, Creature _targetCreature, Player _targetPlayer, int x) {
        Main.printToView(0, "X = " + x + ".");
        _card.text = _card.text.replace("ХХХ", String.valueOf(x));
        System.out.println("text after replace:" + _card.text);
        tempX = x;
        playCard(num,_card, _targetCreature, _targetPlayer);
        tempX = 0;
    }

    void playCard(int num, Card _card, Creature _targetCreature, Player _targetPlayer) {
        int effectiveCost = _card.getCost(_card, this);
        if (tempX != 0) effectiveCost += tempX;

        if (untappedCoin >= effectiveCost) {
            untappedCoin -= effectiveCost;
            Main.printToView(0, "Розыгрышь карты " + _card.name + ".");
            //remove from hand
            cardInHand.remove(num);
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

        } else {
            Main.printToView(0, "Не хватает монет.");
        }

        Main.gameQueue.responseAllQueue();
    }

    void drawCard() {
        if (deck.haveTopDeck())
            cardInHand.add(0, deck.drawTopDeck());
        else {
            Main.printToView(0, "Deck of " + playerName + " is empty.");
        }
    }

    void drawSpecialCard(Card c) {
        cardInHand.add(0, c);
        deck.cards.remove(c);
        deck.suffleDeck(Main.sufflingConst);
    }

    synchronized void drawSpecialCardSL(Card c){
        cardInHand.add(0, c);
    }

    void digSpecialCard(Card c) {
        cardInHand.add(0, c);
        graveyard.remove(c);
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
            if (this.numberPlayer==0){
                System.out.println("You lose game.");
                Main.printToView(2, Color.RED, "Вы проиграли игру.");
                Main.isMyTurn= Main.playerStatus.endGame;
            } else {
                System.out.println("You win game.");
                Main.printToView(2, Color.GREEN, "Вы выиграли игру.");
                Main.isMyTurn= Main.playerStatus.endGame;
            }

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

    public Card searchInGraveyard(String name) {
        for (int i = 0; i <= graveyard.size(); i++) {
            if (graveyard.get(i).name.equals(name)) return graveyard.get(i);
        }
        return null;
    }
}
