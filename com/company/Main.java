package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Main extends JFrame {
    private static int serverPort = 6666;
    private static final String address = "127.0.0.1";

    private static final int B0RDER_RIGHT = 10;
    private static final int B0RDER_LEFT = 10;
    private static final int B0RDER_BOTTOM = 40;
    private static final int B0RDER_TOP = 10;
    private static final int B0RDER_BETWEEN = 5;
    private static final double HERO_DAMAGE_WHERE_TO_SHOW_X = 0.3;
    private static final double HERO_DAMAGE_WHERE_TO_SHOW_Y = 0.3;
    private static final double CREATURE_DAMAGE_WHERE_TO_SHOW_X = 0.3;
    private static final double CREATURE_DAMAGE_WHERE_TO_SHOW_Y = 0.3;
    private static final double CARD_SIZE_FROM_SCREEN = 0.09;
    private static Main main = new Main();
    private static int heroW;// = (int) (main.getWidth() * CARD_SIZE_FROM_SCREEN);
    private static int heroH;// = (heroW * 400 / 283);
    private static int smallCardW;// = (int) (heroW * 0.7);
    private static int smallCardH;// = (int) (heroH * 0.7);
    private static int cardX;// = B0RDER_LEFT + B0RDER_BETWEEN * 3 + smallCardW * 3;

    private static ViewField viewField = new ViewField();
    private static JLabel deckClick = new JLabel();
    private static JLabel cardClick[] = new JLabel[9];
    private static JLabel playerUnitClick[][] = new JLabel[2][9];
    //   private static JLabel playerUnitLabel[][] = new JLabel[2][9];
    private static JLabel battlegroundClick = new JLabel();
    private static JLabel enemyHeroClick = new JLabel();
    private static JLabel playerHeroClick = new JLabel();
    private static JLabel playerCoinLabel = new JLabel();
    private static JLabel enemyCoinLabel = new JLabel();
    private static JLabel playerGraveyardClick = new JLabel();
    private static JLabel enemyGraveyardClick = new JLabel();
    //   private static JLabel playerDamageLabel = new JLabel();
//    private static JLabel enemyDamageLabel = new JLabel();
    private static JLabel gameLog = new JLabel();
    private static JLabel endTurnClick = new JLabel();

    private static Image background;
    private static Image heroImage;
    private static Image enemyImage;
    private static Image heroCoinImage;
    private static Image heroDeckImage;
    private static Image endTurnImage;
    private static Image redcrossImage;
    private static Image heroGraveyardImage;

    //  private static Board board;
    //   private static Player player;
    public static Player[] players = new Player[2];
    //  private static Player enemy;

    private static Card cardMem;
    private static Creature creatureMem;

    private static Deck simpleDeck;
    private static Deck simpleEnemyDeck;

    private static String whereMyMouse;
    private static int whereMyMouseNum;
    private static int repainted;
    private static int coinStart = 0;
    //private static boolean isResized = true;

    enum playerStatus {MyTurn, EnemyTurn, IChoiseBlocker, EnemyChoiseBlocker, MuliganPhase, waitingForConnection, waitOtherPlayer, waitingMulligan, choiseTarget}

    public static Creature activatedAbilityCreature;

    public static playerStatus isMyTurn = playerStatus.waitingForConnection;
    public static boolean wantToMulligan[] = new boolean[4];
    static int creatureWhoAttack;
    static int creatureWhoAttackTarget;

    public static void main(String[] args) throws IOException {
        loadImage();
        main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setInitialProperties();

        //  board = new Board();
        //  Board.firstPlayer = players[0];
        //  Board.secondPlayer = enemy;
        //  Board.secondPlayer = players[1];

        String par1 = "PlayerName";
        String par2 = "defaultDeck";

        for (int i = 0; i < args.length; i++) {
            if (i == 1) par1 = args[i];
            if (i == 2) par2 = args[i];
            if (i == 0) serverPort = Integer.parseInt(args[i]);
            if (i == 3) coinStart = Integer.parseInt(args[i]);
        }

        simpleDeck = new Deck(par2);
        simpleEnemyDeck = new Deck("defaultDeck");
        loadDeck(simpleDeck, par2);
        simpleDeck.suffleDeck(19);
        //TODO Load hero?
        players[0] = new Player(simpleDeck, par1, 0, 30);
        //players[0] = new Player(simpleDeck, par1, 0, 30);
        //   enemy = new Player(simpleEnemyDeck, "PlayerName", 1, 30);
        players[1] = new Player(simpleDeck, par1, 0, 30);
        //   Board.firstPlayer = players[0];
        //   Board.secondPlayer = players[1];

        System.out.println("Game start.");
        main.setLocation(477, 0);
        main.setSize(890, 688);
        //FULL SCREEN
        //  GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //  main.setMaximizedBounds(env.getMaximumWindowBounds());
        //  main.setExtendedState(main.getExtendedState() | main.MAXIMIZED_BOTH);
        viewField.setVisible(true);

        try {
            Client.connect(serverPort, address);
            isMyTurn = playerStatus.waitOtherPlayer;
        } catch (Exception x) {
            System.out.println("Cloud not connect to server.");
        }

        Main.gameLog.setText("<html>");
        printToView("Player=" + players[0].playerName + ",port=" + serverPort);

        System.out.println("$IAM(" + players[0].playerName + "," + players[0].deck.name + ")");
        Client.writeLine("$IAM(" + players[0].playerName + "," + players[0].deck.name + ")");

        cycleServerRead();
    }

    private static void cycleServerRead() throws IOException {
        while (true) {
            String fromServer = Client.readLine();
            if (fromServer != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.contains("$DISCONNECT")) {
                    System.out.println("Disconnect");
                    printToView("Разрыв соединения!");
                    System.exit(1);
                    break;
                } else if (fromServer.contains("$OPPONENTCONNECTED")) {//All player connected
                    ArrayList<String> parameter = Card.getTextBetween(fromServer);
                    players[1] = new Player(simpleEnemyDeck, parameter.get(0), 1, 30);
                    loadDeck(simpleEnemyDeck, parameter.get(1));
                    simpleEnemyDeck.suffleDeck(19);
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
                        main.repaint();
                    }
                } else if (fromServer.contains("$MULLIGANEND(")) {
                    ArrayList<String> parameter = Card.getTextBetween(fromServer);
                    int pl = Board.getPl(parameter.get(0));
                    for (int i = 0; i <= 3; i++) {
                        if (Integer.parseInt(parameter.get(i + 1)) == 1) {
                            players[pl].deck.putOnBottomDeck(players[pl].cardInHand.get(i));
                            players[pl].cardInHand.remove(i);
                            players[pl].drawCard();
                        }
                    }
                    main.repaint();
                } else if (fromServer.contains("$DRAWCARD(")) {
                    ArrayList<String> parameter = Card.getTextBetween(fromServer);
                    int pl = Board.getPl(parameter.get(0));
                    //  System.out.println("Draw Card " + parameter.get(0));
                    players[pl].drawCard();
                } else if (fromServer.contains("$ENDTURN(")) {
                    ArrayList<String> parameter = Card.getTextBetween(fromServer);
                    System.out.println("End turn " + parameter.get(0));
                    if (players[0].playerName.equals(parameter.get(0))) {
                        isMyTurn = playerStatus.EnemyTurn;
                        players[0].endTurn();
                    } else if (players[1].playerName.equals(parameter.get(0))) {
                        isMyTurn = playerStatus.MyTurn;
                        players[1].endTurn();
                    }
                } else if (fromServer.contains("$NEWTURN(")) {
                    ArrayList<String> parameter = Card.getTextBetween(fromServer);
                    main.repaint();
                    if (players[0].playerName.equals(parameter.get(0))) {
                        isMyTurn = playerStatus.MyTurn;
                        players[0].newTurn();
                    } else if (players[1].playerName.equals(parameter.get(0))) {
                        isMyTurn = playerStatus.EnemyTurn;
                        players[1].newTurn();
                    }
                } else if (fromServer.contains("$CHOISEBLOCKER(")) {
                    ArrayList<String> parameter = Card.getTextBetween(fromServer);
                    if (players[0].playerName.equals(parameter.get(0))) {
                        isMyTurn = playerStatus.IChoiseBlocker;
                        creatureWhoAttack = Integer.parseInt(parameter.get(1));
                        creatureWhoAttackTarget = Integer.parseInt(parameter.get(2));
                    }
                } else if (fromServer.contains("$CRYTARGET(")) {
                    ArrayList<String> parameter = Card.getTextBetween(fromServer);
                    int pl = Board.getPl(parameter.get(0));
                    int apl = (pl == 0) ? 1 : 0;
                    isMyTurn = playerStatus.MyTurn;
                    if (parameter.get(2).equals("1")) {
                        if (parameter.get(3).equals("-1")) {
                            Board.creature.get(pl).get(Integer.parseInt(parameter.get(1))).cry(null, players[apl]);
                        } else {
                            Board.creature.get(pl).get(Integer.parseInt(parameter.get(1))).cry(Board.creature.get(apl).get(Integer.parseInt(parameter.get(3))), null);
                        }
                    } else {
                        if (parameter.get(3).equals("-1")) {
                            Board.creature.get(pl).get(Integer.parseInt(parameter.get(1))).cry(null, players[pl]);
                        } else {
                            Board.creature.get(pl).get(Integer.parseInt(parameter.get(1))).cry(Board.creature.get(pl).get(Integer.parseInt(parameter.get(3))), null);
                        }
                    }
                } else if (fromServer.contains("$BLOCKER(")) {
                    ArrayList<String> parameter = Card.getTextBetween(fromServer);
                    int pl = Board.getPl(parameter.get(0));
                    int apl = (pl == 0) ? 1 : 0;
                    isMyTurn = playerStatus.EnemyTurn;
                    Creature cr = Board.creature.get(apl).get(Integer.parseInt(parameter.get(1)));
                    if (Integer.parseInt(parameter.get(2)) == -1) {
                        if (Integer.parseInt(parameter.get(3)) == -1) {
                            //Fight with hero
                            cr.fightPlayer(players[pl]);
                        } else {
                            Creature block = Board.creature.get(pl).get(Integer.parseInt(parameter.get(3)));
                            //Fight with bocker
                            cr.fightCreature(block);
                            if (Integer.parseInt(parameter.get(4)) == 1) {
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
                    ArrayList<String> parameter = Card.getTextBetween(fromServer);
                    int pl = Board.getPl(parameter.get(0));
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
                } else if (fromServer.contains("$ATTACKPLAYER(")) {//$ATTACKPLAYER(Player, Creature)
                    ArrayList<String> parameter = Card.getTextBetween(fromServer);
                    int pl = Board.getPl(parameter.get(0));
                    int apl = (pl == 0) ? 1 : 0;
                    Board.creature.get(pl).get(Integer.parseInt(parameter.get(1))).attackPlayer(players[apl]);
                } else if (fromServer.contains("$ATTACKCREATURE(")) {//$ATTACKREATURE(Player, Creature, TargetCreature)
                    ArrayList<String> parameter = Card.getTextBetween(fromServer);
                    int pl = Board.getPl(parameter.get(0));
                    int apl = (pl == 0) ? 1 : 0;
                    Board.creature.get(pl).get(Integer.parseInt(parameter.get(1))).attackCreature(Board.creature.get(apl).get(Integer.parseInt(parameter.get(2))));
                }
            }
        }
    }

    public static void printToView(String txt) {
        Main.gameLog.setText(Main.gameLog.getText() + txt + "<br>");
        if (gameLog.getText().length() > 600)
            Main.gameLog.setText("<html>" + gameLog.getText().substring(gameLog.getText().length() - 600, gameLog.getText().length()));

        // playerCoinLabel.setText(player.untappedCoin + "/" + player.totalCoin);
        // enemyCoinLabel.setText(enemy.untappedCoin + "/" + enemy.totalCoin);
    }

    private static class MyListener extends MouseInputAdapter {
        enum Compo {Deck, CardInHand, CreatureInMyPlay, Board, EnemyHero, PlayerHero, EnemyUnitInPlay, EndTurnButton}

        Compo onWhat;
        int num;

        MyListener(Compo _compo, int _code) {
            onWhat = _compo;
            num = _code;
        }

        public void mouseClicked(MouseEvent e) {
            if (onWhat == Compo.Deck) {
                System.out.println("$DRAWCARD(" + players[0].playerName + ")");
                Client.writeLine("$DRAWCARD(" + players[0].playerName + ")");
            } else if ((onWhat == Compo.CardInHand) && (isMyTurn == playerStatus.MuliganPhase)) {
                if (wantToMulligan[num]) wantToMulligan[num] = false;
                else wantToMulligan[num] = true;
            } else if ((onWhat == Compo.EndTurnButton) && (isMyTurn == playerStatus.MyTurn)) {
                System.out.println("$ENDTURN(" + players[0].playerName + ")");
                Client.writeLine("$ENDTURN(" + players[0].playerName + ")");
            } else if ((onWhat == Compo.EndTurnButton) && (isMyTurn == playerStatus.MuliganPhase)) {
                //TODO when remake server
                //You know nothing, Server!!!
                //I must remake server. It(not client) must hold deck, card and other.
                System.out.println("$MULLIGANEND(" + players[0].playerName + "," + boolToInt(wantToMulligan[0]) + "," + boolToInt(wantToMulligan[1]) + "," + boolToInt(wantToMulligan[2]) + "," + boolToInt(wantToMulligan[3]) + ")");
                Client.writeLine("$MULLIGANEND(" + players[0].playerName + "," + boolToInt(wantToMulligan[0]) + "," + boolToInt(wantToMulligan[1]) + "," + boolToInt(wantToMulligan[2]) + "," + boolToInt(wantToMulligan[3]) + ")");
                isMyTurn = playerStatus.waitingMulligan;
            } else if ((onWhat == Compo.EnemyHero) && (isMyTurn == playerStatus.choiseTarget)) {
                //Battlecry on enemy hero
                if ((activatedAbilityCreature.targetType == 2) || (activatedAbilityCreature.targetType == 3)) {
                    int nc = Board.creature.get(0).indexOf(activatedAbilityCreature);
                    System.out.println("$CRYTARGET(" + players[0].playerName + "," + nc + ",1,-1)");
                    Client.writeLine("$CRYTARGET(" + players[0].playerName + "," + nc + ",1,-1)");
                    isMyTurn = playerStatus.MyTurn;
                } else {
                    printToView("Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.EnemyUnitInPlay) && (isMyTurn == playerStatus.choiseTarget)) {
                //Battlecry on enemy unit
                if ((activatedAbilityCreature.targetType == 1) || (activatedAbilityCreature.targetType == 3)) {
                    int nc = Board.creature.get(0).indexOf(activatedAbilityCreature);
                    System.out.println("$CRYTARGET(" + players[0].playerName + "," + nc + ",1," + num + ")");
                    Client.writeLine("$CRYTARGET(" + players[0].playerName + "," + nc + ",1," + num + ")");
                    isMyTurn = playerStatus.MyTurn;
                } else {
                    printToView("Выберите корректную цель.");
                }
            } else if ((onWhat == Compo.EndTurnButton) && (isMyTurn == playerStatus.IChoiseBlocker)) {
                System.out.println("$BLOCKER(" + players[0].playerName + "," + creatureWhoAttack + "," + creatureWhoAttackTarget + "," + "-1,0)");
                Client.writeLine("$BLOCKER(" + players[0].playerName + "," + creatureWhoAttack + "," + creatureWhoAttackTarget + "," + "-1,0)");
            } else if ((onWhat == Compo.CreatureInMyPlay) && (isMyTurn == playerStatus.IChoiseBlocker)) {
                //TODO can't block
                if (Board.creature.get(0).get(num).isTapped) {
                    printToView("Повернутые существа не могут блокировать.");
                } else if (creatureWhoAttackTarget == num) {
                    System.out.println("$BLOCKER(" + players[0].playerName + "," + creatureWhoAttack + "," + creatureWhoAttackTarget + "," + num + ",0)");
                    Client.writeLine("$BLOCKER(" + players[0].playerName + "," + creatureWhoAttack + "," + creatureWhoAttackTarget + "," + num + ",0)");
                } else {
                    System.out.println("$BLOCKER(" + players[0].playerName + "," + creatureWhoAttack + "," + creatureWhoAttackTarget + "," + num + ",1)");
                    Client.writeLine("$BLOCKER(" + players[0].playerName + "," + creatureWhoAttack + "," + creatureWhoAttackTarget + "," + num + ",1)");
                }
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
            //  printToView(whereMyMouse);
        }

        public void mouseExited(MouseEvent event) {
            whereMyMouse = "";
            whereMyMouseNum = 0;
        }

        public void mouseReleased(MouseEvent e) {
            if (isMyTurn == playerStatus.MyTurn) {
                if ((whereMyMouse == Compo.Board.toString()) && (cardMem != null)) {
                    //put creature on board
                    if ((cardMem.targetType == 0) || (cardMem.type == 2)) {
                        System.out.println("$PLAYCARD(" + players[0].playerName + "," + num + ",-1,-1)");
                        Client.writeLine("$PLAYCARD(" + players[0].playerName + "," + num + ",-1,-1)");
                    } else {
                        printToView("Заклинание требует цели.");
                    }
                } else if ((whereMyMouse == Compo.EnemyHero.toString()) && (creatureMem != null)) {
                    //enemy hero attack by creature
                    if (creatureMem.isTapped) {
                        printToView("Повернутое существо не может атаковать.");
                    } else {
                        if (creatureMem.isSummonedJust) {
                            printToView("Это существо вошло в игру на этом ходу.");
                        } else {
                            System.out.println("$ATTACKPLAYER(" + players[0].playerName + "," + num + ")");
                            Client.writeLine("$ATTACKPLAYER(" + players[0].playerName + "," + num + ")");
                        }
                    }
                } else if ((whereMyMouse == Compo.EnemyUnitInPlay.toString()) && (creatureMem != null)) {
                    //enemy creature attack by player creature
                    if (creatureMem.isTapped) {
                        printToView("Повернутое существо не может атаковать.");
                    } else {
                        if (creatureMem.isSummonedJust) {
                            printToView("Это существо вошло в игру на этом ходу.");
                        } else {
                            System.out.println("$ATTACKCREATURE(" + players[0].playerName + "," + num + "," + whereMyMouseNum + ")");
                            Client.writeLine("$ATTACKCREATURE(" + players[0].playerName + "," + num + "," + whereMyMouseNum + ")");
                        }
                    }
                } else if ((whereMyMouse == Compo.EnemyHero.toString()) && (cardMem != null)) {
                    //enemy hero attack by spell from hand
                    if (cardMem.targetType == 2) {
                        System.out.println("$PLAYCARD(" + players[0].playerName + "," + num + ",-1," + players[1].playerName + ")");
                        Client.writeLine("$PLAYCARD(" + players[0].playerName + "," + num + ",-1," + players[1].playerName + ")");
                    } else {
                        printToView("Некорректная цель для данного заклинания, выберите существо.");
                    }
                } else if ((whereMyMouse == Compo.CreatureInMyPlay.toString()) && (cardMem != null)) {
                    //spell from hand to my creature in play
                    if (Board.creature.get(0).get(whereMyMouseNum).text.contains("Защита от заклинаний.")) {
                        printToView("У цели защита от заклинаний.");
                    } else {
                        if ((cardMem.targetType == 1) || (cardMem.targetType == 3)) {
                            System.out.println("$PLAYCARD(" + players[0].playerName + "," + num + "," + whereMyMouseNum + "," + players[0].playerName + ")");
                            Client.writeLine("$PLAYCARD(" + players[0].playerName + "," + num + "," + whereMyMouseNum + "," + players[0].playerName + ")");
                        } else {
                            printToView("Некорректная цель для данного заклинания, выберите героя.");
                        }
                    }
                } else if ((whereMyMouse == Compo.EnemyUnitInPlay.toString()) && (cardMem != null)) {
                    //spell from hand to enemy creature in play
                    if (Board.creature.get(1).get(whereMyMouseNum).text.contains("Защита от заклинаний.")) {
                        printToView("У цели защита от заклинаний.");
                    } else {
                        if ((cardMem.targetType == 1) || (cardMem.targetType == 3)) {
                            System.out.println("$PLAYCARD(" + players[0].playerName + "," + num + "," + whereMyMouseNum + "," + players[1].playerName + ")");
                            Client.writeLine("$PLAYCARD(" + players[0].playerName + "," + num + "," + whereMyMouseNum + "," + players[1].playerName + ")");
                        } else {
                            printToView("Некорректная цель для данного заклинания, выберите героя.");
                        }
                    }
                }
            } else {
                if (isMyTurn == playerStatus.EnemyTurn) {
                    printToView("Сейчас идет не ваш ход.");
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
        //  System.out.println("onRepaint " + repainted);
        repainted++;
        int bigCardW = (int) (main.getWidth() * CARD_SIZE_FROM_SCREEN * 1.5);
        int bigCardH = (bigCardW * 400 / 283);
        heroW = (int) (main.getWidth() * CARD_SIZE_FROM_SCREEN);
        heroH = (heroW * 400 / 283);
        smallCardW = (int) (heroW * 0.7);
        smallCardH = (int) (heroH * 0.7);
        cardX = B0RDER_LEFT + B0RDER_BETWEEN * 3 + smallCardW * 3;
        BufferedImage im;
        int numCardInHand = 0;
        //Game log
        gameLog.setLocation(B0RDER_LEFT, B0RDER_TOP + smallCardH + B0RDER_BETWEEN);
        gameLog.setSize(cardX - B0RDER_BETWEEN - B0RDER_LEFT, main.getHeight() - B0RDER_BOTTOM - B0RDER_BETWEEN * 2 - B0RDER_TOP - smallCardH * 2);
        //Background
        g.drawImage(background, 0, 0, main.getWidth(), main.getHeight(), null);
        //Battleground
        battlegroundClick.setLocation(cardX, B0RDER_TOP + B0RDER_BETWEEN + smallCardH);
        battlegroundClick.setSize(main.getWidth() - B0RDER_RIGHT - cardX - heroW - B0RDER_BETWEEN, main.getHeight() - B0RDER_BOTTOM - B0RDER_BETWEEN * 2 - B0RDER_TOP - heroH - smallCardH);
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
        //Heroes
        g.drawImage(heroImage, main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() - heroH - B0RDER_BOTTOM, heroW, heroH, null);
        g.drawImage(enemyImage, main.getWidth() - heroW - B0RDER_RIGHT, B0RDER_TOP, heroW, heroH, null);
        enemyHeroClick.setLocation(main.getWidth() - heroW - B0RDER_RIGHT, B0RDER_TOP);
        enemyHeroClick.setSize(heroW, heroH);
        playerHeroClick.setLocation(main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() - heroH - B0RDER_BOTTOM);
        playerHeroClick.setSize(heroW, heroH);
        // playerDamageLabel.setLocation(playerHeroClick.getX() + (int) (playerHeroClick.getWidth() * HERO_DAMAGE_WHERE_TO_SHOW_X), playerHeroClick.getY() + (int) (playerHeroClick.getHeight() * HERO_DAMAGE_WHERE_TO_SHOW_Y));
        //TODO Draw, not set text, N
        if (players[0].damage != 0) {
            im = ImageIO.read(Main.class.getResourceAsStream("icons/damage/4.png"));
            g.drawImage(im, playerHeroClick.getX() + playerHeroClick.getWidth() / 2 - heroH / 10, playerHeroClick.getY() + (playerHeroClick.getHeight() / 2 - heroH / 10), heroH / 5, heroH / 5, null);
        }
        //playerDamageLabel.setText(player.damage + "");
        //else playerDamageLabel.setText("");
        if (players[1].damage != 0) {
            im = ImageIO.read(Main.class.getResourceAsStream("icons/damage/4.png"));
            g.drawImage(im, enemyHeroClick.getX() + enemyHeroClick.getWidth() / 2 - heroH / 10, enemyHeroClick.getY() + (enemyHeroClick.getHeight() / 2 - heroH / 10), heroH / 5, heroH / 5, null);
        }
        // enemyDamageLabel.setLocation(enemyHeroClick.getX() + (int) (enemyHeroClick.getWidth() * HERO_DAMAGE_WHERE_TO_SHOW_X), enemyHeroClick.getY() + (int) (enemyHeroClick.getHeight() * HERO_DAMAGE_WHERE_TO_SHOW_Y));
        // if (enemy.damage != 0) enemyDamageLabel.setText(enemy.damage + "");
        // else enemyDamageLabel.setText("");
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
            for (int i = 0; i < players[0].cardInHand.size(); i++)
            //for (Card card : player.cardInHand)   // I don't know why, but it create ConcurrentModificationException
            {
                Card card = players[0].cardInHand.get(i);
                if (card.image != null) {
                    try {
                        im = ImageIO.read(Main.class.getResourceAsStream("cards/" + card.image));
                        if (isMyTurn == playerStatus.MuliganPhase) {
                            int tmp = (battlegroundClick.getWidth() - bigCardW * 4) / 5;
                            g.drawImage(im, cardX + (numCardInHand * bigCardW) + ((numCardInHand + 1) * tmp), main.getHeight() / 2 - bigCardH / 2, bigCardW, bigCardH, null);
                            cardClick[numCardInHand].setLocation(cardX + (numCardInHand * bigCardW) + ((numCardInHand + 1) * tmp), main.getHeight() / 2 - bigCardH / 2);
                            cardClick[numCardInHand].setSize(bigCardW, bigCardH);
                            if (wantToMulligan[i]) {
                                g.drawImage(redcrossImage, cardX + (numCardInHand * bigCardW) + ((numCardInHand + 1) * tmp), main.getHeight() / 2 - bigCardH / 2, bigCardW, bigCardH, null);
                            }
                        } else {
                            g.drawImage(im, cardX + (numCardInHand * heroW), main.getHeight() - heroH - B0RDER_BOTTOM, heroW, heroH, null);
                            cardClick[numCardInHand].setLocation(cardX + (numCardInHand * heroW), main.getHeight() - heroH - B0RDER_BOTTOM);
                            cardClick[numCardInHand].setSize(heroW, heroH);
                        }
                        numCardInHand++;
                    } catch (IOException e) {
                        System.out.println("Can't load image.");
                    }
                }
            }
        }
        //Enemy
        im = ImageIO.read(Main.class.getResourceAsStream("icons/Deck.png"));//His card deck up
        if (!players[1].cardInHand.isEmpty()) {
            for (int i = 0; i < players[1].cardInHand.size(); i++) {
                g.drawImage(im, cardX + (int) (i * smallCardW * 0.5), B0RDER_TOP, smallCardW, smallCardH, null);
            }
        }
    }

    private static void drawPlayerCreature(Graphics g, int np) throws IOException {
        int numUnit = 0;
        int h;
        BufferedImage im;
        if (np == 0) h = battlegroundClick.getY() + battlegroundClick.getHeight() - heroH;
        else h = battlegroundClick.getY();

        if (!Board.creature.get(np).isEmpty()) {
            // for (Creature creature : Board.creature.get(np))//Sometimes it make exception after any creature die(((
            for (int i = 0; i < Board.creature.get(np).size(); i++)//{
            {
                if (Board.creature.get(np).get(i).image != null) {
                    try {
                        im = ImageIO.read(Main.class.getResourceAsStream("cards/" + Board.creature.get(np).get(i).image));
                        if (Board.creature.get(np).get(i).isTapped) {
                            g.drawImage(Card.tapImage(im), battlegroundClick.getX() + (int) (numUnit * heroW), h, heroH, heroW, null);
                            playerUnitClick[np][numUnit].setSize(heroH, heroW);
                            playerUnitClick[np][numUnit].setLocation(battlegroundClick.getX() + (int) (numUnit * heroW), h);//May be write not center?
                            //  playerUnitClick[np][numUnit].setIcon(new ImageIcon(tapImage(im)));
                        } else {
                            g.drawImage(im, battlegroundClick.getX() + (int) (numUnit * heroW), h, heroW, heroH, null);
                            playerUnitClick[np][numUnit].setLocation(battlegroundClick.getX() + (int) (numUnit * heroW), h);
                            playerUnitClick[np][numUnit].setSize(heroW, heroH);
//                            playerUnitClick[np][numUnit].setIcon(new ImageIcon(tapImage(im)));
                        }
                        if (Board.creature.get(np).get(i).damage != 0) {
                            //Text call neverending repaint!!!
                            //TODO create image of damage!
                            im = ImageIO.read(Main.class.getResourceAsStream("icons/damage/4.png"));
                            g.drawImage(im, battlegroundClick.getX() + (int) (numUnit * heroW) + heroW / 2 - heroH / 10, h + heroH / 2 - heroH / 10, heroH / 5, heroH / 5, null);
                        }
                        numUnit++;
                    } catch (IOException e) {
                        System.out.println("Can't load image.");
                    }
                }
            }
        }

    }

    private static void loadImage() {
        try {
            background = ImageIO.read(Main.class.getResourceAsStream("Background.jpg"));
            heroImage = ImageIO.read(Main.class.getResourceAsStream("cards/Тарна.jpg"));
            enemyImage = ImageIO.read(Main.class.getResourceAsStream("cards/Тарна.jpg"));
            heroCoinImage = ImageIO.read(Main.class.getResourceAsStream("icons/Coin.png"));
            heroDeckImage = ImageIO.read(Main.class.getResourceAsStream("icons/Deck.png"));
            endTurnImage = ImageIO.read(Main.class.getResourceAsStream("icons/Endturn.png"));
            heroGraveyardImage = ImageIO.read(Main.class.getResourceAsStream("icons/Graveyard.png"));
            redcrossImage = ImageIO.read(Main.class.getResourceAsStream("icons/Bigredcross.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadDeck(Deck deck, String deckName) throws IOException {
        URL path = Main.class.getResource(deckName + ".txt");
        File f = new File(path.getFile());
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), "windows-1251"));
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

    private static void setInitialProperties() {

        Board.creature = new ArrayList<ArrayList<Creature>>(2);
        Board.creature.add(new ArrayList<Creature>());
        Board.creature.add(new ArrayList<Creature>());
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
        //
        viewField.add(battlegroundClick);
        viewField.add(deckClick);
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
            } catch (IOException e) {
                System.out.println("Error in onRepaint.");
                e.printStackTrace();
            }
        }

    }

    private static int boolToInt(boolean b) {
        return b ? 1 : 0;
    }
}
