package com.company;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by samsung on 30.12.2016.
 */
public class Card {
    // Board board;
    public int cost;
    public String name;
    public String text;
    public String image;
    public String creatureType;
    public int color;
    public int type;//1 for spell, 2 for creature
    public int targetType;//Battlecry 1 for creatures, 2 for heroes
    public int tapTargetType;//May exist cards with Battlecry and TAP. Today its only one)))
    public int power;//only for creature, ignore for other
    public int hp;//only for creature and hero, its maximum health, not current
    public String hash;

    public static class ActivatedAbility {
        public static int targetType;
        public static int tapTargetType;
        public static Creature creature;
        public static boolean creatureTap;
        public static boolean heroAbility = false;
        public static boolean weaponAbility = false;
        public int heroAbilityCost;
    }

    public Card(Card _card) {
        name = _card.name;
        text = _card.text;
        cost = _card.cost;
        image = _card.image;
        color = _card.color;
        type = _card.type;
        power = _card.power;
        hp = _card.hp;
        targetType = _card.targetType;
        tapTargetType = _card.tapTargetType;
        creatureType=_card.creatureType;
    }

    public Card(int _cost, String _name, String _crtype, int _color, int _type, int _targetType, int _tapTargetType, String _text, int _power, int _hp) {
        //   board=_board;
        name = _name;
        text = _text;
        cost = _cost;
        image = _name + ".jpg";
        color = _color;
        type = _type;
        power = _power;
        hp = _hp;
        targetType = _targetType;
        tapTargetType = _tapTargetType;
        creatureType=_crtype;
    }

    public static ArrayList<String> getTextBetween(String fromText) {
        ArrayList<String> rtrn = new ArrayList<String>();
        String beforeText = "(";
        fromText = fromText.substring(fromText.indexOf(beforeText) + 1, fromText.length() - 1);
        String[] par = fromText.split(",");
        for (int i = 0; i < par.length; i++)
            rtrn.add(par[i]);
        return rtrn;
    }

    public static BufferedImage tapImage(BufferedImage src) {
        double rotationRequired = Math.toRadians(90);
        AffineTransform tx = new AffineTransform();
        tx.translate(0.5 * src.getHeight(), 0.5 * src.getWidth());
        tx.rotate(rotationRequired);
        tx.translate(-0.5 * src.getWidth(), -0.5 * src.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(src, null);
    }

    public static int getNumericAfterText(String fromText, String afterText) {
        int begin = fromText.indexOf(afterText);
        int end1 = fromText.indexOf(" ", begin + afterText.length() + 1);
        if (end1 == -1) end1 = 1000;
        int end2 = fromText.indexOf(".", begin + afterText.length() + 1);
        if (end2 == -1) end2 = 1000;
        int end = Math.min(end1, end2);
        if (end == 1000) end = fromText.length();
        String dmg = fromText.substring(begin + afterText.length(), end);
        int numdmg = 0;
        try {
            numdmg = Integer.parseInt(dmg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numdmg;
    }

    public void playOnCreature(Player _pl, Creature creature) {
        if (creature.text.contains("Если выбрана целью заклинание - погибает.")) {
            creature.die();
        } else
            ability(this, _pl, creature, null, text);
    }

    public void playOnPlayer(Player _pl, Player _player) {
        ability(this, _pl, null, _player, text);
    }

    public static Card getCardByName(String name) {
        //Here all cards!
        if (name.equals("Тарна"))
            return new Card(0, "Тарна","", 1, 0, 0, 0, "ТАП:4 Взять карт 1.", 0, 28);
        else if (name.equals("Бьорнбон"))
            return new Card(0, name,"", 3, 0, 0, 0, "ТАП:0 Получить щит ББ.", 0, 30);
        else if (name.equals("Тиша"))
            return new Card(0, "Тиша","", 1, 0, 1, 0, "ТАПТ: Отравить+ выбранное существо на 1.", 0, 26);
        else if (name.equals("Раскат грома"))
            return new Card(1, "Раскат грома","", 3, 1, 1, 0, "Ранить выбранное существо на 3.", 0, 0);
        else if (name.equals("Выброс силы"))
            return new Card(2, name,"", 3, 1, 1, 0, "Ранить выбранное существо на 5.", 0, 0);
        else if (name.equals("Гьерхор"))
            return new Card(1, "Гьерхор","Йордлинг", 3, 2, 0, 0, "", 2, 2);
        else if (name.equals("Найтин"))
            return new Card(2, "Найтин", "",6, 2, 0, 0, "Направленный удар. Рывок.", 2, 2);
        else if (name.equals("Кригторн"))
            return new Card(2, "Кригторн", "",3, 2, 0, 0, "Первый удар. Рывок.", 2, 1);
        else if (name.equals("Гном"))
            return new Card(2, "Гном", "Гном",3, 2, 0, 0, "", 3, 3);
        else if (name.equals("Гном-легионер"))
            return new Card(4, name, "Гном",3, 2, 0, 0, "Направленный удар. Рывок.", 3, 5);
        else if (name.equals("Гном-смертник"))
            return new Card(3, name, "Гном",3, 2, 0, 0, "Защита от заклинаний. Рывок.", 3, 4);
        else if (name.equals("Поглощение души"))
            return new Card(3, "Поглощение душ","", 5, 1, 2, 0, "Ранить выбранного героя на 3. Излечить вашего героя на 3.", 0, 0);
        else if (name.equals("Эльф-дозорный"))
            return new Card(4, "Эльф-дозорный", "",4, 2, 0, 0, "Найм: Возьмите карт 1.", 2, 5);
        else if (name.equals("Послушник"))
            return new Card(5, "Послушник", "Лингунг",3, 2, 1, 0, "Наймт: Выстрел по существу на 4.", 2, 3);
        else if (name.equals("Гном-лучник"))
            return new Card(3, "Гном-лучник", "Гном",3, 2, 3, 0, "Защита от выстрелов. Наймт: Выстрел на 2.", 2, 3);
        else if (name.equals("Лучник Захры"))
            return new Card(4, "Лучник Захры","Орк", 2, 2, 3, 0, "Защита от заклинаний. Наймт: Выстрел на 2.", 4, 2);
        else if (name.equals("Цверг-заклинатель"))
            return new Card(3, name,"Гном", 3, 2, 0, 0, "Защита от заклинаний. Защита от выстрелов. Защита от отравления.", 3, 3);
        else if (name.equals("Верцверг"))
            return new Card(4, name,"Гном", 3, 2, 1, 0, "Направленный удар. Наймт: Получает к атаке + 3.", 2, 4);
        else if (name.equals("Цепная молния"))
            return new Card(6, "Цепная молния","", 3, 1, 0, 0, "Ранить каждое существо противника на 3.", 0, 0);
        else if (name.equals("Волна огня"))
            return new Card(3, "Волна огня", "",2, 1, 0, 0, "Ранить каждое существо на 2.", 0, 0);
        else if (name.equals("Чешуя дракона"))
            return new Card(2, "Чешуя дракона", "",4, 1, 0, 0, "Получите * 1.", 0, 0);
        else if (name.equals("Выслеживание"))
            return new Card(0, "Выслеживание", "",4, 1, 0, 0, "Получите до конца хода * 2.", 0, 0);
        else if (name.equals("Фиал порчи"))
            return new Card(2, "Фиал порчи", "",1, 1, 1, 0, "Отравить выбранное существо на 2.", 0, 0);
        else if (name.equals("Глашатай пустоты"))
            return new Card(1, "Глашатай пустоты","Пустой", 6, 2, 0, 0, "Уникальность. Не получает ран.", 0, 1);
        else if (name.equals("Велит"))
            return new Card(2, "Велит", "",2, 2, 0, 3, "ТАПТ: Выстрел на 1.", 1, 3);
        else if (name.equals("Пуф"))
            return new Card(2, name, "Гном",3, 2, 0, 3, "ТАПТ: Выстрел на 1.", 2, 3);
        else if (name.equals("Кьелэрн"))
            return new Card(1, "Кьелэрн", "",6, 2, 0, 0, "Уникальность. Рывок. ТАП: Получите до конца хода * 1.", 0, 1);
        else if (name.equals("Агент Разана"))
            return new Card(2, "Агент Разана", "",1, 2, 1, 0, "Наймт: Отравить выбранное существо на 1.", 1, 2);
        else if (name.equals("Скованный еретик"))
            return new Card(1, "Скованный еретик", "",5, 2, 0, 0, "Найм: Закрыться.", 3, 2);
        else if (name.equals("Вэлла"))
            return new Card(3, "Вэлла", "",4, 2, 3, 0, "Наймт: Излечить выбранное существо или героя на 2.", 3, 4);
        else if (name.equals("Рыцарь Туллена"))
            return new Card(6, "Рыцарь Туллена", "",2, 2, 0, 0, "Броня 3.", 6, 3);
        else if (name.equals("Орк-лучник"))
            return new Card(1, name, "",2, 2, 3, 0, "Гнев. Наймт: Выстрел на 1.", 1, 1);
        else if (name.equals("Безумие"))
            return new Card(3, name, "",1, 1, 1, 0, "Нанести урон выбранному существу, равный его удару.", 0, 0);
        else if (name.equals("Зельеварение"))
            return new Card(1, name, "",1, 1, 1, 0, "Верните выбранное существо в руку его владельца.", 0, 0);
        else if (name.equals("Дахут"))
            return new Card(3, name, "",1, 2, 1, 0, "Наймт: Верните выбранное существо в руку его владельца.", 2, 3);
        else if (name.equals("Забира"))
            return new Card(2, "Забира", "",1, 2, 0, 0, "Если выбрана целью заклинание - погибает.", 3, 4);
        else if (name.equals("Волнорез"))
            return new Card(3, name, "",1, 2, 0, 0, "Если выбрана целью заклинание - погибает.", 4, 5);
        else if (name.equals("Десница Архааля"))
            return new Card(4, name, "",1, 2, 1, 0, "Опыт в защите. Наймт: Уничтожьте отравленное существо.", 1, 4);
        else if (name.equals("Нойта"))
            return new Card(1, name, "Йордлинг",1, 2, 1, 0, "Наймт: Ранить существо без ран на 3.", 1, 1);
        else if (name.equals("Орк-мародер"))
            return new Card(5, name,"", 2, 2, 0, 0, "Опыт в атаке. Первый удар. Рывок.", 5, 2);
        else if (name.equals("Менгир Каррефура"))
            return new Card(3, name, "",1, 2, 0, 1, "ТАПТ: Отравить+ выбранное существо на 1.", 0, 10);
        else if (name.equals("Рыцарь реки"))
            return new Card(5, name, "",1, 2, 1, 0, "Наймт: Выбранное существо не может атаковать и выступать защитником до конца следующего хода.", 4, 6);
        else if (name.equals("Поиск кладов"))
            return new Card(6, name, "",1, 1, 0, 0, "Взять карт 4.", 0, 0);
        else if (name.equals("Прозрение"))
            return new Card(2, name,"", 1, 1, 0, 0, "Взять карт 1. Если у соперника больше существ, чем у вас, взять еще карт 1.", 0, 0);
        else if (name.equals("Плащ Исхара"))
            return new Card(1, name, "Броня",1, 3, 0, 0, "", 0, 6);
        else if (name.equals("Богарт"))
            return new Card(4, name, "",6, 2, 0, 0, "Уникальность. Найм: Каждое другое существо погибает в конце хода противника.", 2, 7);
        else if (name.equals("Полевица"))
            return new Card(4, name, "",1, 2, 0, 0, "Гибель: Взять карт 2.", 2, 3);
        else if (name.equals("Смайта"))
            return new Card(4, name, "",6, 2, 3, 0, "Гибельт: Ранить выбранное существо или героя на 2.", 4, 3);
        else if (name.equals("Ядовитое пламя"))
            return new Card(0, name, "",1, 1, 1, 0, "Доплатите Х *. Ранить выбранное существо на ХХХ.", 0, 0);
        else if (name.equals("Вольный воитель"))
            return new Card(0, name, "",6, 2, 0, 0, "Доплатите Х *. Найм: Получает к характеристикам + ХХХ.", 0, 0);
        else if (name.equals("Шар тины"))
            return new Card(2, name, "",1, 1, 0, 0, "Поиск цвет 1", 0, 0);
        else if (name.equals("Шар молний"))
            return new Card(2, name, "",3, 1, 0, 0, "Поиск цвет 3", 0, 0);
        else if (name.equals("Гном-кузнец"))
            return new Card(3, name, "Гном",1, 2, 0, 0, "Найм: Поиск тип 3", 1, 4);
        else if (name.equals("Гном-кладоискатель"))
            return new Card(5, name, "Гном",3, 2, 0, 0, "Броня 1. Найм: Поиск комбо+ 2 Гном 2.", 5, 4);
        else if (name.equals("Рунопевец"))
            return new Card(3, name, "Гном",3, 2, 0, 0, "Статичный эффект.", 3, 3);
        else if (name.equals("Тан гномов"))
            return new Card(6, name, "Гном",3, 2, 0, 0, "Броня 2. Статичный эффект.", 5, 4);
        else if (name.equals("Безумный охотник"))
            return new Card(5, name, "",6, 2, 0, 0, "Найм: Получает +Х к удару и Броню Х, где Х - число других ваших существ.", 4, 4);
        else if (name.equals("Браслет подчинения"))
            return new Card(3, name, "Амулет",1, 3, 0, 0, "", 0, 0);
        else if (name.equals("Молот прародителя"))
            return new Card(2, name, "Оружие",3, 3, 0, 1, "ТАПТ: Выбранное существо до конца хода получает к атаке + 2.", 0, 0);
        else {
            System.out.println("Ошибка - Неопознанная карта:"+name);
            return null;
        }
    }

    public static void ability(Card _who, Player _whis, Creature _cr, Player _pl, String txt) {
        //Super function! Do all what do cards text!
        //Which Card player(_who), who player(_whis), on what creature(_cr, may null), on what player(_pl, may null), text to play(txt)
        if (txt.contains("Закрыться.")) {//Only here - _cr=_who to get access to creature
            _cr.tapCreature();
        }
        if (txt.contains("Получить щит ББ.")) {//Only here - _cr=_who to get access to creature
            _whis.bbshield=true;
            Main.printToView("Бьорнбон активирует свой щит.");
        }
        if (txt.contains("Поиск цвет ")) {//Only for player, who called it.
            if (_whis.playerName.equals(Main.players[0].playerName)) {
                int dmg = getNumericAfterText(txt, "Поиск цвет ");
                Main.isMyTurn = Main.playerStatus.searchX;
                Main.choiseXcolor = dmg;
            }
        }
        if (txt.contains("Поиск комбо+ ")) {//Only for player, who called it.
            if (_whis.playerName.equals(Main.players[0].playerName)) {
                int type = getNumericAfterText(txt, "Поиск комбо+ ");
                Main.isMyTurn = Main.playerStatus.searchX;
                Main.choiseXtype = type;
                Main.choiseXcreatureType=txt.substring(txt.indexOf("Поиск комбо+ ")+"Поиск комбо+ ".length()+2,txt.indexOf(" ",txt.indexOf("Поиск комбо+ ")+"Поиск комбо+ ".length()+2));
                System.out.println("search type = "+  Main.choiseXcreatureType);
                Main.choiseXcost= getNumericAfterText(txt, "Поиск комбо+ "+type+ " "+ Main.choiseXcreatureType+" ");
                System.out.println("search cost = "+  Main.choiseXcost);
            }
        }
        if (txt.contains("Поиск тип ")) {//Only for player, who called it.
            if (_whis.playerName.equals(Main.players[0].playerName)) {
                int dmg = getNumericAfterText(txt, "Поиск тип ");
                Main.isMyTurn = Main.playerStatus.searchX;
                Main.choiseXtype = dmg;
            }
        }
        if (txt.contains("Получает к характеристикам + ")) {
            int dmg = getNumericAfterText(txt, "Получает к характеристикам + ");
            _cr.effects.bonusTougness+=dmg;
            _cr.effects.bonusPower+=dmg;
        }
        if (txt.contains("Выбранное существо до конца хода получает к атаке + ")) {
            int dmg = getNumericAfterText(txt, "Выбранное существо до конца хода получает к атаке + ");
            Main.printToView(_cr.name+" получает +"+dmg +" к удару до конца хода.");
            _cr.effects.bonusPowerUEOT+=dmg;
        }
        if (txt.contains("Получает к броне + ")) {
            int dmg = getNumericAfterText(txt, "Получает к броне + ");
            _cr.effects.bonusArmor+=dmg;
        }
        if (txt.contains("Получает +Х к удару и Броню Х, где Х - число других ваших существ.")) {
            int dmg = Board.creature.get(_cr.owner.numberPlayer).size()-1;
            if (dmg>0){
            _cr.effects.bonusPower+=dmg;
            _cr.effects.bonusArmor+=dmg;
            _cr.currentArmor+=dmg;
            Main.printToView(_cr.name+" получает +"+dmg +" к удару и броне.");}
        }
        if (txt.contains("Получает к атаке + ")) {
            int dmg = getNumericAfterText(txt, "Получает к атаке + ");
            _cr.effects.bonusPower+=dmg;
        }
        if (txt.contains("Излечить выбранное существо или героя на ")) {
            int dmg = getNumericAfterText(txt, "Излечить выбранное существо или героя на ");
            if (_cr != null) {
                Main.printToView(_who.name + " излечивает " + _cr.name + " на " + dmg + ".");
                _cr.heal(dmg);
            } else {
                Main.printToView(_who.name + " излечивает " + _pl.name + " на " + dmg + ".");
                _pl.heal(dmg);
            }
        }
        if (txt.contains("Ранить выбранное существо или героя на ")) {
            int dmg = getNumericAfterText(txt, "Ранить выбранное существо или героя на ");
            if (_cr != null) {
                Main.printToView(_who.name + " ранит " + _cr.name + " на " + dmg + ".");
                _cr.takeDamage(dmg, Creature.DamageSource.ability);
            } else {
                Main.printToView(_who.name + " ранит " + _pl.name + " на " + dmg + ".");
                _pl.takeDamage(dmg);
            }
        }
        if (txt.contains("Ранить выбранного героя на ")) {
            int dmg = getNumericAfterText(txt, "Ранить выбранного героя на ");
            _pl.takeDamage(dmg);
            Main.printToView(_pl.playerName + " получил " + dmg + " урона.");
        }
        if (txt.contains("Уничтожьте отравленное существо.")) {
            if (_cr.poison > 0)
                _cr.die();
        }
        if (txt.contains("Ранить существо без ран на ")) {
            int dmg = getNumericAfterText(txt, "Ранить существо без ран на ");
            if (_cr.damage==0){
            _cr.takeDamage(dmg, Creature.DamageSource.ability);
            Main.printToView(_cr.name + " получил " + dmg + " урона.");}
        }
        if (txt.contains("Ранить выбранное существо на ")) {
            int dmg = getNumericAfterText(txt, "Ранить выбранное существо на ");
            _cr.takeDamage(dmg, Creature.DamageSource.ability);
            Main.printToView(_cr.name + " получил " + dmg + " урона.");
        }
        if (txt.contains("Выбранное существо не может атаковать и выступать защитником до конца следующего хода.")) {
            _cr.effects.cantAttackOrBlock=2;
            Main.printToView(_cr.name + " не может атаковать и выступать защитником до конца следующего хода.");
        }
        if (txt.contains("Нанести урон выбранному существу, равный его удару.")) {
            int dmg = _cr.getPower();
            _cr.takeDamage(dmg, Creature.DamageSource.spell);
            Main.printToView(_cr.name + " получил " + dmg + " урона.");
        }
        if (txt.contains("Верните выбранное существо в руку его владельца.")) {
            _cr.returnToHand();
            Main.printToView(_cr.name + " возвращается в руку владельца.");
        }
        if (txt.contains("Отравить+ выбранное существо на ")) {
            int dmg = getNumericAfterText(txt, "Отравить+ выбранное существо на ");
            if (_cr.poison != 0) {
                _cr.poison++;
                Main.printToView(_cr.name + " усилил отравление на " + dmg + ".");
            } else {
                if (_cr.poison <= dmg)
                    _cr.poison = dmg;
                Main.printToView(_cr.name + " получил отравление на " + dmg + ".");
            }
        }
        if (txt.contains("Отравить выбранное существо на ")) {
            int dmg = getNumericAfterText(txt, "Отравить выбранное существо на ");
            if (_cr.poison <= dmg)
                _cr.poison = dmg;
            Main.printToView(_cr.name + " получил отравление на " + dmg + ".");
        }
        if (txt.contains(("Излечить вашего героя на "))) {
            int dmg = getNumericAfterText(txt, "Излечить вашего героя на ");
            _whis.heal(dmg);
            Main.printToView(_whis.playerName + " излечил " + dmg + " урона.");
        }
        if (txt.contains(("Получите * "))) {
            int dmg = getNumericAfterText(txt, "Получите * ");
            _whis.untappedCoin += dmg;
            _whis.totalCoin += dmg;
            Main.printToView(_whis.playerName + " получил " + dmg + " монет.");
        }
        if (txt.contains(("Получите до конца хода * "))) {
            int dmg = getNumericAfterText(txt, "Получите до конца хода * ");
            _whis.untappedCoin += dmg;
            _whis.totalCoin += dmg;
            _whis.temporaryCoin += dmg;
            Main.printToView(_whis.playerName + " получил " + dmg + " монет до конца хода.");
        }
        if (txt.contains(("Ранить каждое существо противника на "))) {
            int dmg = getNumericAfterText(txt, "Ранить каждое существо противника на ");
            int op = Board.opponentN(_whis);
            for (int i = Board.creature.get(op).size() - 1; i >= 0; i--) {
                Board.creature.get(op).get(i).takeDamage(dmg, Creature.DamageSource.ability);
            }
            Main.printToView(_who.name + " ранит всех существ противника на " + dmg + ".");
        }
        if (txt.contains(("Каждое другое существо погибает в конце хода противника."))) {
            int op = Board.opponentN(_whis);
            for (int i = Board.creature.get(op).size() - 1; i >= 0; i--) {
                Board.creature.get(op).get(i).effects.turnToDie=2;
            }
            for (int i = Board.creature.get(_whis.numberPlayer).size() - 1; i >= 0; i--) {
                if (!Board.creature.get(_whis.numberPlayer).get(i).name.equals("Богарт"))
                Board.creature.get(_whis.numberPlayer).get(i).effects.turnToDie=2;
            }
            Main.printToView(_who.name + " чумит весь стол!");
        }
        if (txt.contains(("Ранить каждое существо на "))) {
            int dmg = getNumericAfterText(txt, "Ранить каждое существо на ");
            int op = Board.opponentN(_whis);
            for (int i = Board.creature.get(op).size() - 1; i >= 0; i--) {
                Board.creature.get(op).get(i).takeDamage(dmg, Creature.DamageSource.ability);
            }
            for (int i = Board.creature.get(_whis.numberPlayer).size() - 1; i >= 0; i--) {
                Board.creature.get(_whis.numberPlayer).get(i).takeDamage(dmg, Creature.DamageSource.ability);
            }
            Main.printToView(_who.name + " ранит всех существ на " + dmg + ".");
        }
        if (txt.contains("Взять карт ")) {
            int dmg = getNumericAfterText(txt, "Взять карт ");
            Main.printToView(_who.name + " берет " + dmg + " карт.");
            for (int i=0;i<dmg;i++)
            _whis.drawCard();
        }
        if (txt.contains("Если у соперника больше существ, чем у вас, взять еще карт ")) {
            int dmg = getNumericAfterText(txt, "Если у соперника больше существ, чем у вас, взять еще карт ");
            int n1 = Board.creature.get(0).size();
            int n2 = Board.creature.get(1).size();
            if (n1<n2) {
                Main.printToView(_who.name + " берет " + dmg + " карт.");
                for (int i = 0; i < dmg; i++)
                    _whis.drawCard();
            }
        }
        //target
        if (txt.contains("Выстрел по существу на ")) {
            int dmg = getNumericAfterText(txt, "Выстрел по существу на ");
            Main.printToView(_who.name + " стреляет на " + dmg + " по " + _cr.name);
            if (!_cr.text.contains("Защита от выстрелов."))
                _cr.takeDamage(dmg, Creature.DamageSource.scoot, _who.haveRage());
            else {
                Main.printToView("У " + _cr.name + " защита от выстрелов.");
            }
        }
        if (txt.contains("Выстрел на ")) {
            int dmg = getNumericAfterText(txt, "Выстрел на ");
            if (_cr != null) {
                Main.printToView(_who.name + " стреляет на " + dmg + " по " + _cr.name);
                if (!_cr.text.contains("Защита от выстрелов."))
                    _cr.takeDamage(dmg, Creature.DamageSource.scoot, _who.haveRage());
                else {
                    Main.printToView("У " + _cr.name + " защита от выстрелов.");
                }
            } else {
                Main.printToView(_who.name + " стреляет на " + dmg + " по " + _pl.name);
                _pl.takeDamage(dmg);
            }
        }
    }

    public boolean haveRage() {
        return (text.contains("Гнев.")) ? true : false;
    }
}
