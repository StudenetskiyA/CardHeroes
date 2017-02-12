package com.company;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.company.Main.*;
import static com.company.MyFunction.ActivatedAbility;
import static com.company.MyFunction.ActivatedAbility.WhatAbility;

//Created by StudenetskiyA on 25.01.2017.
public class ResponseServerMessage extends Thread {
    String fromServer = "";

    ResponseServerMessage(String _fromServer) {
        fromServer = _fromServer;
    }

    public synchronized void run() {
        ready = false;
        //TODO For each command add Main.printToView. Server don't send text already
        if (fromServer.contains("#TotalStatusPlayer")) {//All player connected
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (players[0].playerName.equals(parameter.get(0))) {
                Main.isMyTurn = PlayerStatus.fromInteger(Integer.parseInt(parameter.get(1)));
                players[0].damage = Integer.parseInt(parameter.get(2));
                players[0].untappedCoin = Integer.parseInt(parameter.get(3));
                players[0].totalCoin = Integer.parseInt(parameter.get(4));
                players[0].temporaryCoin = Integer.parseInt(parameter.get(5));
                players[1].untappedCoin = Integer.parseInt(parameter.get(6));
                players[1].totalCoin = Integer.parseInt(parameter.get(7));
                players[1].temporaryCoin = Integer.parseInt(parameter.get(8));
                //9 is cards in deck expiried
                enemyHandSize = Integer.parseInt(parameter.get(10));
                int nCard = Integer.parseInt(parameter.get(11));
                players[0].cardInHand.clear();
                for (int i = 0; i < nCard; i++) {
                    players[0].cardInHand.add(Card.getCardByName(parameter.get(12 + i)));
                }
            }
        } else if (fromServer.contains("#PlayerStatus")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            Main.isMyTurn = PlayerStatus.fromInteger(Integer.parseInt(parameter.get(0)));
        } else if (fromServer.contains("#AddCardToHand")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            players[0].cardInHand.add(Card.getCardByName(parameter.get(0)));
            players[0].deck.removeCardFromDeckByName(parameter.get(0));//TODO Player must not know you own deck!
        } else if (fromServer.contains("#AddCardToGraveyard")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            players[np].graveyard.add(Card.getCardByName(parameter.get(1)));
        } else if (fromServer.contains("#Surrend")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            String[] args = new String[0];
            if (np==0){
                Main.printToView(1,Color.RED,"Вы проиграли.");
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                main.atEndOfPlay();
            } else {
                Main.printToView(1,Color.GREEN,"Ваш противник сдался.");
                System.out.println("Opp surr");
                main.repaint();
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                main.atEndOfPlay();
            }
        } else if (fromServer.contains("#RemoveCardFromHand")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            if (np==0){
                players[0].cardInHand.remove(Card.getCardByName(parameter.get(1)));
            } else {
                enemyHandSize--;
            }
        } else if (fromServer.contains("#ReturnToHand")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int nc = Integer.parseInt(parameter.get(1))-Board.getDiedCreatureLeftCount(np,Integer.parseInt(parameter.get(1)));
            //When queue response, you may target creature, but it number may not correct if left of it have died creature.
            players[np].cardInHand.add(Card.getCardByName(Board.creature.get(np).get(nc).name));
            Main.printToView(0, Board.creature.get(np).get(nc).name + " возвращается в руку.");
            Board.creature.get(np).remove(Integer.parseInt(parameter.get(1)));
        } else if (fromServer.contains("#PutCreatureToBoard")) {//#PutCreatureToBoard(Player, CreatureName)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            Board.creature.get(np).add(new Creature(Card.getCardByName(parameter.get(1)), players[np]));
            Main.printToView(0, Card.getCardByName(parameter.get(1)).name + " входит в игру.");
        } else if (fromServer.contains("#PutEquipToBoard")) {//#PutCreatureToBoard(Player, CreatureName)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int n=MyFunction.getEquipNumByType(Card.getCardByName(parameter.get(1)).creatureType);
            players[np].equpiment[n] = new Equpiment(Card.getCardByName(parameter.get(1)),players[np]);
            //TODO For event another text
            Main.printToView(0, players[np].name+" экипировал "+Card.getCardByName(parameter.get(1)).name + " .");
        } else if (fromServer.contains("#DieCreature")) {//#DieCreature(Player, CreatureNumOnBoard)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int nc = Integer.parseInt(parameter.get(1))-Board.getDiedCreatureLeftCount(np,Integer.parseInt(parameter.get(1)));
            //When queue response, you may target creature, but it number may not correct if left of it have died creature.
            Creature.die(players[np], Board.creature.get(np).get(nc));
        } else if (fromServer.contains("#UntapAll")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            players[np].untapAll();
        } else if (fromServer.contains("#TakeCreatureText")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            String text = parameter.get(2);
            int nc = Integer.parseInt(parameter.get(1))-Board.getDiedCreatureLeftCount(np,Integer.parseInt(parameter.get(1)));
            //When queue response, you may target creature, but it number may not correct if left of it have died creature.
            Board.creature.get(np).get(nc).effects.takeTextEffect(text);
        } else if (fromServer.contains("#LooseCreatureText")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            String text = parameter.get(2);
            int nc = Integer.parseInt(parameter.get(1))-Board.getDiedCreatureLeftCount(np,Integer.parseInt(parameter.get(1)));
            //When queue response, you may target creature, but it number may not correct if left of it have died creature.
            Board.creature.get(np).get(nc).effects.looseTextEffect(text);
        } else if (fromServer.contains("#TakeCreatureEffect")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int dmg = Integer.parseInt(parameter.get(3));
            int nc = Integer.parseInt(parameter.get(1))-Board.getDiedCreatureLeftCount(np,Integer.parseInt(parameter.get(1)));
            //When queue response, you may target creature, but it number may not correct if left of it have died creature.
            Board.creature.get(np).get(nc).effects.takeEffect(MyFunction.Effect.fromInteger(Integer.parseInt(parameter.get(2))), dmg);
        }  else if (fromServer.contains("#TakePlayerEffect")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int dmg = Integer.parseInt(parameter.get(2));
            //When queue response, you may target creature, but it number may not correct if left of it have died creature.
            players[np].effects.takeEffect(MyFunction.EffectPlayer.fromInteger(Integer.parseInt(parameter.get(1))), dmg);
        } else if (fromServer.contains("#TakeCreatureDamage")) {//#TakeCreatureDamage(Player, CreatureNumOnBoard, Damage)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int dmg = Integer.parseInt(parameter.get(2));
            int nc = Integer.parseInt(parameter.get(1))-Board.getDiedCreatureLeftCount(np,Integer.parseInt(parameter.get(1)));
            //When queue response, you may target creature, but it number may not correct if left of it have died creature.
            Creature.takeDamage(players[np], Board.creature.get(np).get(nc), dmg);
            Main.printToView(0, Board.creature.get(np).get(Integer.parseInt(parameter.get(1))).name + " получает " + dmg + " урона.");
        } else if (fromServer.contains("#TapCreature")) {//#TapCreature(Player, CreatureNumOnBoard, 1 - tap || 0 - untap)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int dmg = Integer.parseInt(parameter.get(2));
            int nc = Integer.parseInt(parameter.get(1))-Board.getDiedCreatureLeftCount(np,Integer.parseInt(parameter.get(1)));
            Creature.tap(players[np], Board.creature.get(np).get(nc), dmg);
        } else if (fromServer.contains("#TakeHeroDamage")) {//#TakeHeroDamage(Player, Damage)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int dmg = Integer.parseInt(parameter.get(1));
            players[np].takeDamage(dmg);
        } else if (fromServer.contains("#TapPlayer")) {//#TapPlayer(Player, 1 - tap || 0 - untap)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int dmg = Integer.parseInt(parameter.get(1));
            players[np].tap(dmg);
        } else if (fromServer.contains("#PlaySpell")) {//#PlaySpell(Player, SpellName, TargetHalfBoard[0-self,1-enemy], TargetCreatureNum[-1 means targets player])
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            Main.printToView(0, parameter.get(0)+" разыгрывает "+parameter.get(1));
        } else if (fromServer.contains("#Chat")) {//#Chat(Player, Text)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            messageArea.append(parameter.get(0) + "\n");
        } else if (fromServer.contains("#Message")) {//#Message(TypeN,Message)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            Main.printToView(Integer.parseInt(parameter.get(0)), parameter.get(1));
        } else if (fromServer.contains("#Attack(")) {//Just informative command
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int anp = (np==0)? 1:0;
            creatureWhoAttack = Integer.parseInt(parameter.get(1));
            creatureWhoAttackTarget = Integer.parseInt(parameter.get(2));
            String t;
            if (creatureWhoAttackTarget==-1) t=players[anp].name;
            else t=Board.creature.get(anp).get(creatureWhoAttackTarget).name;
            Main.printToView(0,Board.creature.get(np).get(creatureWhoAttack).name+" атакует "+t+".");
        } else if (fromServer.contains("#ChoiceBlocker(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (players[0].playerName.equals(parameter.get(0))) {
                isMyTurn = Main.PlayerStatus.IChoiceBlocker;
                creatureWhoAttack = Integer.parseInt(parameter.get(1));
                creatureWhoAttackTarget = Integer.parseInt(parameter.get(2));
                Main.printToView(0,Color.GREEN,"Выберете защитника.");
            }
        } else if (fromServer.contains("#ChoiceTarget")) {//#ChoiceTarget(Player, Status, CreatureNum, WhatAbility, Message)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (players[0].playerName.equals(parameter.get(0))) {
                Main.isMyTurn = PlayerStatus.fromInteger(Integer.parseInt(parameter.get(1)));
                //It may be ability of died creature
                ActivatedAbility.creature = Board.creature.get(0).get(Integer.parseInt(parameter.get(2)));
                ActivatedAbility.whatAbility = WhatAbility.fromInteger(Integer.parseInt(parameter.get(3)));
                Main.printToView(2, Color.GREEN, parameter.get(4));//change it
            }
        } else if (fromServer.contains("#ChoiceYesNo")) {//#ChoiceYesNo(Player, Card, Message, Yes, No)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (players[0].playerName.equals(parameter.get(0))) {
                Main.isMyTurn = PlayerStatus.choiceYesNo;
                main.userChoice = new UserChoice(main.viewField,parameter.get(1),parameter.get(2),parameter.get(3),parameter.get(4));
                main.userChoiceShow=true;
            }
        } else if (fromServer.contains("#ChoiceSearchInDeck")) {//#SearchInDeck(PlayerName,CardType,CardColor,CreatureType,CardCost,CardCostExactly,Message)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (players[0].playerName.equals(parameter.get(0))) {
                Main.isMyTurn = Main.PlayerStatus.searchX;
                Main.choiceXtype = Integer.parseInt(parameter.get(1));
                Main.choiceXcolor = Integer.parseInt(parameter.get(2));
                if (parameter.get(3).equals(" "))
                    Main.choiceXcreatureType = "";
                else Main.choiceXcreatureType = parameter.get(3);
                Main.choiceXcost = Integer.parseInt(parameter.get(4));
                Main.choiceXcostExactly = Integer.parseInt(parameter.get(5));
                if (parameter.get(6).equals(" "))
                    Main.choiceXname = "";
                else Main.choiceXname = parameter.get(6);
                Main.printToView(0, parameter.get(7));
            }
        } else if (fromServer.contains("#ChoiceSearchInGraveyard")) {//#SearchInDeck(PlayerName,CardType,CardColor,CreatureType,CardCost,CardCostExactly,Message)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (players[0].playerName.equals(parameter.get(0))) {
                Main.isMyTurn = PlayerStatus.digX;
                Main.choiceXtype = Integer.parseInt(parameter.get(1));
                Main.choiceXcolor = Integer.parseInt(parameter.get(2));
                if (parameter.get(3).equals("0"))
                    Main.choiceXcreatureType = "";
                else Main.choiceXcreatureType = parameter.get(3);
                Main.choiceXcost = Integer.parseInt(parameter.get(4));
                Main.choiceXcostExactly = Integer.parseInt(parameter.get(5));
                if (parameter.get(6).equals(" "))
                    Main.choiceXname = "";
                else Main.choiceXname = parameter.get(6);
                Main.printToView(0, parameter.get(7));
            }
        }

        synchronized (monitor) {
            ready = true;
            monitor.notifyAll();
        }

    }
}
