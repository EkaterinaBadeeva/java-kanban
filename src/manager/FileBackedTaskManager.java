package manager;

import model.*;

import java.io.*;

import java.util.ArrayList;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private String fileName;
    public FileBackedTaskManager(String fileName) {
        super();
        this.fileName = fileName;
    }


    // Метод, который сохраняет все задачи, подзадачи и эпики
    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            List<Task> allTasks = getAllOfTask();
            List<Epic> allEpic = getAllOfEpic();
            List<Subtask> allSubtasks = getAllOfSubtask();

            bw.write("ID,TYPE,NAME,NEW,DESCRIPTION,EPIC\n");

            if (allTasks != null) {
                for (Task task : allTasks) {
                    String taskAsString = task.toString(); // метод toString
                    bw.write(taskAsString + "\n");
                }
            }

            if (allSubtasks != null) {
                for (Subtask subtask : allSubtasks) {
                    String subtaskAsString = subtask.toString(); // метод toString
                    bw.write(subtaskAsString + "\n");
                }
            }

            if (allEpic != null) {
                for (Epic epic : allEpic) {
                    String epicAsString = epic.toString(); // метод toString
                    bw.write(epicAsString + "\n");
                }
            }
            bw.flush();

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл" + e.getMessage());
        }
    }

    // Метод создания задачи из строки
    public static Task fromString(String value) {
        String[] contents = value.split(",");

        switch (TypeTasks.valueOf(contents[1])) {
            case TASK:
                return new Task(Integer.parseInt(contents[0]), contents[2], contents[4], Status.valueOf(contents[3]));

            case SUBTASK:
                return new Subtask(Integer.parseInt(contents[0]), contents[2], contents[4],
                        Status.valueOf(contents[3]), Integer.parseInt(contents[5]));

            case EPIC:
                return new Epic(Integer.parseInt(contents[0]), contents[2], contents[4]);
        }
        return null;
    }
    // Метод, который восстанавливает данные менеджера из файла при запуске программы
    public static FileBackedTaskManager loadFromFile(String path) {
        TaskManager taskManager = new FileBackedTaskManager(path);

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            //Чтение заголовка
            br.readLine();

            List<Subtask> listOfSubtasks = new ArrayList<>();
            while (br.ready()) {
                String line = br.readLine();

                Task task = fromString(line);
                switch (task.getTypeTasks()) {
                    case TASK:
                        taskManager.addNewTask(task);
                        break;
                    case SUBTASK:
                        listOfSubtasks.add((Subtask) task);

                        break;
                    case EPIC:
                        taskManager.addNewEpic((Epic) task);
                        break;
                }
            }

            for (Subtask subtask : listOfSubtasks) {
                taskManager.addNewSubtask(subtask);
            }

        } catch (IOException e) {
            throw new RuntimeException();
        }
        return (FileBackedTaskManager) taskManager;
    }

    @Override
    public Task addNewTask(Task newTask) {
        Task task = super.addNewTask(newTask);
        save();
        return task;
    }

    @Override
    public Subtask addNewSubtask(Subtask newSubtask) {
        Subtask subtask = super.addNewSubtask(newSubtask);
        save();
        return subtask;
    }

    @Override
    public Epic addNewEpic(Epic newEpic) {
        Epic epic = super.addNewEpic(newEpic);
        save();
        return epic;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        Task task = super.updateTask(updatedTask);
        save();
        return task;
    }

    @Override
    public Subtask updateSubtask(Subtask updatedSubtask) {
        Subtask subtask = super.updateSubtask(updatedSubtask);
        save();
        return subtask;
    }

    @Override
    public Epic updateEpic(Epic updatedEpic) {
        Epic epic = super.updateEpic(updatedEpic);
        save();
        return epic;
    }

    @Override
    public Task deleteTask(Integer id) {
        Task deletedTask = super.deleteTask(id);
        save();
        return deletedTask;
    }

    @Override
    public Subtask deleteSubtask(Integer id) {
        Subtask deletedSubtask = super.deleteSubtask(id);
        save();
        return deletedSubtask;
    }

    @Override
    public Epic deleteEpic(Integer id) {
        Epic deletedEpic = super.deleteEpic(id);
        save();
        return deletedEpic;
    }

    @Override
    public void deleteAllOfTask() {
        super.deleteAllOfTask();
        save();
    }

    @Override
    public void deleteAllOfSubtask() {
        super.deleteAllOfSubtask();
        save();
    }

    @Override
    public void deleteAllOfEpic() {
        super.deleteAllOfEpic();
        save();
    }

    //Дополнительное задание по техническому заданию к спринту №7.
    public static void main(String[] args) {
        String fileName = "data.csv";
        TaskManager fileTaskManager = new FileBackedTaskManager(fileName);
        Task task1 = new Task(12, "Задача1",
                "Описание Задачи1", Status.NEW);
        Task task2 = new Task(13, "Задача2",
                "Описание Задачи2",
                Status.NEW);
        fileTaskManager.addNewTask(task1);
        fileTaskManager.addNewTask(task2);

        Epic epic1 = new Epic(14, "Эпик1", "Описание Эпика1");
        fileTaskManager.addNewEpic(epic1);
        Subtask subtask1InEpic1 = new Subtask(15, "Подзадача1",
                "Описание Подзадачи1", Status.NEW, epic1.getId());
        Subtask subtask2InEpic1 = new Subtask(16, "Подзадача2",
                "Описание Подзадачи2", Status.NEW, epic1.getId());
        Subtask subtask3InEpic1 = new Subtask(17, "Подзадача3",
                "Описание Подзадачи3", Status.NEW, epic1.getId());
        fileTaskManager.addNewSubtask(subtask1InEpic1);
        fileTaskManager.addNewSubtask(subtask2InEpic1);
        fileTaskManager.addNewSubtask(subtask3InEpic1);

        Epic epic2 = new Epic(18, "Эпик2", "Описание Эпика2");
        fileTaskManager.addNewEpic(epic2);

        System.out.println("-".repeat(100));
        System.out.println();
        System.out.println("Созданы и добавлены задачи, эпики, подзадачи для ТЗ №7.");
        System.out.println();

        System.out.println("Все задачи: " + fileTaskManager.getAllOfTask());
        System.out.println("Все подзадачи: " + fileTaskManager.getAllOfSubtask());
        System.out.println("Все эпики: " + fileTaskManager.getAllOfEpic());

        System.out.println("-".repeat(100));
        System.out.println();

        TaskManager fileTaskManager1 = loadFromFile(fileName);

        System.out.println("Все задачи: " + fileTaskManager1.getAllOfTask());
        System.out.println("Все подзадачи: " + fileTaskManager1.getAllOfSubtask());
        System.out.println("Все эпики: " + fileTaskManager1.getAllOfEpic());
    }
}