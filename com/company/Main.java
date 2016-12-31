package com.company;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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

    private static Player player;
    private static Player enemy;

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
        playerCoinLabel.setHorizontalAlignment(SwingConstants.LEFT);
        playerCoinLabel.setVerticalAlignment(SwingConstants.TOP);
        playerCoinLabel.setForeground(Color.WHITE);
        viewField.setLayout(new BorderLayout());
        viewField.add(playerCoinLabel);
        main.add(viewField);

        main.repaint();
        main.setVisible(true);

        //onRepaint();

        Board board =new Board();

        Card simpleCard = new Card(board);
        simpleCard.name="1";
        simpleCard.cost=1;
        Card simpleCard2 = new Card(board);
        simpleCard2.name="2";
        simpleCard2.cost=2;

        Card simpleCard3 = new Card(board);
        simpleCard3.name="3";
        simpleCard3.text="%Рывок%";
        simpleCard3.cost=1;
        simpleCard3.type=2;
        simpleCard3.power=1;
        simpleCard3.hp=2;

        ArrayList<Card> simpleDeckCards = new ArrayList<Card>();
        simpleDeckCards.add(simpleCard);
        simpleDeckCards.add(simpleCard2);
        simpleDeckCards.add(simpleCard3);

        Deck simpleDeck = new Deck(simpleDeckCards);
        player = new Player(simpleDeck,board);
        player.hp=30;
        enemy = new Player(simpleDeck,board);
        enemy.hp=30;

        player.newTurn();
        //System.out.println("PlayerCard:"+player.handToString());
        //System.out.println("InDeckCard:"+player.deck.deckToString());
        System.out.println("HPD:"+player.damage+"/"+enemy.damage+","+"Board:"+player.board.boardToString());
        System.out.println("Draw");
        player.drawCard();
        //System.out.println("PlayerCard:"+player.handToString());
        //System.out.println("InDeckCard:"+player.deck.deckToString());
        System.out.println("Play card 3");
        player.playCard(0);
        System.out.println("Board:"+player.board.boardToString());
//        board.playerCreature.get(0).takeDamage(1);
//        System.out.println("Board:"+player.board.boardToString());
//        board.playerCreature.get(0).takeDamage(1);
//        System.out.println("Board:"+player.board.boardToString());
      //  player.newTurn();
        board.playerCreature.get(0).attackPlayer(enemy);
        System.out.println("HPD:"+player.damage+"/"+enemy.damage+","+"Board:"+player.board.boardToString());
        refillField();
        System.out.println("PlayerCard:"+player.handToString());
        System.out.println("InDeckCard:"+player.deck.deckToString());
    }

    private static void refillField(){
      //  label.setText("HPD:"+player.damage+"/"+enemy.damage+","+"Board:"+player.board.boardToString());
//        label.setText("<html>");
//        label.setText(label.getText()+"Player: HP = "+player.hp+", damage = "+player.damage+"<br>");
//        label.setText(label.getText()+"Enemy: HP = "+enemy.hp+", damage = "+enemy.damage+"<br>");
//        label.setText(label.getText()+"</html>");

        playerCoinLabel.setText(player.untappedCoin+"/"+player.totalCoin);
    }

    private static void onRepaint(Graphics g){
       g.drawImage(background,0,0, main.getWidth(),main.getHeight(),null);
       int heroW = (int)(main.getWidth()*CARD_SIZE_FROM_SCREEN);
       int heroH = (int)(heroW*CARD_PROP);
       int smallCardW = (int)(heroW*0.7);
       int smallCardH = (int)(heroH*0.7);
       g.drawImage(heroImage,main.getWidth()-heroW-B0RDER_RIGHT,main.getHeight()-heroH-B0RDER_BOTTOM,heroW,heroH,null);
       g.drawImage(heroCoinImage,smallCardW*3+B0RDER_LEFT+B0RDER_BETWEEN,main.getHeight()-smallCardH-B0RDER_BOTTOM,smallCardW,smallCardH,null);
       playerCoinLabel.setLocation(smallCardW*3+B0RDER_LEFT+B0RDER_BETWEEN+(int)(smallCardW*0.5),main.getHeight()-smallCardH-B0RDER_BOTTOM+(int)(smallCardH*0.8));
       g.drawImage(heroDeckImage,smallCardW*2+B0RDER_LEFT+B0RDER_BETWEEN,main.getHeight()-smallCardH-B0RDER_BOTTOM,smallCardW,smallCardH,null);

       g.drawImage(enemyImage,main.getWidth()-heroW-B0RDER_RIGHT,B0RDER_TOP,heroW,heroH,null);
       g.drawImage(enemyCoinImage,smallCardW*3+B0RDER_LEFT+B0RDER_BETWEEN,B0RDER_TOP,smallCardW,smallCardH,null);

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
