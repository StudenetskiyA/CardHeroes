package com.company;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.company.Main.main;
import static com.company.Main.*;

/**
 * Created by StudenetskiyA on 27.01.2017.
 */
public class CycleServerRead extends Thread {

    @Override
    public void run() {
        super.run();
        while (true) {
            String fromServer = "";
            if (!Main.isReplay) {
                try {
                    fromServer = Client.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.exit(1);
                } else if (fromServer.contains("$YOUARENOTOK")) {//You client,deck or other NOT correct
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    String code_not_ok = parameter.get(0);
                    printToView(0, code_not_ok);
                } else if (fromServer.contains("$YOUAREOK")) {//You client,deck and other correct
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    sufflingConst = Integer.parseInt(parameter.get(0));

                    Main.isMyTurn = Main.playerStatus.waitOtherPlayer;
                    Main.simpleDeck.suffleDeck(sufflingConst);
                    while (true) {
                        Client.writeLine("wait");
                        String a = "";
                        try {
                            a = Client.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // System.out.println(a);
                        if (!a.equals("wait")) {
                            //           System.out.println("Server said NO WAIT");
                            Main.firstResponse = false;
                            break;
                        }
                    }
                } else if (fromServer.contains("$OPPONENTCONNECTED")) {//All player connected
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    if (replayCounter == 0)
                        try {
                            loadDeckFromServer(simpleEnemyDeck);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    else try {
                        loadDeckFromFile(simpleEnemyDeck, replayDeck);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Card c = new Card(simpleEnemyDeck.cards.get(0));
                    try {
                        playerHeroClick[1].image = ImageIO.read(Main.class.getResourceAsStream("cards/heroes/" + c.name + ".jpg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    players[1] = new Player(c, simpleEnemyDeck, parameter.get(0), 1);
                    simpleEnemyDeck.cards.remove(0);

                    simpleEnemyDeck.suffleDeck(sufflingConst);

                    players[0].untappedCoin = coinStart;
                    players[0].totalCoin = coinStart;
                    players[1].untappedCoin = coinStart;
                    players[1].totalCoin = coinStart;
                    if (isMyTurn == Main.playerStatus.waitOtherPlayer) {
                        for (int i = 0; i <= 3; i++) {
                            players[0].drawCard();
                            players[1].drawCard();
                        }
                        isMyTurn = Main.playerStatus.MuliganPhase;
                        main.repaint();
                    }
                } else {
                    responseServerMessage = new ResponseServerMessage(fromServer);
                    responseServerMessage.start();
                }

                main.repaint();
            }
        }
    }
}
