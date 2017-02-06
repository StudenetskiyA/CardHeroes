package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.company.MyFunction.ActivatedAbility;
import static com.company.MyFunction.ActivatedAbility.WhatAbility.*;

public class Main extends JFrame {
    //Connect constant
    private static final String CLIENT_VERSION = "0.02";
    private static final String address = "127.0.0.1";//"test1.uralgufk.ru";//"127.0.0.1";  //"cardheroes.hldns.ru";
    private static String serverPort = "8901";
    //View constant
    static final boolean NEW_GRAPHICS = true;
    static final int BORDER_CREATURE = 3;
    private static final int B0RDER_RIGHT = 15;
    private static final int B0RDER_LEFT = 10;
    private static final int B0RDER_TOP = 10;
    private static final int B0RDER_BETWEEN = 5;
    private static int B0RDER_BOTTOM = 40;
    private static final double CARD_SIZE_FROM_SCREEN = 0.08;
    private static final double BIG_CARD_SIZE_FROM_SCREEN = 0.17;
    private static final double SMALL_CARD_SIZE_FROM_SCREEN = 0.06;
    private static final double CARD_PROPORTIONAL = 400 / 283;

    static int replayCounter = 0;
    static boolean connected = false;
    static Font font;
    static Graphics2D g1;
    static FontMetrics metrics;
    static PrintWriter writerToLog;
    //Elements of view
    static Main main = new Main();
    static MyFunction.ClickImage playerHeroClick[] = new MyFunction.ClickImage[2];
    static JLabel choiceXLabel[] = new JLabel[15];
    private static int isShowGraveyard = -1;
    //Chat
    static JTextField textField = new JTextField();
    static JTextArea messageArea = new JTextArea();
    //
    static Player[] players = new Player[2];
    static Deck simpleDeck;
    static Deck simpleEnemyDeck;
    static ArrayList<String> decksChoice = new ArrayList<>();
    static ArrayList<String> decksChoiceHeroes = new ArrayList<>();
    static JScrollPane scrollPane;
    static ArrayList<Card> founded;
    static boolean isYouDraggedCard = false;
    static boolean isYouDraggedAttackCreature = false;
    static JTextField enterNameFieled = new JTextField(40);
    static ResponseServerMessage responseServerMessage;
    static int choiceXcolor = 0;
    static int choiceXtype = 0;
    static String choiceXcreatureType = "";
    static int choiceXcost = 0;
    static int choiceXcostExactly = 0;
    static String choiceXtext;
    static PlayerStatus isMyTurn = PlayerStatus.prepareForBattle;
    static boolean wantToMulligan[] = new boolean[4];
    static int creatureWhoAttack;
    static int creatureWhoAttackTarget;
    static int hilightMyCreature = -1;
    static int hilightEnemyCreature = -1;
    static int hilightMyCard = -1;
    static String replayDeck = "";
    //Monitors for thread
    static boolean ready = true;
    public static final Object monitor = new Object();
    //
    static boolean isReplay = false;
    static Thread cycleReadFromServer = new CycleServerRead();
    private static ArrayList<String> replay = new ArrayList<>();
    //For repaint
    private static int heroW;
    private static int heroH;
    private static int smallCardW;
    private static int smallCardH;
    private static int bigCardW;
    private static int bigCardH;
    private static int cardX;
    //Elements of view
    private static ViewField viewField = new ViewField();
    private static JLabel deckClick = new JLabel();
    private static MyFunction.ClickImage weaponClick = new MyFunction.ClickImage();
    private static JLabel cardClick[] = new JLabel[9];//TODO 10 card maximum??
    private static JLabel deckChoiseClick[] = new JLabel[9];
    private static UnitLabel unitClick[][] = new UnitLabel[2][9];
    private static JLabel battlegroundClick = new JLabel();
    private static JLabel playerCoinLabel = new JLabel();
    private static JLabel enemyCoinLabel = new JLabel();
    private static JLabel playerGraveyardClick = new JLabel();
    private static JLabel enemyGraveyardClick = new JLabel();
    private static JLabel gameLog = new JLabel();
    private static JLabel endTurnClick = new JLabel();
    private static JLabel searchXLabel[] = new JLabel[40];
    private static JLabel message = new JLabel();
    private static MyFunction.ClickImage menuClick = new MyFunction.ClickImage();
    private static MyFunction.ClickImage fullScreenClick = new MyFunction.ClickImage();
    private static MyFunction.ClickImage settingsClick = new MyFunction.ClickImage();
    //Static image for button, background and etc.
    private static Image background;
    private static Image heroCoinImage;
    private static Image heroDeckImage;
    private static Image endTurnImage;
    private static Image redcrossImage;
    private static Image heroGraveyardImage;
    private static Card cardMem;
    private static Creature creatureMem;
    private static String whereMyMouse;
    private static int whereMyMouseNum;
    private static int repainted;//For test how many times called onRepaint
    private static MessageToShow messageToShow = new MessageToShow(" ", 0);
    static int enemyHandSize = 0;

    enum PlayerStatus {
        MyTurn(1), EnemyTurn(2), IChoiceBlocker(3), EnemyChoiceBlocker(4), EnemyChoiceTarget(5), MuliganPhase(6), waitingForConnection(7),
        waitOtherPlayer(8), waitingMulligan(9), choiseX(10), searchX(11), choiceTarget(12), digX(13), endGame(14), prepareForBattle(15),
        unknow(0);

        private final int value;

        PlayerStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static PlayerStatus fromInteger(int x) {
            switch (x) {
                case 0:
                    return unknow;
                case 1:
                    return MyTurn;
                case 2:
                    return EnemyTurn;
                case 3:
                    return IChoiceBlocker;
                case 4:
                    return EnemyChoiceBlocker;
                case 5:
                    return EnemyChoiceTarget;
                case 6:
                    return MuliganPhase;
                case 7:
                    return waitingForConnection;
                case 8:
                    return waitOtherPlayer;
                case 9:
                    return waitingMulligan;
                case 10:
                    return choiseX;
                case 11:
                    return searchX;
                case 12:
                    return choiceTarget;
                case 13:
                    return digX;
                case 14:
                    return endGame;
                case 15:
                    return prepareForBattle;
            }
            return null;
        }
    }

    // static UserChoice userChoice;//For choice, when card ask

    public static void main(String[] args) throws IOException, InterruptedException {
        prepareListOfDeck();
        loadImage();
        setInitialProperties();

        //TODO Memorise window size on exit and restore here.
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

        viewField.setVisible(true);

        //  userChoice = new UserChoice(viewField,"cards/Мастер теней.jpg",);

        try {
            //TODO while connect
            Client.connect(Integer.parseInt(serverPort), address);
        } catch (Exception x) {
            System.out.println("Cloud not connect to server.");
        }
        main.repaint();
        if (connected) {
            if (args.length == 0) {
                cycleReadFromServer.start();
            } else {
                if (args.length > 2) {
                    replayDeck = args[3];
                    isReplay = true;
                    runGame(args[0], args[1], args[2]);
                } else {
                    runGame(args[0], args[1], null);
                    main.repaint();
                    cycleReadFromServer.start();
                }
            }
        } else {
            System.exit(1);
        }
    }

    private static void runGame(String playerName, String deckName, String replayName) throws IOException, InterruptedException {
        System.out.println("Game runs " + playerName + " " + deckName);
        simpleDeck = new Deck(deckName);
        simpleEnemyDeck = new Deck("defaultDeck");
        loadDeckFromFile(simpleDeck, deckName);
        Card c = new Card(simpleDeck.cards.get(0));
        players[0] = new Player(c, simpleDeck, playerName, 0);
        simpleDeck.cards.remove(0);
        players[1] = new Player(simpleDeck, "", playerName, 0, 30);
        enterNameFieled.setVisible(false);
        textField.setVisible(true);
        scrollPane.setVisible(true);


        playerHeroClick[0].image = ImageIO.read(Main.class.getResourceAsStream("cards/heroes/" + c.name + ".jpg"));

        if (playerName.equals("replay")) {
            InputStream path = Main.class.getResourceAsStream("replays/" + replayName + ".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(path, "windows-1251"));
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    replay.add(line);
                    System.out.println(line);
                }
            } catch (Exception x) {
                System.out.println("Cloud not read replay.");
            }
            Main.gameLog.setText("<html>");
            printToView(0, "Player=" + players[0].playerName + ",port=" + serverPort);
            isMyTurn = PlayerStatus.waitOtherPlayer;
        } else {
            Main.gameLog.setText("<html>");
            printToView(0, "Player=" + players[0].playerName + ",port=" + serverPort);

            if (connected) {
                isMyTurn = PlayerStatus.waitOtherPlayer;
                Client.writeLine("$IAM(" + players[0].playerName + "," + players[0].deck.name + "," + CLIENT_VERSION + ")");
                //Send deck
                Client.writeLine(players[0].name);
                for (Card card : players[0].deck.cards) {
                    Client.writeLine(card.name);
                }
                Client.writeLine("$ENDDECK");
            }
        }
    }

    static String getNextReplayLine() throws InterruptedException {
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

    public static void printToView(int type, String txt) {
        if (txt.contains("Ход номер ")) Main.gameLog.setText("<html>");
        if (messageToShow!=null) messageToShow.lenght=0;
        if (type == 0) {
            Main.gameLog.setText(Main.gameLog.getText() + txt + "<br>");
        } else if (type == 1) {
            messageToShow = new MessageToShow(txt, 1500);
        }
    }
    public static void printToView(int type, Color c, String txt) {
        if (txt.contains("Ход номер ")) Main.gameLog.setText("<html>");
        messageToShow=new MessageToShow("",c,0);
        if (type == 0) {
            Main.gameLog.setText(Main.gameLog.getText() + txt + "<br>");
        } else if (type == 1) {
            messageToShow = new MessageToShow(txt, c, 1500);
        } else if (type == 2) {
            messageToShow = new MessageToShow(txt, c, 150000);
        }
    }

    static void cardAnimationShowing(Graphics g, BufferedImage image, boolean playerOrEnemy, int step) {
        final double steps = 100;
        if (step > steps / 2) step = (int) steps;
        System.out.println("Step=" + step);
        int x = playerHeroClick[0].getX();
        int y = playerHeroClick[0].getY() - playerHeroClick[0].getHeight();
        int w = smallCardW;
        int h = smallCardH;
        double stepY = Math.abs(y - main.getHeight() / 2 + bigCardH / 2) / (steps * 2);
        double stepW = (bigCardW - w) / (steps * 2);
        double stepH = Math.abs(bigCardH - h) / (steps * 2);
        g.drawImage(image, x, y - (int) (stepY * step), w + (int) (stepW * step), h + (int) (stepH * step), null);
        main.repaint();
    }

    private static void onRepaint(Graphics g) {
            try {
                onRepaintNew(g);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private static void onRepaintNew(Graphics g) throws IOException {
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        // System.out.println("onRepaint " + repainted);
        repainted++;
        bigCardW = (int) (main.getWidth() * BIG_CARD_SIZE_FROM_SCREEN);
        bigCardH = (int) (bigCardW * 400 / 283);//CARD_PROPORTION not work correct
        heroW = (int) (main.getWidth() * CARD_SIZE_FROM_SCREEN);
        heroH = (int) (heroW * 400 / 283);
        smallCardW = (int) (main.getWidth() * SMALL_CARD_SIZE_FROM_SCREEN);
        smallCardH = (int) (smallCardW * 400 / 283);
        cardX = B0RDER_LEFT + B0RDER_BETWEEN * 3 + smallCardW * 3;

        //Background
        int width = background.getWidth(null);
        int height = background.getHeight(null);

        g.drawImage(background, 0, 0, main.getWidth(), main.getWidth() * height / width, null);

        if (isMyTurn != PlayerStatus.prepareForBattle) {
            BufferedImage im;
            int numCardInHand = 0;
            //Battleground
            battlegroundClick.setLocation(cardX, B0RDER_TOP + B0RDER_BETWEEN + heroH);
            battlegroundClick.setSize(main.getWidth() - B0RDER_RIGHT - battlegroundClick.getX(), main.getHeight() - B0RDER_BOTTOM - B0RDER_BETWEEN * 2 - B0RDER_TOP - heroH * 2);
            g.drawRect(battlegroundClick.getX(), battlegroundClick.getY(), battlegroundClick.getWidth(), battlegroundClick.getHeight());//TODO Image of battleground
            //End turn button
            settingsClick.LSD(g, main.getWidth() - smallCardH / 3 - B0RDER_RIGHT, main.getHeight() - smallCardH / 3 - B0RDER_BOTTOM, smallCardW / 3, smallCardH / 3);

            menuClick.setLocation(main.getWidth() / 2 - heroW / 2, main.getHeight() / 2 - heroH / 2);
            menuClick.setSize(heroW, heroW * 149 / 283);
            menuClick.drawImage(g);

            fullScreenClick.setLocation(main.getWidth() / 2 - heroW / 2, main.getHeight() / 2 - heroH / 2 - heroW * 149 / 283 - B0RDER_BETWEEN);
            fullScreenClick.setSize(heroW, heroW * 149 / 283);
            fullScreenClick.drawImage(g);

            endTurnClick.setLocation(main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() / 2 - heroW * 75 / 283);
            endTurnClick.setSize(heroW, heroW * 149 / 283);

            if (isMyTurn == PlayerStatus.MyTurn) {
                endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Endturn.png"));
            } else if (isMyTurn == PlayerStatus.IChoiceBlocker) {
                endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Blockturn.png"));
            } else if (isMyTurn == PlayerStatus.EnemyChoiceBlocker) {
                endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Blockenemyturn.png"));
            } else if (isMyTurn == PlayerStatus.EnemyChoiceTarget) {
                endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Enemychoicetarget.png"));
            } else if (isMyTurn == PlayerStatus.MuliganPhase) {
                endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Mulliganturn.png"));
            } else if (isMyTurn == PlayerStatus.EnemyTurn) {
                endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Enemyturn.png"));
            } else if (isMyTurn == PlayerStatus.waitingMulligan) {
                endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Waitmulliganturn.png"));
            } else if (isMyTurn == PlayerStatus.waitingForConnection) {
                endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Connectionturn.png"));
            } else if (isMyTurn == PlayerStatus.choiceTarget) {
                endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Waittarget.png"));
            } else if (isMyTurn == PlayerStatus.searchX) {
                endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Waittarget.png"));//TODO Other icon
            } else if (isMyTurn == PlayerStatus.waitOtherPlayer) {
                endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Waitotherconnectionturn.png"));
            }
            g.drawImage(endTurnImage, endTurnClick.getX(), endTurnClick.getY(), endTurnClick.getWidth(), endTurnClick.getHeight(), null);

            //Heroes
            playerHeroClick[0].LSDiftap(g, players[0].isTapped, battlegroundClick.getX() + B0RDER_BETWEEN, main.getHeight() - heroH - B0RDER_BOTTOM, heroW, heroH);
            playerHeroClick[1].LSDiftap(g, players[1].isTapped, battlegroundClick.getX() + B0RDER_BETWEEN, B0RDER_TOP, heroW, heroH);

            drawPlayerEquipment(g, 0);
            drawPlayerEquipment(g, 1);
            drawPlayerDamageEffects(g, 0);
            drawPlayerDamageEffects(g, 1);

            //Decks
            deckClick.setLocation(B0RDER_LEFT, main.getHeight() - smallCardH - B0RDER_BOTTOM);
            deckClick.setSize(smallCardW, smallCardH);
            g.drawImage(heroDeckImage, deckClick.getX(), deckClick.getY(), deckClick.getWidth(), deckClick.getHeight(), null);
            g.drawImage(heroDeckImage, deckClick.getX(), B0RDER_TOP, deckClick.getWidth(), deckClick.getHeight(), null);
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
            enemyGraveyardClick.setLocation(deckClick.getX() + deckClick.getWidth() + B0RDER_BETWEEN, B0RDER_TOP);
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
            drawNewCreatures(g, 0);
            drawNewCreatures(g, 1);

            //Hero card in hand
            cardX = playerHeroClick[0].getX() + playerHeroClick[0].getHeight() + B0RDER_BETWEEN;

            if (!players[0].cardInHand.isEmpty()) {
                for (int i = players[0].cardInHand.size() - 1; i >= 0; i--)
                {
                    if (players[0].cardInHand.get(i).image != null) {
                        try {
                            im = ImageIO.read(Main.class.getResourceAsStream("cards/" + players[0].cardInHand.get(i).image));
                            if (isMyTurn == PlayerStatus.MuliganPhase) {
                                int tmp = (battlegroundClick.getWidth() - heroW - bigCardW * 4) / 5;
                                cardClick[i].setLocation(battlegroundClick.getX() + B0RDER_BETWEEN + (numCardInHand * bigCardW) + ((numCardInHand + 1) * tmp), main.getHeight() / 2 - bigCardH / 2);
                                cardClick[i].setSize(bigCardW, bigCardH);
                                g.drawImage(im, cardClick[i].getX(), cardClick[i].getY(), cardClick[i].getWidth(), cardClick[i].getHeight(), null);
                                if (wantToMulligan[i]) {
                                    g.drawImage(redcrossImage, cardClick[i].getX(), cardClick[i].getY(), cardClick[i].getWidth(), cardClick[i].getHeight(), null);
                                }
                            } else {
                                if (!isYouDraggedCard || players[0].cardInHand.get(i) != cardMem) {
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
                                            cardClick[i].setLocation(cardX + smallCardW + (int) (numCardInHand * smallCardW * 0.75), main.getHeight() - smallCardH - B0RDER_BOTTOM);
                                            cardClick[i].setSize(smallCardW, smallCardH);
                                            g.drawImage(im, cardClick[i].getX(), cardClick[i].getY(), cardClick[i].getWidth(), cardClick[i].getHeight(), null);
                                        }
                                    }
                                } else {
                                    cardClick[i].setLocation(0, 0);
                                    cardClick[i].setSize(0, 0);
                                }
                            }
                            numCardInHand++;
                        } catch (IOException e) {
                            System.out.println("Can't load image " + players[0].cardInHand.get(i).image);
                        }
                    }
                }
            }
            //Enemy cards
            im = ImageIO.read(Main.class.getResourceAsStream("icons/Deck.png"));//His card deck up
            for (int i = 0; i < enemyHandSize; i++) {
                g.drawImage(im, cardX + smallCardW + (int) (i * smallCardW * 0.5), B0RDER_TOP, smallCardW, smallCardH, null);
            }

            //Choice X
            if (isMyTurn == PlayerStatus.choiseX) {
                //Work too slow!
                System.out.println("choiseX");
                for (int i = 0; i <= players[0].untappedCoin; i++) {
                    g.drawImage(heroCoinImage, cardX + B0RDER_BETWEEN * i + smallCardW * i, main.getHeight() / 2 - smallCardH / 2, smallCardW, smallCardH, null);
                    choiceXLabel[i].setLocation(cardX + B0RDER_BETWEEN * i + smallCardW * i, main.getHeight() / 2 - smallCardH / 2);
                    choiceXLabel[i].setSize(smallCardW, smallCardH);
                    choiceXLabel[i].setVisible(true);
                }
            } else {
                for (int i = 0; i < choiceXLabel.length; i++)
                    choiceXLabel[i].setVisible(false);
            }
            //Search in deck
            if (isMyTurn == PlayerStatus.searchX) {
                drawSearchInDeck(g, false);
            }
            //Dig
            else if (isMyTurn == PlayerStatus.digX) {
                drawSearchInDeck(g, true);
            } else {
                for (int i = 0; i < searchXLabel.length; i++)
                    searchXLabel[i].setVisible(false);
            }

            //When you dragged card from hand
            if (isYouDraggedCard && cardMem != null) {
                im = ImageIO.read(Main.class.getResourceAsStream("cards/" + cardMem.image));
                g.drawImage(im, (int) MouseInfo.getPointerInfo().getLocation().getX() - smallCardW / 2 - (int) main.getLocationOnScreen().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY() - smallCardH - (int) main.getLocationOnScreen().getY(), smallCardW, smallCardH, null);
            }
            //When you dragged creature to attack
            if (isYouDraggedAttackCreature && creatureMem != null) {
                //center of creature
                int x1 = battlegroundClick.getX() + Board.creature.get(0).indexOf(creatureMem) * (heroH + BORDER_CREATURE) + heroW / 2;
                int y1 = battlegroundClick.getY() + battlegroundClick.getHeight() - heroH / 2;
                //cursor
                int x2 = (int) MouseInfo.getPointerInfo().getLocation().getX() - (int) main.getLocationOnScreen().getX();
                int y2 = (int) MouseInfo.getPointerInfo().getLocation().getY() - (int) main.getLocationOnScreen().getY();
                //Draw line from attacker to target.
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(10));
                g2.setColor(Color.RED);
                g2.draw(new Line2D.Float(x1, y1, x2, y2));
            }

            if (isShowGraveyard != -1) drawPlayerGraveyard(g, isShowGraveyard);

            drawMessage(g);
        } else if (isMyTurn == PlayerStatus.prepareForBattle) drawAvalaibleDeck(g);
    }

    private static void drawMessage(Graphics g) {
        //chat
        // cardX = B0RDER_LEFT + B0RDER_BETWEEN * 3 + smallCardW * 3;
        gameLog.setLocation(B0RDER_LEFT, B0RDER_TOP + smallCardH + B0RDER_BETWEEN);
        gameLog.setSize(battlegroundClick.getX() - B0RDER_BETWEEN - B0RDER_LEFT, main.getHeight() - B0RDER_BOTTOM - B0RDER_TOP - B0RDER_BETWEEN * 2 - smallCardH * 2 - heroH - smallCardH / 5);

        scrollPane.setLocation(B0RDER_LEFT, gameLog.getY() + gameLog.getHeight());
        //
        scrollPane.setSize(battlegroundClick.getX() - B0RDER_BETWEEN - B0RDER_LEFT, heroH);

        textField.setLocation(B0RDER_LEFT, scrollPane.getY() + scrollPane.getHeight());
        textField.setSize(battlegroundClick.getX() - B0RDER_BETWEEN - B0RDER_LEFT, smallCardH / 5);

        //JTextField textField = new JTextField(40);

        Calendar cal = Calendar.getInstance();
        long delta = cal.getTimeInMillis() - messageToShow.whenAdd;
        //  System.out.println("Delta="+delta);
        if ((messageToShow.lenght > delta) || (messageToShow.whenAdd == 0) && (!messageToShow.message.equals(" "))) {
            if (messageToShow.whenAdd == 0)
                messageToShow.whenAdd = cal.getTimeInMillis();

            int fs=(battlegroundClick.getWidth()-endTurnClick.getWidth())*20/450;
            font = new Font("Serif", Font.BOLD, fs);
            //font = new Font("Georgia", Font.ITALIC, 50);
            g1 = (Graphics2D) g;
            g1.setFont(font);
            metrics = g1.getFontMetrics();
            int x = battlegroundClick.getX()+battlegroundClick.getWidth()/2 - metrics.stringWidth(messageToShow.message) / 2;
            int y = main.getHeight() / 2;
            g1.setPaint(new Color(150, 150, 150));
            TextLayout textLayout = new TextLayout(messageToShow.message, font, g1.getFontRenderContext());
            textLayout.draw(g1, x + 3, y + 3);

            if (messageToShow.c != null) g1.setPaint(messageToShow.c);
            else g1.setPaint(Color.yellow);
            textLayout.draw(g1, x, y);
            main.repaint();
        } else if (messageToShow.lenght < delta) message.setVisible(false);
    }

    private static void drawSearchInDeck(Graphics g, boolean isGraveyard) throws IOException {
        if (isGraveyard) founded = new ArrayList<>(players[0].graveyard);
        else founded = new ArrayList<>(players[0].deck.cards);
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
            if ((choiceXcostExactly != founded.get(i).cost) && (choiceXcostExactly != 0)) {
                founded.remove(founded.get(i));
                continue;
            }
        }
        if (founded.size() == 0) {
            printToView(0, "В колоде ничего подходящего не найдено.");
            System.out.println("$FOUND(" + players[0].playerName + ",-1)");
            Client.writeLine("$FOUND(" + players[0].playerName + ",-1)");
            isMyTurn = PlayerStatus.MyTurn;
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
                viewField.revalidate();
            }
        }
    }

    private static void drawPlayerGraveyard(Graphics g, int n) throws IOException {
        founded = new ArrayList<>(players[n].graveyard);
        if (founded.size() == 0) isShowGraveyard = -1;
            //TODO If founded.size() small, draw bigger, if cards>30 - ?
        else {
            g.setFont(new Font("Georgia", Font.BOLD, 15));
            g.setColor(Color.WHITE);
            g.drawString("Кладбище " + players[n].playerName, cardX, main.getHeight() / 2 - 2 * smallCardH);

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

    private static void drawPlayerDamageEffects(Graphics g, int p) throws IOException {
        BufferedImage im;
        int h;
        int effectsFounded = 0;
        int heroCenterX = playerHeroClick[0].getX() + playerHeroClick[0].getWidth() / 2 - heroH / 10;
        if (p == 0) h = playerHeroClick[0].getY() + playerHeroClick[0].getHeight() / 2 - heroH / 10;
        else h = playerHeroClick[1].getY() + playerHeroClick[0].getHeight() / 2 - heroH / 10;

        if (players[p].damage != 0) {
            int j;
            for (j = 0; j < players[p].damage / 10; j++) {
                im = ImageIO.read(Main.class.getResourceAsStream("icons/damage/" + 10 + ".png"));
                g.drawImage(im, heroCenterX + (j - 1) * heroH / 5, h, heroH / 5, heroH / 5, null);
            }
            int a = players[p].damage % 10;
            if (a != 0) {
                im = ImageIO.read(Main.class.getResourceAsStream("icons/damage/" + a + ".png"));
                g.drawImage(im, heroCenterX + (j - 1) * heroH / 5, h, heroH / 5, heroH / 5, null);
            }
        }

        if (players[0].bbshield) {
            im = ImageIO.read(Main.class.getResourceAsStream("icons/effects/bbshield.png"));
            g.drawImage(im, heroCenterX - heroH * effectsFounded / 5, playerHeroClick[0].getY() + playerHeroClick[0].getHeight() / 2 - heroH / 10 - heroH / 5, heroH / 5, heroH / 5, null);
        }
        if (players[1].bbshield) {
            im = ImageIO.read(Main.class.getResourceAsStream("icons/effects/bbshield.png"));
            g.drawImage(im, heroCenterX - heroH * effectsFounded / 5, playerHeroClick[1].getY() + playerHeroClick[0].getHeight() / 2 - heroH / 10 - heroH / 5, heroH / 5, heroH / 5, null);
        }
    }

    private static void drawPlayerEquipment(Graphics g, int p) throws IOException {
        BufferedImage im;
        //TODO durability of armor
        int found = 0;
        int h;
        if (p == 0) h = main.getHeight() - smallCardH - B0RDER_BOTTOM;
        else h = B0RDER_TOP;
        int x = main.getWidth() - B0RDER_RIGHT - smallCardH - B0RDER_BETWEEN;

        if (players[p].equpiment[0] == null) {
            // g.drawImage(heroNoArmorImage, x, h, smallCardW, smallCardH, null);
        } else {
            im = ImageIO.read(Main.class.getResourceAsStream("cards/" + players[p].equpiment[0].image));
            g.drawImage(im, x, h, smallCardW, smallCardH, null);
            if (players[p].equpiment[0].hp != 0) {
                int a = 6 - players[p].equpiment[0].hp;
                if (a != 0) {
                    im = ImageIO.read(Main.class.getResourceAsStream("icons/damage/" + a + ".png"));
                    g.drawImage(im, x + smallCardW / 2 - heroH / 10, h + smallCardH / 2 - heroH / 10, heroH / 5, heroH / 5, null);
                }
            }
            found++;
        }
        //amulet
        if (players[p].equpiment[1] == null) {
            // g.drawImage(heroNoAmuletImage, x-smallCardH-B0RDER_BETWEEN, h, smallCardW, smallCardH, null);
        } else {
            im = ImageIO.read(Main.class.getResourceAsStream("cards/" + players[p].equpiment[1].image));
            g.drawImage(im, x - (smallCardH + B0RDER_BETWEEN) * found, h, smallCardW, smallCardH, null);
            found++;
        }
        //weapon
        if (players[p].equpiment[2] != null) {
            weaponClick.setVisible(true);
            weaponClick.image = ImageIO.read(Main.class.getResourceAsStream("cards/" + players[p].equpiment[2].image));
            weaponClick.LSDiftap(g, players[p].equpiment[2].isTapped, x - (smallCardH + B0RDER_BETWEEN) * found, h, smallCardW, smallCardH);
            found++;
        }

        //event
        if (players[p].equpiment[3] == null) {
            //    g.drawImage(heroNoEventImage, x-smallCardH*3-B0RDER_BETWEEN*3, h, smallCardW, smallCardH, null);
        } else {
            im = ImageIO.read(Main.class.getResourceAsStream("cards/" + players[p].equpiment[3].image));
            g.drawImage(im, x - (smallCardH + B0RDER_BETWEEN) * found, h, smallCardW, smallCardH, null);
            found++;
        }

    }

    private static void drawNewCreatures(Graphics g, int np) throws IOException {
        int numUnit = 0;
        int h;
        BufferedImage im;
        int crW = smallCardW;
        int crH = smallCardH;
        if (np == 0)
            h = battlegroundClick.getY() + battlegroundClick.getHeight() - crH - B0RDER_BETWEEN - UnitLabel.plusSize();
        else h = battlegroundClick.getY() + B0RDER_BETWEEN + UnitLabel.plusSize();

        if (!Board.creature.get(np).isEmpty()) {
            for (int i = 0; i < Board.creature.get(np).size(); i++)//{
            {
                if (Board.creature.get(np).get(i) != null && Board.creature.get(np).get(i).image != null && !Board.creature.get(np).get(i).isDie()) {
                    int crX = battlegroundClick.getX() + B0RDER_BETWEEN * 2 + numUnit * (crW + UnitLabel.plusSize() + BORDER_CREATURE + B0RDER_BETWEEN);// + (heroH - heroW) / 2;
                    unitClick[np][numUnit].setAll(Board.creature.get(np).get(i), crW, crH);
                    unitClick[np][numUnit].setLocation(crX, h);
                    unitClick[np][numUnit].drawImage(g);
                    if (i == hilightMyCreature && np==0) {
                        im = ImageIO.read(Main.class.getResourceAsStream("cards/" + Board.creature.get(np).get(i).image));
                        g.drawImage(im, crX + crW + UnitLabel.plusSize(), h - bigCardH + crH, bigCardW, bigCardH, null);
                    } else if (i == hilightEnemyCreature && np==1) {
                        im = ImageIO.read(Main.class.getResourceAsStream("cards/" + Board.creature.get(np).get(i).image));
                        g.drawImage(im, crX + crW + UnitLabel.plusSize(), h, bigCardW, bigCardH, null);
                    }
                }
                numUnit++;
            }
            numUnit=0;
            //Draw hilight creature AFTER draw other.
            for (int i = 0; i < Board.creature.get(np).size(); i++)//{
            {
                if (Board.creature.get(np).get(i) != null && Board.creature.get(np).get(i).image != null && Board.creature.get(np).get(i).getTougness() > Board.creature.get(np).get(i).damage) {
                    int crX = battlegroundClick.getX() + B0RDER_BETWEEN * 2 + numUnit * (crW + UnitLabel.plusSize() + BORDER_CREATURE + B0RDER_BETWEEN);// + (heroH - heroW) / 2;
                    if (i == hilightMyCreature && np==0) {
                        im = ImageIO.read(Main.class.getResourceAsStream("cards/" + Board.creature.get(np).get(i).image));
                        g.drawImage(im, crX + crW + UnitLabel.plusSize(), h - bigCardH + crH, bigCardW, bigCardH, null);
                    } else if (i == hilightEnemyCreature && np==1) {
                        im = ImageIO.read(Main.class.getResourceAsStream("cards/" + Board.creature.get(np).get(i).image));
                        g.drawImage(im, crX + crW + UnitLabel.plusSize(), h, bigCardW, bigCardH, null);
                    }
                    numUnit++;
                }
            }


            //TODO Do it
            int hBottom=battlegroundClick.getY() + battlegroundClick.getHeight() - crH - B0RDER_BETWEEN - UnitLabel.plusSize();
            int hTop=battlegroundClick.getY() + B0RDER_BETWEEN + UnitLabel.plusSize();
            if (isMyTurn == PlayerStatus.IChoiceBlocker) {
                im = ImageIO.read(Main.class.getResourceAsStream("icons/effects/attackinitiator.png"));
                int crX = battlegroundClick.getX() + B0RDER_BETWEEN * 2 + creatureWhoAttack * (crW + UnitLabel.plusSize() + BORDER_CREATURE + B0RDER_BETWEEN) + crW / 2 - heroW / 10;
                g.drawImage(im, crX, hTop + crH, heroH / 5, heroH / 5, null);
                if (creatureWhoAttackTarget != -1) {
                    crX = battlegroundClick.getX() + B0RDER_BETWEEN * 2 + creatureWhoAttackTarget * (crW + UnitLabel.plusSize() + BORDER_CREATURE + B0RDER_BETWEEN);
                    g.drawImage(im, crX, hBottom - heroH / 5, heroH / 5, heroH / 5, null);
                } else {//TODO Arrow to hero
                    //  g.drawImage(im, playerHeroClick[0].getX() + heroW / 2 - heroH / 10, playerHeroClick[0].getY() - heroH / 10, heroH / 5, heroH / 5, null);
                }
            }
            if ((isMyTurn == PlayerStatus.EnemyChoiceBlocker) && (Main.replayCounter == 0)) {
                im = ImageIO.read(Main.class.getResourceAsStream("icons/effects/attackinitiatorrevert.png"));
                int crX = battlegroundClick.getX() + B0RDER_BETWEEN * 2 + creatureWhoAttack * (crW + UnitLabel.plusSize() + BORDER_CREATURE + B0RDER_BETWEEN) + crW / 2 - heroW / 10;
                g.drawImage(im, crX, hBottom - heroH / 5, heroH / 5, heroH / 5, null);
                if (creatureWhoAttackTarget != -1) {
                    crX = battlegroundClick.getX() + B0RDER_BETWEEN * 2 + creatureWhoAttackTarget * (crW + UnitLabel.plusSize() + BORDER_CREATURE + B0RDER_BETWEEN);
                    g.drawImage(im, crX, hTop + crH, heroH / 5, heroH / 5, null);
                } else {
                    // g.drawImage(im, playerHeroClick[1].getX() + heroW / 2 - heroH / 10, playerHeroClick[1].getY() + playerHeroClick[1].getHeight() - heroH / 10, heroH / 5, heroH / 5, null);
                }
            }
        }
    }

    private static void prepareListOfDeck() throws UnsupportedEncodingException {
        String ppath = (new File(".").getAbsolutePath());
        System.out.println(ppath);
        File folder = new File(ppath + "/decks");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                decksChoice.add(file.getName());
            }
        }

        BufferedReader brIn = null;
        for (String deck : decksChoice) {
            try {
                InputStream path = Main.class.getResourceAsStream("decks/" + deck);
                brIn = new BufferedReader(new InputStreamReader(path, "windows-1251"));
                String a = brIn.readLine();
                decksChoiceHeroes.add(a);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (brIn != null) brIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void drawAvalaibleDeck(Graphics g) throws UnsupportedEncodingException {
        int deckShown = 0;
        //System.out.println("Draw avalaible deck.");
        g.setFont(new Font("Georgia", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        g.drawString("Введите имя и выберите колоду.", B0RDER_LEFT * 5, B0RDER_TOP * 5 + B0RDER_BETWEEN);
        enterNameFieled.setLocation(B0RDER_LEFT * 5, B0RDER_TOP * 5 + B0RDER_BETWEEN * 3);
        enterNameFieled.setSize(main.getWidth() / 3, B0RDER_BETWEEN * 4);
        for (String deck : decksChoice) {
            BufferedImage im;
            try {
                im = ImageIO.read(Main.class.getResourceAsStream("cards/heroes/" + decksChoiceHeroes.get(deckShown) + ".jpg"));
                g.drawImage(im, B0RDER_LEFT * 5 + heroW * deckShown + B0RDER_BETWEEN * deckShown * 2, main.getHeight() / 2, heroW, heroH, null);
                deckChoiseClick[deckShown].setLocation(B0RDER_LEFT * 5 + heroW * deckShown + B0RDER_BETWEEN * deckShown * 2, main.getHeight() / 2);
                deckChoiseClick[deckShown].setSize(heroW, heroH);
                g.setFont(new Font("Georgia", Font.BOLD, 13));
                g.drawString(deck.substring(0, deck.length() - 4).substring(0, Math.min(8, deck.substring(0, deck.length() - 4).length())), B0RDER_LEFT * 5 + heroW * deckShown + B0RDER_BETWEEN * deckShown * 2 + B0RDER_BETWEEN, main.getHeight() / 2 - B0RDER_BETWEEN);
                deckShown++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void releaseCardWithX(int x) {
        System.out.println(choiceXtext + "," + x + ")");
        Client.writeLine(choiceXtext + "," + x + ")");
    }

    private static void loadImage() {
        try {
            background = ImageIO.read(Main.class.getResourceAsStream("icons/background.jpg"));
            heroCoinImage = ImageIO.read(Main.class.getResourceAsStream("icons/Coin.png"));
            heroDeckImage = ImageIO.read(Main.class.getResourceAsStream("icons/Deck.png"));
            menuClick.image = ImageIO.read(Main.class.getResourceAsStream("icons/Exit.png"));
            fullScreenClick.image = ImageIO.read(Main.class.getResourceAsStream("icons/Fullscreen.png"));
            endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Endturn.png"));
            heroGraveyardImage = ImageIO.read(Main.class.getResourceAsStream("icons/Graveyard.png"));
            redcrossImage = ImageIO.read(Main.class.getResourceAsStream("icons/Bigredcross.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void loadDeckFromFile(Deck deck, String deckName) throws IOException {
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
                String message = "There are commands in the output buffer - really quit?";
                String title = "Really Quit?";
                // display the JOptionPane showConfirmDialog
                int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    System.out.println("Yes exit");
                    System.exit(0);
                }    }
        });

        viewField.add(enterNameFieled);

        textField.setEditable(true);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        textField.setVisible(false);
        scrollPane = new JScrollPane(messageArea);
        scrollPane.setVisible(false);

        viewField.add(scrollPane, "North");
        viewField.add(textField, "North");
        // Add Listeners
        textField.addActionListener(e -> {
            Client.writeLine("$CHAT(" + textField.getText() + ")");
            textField.setText("");
        });
        //For logs
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

        main.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        Board.creature = new ArrayList<>(2);
        Board.creature.add(new ArrayList<>());
        Board.creature.add(new ArrayList<>());

        playerCoinLabel.setHorizontalAlignment(SwingConstants.LEFT);
        playerCoinLabel.setVerticalAlignment(SwingConstants.TOP);
        playerCoinLabel.setForeground(Color.WHITE);
        enemyCoinLabel.setHorizontalAlignment(SwingConstants.LEFT);
        enemyCoinLabel.setVerticalAlignment(SwingConstants.TOP);
        enemyCoinLabel.setForeground(Color.WHITE);

        gameLog.setLocation(0, 0);
        gameLog.setSize(1, 1);
        gameLog.setHorizontalAlignment(SwingConstants.LEFT);
        gameLog.setVerticalAlignment(SwingConstants.TOP);
        gameLog.setForeground(Color.WHITE);

        deckClick.addMouseMotionListener(new MyListener(MyListener.Compo.Deck, 0));
        deckClick.addMouseListener(new MyListener(MyListener.Compo.Deck, 0));

        settingsClick.image = ImageIO.read(Main.class.getResourceAsStream("icons/Settings.png"));
        settingsClick.addMouseListener(new MyListener(MyListener.Compo.Settings, 0));

        fullScreenClick.setVisible(false);
        menuClick.setVisible(false);
        fullScreenClick.addMouseListener(new MyListener(MyListener.Compo.Fullscreen, 0));

        menuClick.addMouseListener(new MyListener(MyListener.Compo.Menu, 0));

        weaponClick.addMouseMotionListener(new MyListener(MyListener.Compo.Weapon, 0));
        weaponClick.addMouseListener(new MyListener(MyListener.Compo.Weapon, 0));

        endTurnClick.addMouseListener(new MyListener(MyListener.Compo.EndTurnButton, 0));

        playerHeroClick[0] = new MyFunction.ClickImage();
        playerHeroClick[1] = new MyFunction.ClickImage();
        playerHeroClick[1].addMouseListener(new MyListener(MyListener.Compo.EnemyHero, 0));
        playerHeroClick[1].addMouseMotionListener(new MyListener(MyListener.Compo.EnemyHero, 0));
        playerHeroClick[0].addMouseListener(new MyListener(MyListener.Compo.PlayerHero, 0));
        playerHeroClick[0].addMouseMotionListener(new MyListener(MyListener.Compo.PlayerHero, 0));

        playerGraveyardClick.addMouseListener(new MyListener(MyListener.Compo.PlayerGraveyard, 0));
        enemyGraveyardClick.addMouseListener(new MyListener(MyListener.Compo.EnemyGraveyard, 0));

        viewField.setVisible(false);
        viewField.setLocation(0, 0);

        for (int i = 0; i < deckChoiseClick.length; i++) {
            deckChoiseClick[i] = new JLabel();
            viewField.add(deckChoiseClick[i]);
            deckChoiseClick[i].addMouseListener(new MyListener(MyListener.Compo.DeckChoice, i));
        }
        for (int i = 0; i < cardClick.length; i++) {
            cardClick[i] = new JLabel();
            viewField.add(cardClick[i]);
            cardClick[i].addMouseListener(new MyListener(MyListener.Compo.CardInHand, i));
            cardClick[i].addMouseMotionListener(new MyListener(MyListener.Compo.CardInHand, i));
        }

        message.setHorizontalAlignment(SwingConstants.CENTER);
        message.setVerticalAlignment(SwingConstants.CENTER);
        message.setForeground(Color.YELLOW);
        message.setFont(new Font("Georgia", Font.ITALIC, 50));
        message.setText("");
        message.setVisible(false);

        viewField.add(message);
        viewField.add(deckClick);
        viewField.add(menuClick);
        viewField.add(fullScreenClick);
        viewField.add(settingsClick);
        viewField.add(weaponClick);
        viewField.add(playerGraveyardClick);
        viewField.add(enemyGraveyardClick);
        viewField.add(playerHeroClick[1]);
        viewField.add(playerHeroClick[0]);
        viewField.add(playerCoinLabel);
        viewField.add(enemyCoinLabel);
        viewField.add(gameLog);
        //Battleground block
        battlegroundClick.addMouseMotionListener(new MyListener(MyListener.Compo.Board, 0));
        battlegroundClick.addMouseListener(new MyListener(MyListener.Compo.Board, 0));

        for (int ii = 0; ii <= 1; ii++) {
            for (int i = 0; i < unitClick[ii].length; i++) {
                unitClick[ii][i] = new UnitLabel();
                unitClick[ii][i].tapClick = new MyFunction.ClickImage();
                viewField.add(unitClick[ii][i], 0);
                viewField.add(unitClick[ii][i].tapClick, 0);
                if (ii == 0) {
                    unitClick[ii][i].addMouseListener(new MyListener(MyListener.Compo.CreatureInMyPlay, i));
                    unitClick[ii][i].addMouseMotionListener(new MyListener(MyListener.Compo.CreatureInMyPlay, i));
                    unitClick[ii][i].tapClick.addMouseListener(new MyListener(MyListener.Compo.CreatureInMyPlayTap, i));
                } else {
                    unitClick[ii][i].addMouseListener(new MyListener(MyListener.Compo.EnemyUnitInPlay, i));
                    unitClick[ii][i].addMouseMotionListener(new MyListener(MyListener.Compo.EnemyUnitInPlay, i));
                }
            }
        }

        for (int i = 0; i < choiceXLabel.length; i++) {
            choiceXLabel[i] = new JLabel();
            choiceXLabel[i].setHorizontalAlignment(SwingConstants.CENTER);
            choiceXLabel[i].setVerticalAlignment(SwingConstants.CENTER);
            choiceXLabel[i].setForeground(Color.BLACK);
            choiceXLabel[i].setFont(new Font("Courier", Font.BOLD, 24));
            choiceXLabel[i].setText(String.valueOf(i));
            choiceXLabel[i].addMouseListener(new MyListener(MyListener.Compo.ChoiseX, i));
            viewField.add(choiceXLabel[i], 0);
            choiceXLabel[i].setVisible(false);
        }


        for (int i = 0; i < 40; i++) {
            searchXLabel[i] = new JLabel();
            searchXLabel[i].addMouseListener(new MyListener(MyListener.Compo.SearchX, i));
            searchXLabel[i].addMouseMotionListener(new MyListener(MyListener.Compo.SearchX, i));

            viewField.add(searchXLabel[i], 0);
            searchXLabel[i].setVisible(false);
        }

        viewField.add(endTurnClick, 0);
        //
        viewField.add(battlegroundClick, -1);

        viewField.validate();
        main.add(viewField);

        gameLog.setAutoscrolls(true);
        Border border = LineBorder.createGrayLineBorder();
        gameLog.setBorder(border);

        main.setVisible(true);
    }

    private static class MyListener extends MouseInputAdapter {
        Compo onWhat;
        int num;

        MyListener(Compo _compo, int _code) {
            onWhat = _compo;
            num = _code;
        }

        public void mouseMoved(MouseEvent e) {
            if (isYouDraggedCard) {
                main.repaint();
            }
            //System.out.println("Mouse moved at "+ e.getX());
        }

        public void mouseClicked(MouseEvent e) {
         if (onWhat == Compo.DeckChoice && isMyTurn == PlayerStatus.prepareForBattle) {
                try {
                    if (!enterNameFieled.getText().equals("") && !enterNameFieled.getText().equals(" "))
                        runGame(enterNameFieled.getText(), decksChoice.get(num).substring(0, decksChoice.get(num).length() - 4), null);
                    else {
                        //TODO Show message - enter correct name
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } else if (onWhat == Compo.Settings) {
                if (!menuClick.isVisible()) {
                    menuClick.setVisible(true);
                    fullScreenClick.setVisible(true);
                } else {
                    menuClick.setVisible(false);
                    fullScreenClick.setVisible(false);
                }
            } else if (onWhat == Compo.Menu) {
                System.out.println("$DISCONNECT");
                Client.writeLine("$DISCONNECT");
                writerToLog.close();
                System.exit(0);
            } else if (onWhat == Compo.Fullscreen) {
                menuClick.setVisible(false);
                fullScreenClick.setVisible(false);
                if (main.isResizable()) {
                    //FULL SCREEN
                    main.dispose();
                    main.setUndecorated(true);
                    main.setAlwaysOnTop(true);
                    main.setResizable(false);
                    Toolkit tk = Toolkit.getDefaultToolkit();
                    int xsize = (int) tk.getScreenSize().getWidth();
                    int ysize = (int) tk.getScreenSize().getHeight();
                    main.setLocation(0, 0);
                    main.setSize(xsize, ysize);
                    B0RDER_BOTTOM = 10;
                    main.setVisible(true);
                } else {
                    main.dispose();
                    main.setUndecorated(false);
                    main.setAlwaysOnTop(false);
                    main.setLocation(477, 0);
                    main.setSize(890, 688);
                    main.setResizable(true);
                    B0RDER_BOTTOM = 40;
                    main.setVisible(true);
                }
            } else if ((onWhat == Compo.CardInHand) && (isMyTurn == PlayerStatus.MuliganPhase)) {
                if (wantToMulligan[num]) wantToMulligan[num] = false;
                else wantToMulligan[num] = true;
            } else if ((onWhat == Compo.EndTurnButton) && (isMyTurn == PlayerStatus.MyTurn)) {
                Client.writeLine("$ENDTURN(" + players[0].playerName + ")");
                main.repaint();
            } else if ((onWhat == Compo.PlayerHero) && (isMyTurn == PlayerStatus.MyTurn)) {
                //My hero ability
                if (!players[0].isTapped) {
                    if (players[0].text.contains("ТАПТ:")) {
                        //Check available target
                        boolean canTarget = false;
                        if (Board.creature.get(0).size() > 0 && MyFunction.canTarget(MyFunction.Target.myCreature, players[0].tapTargetType))
                            canTarget = true;
                        if (Board.creature.get(1).size() > 0 && MyFunction.canTarget(MyFunction.Target.enemyCreature, players[0].tapTargetType))
                            canTarget = true;

                        if (canTarget) {
                            int cost = MyFunction.getNumericAfterText(players[0].text, "ТАПТ:");
                            System.out.println("hero ability cost = " + cost);
                            if (players[0].untappedCoin >= cost) {
                                isMyTurn = PlayerStatus.choiceTarget;
                                ActivatedAbility.whatAbility = heroAbility;
                                ActivatedAbility.heroAbilityCost = cost;
                                main.repaint();
                            } else {
                                printToView(0, "Недостаточно монет.");
                            }
                        } else {
                            printToView(0, "Нет подходящих целей.");
                        }
                    } else if (players[0].text.contains("ТАП:")) {
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
            } else if ((onWhat == Compo.EndTurnButton) && (isMyTurn == PlayerStatus.MuliganPhase)) {
                //TODO when remake server
                String ms = "$MULLIGANEND(" + players[0].playerName + ",";
                int n = 0;
                String c = "";
                int i = 0;
                for (Boolean b : wantToMulligan) {
                    if (b) {
                        n++;
                        c = c + "," + players[0].cardInHand.get(i).name;
                    }
                    i++;
                }
                ms = ms + n + c + ")";
                Client.writeLine(ms);
                isMyTurn = PlayerStatus.waitingMulligan;
            } else if ((onWhat == Compo.CardInHand) && (isMyTurn == PlayerStatus.choiceTarget) && ActivatedAbility.isThatAbility(toHandAbility)) {
                //to my hand ability
                Client.writeLine("$DISCARD(" + players[0].playerName + "," + num + ")");
                //  massCryCheckAndSetPlayerStatus(0);
            } else if ((onWhat == Compo.PlayerHero) && (isMyTurn == PlayerStatus.choiceTarget) && ActivatedAbility.isNothingOrDeath()) {
                //Battlecry, deathrattle or TAPT on my hero
                if (MyFunction.canTarget(MyFunction.Target.myPlayer, ActivatedAbility.creature.targetType) || MyFunction.canTarget(MyFunction.Target.myPlayer, ActivatedAbility.creature.tapTargetType)) {
                    int nc = Board.creature.get(0).indexOf(ActivatedAbility.creature);
                    if (ActivatedAbility.creatureTap) {
                        System.out.println("$TAPTARGET(" + players[0].playerName + "," + nc + ",0,-1)");
                        Client.writeLine("$TAPTARGET(" + players[0].playerName + "," + nc + ",0,-1)");
                    } else {
                        System.out.println("$CRYTARGET(" + players[0].playerName + "," + nc + ",0,-1)");
                        Client.writeLine("$CRYTARGET(" + players[0].playerName + "," + nc + ",0,-1)");
                    }
                    ActivatedAbility.creatureTap = false;
                } else {
                    printToView(0, "Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.EnemyHero) && (isMyTurn == PlayerStatus.choiceTarget) && ActivatedAbility.isNothingOrDeath()) {
                //Battlecry or TAPT on enemy hero
                if (MyFunction.canTarget(MyFunction.Target.enemyPlayer, ActivatedAbility.creature.targetType) || MyFunction.canTarget(MyFunction.Target.enemyPlayer, ActivatedAbility.creature.tapTargetType)) {
                    if ((players[1].bbshield) && (ActivatedAbility.creature.text.contains("Выстрел")))
                        players[1].bbshield = false;
                    int nc = Board.creature.get(0).indexOf(ActivatedAbility.creature);
                    if (ActivatedAbility.creatureTap) {
                        Client.writeLine("$TAPTARGET(" + players[0].playerName + "," + nc + ",1,-1)");
                    } else {
                        Client.writeLine("$CRYTARGET(" + players[0].playerName + "," + nc + ",1,-1)");
                    }
                    ActivatedAbility.creatureTap = false;
                } else {
                    printToView(0, "Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.CreatureInMyPlay) && (isMyTurn == PlayerStatus.choiceTarget) && ActivatedAbility.isNothingOrDeath()) {
                //Battlecry or TAPT on my unit
                if (MyFunction.canTarget(MyFunction.Target.myCreature, ActivatedAbility.creature.targetType) || MyFunction.canTarget(MyFunction.Target.myCreature, ActivatedAbility.creature.tapTargetType)) {
                    int nc = Board.creature.get(0).indexOf(ActivatedAbility.creature);
                    if ((ActivatedAbility.creature.targetType == 10 || ActivatedAbility.creature.targetType == 12) && nc == num) {
                        printToView(0, "Существо не может целить само себя.");
                    } else {
                        if (ActivatedAbility.creatureTap) {
                            System.out.println("$TAPTARGET(" + players[0].playerName + "," + nc + ",0," + num + ")");
                            Client.writeLine("$TAPTARGET(" + players[0].playerName + "," + nc + ",0," + num + ")");
                        } else {
                            System.out.println("$CRYTARGET(" + players[0].playerName + "," + nc + ",0," + num + ")");
                            Client.writeLine("$CRYTARGET(" + players[0].playerName + "," + nc + ",0," + num + ")");
                        }
                        ActivatedAbility.creatureTap = false;
                    }
                } else {
                    printToView(0, "Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.EnemyUnitInPlay) && (isMyTurn == PlayerStatus.choiceTarget) && ActivatedAbility.isNothingOrDeath()) {
                //Battlecry or TAPT on enemy unit
                if (MyFunction.canTarget(MyFunction.Target.enemyCreature, ActivatedAbility.creature.targetType) || MyFunction.canTarget(MyFunction.Target.enemyCreature, ActivatedAbility.creature.tapTargetType)) {
                    //Bjornbon check attack or not this cry or tap.
                    if ((players[1].bbshield) && (ActivatedAbility.creature.text.contains("Выстрел"))) {
                        printToView(0, "Целью первой атаки должен быть Бьорнбон.");
                    } else {
                        int nc = Board.creature.get(0).indexOf(ActivatedAbility.creature);
                        //Check correct target or it not able?
                        if (ActivatedAbility.creatureTap) {
                            Client.writeLine("$TAPTARGET(" + players[0].playerName + "," + nc + ",1," + num + ")");
                        } else {
                            Client.writeLine("$CRYTARGET(" + players[0].playerName + "," + nc + ",1," + num + ")");
                        }
                        ActivatedAbility.creatureTap = false;
                    }
                } else {
                    printToView(0, "Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.EnemyUnitInPlay) && (isMyTurn == PlayerStatus.choiceTarget) && ActivatedAbility.isThatAbility(heroAbility)) {
                //Hero ability on enemy unit
                if ((players[0].tapTargetType == 1) || (players[0].tapTargetType == 3)) {
                    System.out.println("$HEROTARGET(" + players[0].playerName + ",1," + num + "," + ActivatedAbility.heroAbilityCost + ")");
                    Client.writeLine("$HEROTARGET(" + players[0].playerName + ",1," + num + "," + ActivatedAbility.heroAbilityCost + ")");
                    ActivatedAbility.whatAbility = nothing;
                } else {
                    printToView(0, "Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.CreatureInMyPlay) && (isMyTurn == PlayerStatus.choiceTarget) && (ActivatedAbility.isThatAbility(heroAbility))) {
                //Hero ability on my unit
                if (MyFunction.canTarget(MyFunction.Target.myCreature, players[0].tapTargetType)) {
                    Client.writeLine("$HEROTARGET(" + players[0].playerName + ",0," + num + "," + ActivatedAbility.heroAbilityCost + ")");
                    ActivatedAbility.whatAbility = nothing;
                } else {
                    printToView(0, "Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.CreatureInMyPlay) && (isMyTurn == PlayerStatus.choiceTarget) && (ActivatedAbility.isThatAbility(weaponAbility))) {
                //Weapon ability on my unit
                if ((players[0].equpiment[2].tapTargetType == 1) || (players[0].equpiment[2].tapTargetType == 3)) {
                    System.out.println("$EQUIPTARGET(" + players[0].playerName + ",2,0," + num + ")");
                    Client.writeLine("$EQUIPTARGET(" + players[0].playerName + ",2,0," + num + ")");
                    ActivatedAbility.whatAbility = nothing;
                } else {
                    printToView(0, "Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.CreatureInMyPlayTap) && (isMyTurn == PlayerStatus.MyTurn) && (Board.creature.get(0).get(num).text.contains("ТАПТ:"))) {
                //TAP creature with target ability - first step
                if (!Board.creature.get(0).get(num).getIsSummonedJust()) {
                    if (!Board.creature.get(0).get(num).isTapped) {
                        System.out.println("tapt ability.");
                        isMyTurn = PlayerStatus.choiceTarget;
                        ActivatedAbility.creature = Board.creature.get(0).get(num);
                        ActivatedAbility.creature.targetType = Board.creature.get(0).get(num).targetType;
                        ActivatedAbility.creature.tapTargetType = Board.creature.get(0).get(num).tapTargetType;
                        ActivatedAbility.creatureTap = true;
                        main.repaint();
                    } else {
                        printToView(0, "Повернутое существо не может это сделать.");
                    }
                } else {
                    printToView(0, "Это существо недавно вошло в игру.");
                }
            } else if ((onWhat == Compo.CreatureInMyPlayTap) && (isMyTurn == PlayerStatus.MyTurn) && (Board.creature.get(0).get(num).text.contains("ТАП:"))) {
                //TAP creature with no target ability - first step
                if (!Board.creature.get(0).get(num).getIsSummonedJust()) {
                    if (!Board.creature.get(0).get(num).isTapped) {
                        Client.writeLine("$TAPNOTARGET(" + players[0].playerName + "," + num + ")");
                    } else {
                        printToView(0, "Повернутое существо не может это сделать.");
                    }
                } else {
                    printToView(0, "Это существо недавно вошло в игру.");
                }
            } else if (onWhat == Compo.Weapon && isMyTurn == PlayerStatus.MyTurn && players[0].equpiment[2].text.contains("ТАПТ:")) {
                //TAP weapon with target ability - first step
                if (!players[0].equpiment[2].isTapped) {
                    System.out.println("tapt weapon ability.");
                    if ((players[0].equpiment[2].tapTargetType == 1) && (Board.creature.get(0).isEmpty()) && (Board.creature.get(1).isEmpty())) {
                        printToView(0, "Нет подходящей цели.");
                    } else {
                        isMyTurn = PlayerStatus.choiceTarget;
                        ActivatedAbility.creature = new Creature(Card.simpleCard, players[0]);//
                        ActivatedAbility.creature.targetType = players[0].equpiment[2].targetType;
                        ActivatedAbility.creature.tapTargetType = players[0].equpiment[2].tapTargetType;
                        ActivatedAbility.whatAbility = weaponAbility;
                        main.repaint();
                    }
                } else {
                    printToView(0, "Повернутое оружие не может это сделать.");
                }
            } else if ((onWhat == Compo.EndTurnButton) && (isMyTurn == PlayerStatus.IChoiceBlocker)) {
                System.out.println("$BLOCKER(" + players[0].playerName + "," + creatureWhoAttack + "," + creatureWhoAttackTarget + "," + "-1,0)");
                Client.writeLine("$BLOCKER(" + players[0].playerName + "," + creatureWhoAttack + "," + creatureWhoAttackTarget + "," + "-1,0)");
            } else if ((onWhat == Compo.CreatureInMyPlay) && (isMyTurn == PlayerStatus.IChoiceBlocker)) {
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
            } else if ((onWhat == Compo.ChoiseX) && (isMyTurn == PlayerStatus.choiseX)) {
                isMyTurn = PlayerStatus.MyTurn;
                main.repaint();
                releaseCardWithX(num);
            } else if ((onWhat == Compo.SearchX) && (isMyTurn == PlayerStatus.searchX)) {
                isMyTurn = PlayerStatus.MyTurn;
                main.repaint();
                System.out.println("$FOUND(" + players[0].playerName + "," + founded.get(num).name + ")");
                Client.writeLine("$FOUND(" + players[0].playerName + "," + founded.get(num).name + ")");
            } else if ((onWhat == Compo.SearchX) && (isMyTurn == PlayerStatus.digX)) {
                isMyTurn = PlayerStatus.MyTurn;
                main.repaint();
                System.out.println("$DIGFOUND(" + players[0].playerName + "," + founded.get(num).name + ")");
                Client.writeLine("$DIGFOUND(" + players[0].playerName + "," + founded.get(num).name + ")");
            } else if ((onWhat == Compo.PlayerGraveyard)) {
                if (isShowGraveyard != 0) isShowGraveyard = 0;
                else isShowGraveyard = -1;
                main.repaint();
            } else if ((onWhat == Compo.EnemyGraveyard)) {
                System.out.println("Click on graveyard");
                if (isShowGraveyard != 1) isShowGraveyard = 1;
                else isShowGraveyard = -1;
                main.repaint();
            }
        }

        public void mousePressed(MouseEvent e) {
            // you may not need this method
            //System.out.println("pressed");
            // hilightMyCard = -1;
        }

        public void mouseEntered(MouseEvent event) {
            //System.out.println(onWhat.toString());
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
            if (onWhat == Compo.EnemyUnitInPlay) {
                hilightEnemyCreature = num;
                main.repaint();
            }
            //printToView(0,whereMyMouse);
        }

        public void mouseExited(MouseEvent event) {
            whereMyMouse = "";
            whereMyMouseNum = 0;
            hilightMyCreature = -1;
            hilightEnemyCreature = -1;
            hilightMyCard = -1;
            main.repaint();
        }

        public void mouseReleased(MouseEvent e) {
            isYouDraggedCard = false;
            isYouDraggedAttackCreature = false;
            if (isMyTurn == PlayerStatus.MyTurn) {
                if ((whereMyMouse == Compo.Board.toString()) && (cardMem != null)) {
                    //put creature on board
                    if ((cardMem.targetType == 0) || (cardMem.type == 2)) {
                        if (cardMem.text.contains("Доплатите Х *")) {
                            //TODO If X==0
                            isMyTurn = PlayerStatus.choiseX;
                            //choiseXnum = num;
                            choiceXtext = "$PLAYWITHX(" + players[0].playerName + "," + players[0].cardInHand.get(num).name + "," + num + ",-1,-1";
                            main.repaint();
                        } else {
                            Client.writeLine("$PLAYCARD(" + players[0].playerName + "," + players[0].cardInHand.get(num).name + "," + num + ",-1,-1)");
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
                        Client.writeLine("$PLAYCARD(" + players[0].playerName + "," + players[0].cardInHand.get(num).name + "," + num + ",-1," + players[1].playerName + ")");
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
                                isMyTurn = PlayerStatus.choiseX;
                                //choiseXnum = num;
                                choiceXtext = "$PLAYWITHX(" + players[0].playerName + "," + players[0].cardInHand.get(num).name + "," + num + "," + whereMyMouseNum + "," + players[0].playerName;
                                main.repaint();
                            } else {
                                Client.writeLine("$PLAYCARD(" + players[0].playerName + "," + players[0].cardInHand.get(num).name + "," + num + "," + whereMyMouseNum + "," + players[0].playerName + ")");
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
                                isMyTurn = PlayerStatus.choiseX;
                                //choiseXnum = num;
                                choiceXtext = "$PLAYWITHX(" + players[0].playerName + "," + players[0].cardInHand.get(num).name + "," + num + "," + whereMyMouseNum + "," + players[1].playerName;
                                main.repaint();
                            } else {
                                Client.writeLine("$PLAYCARD(" + players[0].playerName + "," + players[0].cardInHand.get(num).name + "," + num + "," + whereMyMouseNum + "," + players[1].playerName + ")");
                            }
                        } else {
                            printToView(0, "Некорректная цель для данного заклинания, выберите героя.");
                        }
                    }
                }
            } else {
                if (isMyTurn == PlayerStatus.EnemyTurn || isMyTurn == PlayerStatus.EnemyChoiceBlocker || isMyTurn == PlayerStatus.EnemyChoiceTarget) {
                    printToView(0, "Сейчас идет не ваш ход.");
                }
                main.repaint();
            }
            cardMem = null;
            creatureMem = null;
            main.repaint();
        }

        public void mouseDragged(MouseEvent e) {
            hilightMyCard = -1;

            if (onWhat == Compo.CardInHand && isMyTurn == PlayerStatus.MyTurn) {//Creature in hand
                cardMem = players[0].cardInHand.get(num);
                isYouDraggedCard = true;
            } else if (onWhat == Compo.CreatureInMyPlay && isMyTurn == PlayerStatus.MyTurn) {//Creature in play
                creatureMem = Board.creature.get(0).get(num);
                isYouDraggedAttackCreature = true;
            }

            main.repaint();
        }

        enum Compo {Deck, CardInHand, CreatureInMyPlay, Board, EnemyHero, PlayerHero, EnemyUnitInPlay, ChoiseX, SearchX, Weapon, Menu, EndTurnButton, Fullscreen, Settings, DeckChoice, PlayerGraveyard, CreatureInMyPlayTap, EnemyGraveyard}
    }

    static class ViewField extends JPanel {
        ViewField() {
            super();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            onRepaint(g);//its too slow!! TODO repaint not many time
        }

    }

    private static class MessageToShow {
        String message;
        long whenAdd;
        long lenght;
        Color c;

        MessageToShow(String _message, Color _c, long _lenght) {
            Calendar cal = Calendar.getInstance();
            message = _message;
            whenAdd = 0;
            lenght = _lenght;
            c = _c;
        }

        MessageToShow(String _message, long _lenght) {
            Calendar cal = Calendar.getInstance();
            message = _message;
            whenAdd = 0;
            lenght = _lenght;
        }

    }
}
