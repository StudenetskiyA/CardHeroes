package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main extends JFrame {
    private static final String CLIENT_VERSION = "0.01";
    private static ArrayList<String> replay;
    static int replayCounter = 0;
    static boolean connected = false;
    static Font font;
    static Graphics2D g1;
    static FontMetrics metrics;
    private static String serverPort = "6666";
    private static final String address = "127.0.0.1";//"cardheroes.hldns.ru";
    private static PrintWriter writerToLog;
    static int sufflingConst = 21;//By default 21, in normal - get from server
    //View constant
    private static final int B0RDER_RIGHT = 10;
    private static final int B0RDER_LEFT = 10;
    private static final int B0RDER_BOTTOM = 40;
    private static final int B0RDER_TOP = 10;
    private static final int B0RDER_BETWEEN = 5;
    private static final double CARD_SIZE_FROM_SCREEN = 0.09;
    private static int heroW;
    private static int heroH;
    private static int smallCardW;
    private static int smallCardH;
    private static int bigCardW;
    private static int bigCardH;
    private static int cardX;
    //Elements of view
    private static Main main = new Main();
    private static ViewField viewField = new ViewField();
    private static JLabel deckClick = new JLabel();
    private static JLabel menuClick = new JLabel();
    private static JLabel weaponClick = new JLabel();
    private static JLabel cardClick[] = new JLabel[9];
    private static JLabel playerUnitClick[][] = new JLabel[2][9];
    private static JLabel battlegroundClick = new JLabel();
    private static JLabel enemyHeroClick = new JLabel();
    private static JLabel playerHeroClick = new JLabel();
    private static JLabel playerCoinLabel = new JLabel();
    private static JLabel enemyCoinLabel = new JLabel();
    private static JLabel playerGraveyardClick = new JLabel();
    private static JLabel enemyGraveyardClick = new JLabel();
    private static JLabel gameLog = new JLabel();
    private static JLabel endTurnClick = new JLabel();
    private static JLabel choiceXLabel[] = new JLabel[9];
    private static JLabel searchXLabel[] = new JLabel[40];
    private static JLabel message = new JLabel();
    //Static image for button, background and etc.
    private static Image background;
    private static BufferedImage heroImage;
    private static BufferedImage heroNoArmorImage;
    private static BufferedImage heroNoAmuletImage;
    private static BufferedImage heroNoWeaponImage;
    private static BufferedImage enemyImage;
    private static Image heroCoinImage;
    private static Image heroDeckImage;
    private static Image menuImage;
    private static Image endTurnImage;
    private static Image redcrossImage;
    private static Image heroGraveyardImage;

    static Player[] players = new Player[2];
    private static Deck simpleDeck;
    private static Deck simpleEnemyDeck;

    private static Card cardMem;
    private static Creature creatureMem;


    private static String whereMyMouse;
    private static int whereMyMouseNum;
    private static int repainted;//For test how many times called onRepaint
    static ArrayList<Card> founded;
    private static int coinStart = 0;
    private static MessageToShow messageToShow = new MessageToShow(" ", 0);

    enum playerStatus {MyTurn, EnemyTurn, IChoiseBlocker, EnemyChoiseBlocker, MuliganPhase, waitingForConnection, waitOtherPlayer, waitingMulligan, choiseX, searchX, choiseTarget}

    static int choiceXcolor = 0;
    static int choiceXtype = 0;
    static String choiceXcreatureType = "";
    static int choiceXcost = 0;
    static String choiceXtext;

    //  static Card.ActivatedAbility Card.ActivatedAbility;

    static playerStatus isMyTurn = playerStatus.waitingForConnection;
    static boolean wantToMulligan[] = new boolean[4];
    static int creatureWhoAttack;
    static int creatureWhoAttackTarget;
    static int hilightMyCreature = -1;
    static int hilightMyCard = -1;
    private static String replayDeck = "";

    public static void main(String[] args) throws IOException, InterruptedException {
        String replayName = "";

        loadImage();
        setInitialProperties();

        String par1 = "PlayerName";
        String par2 = "defaultDeck";

        for (int i = 0; i < args.length; i++) {
            if (i == 1) par1 = args[i];
            if (i == 2) par2 = args[i];
            if (i == 0) serverPort = args[i];
            if (i == 3) coinStart = Integer.parseInt(args[i]);
            if (i == 4) replayName = args[i];
            if (i == 5) replayDeck = args[i];
        }

        simpleDeck = new Deck(par2);
        simpleEnemyDeck = new Deck("defaultDeck");
        loadDeckFromFile(simpleDeck, par2);
        Card c = new Card(simpleDeck.cards.get(0));
        simpleDeck.cards.remove(0);
        heroImage = ImageIO.read(Main.class.getResourceAsStream("cards/heroes/" + c.name + ".jpg"));
        players[0] = new Player(c, simpleDeck, par1, 0);
        players[1] = new Player(simpleDeck, "", par1, 0, 30);

        main.setLocation(477, 0);
        main.setSize(890, 688);
        main.setVisible(true);
        //FULL SCREEN
//        main.dispose();
//        main.setUndecorated(true);
        //????? Here
//        main.setAlwaysOnTop(true);
//        main.setResizable(false);
//        Toolkit tk = Toolkit.getDefaultToolkit();
//        int xsize = (int)tk.getScreenSize().getWidth();
//        int ysize = (int)tk.getScreenSize().getHeight();
//        main.setSize(xsize,ysize);
//        B0RDER_BOTTOM=10;
//        main.setVisible(true);
        //
        viewField.setVisible(true);

        replay = new ArrayList<>();
        if (serverPort.equals("replay")) {

            InputStream path = Main.class.getResourceAsStream("replays/" + replayName + ".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(path, "windows-1251"));

            try {
                String line;
                while ((line = br.readLine()) != null) {
                    // process the line.
                    replay.add(line);
                    System.out.println(line);
                }
            } catch (Exception x) {
                System.out.println("Cloud not read replay.");
            }
            Main.gameLog.setText("<html>");
            printToView(0, "Player=" + players[0].playerName + ",port=" + serverPort);
            isMyTurn = playerStatus.waitOtherPlayer;
            cycleServerRead(true);
        } else {
            try {
                //TODO while connect
                Client.connect(Integer.parseInt(serverPort), address);
            } catch (Exception x) {
                System.out.println("Cloud not connect to server.");
            }

            Main.gameLog.setText("<html>");
            printToView(0, "Player=" + players[0].playerName + ",port=" + serverPort);

            if (connected) {
                System.out.println("$IAM(" + players[0].playerName + "," + players[0].deck.name + "," + CLIENT_VERSION + ")");
                Client.writeLine("$IAM(" + players[0].playerName + "," + players[0].deck.name + "," + CLIENT_VERSION + ")");
                //Send deck
                Client.writeLine(players[0].name);
                for (Card card : players[0].deck.cards) {
                    Client.writeLine(card.name);
                }
                Client.writeLine("$ENDDECK");
//
//                heroW = (int) (main.getWidth() * CARD_SIZE_FROM_SCREEN);
//                heroH = (heroW * 400 / 283);
//                 cardX = B0RDER_LEFT + B0RDER_BETWEEN * 3 + smallCardW * 3;
//                 Graphics g=main.getGraphics();
//                g.drawImage(menuImage, main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() / 2 - heroW * 75 / 283 - heroW * 150 / 283 - B0RDER_BETWEEN, heroW, heroW * 149 / 283, null);
//               // menuClick.setLocation(main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() / 2 - heroW * 75 / 283 - heroW * 150 / 283 - B0RDER_BETWEEN);
                //menuClick.setSize(heroW, heroW * 149 / 283);

                cycleServerRead(false);
            }
        }
    }

    private static String getNextReplayLine() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        if (replayCounter < replay.size()) {
            String tmp = replay.get(replayCounter);
            System.out.println("Replay - " + tmp);
            replayCounter++;
            return tmp;
        } else {
            return "Конец реплея.";
        }
    }

    private static void cycleServerRead(boolean isReplay) throws IOException, InterruptedException {
        while (true) {

            String fromServer;
            if (!isReplay)
                fromServer = Client.readLine();
            else {
                main.repaint();
                fromServer = getNextReplayLine();
            }
            if (fromServer != null) {
                writerToLog.println(fromServer);
                System.out.println("Server: " + fromServer);
                if (fromServer.contains("$DISCONNECT")) {
                    System.out.println("Disconnect");
                    printToView(0, "Разрыв соединения!");
                    writerToLog.close();
                    printToView(0, "Opponent disconnected");
                    TimeUnit.SECONDS.sleep(5);
                    System.exit(1);
                    break;
                } else if (fromServer.contains("$YOUARENOTOK")) {//You client,deck and other correct
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    String code_not_ok = parameter.get(0);
                    printToView(0, code_not_ok);
                    // main.repaint();
                } else if (fromServer.contains("$YOUAREOK")) {//You client,deck and other correct
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    sufflingConst = Integer.parseInt(parameter.get(0));
                    isMyTurn = playerStatus.waitOtherPlayer;
                    simpleDeck.suffleDeck(sufflingConst);
                    // main.repaint();
                } else if (fromServer.contains("$OPPONENTCONNECTED")) {//All player connected
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    if (replayCounter == 0)
                        loadDeckFromServer(simpleEnemyDeck);
                    else loadDeckFromFile(simpleEnemyDeck, replayDeck);
                    // System.out.print("enemy hero= "+simpleEnemyDeck.cards.get(0).name);

                    Card c = new Card(simpleEnemyDeck.cards.get(0));
                    enemyImage = ImageIO.read(Main.class.getResourceAsStream("cards/heroes/" + c.name + ".jpg"));
                    players[1] = new Player(c, simpleEnemyDeck, parameter.get(0), 1);
                    simpleEnemyDeck.cards.remove(0);

                    simpleEnemyDeck.suffleDeck(sufflingConst);

                    players[0].untappedCoin = coinStart;
                    players[0].totalCoin = coinStart;
                    players[1].untappedCoin = coinStart;
                    players[1].totalCoin = coinStart;
                    if (isMyTurn == playerStatus.waitOtherPlayer) {
                        for (int i = 0; i <= 3; i++) {
                            players[0].drawCard();
                            players[1].drawCard();
                        }
                        isMyTurn = playerStatus.MuliganPhase;
                        //   main.repaint();
                    }
                } else if (fromServer.contains("$MULLIGANEND(")) {
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    int pl = Board.getPlayerNumByName(parameter.get(0));
                    for (int i = 3; i >= 0; i--) {
                        if (Integer.parseInt(parameter.get(i + 1)) == 1) {
                            players[pl].deck.putOnBottomDeck(players[pl].cardInHand.get(i));
                            players[pl].cardInHand.remove(i);
                            players[pl].drawCard();
                        }
                    }
                    // main.repaint();
                } else if (fromServer.contains("$DRAWCARD(")) {
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    int pl = Board.getPlayerNumByName(parameter.get(0));
                    //  System.out.println("Draw Card " + parameter.get(0));
                    players[pl].drawCard();
                } else if (fromServer.contains("$ENDTURN(")) {
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    System.out.println("End turn " + parameter.get(0));
                    if (players[0].playerName.equals(parameter.get(0))) {
                        isMyTurn = playerStatus.EnemyTurn;
                        players[0].endTurn();
                    } else if (players[1].playerName.equals(parameter.get(0))) {
                        isMyTurn = playerStatus.MyTurn;
                        players[1].endTurn();
                    }
                    //main.repaint();
                } else if (fromServer.contains("$NEWTURN(")) {
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    //main.repaint();
                    if (players[0].playerName.equals(parameter.get(0))) {
                        isMyTurn = playerStatus.MyTurn;
                        players[0].newTurn();
                    } else if (players[1].playerName.equals(parameter.get(0))) {
                        isMyTurn = playerStatus.EnemyTurn;
                        players[1].newTurn();
                    }
                } else if (fromServer.contains("$CHOISEBLOCKER(")) {
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    if (players[0].playerName.equals(parameter.get(0))) {
                        isMyTurn = playerStatus.IChoiseBlocker;
                        creatureWhoAttack = Integer.parseInt(parameter.get(1));
                        creatureWhoAttackTarget = Integer.parseInt(parameter.get(2));
                        //  main.repaint();
                    }
                } else if ((fromServer.contains("$CRYTARGET(")) || (fromServer.contains("$TAPTARGET("))) {
                    // CRYTARGET also for DeathratleTarget
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    int pl = Board.getPlayerNumByName(parameter.get(0));
                    int apl = (pl == 0) ? 1 : 0;
                    isMyTurn = playerStatus.MyTurn;
                    Creature cr;
                    boolean death = false;
                    if (parameter.get(1).equals("-1")) {
                        //died creature ability.
                        death = true;
                        cr = Card.ActivatedAbility.creature;
                        Card.ActivatedAbility.creature = null;
                    } else {
                        cr = Board.creature.get(pl).get(Integer.parseInt(parameter.get(1)));
                    }
                    if (parameter.get(2).equals("1")) {
                        if (parameter.get(3).equals("-1")) {
                            if (fromServer.contains("$CRYTARGET("))
                                if (death) cr.deathratle(null, players[apl]);
                                else cr.battlecryTarget(null, players[apl]);
                            else
                                cr.tapTargetAbility(null, players[apl]);
                        } else {
                            if (fromServer.contains("$CRYTARGET("))
                                if (death)
                                    cr.deathratle(Board.creature.get(apl).get(Integer.parseInt(parameter.get(3))), null);
                                else
                                    cr.battlecryTarget(Board.creature.get(apl).get(Integer.parseInt(parameter.get(3))), null);
                            else
                                cr.tapTargetAbility(Board.creature.get(apl).get(Integer.parseInt(parameter.get(3))), null);
                        }
                    } else {
                        if (parameter.get(3).equals("-1")) {
                            if (fromServer.contains("$CRYTARGET("))
                                if (death) cr.deathratle(null, players[pl]);
                                else cr.battlecryTarget(null, players[pl]);
                            else
                                cr.tapTargetAbility(null, players[pl]);
                        } else {
                            if (fromServer.contains("$CRYTARGET("))
                                if (death)
                                    cr.deathratle(Board.creature.get(pl).get(Integer.parseInt(parameter.get(3))), null);
                                else
                                    cr.battlecryTarget(Board.creature.get(pl).get(Integer.parseInt(parameter.get(3))), null);
                            else
                                cr.tapTargetAbility(Board.creature.get(pl).get(Integer.parseInt(parameter.get(3))), null);
                        }
                    }
                } else if (fromServer.contains("$EQUIPTARGET(")) {
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    int pl = Board.getPlayerNumByName(parameter.get(0));
                    int apl = (pl == 0) ? 1 : 0;
                    isMyTurn = playerStatus.MyTurn;
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
                    if (pl == 0) isMyTurn = playerStatus.MyTurn;
                    else isMyTurn = playerStatus.EnemyTurn;
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
                    if (pl == 0) isMyTurn = playerStatus.MyTurn;
                    else isMyTurn = playerStatus.EnemyTurn;
                    players[pl].isTapped = true;
                    players[pl].untappedCoin -= Integer.parseInt(parameter.get(1));
                    players[pl].abilityNoTarget();
                } else if (fromServer.contains("$BLOCKER(")) {
                    ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
                    int pl = Board.getPlayerNumByName(parameter.get(0));
                    int apl = (pl == 0) ? 1 : 0;
                    if (pl == 0)
                        isMyTurn = playerStatus.EnemyTurn;
                    else
                        isMyTurn = playerStatus.MyTurn;

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
                }
                main.repaint();
            }
        }
    }

    public static void printToView(int type, String txt) {
        if (txt.contains("Ход номер ")) Main.gameLog.setText("<html>");

        if (type == 0) {
            Main.gameLog.setText(Main.gameLog.getText() + txt + "<br>");
        } else if (type == 1) {
            messageToShow = new MessageToShow(txt, 1500);
        }
    }

    private static class MyListener extends MouseInputAdapter {
        enum Compo {Deck, CardInHand, CreatureInMyPlay, Board, EnemyHero, PlayerHero, EnemyUnitInPlay, ChoiseX, SearchX, Weapon, Menu, EndTurnButton}

        Compo onWhat;
        int num;

        MyListener(Compo _compo, int _code) {
            onWhat = _compo;
            num = _code;
        }

        public void mouseClicked(MouseEvent e) {
            if (onWhat == Compo.Deck) {
                //    System.out.println("$DRAWCARD(" + players[0].playerName + ")");
                //    Client.writeLine("$DRAWCARD(" + players[0].playerName + ")");
            } else if (onWhat == Compo.Menu) {
                System.out.println("$DISCONNECT");
                Client.writeLine("$DISCONNECT");
                writerToLog.close();
                System.exit(0);
            } else if ((onWhat == Compo.CardInHand) && (isMyTurn == playerStatus.MuliganPhase)) {
                if (wantToMulligan[num]) wantToMulligan[num] = false;
                else wantToMulligan[num] = true;
            } else if ((onWhat == Compo.EndTurnButton) && (isMyTurn == playerStatus.MyTurn)) {
                System.out.println("$ENDTURN(" + players[0].playerName + ")");
                Client.writeLine("$ENDTURN(" + players[0].playerName + ")");
                main.repaint();
            } else if ((onWhat == Compo.PlayerHero) && (isMyTurn == playerStatus.MyTurn)) {
                //My hero ability
                if (!players[0].isTapped) {
                    if (players[0].name.equals("Тиша")) {
                        if ((Board.creature.get(0).size() > 0) || (Board.creature.get(1).size() > 0)) {
                            if (players[0].untappedCoin >= 2) {
                                //replace
                                //players[0].isTapped = true;
                                isMyTurn = playerStatus.choiseTarget;
                                //change
                                Card.ActivatedAbility.targetType = players[0].targetType;
                                Card.ActivatedAbility.heroAbility = true;
                                Card.ActivatedAbility.heroAbilityCost = 2;
                                Card.ActivatedAbility.creature = null;
                                main.repaint();
                            } else {
                                printToView(0, "Недостаточно монет.");
                            }
                        } else {
                            printToView(0, "Нет подходящих целей.");
                        }
                    }
                    if ((players[0].name.equals("Тарна")) || (players[0].name.equals("Бьорнбон"))) {
                        int cost = MyFunction.getNumericAfterText(players[0].text, "ТАП:");
                        System.out.println("hero ability cost = " + cost);
                        if (players[0].untappedCoin >= cost) {
                            System.out.println("$HERONOTARGET(" + players[0].playerName + "," + cost + ")");
                            Client.writeLine("$HERONOTARGET(" + players[0].playerName + "," + cost + ")");
                            main.repaint();
                        } else {
                            printToView(0, "Недостаточно монет.");
                        }
                    }
                } else {
                    printToView(0, "Повернутый герой не может действовать.");
                }
            } else if ((onWhat == Compo.EndTurnButton) && (isMyTurn == playerStatus.MuliganPhase)) {
                //TODO when remake server
                //You know nothing, Server!!!
                //I must remake server. It(not client) must hold deck, card and other.
                System.out.println("$MULLIGANEND(" + players[0].playerName + "," + boolToInt(wantToMulligan[0]) + "," + boolToInt(wantToMulligan[1]) + "," + boolToInt(wantToMulligan[2]) + "," + boolToInt(wantToMulligan[3]) + ")");
                Client.writeLine("$MULLIGANEND(" + players[0].playerName + "," + boolToInt(wantToMulligan[0]) + "," + boolToInt(wantToMulligan[1]) + "," + boolToInt(wantToMulligan[2]) + "," + boolToInt(wantToMulligan[3]) + ")");
                isMyTurn = playerStatus.waitingMulligan;
            } else if ((onWhat == Compo.PlayerHero) && (isMyTurn == playerStatus.choiseTarget) && (!Card.ActivatedAbility.heroAbility)) {
                //Battlecry or TAPT on my hero
                if (MyFunction.canTarget(MyFunction.Target.myPlayer, Card.ActivatedAbility.targetType) || MyFunction.canTarget(MyFunction.Target.myPlayer, Card.ActivatedAbility.tapTargetType)) {
                    int nc = Board.creature.get(0).indexOf(Card.ActivatedAbility.creature);
                    if (Card.ActivatedAbility.creatureTap) {
                        System.out.println("$TAPTARGET(" + players[0].playerName + "," + nc + ",0,-1)");
                        Client.writeLine("$TAPTARGET(" + players[0].playerName + "," + nc + ",0,-1)");
                    } else {
                        System.out.println("$CRYTARGET(" + players[0].playerName + "," + nc + ",0,-1)");
                        Client.writeLine("$CRYTARGET(" + players[0].playerName + "," + nc + ",0,-1)");
                    }
                    isMyTurn = playerStatus.MyTurn;
                    // Card.ActivatedAbility.creature = null;//Not safety. Do check.
                    Card.ActivatedAbility.creatureTap = false;
                } else {
                    printToView(0, "Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.EnemyHero) && (isMyTurn == playerStatus.choiseTarget) && (!Card.ActivatedAbility.heroAbility)) {
                //Battlecry or TAPT on enemy hero
                if (MyFunction.canTarget(MyFunction.Target.enemyPlayer, Card.ActivatedAbility.targetType) || MyFunction.canTarget(MyFunction.Target.enemyPlayer, Card.ActivatedAbility.tapTargetType)) {
                    if ((players[1].bbshield) && (Card.ActivatedAbility.creature.text.contains("Выстрел"))) {
                        players[1].bbshield = false;
                    }
                    int nc = Board.creature.get(0).indexOf(Card.ActivatedAbility.creature);
                    if (Card.ActivatedAbility.creatureTap) {
                        System.out.println("$TAPTARGET(" + players[0].playerName + "," + nc + ",1,-1)");
                        Client.writeLine("$TAPTARGET(" + players[0].playerName + "," + nc + ",1,-1)");
                    } else {
                        System.out.println("$CRYTARGET(" + players[0].playerName + "," + nc + ",1,-1)");
                        Client.writeLine("$CRYTARGET(" + players[0].playerName + "," + nc + ",1,-1)");
                    }
                    isMyTurn = playerStatus.MyTurn;
                    // Card.ActivatedAbility.creature = null;//Not safety. Do check.
                    Card.ActivatedAbility.creatureTap = false;
                } else {
                    printToView(0, "Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.CreatureInMyPlay) && (isMyTurn == playerStatus.choiseTarget) && (!Card.ActivatedAbility.heroAbility) && (!Card.ActivatedAbility.weaponAbility)) {
                //Battlecry or TAPT on my unit
                if (MyFunction.canTarget(MyFunction.Target.myCreature, Card.ActivatedAbility.targetType) || MyFunction.canTarget(MyFunction.Target.myCreature, Card.ActivatedAbility.tapTargetType)) {
                    int nc = Board.creature.get(0).indexOf(Card.ActivatedAbility.creature);
                    if (Card.ActivatedAbility.targetType == 10 && nc == num) {
                        printToView(0, "Существо не может целить само себя.");
                    } else {
                        if (Card.ActivatedAbility.creatureTap) {
                            System.out.println("$TAPTARGET(" + players[0].playerName + "," + nc + ",0," + num + ")");
                            Client.writeLine("$TAPTARGET(" + players[0].playerName + "," + nc + ",0," + num + ")");
                        } else {
                            System.out.println("$CRYTARGET(" + players[0].playerName + "," + nc + ",0," + num + ")");
                            Client.writeLine("$CRYTARGET(" + players[0].playerName + "," + nc + ",0," + num + ")");
                        }
                        isMyTurn = playerStatus.MyTurn;
                        //  Card.ActivatedAbility.creature = null;//Not safety. Do check.
                        Card.ActivatedAbility.creatureTap = false;
                    }
                } else {
                    printToView(0, "Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.EnemyUnitInPlay) && (isMyTurn == playerStatus.choiseTarget) && (!Card.ActivatedAbility.heroAbility)) {
                //Battlecry or TAPT on enemy unit
                if (MyFunction.canTarget(MyFunction.Target.enemyCreature, Card.ActivatedAbility.targetType) || MyFunction.canTarget(MyFunction.Target.enemyCreature, Card.ActivatedAbility.tapTargetType))
                    {
                    //Bjornbon check attack or not this cry or tap.
                    if ((players[1].bbshield) && (Card.ActivatedAbility.creature.text.contains("Выстрел"))) {
                        printToView(0, "Целью первой атаки должен быть Бьорнбон.");
                    } else {
                        int nc = Board.creature.get(0).indexOf(Card.ActivatedAbility.creature);

                        //Check correct target or it not able?
                        if (Card.ActivatedAbility.creatureTap) {
                            System.out.println("$TAPTARGET(" + players[0].playerName + "," + nc + ",1," + num + ")");
                            Client.writeLine("$TAPTARGET(" + players[0].playerName + "," + nc + ",1," + num + ")");
                        } else {
                            System.out.println("$CRYTARGET(" + players[0].playerName + "," + nc + ",1," + num + ")");
                            Client.writeLine("$CRYTARGET(" + players[0].playerName + "," + nc + ",1," + num + ")");
                        }
                        isMyTurn = playerStatus.MyTurn;
                        //  Card.ActivatedAbility.creature = null;//Not safety. Do check.
                        Card.ActivatedAbility.creatureTap = false;
                    }
                } else {
                    printToView(0, "Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.EnemyUnitInPlay) && (isMyTurn == playerStatus.choiseTarget) && (Card.ActivatedAbility.heroAbility)) {
                //Hero ability on enemy unit
                if ((players[0].targetType == 1) || (players[0].targetType == 3)) {
                    System.out.println("$HEROTARGET(" + players[0].playerName + ",1," + num + "," + Card.ActivatedAbility.heroAbilityCost + ")");
                    Client.writeLine("$HEROTARGET(" + players[0].playerName + ",1," + num + "," + Card.ActivatedAbility.heroAbilityCost + ")");
                    isMyTurn = playerStatus.MyTurn;
                    Card.ActivatedAbility.heroAbility = false;
                } else {
                    printToView(0, "Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.CreatureInMyPlay) && (isMyTurn == playerStatus.choiseTarget) && (Card.ActivatedAbility.heroAbility)) {
                //Hero ability on my unit
                if ((players[0].targetType == 1) || (players[0].targetType == 3)) {
                    System.out.println("$HEROTARGET(" + players[0].playerName + ",0," + num + "," + Card.ActivatedAbility.heroAbilityCost + ")");
                    Client.writeLine("$HEROTARGET(" + players[0].playerName + ",0," + num + "," + Card.ActivatedAbility.heroAbilityCost + ")");
                    isMyTurn = playerStatus.MyTurn;
                    Card.ActivatedAbility.heroAbility = false;
                } else {
                    printToView(0, "Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.CreatureInMyPlay) && (isMyTurn == playerStatus.choiseTarget) && (Card.ActivatedAbility.weaponAbility)) {
                //Weapon ability on my unit
                if ((players[0].equpiment[2].tapTargetType == 1) || (players[0].equpiment[2].tapTargetType == 3)) {
                    System.out.println("$EQUIPTARGET(" + players[0].playerName + ",2,0," + num + ")");
                    Client.writeLine("$EQUIPTARGET(" + players[0].playerName + ",2,0," + num + ")");
                    isMyTurn = playerStatus.MyTurn;
                    Card.ActivatedAbility.weaponAbility = false;
                } else {
                    printToView(0, "Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.CreatureInMyPlay) && (isMyTurn == playerStatus.MyTurn) && (Board.creature.get(0).get(num).text.contains("ТАПТ:"))) {
                //TAP with target ability - first step
                if (!Board.creature.get(0).get(num).getIsSummonedJust()) {
                    if (!Board.creature.get(0).get(num).isTapped) {
                        System.out.println("tapt ability.");
                        isMyTurn = playerStatus.choiseTarget;
                        Card.ActivatedAbility.creature = Board.creature.get(0).get(num);
                        Card.ActivatedAbility.targetType = Board.creature.get(0).get(num).targetType;
                        Card.ActivatedAbility.tapTargetType = Board.creature.get(0).get(num).tapTargetType;
                        Card.ActivatedAbility.creatureTap = true;
                        main.repaint();
                    } else {
                        printToView(0, "Повернутое существо не может это сделать.");
                    }
                } else {
                    printToView(0, "Это существо недавно вошло в игру.");
                }
            } else if ((onWhat == Compo.Weapon) && (isMyTurn == playerStatus.MyTurn) && (players[0].equpiment[2].text.contains("ТАПТ:"))) {
                //TAP weapon with target ability - first step
                if (!players[0].equpiment[2].isTapped) {
                    System.out.println("tapt weapon ability.");
                    if ((players[0].equpiment[2].tapTargetType == 1) && (Board.creature.get(0).isEmpty()) && (Board.creature.get(1).isEmpty())) {
                        printToView(0, "Нет подходящей цели.");
                    } else {
                        isMyTurn = playerStatus.choiseTarget;
                        Card.ActivatedAbility.targetType = players[0].equpiment[2].targetType;
                        Card.ActivatedAbility.tapTargetType = players[0].equpiment[2].tapTargetType;
                        Card.ActivatedAbility.weaponAbility = true;
                        Card.ActivatedAbility.creature = null;
                        main.repaint();
                    }
                } else {
                    printToView(0, "Повернутое оружие не может это сделать.");
                }
            } else if ((onWhat == Compo.CreatureInMyPlay) && (isMyTurn == playerStatus.MyTurn) && (Board.creature.get(0).
                    get(num).text.contains("ТАП:"))) {
                //TAP with target ability - first step
                if (!Board.creature.get(0).get(num).getIsSummonedJust()) {
                    if (!Board.creature.get(0).get(num).isTapped) {
                        System.out.println("tap ability.");
                        Board.creature.get(0).get(num).tapNoTargetAbility();
                        main.repaint();
                    } else {
                        printToView(0, "Повернутое существо не может это сделать.");
                    }
                } else {
                    printToView(0, "Это существо недавно вошло в игру.");
                }
            } else if ((onWhat == Compo.EndTurnButton) && (isMyTurn == playerStatus.IChoiseBlocker)) {
                System.out.println("$BLOCKER(" + players[0].playerName + "," + creatureWhoAttack + "," + creatureWhoAttackTarget + "," + "-1,0)");
                Client.writeLine("$BLOCKER(" + players[0].playerName + "," + creatureWhoAttack + "," + creatureWhoAttackTarget + "," + "-1,0)");
            } else if ((onWhat == Compo.CreatureInMyPlay) && (isMyTurn == playerStatus.IChoiseBlocker)) {
                //TODO can't block
                if ((Board.creature.get(0).get(num).isTapped) || (Board.creature.get(0).get(num).blockThisTurn)) {
                    printToView(0, "Повернутые/уже блокировавшие существа не могут блокировать.");
                } else if (creatureWhoAttackTarget == num) {
                    System.out.println("$BLOCKER(" + players[0].playerName + "," + creatureWhoAttack + "," + creatureWhoAttackTarget + "," + num + ",0)");
                    Client.writeLine("$BLOCKER(" + players[0].playerName + "," + creatureWhoAttack + "," + creatureWhoAttackTarget + "," + num + ",0)");
                } else {
                    System.out.println("$BLOCKER(" + players[0].playerName + "," + creatureWhoAttack + "," + creatureWhoAttackTarget + "," + num + ",1)");
                    Client.writeLine("$BLOCKER(" + players[0].playerName + "," + creatureWhoAttack + "," + creatureWhoAttackTarget + "," + num + ",1)");
                }
            } else if ((onWhat == Compo.ChoiseX) && (isMyTurn == playerStatus.choiseX)) {
                isMyTurn = playerStatus.MyTurn;
                main.repaint();
                releaseCardWithX(num);
            } else if ((onWhat == Compo.SearchX) && (isMyTurn == playerStatus.searchX)) {
                isMyTurn = playerStatus.MyTurn;
                main.repaint();
                System.out.println("$FOUND(" + players[0].playerName + "," + founded.get(num).name + ")");
                Client.writeLine("$FOUND(" + players[0].playerName + "," + founded.get(num).name + ")");
            }

        }

        public void mousePressed(MouseEvent e) {
            // you may not need this method
        }

        public void mouseEntered(MouseEvent event) {
            whereMyMouse = onWhat.toString();
            whereMyMouseNum = num;

            //Hilight destination
            if (cardMem != null) {
                //
            }
            if (onWhat == Compo.CardInHand) {
                hilightMyCard = num;
                main.repaint();
            }
            if (onWhat == Compo.CreatureInMyPlay) {
                hilightMyCreature = num;
                main.repaint();
            }
            // printToView(0,whereMyMouse);
        }

        public void mouseExited(MouseEvent event) {
            whereMyMouse = "";
            whereMyMouseNum = 0;
            hilightMyCreature = -1;
            hilightMyCard = -1;
            main.repaint();
        }

        public void mouseReleased(MouseEvent e) {
            if (isMyTurn == playerStatus.MyTurn) {
                if ((whereMyMouse == Compo.Board.toString()) && (cardMem != null)) {
                    //put creature on board
                    if ((cardMem.targetType == 0) || (cardMem.type == 2)) {
                        if (cardMem.text.contains("Доплатите Х *")) {
                            //TODO If X==0
                            isMyTurn = playerStatus.choiseX;
                            //choiseXnum = num;
                            choiceXtext = "$PLAYWITHX(" + players[0].playerName + "," + num + ",-1,-1";
                            main.repaint();
                        } else {
                            System.out.println("$PLAYCARD(" + players[0].playerName + "," + num + ",-1,-1)");
                            Client.writeLine("$PLAYCARD(" + players[0].playerName + "," + num + ",-1,-1)");
                        }
                    } else {
                        printToView(0, "Заклинание требует цели.");
                    }
                } else if ((whereMyMouse == Compo.EnemyHero.toString()) && (creatureMem != null)) {
                    //enemy hero attack by creature
                    if ((creatureMem.isTapped) || (creatureMem.attackThisTurn) || (creatureMem.effects.cantAttackOrBlock > 0)) {
                        printToView(0, "Повернутое/атаковавшее/т.д. существо не может атаковать.");
                    } else {
                        if (creatureMem.getIsSummonedJust()) {
                            printToView(0, "Это существо вошло в игру на этом ходу.");
                        } else {
                            players[1].bbshield = false;
                            System.out.println("$ATTACKPLAYER(" + players[0].playerName + "," + num + ")");
                            Client.writeLine("$ATTACKPLAYER(" + players[0].playerName + "," + num + ")");
                        }
                    }
                } else if ((whereMyMouse == Compo.EnemyUnitInPlay.toString()) && (creatureMem != null)) {
                    //enemy creature attack by player creature
                    if (players[1].bbshield) {
                        printToView(0, "Первая атака должна быть в Бьорнбона.");
                    } else {
                        if ((creatureMem.isTapped) || (creatureMem.attackThisTurn) || (creatureMem.effects.cantAttackOrBlock > 0)) {
                            printToView(0, "Повернутое/атаковавшее/т.д. существо не может атаковать.");
                        } else {
                            if (creatureMem.getIsSummonedJust()) {
                                printToView(0, "Это существо вошло в игру на этом ходу.");
                            } else {
                                System.out.println("$ATTACKCREATURE(" + players[0].playerName + "," + num + "," + whereMyMouseNum + ")");
                                Client.writeLine("$ATTACKCREATURE(" + players[0].playerName + "," + num + "," + whereMyMouseNum + ")");
                            }
                        }
                    }
                } else if ((whereMyMouse == Compo.EnemyHero.toString()) && (cardMem != null)) {
                    //enemy hero attack by spell from hand
                    if (cardMem.targetType == 2) {
                        System.out.println("$PLAYCARD(" + players[0].playerName + "," + num + ",-1," + players[1].playerName + ")");
                        Client.writeLine("$PLAYCARD(" + players[0].playerName + "," + num + ",-1," + players[1].playerName + ")");
                    } else {
                        printToView(0, "Некорректная цель для данного заклинания, выберите существо.");
                    }
                } else if ((whereMyMouse == Compo.CreatureInMyPlay.toString()) && (cardMem != null)) {
                    //spell from hand to my creature in play
                    if (Board.creature.get(0).get(whereMyMouseNum).text.contains("Защита от заклинаний.")) {
                        printToView(0, "У цели защита от заклинаний.");
                    } else {
                        if ((cardMem.targetType == 1) || (cardMem.targetType == 3)) {
                            if (cardMem.text.contains("Доплатите Х *")) {
                                //TODO If X==0
                                isMyTurn = playerStatus.choiseX;
                                //choiseXnum = num;
                                choiceXtext = "$PLAYWITHX(" + players[0].playerName + "," + num + "," + whereMyMouseNum + "," + players[0].playerName;
                                main.repaint();
                            } else {
                                System.out.println("$PLAYCARD(" + players[0].playerName + "," + num + "," + whereMyMouseNum + "," + players[0].playerName + ")");
                                Client.writeLine("$PLAYCARD(" + players[0].playerName + "," + num + "," + whereMyMouseNum + "," + players[0].playerName + ")");
                            }
                        } else {
                            printToView(0, "Некорректная цель для данного заклинания, выберите героя.");
                        }
                    }
                } else if ((whereMyMouse == Compo.EnemyUnitInPlay.toString()) && (cardMem != null)) {
                    //spell from hand to enemy creature in play
                    if (Board.creature.get(1).get(whereMyMouseNum).text.contains("Защита от заклинаний.")) {
                        printToView(0, "У цели защита от заклинаний.");
                    } else {
                        if ((cardMem.targetType == 1) || (cardMem.targetType == 3)) {
                            if (cardMem.text.contains("Доплатите Х *")) {
                                //TODO If X==0
                                isMyTurn = playerStatus.choiseX;
                                //choiseXnum = num;
                                choiceXtext = "$PLAYWITHX(" + players[0].playerName + "," + num + "," + whereMyMouseNum + "," + players[1].playerName;
                                main.repaint();
                            } else {
                                System.out.println("$PLAYCARD(" + players[0].playerName + "," + num + "," + whereMyMouseNum + "," + players[1].playerName + ")");
                                Client.writeLine("$PLAYCARD(" + players[0].playerName + "," + num + "," + whereMyMouseNum + "," + players[1].playerName + ")");
                            }
                        } else {
                            printToView(0, "Некорректная цель для данного заклинания, выберите героя.");
                        }
                    }
                }
            } else {
                if (isMyTurn == playerStatus.EnemyTurn) {
                    printToView(0, "Сейчас идет не ваш ход.");
                }
                main.repaint();
            }
            cardMem = null;
            creatureMem = null;
        }

        public void mouseDragged(MouseEvent e) {
            if (onWhat == Compo.CardInHand) {//Creature in hand
                cardMem = players[0].cardInHand.get(num);
            } else if (onWhat == Compo.CreatureInMyPlay) {//Creature in play
                creatureMem = Board.creature.get(0).get(num);
            }


        }
    }

    private static void onRepaint(Graphics g) throws IOException {
        //setLocation and setSize to other block?
        // System.out.println("onRepaint " + repainted);
        repainted++;
        bigCardW = (int) (main.getWidth() * CARD_SIZE_FROM_SCREEN * 1.9);
        bigCardH = (bigCardW * 400 / 283);
        heroW = (int) (main.getWidth() * CARD_SIZE_FROM_SCREEN);
        heroH = (heroW * 400 / 283);
        smallCardW = (int) (heroW * 0.7);
        smallCardH = (int) (heroH * 0.7);
        cardX = B0RDER_LEFT + B0RDER_BETWEEN * 3 + smallCardW * 3;
        BufferedImage im;
        int numCardInHand = 0;
        g.drawImage(menuImage, main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() / 2 - heroW * 75 / 283 - heroW * 150 / 283 - B0RDER_BETWEEN, heroW, heroW * 149 / 283, null);
        menuClick.setLocation(main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() / 2 - heroW * 75 / 283 - heroW * 150 / 283 - B0RDER_BETWEEN);
        menuClick.setSize(heroW, heroW * 149 / 283);
        //Game log
        gameLog.setLocation(B0RDER_LEFT, B0RDER_TOP + smallCardH + B0RDER_BETWEEN);
        gameLog.setSize(cardX - B0RDER_BETWEEN - B0RDER_LEFT, main.getHeight() - B0RDER_BOTTOM - B0RDER_BETWEEN * 2 - B0RDER_TOP - smallCardH * 2);
        //Background
        g.drawImage(background, 0, 0, main.getWidth(), main.getHeight(), null);
        //Battleground
        battlegroundClick.setLocation(cardX, B0RDER_TOP + B0RDER_BETWEEN + smallCardH);
        battlegroundClick.setSize(main.getWidth() - B0RDER_RIGHT - cardX - heroW - B0RDER_BETWEEN, main.getHeight() - B0RDER_BOTTOM - B0RDER_BETWEEN * 2 - B0RDER_TOP - smallCardH * 2);
        g.drawRect(battlegroundClick.getX(), battlegroundClick.getY(), battlegroundClick.getWidth(), battlegroundClick.getHeight());//TODO Image of battleground
        //End turn button
        if (isMyTurn == playerStatus.MyTurn) {
            endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Endturn.png"));
        } else if (isMyTurn == playerStatus.IChoiseBlocker) {
            endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Blockturn.png"));
        } else if (isMyTurn == playerStatus.EnemyChoiseBlocker) {
            endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Blockenemyturn.png"));
        } else if (isMyTurn == playerStatus.MuliganPhase) {
            endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Mulliganturn.png"));
        } else if (isMyTurn == playerStatus.EnemyTurn) {
            endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Enemyturn.png"));
        } else if (isMyTurn == playerStatus.waitingMulligan) {
            endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Waitmulliganturn.png"));
        } else if (isMyTurn == playerStatus.waitingForConnection) {
            endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Connectionturn.png"));
        } else if (isMyTurn == playerStatus.choiseTarget) {
            endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Waittarget.png"));
        } else if (isMyTurn == playerStatus.waitOtherPlayer) {
            endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Waitotherconnectionturn.png"));
        }
        g.drawImage(endTurnImage, main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() / 2 - heroW * 75 / 283, heroW, heroW * 149 / 283, null);
        endTurnClick.setLocation(main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() / 2 - heroW * 75 / 283);
        endTurnClick.setSize(heroW, heroW * 149 / 283);
        g.drawImage(menuImage, main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() / 2 - heroW * 75 / 283 - heroW * 150 / 283 - B0RDER_BETWEEN, heroW, heroW * 149 / 283, null);
        menuClick.setLocation(main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() / 2 - heroW * 75 / 283 - heroW * 150 / 283 - B0RDER_BETWEEN);
        menuClick.setSize(heroW, heroW * 149 / 283);
        //Heroes
        if (players[0].isTapped) {
            g.drawImage(MyFunction.tapImage(heroImage), main.getWidth() - heroH - B0RDER_RIGHT, main.getHeight() - heroH - B0RDER_BOTTOM, heroH, heroW, null);
        } else
            g.drawImage(heroImage, main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() - heroH - B0RDER_BOTTOM, heroW, heroH, null);
        if (players[1].isTapped) {
            g.drawImage(MyFunction.tapImage(enemyImage), main.getWidth() - heroH - B0RDER_RIGHT, B0RDER_TOP, heroH, heroW, null);
        } else g.drawImage(enemyImage, main.getWidth() - heroW - B0RDER_RIGHT, B0RDER_TOP, heroW, heroH, null);
        //Heroes effects
        if (players[0].bbshield) {
            im = ImageIO.read(Main.class.getResourceAsStream("icons/effects/bbshield.png"));
            g.drawImage(im, main.getWidth() - heroW - B0RDER_RIGHT + heroW / 2 - heroH / 10 - heroH / 5, main.getHeight() - heroH - B0RDER_BOTTOM + heroH / 2 - heroH / 10 - heroH / 5, heroH / 5, heroH / 5, null);
        }
        if (players[1].bbshield) {
            im = ImageIO.read(Main.class.getResourceAsStream("icons/effects/bbshield.png"));
            g.drawImage(im, main.getWidth() - heroW - B0RDER_RIGHT + heroW / 2 - heroH / 10 - heroH / 5, B0RDER_TOP + heroH / 2 - heroH / 10 - heroH / 5, heroH / 5, heroH / 5, null);
        }
        enemyHeroClick.setLocation(main.getWidth() - heroW - B0RDER_RIGHT, B0RDER_TOP);
        enemyHeroClick.setSize(heroW, heroH);
        playerHeroClick.setLocation(main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() - heroH - B0RDER_BOTTOM);
        playerHeroClick.setSize(heroW, heroH);
        //Heroes equpiment
        drawPlayerEquipment(g, 0);
        drawPlayerEquipment(g, 1);
        //TODO Draw N not 4
        drawPlayerDamage(g, 0);
        drawPlayerDamage(g, 1);
        //Decks
        g.drawImage(heroDeckImage, B0RDER_LEFT, main.getHeight() - smallCardH - B0RDER_BOTTOM, smallCardW, smallCardH, null);
        deckClick.setLocation(B0RDER_LEFT, main.getHeight() - smallCardH - B0RDER_BOTTOM);
        deckClick.setSize(smallCardW, smallCardH);

        g.drawImage(heroDeckImage, B0RDER_LEFT, B0RDER_TOP, smallCardW, smallCardH, null);
        //Hero graveyard
        playerGraveyardClick.setLocation(deckClick.getX() + deckClick.getWidth() + B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM);
        playerGraveyardClick.setSize(smallCardW, smallCardH);
        if (players[0].graveyard.size() == 0) {
            g.drawImage(heroGraveyardImage, deckClick.getX() + deckClick.getWidth() + B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM, smallCardW, smallCardH, null);
        } else {
            im = ImageIO.read(Main.class.getResourceAsStream("cards/" + players[0].graveyard.get(players[0].graveyard.size() - 1).image));
            g.drawImage(im, deckClick.getX() + deckClick.getWidth() + B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM, smallCardW, smallCardH, null);
        }
        //Enemy graveyard
        enemyGraveyardClick.setLocation(deckClick.getX() + deckClick.getWidth() + B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM);
        enemyGraveyardClick.setSize(smallCardW, smallCardH);
        if (players[1].graveyard.size() == 0) {
            g.drawImage(heroGraveyardImage, deckClick.getX() + deckClick.getWidth() + B0RDER_BETWEEN, B0RDER_TOP, smallCardW, smallCardH, null);
        } else {
            im = ImageIO.read(Main.class.getResourceAsStream("cards/" + players[1].graveyard.get(players[1].graveyard.size() - 1).image));
            g.drawImage(im, deckClick.getX() + deckClick.getWidth() + B0RDER_BETWEEN, B0RDER_TOP, smallCardW, smallCardH, null);
        }
        //Hero&enemy coin
        g.drawImage(heroCoinImage, playerGraveyardClick.getX() + playerGraveyardClick.getWidth() + B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM, smallCardW, smallCardH, null);
        playerCoinLabel.setLocation(playerGraveyardClick.getX() + playerGraveyardClick.getWidth() + B0RDER_BETWEEN + (int) (smallCardW * 0.5), main.getHeight() - smallCardH - B0RDER_BOTTOM + (int) (smallCardH * 0.8));
        playerCoinLabel.setText(players[0].untappedCoin + "/" + players[0].totalCoin);
        g.drawImage(heroCoinImage, playerGraveyardClick.getX() + playerGraveyardClick.getWidth() + B0RDER_BETWEEN, B0RDER_TOP, smallCardW, smallCardH, null);
        enemyCoinLabel.setLocation(playerGraveyardClick.getX() + playerGraveyardClick.getWidth() + B0RDER_BETWEEN + (int) (smallCardW * 0.5), B0RDER_TOP + (int) (smallCardH * 0.8));
        enemyCoinLabel.setText(players[1].untappedCoin + "/" + players[1].totalCoin);
        //Creatures
        drawPlayerCreature(g, 0);
        drawPlayerCreature(g, 1);
        //Hero card in hand
        if (!players[0].cardInHand.isEmpty()) {
            //for (int i = 0; i < players[0].cardInHand.size(); i++)
            for (int i = players[0].cardInHand.size() - 1; i >= 0; i--)
            //for (Card card : player.cardInHand)   // I don't know why, but it create ConcurrentModificationException
            {
                Card card = players[0].cardInHand.get(i);
                if (card.image != null) {
                    try {
                        im = ImageIO.read(Main.class.getResourceAsStream("cards/" + card.image));
                        if (isMyTurn == playerStatus.MuliganPhase) {
                            int tmp = (battlegroundClick.getWidth() - bigCardW * 4) / 5;
                            g.drawImage(im, cardX + (numCardInHand * bigCardW) + ((numCardInHand + 1) * tmp), main.getHeight() / 2 - bigCardH / 2, bigCardW, bigCardH, null);
                            cardClick[i].setLocation(cardX + (numCardInHand * bigCardW) + ((numCardInHand + 1) * tmp), main.getHeight() / 2 - bigCardH / 2);
                            cardClick[i].setSize(bigCardW, bigCardH);
                            if (wantToMulligan[i]) {
                                g.drawImage(redcrossImage, cardX + (numCardInHand * bigCardW) + ((numCardInHand + 1) * tmp), main.getHeight() / 2 - bigCardH / 2, bigCardW, bigCardH, null);
                            }
                        } else {
                            if (hilightMyCard == i) {
                                g.drawImage(im, cardX + smallCardW + (int) (numCardInHand * smallCardW * 0.75), main.getHeight() - bigCardH - B0RDER_BOTTOM, bigCardW, bigCardH, null);
                                cardClick[i].setLocation(cardX + smallCardW + (int) (numCardInHand * smallCardW * 0.75), main.getHeight() - bigCardH - B0RDER_BOTTOM);
                                cardClick[i].setSize(bigCardW, bigCardH);
                            } else {
                                if ((hilightMyCard > i) && (hilightMyCard != -1)) {
                                    g.drawImage(im, cardX + smallCardW + (int) (numCardInHand * smallCardW * 0.75) + bigCardW - smallCardW, main.getHeight() - smallCardH - B0RDER_BOTTOM, smallCardW, smallCardH, null);
                                    cardClick[i].setLocation(cardX + smallCardW + (int) (numCardInHand * smallCardW * 0.75) + bigCardW - smallCardW, main.getHeight() - smallCardH - B0RDER_BOTTOM);
                                    cardClick[i].setSize(smallCardW, smallCardH);
                                } else {
                                    g.drawImage(im, cardX + smallCardW + (int) (numCardInHand * smallCardW * 0.75), main.getHeight() - smallCardH - B0RDER_BOTTOM, smallCardW, smallCardH, null);
                                    cardClick[i].setLocation(cardX + smallCardW + (int) (numCardInHand * smallCardW * 0.75), main.getHeight() - smallCardH - B0RDER_BOTTOM);
                                    cardClick[i].setSize(smallCardW, smallCardH);
                                }
                            }
                        }
                        numCardInHand++;
                    } catch (IOException e) {
                        System.out.println("Can't load image " + card.image);
                    }
                }
            }
        }
        //Enemy cards
        im = ImageIO.read(Main.class.getResourceAsStream("icons/Deck.png"));//His card deck up
        if (!players[1].cardInHand.isEmpty()) {
            for (int i = 0; i < players[1].cardInHand.size(); i++) {
                g.drawImage(im, cardX + smallCardW + (int) (i * smallCardW * 0.5), B0RDER_TOP, smallCardW, smallCardH, null);
            }
        }

        //Choise X
        if (isMyTurn == playerStatus.choiseX) {
            //Work too slow!
            System.out.println("choiseX");
            for (int i = 0; i <= players[0].untappedCoin; i++) {
                g.drawImage(heroCoinImage, cardX + B0RDER_BETWEEN * i + smallCardW * i, main.getHeight() / 2 - smallCardH / 2, smallCardW, smallCardH, null);
                choiceXLabel[i].setLocation(cardX + B0RDER_BETWEEN * i + smallCardW * i, main.getHeight() / 2 - smallCardH / 2);
                choiceXLabel[i].setSize(smallCardW, smallCardH);
                choiceXLabel[i].setVisible(true);
            }
        }
        //Search in deck
        if (isMyTurn == playerStatus.searchX) {
            drawSearchInDeck(g);
        } else {
            for (int i = 0; i < 40; i++)
                searchXLabel[i].setVisible(false);
        }

        drawMessage(g);
    }

    private static void drawMessage(Graphics g) {
        Calendar cal = Calendar.getInstance();
        long delta = cal.getTimeInMillis() - messageToShow.whenAdd;
        //  System.out.println("Delta="+delta);
        if ((messageToShow.lenght > delta) || (messageToShow.whenAdd == 0) && (!messageToShow.message.equals(" "))) {
            if (messageToShow.whenAdd == 0)
                messageToShow.whenAdd = cal.getTimeInMillis();

            font = new Font("Georgia", Font.ITALIC, 50);
            g1 = (Graphics2D) g;
            g1.setFont(font);
            metrics = g1.getFontMetrics();
            int x = main.getWidth() / 2 - metrics.stringWidth(messageToShow.message) / 2;
            int y = main.getHeight() / 2;
            g1.setPaint(new Color(150, 150, 150));
            TextLayout textLayout = new TextLayout(messageToShow.message, font, g1.getFontRenderContext());
            textLayout.draw(g1, x + 3, y + 3);

            g1.setPaint(Color.yellow);
            textLayout.draw(g1, x, y);
            main.repaint();
        } else if (messageToShow.lenght < delta) message.setVisible(false);
    }

    private static void drawSearchInDeck(Graphics g) throws IOException {
        founded = new ArrayList<>(players[0].deck.cards);
        for (int i = founded.size() - 1; i >= 0; i--) {
            if ((choiceXcolor != founded.get(i).color) && (choiceXcolor != 0)) {
                founded.remove(founded.get(i));
                continue;//Without it may be deleted twice one card
            }
            if ((choiceXtype != founded.get(i).type) && (choiceXtype != 0)) {
                founded.remove(founded.get(i));
                continue;
            }
            if (((!choiceXcreatureType.equals(founded.get(i).creatureType))) && (!choiceXcreatureType.equals(""))) {
                founded.remove(founded.get(i));
                continue;
            }
            if ((choiceXcost < founded.get(i).cost) && (choiceXcost != 0)) {
                founded.remove(founded.get(i));
                continue;
            }
        }
        if (founded.size() == 0) {
            printToView(0, "В колоде ничего подходящего не найдено.");
            System.out.println("$FOUND(" + players[0].playerName + ",-1)");
            Client.writeLine("$FOUND(" + players[0].playerName + ",-1)");
            isMyTurn = playerStatus.MyTurn;
            choiceXcolor = 0;
            choiceXtype = 0;
            main.repaint();
        } else {
            //TODO If founded.size() small, draw bigger
            for (int i = 0; i < founded.size(); i++) {
                int ii = i % 10;
                int jj = i / 10;
                BufferedImage im_tmp = ImageIO.read(Main.class.getResourceAsStream("cards/" + founded.get(i).image));
                g.drawImage(im_tmp, cardX + B0RDER_BETWEEN * ii + smallCardW * ii, main.getHeight() / 2 - smallCardH / 2 + B0RDER_BETWEEN * jj + smallCardH * (jj - 1), smallCardW, smallCardH, null);
                searchXLabel[i].setLocation(cardX + B0RDER_BETWEEN * ii + smallCardW * ii, main.getHeight() / 2 - smallCardH / 2 + B0RDER_BETWEEN * jj + smallCardH * (jj - 1));
                searchXLabel[i].setSize(smallCardW, smallCardH);
                searchXLabel[i].setVisible(true);
            }
        }
    }

    private static void drawPlayerDamage(Graphics g, int p) throws IOException {
        BufferedImage im;
        int h;
        if (p == 0) h = playerHeroClick.getY() + (playerHeroClick.getHeight() / 2 - (heroH / 10));
        else h = enemyHeroClick.getY() + (playerHeroClick.getHeight() / 2 - (heroH / 10));
        if (players[p].damage != 0) {
            int j;
            for (j = 0; j < players[p].damage / 10; j++) {
                im = ImageIO.read(Main.class.getResourceAsStream("icons/damage/" + 10 + ".png"));
                g.drawImage(im, playerHeroClick.getX() + playerHeroClick.getWidth() / 2 - heroH / 10 + (j - 1) * heroH / 5, h, heroH / 5, heroH / 5, null);
            }
            int a = players[p].damage % 10;
            if (a != 0) {
                im = ImageIO.read(Main.class.getResourceAsStream("icons/damage/" + a + ".png"));
                g.drawImage(im, playerHeroClick.getX() + playerHeroClick.getWidth() / 2 - heroH / 10 + (j - 1) * heroH / 5, h, heroH / 5, heroH / 5, null);
            }
        }
    }

    private static void drawPlayerEquipment(Graphics g, int p) throws IOException {
        BufferedImage im;
        //TODO durability of armor
        int h;
        if (p == 0) h = main.getHeight() - smallCardH - B0RDER_BOTTOM;
        else h = B0RDER_TOP;

        //TODO draw event

        if (players[p].equpiment[0] == null) {
            g.drawImage(heroNoArmorImage, main.getWidth() - smallCardW - heroW - B0RDER_RIGHT, h, smallCardW, smallCardH, null);
        } else {
            im = ImageIO.read(Main.class.getResourceAsStream("cards/" + players[p].equpiment[0].image));
            g.drawImage(im, main.getWidth() - smallCardW - heroW - B0RDER_RIGHT, h, smallCardW, smallCardH, null);
        }
        //replace
        weaponClick.setLocation(main.getWidth() - smallCardW * 3 - heroW - B0RDER_RIGHT, main.getHeight() - smallCardH - B0RDER_BOTTOM);
        weaponClick.setSize(smallCardW, smallCardH);
        //
        if (players[p].equpiment[2] == null) {
            g.drawImage(heroNoWeaponImage, main.getWidth() - smallCardW * 3 - heroW - B0RDER_RIGHT, h, smallCardW, smallCardH, null);
        } else {
            im = ImageIO.read(Main.class.getResourceAsStream("cards/" + players[p].equpiment[2].image));
            if (players[p].equpiment[2].isTapped)
                g.drawImage(MyFunction.tapImage(im), main.getWidth() - smallCardW * 3 - heroW - B0RDER_RIGHT, h, smallCardH, smallCardW, null);
            else
                g.drawImage(im, main.getWidth() - smallCardW * 3 - heroW - B0RDER_RIGHT, h, smallCardW, smallCardH, null);
        }
        //amulet
        if (players[p].equpiment[1] == null) {
            g.drawImage(heroNoAmuletImage, main.getWidth() - smallCardW * 2 - heroW - B0RDER_RIGHT, h, smallCardW, smallCardH, null);
        } else {
            im = ImageIO.read(Main.class.getResourceAsStream("cards/" + players[p].equpiment[1].image));
            g.drawImage(im, main.getWidth() - smallCardW * 2 - heroW - B0RDER_RIGHT, h, smallCardW, smallCardH, null);
        }
    }

    private static void drawPlayerCreature(Graphics g, int np) throws IOException {
        final int BORDER_CREATURE = 3;
        int numUnit = 0;
        int h;
        BufferedImage im;
        if (np == 0) h = battlegroundClick.getY() + battlegroundClick.getHeight() - heroH;
        else h = battlegroundClick.getY();
        int ht = h + (heroH - heroW) / 2;

        if (!Board.creature.get(np).isEmpty()) {
            // for (Creature creature : Board.creature.get(np))//Sometimes it make exception after any creature die(((
            for (int i = 0; i < Board.creature.get(np).size(); i++)//{
            {
                if (Board.creature.get(np).get(i).image != null) {
                    try {
                        int effects = 0;
                        int crX = battlegroundClick.getX() + numUnit * (heroH + BORDER_CREATURE) + (heroH - heroW) / 2;
                        int crXt = battlegroundClick.getX() + numUnit * (heroH + BORDER_CREATURE);
                        im = ImageIO.read(Main.class.getResourceAsStream("cards/" + Board.creature.get(np).get(i).image));
//                        if ((hilightMyCreature==i)&& (np==0)){
//                            g.drawImage(im, B0RDER_LEFT, battlegroundClick.getY() + battlegroundClick.getHeight() -bigCardH , bigCardW, bigCardH, null);
//                            //Draw effects
//                            BufferedImage tmpim
//                            if (Board.creature.get(np).get(i).effects.bonusPower!=0){
//
//                            }
//                        }
                        if (Board.creature.get(np).get(i).isTapped) {
                            g.drawImage(MyFunction.tapImage(im), crXt, ht, heroH, heroW, null);
                            playerUnitClick[np][numUnit].setSize(heroH, heroW);
                            playerUnitClick[np][numUnit].setLocation(crXt, ht);//May be write not center?
                        } else {
                            g.drawImage(im, crX, h, heroW, heroH, null);
                            playerUnitClick[np][numUnit].setLocation(crX, h);
                            playerUnitClick[np][numUnit].setSize(heroW, heroH);
                        }
                        if (Board.creature.get(np).get(i).damage != 0) {
                            im = ImageIO.read(Main.class.getResourceAsStream("icons/damage/" + Board.creature.get(np).get(i).damage + ".png"));
                            g.drawImage(im, crX + heroW / 2 - heroH / 10, h + heroH / 2 - heroH / 10, heroH / 5, heroH / 5, null);
                        }
                        if (Board.creature.get(np).get(i).effects.poison != 0) {
                            im = ImageIO.read(Main.class.getResourceAsStream("icons/effects/poison" + Board.creature.get(np).get(i).effects.poison + ".png"));
                            g.drawImage(im, crX + heroW / 2 - heroH / 10 - heroH / 5, h + heroH / 2 - heroH / 10 - heroH / 5, heroH / 5, heroH / 5, null);
                            effects++;
                        }
                        //TODO When icons complite, replace 3 for
                        if (Board.creature.get(np).get(i).effects.bonusPower != 0) {
                            im = ImageIO.read(Main.class.getResourceAsStream("icons/effects/bonuspower" + 3 + ".png"));
                            g.drawImage(im, crX + heroW / 2 - heroH / 10 - heroH / 5 + effects * heroH / 5, h + heroH / 2 - heroH / 10 - heroH / 5, heroH / 5, heroH / 5, null);
                            effects++;
                        }
                        if (Board.creature.get(np).get(i).effects.bonusArmor != 0) {
                            im = ImageIO.read(Main.class.getResourceAsStream("icons/effects/bonusarmor" + 3 + ".png"));
                            g.drawImage(im, crX + heroW / 2 - heroH / 10 - heroH / 5 + effects * heroH / 5, h + heroH / 2 - heroH / 10 - heroH / 5, heroH / 5, heroH / 5, null);
                            effects++;
                        }
//                        if (Board.creature.get(np).get(i).effects.bonusTougness != 0) {
//                            im = ImageIO.read(Main.class.getResourceAsStream("icons/effects/bonustougness" + Board.creature.get(np).get(i).effects.bonusTougness + ".png"));
//                            g.drawImage(im, battlegroundClick.getX() + (int) (numUnit * heroW) + heroW / 2 - heroH / 10 - heroH / 5+effects*heroH / 5, h + heroH / 2 - heroH / 10 - heroH / 5, heroH / 5, heroH / 5, null);
//                            effects++;
//                        }
//                        if (Board.creature.get(np).get(i).effects.turnToDie < 3) {
//                            im = ImageIO.read(Main.class.getResourceAsStream("icons/effects/turntodie" + Board.creature.get(np).get(i).effects.turnToDie + ".png"));
//                            g.drawImage(im, battlegroundClick.getX() + (int) (numUnit * heroW) + heroW / 2 - heroH / 10 - heroH / 5+effects*heroH / 5, h + heroH / 2 - heroH / 10 - heroH / 5, heroH / 5, heroH / 5, null);
//                            effects++;
//                        }
                        numUnit++;
                    } catch (IOException e) {
                        System.out.println("Can't load image.");
                    }
                }
            }
            //TODO when you add Atack Exp, draw other
            if (isMyTurn == playerStatus.IChoiseBlocker) {
                im = ImageIO.read(Main.class.getResourceAsStream("icons/effects/attackinitiator.png"));
                g.drawImage(im, battlegroundClick.getX() + creatureWhoAttack * (BORDER_CREATURE + heroH) + heroH / 2 - heroH / 10, battlegroundClick.getY() + (heroH + heroW) / 2 - heroH / 10, heroH / 5, heroH / 5, null);
                if (creatureWhoAttackTarget != -1)
                    if (Board.creature.get(0).get(creatureWhoAttackTarget).isTapped) {
                        g.drawImage(im, battlegroundClick.getX() + creatureWhoAttackTarget * (BORDER_CREATURE + heroH) + heroW / 2 - heroH / 10, ht - heroH / 5, heroH / 5, heroH / 5, null);
                    } else
                        g.drawImage(im, battlegroundClick.getX() + creatureWhoAttackTarget * (BORDER_CREATURE + heroH) + heroW / 2 - heroH / 10, h - heroH / 5, heroH / 5, heroH / 5, null);
                else
                    g.drawImage(im, playerHeroClick.getX() + heroW / 2 - heroH / 10, playerHeroClick.getY() - heroH / 10, heroH / 5, heroH / 5, null);
            }
            if ((isMyTurn == playerStatus.EnemyChoiseBlocker) && (Main.replayCounter == 0)) {
                im = ImageIO.read(Main.class.getResourceAsStream("icons/effects/attackinitiatorrevert.png"));
                g.drawImage(im, battlegroundClick.getX() + creatureWhoAttack * (heroH + BORDER_CREATURE) + heroH / 2 - heroH / 10, battlegroundClick.getY() + battlegroundClick.getHeight() - (heroH + heroW) / 2 - heroH / 10, heroH / 5, heroH / 5, null);
                if (creatureWhoAttackTarget != -1) {
                    if (Board.creature.get(0).get(creatureWhoAttackTarget).isTapped) {
                        g.drawImage(im, battlegroundClick.getX() + creatureWhoAttackTarget * (heroH + BORDER_CREATURE) + heroH / 2 - heroH / 10, battlegroundClick.getY() + (heroH + heroW) / 2 - heroH / 10, heroH / 5, heroH / 5, null);
                    } else
                        g.drawImage(im, battlegroundClick.getX() + creatureWhoAttackTarget * (heroH + BORDER_CREATURE) + heroW / 2 - heroH / 10, battlegroundClick.getY() + heroH - heroH / 10, heroH / 5, heroH / 5, null);
                } else
                    g.drawImage(im, enemyHeroClick.getX() + heroW / 2 - heroH / 10, enemyHeroClick.getY() + enemyHeroClick.getHeight() - heroH / 10, heroH / 5, heroH / 5, null);
            }
        }

    }

    private static void releaseCardWithX(int x) {
        System.out.println(choiceXtext + "," + x + ")");
        Client.writeLine(choiceXtext + "," + x + ")");
    }

    private static void loadImage() {
        try {
            background = ImageIO.read(Main.class.getResourceAsStream("Background.jpg"));
            heroCoinImage = ImageIO.read(Main.class.getResourceAsStream("icons/Coin.png"));
            heroDeckImage = ImageIO.read(Main.class.getResourceAsStream("icons/Deck.png"));
            menuImage = ImageIO.read(Main.class.getResourceAsStream("icons/Exit.png"));
            endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Endturn.png"));
            heroGraveyardImage = ImageIO.read(Main.class.getResourceAsStream("icons/Graveyard.png"));
            redcrossImage = ImageIO.read(Main.class.getResourceAsStream("icons/Bigredcross.png"));
            heroNoArmorImage = ImageIO.read(Main.class.getResourceAsStream("icons/Noarmor.png"));
            heroNoAmuletImage = ImageIO.read(Main.class.getResourceAsStream("icons/Noamulet.png"));
            heroNoWeaponImage = ImageIO.read(Main.class.getResourceAsStream("icons/Noweapon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadDeckFromServer(Deck deck) throws IOException {
        String card;
        while (!(card = Client.readLine()).equals("$ENDDECK")) {
            deck.cards.add(new Card(Card.getCardByName(card)));
        }
    }

    private static void loadDeckFromFile(Deck deck, String deckName) throws IOException {
        InputStream path = Main.class.getResourceAsStream("decks/" + deckName + ".txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(path, "windows-1251"));
        try {
            String line = "";
            while (line != null) {
                line = br.readLine();
                if (line != null) {
                    //    System.out.println("Load card = " + line);
                    deck.cards.add(new Card(Card.getCardByName(line)));
                }
            }
        } finally {
            br.close();
        }
    }

    private static void setInitialProperties() throws IOException {
        main.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(main,
                        "", "Действительно выйти",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    System.out.println("$DISCONNECT");
                    Client.writeLine("$DISCONNECT");
                    writerToLog.close();
                    System.exit(0);
                }
            }
        });
        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        String fname = dateFormat.format(date);
        File folder = new File("logs");
        if (!folder.exists()) {
            folder.mkdir();
        }

        File file = new File("logs/" + fname + ".txt");
        FileWriter writer = new FileWriter(file, true);
        writerToLog = new PrintWriter(writer);
        main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Board.creature = new ArrayList<>(2);
        Board.creature.add(new ArrayList<>());
        Board.creature.add(new ArrayList<>());
        //     Board.isActiveFirst = true;

        playerCoinLabel.setHorizontalAlignment(SwingConstants.LEFT);
        playerCoinLabel.setVerticalAlignment(SwingConstants.TOP);
        playerCoinLabel.setForeground(Color.WHITE);
        enemyCoinLabel.setHorizontalAlignment(SwingConstants.LEFT);
        enemyCoinLabel.setVerticalAlignment(SwingConstants.TOP);
        enemyCoinLabel.setForeground(Color.WHITE);

//        enemyDamageLabel.setHorizontalAlignment(SwingConstants.LEFT);
//        enemyDamageLabel.setVerticalAlignment(SwingConstants.TOP);
//        enemyDamageLabel.setForeground(Color.RED);
//        enemyDamageLabel.setFont(new Font(enemyDamageLabel.getFont().getName(), Font.PLAIN, 20));
//        playerDamageLabel.setHorizontalAlignment(SwingConstants.LEFT);
//        playerDamageLabel.setVerticalAlignment(SwingConstants.TOP);
//        playerDamageLabel.setForeground(Color.RED);
//        playerDamageLabel.setFont(new Font(playerDamageLabel.getFont().getName(), Font.PLAIN, 20));

        gameLog.setLocation(0, 0);
        gameLog.setSize(1, 1);
        gameLog.setHorizontalAlignment(SwingConstants.LEFT);
        gameLog.setVerticalAlignment(SwingConstants.TOP);
        gameLog.setForeground(Color.WHITE);

        deckClick.addMouseMotionListener(new MyListener(MyListener.Compo.Deck, 0));
        deckClick.addMouseListener(new MyListener(MyListener.Compo.Deck, 0));
        menuClick.addMouseMotionListener(new MyListener(MyListener.Compo.Menu, 0));
        menuClick.addMouseListener(new MyListener(MyListener.Compo.Menu, 0));

        weaponClick.addMouseMotionListener(new MyListener(MyListener.Compo.Weapon, 0));
        weaponClick.addMouseListener(new MyListener(MyListener.Compo.Weapon, 0));

        battlegroundClick.addMouseMotionListener(new MyListener(MyListener.Compo.Board, 0));
        battlegroundClick.addMouseListener(new MyListener(MyListener.Compo.Board, 0));
        endTurnClick.addMouseListener(new MyListener(MyListener.Compo.EndTurnButton, 0));
        enemyHeroClick.addMouseListener(new MyListener(MyListener.Compo.EnemyHero, 0));
        enemyHeroClick.addMouseMotionListener(new MyListener(MyListener.Compo.EnemyHero, 0));
        playerHeroClick.addMouseListener(new MyListener(MyListener.Compo.PlayerHero, 0));
        playerHeroClick.addMouseMotionListener(new MyListener(MyListener.Compo.PlayerHero, 0));

        viewField.setVisible(false);
        viewField.setLocation(0, 0);
        for (int i = 0; i < cardClick.length; i++) {
            cardClick[i] = new JLabel();
            viewField.add(cardClick[i]);
            cardClick[i].addMouseListener(new MyListener(MyListener.Compo.CardInHand, i));
            cardClick[i].addMouseMotionListener(new MyListener(MyListener.Compo.CardInHand, i));
        }
        for (int ii = 0; ii <= 1; ii++) {
            for (int i = 0; i < playerUnitClick[ii].length; i++) {
                playerUnitClick[ii][i] = new JLabel();
                //playerUnitLabel[ii][i] = new JLabel();
                viewField.add(playerUnitClick[ii][i]);
                //  viewField.add(playerUnitLabel[ii][i]);
                if (ii == 0) {
                    playerUnitClick[ii][i].addMouseListener(new MyListener(MyListener.Compo.CreatureInMyPlay, i));
                    playerUnitClick[ii][i].addMouseMotionListener(new MyListener(MyListener.Compo.CreatureInMyPlay, i));
                } else {
                    playerUnitClick[ii][i].addMouseListener(new MyListener(MyListener.Compo.EnemyUnitInPlay, i));
                    playerUnitClick[ii][i].addMouseMotionListener(new MyListener(MyListener.Compo.EnemyUnitInPlay, i));
                }
                playerUnitClick[ii][i].setHorizontalAlignment(SwingConstants.CENTER);
                playerUnitClick[ii][i].setVerticalAlignment(SwingConstants.CENTER);
                playerUnitClick[ii][i].setForeground(Color.RED);
                // playerUnitClick[ii][i].setFont(new Font(enemyDamageLabel.getFont().getName(), Font.PLAIN, 20));
//                playerUnitLabel[ii][i].setHorizontalAlignment(SwingConstants.LEFT);
//                playerUnitLabel[ii][i].setVerticalAlignment(SwingConstants.TOP);
//                playerUnitLabel[ii][i].setForeground(Color.RED);
//                playerUnitLabel[ii][i].setFont(new Font(enemyDamageLabel.getFont().getName(), Font.PLAIN, 20));
            }
        }

        message.setHorizontalAlignment(SwingConstants.CENTER);
        message.setVerticalAlignment(SwingConstants.CENTER);
        message.setForeground(Color.YELLOW);
        message.setFont(new Font("Georgia", Font.ITALIC, 50));
        message.setText("");
        message.setVisible(false);

//        g1 = (Graphics2D) g;
//        g1.setFont(font);
//        metrics = g1.getFontMetrics();
//        int x = main.getWidth() / 2 - metrics.stringWidth(messageToShow.message) / 2;
//        int y = main.getHeight() / 2;
//        g1.setPaint(new Color(150, 150, 150));
//        TextLayout textLayout = new TextLayout(messageToShow.message, font, g1.getFontRenderContext());
//        textLayout.draw(g1, x + 3, y + 3);
//
//        g1.setPaint(Color.yellow);
//        textLayout.draw(g1, x, y);

        viewField.add(message);

        for (int i = 0; i < 9; i++) {
            choiceXLabel[i] = new JLabel();
            choiceXLabel[i].setHorizontalAlignment(SwingConstants.CENTER);
            choiceXLabel[i].setVerticalAlignment(SwingConstants.CENTER);
            choiceXLabel[i].setForeground(Color.BLACK);
            choiceXLabel[i].setFont(new Font("Courier", Font.BOLD, 24));
            choiceXLabel[i].setText(String.valueOf(i));
            choiceXLabel[i].addMouseListener(new MyListener(MyListener.Compo.ChoiseX, i));
            choiceXLabel[i].addMouseMotionListener(new MyListener(MyListener.Compo.ChoiseX, i));
            choiceXLabel[i].setVisible(false);
            viewField.add(choiceXLabel[i]);
        }
        for (int i = 0; i < 40; i++) {
            searchXLabel[i] = new JLabel();
            searchXLabel[i].addMouseListener(new MyListener(MyListener.Compo.SearchX, i));
            searchXLabel[i].addMouseMotionListener(new MyListener(MyListener.Compo.SearchX, i));
            searchXLabel[i].setVisible(false);
            viewField.add(searchXLabel[i]);
        }
        //
        viewField.add(battlegroundClick);
        viewField.add(deckClick);
        viewField.add(menuClick);
        viewField.add(weaponClick);
        viewField.add(playerGraveyardClick);
        viewField.add(enemyGraveyardClick);
        viewField.add(endTurnClick);
        viewField.add(enemyHeroClick);
        viewField.add(playerHeroClick);
        viewField.add(playerCoinLabel);
        viewField.add(enemyCoinLabel);
        // viewField.add(playerDamageLabel);
        // viewField.add(enemyDamageLabel);
        viewField.add(gameLog);
        viewField.validate();
        main.add(viewField);
        viewField.repaint();

        //
        gameLog.setAutoscrolls(true);
        Border border = LineBorder.createGrayLineBorder();
        gameLog.setBorder(border);

        main.setVisible(true);
    }

    private static class ViewField extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            try {
                onRepaint(g);//its too slow!! TODO repaint not many time
                //    if (needToRefreshMessage) { drawMessage(g);}
            } catch (IOException e) {
                System.out.println("Error in onRepaint.");
                e.printStackTrace();
            }
        }

        ViewField() {
            super();
        }

    }

    private static class MessageToShow {
        String message;
        long whenAdd;
        long lenght;

        MessageToShow(String _message, long _lenght) {
            Calendar cal = Calendar.getInstance();
            message = _message;
            whenAdd = 0;
            lenght = _lenght;
        }
    }

    private static int boolToInt(boolean b) {
        return b ? 1 : 0;
    }
}
