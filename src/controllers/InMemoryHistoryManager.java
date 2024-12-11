package controllers;

import interfaces.HistoryManager;
import models.Task;
import utils.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    //TODO история должна храниться в LinkedList, сделать класс свой.
    private HandMadeLinkedList<Task> taskList = new HandMadeLinkedList<>();
    private HashMap<Integer, Node<Task>> historyMap = new HashMap<>();

    /**
     * ----- HistoryManager -----
     */

    @Override
    public List<Task> getHistory() {
        List<Task> historyCopy = taskList.getTasks();
        return historyCopy;
    }

    @Override
    public void add(Task task) {
        //TODO что делать, если повторяется просмотр?
        // Удалять из taskList, или только обновить ссылку на Node в historyMap?
        if (task == null) {
            return;
        }
        taskList.linkLast(task);
        historyMap.put(task.getId(), taskList.getTail());
    }

    @Override
    public void remove(int id) {
        //TODO тут удаляется за О(1), соответственно только из historyMap удаляется Node
    }

    public void removeNode(Node<Task> node){
        //TODO надо ли при этом удалять его из мапы? Чтобы там не хранился уже несуществующий Node
        taskList.unlink(node);
    }

    class HandMadeLinkedList<T> {

        private Node<T> head;
        private Node<T> tail;
        private int size = 0;

        public Node<T> getHead() {
            return head;
        }

        public Node<T> getTail() {
            return tail;
        }

        public void linkLast(T element) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            size++;
        }

        public ArrayList<T> getTasks(){
            ArrayList<T> result = new ArrayList<>(size);

            for (Node<T> x = head; x != null; x = x.next) {
                result.add(x.data);
            }

            return result;
        }

        void unlink(Node<T> x) {
            final T data = x.data;
            final Node<T> next = x.next;
            final Node<T> prev = x.prev;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                x.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                x.next = null;
            }

            x.data = null;
            size--;
        }


    }

}
