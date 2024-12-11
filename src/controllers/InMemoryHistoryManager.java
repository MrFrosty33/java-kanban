package controllers;

import interfaces.HistoryManager;
import models.Epic;
import models.Task;
import utils.Node;

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
            moveToTail(task);
        } else {
            taskList.linkLast(task);
            historyMap.put(task.getId(), taskList.getTail());
        }
    }

    @Override
    public void remove(int id) {
        final Node<Task> node = historyMap.get(id);
        removeNode(node);
    }

    public void moveToTail(Task task) {
        // Этот метод является решением следующей проблемы: если в истории был просмотрен эпик с подзадачами,
        // то при его удалении и восстановлении последующем в конце списка, удалятся из истории просмотренные подзадачи.
        final Node<Task> node = historyMap.get(task.getId());
        historyMap.remove(node.data.getId());
        taskList.unlink(node);
        taskList.linkLast(task);
        historyMap.put(task.getId(), taskList.getTail());
    }

    public void removeNode(Node<Task> node) {
        if(node.data instanceof Epic && !((Epic) node.data).getSubtaskIds().isEmpty()){
            ArrayList<Integer> subtasks = ((Epic) node.data).getSubtaskIds();
            for (Integer id : subtasks) {
                remove(id);
            }
        }

        historyMap.remove(node.data.getId());
        taskList.unlink(node);
    }

    class HandMadeLinkedList<T> {

        Node<T> head;
        Node<T> tail;
        int size = 0;

        Node<T> getHead() {
            return head;
        }

        Node<T> getTail() {
            return tail;
        }


        void linkLast(T element) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            size++;
        }

        ArrayList<T> getTasks() {
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
