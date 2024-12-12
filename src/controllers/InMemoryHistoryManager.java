package controllers;

import interfaces.HistoryManager;
import models.Epic;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

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
        if (task == null)
            return;

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
            final Node<Task> node = historyMap.get(id);
            removeNode(node);
        }
    }

    public void removeNode(Node<Task> node) {
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

    class Node<Task> {

        Task data;
        Node<Task> next;
        Node<Task> prev;

        Node(Node<Task> prev, Task data, Node<Task> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    class HandMadeLinkedList<Task> {

        Node<Task> head;
        Node<Task> tail;
        int size = 0;

        Node<Task> getHead() {
            return head;
        }

        Node<Task> getTail() {
            return tail;
        }

        void moveToTail(Node<Task> node) {
            if (node == tail)
                return;

            final Node<Task> oldTail = tail;
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
            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            size++;
        }

        ArrayList<Task> getTasks() {
            ArrayList<Task> result = new ArrayList<>(size);

            for (Node<Task> x = head; x != null; x = x.next) {
                result.add(x.data);
            }

            return result;
        }

        void unlink(Node<Task> x) {
            final Task data = x.data;
            final Node<Task> next = x.next;
            final Node<Task> prev = x.prev;

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
