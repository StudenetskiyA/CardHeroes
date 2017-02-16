package ru.berserk.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

//Created by StudenetskiyA on 27.01.2017.

public class CycleServerRead{

    public void  processCommand(String fromServer) {
            //TODO replay mode
//        if (!Main.isReplay) {
//        	 fromServer = Client.readLine();
//        } else {
//            try {
//                fromServer = getNextReplayLine();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
            if (fromServer != null) {
                //waiting is obsolete
//            if (!fromServer.equals("wait") && !fromServer.equals("")) {
//                writerToLog.println(fromServer);
//                System.out.println("Server: " + fromServer);
//            }
                if (fromServer.contains("$DISCONNECT")) {
                    System.out.println("Disconnect");
                    Main.message(MyFunction.MessageType.simpleText, "Разрыв соединения!");
                    Main.writerToLog.close();
                    Main.message(MyFunction.MessageType.error, "Opponent disconnected");
                   // message(loose,"Вы выиграли.");
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Main.main.atEndOfPlay();
                } else if (fromServer.contains("$YOUARENOTOK")) {//You client,deck or other NOT correct
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    String code_not_ok = parameter.get(0);
                    Main.message(MyFunction.MessageType.error, code_not_ok);
                    try {
                        //TODO Do something - call update client may be
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.exit(1);
                } else if (fromServer.contains("$YOUAREOK")) {//You client,deck and other correct
                    Main.isMyTurn = Main.PlayerStatus.waitOtherPlayer;
                    //Server send "wait", you must answer "wait" or server think you are gone
//                    while (true) {
//                         WebsocketClient.client.sendMessage("wait");
//                        String a = Client.readLine();
//                        //System.out.println("Sv = "+a);
//                        if (!a.equals("wait")) {
//                            break;
//                        }
//                    }
                } else if (fromServer.contains("$OPPONENTCONNECTED")) {//Both players connected
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    if (Main.replayCounter == 0) {
                    } else try {
                        Main.loadDeckFromFile(Main.simpleEnemyDeck, Main.replayDeck);//Only for replaying
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Load information about opponent - name and heroHame.
                    Card c = Card.getCardByName(parameter.get(1));
                    Main.players[1] = new Player(c, Main.simpleEnemyDeck, parameter.get(0), 1);
                    System.out.println("Opponent loaded. Name = "+Main.players[1].playerName);
                    //For tests, server may begin with any coin.
                    Main.heroLabel[1].setVisible(true);
                    Main.players[0].untappedCoin = Integer.parseInt(parameter.get(2));
                    Main.players[0].totalCoin = Integer.parseInt(parameter.get(2));
                    Main.players[1].untappedCoin = Integer.parseInt(parameter.get(2));
                    Main.players[1].totalCoin = Integer.parseInt(parameter.get(2));
                    if (Main.isMyTurn == Main.PlayerStatus.waitOtherPlayer) {
                        Main.isMyTurn = Main.PlayerStatus.MuliganPhase;
                        Main.main.repaint();
                    }
                } else {
                    Main.responseServerMessage = new ResponseServerMessage(fromServer);
                    Main.responseServerMessage.start();
                    //pause until response ends
                    synchronized (Main.monitor) {
                        try {
                            Main.monitor.wait();
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
                Main.main.repaint();
            }
        }
}
