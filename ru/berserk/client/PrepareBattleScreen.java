package ru.berserk.client;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static ru.berserk.client.Main.*;

public class PrepareBattleScreen {

    private static final int B0RDER_RIGHT = 15;
    private static final int B0RDER_LEFT = 10;
    private static final int B0RDER_TOP = 10;
    private static final int B0RDER_BETWEEN = 5;
    private static int B0RDER_BOTTOM = 40;
    private static final double CARD_SIZE_FROM_SCREEN = 0.08;
    private static final double BIG_CARD_SIZE_FROM_SCREEN = 0.17;
    private static final double SMALL_CARD_SIZE_FROM_SCREEN = 0.06;
    //Elements of view
    static ArrayList<String> decksChoice;//= new ArrayList<>();
    static ArrayList<String> decksChoiceHeroes;//= new ArrayList<>();
    static ArrayList<ArrayList<String>> myDecks;
    static JTextField enterNameField;
    static JTextField enterPasswordField;
    //Create deck
    static JTextField enterDeckName;// = new JTextField();
    static MyFunction.ClickImage cardsInNewDeck[] = new MyFunction.ClickImage[200];
    static MyFunction.ClickImage newHeroChoice = new MyFunction.ClickImage();
    static MyFunction.ClickImage okNewDeckClick = new MyFunction.ClickImage();
    static MyFunction.ClickImage cancelNewDeckClick = new MyFunction.ClickImage();
    static String totalCards="";
    static ArrayList<String> myCards = new ArrayList<>();
    //
    static int rating = 0;
    static int gold = 0;
    static String fromServerMessage = "";
    //For repaint
    //Elements of view
    private static JLabel deckChoiseClick[] = new JLabel[10];
    private static MyFunction.ClickImage connectButton = new MyFunction.ClickImage();
    private static MyFunction.ClickImage createUserButton = new MyFunction.ClickImage();
    // private static JLabel ratingLabel = new JLabel();
    //Static image for button, background and etc.
    private static Image background;
    static boolean isVisible = false;
    static int showed = 0;

    enum PlayerStatus {
        MyTurn(1), EnemyTurn(2), IChoiceBlocker(3), EnemyChoiceBlocker(4), EnemyChoiceTarget(5), MuliganPhase(6), waitingForConnection(7),
        waitOtherPlayer(8), waitingMulligan(9), choiseX(10), searchX(11), choiceTarget(12), digX(13), endGame(14), prepareForBattle(15),
        unknow(0), choiceYesNo(16);

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
                case 16:
                    return choiceYesNo;
            }
            return null;
        }
    }

    public static void showWindow() throws IOException {
        enterNameField.setVisible(true);
        enterPasswordField.setVisible(true);
        connectButton.setVisible(true);
        createUserButton.setVisible(true);
        decksChoice = new ArrayList<>();
        decksChoiceHeroes = new ArrayList<>();
        myDecks = new ArrayList<>();

        loadImage();

        main.repaint();
        Main.viewField.setVisible(true);
        isVisible = true;
    }

    static void connectOk() throws UnsupportedEncodingException {
        showed = 1;

    }

    public static void hideWindow() {
        if (showed!=0) {
            isVisible = false;
            enterNameField.setVisible(false);
            enterPasswordField.setVisible(false);
            connectButton.setVisible(false);
            createUserButton.setVisible(false);
            enterDeckName.setVisible(false);
            newHeroChoice.setVisible(false);
            okNewDeckClick.setVisible(false);
            cancelNewDeckClick.setVisible(false);
        }
    }

    static void onRepaint(Graphics g) {
        try {
            g.setFont(new Font("Georgia", Font.BOLD, 15));
            g.setColor(Color.WHITE);
            enterNameField.setLocation(B0RDER_LEFT * 5, B0RDER_TOP * 5 + B0RDER_BETWEEN * 3);
            enterNameField.setSize(main.getWidth() / 3, B0RDER_BETWEEN * 4);
            enterPasswordField.setLocation(B0RDER_LEFT * 5, B0RDER_TOP * 5 + B0RDER_BETWEEN * 4 + enterNameField.getHeight());
            enterPasswordField.setSize(main.getWidth() / 3, B0RDER_BETWEEN * 4);
            if (showed==0 || showed==1) {
                g.setFont(new Font("Georgia", Font.BOLD, 15));
                g.drawString(fromServerMessage, B0RDER_LEFT * 5, enterPasswordField.getY() + enterPasswordField.getHeight() + B0RDER_BETWEEN * 3);
            }
            if (showed==0) {
                g.drawString("Введите имя и пароль.", B0RDER_LEFT * 5, B0RDER_TOP * 5 + B0RDER_BETWEEN);
                connectButton.LSD(g, B0RDER_LEFT * 6 + enterNameField.getWidth(), enterNameField.getY(), heroW, heroW * 149 / 283);
                createUserButton.LSD(g, B0RDER_LEFT * 6 + enterNameField.getWidth() + B0RDER_BETWEEN + connectButton.getWidth(), enterNameField.getY(), heroW, heroW * 149 / 283);
            }
            if (showed==1) {
                drawAvalaibleDeck(g);
                g.drawString(enterNameField.getText(), B0RDER_LEFT * 5, B0RDER_TOP * 5 + B0RDER_BETWEEN);
                g.drawString("Рейтинг = " + rating, B0RDER_LEFT * 5, enterPasswordField.getY() + enterPasswordField.getHeight() + B0RDER_BETWEEN * 6);
                g.drawString("Золото = " + gold, B0RDER_LEFT * 5, enterPasswordField.getY() + enterPasswordField.getHeight() + B0RDER_BETWEEN * 9);
            }
            if (showed==2) {
                g.drawString("Введите всю колоду сюда(пока так): ИмяКолоды,ИмяГероя,ВсеКартыЧерезЗапятую.", B0RDER_LEFT * 5, B0RDER_TOP * 5 + B0RDER_BETWEEN);
                enterDeckName.setLocation(B0RDER_LEFT * 5, B0RDER_TOP * 5 + B0RDER_BETWEEN * 2);
                enterDeckName.setSize(main.getWidth() / 3, B0RDER_BETWEEN * 4);
                okNewDeckClick.LSD(g,B0RDER_LEFT * 5 + enterDeckName.getWidth()+ B0RDER_BETWEEN, B0RDER_TOP * 5 + B0RDER_BETWEEN, heroW, heroW * 149 / 283);
                cancelNewDeckClick.LSD(g,B0RDER_LEFT * 5 + enterDeckName.getWidth()+ B0RDER_BETWEEN*2+okNewDeckClick.getWidth(), B0RDER_TOP * 5 + B0RDER_BETWEEN, heroW, heroW * 149 / 283);

                g.drawString("Все ваши карты:", B0RDER_LEFT * 5, enterDeckName.getX()+enterDeckName.getHeight()+B0RDER_BETWEEN*5);

                for (int i=0;i<myCards.size();i++){
                    cardsInNewDeck[i] = new MyFunction.ClickImage();
                    cardsInNewDeck[i].image=ImageIO.read(new File("cards/"+myCards.get(i)+".jpg"));
                    cardsInNewDeck[i].LSD(g,B0RDER_LEFT * 5+(B0RDER_BETWEEN+heroW)*i, enterDeckName.getX()+enterDeckName.getHeight()+B0RDER_BETWEEN*8,heroW, heroW*400/283 );
                }
                //newHeroChoice.LSD(g, B0RDER_LEFT * 5, B0RDER_TOP * 5 + B0RDER_BETWEEN * 8, heroW, heroW * 149 / 283);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawAvalaibleDeck(Graphics g) throws IOException {
        enterNameField.setVisible(false);
        enterPasswordField.setVisible(false);
        connectButton.setVisible(false);
        createUserButton.setVisible(false);
        int deckShown = 0;
        for (String deck : decksChoice) {
            BufferedImage im;
            try {
                im = ImageIO.read(new File("cards/heroes/" + decksChoiceHeroes.get(deckShown) + ".jpg"));
                g.drawImage(im, B0RDER_LEFT * 5 + heroW * deckShown + B0RDER_BETWEEN * deckShown * 2, main.getHeight() / 2, heroW, Main.heroH, null);
                deckChoiseClick[deckShown].setLocation(B0RDER_LEFT * 5 + heroW * deckShown + B0RDER_BETWEEN * deckShown * 2, main.getHeight() / 2);
                deckChoiseClick[deckShown].setSize(heroW, Main.heroH);
                g.setFont(new Font("Georgia", Font.BOLD, 13));
                g.drawString(deck, B0RDER_LEFT * 5 + heroW * deckShown + B0RDER_BETWEEN * deckShown * 2 + B0RDER_BETWEEN, main.getHeight() / 2 - B0RDER_BETWEEN);
                deckShown++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedImage im;
            im = ImageIO.read(new File("icons/createnewdeck.png"));
            g.drawImage(im, B0RDER_LEFT * 5 + heroW * deckShown + B0RDER_BETWEEN * deckShown * 2, main.getHeight() / 2, heroW, Main.heroH, null);
            deckChoiseClick[deckShown+1].setLocation(B0RDER_LEFT * 5 + heroW * deckShown + B0RDER_BETWEEN * deckShown * 2, main.getHeight() / 2);
        deckChoiseClick[deckShown+1].setSize(heroW, Main.heroH);
    }

    private static void showNewDeckWindow(){
        showed=2;
        enterDeckName.setVisible(true);
        okNewDeckClick.setVisible(true);
        cancelNewDeckClick.setVisible(true);
        //newHeroChoice.setVisible(true);

        main.repaint();
    }

    private static void loadImage() {
        try {
            background = ImageIO.read(new File("icons/background.jpg"));
            connectButton.image = ImageIO.read(new File("icons/buttons/connect.png"));
            createUserButton.image = ImageIO.read(new File("icons/buttons/addnewuser.png"));
            newHeroChoice.image = ImageIO.read(new File("icons/buttons/addnewuser.png"));
            okNewDeckClick.image = ImageIO.read(new File("icons/buttons/ok.png"));
            cancelNewDeckClick.image = ImageIO.read(new File("icons/buttons/cancel.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void setInitialProperties() throws IOException {
        decksChoice = new ArrayList<>();
        decksChoiceHeroes = new ArrayList<>();
        enterNameField = new JTextField(40);
        enterPasswordField = new JTextField(40);
        enterDeckName = new JTextField(40);
        enterDeckName.setVisible(false);
        okNewDeckClick.setVisible(false);
        cancelNewDeckClick.setVisible(false);

        Main.viewField.add(enterNameField);
        Main.viewField.add(enterPasswordField);
        Main.viewField.add(connectButton);
        Main.viewField.add(createUserButton);
        Main.viewField.add(newHeroChoice);
        Main.viewField.add(enterDeckName);
        Main.viewField.add(okNewDeckClick);
        Main.viewField.add(cancelNewDeckClick);

        okNewDeckClick.addMouseListener(new MyListener(MyListener.Compo.okNewDeck, 0));
        cancelNewDeckClick.addMouseListener(new MyListener(MyListener.Compo.cancelNewDeck, 0));
        connectButton.addMouseListener(new MyListener(MyListener.Compo.Connect, 0));
        createUserButton.addMouseListener(new MyListener(MyListener.Compo.ConnectNew, 0));

        for (int i = 0; i < deckChoiseClick.length; i++) {
            deckChoiseClick[i] = new JLabel();
            Main.viewField.add(deckChoiseClick[i]);
            deckChoiseClick[i].addMouseListener(new MyListener(MyListener.Compo.DeckChoice, i));
        }

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

        }

        public void mouseClicked(MouseEvent e) {
            if (onWhat == Compo.DeckChoice) {
                try {
                    if (!enterNameField.getText().equals("") && !enterNameField.getText().equals(" ") && num<decksChoice.size())
                        Main.runGame(enterNameField.getText(), decksChoice.get(num), null);
                    else if  (!enterNameField.getText().equals("") && !enterNameField.getText().equals(" ") && num==decksChoice.size()+1){
                        System.out.println("NEW DECK");
                        showNewDeckWindow();
                    }
                    else {

                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } else if (onWhat == Compo.Connect) {
                try {
                    connect();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
            } else if (onWhat == Compo.ConnectNew) {
                String command = "$NEWUSER(" + enterNameField.getText() + "," + enterPasswordField.getText() + "," + CLIENT_VERSION + ")\n";
                WebsocketClient.client.sendMessage(command);
            } else if (onWhat == Compo.cancelNewDeck) {
                showed=1;
                enterDeckName.setVisible(false);
                //connect();
                main.repaint();
            } else if (onWhat == Compo.okNewDeck) {
                System.out.println(enterDeckName.getText());
                String command = "$CREATEDECK(" +enterDeckName.getText()+")\n";
                WebsocketClient.client.sendMessage(command);
                showed=1;
                enterDeckName.setVisible(false);
                try {
                    connect();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                main.repaint();
            }

        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent event) {

        }

        public void mouseExited(MouseEvent event) {

        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
        }

        enum Compo {Deck, CardInHand, CreatureInMyPlay, Board, EnemyHero, PlayerHero, EnemyUnitInPlay, ChoiseX, SearchX, Weapon, Menu, EndTurnButton, Fullscreen, Settings, DeckChoice, PlayerGraveyard, CreatureInMyPlayTap, PlayerHeroTap, Connect, ConnectNew, okNewDeck, cancelNewDeck, EnemyGraveyard}
    }

    static void connect() throws UnsupportedEncodingException {
        String command = "$CONNECT(" + enterNameField.getText() + "," + enterPasswordField.getText() + "," + CLIENT_VERSION + ")\n";
        WebsocketClient.client.sendMessage(command);
        PrepareBattleScreen.decksChoice = new ArrayList<>();
        PrepareBattleScreen.decksChoiceHeroes = new ArrayList<>();

    }
}
