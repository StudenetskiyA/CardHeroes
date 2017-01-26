package com.company;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.company.Main.*;

/**
 * Created by StudenetskiyA on 25.01.2017.
 */
public class ResponseServerMessage extends Thread {
    String fromServer="";

    ResponseServerMessage(String _fromServer){
        fromServer=_fromServer;
    }

    public void run(){
       // Main.memPlayerStatus=Main.isMyTurn;
         if (fromServer.contains("$DRAWCARD(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int pl = Board.getPlayerNumByName(parameter.get(0));
            //  System.out.println("Draw Card " + parameter.get(0));
            players[pl].drawCard();
        } else if (fromServer.contains("$ENDTURN(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            System.out.println("End turn " + parameter.get(0));
            if (players[0].playerName.equals(parameter.get(0))) {
                isMyTurn = Main.playerStatus.EnemyTurn;
                players[0].endTurn();
            } else if (players[1].playerName.equals(parameter.get(0))) {
                isMyTurn = Main.playerStatus.MyTurn;
                players[1].endTurn();
            }
            //main.repaint();
        } else if (fromServer.contains("$NEWTURN(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            //main.repaint();
            if (players[0].playerName.equals(parameter.get(0))) {
                isMyTurn = Main.playerStatus.MyTurn;
                players[0].newTurn();
            } else if (players[1].playerName.equals(parameter.get(0))) {
                isMyTurn = Main.playerStatus.EnemyTurn;
                players[1].newTurn();
            }
        } else if (fromServer.contains("$CHOISEBLOCKER(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (players[0].playerName.equals(parameter.get(0))) {
                isMyTurn = Main.playerStatus.IChoiseBlocker;
                creatureWhoAttack = Integer.parseInt(parameter.get(1));
                creatureWhoAttackTarget = Integer.parseInt(parameter.get(2));
            }
        } else if ((fromServer.contains("$CRYTARGET(")) || (fromServer.contains("$TAPTARGET("))) {
            // CRYTARGET also for DeathratleTarget
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int pl = Board.getPlayerNumByName(parameter.get(0));
            int apl = (pl == 0) ? 1 : 0;
            Creature cr;
            boolean death = false;
            if (parameter.get(1).equals("-1")) {
                //died creature ability.
                death = true;
                cr = new Creature(Card.ActivatedAbility.creature);
                // Card.ActivatedAbility.creature = null;
            } else {
                cr = Board.creature.get(pl).get(Integer.parseInt(parameter.get(1)));
            }
            if (parameter.get(2).equals("1")) pl = apl;
            if (parameter.get(3).equals("-1")) {
                if (fromServer.contains("$CRYTARGET("))
                    if (death) cr.deathratle(null, players[pl]);
                    else cr.battlecryTarget(null, players[pl]);
                else
                    cr.tapTargetAbility(null, players[pl]);
            } else {
                if (Board.creature.get(pl).size() - 1 >= Integer.parseInt(parameter.get(3))) {
                    if (fromServer.contains("$CRYTARGET("))
                        if (death)
                            cr.deathratle(Board.creature.get(pl).get(Integer.parseInt(parameter.get(3))), null);
                        else
                            cr.battlecryTarget(Board.creature.get(pl).get(Integer.parseInt(parameter.get(3))), null);
                    else
                        cr.tapTargetAbility(Board.creature.get(pl).get(Integer.parseInt(parameter.get(3))), null);
                }
            }
//            synchronized (monitor) {
//                ready = true;
//                monitor.notifyAll();
//            }
        } else if (fromServer.contains("$EQUIPTARGET(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int pl = Board.getPlayerNumByName(parameter.get(0));
            int apl = (pl == 0) ? 1 : 0;
            isMyTurn = Main.playerStatus.MyTurn;
            int equip = Integer.parseInt(parameter.get(1));
            if (parameter.get(2).equals("1")) {
                if (parameter.get(3).equals("-1"))
                    players[pl].equpiment[equip].tapTargetAbility(null, players[1]);
                else
                    players[pl].equpiment[equip].tapTargetAbility(Board.creature.get(apl).get(Integer.parseInt(parameter.get(3))), null);
            } else {
                if (parameter.get(3).equals("-1"))
                    players[pl].equpiment[equip].tapTargetAbility(null, players[0]);
                else
                    players[pl].equpiment[equip].tapTargetAbility(Board.creature.get(pl).get(Integer.parseInt(parameter.get(3))), null);
            }
        } else if (fromServer.contains("$HEROTARGET(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int pl = Board.getPlayerNumByName(parameter.get(0));
            int apl = (pl == 0) ? 1 : 0;
           // if (pl == 0) isMyTurn = Main.playerStatus.MyTurn;
           // else isMyTurn = Main.playerStatus.EnemyTurn;
            players[pl].isTapped = true;
            players[pl].untappedCoin -= Integer.parseInt(parameter.get(3));
            if (parameter.get(1).equals("1")) {
                if (parameter.get(2).equals("-1")) players[pl].ability(null, players[1]);
                else players[pl].ability(Board.creature.get(apl).get(Integer.parseInt(parameter.get(2))), null);
            } else {
                if (parameter.get(2).equals("-1")) players[pl].ability(null, players[0]);
                else players[pl].ability(Board.creature.get(pl).get(Integer.parseInt(parameter.get(2))), null);
            }
        } else if (fromServer.contains("$HERONOTARGET(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int pl = Board.getPlayerNumByName(parameter.get(0));
            //if (pl == 0) isMyTurn = Main.playerStatus.MyTurn;
            //else isMyTurn = Main.playerStatus.EnemyTurn;
            players[pl].isTapped = true;
            players[pl].untappedCoin -= Integer.parseInt(parameter.get(1));
            players[pl].abilityNoTarget();
        } else if (fromServer.contains("$BLOCKER(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int pl = Board.getPlayerNumByName(parameter.get(0));
            int apl = (pl == 0) ? 1 : 0;
            if (pl == 0) isMyTurn = Main.playerStatus.EnemyTurn;
            else isMyTurn = Main.playerStatus.MyTurn;

            Creature cr = Board.creature.get(apl).get(Integer.parseInt(parameter.get(1)));
            if (Integer.parseInt(parameter.get(2)) == -1) {
                if (Integer.parseInt(parameter.get(3)) == -1) {
                    //Fight with hero
                    cr.fightPlayer(players[pl]);
                } else {
                    Creature block = Board.creature.get(pl).get(Integer.parseInt(parameter.get(3)));
                    //Fight with bocker
                    block.blockThisTurn = true;
                    cr.fightCreature(block);
                    if (Integer.parseInt(parameter.get(4)) == 1) {
                        if (!block.text.contains("Опыт в защите."))
                            block.tapCreature();
                    }
                }
            } else {
                if (Integer.parseInt(parameter.get(3)) == -1) {
                    //Fight with first target
                    Creature block = Board.creature.get(pl).get(Integer.parseInt(parameter.get(2)));
                    cr.fightCreature(block);
                } else {
                    Creature block = Board.creature.get(pl).get(Integer.parseInt(parameter.get(3)));
                    //Fight with blocker
                    cr.fightCreature(block);
                    if (Integer.parseInt(parameter.get(4)) == 1) {
                        block.tapCreature();
                    }
                }
            }
        } else if (fromServer.contains("$PLAYCARD(")) {
            //$PLAYCARD(player, numInHand, targetCreature, targetPlayer[1,2])
            //$PLAYCARD(Jeremy,0,-1,Bob) - play 0 card to enemy.
            //$PLAYCARD(Jeremy,2,-1,-1) - play 2th card to board.
            //$PLAYCARD(Bob,1,1,Jeremy) - play 1th card to 1th creature of Jeremy
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int pl = Board.getPlayerNumByName(parameter.get(0));
            int apl = (pl == 0) ? 1 : 0;
            if (!parameter.get(2).equals("-1")) {//if card targets creature
                if ((parameter.get(3).equals(players[apl].playerName)))
                    players[pl].playCard(players[pl].cardInHand.get(Integer.parseInt(parameter.get(1))), Board.creature.get(apl).get(Integer.parseInt(parameter.get(2))), null);
                else //to self creature
                    players[pl].playCard(players[pl].cardInHand.get(Integer.parseInt(parameter.get(1))), Board.creature.get(pl).get(Integer.parseInt(parameter.get(2))), null);
            } else {
                if (parameter.get(3).equals(players[apl].playerName))//enemy
                    players[pl].playCard(players[pl].cardInHand.get(Integer.parseInt(parameter.get(1))), null, players[apl]);
                else if (parameter.get(3).equals(players[pl].playerName))//target - self player
                    players[pl].playCard(players[pl].cardInHand.get(Integer.parseInt(parameter.get(1))), null, players[pl]);
                else
                    players[pl].playCard(players[pl].cardInHand.get(Integer.parseInt(parameter.get(1))), null, null);
            }
        } else if (fromServer.contains("$PLAYWITHX(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int pl = Board.getPlayerNumByName(parameter.get(0));
            int apl = (pl == 0) ? 1 : 0;
            int x = Integer.parseInt(parameter.get(4));
            //visible off
            for (int i = 0; i < 9; i++) {
                choiceXLabel[i].setVisible(false);
            }
            if (!parameter.get(2).equals("-1")) {//if card targets creature
                if ((parameter.get(3).equals(players[apl].playerName)))
                    players[pl].playCardX(players[pl].cardInHand.get(Integer.parseInt(parameter.get(1))), Board.creature.get(apl).get(Integer.parseInt(parameter.get(2))), null, x);
                else //to self creature
                    players[pl].playCardX(players[pl].cardInHand.get(Integer.parseInt(parameter.get(1))), Board.creature.get(pl).get(Integer.parseInt(parameter.get(2))), null, x);
            } else {
                if (parameter.get(3).equals(players[apl].playerName))//enemy
                    players[pl].playCardX(players[pl].cardInHand.get(Integer.parseInt(parameter.get(1))), null, players[apl], x);
                else if (parameter.get(3).equals(players[pl].playerName))//target - self player
                    players[pl].playCardX(players[pl].cardInHand.get(Integer.parseInt(parameter.get(1))), null, players[pl], x);
                else
                    players[pl].playCardX(players[pl].cardInHand.get(Integer.parseInt(parameter.get(1))), null, null, x);
            }
        } else if (fromServer.contains("$ATTACKPLAYER(")) {//$ATTACKPLAYER(Player, Creature)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int pl = Board.getPlayerNumByName(parameter.get(0));
            int apl = (pl == 0) ? 1 : 0;
            printToView(0, Board.creature.get(pl).get(Integer.parseInt(parameter.get(1))).name + " атакует " + players[apl].name);
            Board.creature.get(pl).get(Integer.parseInt(parameter.get(1))).attackPlayer(players[apl]);
        } else if (fromServer.contains("$ATTACKCREATURE(")) {//$ATTACKREATURE(Player, Creature, TargetCreature)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int pl = Board.getPlayerNumByName(parameter.get(0));
            int apl = (pl == 0) ? 1 : 0;
            printToView(0, Board.creature.get(pl).get(Integer.parseInt(parameter.get(1))).name + " атакует " + Board.creature.get(apl).get(Integer.parseInt(parameter.get(2))).name);
            Board.creature.get(pl).get(Integer.parseInt(parameter.get(1))).attackCreature(Board.creature.get(apl).get(Integer.parseInt(parameter.get(2))));
        } else if (fromServer.contains("$FOUND(")) {//$FOUND(Player, Card)
            choiceXcolor = 0;
            choiceXtype = 0;
            choiceXcost = 0;
            choiceXcostExactly = 0;
            choiceXcreatureType = "";
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int pl = Board.getPlayerNumByName(parameter.get(0));
            if (parameter.get(1).equals("-1")) {
                if (pl == 0) {
                    printToView(0, "Вы ищете в колоде, но ничего подходящего не находите.");
                } else {
                    printToView(0, "Противник ищет в колоде, но ничего подходящего не находит.");
                }
            } else {
                if (pl == 0) {
                    Card card = players[0].deck.searchCard(parameter.get(1));
                    players[0].drawSpecialCard(card);
                    printToView(0, "Вы находите в колоде " + card.name + ".");
                } else {
                    Card card = players[1].deck.searchCard(parameter.get(1));
                    players[1].drawSpecialCard(card);
                    printToView(0, "Противник находит в колоде " + parameter.get(1) + ".");
                }
            }
        } else if (fromServer.contains("$FREE")) {
             synchronized (Main.cretureDiedMonitor) {
                        Main.readyDied=true;
                        Main.cretureDiedMonitor.notifyAll();
                    }
         } else {
            if (fromServer.contains(":")) {
                messageArea.append(fromServer + "\n");
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
            }
        }
        synchronized (monitor) {
               ready = true;
               monitor.notifyAll();
            }
//

//        synchronized (Main.firstMonitor) {
//            Main.firstMonitor.notifyAll();
//        }
    }
}
