package com.company;

//Created by StudenetskiyA on 30.12.2016.

public class Creature extends Card {
    boolean isTapped;
    boolean isSummonedJust;
    boolean activatedAbilityPlayed = false;
    boolean takedDamageThisTurn = false;
    boolean attackThisTurn = false;
    boolean blockThisTurn = false;

    Player owner;
    int currentArmor = 0;
    int maxArmor = 0;
    int damage;//taked damage

    Effects effects = new Effects(this);

    public class Effects {
        Creature whis;
        String additionalText = "";
        public boolean isDie=false;
        public int poison = 0;
        public int bonusPower = 0;
        public int bonusPowerUEOT = 0;
        public int bonusTougness = 0;
        public int bonusArmor = 0;
        public int turnToDie = 999;
        boolean vulnerability = false;

        Effects(Creature _cr) {
            whis = _cr;
        }

        String getAdditionalText(){
            return additionalText;
        }

        int getBonusPower(){
            //TODO Remove logic from it. Server knows, it must say it to client.
            int staticBonus = 0;
            //TanGnome take + for power
            if ((creatureType.equals("Гном")) && (!name.equals("Тан гномов"))) {
                int tanFounded = 0;
                for (int i = 0; i < Board.creature.get(owner.numberPlayer).size(); i++) {
                    if (Board.creature.get(owner.numberPlayer).get(i).name.equals("Тан гномов")) tanFounded++;
                }
                staticBonus += tanFounded;
            }
            //Yatagan
            if (owner.equpiment[2]!=null &&owner.equpiment[2].name.equals("Орочий ятаган")) {
                staticBonus+=1;
            }
            //Flaming giant
            if (name.equals("Пылающий исполин")) {
                staticBonus+=damage;
            }
            //Vozd' clana on graveyard
            if (color==2) {
                int vozdFounded = 0;
                for (int i = 0; i < owner.graveyard.size(); i++) {
                    if (owner.graveyard.get(i).name.equals("Вождь клана")) vozdFounded++;
                }
                staticBonus += vozdFounded;
            }
            //Chain dog take and get + power
            if ((name.equals("Цепной пес"))) {
                int houndFounded = 0;
                for (int i = 0; i < Board.creature.get(owner.numberPlayer).size(); i++) {
                    if (Board.creature.get(owner.numberPlayer).get(i).name.equals("Цепной пес")) houndFounded++;
                }
                //and for opponent
                for (int i = 0; i < Board.creature.get(Board.opponentN(owner)).size(); i++) {
                    if (Board.creature.get(Board.opponentN(owner)).get(i).name.equals("Цепной пес")) houndFounded++;
                }
                staticBonus += houndFounded - 1;
            }
            return staticBonus+bonusPower+bonusPowerUEOT;
        }

        int getBonusTougness(){
            return bonusTougness;
        }

        void takeTextEffect(String txt){
            additionalText+=txt;
            Main.printToView(0,whis.name+ " получает '"+txt+"'.");
        }
        void looseTextEffect(String txt){
            if (!additionalText.contains(txt)) return;
            additionalText = additionalText.replaceFirst(txt,"");
        }

        void takeEffect(MyFunction.Effect effect,int p){
            switch(effect) {
                case poison: {
                    poison=p;
                    Main.printToView(0,whis.name+ " получает отравление на "+p);
                    break;
                }
                case vulnerability:{
                    vulnerability=true;
                    Main.printToView(0,whis.name+ " получает уязвимость.");
                    break;
                }
                case turnToDie:{
                    turnToDie=p;
                    Main.printToView(0,whis.name+ " получает чуму.");
                    break;
                }
                case die:{
                    isDie=true;
                    break;
                }
                case bonusPower:{
                    bonusPower+=p;
                    Main.printToView(0,whis.name+ " получает +"+p+" к удару.");
                    break;
                }
                case bonusPowerUEOT:{
                    bonusPowerUEOT+=p;
                    Main.printToView(0,whis.name+ " получает до конца хода +"+p+" к удару.");
                    break;
                }
			default:
				break;
            }
        }
    }

    boolean getCanAttack(){
        if (getIsSummonedJust()) return false;
        if (isTapped) return false;
        if (attackThisTurn) return false;
        return !MyFunction.textNotInTake(getText()).contains("Не может атаковать");
    }

    boolean getCanBlock(){
        if (isTapped) return false;
        if (blockThisTurn) return false;
        return !MyFunction.textNotInTake(getText()).contains("Не может блокировать");
    }
    String getText() {
        return text + "." + effects.getAdditionalText();
    }

    int getBonusOrMinusTougness(){
        if (damage>0) return -1;
        else if (damage==0 && effects.getBonusTougness()>0) return 1;
        else return 0;
    }

    int getBonusOrMinusPower(){
        if (getPower()>power) return 1;
        if (getPower()==power) return 0;
        else return -1;
    }

    boolean getIsSummonedJust() {
        if (text.contains("Рывок")) return false;
        if (effects.additionalText.contains("Рывок")) return false;
        //Chain dog take charge
        if ((name.equals("Цепной пес"))) {
            int houndFounded = 0;
            for (int i = 0; i < Board.creature.get(owner.numberPlayer).size(); i++) {
                if (Board.creature.get(owner.numberPlayer).get(i).name.equals("Цепной пес")) houndFounded++;
            }
            //and for opponent
            for (int i = 0; i < Board.creature.get(Board.opponentN(owner)).size(); i++) {
                if (Board.creature.get(Board.opponentN(owner)).get(i).name.equals("Цепной пес")) houndFounded++;
            }
            if (houndFounded > 1) return false;
        }
        return isSummonedJust;
    }

    int getMaxArmor() {
        return maxArmor + effects.bonusArmor;
    }

    public int getCurrentArmor() {
        return currentArmor;
    }

    int getPower() {
        return power + effects.getBonusPower();
    }

    int getTougness() {
        return hp + effects.getBonusTougness();
    }

    static void die(Player pl, Creature cr){
        //Animation
        Board.creature.get(pl.numberPlayer).remove(Board.creature.get(pl.numberPlayer).indexOf(cr));
    }

    static void tap(Player pl, Creature cr, int dmg) {
        //Animation
        boolean t=(dmg==1)? true:false;
        Board.creature.get(pl.numberPlayer).get(Board.creature.get(pl.numberPlayer).indexOf(cr)).isTapped=t;
    }

    static void takeDamage(Player pl, Creature cr, int dmg){
        //Animation
        Board.creature.get(pl.numberPlayer).get(Board.creature.get(pl.numberPlayer).indexOf(cr)).damage+=dmg;
        if (Board.creature.get(pl.numberPlayer).get(Board.creature.get(pl.numberPlayer).indexOf(cr)).damage<0)
            Board.creature.get(pl.numberPlayer).get(Board.creature.get(pl.numberPlayer).indexOf(cr)).damage=0;
    }

    Creature(Card _card, Player _owner) {
        super(_card.cost, _card.name, _card.creatureType, _card.color, _card.type, _card.targetType, _card.tapTargetType, _card.text, _card.power, _card.hp);
        image = _card.image;
        cost = _card.cost;
        isTapped = false;
        isSummonedJust = true;
        name = _card.name;
        owner = _owner;
        if (text.contains("Броня ")) {
            maxArmor = MyFunction.getNumericAfterText(text, "Броня ");
            currentArmor = getMaxArmor();
        }
    }

    boolean isDie() {
        if (effects.isDie) return true;
        return (getTougness() <= damage);//And other method to die!
    }
}
