package com.company;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.company.Main.*;

//Created by StudenetskiyA on 27.01.2017.

public class CycleServerRead extends Thread {

    @Override
    public synchronized void run() {
        super.run();
        while (true) {
            String fromServer = "";
            if (!Main.isReplay) {
                fromServer = Client.readLine();
            } else {
                try {
                    fromServer = getNextReplayLine();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (fromServer != null) {
                if (!fromServer.equals("wait") && !fromServer.equals("")) {
                    writerToLog.println(fromServer);
                    System.out.println("Server: " + fromServer);
                }
                if (fromServer.contains("$DISCONNECT")) {
                    System.out.println("Disconnect");
                    printToView(0, "Разрыв соединения!");
                    writerToLog.close();
                    printToView(1, "Opponent disconnected");
                    try {
                        //TODO Do something - return to first screen as example
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.exit(1);
                } else if (fromServer.contains("$YOUARENOTOK")) {//You client,deck or other NOT correct
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    String code_not_ok = parameter.get(0);
                    printToView(0, code_not_ok);
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
                    while (true) {
                        Client.writeLine("wait");
                        String a = Client.readLine();
                        if (!a.equals("wait")) {
                            break;
                        }
                    }
                } else if (fromServer.contains("$OPPONENTCONNECTED")) {//Both players connected
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    if (replayCounter == 0) {
                    } else try {
                        loadDeckFromFile(simpleEnemyDeck, replayDeck);//Only for replaying
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Load information about opponent - name and heroHame.
                    Card c = Card.getCardByName(parameter.get(1));
                    players[1] = new Player(c, simpleEnemyDeck, parameter.get(0), 1);
                    //For tests, server may begin with any coin.
                    Main.heroLabel[1].setVisible(true);
                    players[0].untappedCoin = Integer.parseInt(parameter.get(2));
                    players[0].totalCoin = Integer.parseInt(parameter.get(2));
                    players[1].untappedCoin = Integer.parseInt(parameter.get(2));
                    players[1].totalCoin = Integer.parseInt(parameter.get(2));
                    if (isMyTurn == Main.PlayerStatus.waitOtherPlayer) {
                        isMyTurn = Main.PlayerStatus.MuliganPhase;
                        main.repaint();
                    }
                } else {
                    responseServerMessage = new ResponseServerMessage(fromServer);
                    responseServerMessage.start();
                    //pause until response ends
                    synchronized (monitor) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
                main.repaint();
            }
        }
    }
}
