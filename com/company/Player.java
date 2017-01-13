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
    public Deck deck;
    public ArrayList<Card> cardInHand;
    public ArrayList<Card> graveyard;

    public Player(Deck _deck,String _playerName,int _n, int _hp){
        super(0,"Тарна",1,0,0,0,"",0,_hp);
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

        Board.opponent(this).newTurn();
    }

    public void newTurn(){
        Board.turnCount++;
        Main.printToView("Ход номер "+Board.turnCount+", игрок "+playerName);

        //Get coin
        if (totalCoin<10) totalCoin++;
        //Untap
        untappedCoin=totalCoin;

        for (int i=Board.creature.get(numberPlayer).size()-1;i>=0;i--){
                Board.creature.get(numberPlayer).get(i).isSummonedJust=false;
                Board.creature.get(numberPlayer).get(i).isTapped=false;
            if ((Board.creature.get(numberPlayer).get(i).poison!=0) && (!Board.creature.get(numberPlayer).get(i).text.contains("Защита от отравления.")))
                Board.creature.get(numberPlayer).get(i).takeDamage( Board.creature.get(numberPlayer).get(i).poison);
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
                    _card.playOnPlayer(_targetPlayer);
                }
                if (_targetCreature != null) {
                    _card.playOnCreature(_targetCreature);
                }
                //No target
                ability(_card,null,null,_card.text);
                //and after play
                Board.putCardToGraveyard(_card,this);
            }
            else if (_card.type==2){
                //creature
                Board.addCreatureToBoard(_card,this);
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
        if (hp>damage+dmg){
            damage+=dmg;
        }
        else {
            System.out.println("Player lose game.");
            //TODO Lose play
        }
    }
    public void heal(int dmg){
        damage-=dmg;
        if (damage<0) damage=0;
    }

    public void ability(Card _who,Creature _cr, Player _pl, String txt) {

        if (txt.contains(("Излечить вашего героя на "))){
            int dmg = getNumericAfterText(txt,"Излечить вашего героя на ");
            heal(dmg);
            Main.printToView(this.playerName+" излечил "+dmg+" урона.");
        }
        if (txt.contains(("Получите * "))){
            int dmg = getNumericAfterText(txt,"Получите * ");
            untappedCoin+=dmg;
            totalCoin+=dmg;
            Main.printToView(this.playerName+" получил "+dmg+" монет.");
        }
        if (txt.contains(("Получите до конца хода * "))){
            int dmg = getNumericAfterText(txt,"Получите до конца хода * ");
            untappedCoin+=dmg;
            totalCoin+=dmg;
            temporaryCoin+=dmg;
            Main.printToView(this.playerName+" получил "+dmg+" монет до конца хода.");
        }
        if (txt.contains(("Ранить каждое существо противника на "))){
            int dmg = getNumericAfterText(txt,"Ранить каждое существо противника на ");
            int op= Board.opponentN(this);
            for (int i=Board.creature.get(op).size()-1;i>=0;i--){
                Board.creature.get(op).get(i).takeDamage(dmg);
            }
            Main.printToView(_who.name+" ранит всех существ противника на "+dmg+".");
        }
        if (txt.contains(("Ранить каждое существо на "))){
            int dmg = getNumericAfterText(txt,"Ранить каждое существо на ");
            int op= Board.opponentN(this);
            for (int i=Board.creature.get(op).size()-1;i>=0;i--){
                Board.creature.get(op).get(i).takeDamage(dmg);
            }
            for (int i=Board.creature.get(this.numberPlayer).size()-1;i>=0;i--){
                Board.creature.get(this.numberPlayer).get(i).takeDamage(dmg);
            }
            Main.printToView(_who.name+" ранит всех существ на "+dmg+".");
        }
        //target
        if (txt.contains("Выстрел по существу на ")) {
            int dmg = getNumericAfterText(txt, "Выстрел по существу на ");
            Main.printToView(_who.name + " стреляет на " + dmg + " по " + _cr.name);
            if (!_cr.text.contains("Защита от выстрелов."))
                _cr.takeDamage(dmg);
            else {
                Main.printToView("У " + _cr.name + " защита от выстрелов.");
            }
        } else if (txt.contains("Выстрел на ")) {
            int dmg = getNumericAfterText(txt, "Выстрел на ");
            if (_cr != null) {
                Main.printToView(_who.name + " стреляет на " + dmg + " по " + _cr.name);
                if (!_cr.text.contains("Защита от выстрелов."))
                    _cr.takeDamage(dmg);
                else {
                    Main.printToView("У " + _cr.name + " защита от выстрелов.");
                }
            } else {
                Main.printToView(_who.name + " стреляет на " + dmg + " по " + _pl.name);
                _pl.takeDamage(dmg);
            }
        }
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
