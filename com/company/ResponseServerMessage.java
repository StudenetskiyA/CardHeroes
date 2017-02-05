package com.company;

import java.awt.*;
import java.util.ArrayList;

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
                //5 is cards in deck expiried
                enemyHandSize = Integer.parseInt(parameter.get(6));
                int nCard = Integer.parseInt(parameter.get(7));
                players[0].cardInHand.clear();
                for (int i = 0; i < nCard; i++) {
                    players[0].cardInHand.add(Card.getCardByName(parameter.get(8 + i)));
                }
            }
        } else if (fromServer.contains("#PlayerStatus")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            Main.isMyTurn = PlayerStatus.fromInteger(Integer.parseInt(parameter.get(0)));
        } else if (fromServer.contains("#AddCardToHand")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            players[0].cardInHand.add(Card.getCardByName(parameter.get(0)));
            players[0].deck.removeCardFromDeckByName(parameter.get(0));//TODO Player must not know you own deck!
        } else if (fromServer.contains("#PutCreatureToBoard")) {//#PutCreatureToBoard(Player, CreatureName)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            Board.creature.get(np).add(new Creature(Card.getCardByName(parameter.get(1)), players[np]));
            Main.printToView(0, Card.getCardByName(parameter.get(1)).name + " входит в игру.");
        } else if (fromServer.contains("#DieCreature")) {//#DieCreature(Player, CreatureNumOnBoard)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            Creature.die(players[np], Board.creature.get(np).get(Integer.parseInt(parameter.get(1))));
        } else if (fromServer.contains("#UntapAll")) {
            players[0].untapAll();
        } else if (fromServer.contains("#TakeCreatureEffect")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int dmg = Integer.parseInt(parameter.get(3));
            Board.creature.get(np).get(Integer.parseInt(parameter.get(1))).effects.takeEffect(MyFunction.Effect.fromInteger(Integer.parseInt(parameter.get(2))), dmg);
            Main.printToView(0, Board.creature.get(np).get(Integer.parseInt(parameter.get(1))).name + " получает " + dmg + " урона.");
        } else if (fromServer.contains("#TakeCreatureDamage")) {//#TakeCreatureDamage(Player, CreatureNumOnBoard, Damage)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int dmg = Integer.parseInt(parameter.get(2));
            Creature.takeDamage(players[np], Board.creature.get(np).get(Integer.parseInt(parameter.get(1))), dmg);
            Main.printToView(0, Board.creature.get(np).get(Integer.parseInt(parameter.get(1))).name + " получает " + dmg + " урона.");
        } else if (fromServer.contains("#TapCreature")) {//#TapCreature(Player, CreatureNumOnBoard, 1 - tap || 0 - untap)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int dmg = Integer.parseInt(parameter.get(2));
            Creature.tap(players[np], Board.creature.get(np).get(Integer.parseInt(parameter.get(1))), dmg);
            Main.printToView(0, "");
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
        } else if (fromServer.contains("#ChoiceBlocker(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (players[0].playerName.equals(parameter.get(0))) {
                isMyTurn = Main.PlayerStatus.IChoiceBlocker;
                creatureWhoAttack = Integer.parseInt(parameter.get(1));
                creatureWhoAttackTarget = Integer.parseInt(parameter.get(2));
            }
        } else if (fromServer.contains("#ChoiceTarget")) {//#ChoiceTarget(Player, Status, CreatureNum, WhatAbility, Message)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (players[0].playerName.equals(parameter.get(0))) {
                Main.isMyTurn = PlayerStatus.fromInteger(Integer.parseInt(parameter.get(1)));
                ActivatedAbility.creature = Board.creature.get(0).get(Integer.parseInt(parameter.get(2)));
                ActivatedAbility.whatAbility = WhatAbility.fromInteger(Integer.parseInt(parameter.get(3)));
                Main.printToView(2, Color.GREEN, parameter.get(4));
            }
        } else if (fromServer.contains("#ChoiceSearchInDeck")) {//#SearchInDeck(PlayerName,CardType,CardColor,CreatureType,CardCost,CardCostExactly,Message)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (players[0].playerName.equals(parameter.get(0))) {
                Main.isMyTurn = Main.PlayerStatus.searchX;
                Main.choiceXtype = Integer.parseInt(parameter.get(1));
                Main.choiceXcolor = Integer.parseInt(parameter.get(2));
                if (parameter.get(3).equals("0"))
                    Main.choiceXcreatureType = "";
                else Main.choiceXcreatureType = parameter.get(3);
                Main.choiceXcost = Integer.parseInt(parameter.get(4));
                Main.choiceXcostExactly = Integer.parseInt(parameter.get(5));
                Main.printToView(0, parameter.get(6));
            }
        }

        synchronized (monitor) {
            ready = true;
            monitor.notifyAll();
        }

    }
}
