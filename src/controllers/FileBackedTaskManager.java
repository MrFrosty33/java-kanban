package controllers;

import exceptions.ManagerSaveException;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private File myFile;
    private HistoryManager history;
    private TaskManager manager;

    public FileBackedTaskManager(File myFile, TaskManager manager) {
        this.myFile = myFile;
        this.manager = manager;
        this.history = manager.getHistory();
    }


    public FileBackedTaskManager(File myFile) {
        this.myFile = myFile;
        this.history = super.getHistory();
    }

    public static File copyFile(File file) {
        StringBuilder copy = new StringBuilder(file.getPath());
        copy.insert(copy.lastIndexOf(".csv"), "_Copy");
        Path copyPath = Path.of(copy.toString());
        try {
            Files.copy(file.toPath(), copyPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return copyPath.toFile();
    }

    public void save() {
        // Пример сохранения в CSV: id,type,name,status,description,duration,startTime,endTime,epic
        try (FileWriter fw = new FileWriter(myFile); BufferedWriter bw = new BufferedWriter(fw)) {
            String title = "id,type,name,status,description,duration,startTime,endTime,epic" + "\n";
            bw.write(title);

            for (Task task : history.getHistory()) {
                bw.write(task.toString() + "\n");
            }

        } catch (IOException ex) {
            throw new ManagerSaveException(ex.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file, TaskManager manager) {
        // Делаю копию, потому что во время чтения файла пропадает вся информация из него.
        // Таким образом на данный момент пропадают данные из копии, а файл копии в конце удаляю
        File fileCopy = copyFile(file);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy|HH:mm");

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(fileCopy);
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<Epic> epics = new ArrayList<>();
        ArrayList<Subtask> subtasks = new ArrayList<>();

        try (FileReader fr = new FileReader(fileCopy); BufferedReader br = new BufferedReader(fr)) {
            br.readLine(); // пропускаем первую стоку с оглавлением

            while (br.ready()) {
                String next = br.readLine();
                String[] nextLine = next.split(",");
                Status status;

                if (nextLine[3].equals("NEW")) {
                    status = Status.NEW;
                } else if (nextLine[3].equals("DONE")) {
                    status = Status.DONE;
                } else {
                    status = Status.IN_PROGRESS;
                }


                //                  0   1    2    3        4          5         6        7     8
                // String title = "id,type,name,status,description,duration,startTime,endTime,epic" + "\n";

                boolean hasTime = true; // могут быть случае, где не будет указано время
                if (nextLine[5].equals("xxx") || nextLine[6].equals("xxx") || nextLine[7].equals("xxx")) {
                    hasTime = false;
                }

                if (nextLine[1].equals("EPIC")) {
                    Epic epic;
                    if (hasTime) {
                        epic = new Epic(nextLine[2], //name
                                nextLine[4], //descr
                                Duration.parse(nextLine[5]), //duration
                                LocalDateTime.parse(nextLine[6], formatter), //startTime
                                LocalDateTime.parse(nextLine[7], formatter)); //endTime
                    } else {
                        epic = new Epic(nextLine[2], //name
                                nextLine[4]); //descr
                    }
                    epics.add(epic);
                } else if (nextLine[1].equals("SUBTASK")) {
                    Subtask subtask;
                    if (hasTime) {
                        subtask = new Subtask(nextLine[2], //name
                                nextLine[4], //descr
                                status,
                                Duration.parse(nextLine[5]), //duration
                                LocalDateTime.parse(nextLine[6], formatter), //startTime
                                Integer.valueOf(nextLine[8])); //epic id
                    } else {
                        subtask = new Subtask(nextLine[2], //name
                                nextLine[4], //descr
                                status,
                                Integer.valueOf(nextLine[8])); //epic id
                    }
                    subtasks.add(subtask);
                } else {
                    Task task;
                    if (hasTime) {
                        task = new Task(nextLine[2], //name
                                nextLine[4], //descr
                                status,
                                Duration.parse(nextLine[5]), //duration
                                LocalDateTime.parse(nextLine[6], formatter)); //startTime
                    } else {
                        task = new Task(nextLine[2], //name
                                nextLine[4], //descr
                                status);
                    }
                    tasks.add(task);
                }
            }

            // сперва надо класть эпики, иначе сабтаскам не к кому прикрепляться
            for (Epic epic : epics) {
                fileBackedTaskManager.addEpic(epic);
            }

            for (Task task : tasks) {
                fileBackedTaskManager.addTask(task);
            }

            for (Subtask subtask : subtasks) {
                fileBackedTaskManager.addSubtask(subtask);
            }

            // принудительно закрываю, несмотря на try-with-resources
            // иначе не может получить к доступу, мол, он ещё используется
            fr.close();
            br.close();
            Files.delete(fileCopy.toPath());
            return fileBackedTaskManager;

        } catch (IOException ex) {
            throw new ManagerSaveException(ex.getMessage());
        }
    }


    /**
     * ----- Tasks -----
     */

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    /**
     * ----- Subtasks -----
     */

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    /**
     * ----- Epics -----
     */

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

}