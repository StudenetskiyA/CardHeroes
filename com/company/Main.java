package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

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
    private static int heroW = (int) (main.getWidth() * CARD_SIZE_FROM_SCREEN);
    private static int heroH = (heroW * 400 / 283);
    private static int smallCardW = (int) (heroW * 0.7);
    private static int smallCardH = (int) (heroH * 0.7);
    private static int cardX = B0RDER_LEFT + B0RDER_BETWEEN * 3 + smallCardW * 3;

    private static ViewField viewField = new ViewField();
    private static JLabel deckClick = new JLabel();
    private static JLabel cardClick[] = new JLabel[9];
    private static JLabel playerUnitClick[][] = new JLabel[2][9];
    private static JLabel playerUnitLabel[][] = new JLabel[2][9];
    private static JLabel battlegroundClick = new JLabel();
    private static JLabel enemyHeroClick = new JLabel();
    private static JLabel playerHeroClick = new JLabel();
    private static JLabel playerCoinLabel = new JLabel();
    private static JLabel enemyCoinLabel = new JLabel();
    private static JLabel playerGraveyardClick = new JLabel();
    private static JLabel enemyGraveyardClick = new JLabel();
    private static JLabel playerDamageLabel = new JLabel();
    private static JLabel enemyDamageLabel = new JLabel();
    private static JLabel gameLog = new JLabel();
    private static JLabel endTurnClick = new JLabel();

    private static Image background;
    private static Image heroImage;
    private static Image enemyImage;
    private static Image heroCoinImage;
    private static Image heroDeckImage;
    private static Image notMyTurnImage;
    private static Image blockTurnImage;
    private static Image blockEnemyTurnImage;
    private static Image endTurnImage;
    private static Image heroGraveyardImage;

    private static Board board;
    private static Player player;
    private static Player enemy;

    private static Card cardMem;
    private static Creature creatureMem;
    private static ArrayList<Card> simpleDeckCards;
    private static ArrayList<Card> simpleDeckCards2;

    private static String whereMyMouse;
    private static int whereMyMouseNum;

    enum playerStatus {MyTurn, EnemyTurn, IChoiseBlocker, EnemyChoiseBlocker}

    public static playerStatus isMyTurn = playerStatus.EnemyTurn;
    static int creatureWhoAttack;
    static int creatureWhoAttackTarget;

    public static void main(String[] args) throws IOException {
        loadImage();
        main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setInitialProperties();
        loadCardDeck();
        board = new Board();

        String par1 = "";
        String par2 = "";

        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
            if (i == 1) par1 = args[i];
            if (i == 2) par2 = args[i];
            if (i == 0) serverPort = Integer.parseInt(args[i]);
        }

        Deck simpleDeck = new Deck(simpleDeckCards);
        Deck simpleEnemyDeck = new Deck(simpleDeckCards2);

        if (par1.equals("Jeremy")) simpleDeck = new Deck(simpleDeckCards);
        if (par1.equals("Bob")) simpleDeck = new Deck(simpleDeckCards2);
        if (par2.equals("Jeremy")) simpleEnemyDeck = new Deck(simpleDeckCards);
        if (par2.equals("Bob")) simpleEnemyDeck = new Deck(simpleDeckCards2);

        player = new Player(simpleDeck, board, par1, 0, 30);
        enemy = new Player(simpleEnemyDeck, board, par2, 1, 30);
        Board.firstPlayer = player;
        Board.secondPlayer = enemy;

        player.untappedCoin = 5;
        player.totalCoin = 5;
        enemy.untappedCoin = 5;
        enemy.totalCoin = 5;

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
        } catch (Exception x) {
            System.out.println("Cloud not connect to server.");
        }

        Main.gameLog.setText("<html>");
        printToView("Player=" + player.playerName + ",port=" + serverPort);

        gameLog.setAutoscrolls(true);

        cycleServerRead();
    }

    private static void cycleServerRead() throws IOException {
        while (true) {
            String fromServer = Client.readLine();
            if (fromServer != null)
                System.out.println("Server: " + fromServer);

            if (fromServer.contains("$DISCONNECT")) {
                System.out.println("Disconnect");
                break;
            } else if (fromServer.contains("$DRAWCARD(")) {
                ArrayList<String> parameter = Card.getTextBetween(fromServer);
                //  System.out.println("Draw Card " + parameter.get(0));
                if (player.playerName.equals(parameter.get(0))) player.drawCard();
                else if (enemy.playerName.equals(parameter.get(0))) enemy.drawCard();
            } else if (fromServer.contains("$ENDTURN(")) {
                ArrayList<String> parameter = Card.getTextBetween(fromServer);
                System.out.println("End turn " + parameter.get(0));
                if (player.playerName.equals(parameter.get(0))) {
                    isMyTurn = playerStatus.EnemyTurn;
                    enemy.newTurn();
                } else if (enemy.playerName.equals(parameter.get(0))) {
                    isMyTurn = playerStatus.MyTurn;
                    player.newTurn();
                }
            } else if (fromServer.contains("$NEWTURN(")) {
                ArrayList<String> parameter = Card.getTextBetween(fromServer);
                //System.out.println("Draw Card " + parameter.get(0));
                if (player.playerName.equals(parameter.get(0))) {
                    isMyTurn = playerStatus.MyTurn;
                    player.newTurn();
                } else if (enemy.playerName.equals(parameter.get(0))) {
                    isMyTurn = playerStatus.EnemyTurn;
                    enemy.newTurn();
                }
            } else if (fromServer.contains("$CHOISEBLOCKER(")) {
                ArrayList<String> parameter = Card.getTextBetween(fromServer);
                if (player.playerName.equals(parameter.get(0))) {
                    isMyTurn = playerStatus.IChoiseBlocker;
                    creatureWhoAttack = Integer.parseInt(parameter.get(1));
                    creatureWhoAttackTarget = Integer.parseInt(parameter.get(2));
                }
            } else if (fromServer.contains("$BLOCKER(")) {
                ArrayList<String> parameter = Card.getTextBetween(fromServer);
                if (player.playerName.equals(parameter.get(0))) {
                    isMyTurn = playerStatus.EnemyTurn;
                    Creature cr = Board.creature.get(1).get(Integer.parseInt(parameter.get(1)));
                    if (Integer.parseInt(parameter.get(2)) == -1) {
                        if (Integer.parseInt(parameter.get(3)) == -1) {
                            //Fight with hero
                            cr.fightPlayer(player);
                        } else {
                            Creature block = Board.creature.get(0).get(Integer.parseInt(parameter.get(3)));
                            //Fight with bocker
                            cr.fightCreature(block);
                            if (Integer.parseInt(parameter.get(4)) == 1) {
                                block.tapCreature();
                            }
                        }
                    } else {
                        if (Integer.parseInt(parameter.get(3)) == -1) {
                            //Fight with first target
                            Creature block = Board.creature.get(0).get(Integer.parseInt(parameter.get(2)));
                            cr.fightCreature(block);
                        } else {
                            Creature block = Board.creature.get(0).get(Integer.parseInt(parameter.get(3)));
                            //Fight with blocker
                            cr.fightCreature(block);
                            if (Integer.parseInt(parameter.get(4)) == 1) {
                                block.tapCreature();
                            }
                        }
                    }
                } else if (enemy.playerName.equals(parameter.get(0))) {
                    isMyTurn = playerStatus.MyTurn;
                    Creature cr = Board.creature.get(0).get(Integer.parseInt(parameter.get(1)));
                    if (Integer.parseInt(parameter.get(2)) == -1) {
                        if (Integer.parseInt(parameter.get(3)) == -1) {
                            //Fight with hero
                            cr.fightPlayer(enemy);
                        } else {
                            Creature block = Board.creature.get(1).get(Integer.parseInt(parameter.get(3)));
                            //Fight with bocker
                            cr.fightCreature(block);
                            if (Integer.parseInt(parameter.get(4)) == 1) {
                                block.tapCreature();
                            }
                        }
                    } else {
                        if (Integer.parseInt(parameter.get(3)) == -1) {
                            //Fight with first target
                            Creature block = Board.creature.get(1).get(Integer.parseInt(parameter.get(2)));
                            cr.fightCreature(block);
                        } else {
                            Creature block = Board.creature.get(1).get(Integer.parseInt(parameter.get(3)));
                            //Fight with blocker
                            cr.fightCreature(block);
                            if (Integer.parseInt(parameter.get(4)) == 1) {
                                block.tapCreature();
                            }
                        }
                    }
                }
            } else if (fromServer.contains("$PLAYCARD(")) {
                //$PLAYCARD(player, numInHand, targetCreature, targetPlayer[1,2])
                //$PLAYCARD(Jeremy,0,-1,Bob) - play 0 card to enemy.
                //$PLAYCARD(Jeremy,2,-1,-1) - play 2th card to board.
                //$PLAYCARD(Bob,1,1,Jeremy) - play 1th card to 1th creature of Jeremy
                ArrayList<String> parameter = Card.getTextBetween(fromServer);
                if (player.playerName.equals(parameter.get(0))) {
                    if (!parameter.get(2).equals("-1")) {//if card targets creature
                        if ((parameter.get(3).equals(enemy.playerName)))
                            player.playCard(player.cardInHand.get(Integer.parseInt(parameter.get(1))), board.creature.get(1).get(Integer.parseInt(parameter.get(2))), null);
                        else //to self creature
                            player.playCard(player.cardInHand.get(Integer.parseInt(parameter.get(1))), board.creature.get(0).get(Integer.parseInt(parameter.get(2))), null);
                    } else {
                        if (parameter.get(3).equals(enemy.playerName))//enemy
                            player.playCard(player.cardInHand.get(Integer.parseInt(parameter.get(1))), null, enemy);
                        else if (parameter.get(3).equals(player.playerName))//target - self player
                            player.playCard(player.cardInHand.get(Integer.parseInt(parameter.get(1))), null, player);
                        else player.playCard(player.cardInHand.get(Integer.parseInt(parameter.get(1))), null, null);
                    }
                } else if (enemy.playerName.equals(parameter.get(0))) {
                    if (!parameter.get(2).equals("-1")) {
                        if ((parameter.get(3).equals(enemy.playerName)))
                            enemy.playCard(enemy.cardInHand.get(Integer.parseInt(parameter.get(1))), board.creature.get(1).get(Integer.parseInt(parameter.get(2))), null);
                        else
                            enemy.playCard(enemy.cardInHand.get(Integer.parseInt(parameter.get(1))), board.creature.get(0).get(Integer.parseInt(parameter.get(2))), null);
                    } else {
                        if (parameter.get(3).equals(enemy.playerName))//enemy
                            enemy.playCard(enemy.cardInHand.get(Integer.parseInt(parameter.get(1))), null, enemy);
                        else if (parameter.get(3).equals(player.playerName))//target - self player
                            enemy.playCard(enemy.cardInHand.get(Integer.parseInt(parameter.get(1))), null, player);
                        else enemy.playCard(enemy.cardInHand.get(Integer.parseInt(parameter.get(1))), null, null);
                    }
                }
            } else if (fromServer.contains("$ATTACKPLAYER(")) {//$ATTACKPLAYER(Player, Creature)
                ArrayList<String> parameter = Card.getTextBetween(fromServer);
                if (player.playerName.equals(parameter.get(0))) {
                    board.creature.get(0).get(Integer.parseInt(parameter.get(1))).attackPlayer(enemy);
                }
                if (enemy.playerName.equals(parameter.get(0))) {
                    board.creature.get(1).get(Integer.parseInt(parameter.get(1))).attackPlayer(player);
                }
            } else if (fromServer.contains("$ATTACKCREATURE(")) {//$ATTACKREATURE(Player, Creature, TargetCreature)
                ArrayList<String> parameter = Card.getTextBetween(fromServer);
                if (player.playerName.equals(parameter.get(0))) {
                    board.creature.get(0).get(Integer.parseInt(parameter.get(1))).attackCreature(board.creature.get(1).get(Integer.parseInt(parameter.get(2))));
                }
                if (enemy.playerName.equals(parameter.get(0))) {
                    board.creature.get(1).get(Integer.parseInt(parameter.get(1))).attackCreature(board.creature.get(0).get(Integer.parseInt(parameter.get(2))));
                }
            }
        }
    }

    public static void printToView(String txt) {
        Main.gameLog.setText(Main.gameLog.getText() + txt + "<br>");
        if (gameLog.getText().length() > 600)
            Main.gameLog.setText("<html>" + gameLog.getText().substring(gameLog.getText().length() - 600, gameLog.getText().length()));
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
                System.out.println("$DRAWCARD(" + player.playerName + ")");
                Client.writeLine("$DRAWCARD(" + player.playerName + ")");
            }
            if ((onWhat == Compo.EndTurnButton) && (isMyTurn == playerStatus.MyTurn)) {
                System.out.println("$ENDTURN(" + player.playerName + ")");
                Client.writeLine("$ENDTURN(" + player.playerName + ")");
            }
            if ((onWhat == Compo.EndTurnButton) && (isMyTurn == playerStatus.IChoiseBlocker)) {
                System.out.println("$BLOCKER(" + player.playerName +"," +creatureWhoAttack+","+creatureWhoAttackTarget+","+ "-1,0)");
                Client.writeLine("$BLOCKER(" + player.playerName +"," +creatureWhoAttack+","+creatureWhoAttackTarget+","+ "-1,0)");
        }
            if ((onWhat==Compo.CreatureInMyPlay)  && (isMyTurn==playerStatus.IChoiseBlocker)){
                //TODO can't block
                if ( board.creature.get(0).get(num).isTapped){printToView("Повернутые существа не могут блокировать.");}
                else if (creatureWhoAttackTarget==num){
                System.out.println("$BLOCKER(" + player.playerName +"," +creatureWhoAttack+","+creatureWhoAttackTarget+","+num+",0)");
                Client.writeLine("$BLOCKER(" + player.playerName +"," +creatureWhoAttack+","+creatureWhoAttackTarget+","+num+",0)");}
                else {
                    System.out.println("$BLOCKER(" + player.playerName +"," +creatureWhoAttack+","+creatureWhoAttackTarget+","+num+",1)");
                    Client.writeLine("$BLOCKER(" + player.playerName +"," +creatureWhoAttack+","+creatureWhoAttackTarget+","+num+",1)");
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
                    System.out.println("$PLAYCARD(" + player.playerName + "," + num + ",-1,-1)");
                    Client.writeLine("$PLAYCARD(" + player.playerName + "," + num + ",-1,-1)");
                    // player.playCard(cardMem, null, null);
                } else if ((whereMyMouse == Compo.EnemyHero.toString()) && (creatureMem != null)) {
                    //enemy hero attack by creature
                    if (creatureMem.isTapped) {
                        printToView("Повернутое существо не может атаковать.");
                    } else {
                        if (creatureMem.isSummonedJust) {
                            printToView("Это существо вошло в игру на этом ходу.");
                        } else {
                            System.out.println("$ATTACKPLAYER(" + player.playerName + "," + num + ")");
                            Client.writeLine("$ATTACKPLAYER(" + player.playerName + "," + num + ")");
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
                            System.out.println("$ATTACKCREATURE(" + player.playerName + "," + num + "," + whereMyMouseNum + ")");
                            Client.writeLine("$ATTACKCREATURE(" + player.playerName + "," + num + "," + whereMyMouseNum + ")");
                        }
                    }
                } else if ((whereMyMouse == Compo.EnemyHero.toString()) && (cardMem != null)) {
                    //enemy hero attack by spell from hand
                    System.out.println("$PLAYCARD(" + player.playerName + "," + num + ",-1," + enemy.playerName + ")");
                    Client.writeLine("$PLAYCARD(" + player.playerName + "," + num + ",-1," + enemy.playerName + ")");
                } else if ((whereMyMouse == Compo.CreatureInMyPlay.toString()) && (cardMem != null)) {
                    //spell from hand to my creature in play
                    System.out.println("$PLAYCARD(" + player.playerName + "," + num + "," + whereMyMouseNum + "," + player.playerName + ")");
                    Client.writeLine("$PLAYCARD(" + player.playerName + "," + num + "," + whereMyMouseNum + "," + player.playerName + ")");
                } else if ((whereMyMouse == Compo.EnemyUnitInPlay.toString()) && (cardMem != null)) {
                    //spell from hand to enemy creature in play
                    System.out.println("$PLAYCARD(" + player.playerName + "," + num + "," + whereMyMouseNum + "," + enemy.playerName + ")");
                    Client.writeLine("$PLAYCARD(" + player.playerName + "," + num + "," + whereMyMouseNum + "," + enemy.playerName + ")");
                }
            } else {
                printToView("Сейчас идет не ваш ход.");
            }
            cardMem = null;
            creatureMem = null;
        }

        public void mouseDragged(MouseEvent e) {
            if (onWhat == Compo.CardInHand) {//Creature in hand
                cardMem = player.cardInHand.get(num);
            } else if (onWhat == Compo.CreatureInMyPlay) {//Creature in play
                creatureMem = board.creature.get(0).get(num);
            }


        }
    }

    private static BufferedImage tapImage(BufferedImage src) {
        double rotationRequired = Math.toRadians(90);
        AffineTransform tx = new AffineTransform();
        tx.translate(0.5 * src.getHeight(), 0.5 * src.getWidth());
        tx.rotate(rotationRequired);
        tx.translate(-0.5 * src.getWidth(), -0.5 * src.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(src, null);
    }

    private static void onRepaint(Graphics g) throws IOException {
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
        gameLog.setAutoscrolls(true);
        Border border = LineBorder.createGrayLineBorder();
        gameLog.setBorder(border);
        //Background
        g.drawImage(background, 0, 0, main.getWidth(), main.getHeight(), null);
        //Battleground
        battlegroundClick.setLocation(cardX, B0RDER_TOP + B0RDER_BETWEEN + heroH);
        battlegroundClick.setSize(main.getWidth() - B0RDER_RIGHT - cardX - heroW - B0RDER_BETWEEN, main.getHeight() - B0RDER_BOTTOM - B0RDER_BETWEEN * 2 - B0RDER_TOP - heroH * 2);
        g.drawRect(battlegroundClick.getX(), battlegroundClick.getY(), battlegroundClick.getWidth(), battlegroundClick.getHeight());//TODO Image of battleground
        //End turn button
        if (isMyTurn == playerStatus.MyTurn)
            g.drawImage(endTurnImage, main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() / 2, heroW, heroW*149/283,null);
        else if (isMyTurn == playerStatus.IChoiseBlocker)
            g.drawImage(blockTurnImage, main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() / 2,  heroW, heroW*149/283,null);
        else if (isMyTurn == playerStatus.EnemyChoiseBlocker)
            g.drawImage(blockEnemyTurnImage, main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() / 2, heroW, heroW*149/283, null);
        else
            g.drawImage(notMyTurnImage, main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() / 2,  heroW, heroW*149/283, null);

        endTurnClick.setLocation(main.getWidth() - B0RDER_RIGHT - endTurnImage.getWidth(null), main.getHeight() / 2);
        endTurnClick.setSize(endTurnImage.getWidth(null), endTurnImage.getHeight(null));
        //Heroes
        g.drawImage(heroImage, main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() - heroH - B0RDER_BOTTOM, heroW, heroH, null);
        g.drawImage(enemyImage, main.getWidth() - heroW - B0RDER_RIGHT, B0RDER_TOP, heroW, heroH, null);
        enemyHeroClick.setLocation(main.getWidth() - heroW - B0RDER_RIGHT, B0RDER_TOP);
        enemyHeroClick.setSize(heroW, heroH);
        playerHeroClick.setLocation(main.getWidth() - heroW - B0RDER_RIGHT, main.getHeight() - heroH - B0RDER_BOTTOM);
        playerHeroClick.setSize(heroW, heroH);
        playerDamageLabel.setLocation(playerHeroClick.getX() + (int) (playerHeroClick.getWidth() * HERO_DAMAGE_WHERE_TO_SHOW_X), playerHeroClick.getY() + (int) (playerHeroClick.getHeight() * HERO_DAMAGE_WHERE_TO_SHOW_Y));
        if (player.damage != 0) playerDamageLabel.setText(player.damage + "");
        else playerDamageLabel.setText("");
        enemyDamageLabel.setLocation(enemyHeroClick.getX() + (int) (enemyHeroClick.getWidth() * HERO_DAMAGE_WHERE_TO_SHOW_X), enemyHeroClick.getY() + (int) (enemyHeroClick.getHeight() * HERO_DAMAGE_WHERE_TO_SHOW_Y));
        if (enemy.damage != 0) enemyDamageLabel.setText(enemy.damage + "");
        else enemyDamageLabel.setText("");
        //Decks
        g.drawImage(heroDeckImage, B0RDER_LEFT, main.getHeight() - smallCardH - B0RDER_BOTTOM, smallCardW, smallCardH, null);
        deckClick.setLocation(B0RDER_LEFT, main.getHeight() - smallCardH - B0RDER_BOTTOM);
        deckClick.setSize(smallCardW, smallCardH);
        g.drawImage(heroDeckImage, B0RDER_LEFT, B0RDER_TOP, smallCardW, smallCardH, null);
        //Hero graveyard
        playerGraveyardClick.setLocation(deckClick.getX() + deckClick.getWidth() + B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM);
        playerGraveyardClick.setSize(smallCardW, smallCardH);
        if (player.graveyard.size() == 0) {
            g.drawImage(heroGraveyardImage, deckClick.getX() + deckClick.getWidth() + B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM, smallCardW, smallCardH, null);
        } else {
            im = ImageIO.read(Main.class.getResourceAsStream(player.graveyard.get(player.graveyard.size() - 1).image));
            g.drawImage(im, deckClick.getX() + deckClick.getWidth() + B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM, smallCardW, smallCardH, null);
        }
        //Enemy graveyard
        enemyGraveyardClick.setLocation(deckClick.getX() + deckClick.getWidth() + B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM);
        enemyGraveyardClick.setSize(smallCardW, smallCardH);
        if (enemy.graveyard.size() == 0) {
            g.drawImage(heroGraveyardImage, deckClick.getX() + deckClick.getWidth() + B0RDER_BETWEEN, B0RDER_TOP, smallCardW, smallCardH, null);
        } else {
            im = ImageIO.read(Main.class.getResourceAsStream(enemy.graveyard.get(enemy.graveyard.size() - 1).image));
            g.drawImage(im, deckClick.getX() + deckClick.getWidth() + B0RDER_BETWEEN, B0RDER_TOP, smallCardW, smallCardH, null);
        }
        //Hero&enemy coin
        g.drawImage(heroCoinImage, playerGraveyardClick.getX() + playerGraveyardClick.getWidth() + B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM, smallCardW, smallCardH, null);
        playerCoinLabel.setLocation(playerGraveyardClick.getX() + playerGraveyardClick.getWidth() + B0RDER_BETWEEN + (int) (smallCardW * 0.5), main.getHeight() - smallCardH - B0RDER_BOTTOM + (int) (smallCardH * 0.8));
        playerCoinLabel.setText(player.untappedCoin + "/" + player.totalCoin);
        g.drawImage(heroCoinImage, playerGraveyardClick.getX() + playerGraveyardClick.getWidth() + B0RDER_BETWEEN, B0RDER_TOP, smallCardW, smallCardH, null);
        enemyCoinLabel.setLocation(playerGraveyardClick.getX() + playerGraveyardClick.getWidth() + B0RDER_BETWEEN + (int) (smallCardW * 0.5), B0RDER_TOP + (int) (smallCardH * 0.8));
        enemyCoinLabel.setText(enemy.untappedCoin + "/" + enemy.totalCoin);
        //Creatures
        drawPlayerCreature(g, 0);
        drawPlayerCreature(g, 1);
        //Hero card in hand
        if (!player.cardInHand.isEmpty()) {
            for (int i = 0; i < player.cardInHand.size(); i++)
            //for (Card card : player.cardInHand)   // I don't know why, but it create ConcurrentModificationException
            {
                Card card = player.cardInHand.get(i);
                if (card.image != null) {
                    try {
                        im = ImageIO.read(Main.class.getResourceAsStream(card.image));
                        g.drawImage(im, cardX + (numCardInHand * heroW), main.getHeight() - heroH - B0RDER_BOTTOM, heroW, heroH, null);
                        cardClick[numCardInHand].setLocation(cardX + (numCardInHand * heroW), main.getHeight() - heroH - B0RDER_BOTTOM);
                        cardClick[numCardInHand].setSize(heroW, heroH);
                        numCardInHand++;
                    } catch (IOException e) {
                        System.out.println("Can't load image.");
                    }
                }
            }
        }
        //Enemy hand
        im = ImageIO.read(Main.class.getResourceAsStream("Deck.png"));//His card deck up
        if (!enemy.cardInHand.isEmpty()) {
            for (int i = 0; i < enemy.cardInHand.size(); i++) {
                g.drawImage(im, cardX + (int) (i * heroW * 0.5), B0RDER_TOP, heroW, heroH, null);
            }
        }
    }

    private static void drawPlayerCreature(Graphics g, int np) {
        int numUnit = 0;
        int h;
        BufferedImage im;
        if (np == 0) h = battlegroundClick.getY() + battlegroundClick.getHeight() - heroH;
        else h = battlegroundClick.getY();

        for (int ii = 0; ii <= 1; ii++) {
            for (int i = 0; i < playerUnitClick[ii].length; i++) {
                playerUnitLabel[ii][i].setText("");
                playerUnitLabel[ii][i].setVisible(false);}}

        if (!board.creature.get(np).isEmpty()) {
            for (Creature creature : board.creature.get(np)//Sometimes it make exception after any creature die(((
                    ) {
                if (creature.image != null) {
                    try {
                        im = ImageIO.read(Main.class.getResourceAsStream(creature.image));
                        if (creature.isTapped) {
                            g.drawImage(tapImage(im), battlegroundClick.getX() + (int) (numUnit * heroW), h, heroH, heroW, null);
                            playerUnitClick[np][numUnit].setSize(heroH, heroW);
                            playerUnitClick[np][numUnit].setLocation(battlegroundClick.getX() + (int) (numUnit * heroW), h);//May be write not center?
                        } else {
                            g.drawImage(im, battlegroundClick.getX() + (int) (numUnit * heroW), h, heroW, heroH, null);
                            playerUnitClick[np][numUnit].setLocation(battlegroundClick.getX() + (int) (numUnit * heroW), h);
                            playerUnitClick[np][numUnit].setSize(heroW, heroH);
                        }
                        if (creature.damage != 0) {
                            playerUnitLabel[np][numUnit].setLocation(playerUnitClick[np][numUnit].getX() + (int) (playerUnitClick[np][numUnit].getWidth() * CREATURE_DAMAGE_WHERE_TO_SHOW_X), playerUnitClick[np][numUnit].getY() + (int) (playerUnitClick[np][numUnit].getHeight() * CREATURE_DAMAGE_WHERE_TO_SHOW_Y));
                            playerUnitLabel[np][numUnit].setText(creature.damage + "");
                            playerUnitLabel[np][numUnit].setVisible(true);

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
            heroImage = ImageIO.read(Main.class.getResourceAsStream("Тарна.png"));
            enemyImage = ImageIO.read(Main.class.getResourceAsStream("Тарна.png"));
            heroCoinImage = ImageIO.read(Main.class.getResourceAsStream("Coin.png"));
            heroDeckImage = ImageIO.read(Main.class.getResourceAsStream("Deck.png"));
            heroGraveyardImage = ImageIO.read(Main.class.getResourceAsStream("Graveyard.png"));
            endTurnImage = ImageIO.read(Main.class.getResourceAsStream("Endturn.png"));
            blockTurnImage = ImageIO.read(Main.class.getResourceAsStream("Blockturn.png"));
            blockEnemyTurnImage = ImageIO.read(Main.class.getResourceAsStream("Blockenemyturn.png"));
            notMyTurnImage = ImageIO.read(Main.class.getResourceAsStream("Enemyturn.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadCardDeck() {
        Card simpleCard = new Card(1, "Раскат грома", 1, 1, "Ранить выбранное существо на 2.", 0, 0);
        Card simpleCard2 = new Card(1, "Гьерхор", 1, 2, "Рывок.", 2, 2);
        Card simpleCard3 = new Card(2, "Гном", 1, 2, "", 3, 3);
        Card simpleCard4 = new Card(1, "Поглощение душ", 1, 1, "Ранить выбранного героя на 3.", 0, 0);
        simpleDeckCards = new ArrayList<>();
        simpleDeckCards.add(simpleCard);
        simpleDeckCards.add(simpleCard2);
        simpleDeckCards.add(simpleCard3);
        simpleDeckCards.add(simpleCard2);
        simpleDeckCards.add(simpleCard4);

        simpleDeckCards2 = new ArrayList<>();
        simpleDeckCards2.add(simpleCard2);
        simpleDeckCards2.add(simpleCard3);
        simpleDeckCards2.add(simpleCard2);
        simpleDeckCards2.add(simpleCard3);
        simpleDeckCards2.add(simpleCard2);
    }

    private static void setInitialProperties() {
        playerCoinLabel.setHorizontalAlignment(SwingConstants.LEFT);
        playerCoinLabel.setVerticalAlignment(SwingConstants.TOP);
        playerCoinLabel.setForeground(Color.WHITE);

        enemyCoinLabel.setHorizontalAlignment(SwingConstants.LEFT);
        enemyCoinLabel.setVerticalAlignment(SwingConstants.TOP);
        enemyCoinLabel.setForeground(Color.WHITE);

        enemyDamageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        enemyDamageLabel.setVerticalAlignment(SwingConstants.TOP);
        enemyDamageLabel.setForeground(Color.RED);
        enemyDamageLabel.setFont(new Font(enemyDamageLabel.getFont().getName(), Font.PLAIN, 20));
        playerDamageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        playerDamageLabel.setVerticalAlignment(SwingConstants.TOP);
        playerDamageLabel.setForeground(Color.RED);
        playerDamageLabel.setFont(new Font(playerDamageLabel.getFont().getName(), Font.PLAIN, 20));

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
                playerUnitLabel[ii][i] = new JLabel();
                viewField.add(playerUnitClick[ii][i]);
                viewField.add(playerUnitLabel[ii][i]);
                if (ii == 0) {
                    playerUnitClick[ii][i].addMouseListener(new MyListener(MyListener.Compo.CreatureInMyPlay, i));
                    playerUnitClick[ii][i].addMouseMotionListener(new MyListener(MyListener.Compo.CreatureInMyPlay, i));
                } else {
                    playerUnitClick[ii][i].addMouseListener(new MyListener(MyListener.Compo.EnemyUnitInPlay, i));
                    playerUnitClick[ii][i].addMouseMotionListener(new MyListener(MyListener.Compo.EnemyUnitInPlay, i));
                }
                playerUnitLabel[ii][i].setHorizontalAlignment(SwingConstants.LEFT);
                playerUnitLabel[ii][i].setVerticalAlignment(SwingConstants.TOP);
                playerUnitLabel[ii][i].setForeground(Color.RED);
                playerUnitLabel[ii][i].setFont(new Font(enemyDamageLabel.getFont().getName(), Font.PLAIN, 20));
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
        viewField.add(playerDamageLabel);
        viewField.add(enemyDamageLabel);
        viewField.add(gameLog);
        viewField.validate();
        main.add(viewField);
        viewField.repaint();
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
}
