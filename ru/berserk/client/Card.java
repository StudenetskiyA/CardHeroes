package ru.berserk.client;

//Created by StudenetskiyA on 30.12.2016.

class Card {
    int cost;
    String id="";
    String name;
    String text;
    String image = "cards/Гном.jpg";//null
    String creatureType;
    int color;//1-swamp,2-field,3-mountain,4-forest,5-dark,6-neutral,7 and more - multicolor
    int type;//1 for spell, 2 for creature
    int targetType;//Battlecry 1 for creatures, 2 for heroes, 3 for heroes and creatures, 4 for only opponent creature, 9 for my creature or hero, 10 as 9, but not self
    int tapTargetType;//May exist cards with Battlecry and TAP. Today its only one)))
    int power;//only for creature, ignore for other
    int hp;//only for creature and hero, its maximum health, not current

    static Card simpleCard = new Card(0, "", "", 0, 0, 0, 0, "", 0, 0);

    static int heroAbilityCost = 0;

    public static boolean isThatAbility(MyFunction.ActivatedAbility.WhatAbility ab) {
        if (ab == MyFunction.ActivatedAbility.whatAbility) return true;
        return false;
    }

    public static boolean isNothingOrDeath() {
        if (MyFunction.ActivatedAbility.whatAbility == MyFunction.ActivatedAbility.WhatAbility.nothing) return true;
        if (MyFunction.ActivatedAbility.whatAbility == MyFunction.ActivatedAbility.WhatAbility.onDeathPlayed) return true;
        if (MyFunction.ActivatedAbility.whatAbility == MyFunction.ActivatedAbility.WhatAbility.onUpkeepPlayed) return true;
        if (MyFunction.ActivatedAbility.whatAbility == MyFunction.ActivatedAbility.WhatAbility.onOtherDeathPlayed) return true;
        return false;
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
        creatureType = _card.creatureType;
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
        creatureType = _crtype;
    }

    public static Card getCardByName(String name) {
        //Here is all cards!
        switch (name) {
            case "Тарна":
                return new Card(0, "Тарна", "", 1, 0, 0, 0, "ТАП:4 Взять карт 1.", 0, 28);
            case "Рэйвенкар":
                return new Card(0, name, "", 5, 0, 0, 0, "ТАП:4 Ранить героя противника на 2. Излечить вашего героя на 2.", 0, 24);
            case "Бьорнбон":
                return new Card(0, name, "", 3, 0, 0, 0, "ТАП:0 Получить щит ББ.", 0, 30);
            case "Тиша":
                return new Card(0, "Тиша", "", 1, 0, 0, 1, "ТАПТ:2 Отравить+ выбранное существо на 1.", 0, 26);
            case "Свирепый резак":
                return new Card(0, name, "", 2, 0, 0, 1, "ТАПТ:2 Выбранное существо получает 'Опыт в атаке, Рывок'.", 0, 28);
            case "Эндор Флем":
                return new Card(0, name, "", 2, 0, 0, 7, "ТАПТ:3 Ранить выбранное существо на 1, Взять карт 1.", 0, 26);
            case "Руах":
                return new Card(0, name, "", 2, 0, 0, 1, "ТАПТ:1 Ранить на половину жизней выбранное существо.", 0, 25);
            case "Раскат грома":
                return new Card(1, "Раскат грома", "", 3, 1, 1, 0, "Ранить выбранное существо на 3.", 0, 0);
            case "Выброс силы":
                return new Card(2, name, "", 3, 1, 1, 0, "Ранить выбранное существо на 5.", 0, 0);
            case "Неудача":
                return new Card(1, name, "", 5, 1, 1, 0, "Ранить на остаток выбранное существо и своего героя на столько же.", 0, 0);
            case "Возрождение":
                return new Card(1, name, "", 5, 1, 0, 0, "Раскопать тип 2.", 0, 0);
            case "Гьерхор":
                return new Card(1, "Гьерхор", "Йордлинг", 3, 2, 0, 0, "", 2, 2);
            case "Алчущие крови":
                return new Card(2, name, "Слуа", 5, 2, 10, 0, "Направленный удар. Наймт: Жажда 1.", 3, 3);
            case "Змееуст":
                return new Card(1, name, "Слуа", 5, 2, 10, 0, "Защита от заклинаний. Наймт: Жажда 2.", 3, 2);
            case "Лики судьбы":
                return new Card(3, name, "Пустой", 6, 2, 0, 0, "Найм: Лики-абилка.", 2, 3);
            case "Найтин":
                return new Card(2, name, "", 6, 2, 0, 0, "Направленный удар. Рывок.", 2, 2);
            case "Кригторн":
                return new Card(2, name, "", 3, 2, 0, 0, "Первый удар. Рывок.", 2, 1);
            case "Гном":
                return new Card(2, name, "Гном", 3, 2, 0, 0, "", 3, 3);
            case "Секретное существо":
                return new Card(2, name, "Гном", 3, 2, 0, 0, "Рывок", 3, 3);
            case "Супергипноз":
                return new Card(1, name, "", 1, 1, 1, 0, "Взять под контроль выбранное существо.", 0, 0);
            case "Гном-легионер":
                return new Card(4, name, "Гном", 3, 2, 0, 0, "Направленный удар. Рывок.", 3, 5);
            case "Гном-смертник":
                return new Card(3, name, "Гном", 3, 2, 0, 0, "Защита от заклинаний. Рывок.", 3, 4);
            case "Цепной пес":
                return new Card(1, name, "Зверь", 2, 2, 0, 0, "Орда. Статичный эффект. Получает за каждого другого Цепного пса рывок и +1 к удару.", 1, 2);
            case "Амбрадор":
                return new Card(1, name, "Зверь", 6, 2, 12, 0, "В начале вашего хода: Верните выбранное существо в руку его владельца.", 4, 3);
            case "Трюкач":
                return new Card(1, name, "", 6, 2, 0, 0, "", 4, 5);
            case "Поглощение души":
                return new Card(3, "Поглощение душ", "", 5, 1, 2, 0, "Ранить выбранного героя на 3. Излечить вашего героя на 3.", 0, 0);
            case "Эльф-дозорный":
                return new Card(4, "Эльф-дозорный", "", 4, 2, 0, 0, "Найм: Возьмите карт 1.", 2, 5);
            case "Послушник":
                return new Card(5, "Послушник", "Лингунг", 3, 2, 1, 0, "Наймт: Выстрел по существу на 4.", 2, 3);
            case "Гном-лучник":
                return new Card(3, "Гном-лучник", "Гном", 3, 2, 3, 0, "Защита от выстрелов. Наймт: Выстрел на 2.", 2, 3);
            case "Лучник Захры":
                return new Card(4, "Лучник Захры", "Орк", 2, 2, 3, 0, "Защита от заклинаний. Наймт: Выстрел на 2.", 4, 2);
            case "Жрец клана":
                return new Card(2, name, "Орк", 2, 2, 0, 0, "Рывок.", 3, 2);
            case "Молодой орк":
                return new Card(1, name, "Орк", 2, 2, 0, 0, "", 3, 1);
            case "Орк-провокатор":
                return new Card(1, name, "Орк", 2, 2, 0, 0, "Рывок.", 2, 1);
            case "Цверг-заклинатель":
                return new Card(3, name, "Гном", 3, 2, 0, 0, "Защита от заклинаний. Защита от выстрелов. Защита от отравления.", 3, 3);
            case "Верцверг":
                return new Card(4, name, "Гном", 3, 2, 1, 0, "Направленный удар. Наймт: Получает к атаке + 3.", 2, 4);
            case "Цепная молния":
                return new Card(6, "Цепная молния", "", 3, 1, 0, 0, "Ранить каждое существо противника на 3.", 0, 0);
            case "Волна огня"://Fix it
                return new Card(3, "Волна огня", "", 2, 1, 0, 0, "Ранить каждое существо на 5.", 0, 0);
            case "Чешуя дракона":
                return new Card(2, "Чешуя дракона", "", 4, 1, 0, 0, "Получите * 1.", 0, 0);
            case "Выслеживание":
                return new Card(0, "Выслеживание", "", 4, 1, 0, 0, "Получите до конца хода * 2.", 0, 0);
            case "Фиал порчи":
                return new Card(2, "Фиал порчи", "", 1, 1, 1, 0, "Отравить выбранное существо на 2.", 0, 0);
            case "Глашатай пустоты":
                return new Card(1, "Глашатай пустоты", "Пустой", 6, 2, 0, 0, "Уникальность. Не получает ран.", 0, 1);
            case "Мастер теней":
                return new Card(1, name, "Наемник", 6, 2, 0, 0, "Найм: Посмотрите топдек противника, можете положить его на кладбище.", 2, 1);
            case "Велит":
                return new Card(2, "Велит", "", 2, 2, 0, 3, "ТАПТ: Выстрел на 1.", 1, 3);
            case "Пуф":
                return new Card(2, name, "Гном", 3, 2, 0, 3, "ТАПТ: Выстрел на 1.", 2, 3);
            case "Кьелэрн":
                return new Card(1, "Кьелэрн", "", 6, 2, 0, 0, "Уникальность. Рывок. ТАП: Получите до конца хода * 1.", 0, 1);
            case "Агент Разана":
                return new Card(2, "Агент Разана", "", 1, 2, 4, 0, "Наймт: Отравить выбранное существо на 1.", 1, 2);
            case "Скованный еретик":
                return new Card(1, "Скованный еретик", "", 5, 2, 0, 0, "Найм: Закрыться.", 3, 2);
            case "Вэлла":
                return new Card(3, "Вэлла", "", 4, 2, 3, 0, "Наймт: Излечить выбранное существо или героя на 2.", 3, 4);
            case "Рыцарь Туллена":
                return new Card(6, "Рыцарь Туллена", "", 2, 2, 0, 0, "Броня 3.", 6, 3);
            case "Орк-лучник":
                return new Card(1, name, "", 2, 2, 3, 0, "Гнев. Наймт: Выстрел на 1.", 1, 1);
            case "Нгонасах":
                return new Card(2, name, "Орк", 2, 2, 13, 0, "Рывок. Наймт: Выбранное существо получает 'Направленный удар'.", 2, 2);
            case "Орк-мститель":
                return new Card(2, name, "Орк", 2, 2, 0, 0, "При гибели другого вашего существа: Поиск (0,0, ,0,0,Орк-мститель).", 3, 2);
            case "Мастер поединка":
                return new Card(6, name, "Орк", 2, 2, 1, 0, "Наймт: Уничтожьте по стоимости 2.", 6, 5);
            case "Раптор":
                return new Card(8, name, "Орк", 2, 2, 0, 0, "Найм: Уничтожьте каждое по стоимости 3.", 5, 9);
            case "Вождь клана":
                return new Card(5, name, "Орк", 2, 2, 0, 0, "Статичный эффект.", 5, 5);
            case "Ожившее пламя":
                return new Card(1, name, "", 2, 1, 1, 0, "Ранить выбранное существо на 1. Получает к атаке + 2.", 0, 0);
            case "Пылающий исполин":
                return new Card(6, name, "Орк", 2, 2, 0, 0, "Направленный удар. Статичный эффект.", 6, 6);
            case "Огонь прародителя":
                return new Card(7, name, "", 3, 1, 3, 0, "Ранить выбранное существо на 8.", 0, 0);
            case "Плетение огня":
                return new Card(3, name, "", 3, 1, 1, 0, "Ранить выбранное существо на 4.", 0, 0);
            case "Рубака клана":
                return new Card(3, name, "Орк", 2, 2, 0, 0, "Направленный удар.", 5, 3);
            case "Багатур":
                return new Card(6, name, "Орк", 2, 2, 0, 0, "Направленный удар. Гнев. Первый удар. Рывок.", 6, 2);
            case "Тарантул":
                return new Card(2, name, "Койар", 6, 2, 0, 0, "Защита от атак стоимость менее 3.", 4, 2);
            case "Огненный щит":
                return new Card(2, name, "", 2, 1, 1, 0, "Выбранное существо до конца следующего хода получает 'Не получает от ударов ран'. Возьмите карт 1.", 0, 0);
            case "Орк-егерь":
                return new Card(3, name, "Орк", 2, 2, 1, 0, "Наймт: Выбор - открыть/закрыть существо.", 4, 2);
            case "Безумие":
                return new Card(3, name, "", 1, 1, 1, 0, "Нанести урон выбранному существу, равный его удару.", 0, 0);
            case "Зельеварение":
                return new Card(1, name, "", 1, 1, 1, 0, "Верните выбранное существо в руку его владельца.", 0, 0);
            case "Дахут":
                return new Card(3, name, "", 1, 2, 1, 0, "Наймт: Верните выбранное существо в руку его владельца.", 2, 3);
            case "Забира":
                return new Card(2, "Забира", "", 1, 2, 0, 0, "Если выбрана целью заклинание - погибает.", 3, 4);
            case "Волнорез":
                return new Card(3, name, "", 1, 2, 0, 0, "Если выбрана целью заклинание - погибает.", 4, 5);
            case "Десница Архааля":
                return new Card(4, name, "", 1, 2, 1, 0, "Опыт в защите. Наймт: Уничтожьте отравленное существо.", 1, 4);
            case "Нойта":
                return new Card(1, name, "Йордлинг", 1, 2, 1, 0, "Наймт: Ранить существо без ран на 3.", 1, 1);
            case "Орк-мародер":
                return new Card(5, name, "", 2, 2, 0, 0, "Опыт в атаке. Первый удар. Рывок.", 5, 2);
            case "Менгир Каррефура":
                return new Card(3, name, "", 1, 2, 0, 1, "ТАПТ: Отравить+ выбранное существо на 1.", 0, 10);
            case "Рыцарь реки":
                return new Card(5, name, "", 1, 2, 1, 0, "Наймт: Выбранное существо не может атаковать и выступать защитником до конца следующего хода.", 4, 6);
            case "Поиск кладов":
                return new Card(6, name, "", 1, 1, 0, 0, "Взять карт 4.", 0, 0);
            case "Прозрение":
                return new Card(2, name, "", 1, 1, 0, 0, "Взять карт 1. Если у соперника больше существ, чем у вас, взять еще карт 1.", 0, 0);
            case "Плащ Исхара":
                return new Card(1, name, "Броня", 1, 3, 0, 0, "", 0, 6);
            case "Богарт":
                return new Card(4, name, "", 6, 2, 0, 0, "Уникальность. Найм: Каждое другое существо погибает в конце хода противника.", 2, 7);
            case "Полевица":
                return new Card(4, name, "", 1, 2, 0, 0, "Гибель: Взять карт 2.", 2, 3);
            case "Смайта":
                return new Card(4, name, "", 6, 2, 3, 0, "Гибельт: Ранить выбранное существо или героя на 2.", 4, 3);
            case "Вестник смерти":
                return new Card(1, name, "Слуа", 5, 2, 99, 0, "Гибельт: Сбросьте карту.", 3, 3);
            case "Падальщик пустоши"://fix
                return new Card(1, name, "Зверь", 6, 2, 4, 0, "При гибели в ваш ход другого вашего существа: Ранить выбранное существо на 2.", 1, 2);
            case "Ядовитое пламя":
                return new Card(0, name, "", 1, 1, 1, 0, "Доплатите Х *. Ранить выбранное существо на ХХХ.", 0, 0);
            case "Вольный воитель":
                return new Card(0, name, "", 6, 2, 0, 0, "Доплатите Х *. Найм: Получает к характеристикам + ХХХ.", 0, 0);
            case "Шар тины":
                return new Card(2, name, "", 1, 1, 0, 0, "Поиск цвет 1.", 0, 0);
            case "Шар бури":
                return new Card(2, name, "", 2, 1, 0, 0, "Поиск (0,2, ,0,0, ).", 0, 0);
            case "Карта сокровищ":
                return new Card(2, name, "", 6, 1, 0, 0, "Поиск цвет 6.", 0, 0);
            case "Шар молний":
                return new Card(2, name, "", 3, 1, 0, 0, "Поиск цвет 3.", 0, 0);
            case "Гном-кузнец":
                return new Card(3, name, "Гном", 1, 2, 0, 0, "Найм: Поиск тип 3.", 1, 4);
            case "Гном-кладоискатель":
                return new Card(5, name, "Гном", 3, 2, 0, 0, "Броня 1. Найм: Поиск комбо+ 2 Гном 2.", 5, 4);
            case "Шаман племени ворона":
                return new Card(1, name, "Наемник", 6, 2, 0, 0, "Найм: Поиск ТС 2 2.", 1, 1);
            case "Дух Эллиона":
                return new Card(1, name, "Дух", 6, 2, 0, 0, "Найм: Потеряйте * 1.", 3, 4);
            case "Рунопевец":
                return new Card(3, name, "Гном", 3, 2, 0, 0, "Статичный эффект.", 3, 3);
            case "Гном-каратель":
                return new Card(4, name, "Гном", 3, 2, 0, 3, "Броня 2. ТАПТ: Ранить выбранное существо или героя на 2.", 1, 3);
            case "Тан гномов":
                return new Card(6, name, "Гном", 3, 2, 0, 0, "Броня 2. Статичный эффект.", 5, 4);
            case "Безумный охотник":
                return new Card(5, name, "", 6, 2, 0, 0, "Найм: Получает +Х к удару и Броню Х, где Х - число других ваших существ.", 4, 4);
            case "Браслет подчинения":
                return new Card(3, name, "Амулет", 1, 3, 0, 0, "", 0, 0);
            case "Молот прародителя":
                return new Card(2, name, "Оружие", 3, 3, 0, 1, "ТАПТ: Выбранное существо до конца хода получает к атаке + 2.", 0, 0);
            case "Орочий ятаган":
                return new Card(3, name, "Оружие", 2, 3, 0, 0, "Статичный эффект.", 0, 0);
            case "Аккения":
                return new Card(4, name, "Событие", 2, 4, 0, 0, "Статичный эффект.", 0, 0);
            case "Пустошь Тул-Багара":
                return new Card(1, name, "Событие", 5, 4, 0, 0, "Статичный эффект.", 0, 0);
            case "Гипноз":
                return new Card(7, name, "", 1, 1, 0, 0, "Противник выбирает существо, оно переходит под ваш контроль.", 0, 0);
            case "Дурные советы":
                return new Card(5, name, "", 1, 1, 0, 0, "Противник выбирает существо по стоимости, оно переходит под ваш контроль, стоимость не больше 3.", 0,0);
            case "Брат по оружию":
                return new Card(3, name, "Инквизитор", 6, 2, 21, 0, "Наймт: Уничтожить выбранную экипировку.", 3, 3);
            case "Сдерживающий":
                return new Card(3, name, "Драконид", 6, 2, 22, 0, "Наймт: Уничтожить выбранную экипировку.", 5, 5);
            case "Кутила":
                return new Card(10, name, "Пират", 6, 2, 0, 0, "Статичный эффект.", 6, 6);
            default:
                System.out.println("Ошибка - Неопознанная карта:" + name);
                return null;
        }
    }

    public static Card getCardFromHandById(Player pl,String _id){
        for (int i=0;i<pl.cardInHand.size();i++){
            if (pl.cardInHand.get(i).id.equals(_id)) return pl.cardInHand.get(i);
        }
        return null;
    }

    int getCost(Player pl) {
        int effectiveCost = cost;
        // Gnome cost less
        if (creatureType.equals("Гном")) {
            int runopevecFounded = 0;
            for (int i = 0; i < Board.creature.get(pl.numberPlayer).size(); i++) {
                if (Board.creature.get(pl.numberPlayer).get(i).name.equals("Рунопевец"))
                    runopevecFounded++;
            }
            effectiveCost -= runopevecFounded;
        }

        if (name.equals("Трюкач")) {
            effectiveCost += pl.cardInHand.size() - 1;
        }
        if (name.equals("Кутила")) {
            effectiveCost -= pl.cardInHand.size();
        }

        return effectiveCost;
    }
}
