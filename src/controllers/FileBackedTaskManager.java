package controllers;

import exceptions.ManagerSaveException;
import interfaces.HistoryManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private File myFile;
    private HistoryManager history;
    private InMemoryTaskManager manager;

    public FileBackedTaskManager(File myFile, InMemoryTaskManager manager) {
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
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return copyPath.toFile();
    }

    public void save() {
        // Пример сохранения в CSV: id,type,name,status,description,epic
        try (FileWriter fw = new FileWriter(myFile); BufferedWriter bw = new BufferedWriter(fw)) {
            String title = "id,type,name,status,description,epic" + "\n";
            bw.write(title);

            for (Task task : history.getHistory()) {
                bw.write(task.toString() + "\n");
            }

        } catch (IOException ex) {
            throw new ManagerSaveException(ex.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file, InMemoryTaskManager manager) {
        // Делаю копию, потому что во время чтения файла пропадает вся информация из него.
        File fileCopy = copyFile(file);

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

                if (nextLine[1].equals("EPIC")) {
                    Epic epic = new Epic(nextLine[2], nextLine[4]);
                    //name,      descr
                    epics.add(epic);
                } else if (nextLine[1].equals("SUBTASK")) {
                    Subtask subtask = new Subtask(nextLine[2], nextLine[4], status, Integer.valueOf(nextLine[5]));
                    //name,      descr,    status,       epic id
                    subtasks.add(subtask);
                } else {
                    Task task = new Task(nextLine[2], nextLine[4]);
                    //name,      descr
                    tasks.add(task);
                }
            }

            // сперва надо класть эпики, иначе сабтаскам не к кому прикрепляться
            for (Task task : tasks) {
                fileBackedTaskManager.addTask(task);
            }

            for (Epic epic : epics) {
                fileBackedTaskManager.addEpic(epic);
            }

            for (Subtask subtask : subtasks) {
                fileBackedTaskManager.addSubtask(subtask);
            }

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