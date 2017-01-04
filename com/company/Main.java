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

public class Main extends JFrame{
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

    private static ViewField viewField = new ViewField();
    private static JLabel deckClick = new JLabel();
    private static JLabel cardClick[]= new JLabel[9];
    private static JLabel playerUnitClick[]= new JLabel[9];
    private static JLabel playerUnitLabel[]= new JLabel[9];
    private static JLabel battlegroundClick = new JLabel();
    private static JLabel enemyHeroClick = new JLabel();
    private static JLabel playerCoinLabel = new JLabel();
    private static JLabel playerGraveyardClick = new JLabel();
    private static JLabel enemyGraveyardClick = new JLabel();
    private static JLabel enemyDamageLabel = new JLabel();
    public static JLabel gameLog=new JLabel();
    private static JLabel endTurnClick=new JLabel();

    private static Image background;
    private static Image heroImage;
    private static Image enemyImage;
    private static Image heroCoinImage;
    private static Image heroDeckImage;
    private static Image endTurnImage;
    private static Image heroGraveyardImage;

    private static Board board;
    private static Player player;
    private static Player enemy;

    private static Card cardMem;
    private static Creature creatureMem;

    public static void main(String[] args) throws IOException {
        try {
            background = ImageIO.read(Main.class.getResourceAsStream("Background.jpg"));
            heroImage = ImageIO.read(Main.class.getResourceAsStream("Тарна.png"));
            enemyImage = ImageIO.read(Main.class.getResourceAsStream("Тарна.png"));
            heroCoinImage = ImageIO.read(Main.class.getResourceAsStream("Coin.png"));
            heroDeckImage = ImageIO.read(Main.class.getResourceAsStream("Deck.png"));
            heroGraveyardImage =  ImageIO.read(Main.class.getResourceAsStream("Graveyard.png"));
            endTurnImage = ImageIO.read(Main.class.getResourceAsStream("Endturn.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        main.setLocation(0,0);
        main.setSize(1382,744);

        playerCoinLabel.setHorizontalAlignment(SwingConstants.LEFT);
        playerCoinLabel.setVerticalAlignment(SwingConstants.TOP);
        playerCoinLabel.setForeground(Color.WHITE);

        enemyDamageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        enemyDamageLabel.setVerticalAlignment(SwingConstants.TOP);
        enemyDamageLabel.setForeground(Color.RED);
        enemyDamageLabel.setFont(new Font(enemyDamageLabel.getFont().getName(),Font.PLAIN,20));

        gameLog.setLocation(0,0);
        gameLog.setSize(1,1);
        gameLog.setHorizontalAlignment(SwingConstants.LEFT);
        gameLog.setVerticalAlignment(SwingConstants.TOP);
        gameLog.setForeground(Color.WHITE);

        deckClick.addMouseMotionListener(new  MyListener(MyListener.Compo.Deck,0));
        deckClick.addMouseListener(new  MyListener(MyListener.Compo.Deck,0));
        battlegroundClick.addMouseMotionListener(new  MyListener(MyListener.Compo.Board,0));
        battlegroundClick.addMouseListener(new  MyListener(MyListener.Compo.Board,0));
        endTurnClick.addMouseListener(new  MyListener(MyListener.Compo.EndTurnButton,0));
        enemyHeroClick.addMouseListener(new  MyListener(MyListener.Compo.EnemyHero,0));
        enemyHeroClick.addMouseMotionListener(new  MyListener(MyListener.Compo.EnemyHero,0));

        viewField.setVisible(false);
        viewField.setLocation(0,0);
        for (int i=0;i<cardClick.length;i++){
            cardClick[i]=new JLabel();
            viewField.add(cardClick[i]);
            cardClick[i].addMouseListener(new MyListener(MyListener.Compo.CardInHand,i));
            cardClick[i].addMouseMotionListener(new MyListener(MyListener.Compo.CardInHand,i));
        }
        for (int i=0;i<playerUnitClick.length;i++){
            playerUnitClick[i]=new JLabel();
            playerUnitLabel[i]=new JLabel();
            viewField.add(playerUnitClick[i]);
            viewField.add(playerUnitLabel[i]);
            playerUnitClick[i].addMouseListener(new MyListener(MyListener.Compo.CreatureInPlay,i));
            playerUnitClick[i].addMouseMotionListener(new MyListener(MyListener.Compo.CreatureInPlay,i));
            playerUnitLabel[i].setHorizontalAlignment(SwingConstants.LEFT);
            playerUnitLabel[i].setVerticalAlignment(SwingConstants.TOP);
            playerUnitLabel[i].setForeground(Color.RED);
            playerUnitLabel[i].setFont(new Font(enemyDamageLabel.getFont().getName(),Font.PLAIN,20));
        }
      //
        viewField.add(battlegroundClick);
        viewField.add(deckClick);
        viewField.add(playerGraveyardClick);
        viewField.add(enemyGraveyardClick);
        viewField.add(endTurnClick);
        viewField.add(enemyHeroClick);
        viewField.add(playerCoinLabel);
        viewField.add(enemyDamageLabel);
        viewField.add(gameLog);
        viewField.validate();
        main.add(viewField);
        viewField.repaint();
        main.setVisible(true);

        board =new Board();

        Card simpleCard = new Card(board,1,"Раскат грома",1,1,"Ранить выбранное существо на 2.",0,0);
        Card simpleCard2 = new Card(board,1,"Гьерхор",1,2,"",2,2);
        Card simpleCard3 = new Card(board,2,"Гном",1,2,"",3,3);

        ArrayList<Card> simpleDeckCards = new ArrayList<>();
        simpleDeckCards.add(simpleCard);
        simpleDeckCards.add(simpleCard);
        simpleDeckCards.add(simpleCard);
        simpleDeckCards.add(simpleCard2);
        simpleDeckCards.add(simpleCard3);

        ArrayList<Card> simpleDeckCards2 = new ArrayList<>();
        simpleDeckCards2.add(simpleCard);
        simpleDeckCards2.add(simpleCard);
        simpleDeckCards2.add(simpleCard);
        simpleDeckCards2.add(simpleCard2);
        simpleDeckCards2.add(simpleCard3);

        Deck simpleDeck = new Deck(simpleDeckCards);
        Deck simpleEnemyDeck = new Deck(simpleDeckCards2);
        player = new Player(simpleDeck,board,"Jeremy",30);
        enemy = new Player(simpleEnemyDeck,board,"Bob",30);

        System.out.println("Game start.");
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        main.setMaximizedBounds(env.getMaximumWindowBounds());
        main.setExtendedState(main.getExtendedState() | main.MAXIMIZED_BOTH);
        viewField.setVisible(true);

        player.newTurn();
        player.drawCard();
        enemy.drawCard();
        enemy.drawCard();
        enemy.drawCard();
        enemy.drawCard();
    }

    public static void printToView(String txt){
        Main.gameLog.setText(Main.gameLog.getText()+txt+"<br>");
    }

    private static String whereMyMouse;
    private static int whereMyMouseNum;

    private static class MyListener extends MouseInputAdapter {
        enum Compo {Deck,CardInHand,CreatureInPlay,Board,EnemyHero,EnemyUnit,EndTurnButton};
        Compo onWhat;
        int num;

        MyListener(Compo _compo,int _code){
            onWhat=_compo;
            num=_code;
        }

        public void mouseClicked(MouseEvent e) {
            if (onWhat==Compo.Deck)  player.drawCard();
            if (onWhat==Compo.EndTurnButton) player.newTurn();
        }

        public void mousePressed(MouseEvent e) {
            // you may not need this method
        }

        public void mouseEntered(MouseEvent event) {
            whereMyMouse=onWhat.toString();
            whereMyMouseNum = num;
        }

        public void mouseExited(MouseEvent event) {
            whereMyMouse="";
        }

        public void mouseReleased(MouseEvent e) {
            if ((whereMyMouse==Compo.Board.toString()) && (cardMem.type==2)) {
                //  put on board
                    System.out.println("Release on battleground," + cardMem.name);
                    player.playCard(cardMem,null,null);
            }
            else if ((whereMyMouse==Compo.EnemyHero.toString()) && (creatureMem!=null)){
                //enemy hero attack by creature
               creatureMem.attackPlayer(enemy);
            }
            else if ((whereMyMouse==Compo.EnemyHero.toString()) && (cardMem.type==1)){
                //enemy hero attack by spell from hand
                player.playCard(cardMem,null,enemy);
            }
            else if ((whereMyMouse==Compo.CreatureInPlay.toString()) && (cardMem.type==1)){
                //spell from hand to creature in play
                player.playCard(cardMem,board.playerCreature.get(whereMyMouseNum),null);
            }

            cardMem=null;
            creatureMem=null;
        }

        public void mouseDragged(MouseEvent e) {
            if (onWhat==Compo.CardInHand){//Creature in hand
                  cardMem = player.cardInHand.get(num);
            }
            else if(onWhat==Compo.CreatureInPlay){//Creature in play
                if (board.playerCreature.get(num).isTapped){printToView("Повернутое существо не может атаковать.");}
                else {
                    if (board.playerCreature.get(num).isSummonedJust){printToView("Это существо вошло в игру на этом ходу.");}
                    else {
                       // System.out.println("Pressed on card" + player.cardInHand.get(num).name);
                        creatureMem = board.playerCreature.get(num);
                    }
                }
            }


        }
    }

    private static BufferedImage tapImage(BufferedImage src){
        double rotationRequired = Math.toRadians (90);
        AffineTransform tx = new AffineTransform();
        tx.translate(0.5*src.getHeight(), 0.5*src.getWidth());
        tx.rotate(rotationRequired);
        tx.translate(-0.5*src.getWidth(), -0.5*src.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(src,null);
    }


    private static void onRepaint(Graphics g) throws IOException {
        int heroW = (int)(main.getWidth()*CARD_SIZE_FROM_SCREEN);
        int heroH = (int)(heroW*heroImage.getHeight(null)/heroImage.getWidth(null));
        int smallCardW = (int)(heroW*0.7);
        int smallCardH = (int)(heroH*0.7);
        final int cardX =B0RDER_LEFT+B0RDER_BETWEEN*3+smallCardW*3;
        BufferedImage im;
        int numUnit=0;
        int numCardInHand=0;


        gameLog.setLocation(B0RDER_LEFT,B0RDER_TOP+smallCardH+B0RDER_BETWEEN);
        gameLog.setSize(cardX-B0RDER_BETWEEN-B0RDER_LEFT,main.getHeight()-B0RDER_BOTTOM-B0RDER_BETWEEN*2-B0RDER_TOP-smallCardH*2);
        gameLog.setAutoscrolls(true);
        Border border = LineBorder.createGrayLineBorder();
        gameLog.setBorder(border);

        g.drawImage(background,0,0, main.getWidth(),main.getHeight(),null);
        //battleground
        battlegroundClick.setLocation(cardX,B0RDER_TOP+B0RDER_BETWEEN+heroH);
        battlegroundClick.setSize(main.getWidth()-B0RDER_RIGHT-cardX-heroW-B0RDER_BETWEEN,main.getHeight()-B0RDER_BOTTOM-B0RDER_BETWEEN*2-B0RDER_TOP-heroH*2);
        g.drawRect(battlegroundClick.getX(),battlegroundClick.getY(),battlegroundClick.getWidth(),battlegroundClick.getHeight());
        //end turn button
        g.drawImage(endTurnImage,main.getWidth()- B0RDER_RIGHT - endTurnImage.getWidth(null),(int)(main.getHeight()/2),null);
        endTurnClick.setLocation(main.getWidth()- B0RDER_RIGHT - endTurnImage.getWidth(null),(int)(main.getHeight()/2));
        endTurnClick.setSize(endTurnImage.getWidth(null),endTurnImage.getHeight(null));
        //Hero
        g.drawImage(heroImage,main.getWidth()-heroW-B0RDER_RIGHT,main.getHeight()-heroH-B0RDER_BOTTOM,heroW,heroH,null);
        //Hero&enemy deck
        g.drawImage(heroDeckImage,B0RDER_LEFT,main.getHeight()-smallCardH-B0RDER_BOTTOM,smallCardW,smallCardH,null);
        deckClick.setLocation(B0RDER_LEFT,main.getHeight()-smallCardH-B0RDER_BOTTOM);
        deckClick.setSize(smallCardW,smallCardH);
        g.drawImage(heroDeckImage,B0RDER_LEFT,B0RDER_TOP,smallCardW,smallCardH,null);
        //enemyDeckClick.setLocation(B0RDER_LEFT,main.getHeight()-smallCardH-B0RDER_BOTTOM);
        //enemyDeckClick.setSize(smallCardW,smallCardH);
        //Hero graveyard
        playerGraveyardClick.setLocation(deckClick.getX()+deckClick.getWidth()+B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM);
        playerGraveyardClick.setSize(smallCardW,smallCardH);
        if (player.graveyard.size()==0) {
            g.drawImage(heroGraveyardImage, deckClick.getX()+deckClick.getWidth()+B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM, smallCardW, smallCardH, null);
        }
        else {
            im = ImageIO.read(Main.class.getResourceAsStream(player.graveyard.get(player.graveyard.size()-1).image));
            g.drawImage(im, deckClick.getX()+deckClick.getWidth()+B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM, smallCardW, smallCardH, null);
        }
        //Enemy graveyard
        enemyGraveyardClick.setLocation(deckClick.getX()+deckClick.getWidth()+B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM);
        enemyGraveyardClick.setSize(smallCardW,smallCardH);
        if (enemy.graveyard.size()==0) {
            g.drawImage(heroGraveyardImage, deckClick.getX()+deckClick.getWidth()+B0RDER_BETWEEN, B0RDER_TOP, smallCardW, smallCardH, null);
        }
        else {
            im = ImageIO.read(Main.class.getResourceAsStream(enemy.graveyard.get(enemy.graveyard.size()-1).image));
            g.drawImage(im, deckClick.getX()+deckClick.getWidth()+B0RDER_BETWEEN, main.getHeight() - smallCardH - B0RDER_BOTTOM, smallCardW, smallCardH, null);
        }
        //Hero&enemy coin
        g.drawImage(heroCoinImage,playerGraveyardClick.getX()+playerGraveyardClick.getWidth()+B0RDER_BETWEEN,main.getHeight()-smallCardH-B0RDER_BOTTOM,smallCardW,smallCardH,null);
        playerCoinLabel.setLocation(playerGraveyardClick.getX()+playerGraveyardClick.getWidth()+B0RDER_BETWEEN+(int)(smallCardW*0.5),main.getHeight()-smallCardH-B0RDER_BOTTOM+(int)(smallCardH*0.8));
        playerCoinLabel.setText(player.untappedCoin+"/"+player.totalCoin);
        g.drawImage(heroCoinImage,playerGraveyardClick.getX()+playerGraveyardClick.getWidth()+B0RDER_BETWEEN,B0RDER_TOP,smallCardW,smallCardH,null);
        //enemyCoinLabel.setLocation(smallCardW*3+B0RDER_LEFT+B0RDER_BETWEEN+(int)(smallCardW*0.5),main.getHeight()-smallCardH-B0RDER_BOTTOM+(int)(smallCardH*0.8));
        //enemyCoinLabel.setText(player.untappedCoin+"/"+player.totalCoin);

        //Enemy
        g.drawImage(enemyImage,main.getWidth()-heroW-B0RDER_RIGHT,B0RDER_TOP,heroW,heroH,null);
        enemyHeroClick.setLocation(main.getWidth()-heroW-B0RDER_RIGHT,B0RDER_TOP);
        enemyHeroClick.setSize(heroW,heroH);
        enemyDamageLabel.setLocation(enemyHeroClick.getX()+(int)(enemyHeroClick.getWidth()*HERO_DAMAGE_WHERE_TO_SHOW_X),enemyHeroClick.getY()+(int)(enemyHeroClick.getHeight()*HERO_DAMAGE_WHERE_TO_SHOW_Y));
        if (enemy.damage!=0) enemyDamageLabel.setText(enemy.damage+"");
        else enemyDamageLabel.setText("");

        if (!board.playerCreature.isEmpty()) {
            for (Creature creature : board.playerCreature
                    ) {
                if (creature.image!=null) {
                    try {
                        im = ImageIO.read(Main.class.getResourceAsStream(creature.image));
                        if (creature.isTapped){
                            g.drawImage(tapImage(im), battlegroundClick.getX() +(int)(numUnit*heroW), battlegroundClick.getY()+battlegroundClick.getHeight()-heroH, heroH,heroW, null);
                            playerUnitClick[numUnit].setSize(heroH,heroW);
                         }
                        else{
                            g.drawImage(im, battlegroundClick.getX() +(int)(numUnit*heroW),battlegroundClick.getY()+battlegroundClick.getHeight()-heroH, heroW, heroH, null);
                            playerUnitClick[numUnit].setSize(heroW,heroH);
                            playerUnitClick[numUnit].setLocation(battlegroundClick.getX() +(int)(numUnit*heroW),battlegroundClick.getY()+battlegroundClick.getHeight()-heroH);
                        }
                        if (creature.damage!=0){
                            playerUnitLabel[numUnit].setLocation(playerUnitClick[numUnit].getX()+(int)(playerUnitClick[numUnit].getWidth()*CREATURE_DAMAGE_WHERE_TO_SHOW_X),playerUnitClick[numUnit].getY()+(int)(playerUnitClick[numUnit].getHeight()*CREATURE_DAMAGE_WHERE_TO_SHOW_Y));
                            playerUnitLabel[numUnit].setText(creature.damage+"");
                        }
                        numUnit++;
                    } catch (IOException e) {
                        System.out.println("Can't load image.");
                    }
                }
            }
        }

        //Hero card in hand
        if (!player.cardInHand.isEmpty()) {
            for (int i=0;i<player.cardInHand.size();i++)
            //for (Card card : player.cardInHand)   // I don't know why, but it create ConcurrentModificationException
            {
                Card card=player.cardInHand.get(i);
                System.out.println("load card in hand");
                if (card.image!=null) {
                    try {
                        im = ImageIO.read(Main.class.getResourceAsStream(card.image));
                        g.drawImage(im, cardX+(numCardInHand*heroW), main.getHeight() - heroH - B0RDER_BOTTOM, heroW, heroH, null);
                        cardClick[numCardInHand].setLocation(cardX+(numCardInHand*heroW),main.getHeight() - heroH - B0RDER_BOTTOM);
                        cardClick[numCardInHand].setSize(heroW,heroH);
                        numCardInHand++;
                    } catch (IOException e) {
                        System.out.println("Can't load image.");
                    }
                }
            }
        }
        //Enemy hand
        im = ImageIO.read(Main.class.getResourceAsStream("Deck.png"));
        if (!enemy.cardInHand.isEmpty()) {
            for (int i=0;i<enemy.cardInHand.size();i++)
            {
                        g.drawImage(im, cardX+(int)(i*heroW*0.5), B0RDER_TOP, heroW, heroH, null);
                    }
                }
        }


    private static class ViewField extends JPanel{

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
