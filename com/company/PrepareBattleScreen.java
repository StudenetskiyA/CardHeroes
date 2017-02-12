package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import static com.company.Main.main;

public class PrepareBattleScreen {

    private static final int B0RDER_RIGHT = 15;
    private static final int B0RDER_LEFT = 10;
    private static final int B0RDER_TOP = 10;
    private static final int B0RDER_BETWEEN = 5;
    private static int B0RDER_BOTTOM = 40;
    private static final double CARD_SIZE_FROM_SCREEN = 0.08;
    private static final double BIG_CARD_SIZE_FROM_SCREEN = 0.17;
    private static final double SMALL_CARD_SIZE_FROM_SCREEN = 0.06;
    static PrintWriter writerToLog;
    //Elements of view
    static ArrayList<String> decksChoice ;//= new ArrayList<>();
    static ArrayList<String> decksChoiceHeroes ;//= new ArrayList<>();
    static JTextField enterNameFieled;// = new JTextField(40);
   // static Thread cycleReadFromServer;// = new CycleServerRead();
    //For repaint
    //Elements of view
    private static JLabel deckChoiseClick[] = new JLabel[10];
    //Static image for button, background and etc.
    private static Image background;
    static boolean isVisible=false;
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
        decksChoice = new ArrayList<>();
        decksChoiceHeroes = new ArrayList<>();
        enterNameFieled = new JTextField(40);
       // cycleReadFromServer = new CycleServerRead();
        prepareListOfDeck();
        loadImage();
        setInitialProperties();

        Main.viewField.setVisible(true);
        isVisible=true;

    }

    public static void hideWindow(){
        isVisible=false;
        Main.viewField.remove(enterNameFieled);
    }

    static void onRepaint(Graphics g) {
             try {
//                heroW = (int) (main.getWidth() * CARD_SIZE_FROM_SCREEN);
//                heroW -= HeroLabel.plusSize();
//                heroH = (int) (heroW * 400 / 283);
//                heroH -= HeroLabel.plusSize();
//
//                //Background
//                int width = Main.background.getWidth(null);
//                int height = Main.background.getHeight(null);
//
//                g.drawImage(background, 0, 0, main.getWidth(), main.getWidth() * height / width, null);

                drawAvalaibleDeck(g);
            } catch (IOException e) {
                e.printStackTrace();
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
                g.drawImage(im, B0RDER_LEFT * 5 + Main.heroW * deckShown + B0RDER_BETWEEN * deckShown * 2, main.getHeight() / 2, Main.heroW, Main.heroH, null);
                deckChoiseClick[deckShown].setLocation(B0RDER_LEFT * 5 + Main.heroW * deckShown + B0RDER_BETWEEN * deckShown * 2, main.getHeight() / 2);
                deckChoiseClick[deckShown].setSize(Main.heroW, Main.heroH);
                g.setFont(new Font("Georgia", Font.BOLD, 13));
                g.drawString(deck.substring(0, deck.length() - 4).substring(0, Math.min(8, deck.substring(0, deck.length() - 4).length())), B0RDER_LEFT * 5 + Main.heroW * deckShown + B0RDER_BETWEEN * deckShown * 2 + B0RDER_BETWEEN, main.getHeight() / 2 - B0RDER_BETWEEN);
                deckShown++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadImage() {
        try {
            background = ImageIO.read(Main.class.getResourceAsStream("icons/background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setInitialProperties() throws IOException {
        Main.viewField.add(enterNameFieled);

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
                    if (!enterNameFieled.getText().equals("") && !enterNameFieled.getText().equals(" "))
                        Main.runGame(enterNameFieled.getText(), decksChoice.get(num).substring(0, decksChoice.get(num).length() - 4), null);
                    else {
                        //TODO Show message - enter correct name
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
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

        enum Compo {Deck, CardInHand, CreatureInMyPlay, Board, EnemyHero, PlayerHero, EnemyUnitInPlay, ChoiseX, SearchX, Weapon, Menu, EndTurnButton, Fullscreen, Settings, DeckChoice, PlayerGraveyard, CreatureInMyPlayTap, PlayerHeroTap, EnemyGraveyard}
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
}
