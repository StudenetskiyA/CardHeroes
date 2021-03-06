package ru.berserk.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static ru.berserk.client.Main.*;

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
                    Card tmp = Card.getCardByName(parameter.get(12 + i * 2));
                    tmp.id = parameter.get(12 + i * 2 + 1);
                    players[0].cardInHand.add(tmp);
                }
            }
        } else if (fromServer.startsWith("#YouHaveDeck")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            PrepareBattleScreen.decksChoice.add(parameter.get(0));
            PrepareBattleScreen.decksChoiceHeroes.add(parameter.get(1));
            ArrayList<String> newDeck = new ArrayList<>();
            newDeck.add(parameter.get(0));
            newDeck.add(parameter.get(1));
            for (int i=2;i<parameter.size();i++){
                newDeck.add(parameter.get(i));
            }
            PrepareBattleScreen.myDecks.add(newDeck);
        } else if (fromServer.startsWith("#YouTotalCards")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            String c="";
            for (int i=0;i<parameter.size();i++){
                c+=parameter.get(i)+",";
            }
            PrepareBattleScreen.totalCards = c;
            System.out.println("Your total cards: "+c);
            PrepareBattleScreen.myCards = new ArrayList<>(Arrays.asList(c.split(",")));
        } else if (fromServer.contains("#PlayerStatus")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            Main.isMyTurn = PlayerStatus.fromInteger(Integer.parseInt(parameter.get(0)));
        } else if (fromServer.contains("#AddCardToHand")) {//PlayerName,CardName,CardID
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            if (np == 0) {
                Card tmp = Card.getCardByName(parameter.get(1));
                tmp.id = parameter.get(2);
                players[0].cardInHand.add(tmp);
                players[0].deck.removeCardFromDeckByName(tmp.name);//TODO Player must not know you own deck!
            } else {
                enemyHandSize++;
            }
        } else if (fromServer.contains("#AddCardToGraveyard")) {//PlayerName, CardName, CardID
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            Card tmp = Card.getCardByName(parameter.get(1));
            tmp.id = parameter.get(2);
            players[np].graveyard.add(tmp);
        } else if (fromServer.contains("#LoseGame")) {//Like surrend, but message other.
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            if (np == 0) {
                message(MyFunction.MessageType.loose, "Вы проиграли.");
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                main.atEndOfPlay();
            } else {
                if (parameter.get(1).equals("0"))
                message(MyFunction.MessageType.win, "Вы выиграли.");
                else
                    message(MyFunction.MessageType.win, "Ваш противник сдался.");
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                main.atEndOfPlay();
            }
        } else if (fromServer.contains("#RemoveCardFromHandById")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            if (np == 0) {
                players[0].cardInHand.remove(Board.getCardFromHandById(parameter.get(1)));
            } else {
                enemyHandSize--;
            }
        } else if (fromServer.contains("#PutOnBottomDeck")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            if (np == 0) {
                //May be not in hand?
                players[0].deck.cards.add(0,Board.getCardFromHandById(parameter.get(1)));
            }
        } else if (fromServer.contains("#RemoveCardFromGraveyard")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            if (np == 0) {
                players[0].graveyard.remove(Board.getCardFromGraveyardById(parameter.get(1)));
            } else {
                enemyHandSize--;
            }
        } else if (fromServer.contains("#RemoveEquip")) {//PlayerName, EquipID
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            Equpiment tmp = Board.getEqupimentByID(parameter.get(1));
            int n =  MyFunction.getEquipNumByType(tmp.creatureType);
            players[np].equpiment[n] = null;
            message(MyFunction.MessageType.simpleText, tmp.name + " уничтожена.");
        } else if (fromServer.contains("#ChangeControll")) {//PlayerOwnerName,CreatureID
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int anp = (np == 0) ? 1 : 0;
            Board.creature.get(anp).set(Board.creature.get(anp).size() - 1, Board.getCreatureByID(parameter.get(1)));//Copy all properties of creature
            Board.creature.get(np).remove(Board.getCreatureByID(parameter.get(1)));//Here need to call with playerN
        } else if (fromServer.contains("#ReturnToHand")) {//CreatureID
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            Creature tmp = Board.getCreatureByID(parameter.get(0));
            //TODO If null
            message(MyFunction.MessageType.simpleText, tmp.name + " возвращается в руку.");
            if (Board.creature.get(0).contains(tmp)) Board.creature.get(0).remove(tmp);
            if (Board.creature.get(1).contains(tmp)) Board.creature.get(1).remove(tmp);
        } else if (fromServer.contains("#PutCreatureToBoard")) {//#PutCreatureToBoard(Player, CreatureName, CreatureID)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            Creature tmp = new Creature(Card.getCardByName(parameter.get(1)), players[np]);
            tmp.id = parameter.get(2);
            Board.creature.get(np).add(tmp);
            message(MyFunction.MessageType.simpleText, Card.getCardByName(parameter.get(1)).name + " входит в игру.");
        } else if (fromServer.contains("#PutEquipToBoard")) {//#PutEquipToBoard(Player, EquipName,ID)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int n = MyFunction.getEquipNumByType(Card.getCardByName(parameter.get(1)).creatureType);
            players[np].equpiment[n] = new Equpiment(Card.getCardByName(parameter.get(1)), players[np]);
            players[np].equpiment[n].id = parameter.get(2);
            //TODO For event another text
            message(MyFunction.MessageType.simpleText, players[np].name + " экипировал " + Card.getCardByName(parameter.get(1)).name + " .");
        } else if (fromServer.contains("#AddEquipEffectHP")) {//#PutEquipToBoard(Player, EquipN, HP)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int n = Integer.parseInt(parameter.get(1));
            int hp = Integer.parseInt(parameter.get(2));
            players[np].equpiment[n].hp=hp;
        } else if (fromServer.contains("#DieCreature")) {//#DieCreature(Player, CreatureID)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            Creature.die(players[np], Board.getCreatureByID(parameter.get(1)));
        } else if (fromServer.contains("#UntapAll")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            players[np].untapAll();
        } else if (fromServer.contains("#TakeCreatureText")) {//Depricated
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            String text = parameter.get(2);
            int nc = Integer.parseInt(parameter.get(1)) - Board.getDiedCreatureLeftCount(np, Integer.parseInt(parameter.get(1)));
            Board.creature.get(np).get(nc).effects.takeTextEffect(text);
        } else if (fromServer.contains("#TakeCreatureIdText")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            String text = parameter.get(1);
            Creature tmp = Board.getCreatureByID(parameter.get(0));
            tmp.effects.takeTextEffect(text);
        } else if (fromServer.contains("#LooseCreatureText")) {//Depricated
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            String text = parameter.get(2);
            int nc = Integer.parseInt(parameter.get(1)) - Board.getDiedCreatureLeftCount(np, Integer.parseInt(parameter.get(1)));
            Board.creature.get(np).get(nc).effects.looseTextEffect(text);
        } else if (fromServer.contains("#LooseCreatureIdText")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            String text = parameter.get(1);
            Creature tmp = Board.getCreatureByID(parameter.get(0));
            tmp.effects.looseTextEffect(text);
        } else if (fromServer.contains("#TakeCreatureEffect")) { //Depricated
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int dmg = Integer.parseInt(parameter.get(3));
            int nc = Integer.parseInt(parameter.get(1)) - Board.getDiedCreatureLeftCount(np, Integer.parseInt(parameter.get(1)));
            Board.creature.get(np).get(nc).effects.takeEffect(MyFunction.Effect.fromInteger(Integer.parseInt(parameter.get(2))), dmg);
        } else if (fromServer.contains("#TakeCreatureIdEffect")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int dmg = Integer.parseInt(parameter.get(2));
            Creature tmp = Board.getCreatureByID(parameter.get(0));
            tmp.effects.takeEffect(MyFunction.Effect.fromInteger(Integer.parseInt(parameter.get(1))), dmg);
        }  else if (fromServer.contains("#LooseCreatureIdEffect")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            Creature tmp = Board.getCreatureByID(parameter.get(0));
            tmp.effects.looseEffect(MyFunction.Effect.fromInteger(Integer.parseInt(parameter.get(1))));
        } else if (fromServer.contains("#TakePlayerEffect")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int dmg = Integer.parseInt(parameter.get(2));
            //When queue response, you may target creature, but it number may not correct if left of it have died creature.
            players[np].effects.takeEffect(MyFunction.EffectPlayer.fromInteger(Integer.parseInt(parameter.get(1))), dmg);
        } else if (fromServer.contains("#TakeCreatureDamage")) {//#TakeCreatureDamage(Player, CreatureNumOnBoard, Damage)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int dmg = Integer.parseInt(parameter.get(2));
            Creature tmp = Board.getCreatureByID(parameter.get(1));
            tmp.takeDamage(dmg);
            message(MyFunction.MessageType.simpleText, tmp.name + " получает " + dmg + " урона.");
        } else if (fromServer.contains("#TapCreature")) {//#TapCreature(Player, CreatureID, 1 - tap || 0 - untap)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            Creature tmp = Board.getCreatureByID(parameter.get(1));
            tmp.tap(Integer.parseInt(parameter.get(2)));
        } else if (fromServer.contains("#TapEqupiment")) {//#TapCreature(Player, EquipID, 1 - tap || 0 - untap)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            Equpiment tmp = Board.getEqupimentByID(parameter.get(1));
            tmp.tap(Integer.parseInt(parameter.get(2)));
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
            message(MyFunction.MessageType.simpleText, parameter.get(0) + " разыгрывает " + parameter.get(1));
        } else if (fromServer.contains("#Chat")) {//#Chat(Player: Text)
            fromServer = fromServer.substring(fromServer.indexOf("(") + 1);
            fromServer = fromServer.substring(0, fromServer.lastIndexOf(")"));
            messageArea.setText(fromServer);
        } else if (fromServer.contains("#Message")) {//#Message(TypeN,Message)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            MyFunction.MessageType mt;
            if (parameter.get(0).equals("1")) mt = MyFunction.MessageType.error;
            else mt = MyFunction.MessageType.simpleText;
            message(mt, parameter.get(1));
        } else if (fromServer.contains("#Attack(")) {//Just informative command
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int np = (players[0].playerName.equals(parameter.get(0))) ? 0 : 1;
            int anp = (np == 0) ? 1 : 0;
            creatureWhoAttack = Integer.parseInt(parameter.get(1));
            creatureWhoAttackTarget = Integer.parseInt(parameter.get(2));
            String t;
            if (creatureWhoAttackTarget == -1) t = players[anp].name;
            else t = Board.creature.get(anp).get(creatureWhoAttackTarget).name;
            message(MyFunction.MessageType.simpleText, Board.creature.get(np).get(creatureWhoAttack).name + " атакует " + t + ".");
        } else if (fromServer.contains("#ChoiceBlocker(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (players[0].playerName.equals(parameter.get(0))) {
                isMyTurn = Main.PlayerStatus.IChoiceBlocker;
                creatureWhoAttack = Integer.parseInt(parameter.get(1));
                creatureWhoAttackTarget = Integer.parseInt(parameter.get(2));
                message(MyFunction.MessageType.choiceTarget, "Выберете защитника.");
            }
        } else if (fromServer.contains("#ChoiceForSpell")) {// #ChoiceForSpell(PlayerName,Status,TargetType,costN-,message)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (players[0].playerName.equals(parameter.get(0))) {
                Main.isMyTurn = PlayerStatus.fromInteger(Integer.parseInt(parameter.get(1)));
                MyFunction.ActivatedAbility.nonCreatureTargetType = Integer.parseInt(parameter.get(2));
                MyFunction.ActivatedAbility.nonCreatureTargetCost = (Integer.parseInt(parameter.get(3)) == 0) ? 999 : Integer.parseInt(parameter.get(3));
                MyFunction.ActivatedAbility.whatAbility = MyFunction.ActivatedAbility.WhatAbility.spellAbility;
                message(MyFunction.MessageType.choiceTarget, parameter.get(4));
            }
        } else if (fromServer.contains("#ChoiceTarget")) {//#ChoiceTarget(Player, Status, CreatureNum, WhatAbility, Message, Able 1 - yes, 0 - no)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (players[0].playerName.equals(parameter.get(0))) {
                Main.isMyTurn = PlayerStatus.fromInteger(Integer.parseInt(parameter.get(1)));
                //It may be ability of died creature
                MyFunction.ActivatedAbility.creature = Board.creature.get(0).get(Integer.parseInt(parameter.get(2)));
                MyFunction.ActivatedAbility.whatAbility = MyFunction.ActivatedAbility.WhatAbility.fromInteger(Integer.parseInt(parameter.get(3)));
                Main.ableToChoiceTarget = (parameter.get(5).equals("1")) ? true:false;
                message(MyFunction.MessageType.choiceTarget, parameter.get(4));//change it or not?
            }
        } else if (fromServer.contains("#ChoiceYesNo")) {//#ChoiceYesNo(Player, Card, Message, Yes, No)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (players[0].playerName.equals(parameter.get(0))) {
                Main.isMyTurn = PlayerStatus.choiceYesNo;
                main.userChoice = new UserChoice(main.viewField, parameter.get(1), parameter.get(2), parameter.get(3), parameter.get(4));
                main.userChoiceShow = true;
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
                // message(MyFunction.MessageType.simpleText, parameter.get(7));
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
            }
        }

        synchronized (monitor) {
            ready = true;
            monitor.notifyAll();
        }

    }
}
