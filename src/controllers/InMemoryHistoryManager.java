package controllers;

import interfaces.HistoryManager;
import models.Epic;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private HandMadeLinkedList taskList = new HandMadeLinkedList();
    private HashMap<Integer, Node> historyMap = new HashMap<>();

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
        if (task == null) return;

        if (historyMap.containsKey(task.getId())) {
            taskList.moveToTail(historyMap.get(task.getId()));
        } else {
            taskList.linkLast(task);
            historyMap.put(task.getId(), taskList.getTail());
        }
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            final Node node = historyMap.get(id);
            removeNode(node);
        }
    }

    public void removeNode(Node node) {
        if (node.data instanceof Epic && !((Epic) node.data).getSubtaskIds().isEmpty()) {
            ArrayList<Integer> subtasks = ((Epic) node.data).getSubtaskIds();
            for (Integer id : subtasks) {
                remove(id);
            }
        }

        historyMap.remove(node.data.getId());
        taskList.unlink(node);
    }

    /**
     * ----- Вложенные классы -----
     */

    class Node {

        Task data;
        Node next;
        Node prev;

        Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    class HandMadeLinkedList {

        Node head;
        Node tail;
        int size = 0;

        Node getHead() {
            return head;
        }

        Node getTail() {
            return tail;
        }

        void moveToTail(Node node) {
            if (node == tail) return;

            final Node oldTail = tail;
            oldTail.next = node;
            tail = node;

            if (node == head) {
                node.next.prev = null;
                head = node.next;
            } else {
                node.next.prev = node.prev;
                node.prev.next = node.next;
            }

            node.prev = oldTail;
            node.next = null;
        }


        void linkLast(Task element) {
            final Node oldTail = tail;
            final Node newNode = new Node(oldTail, element, null);
            tail = newNode;
            if (oldTail == null) head = newNode;
            else oldTail.next = newNode;
            size++;
        }

        ArrayList<Task> getTasks() {
            ArrayList<Task> result = new ArrayList<>(size);

            for (Node x = head; x != null; x = x.next) {
                result.add(x.data);
            }

            return result;
        }

        void unlink(Node x) {
            final Task data = x.data;
            final Node next = x.next;
            final Node prev = x.prev;

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
