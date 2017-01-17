package com.company;

import java.util.ArrayList;

/**
 * Created by samsung on 30.12.2016.
 */
public class Creature extends Card {
    private int power;
    private int tougness;
    public boolean isTapped;
    public boolean isSummonedJust;
    public int poison;
    public Player owner;
    public int currentArmor = 0;
    public int maxArmor = 0;
    int damage;
    public boolean takedDamageThisTurn = false;
    public boolean attackThisTurn = false;
    public boolean blockThisTurn = false;

    public Effects effects = new Effects();

    public class Effects{
        public int bonusPower=0;
        public int bonusTougness=0;
        public int cantAttackOrBlock = 0;
        public int turnToDie=999;
        public void EOT(){
            cantAttackOrBlock --;
            turnToDie--;
            if (cantAttackOrBlock<0) cantAttackOrBlock=0;
            if (turnToDie==0) die();
        }
    }

    public int getPower(){return power+effects.bonusPower;}
    public int getTougness(){return hp+effects.bonusTougness;}

    public Creature(Creature _card) {
        super(_card.cost, _card.name, _card.color, _card.type, _card.targetType, _card.tapTargetType, _card.text, _card.power, _card.hp);
        power = _card.power;
        tougness = _card.hp;
        image = _card.image;
        cost = _card.cost;
        isTapped = false;
        isSummonedJust = true;
        name = _card.name;
        owner = _card.owner;

        if (text.contains("Броня ")) {
            maxArmor = getNumericAfterText(text, "Броня ");
            currentArmor = maxArmor;
        }
    }

    public Creature(Card _card, Player _owner) {
        super(_card.cost, _card.name, _card.color, _card.type, _card.targetType, _card.tapTargetType, _card.text, _card.power, _card.hp);
        power = _card.power;
        tougness = _card.hp;
        image = _card.image;
        cost = _card.cost;
        isTapped = false;
        isSummonedJust = true;
        name = _card.name;
        owner = _owner;
        if (text.contains("Броня ")) {
            maxArmor = getNumericAfterText(text, "Броня ");
            currentArmor = maxArmor;
        }
    }

    void tapCreature() {
        isTapped = true;
    }

    ArrayList<Creature> canAnyoneBlock(Creature target) {
        int pl;
        if (owner.numberPlayer == 0) pl = 1;
        else pl = 0;

        //get list of opponent creature
        ArrayList<Creature> crt = new ArrayList<>(Board.creature.get(pl));
        //delete from it tapped
        if (crt.contains(target)) crt.remove(target);
        for (int i = 0; i < crt.size(); i++) {
            //for (Creature cr:creature){
            if ((crt.get(i).blockThisTurn)||(crt.get(i).isTapped)||(crt.get(i).effects.cantAttackOrBlock>0)) crt.remove(crt.get(i));
        }
        //delete from it target
        return crt;
    }

    public void fightCreature(Creature second) {
        //TODO First strike and other
        Main.printToView(this.name + " сражается с " + second.name + ".");
        if ((second.text.contains("Первый удар.")) && (!this.text.contains("Первый удар."))) {
            this.takeDamage(second.power, DamageSource.fight, second.haveRage());
            if (this.damage < this.hp) second.takeDamage(this.power, DamageSource.fight, second.haveRage());
        } else if ((this.text.contains("Первый удар.")) && (!second.text.contains("Первый удар."))) {
            second.takeDamage(this.power, DamageSource.fight, second.haveRage());
            if (second.damage < second.hp) this.takeDamage(second.power, DamageSource.fight, second.haveRage());
        } else if ((this.text.contains("Первый удар.")) && (second.text.contains("Первый удар."))) {
            this.takeDamage(second.power, DamageSource.fight, second.haveRage());
            second.takeDamage(this.power, DamageSource.fight, second.haveRage());
        } else {
            this.takeDamage(second.power, DamageSource.fight, second.haveRage());
            second.takeDamage(this.power, DamageSource.fight, second.haveRage());
        }
    }

    public void heal(int dmg) {
        damage -= dmg;
        if (damage < 0) damage = 0;
    }

    public void fightPlayer(Player second) {
        Main.printToView(this.name + " атакует " + second.name + ".");
        second.takeDamage(this.power);
    }

    public void attackCreature(Creature target) {
        if (!text.contains("Опыт в атаке."))
            tapCreature();
        attackThisTurn = true;

        if (this.text.contains("Направленный удар.")) {
            fightCreature(target);
        } else {
            ArrayList<Creature> blocker;
            blocker = canAnyoneBlock(target);
            if (blocker.size() != 0) {
                int nc = Board.creature.get(owner.numberPlayer).indexOf(this);
                int nt = Board.creature.get(Board.opponentN(owner)).indexOf(target);
                System.out.println("$CHOISEBLOCKER(" + Board.opponent(owner).playerName + "," + nc + "," + nt + ")");
                Client.writeLine("$CHOISEBLOCKER(" + Board.opponent(owner).playerName + "," + nc + "," + nt + ")");
                Main.isMyTurn = Main.playerStatus.EnemyChoiseBlocker;
                for (Creature cr : blocker) {
                    Main.printToView(cr.name + " can block!");
                }
            } else {
                fightCreature(target);
            }
        }
    }

    public void attackPlayer(Player target) {
        if (!text.contains("Опыт в атаке."))
            tapCreature();
        attackThisTurn = true;

        if (this.text.contains("Направленный удар.")) {
            fightPlayer(target);
        } else {
            ArrayList<Creature> blocker = canAnyoneBlock(null);
            if (blocker.size() != 0) {
                int nc = Board.creature.get(owner.numberPlayer).indexOf(this);
                int nt = -1;
                System.out.println("$CHOISEBLOCKER(" + Board.opponent(owner).playerName + "," + nc + "," + nt + ")");
                Client.writeLine("$CHOISEBLOCKER(" + Board.opponent(owner).playerName + "," + nc + "," + nt + ")");
                Main.isMyTurn = Main.playerStatus.EnemyChoiseBlocker;
                for (Creature cr : blocker) {
                    Main.printToView(cr.name + " can block!");
                }
            } else {
                fightPlayer(target);
            }
        }
    }

    enum DamageSource {fight, spell, poison, ability, scoot}

    public void takeDamage(int dmg, DamageSource dmgsrc, Boolean... rage) {
        if (!this.text.contains("Не получает ран.")) {
            if ((dmgsrc == DamageSource.scoot) || (dmgsrc == DamageSource.fight)) {
                if ((takedDamageThisTurn) && (rage[0])) {
                    dmg++;
                    System.out.println("RAGE!");
                }
                int tmp = dmg;
                dmg -= currentArmor;
                currentArmor -= tmp;
                if (dmg < 0) dmg = 0;
                if (currentArmor < 0) currentArmor = 0;
            }
            damage += dmg;
            takedDamageThisTurn = true;
            if (tougness > damage) {
            } else {
                die();
            }
        }

    }

    public void tapNoTargetAbility() {
        String txt = this.text.substring(this.text.indexOf("ТАП:") + "ТАП:".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАП:")) + 1);
        System.out.println("ТАПТ: " + txt);
        tapCreature();
        Card.ability(this, owner, null, null, txt);
    }

    public void tapTargetAbility(Creature _cr, Player _pl) {
        String txt = this.text.substring(this.text.indexOf("ТАПТ:") + "ТАПТ:".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАПТ:")) + 1);
        System.out.println("ТАПТ: " + txt);
        tapCreature();
        Card.ability(this, owner, _cr, _pl, txt);
    }


    public void deathratle(Creature _cr, Player _pl) {
        String txt = this.text.substring(this.text.indexOf("Гибельт:") + "Гибельт:".length() + 1, this.text.indexOf(".", this.text.indexOf("Гибельт:")) + 1);
        System.out.println("Гибельт: " + txt);
        Card.ability(this, owner, _cr, _pl, txt);
    }

    public void cry(Creature _cr, Player _pl) {
        String txt = this.text.substring(this.text.indexOf("Наймт:") + "Наймт:".length() + 1, this.text.indexOf(".", this.text.indexOf("Наймт:")) + 1);
        System.out.println("Наймт: " + txt);
        Card.ability(this, owner, _cr, _pl, txt);
    }

    public static void deathratleNoTarget(Creature _card,Player _owner){
        String txt = _card.text.substring(_card.text.indexOf("Гибель:") + "Гибель:".length() + 1, _card.text.indexOf(".", _card.text.indexOf("Гибель:"))+1);
        Card.ability(_card,_owner,_card,null,txt);//Only here 3th parametr=1th
    }

    public void deathratleTarget(Creature _creature) {
        if (owner==Main.players[0]) {
            Main.isMyTurn = Main.playerStatus.choiseTarget;
        }
        //TODO Not my creature
        else {
            Main.isMyTurn = Main.playerStatus.EnemyChoiseBlocker;
        }
        Main.activatedAbility.creature = new Creature(_creature);
        Main.activatedAbility.targetType= _creature.targetType;
        Main.activatedAbility.tapTargetType= 0;
        Main.activatedAbility.creatureTap=false;
    }

    public void die() {
        //May be wannts to free exemplar of creature, if you do this, change 'fight' method
//        this.isTapped=false;
//        this.damage=0;
        //And may be other
        // Or not?
        Main.printToView(this.name + " умирает.");
        if (this.text.contains("Гибельт:")) {
            deathratleTarget(this);
        }
        if (this.text.contains("Гибель:")) {
            deathratleNoTarget(this,owner);
        }
        Board.removeCreatureFromPlayerBoard(this);
        Board.putCardToGraveyard(this, this.owner);
    }

    public void returnToHand() {
        Board.removeCreatureFromPlayerBoard(this);
        owner.cardInHand.add(this);
        // Board.putCardToGraveyard(this, this.owner);
    }
}
