package com.company;

import java.util.ArrayList;

/**
 * Created by samsung on 30.12.2016.
 */
public class Player extends Card{
    public int numberPlayer;
    public int damage;
    String playerName;
    public int totalCoin;
    public int untappedCoin;
    public int temporaryCoin=0;
    public boolean isTapped=false;
    public Deck deck;
    public ArrayList<Card> cardInHand;
    public ArrayList<Card> graveyard;
    public Card armor;

    public Player(Card _card,Deck _deck,String _playerName,int _n){
        super(0,_card.name,1,0,_card.targetType,0,_card.text,0,_card.hp);
        deck=_deck;
        playerName=_playerName;
        cardInHand = new ArrayList<Card>();
        graveyard = new ArrayList<Card>();
        numberPlayer=_n;
    }

    public Player(Deck _deck,String _heroName,String _playerName,int _n, int _hp){
        super(0,_heroName,1,0,0,0,"",0,_hp);
        deck=_deck;
        playerName=_playerName;
        cardInHand = new ArrayList<Card>();
        graveyard = new ArrayList<Card>();
        numberPlayer=_n;
    }

    public void endTurn(){
        totalCoin-=temporaryCoin;
        if (untappedCoin>totalCoin) untappedCoin=totalCoin;
        temporaryCoin=0;
        //Creature effects until eot
        if (!Board.creature.get(0).isEmpty()) {
        for (int i = Board.creature.get(0).size() - 1; i >= 0; i--)
            Board.creature.get(0).get(i).effects.EOT();}
       if (!Board.creature.get(1).isEmpty()) {
           for (int i = Board.creature.get(1).size() - 1; i >= 0; i--)
            Board.creature.get(1).get(i).effects.EOT();}

        Board.opponent(this).newTurn();
    }

    public void newTurn(){
        Board.turnCount++;
        Main.printToView("Ход номер "+Board.turnCount+", игрок "+playerName);
        isTapped=false;
        //Get coin
        if (totalCoin<10) totalCoin++;
        //Untap
        untappedCoin=totalCoin;

        for (int i=Board.creature.get(numberPlayer).size()-1;i>=0;i--){
                //untap
                Board.creature.get(numberPlayer).get(i).isSummonedJust=false;
                Board.creature.get(numberPlayer).get(i).isTapped=false;
                //poison
            if ((Board.creature.get(numberPlayer).get(i).poison!=0) && (!Board.creature.get(numberPlayer).get(i).text.contains("Защита от отравления.")))
                Board.creature.get(numberPlayer).get(i).takeDamage( Board.creature.get(numberPlayer).get(i).poison, Creature.DamageSource.poison);
            //armor
            Board.creature.get(numberPlayer).get(i).currentArmor= Board.creature.get(numberPlayer).get(i).maxArmor;
            //for rage
            Board.creature.get(numberPlayer).get(i).takedDamageThisTurn=false;
            Board.creature.get(numberPlayer).get(i).attackThisTurn=false;
            Board.creature.get(numberPlayer).get(i).blockThisTurn=false;
        }
        //Draw
        if (Board.turnCount!=1) drawCard();//First player not draw card in first turn. It's rule.
    }

    void playCard(Card _card, Creature _targetCreature, Player _targetPlayer){
        int num = cardInHand.indexOf(_card);
        if (num==-1) return;

        if (untappedCoin>=_card.cost){
            untappedCoin-=_card.cost;
            //put on table or cast spell
            if (_card.type==1) {
                //release text on spell
                //check target
                if (_targetPlayer != null) {
                    _card.playOnPlayer(this,_targetPlayer);
                }
                if (_targetCreature != null) {
                    _card.playOnCreature(this,_targetCreature);
                }
                //No target
                if ((_targetCreature == null) && (_targetPlayer == null))
                ability(_card,this,null,null,_card.text);
                //and after play
                Board.putCardToGraveyard(_card,this);
            }
            else if (_card.type==2){
                //creature
                Board.addCreatureToBoard(_card,this);
            }
            else if (_card.type==3){
                if (this.armor!=null) Board.putCardToGraveyard(this.armor,this);
                this.armor=new Card(_card);
                Main.printToView(name+ " экипировал "+ _card.name+".");
            }
            //remove from hand
            cardInHand.remove(_card);
        }
        else{
            Main.printToView("Не хватает монет.");
        }
    }

    void drawCard(){
        if (deck.haveTopDeck())
        cardInHand.add(deck.drawTopDeck());
        else {
            Main.printToView("Deck of "+playerName+" is empty.");
        }
    }

    public void takeDamage(int dmg){
        if (armor!=null) {
            if (armor.name.equals("Плащ Исхара")) {
                //Плащ исхара
                int tmp = dmg;
                dmg -= armor.hp;
                armor.hp -= tmp;
                if (dmg < 0) dmg = 0;
                Main.printToView("Плащ Исхара предотвратил "+(tmp-dmg)+" урона.");
                if (armor.hp <= 0) {
                    Board.putCardToGraveyard(armor, this);
                    armor=null;
                }
            }
        }
        if (hp>damage+dmg){
            damage+=dmg;
        }
        else {
            System.out.println("Player lose game.");
            Main.printToView(playerName + " проиграл игру.");
            //TODO Lose play
        }
    }
    public void heal(int dmg){
        damage-=dmg;
        if (damage<0) damage=0;
    }

    public void abilityNoTarget() {
        String txt = this.text.substring(this.text.indexOf("ТАП:") + "ТАП:".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАП:") + 1));
        System.out.println("ТАПТ HERO: " + txt);
        isTapped=true;
        Card.ability(this,this,null, null, txt);
    }

    public void ability(Creature _cr, Player _pl) {
        String txt = this.text.substring(this.text.indexOf("ТАПТ:") + "ТАПТ:".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАПТ:") + 1));
        System.out.println("ТАПТ HERO: " + txt);
        isTapped=true;
        Card.ability(this,this,_cr, _pl, txt);
    }

    public String handToString(){
        String tmp="";
        for (Card card: cardInHand
             ) {
            tmp+=card.name;
            tmp+=",";
        }
        return tmp;
    }
}
