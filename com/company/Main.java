package com.company;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends JFrame{

    private static int B0RDER_RIGHT = 20;
    private static int B0RDER_LEFT = 0;
    private static int B0RDER_BOTTOM = 40;
    private static int B0RDER_TOP = 10;
    private static int B0RDER_BETWEEN = 20;

    private static double CARD_PROP = 1.4;
    private static double CARD_SIZE_FROM_SCREEN = 0.12;

    private static Main main;
    //JLabel label;
    private static ViewField viewField;
    private static  JLabel deckClick;
    private static JLabel cardClick[]= new JLabel[9];
    private static JLabel playerUnitClick[]= new JLabel[9];

    private static JLabel battlegroundClick = new JLabel();

    private static JLabel playerCoinLabel;
    private static Image background;
    private static Image heroImage;
    private static Image enemyImage;
    private static Image heroCoinImage;
    private static Image enemyCoinImage;
    private static Image heroDeckImage;

    private static Image heroGraveyardImage;
    private static Image enemyDeckImage;
    private static Image enemyGraveyardImage;

    private static Board board;
    private static Player player;
    private static Player enemy;

    private static Card cardMem;

    public static void main(String[] args) {
        try {
            background = ImageIO.read(Main.class.getResourceAsStream("Background.jpg"));
            heroImage = ImageIO.read(Main.class.getResourceAsStream("Тарна.png"));
            enemyImage = ImageIO.read(Main.class.getResourceAsStream("Тарна.png"));
            heroCoinImage = ImageIO.read(Main.class.getResourceAsStream("Coin.png"));
            enemyCoinImage = ImageIO.read(Main.class.getResourceAsStream("Coin.png"));
            heroDeckImage = ImageIO.read(Main.class.getResourceAsStream("Deck.png"));
            enemyDeckImage = ImageIO.read(Main.class.getResourceAsStream("Deck.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        main = new Main();
        main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        main.setLocation(0,0);
        main.setSize(1300,700);

        viewField = new ViewField();


        playerCoinLabel = new JLabel();
        playerCoinLabel.setLocation(0,0);
        playerCoinLabel.setSize(1,1);
        playerCoinLabel.setHorizontalAlignment(SwingConstants.LEFT);
        playerCoinLabel.setVerticalAlignment(SwingConstants.TOP);
        playerCoinLabel.setForeground(Color.WHITE);
        viewField.setLayout(new BorderLayout());

        deckClick = new JLabel("");
        deckClick.addMouseListener(mouseListener(1,0));
        battlegroundClick = new JLabel();
        battlegroundClick.addMouseListener(mouseListener(3,0));

        for (int i=0;i<cardClick.length;i++){
            cardClick[i]=new JLabel();
            viewField.add(cardClick[i]);
            cardClick[i].addMouseListener(mouseListener(2,i));
        }
        for (int i=0;i<playerUnitClick.length;i++){
            playerUnitClick[i]=new JLabel();
            viewField.add(playerUnitClick[i]);
            playerUnitClick[i].addMouseListener(mouseListener(4,i));
        }
      //
        viewField.add(battlegroundClick);
        viewField.add(deckClick);
      //  viewField.add(cardClick);
        viewField.add(playerCoinLabel);

        main.add(viewField);

        main.repaint();
        main.setVisible(true);

        board =new Board();

        Card simpleCard = new Card(board);
        simpleCard.name="Раскат грома";
        simpleCard.text="";
        simpleCard.cost=1;
        simpleCard.type=1;
        simpleCard.image="Раскат грома.jpg";

        Card simpleCard2 = new Card(board);
        simpleCard2.name="Гьерхор";
        simpleCard2.text="";
        simpleCard2.cost=1;
        simpleCard2.type=2;
        simpleCard2.power=2;
        simpleCard2.hp=2;
        simpleCard2.image="Гьерхор.jpg";

        Card simpleCard3 = new Card(board);
        simpleCard3.name="Гном";
        simpleCard3.text="";
        //  simpleCard3.text="%Рывок%";
        simpleCard3.cost=2;
        simpleCard3.type=2;
        simpleCard3.power=3;
        simpleCard3.hp=3;
        simpleCard3.image="Гном.jpg";

        ArrayList<Card> simpleDeckCards = new ArrayList<Card>();
        simpleDeckCards.add(simpleCard);
        simpleDeckCards.add(simpleCard);
        simpleDeckCards.add(simpleCard);
        simpleDeckCards.add(simpleCard2);
        simpleDeckCards.add(simpleCard3);

        Deck simpleDeck = new Deck(simpleDeckCards);
        player = new Player(simpleDeck,board);
        player.hp=30;
        enemy = new Player(simpleDeck,board);
        enemy.hp=30;

        player.newTurn();
        player.drawCard();

        refillField();
    }

    private static void refillField(){
        playerCoinLabel.setText(player.untappedCoin+"/"+player.totalCoin);
    }

    private static MouseListener mouseListener(int onWhat,int num){
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onWhat==1)  player.drawCard();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (onWhat==2){
                    System.out.println("Click on card"+player.cardInHand.get(num).name);
                    cardMem = player.cardInHand.get(num);
                }
                }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (cardMem.type==2) {//creature
                    System.out.println("Release on battleground," + cardMem.name);
                    player.playCard(cardMem);
                    refillField();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
    }


    private static void onRepaint(Graphics g){
       g.drawImage(background,0,0, main.getWidth(),main.getHeight(),null);
       g.drawRect(100,200,600,200);
       int heroW = (int)(main.getWidth()*CARD_SIZE_FROM_SCREEN);
       int heroH = (int)(heroW*CARD_PROP);
       int smallCardW = (int)(heroW*0.7);
       int smallCardH = (int)(heroH*0.7);
       g.drawImage(heroImage,main.getWidth()-heroW-B0RDER_RIGHT,main.getHeight()-heroH-B0RDER_BOTTOM,heroW,heroH,null);
       g.drawImage(heroCoinImage,smallCardW*3+B0RDER_LEFT+B0RDER_BETWEEN,main.getHeight()-smallCardH-B0RDER_BOTTOM,smallCardW,smallCardH,null);
        g.drawImage(heroDeckImage,smallCardW*2+B0RDER_LEFT+B0RDER_BETWEEN,main.getHeight()-smallCardH-B0RDER_BOTTOM,smallCardW,smallCardH,null);

       deckClick.setLocation(smallCardW*2+B0RDER_LEFT+B0RDER_BETWEEN,main.getHeight()-smallCardH-B0RDER_BOTTOM);
       deckClick.setSize(smallCardW,smallCardH);
       g.drawImage(enemyImage,main.getWidth()-heroW-B0RDER_RIGHT,B0RDER_TOP,heroW,heroH,null);
       g.drawImage(enemyCoinImage,smallCardW*3+B0RDER_LEFT+B0RDER_BETWEEN,B0RDER_TOP,smallCardW,smallCardH,null);

        Image im;
       int numUnit=0;
        if (!player.cardInHand.isEmpty()) {
            for (Card creature : board.playerCreature
                    ) {
                if (creature.image!=null) {
                    try {
                        im = ImageIO.read(Main.class.getResourceAsStream(creature.image));
                        g.drawImage(im,  B0RDER_LEFT +(int)(numUnit*heroW), 200, heroW, heroH, null);
                        playerUnitClick[numUnit].setLocation(B0RDER_LEFT +(int)(numUnit*heroW),200);
                        playerUnitClick[numUnit].setSize(heroW,heroH);
                        numUnit++;
                        //TODO number card
                    } catch (IOException e) {
                        System.out.println("Can't load image.");
                    }
                }
            }
        }

        int numCardInHand=0;

        if (!player.cardInHand.isEmpty()) {
            for (Card card : player.cardInHand
                    ) {
                if (card.image!=null) {
                    try {
                        im = ImageIO.read(Main.class.getResourceAsStream(card.image));
                        g.drawImage(im, smallCardW * 4 + B0RDER_LEFT + B0RDER_BETWEEN + (int) (smallCardW * 0.5)+(int)(numCardInHand*heroW), main.getHeight() - heroH - B0RDER_BOTTOM, heroW, heroH, null);
                        cardClick[numCardInHand].setLocation(smallCardW * 4 + B0RDER_LEFT + B0RDER_BETWEEN + (int) (smallCardW * 0.5)+(int)(numCardInHand*heroW),main.getHeight() - heroH - B0RDER_BOTTOM);
                        cardClick[numCardInHand].setSize(heroW,heroH);
                        numCardInHand++;
                        //TODO number card
                    } catch (IOException e) {
                        System.out.println("Can't load image.");
                    }
                }
            }
        }
        playerCoinLabel.setLocation(smallCardW*3+B0RDER_LEFT+B0RDER_BETWEEN+(int)(smallCardW*0.5),main.getHeight()-smallCardH-B0RDER_BOTTOM+(int)(smallCardH*0.8));
    }

    private static class ViewField extends JPanel{
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            onRepaint(g);
            repaint();
        }
    }
}
