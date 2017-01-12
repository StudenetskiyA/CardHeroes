package com.company;

import java.util.ArrayList;

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

    public Creature(Creature _card){
        super(_card.cost,_card.name,_card.color,_card.type,_card.targetType,_card.text,_card.power,_card.hp);
        power = _card.power;
        tougness = _card.hp;
        image = _card.image;
        cost = _card.cost;
        isTapped=false;
        isSummonedJust=true;
        name= _card.name;
        owner=_card.owner;
    }

    public Creature(Card _card, Player _owner){
        super(_card.cost,_card.name,_card.color,_card.type,_card.targetType,_card.text,_card.power,_card.hp);
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

    ArrayList<Creature> canAnyoneBlock(Creature target){
        int pl;
        if (owner.numberPlayer==0) pl=1;
        else pl=0;

        //get list of opponent creature
        ArrayList<Creature> crt = new ArrayList<>(Board.creature.get(pl));
        //delete from it tapped
        for (int i=0;i<crt.size();i++){
        //for (Creature cr:creature){
            if (crt.get(i).isTapped) crt.remove(crt.get(i));
        }
        //delete from it target
        if (crt.contains(target)) crt.remove(target);
    return crt;
    }

    public void fightCreature(Creature second){
        //TODO First strike and other
        Main.printToView(this.name+" сражается с "+second.name+".");
        if ((second.text.contains("Первый удар."))&&(!this.text.contains("Первый удар."))){
            this.takeDamage(second.power);
            if (this.damage<this.hp) second.takeDamage(this.power);
        }
        else if  ((this.text.contains("Первый удар."))&&(!second.text.contains("Первый удар."))){
            second.takeDamage(this.power);
            if (second.damage<second.hp) this.takeDamage(second.power);
        }
        else if  ((this.text.contains("Первый удар."))&&(second.text.contains("Первый удар."))) {
            this.takeDamage(second.power);
            second.takeDamage(this.power);
        }
        else {
            this.takeDamage(second.power);
            second.takeDamage(this.power);
        }
    }
    public void fightPlayer(Player second){
        Main.printToView(this.name+" атакует "+second.name+".");
        second.takeDamage(this.power);
    }

    public void attackCreature(Creature target){
                tapCreature();

        if (this.text.contains("Направленный удар.")){fightCreature(target);}
        else {
            ArrayList<Creature> blocker;
            blocker = canAnyoneBlock(target);
            if (blocker.size() != 0) {
                int nc = Board.creature.get(owner.numberPlayer).indexOf(this);
                int nt = Board.creature.get(Board.opponent(owner).numberPlayer).indexOf(target);
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

    public void attackPlayer(Player target){
                tapCreature();
        if (this.text.contains("Направленный удар.")){fightPlayer(target);}
        else {
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

    public void takeDamage(int dmg){
        damage+=dmg;
        if (tougness>damage){
        }
        else {
           die();
        }
    }

    public void cry(Creature _cr, Player _pl){
        String txt = this.text.substring(this.text.indexOf("Найм:") + "Найм:".length() + 1, this.text.indexOf(".", this.text.indexOf("Найм:") + 1));
        System.out.println("Найм: " + txt);
        if (txt.contains("Выстрел по существу на ")){
            int dmg = getNumericAfterText(txt,"Выстрел по существу на ");
            Main.printToView(this.name+ " стреляет на "+dmg + " по "+_cr.name);
            if (!_cr.text.contains("Защита от выстрелов."))
            _cr.takeDamage(dmg);
            else{
                Main.printToView("У "+_cr.name+ " защита от выстрелов.");
            }
        }
        else if (txt.contains("Выстрел на ")){
            int dmg = getNumericAfterText(txt,"Выстрел на ");
            if (_cr!=null) {
                Main.printToView(this.name + " стреляет на " + dmg + " по " + _cr.name);
                if (!_cr.text.contains("Защита от выстрелов."))
                    _cr.takeDamage(dmg);
                else {
                    Main.printToView("У " + _cr.name + " защита от выстрелов.");
                }
            }
            else {
                Main.printToView(this.name + " стреляет на " + dmg + " по " + _pl.name);
                _pl.takeDamage(dmg);
            }
        }
    }

    public void die(){
        //May be wannts to free exemplar of creature, if you do this, change 'fight' method
//        this.isTapped=false;
//        this.damage=0;
        //And may be other
        // Or not?
        Board.removeCreatureFromPlayerBoard(this);
        Board.putCardToGraveyard(this, this.owner);
    }
}
