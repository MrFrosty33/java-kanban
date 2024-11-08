import java.util.ArrayList;

public class TaskManager {

    private ArrayList<Task> tasks;



    /*
    1. Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
    2.Методы для каждого из типа задач(Задача/Эпик/Подзадача):
         a. Получение списка всех задач.
        b. Удаление всех задач.
        c. Получение по идентификатору.
        d. Создание. Сам объект должен передаваться в качестве параметра.
        e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        f. Удаление по идентификатору.
    3.Дополнительные методы:
        a. Получение списка всех подзадач определённого эпика.
    4.Управление статусами осуществляется по следующему правилу:
        a. Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией о самой задаче. По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.
        b. Для эпиков:
            - если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
            - если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
            - во всех остальных случаях статус должен быть IN_PROGRESS.





     */
}