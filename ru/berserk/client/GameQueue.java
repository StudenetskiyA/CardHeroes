package ru.berserk.client;

/**
 * Created by StudenetskiyA on 23.01.2017.
 */

public class GameQueue
{
    static class QueueEvent {
        String whatToDo;
        Creature targetCr;
        int howMany;

        public QueueEvent(String _what, Creature _tc,int _howMany) {
            whatToDo = _what;
            targetCr=_tc;
            howMany=_howMany;
        }
    }

    // Указатель на первый элемент
    private ObjectBox head = null;
    // Указатель на последний элемент
    private ObjectBox tail = null;
    // Поле для хранения размера очереди
    private int size = 0;

    public void push(QueueEvent obj) {
        // Сразу создаем вспомогательный объект и помещаем новый элемент в него
        ObjectBox ob = new ObjectBox();
        ob.setObject(obj);
        // Если очередь пустая - в ней еще нет элементов
        if (head == null) {
            // Теперь наша голова указывает на наш первый элемент
            head = ob;
        } else {
            // Если это не первый элемент, то надо, чтобы последний элемент в очереди
            // указывал на вновь прибывший элемент
            tail.setNext(ob);
        }
        // И в любом случае нам надо наш "хвост" переместить на новый элемент
        // Если это первый элемент, то "голова" и "хвост" будут указывать на один и тот же элемент
        tail = ob;
        // Увеличиваем размер нашей очереди
        size++;
    }
//
//    public void responseAllQueue(){
//        Main.memPlayerStatus=Main.isMyTurn;
//        while (Main.gameQueue.size() != 0) {
//            GameQueue.QueueEvent event = Main.gameQueue.pull();
//            Main.readyQueue=false;
//            System.out.println("next queue response");
//            if (event.whatToDo.equals("Die")) {
//                if (Board.creature.get(event.targetCr.owner.numberPlayer).contains(event.targetCr)) {
//                    Main.printToView(0, event.targetCr.name + " умирает.");
//
//                    event.targetCr.owner.massDieCheckNeededTarget();
//
//                    System.out.println(event.targetCr.name + " удален/" + event.targetCr.owner.playerName);
//                    Board.creature.get(event.targetCr.owner.numberPlayer).remove(event.targetCr);
//                }
//            }
//            else if (event.whatToDo.equals("Upkeep")) {
//                if (Board.creature.get(event.targetCr.owner.numberPlayer).contains(event.targetCr)) {
//                    event.targetCr.owner.massUpkeepCheckNeededTarget();
//                }
//            }
//            else if (event.whatToDo.equals("Summon")) {
//                if (Board.creature.get(event.targetCr.owner.numberPlayer).contains(event.targetCr)) {
//                    event.targetCr.owner.massSummonCheckNeededTarget();
//                }
//            }
//        }
//        Main.isMyTurn=Main.memPlayerStatus;
//        synchronized (Main.queueMonitor) {
//            readyQueue = true;
//            Main.queueMonitor.notifyAll();
//        }
//    }
//

    public QueueEvent pull() {
        // Если у нас нет элементов, то возвращаем null
        if (size == 0) {
            return null;
        }
        // Получаем наш объект из вспомогательного класса из "головы"
        QueueEvent obj = head.getObject();
        // Перемещаем "голову" на следующий элемент
        head = head.getNext();
        // Если это был единственный элемент, то head станет равен null
        // и тогда tail (хвост) тоже дожен указать на null.
        if (head == null) {
            tail = null;
        }
        // Уменьшаем размер очереди
        size--;
        // Возвращаем значение
        return obj;
    }

    public Object get(int index) {
        // Если нет элементов или индекс больше размера или индекс меньше 0
        if(size == 0 || index >= size || index < 0) {
            return null;
        }
        // Устанавлваем указатель, который будем перемещать на "голову"
        ObjectBox current = head;
        // В этом случае позиция равну 0
        int pos = 0;
        // Пока позиция не достигла нужного индекса
        while(pos < index) {
            // Перемещаемся на следующий элемент
            current = current.getNext();
            // И увеличиваем позицию
            pos++;
        }
        // Мы дошли до нужной позиции и теперь можем вернуть элемент
        QueueEvent obj = current.getObject();
        return obj;
    }

    public int size() {
        return size;
    }

    // Наш вспомогательный класс будет закрыт от посторонних глаз
    private class ObjectBox
    {
        // Поле для хранения объекта
        private QueueEvent object;
        // Поле для указания на следующий элемент в цепочке.
        // Если оно равно NULL - значит это последний элемент
        private ObjectBox next;

        public QueueEvent getObject() {
            return object;
        }

        public void setObject(QueueEvent object) {
            this.object = object;
        }

        public ObjectBox getNext() {
            return next;
        }

        public void setNext(ObjectBox next) {
            this.next = next;
        }
    }
}
