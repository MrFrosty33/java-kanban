package controllers;

import interfaces.HistoryManager;
import models.Task;

public class InMemoryHistoryManager implements HistoryManager {

    private int nextIndexHistory = 0;
    private Task[] history = new Task[10];
    /*
     Как понял, ТЗ требует обычный массив. Была идея, сделать листом, и просто,
     в случае если это уже 11-й эллемент массива, стирать первый. Сортировалось бы автоматически тогда.
     Мне кажется, так было бы проще, не пришлось писать код для сортировки

     */

    /**
     * ----- HistoryManager -----
     */

    @Override
    public Task[] getHistory() {
        return history;
    }

    @Override
    public void addInHistory(Task task) {
        if(nextIndexHistory <= history.length-1){
            history[nextIndexHistory++] = task;
        } else {
            for(int i = 0; i < history.length - 2; i++){
                history[i] = history [i + 1];
            }
            history[history.length - 1] = task;
        }

    }
}
