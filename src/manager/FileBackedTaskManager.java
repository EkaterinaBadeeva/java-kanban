package manager;

import model.*;

import java.io.*;

import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
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

            bw.write("ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC\n");

            if (allTasks != null) {
                for (Task task : allTasks) {
                    String taskAsString = task.toString();
                    bw.write(taskAsString + "\n");
                }
            }

            if (allSubtasks != null) {
                for (Subtask subtask : allSubtasks) {
                    String subtaskAsString = subtask.toString();
                    bw.write(subtaskAsString + "\n");
                }
            }

            if (allEpic != null) {
                for (Epic epic : allEpic) {
                    String epicAsString = epic.toString();
                    bw.write(epicAsString + "\n");
                }
            }
            bw.flush();

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл" + e.getMessage());
        }
    }

    // Метод создания задачи из строки
    private static Task fromString(String value) {
        try {
            String[] contents = value.split(",");

            // id - contents[0], type - contents[1], name - contents[2], status - contents[3],
            // description - contents[4], idEpic - contents[5]
            Integer id = Integer.parseInt(contents[0]);
            TypeTasks type = TypeTasks.valueOf(contents[1]);
            String name = contents[2];
            Status status = Status.valueOf(contents[3]);
            String description = contents[4];

            switch (type) {
                case TASK:
                    return new Task(id, name, description, status);

                case SUBTASK:
                    return new Subtask(id, name, description, status, Integer.parseInt(contents[5]));

                case EPIC:
                    return new Epic(id, name, description, status);
            }

        } catch (RuntimeException e) {
            throw new ManagerSaveException("Ошибка при создании задачи из строки" + e.getMessage());
        }
        return null;
    }

    // Метод, который восстанавливает данные менеджера из файла при запуске программы
    public static FileBackedTaskManager loadFromFile(String path) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(path);

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            //Чтение заголовка
            br.readLine();

            int maxId = 0;

            while (br.ready()) {
                String line = br.readLine();

                Task task = fromString(line);
                int taskId = task.getId();

                switch (task.getTypeTasks()) {
                    case TASK:
                        taskManager.idTask.put(taskId,task);
                        break;
                    case SUBTASK:
                        taskManager.idSubtask.put(taskId, (Subtask) task);
                        break;
                    case EPIC:
                        taskManager.idEpic.put(taskId, (Epic) task);
                        break;
                }

                if (maxId < taskId) {
                    maxId = taskId;
                }
            }

            taskManager.id = maxId;

            for (Subtask subtask : taskManager.idSubtask.values()) {
                Epic epic = taskManager.idEpic.get(subtask.getEpicId());

                if (epic != null) {
                    epic.addSubtaskId(subtask.getId());
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при восстановлении данных менеджера из файла" + e.getMessage());
        }
        return taskManager;
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

        Subtask subtask1UpdatedInEpic1 = new Subtask(subtask1InEpic1.getId(), "Подзадача1",
                "Описание Подзадачи1", Status.IN_PROGRESS, epic1.getId());
        fileTaskManager.updateSubtask(subtask1UpdatedInEpic1);

        System.out.println("-".repeat(100));
        System.out.println();
        System.out.println("Созданы и добавлены задачи, эпики, подзадачи для ТЗ №7.");
        System.out.println();

        System.out.println("Все задачи: " + fileTaskManager.getAllOfTask());
        System.out.println("Все подзадачи: " + fileTaskManager.getAllOfSubtask());
        System.out.println("Все эпики: " + fileTaskManager.getAllOfEpic());

        System.out.println("-".repeat(100));
        System.out.println();

        TaskManager fileTaskManagerFromFile = loadFromFile(fileName);

        System.out.println("Все задачи: " + fileTaskManagerFromFile.getAllOfTask());
        System.out.println("Все подзадачи: " + fileTaskManagerFromFile.getAllOfSubtask());
        System.out.println("Все эпики: " + fileTaskManagerFromFile.getAllOfEpic());
        System.out.println("-".repeat(100));

        Task task3 = new Task(12, "Задача3",
                "Описание Задачи3", Status.NEW);
        fileTaskManagerFromFile.addNewTask(task3);
        System.out.println("Все задачи: " + fileTaskManagerFromFile.getAllOfTask());
    }
}