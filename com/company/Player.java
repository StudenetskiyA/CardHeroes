package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.ListIterator;

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

    ArrayList<Creature> diedCreatureOnBoard() {
        ArrayList<Creature> r = new ArrayList<>();
        for (Creature c : Board.creature.get(numberPlayer)) {
            if (c.isDie()) {
                //Or other method for die!
                //if (!c.effects.deathPlayed)
                    r.add(c);
            }
        }
        return r;
    }

    Creature searchWhenOtherDieAbility(Creature cr) {
        for (Creature p : Board.creature.get(numberPlayer)) {
            if (p.text.contains("При гибели другого вашего существа:") && p != cr && !p.activatedAbilityPlayed)
                return p;
        }
        return null;
    }

    void massDieCheckNeededTarget() {//if someone wants to choice target at death(self or other) - pause game
        crDied = new ArrayList<>(diedCreatureOnBoard());//died creature
        ListIterator<Creature> temp = crDied.listIterator();
        System.out.println("massDie, pl="+playerName+", found died "+crDied.size());

        while (temp.hasNext()) {
            Creature tmp = temp.next();
            //Creature ability at death
            Creature cr = searchWhenOtherDieAbility(tmp);//creature, who wants to other die(ex. Падальщик Пустоши)
            if (cr != null && crDied.size()>0) {
                System.out.println("Падальщик "+playerName);
                //CHECK EXIST TARGET
                if (MyFunction.canTargetComplex(cr)) {
                    Main.printToView(0, cr.name + " просит выбрать цель.");

                    Main.memPlayerStatus=Main.isMyTurn;
                    if (numberPlayer == 0) { Main.isMyTurn = Main.playerStatus.choiseTarget;
                    } else { Main.isMyTurn = Main.playerStatus.EnemyChoiceTarget;
                    }

                    ActivatedAbility.creature = cr;
                    ActivatedAbility.onUpkeepPlayed = false;
                    ActivatedAbility.onDeathPlayed = false;
                    ActivatedAbility.onOtherDeathPlayed = true;
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
                    Main.isMyTurn=Main.memPlayerStatus;
                }
                else {
                    Main.printToView(0, "Целей для " + cr.name + " нет.");
                    cr.activatedAbilityPlayed=true;//If you can't target, after you can't play this ability
                }
            }
            if (tmp.text.contains("Гибельт:") && !tmp.effects.deathPlayed) {
                //CHECK EXIST TARGET
                if (MyFunction.canTargetComplex(tmp)) {
                    Main.printToView(0, tmp.name + " просит выбрать цель.");
                    Main.memPlayerStatus=Main.isMyTurn;
                    if (numberPlayer == 0) { Main.isMyTurn = Main.playerStatus.choiseTarget;
                    } else { Main.isMyTurn = Main.playerStatus.EnemyChoiceTarget;
                    }

                    ActivatedAbility.creature = new Creature(tmp);
                    ActivatedAbility.onUpkeepPlayed = false;
                    ActivatedAbility.onDeathPlayed = true;
                    ActivatedAbility.onOtherDeathPlayed = false;
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
                    Main.isMyTurn=Main.memPlayerStatus;
                }
                else {
                        Main.printToView(0, "Целей для " + tmp.name + " нет.");
                        tmp.effects.deathPlayed=true;//If you can't target, after you can't play this ability
                    }
            }
        }
    }

    boolean massSummon() {//Return true if someone wants to choise target at mass summon
        boolean someFounded = false;
        if (numberPlayer == 0) {//Until not have ability mass summon with target at opponent turn!
            // System.out.println("Upkeep2");
            while (true) {
                for (int i = Board.creature.get(numberPlayer).size() - 1; i >= 0; i--) {
                    //Creature ability at begin turn
                    if (Board.creature.get(numberPlayer).get(i).text.contains("Наймт:")) {
                        //Check of correct target
                        if (!Board.creature.get(numberPlayer).get(i).effects.battlecryPlayed) {
                            //Begin choise target for ability
                            //  Main.printToView(0,"begin target creature n="+i);
                            Main.isMyTurn = Main.playerStatus.choiseTarget;
                            Card.ActivatedAbility.creature = Board.creature.get(numberPlayer).get(i);
                            Card.ActivatedAbility.creatureTap = false;
                            ActivatedAbility.onUpkeepPlayed = false;
                            ActivatedAbility.onDeathPlayed = false;
                            someFounded = true;
                            //  Main.printToView(0, "Амбрадор заставляет вернуть другое существо.");
                            break;
                        }
                    }
                }
                return someFounded;
            }
        }
        return false;
    }

    boolean upkeep() {//Return true if someone wants to choise target at begin turn
        boolean someFounded = false;
        if (numberPlayer == 0) {//Until not have ability at begin of opponent turn!
            // System.out.println("Upkeep");
            while (true) {
                for (int i = Board.creature.get(numberPlayer).size() - 1; i >= 0; i--) {
                    //Creature ability at begin turn
                    if (Board.creature.get(numberPlayer).get(i).text.contains("В начале вашего хода: ") || Board.creature.get(numberPlayer).get(i).text.contains("В начале хода: ")) {
                        //Check of correct target
                        //TODO For Ambrador ok, when you add new card with on begin turn - fix here!
                        if ((Board.creature.get(numberPlayer).size() > 1) && (!Board.creature.get(numberPlayer).get(i).effects.upkeepPlayed)) {
                            //Begin choise target for ability
                            //    Main.printToView(0,"begin target creature n="+i);
                            Main.isMyTurn = Main.playerStatus.choiseTarget;
                            Card.ActivatedAbility.creature = Board.creature.get(numberPlayer).get(i);
                            Card.ActivatedAbility.creatureTap = false;
                            ActivatedAbility.onUpkeepPlayed = true;
                            Board.creature.get(numberPlayer).get(i).effects.upkeepPlayed = true;
                            someFounded = true;
                            Main.printToView(0, "Амбрадор заставляет вернуть другое существо.");
                            break;
                        }
                    }
                }
                return someFounded;
            }
        }
        return false;
    }

    void newTurn() {
        Board.turnCount++;
        Main.printToView(0, "Ход номер " + Board.turnCount + ", игрок " + playerName);
        if (numberPlayer == 0)
            Main.printToView(1, Color.GREEN, "Ваш ход");
        else Main.printToView(1, Color.RED, "Ход противника");

        //Bogart!
        //TODO
        //

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
                // Board.newTurnQueue.push(new NewTurnQueue.QueueEvent(Board.creature.get(numberPlayer).get(i),"poison",Board.creature.get(numberPlayer).get(i)));
                Board.creature.get(numberPlayer).get(i).takeDamage(Board.creature.get(numberPlayer).get(i).effects.poison, Creature.DamageSource.poison);
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
        int effectiveCost = _card.getCost(_card, this);
        if (tempX != 0) effectiveCost += tempX;

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
