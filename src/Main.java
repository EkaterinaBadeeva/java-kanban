import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task taskOne = new Task(12, "Литература",
                "Прочитать книгу Война и Мир", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(30));
        Task taskTwo = new Task(13, "Литература",
                "Прочитать Книгу Гарри Поттер и Дары Смерти", Status.NEW,
                Instant.now().plus(100, ChronoUnit.MINUTES), Duration.ofMinutes(30));

        Task createdTaskOne = taskManager.addNewTask(taskOne);
        Task createdTaskTwo = taskManager.addNewTask(taskTwo);

        Epic epicOne = new Epic(14, "Уборка", "Уборка в доме");
        Epic createdEpicOne = taskManager.addNewEpic(epicOne);
        Subtask subtaskOneInEpicOne = new Subtask(15, "Пропылесосить",
                "Пропылесосить во всех комнатах, и не забыть под диваном",
                Status.NEW, Instant.now().plus(1000, ChronoUnit.MINUTES),
                Duration.ofMinutes(30), epicOne.getId());

        Epic epicTwo = new Epic(16, "Путешествие", "Собрать все необходимие в путешествие");
        Epic createdEpicTwo = taskManager.addNewEpic(epicTwo);
        Subtask subtaskOneInEpicTwo = new Subtask(17, "Найти чемодан", "поискать чемодан, " +
                "отчаяться и купить новый чемодан", Status.NEW, Instant.now().plus(10000, ChronoUnit.MINUTES),
                Duration.ofMinutes(30), epicTwo.getId());
        Subtask subtaskTwoInEpicTwo = new Subtask(18, "Собрать чемодан",
                "Не забыть положить вещи, которые ни разу не надену",
                Status.NEW, Instant.now().plus(100000, ChronoUnit.MINUTES),
                Duration.ofMinutes(30), epicTwo.getId());

        Subtask createdSubtaskOneInEpicOne = taskManager.addNewSubtask(subtaskOneInEpicOne);
        Subtask createdSubtaskOneInEpicTwo = taskManager.addNewSubtask(subtaskOneInEpicTwo);
        Subtask createdSubtaskTwoInEpicTwo = taskManager.addNewSubtask(subtaskTwoInEpicTwo);

        System.out.println();
        System.out.println("Созданы и добавлены задачи, эпики, подзадачи.");
        System.out.println();

        System.out.println("Все задачи: " + taskManager.getAllOfTask());
        System.out.println("Все подзадачи: " + taskManager.getAllOfSubtask());
        System.out.println("Все эпики: " + taskManager.getAllOfEpic());

        System.out.println("-".repeat(100));
        System.out.println();

        taskOne = new Task(taskOne.getId(), "Литература",
                "Прочитать книгу Война и Мир", Status.DONE,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(30));
        taskTwo = new Task(taskTwo.getId(), "Литература",
                "Прочитать Книгу Гарри Поттер и Дары Смерти", Status.NEW,
                Instant.now().plus(100, ChronoUnit.MINUTES), Duration.ofMinutes(30));
        Task updatedTaskOne = taskManager.updateTask(taskOne);
        Task updatedTaskTwo = taskManager.updateTask(taskTwo);

        epicOne = new Epic(epicOne.getId(), "Уборка", "Уборка в доме");
        subtaskOneInEpicOne = new Subtask(subtaskOneInEpicOne.getId(), "Пропылесосить",
                "Пропылесосить во всех комнатах, " +
                        "и не забыть под диваном", Status.IN_PROGRESS, Instant.now().plus(1000, ChronoUnit.MINUTES),
                Duration.ofMinutes(30), epicOne.getId());

        epicTwo = new Epic(epicTwo.getId(), "Путешествие", "Собрать все необходимие в путешествие");
        subtaskOneInEpicTwo = new Subtask(subtaskOneInEpicTwo.getId(), "Найти чемодан",
                "Поискать чемодан, отчаяться и купить новый чемодан", Status.DONE,
                Instant.now().plus(10000, ChronoUnit.MINUTES), Duration.ofMinutes(30), epicTwo.getId());
        subtaskTwoInEpicTwo = new Subtask(subtaskTwoInEpicTwo.getId(), "Собрать чемодан",
                "Не забыть положить вещи, которые ни разу не надену", Status.DONE,
                Instant.now().plus(100000, ChronoUnit.MINUTES), Duration.ofMinutes(30), epicTwo.getId());

        Subtask updatedSubtaskOneInEpicOne = taskManager.updateSubtask(subtaskOneInEpicOne);
        Subtask updatedSubtaskOneInEpicTwo = taskManager.updateSubtask(subtaskOneInEpicTwo);
        Subtask updatedSubtaskTwoInEpicTwo = taskManager.updateSubtask(subtaskTwoInEpicTwo);

        System.out.println("Статус у задач, подзадач и эпиков обновлен.");
        System.out.println();

        System.out.println("Все задачи: " + taskManager.getAllOfTask());
        System.out.println("Все подзадачи: " + taskManager.getAllOfSubtask());
        System.out.println("Все эпики: " + taskManager.getAllOfEpic());

        System.out.println("-".repeat(100));
        System.out.println();

        System.out.println("История просмотра.");
        System.out.println();

        System.out.println("История:");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("-".repeat(100));
        System.out.println();

        System.out.println("Выводим задачу, подзадачу, эпик по id.");
        System.out.println();

        System.out.println("Задача по id: " + taskManager.getByIdTask(taskTwo.getId()).orElse(null));
        System.out.println("Подзадача по id: " + taskManager.getByIdSubtask(subtaskOneInEpicOne.getId()).orElse(null));
        System.out.println("Эпик по id: " + taskManager.getByIdEpic(epicTwo.getId()).orElse(null));

        System.out.println("-".repeat(100));
        System.out.println();

        System.out.println("Для проверки истории просмотра выводим/просматриваем задачи, подзадачи, эпики по id.");
        System.out.println();

        System.out.println("Задача по id: " + taskManager.getByIdTask(taskTwo.getId()).orElse(null));
        System.out.println("Подзадача по id: " + taskManager.getByIdSubtask(subtaskOneInEpicOne.getId()).orElse(null));
        System.out.println("Эпик по id: " + taskManager.getByIdEpic(epicTwo.getId()).orElse(null));
        System.out.println("Задача по id: " + taskManager.getByIdTask(taskTwo.getId()).orElse(null));
        System.out.println("Подзадача по id: " + taskManager.getByIdSubtask(subtaskOneInEpicOne.getId()).orElse(null));
        System.out.println("Эпик по id: " + taskManager.getByIdEpic(epicTwo.getId()).orElse(null));
        System.out.println("Задача по id: " + taskManager.getByIdTask(taskTwo.getId()).orElse(null));
        System.out.println("Подзадача по id: " + taskManager.getByIdSubtask(subtaskOneInEpicOne.getId()).orElse(null));
        System.out.println("Эпик по id: " + taskManager.getByIdEpic(epicTwo.getId()).orElse(null));
        System.out.println("Задача по id: " + taskManager.getByIdTask(taskTwo.getId()).orElse(null));
        System.out.println("Подзадача по id: " + taskManager.getByIdSubtask(subtaskOneInEpicOne.getId()).orElse(null));
        System.out.println("Эпик по id: " + taskManager.getByIdEpic(epicTwo.getId()).orElse(null));
        System.out.println("Задача по id: " + taskManager.getByIdTask(taskTwo.getId()).orElse(null));
        System.out.println("Подзадача по id: " + taskManager.getByIdSubtask(subtaskOneInEpicOne.getId()).orElse(null));
        System.out.println("Эпик по id: " + taskManager.getByIdEpic(epicTwo.getId()).orElse(null));

        System.out.println("-".repeat(100));
        System.out.println();

        System.out.println("История просмотра.");
        System.out.println();

        System.out.println("История:");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("-".repeat(100));
        System.out.println();

        System.out.println("Удалена одна задача и один эпик.");
        System.out.println();

        taskManager.deleteTask(taskOne.getId());
        System.out.println("Все задачи: " + taskManager.getAllOfTask());
        taskManager.deleteEpic(epicTwo.getId());
        System.out.println("Все подзадачи: " + taskManager.getAllOfSubtask());
        System.out.println("Все эпики: " + taskManager.getAllOfEpic());

        System.out.println("-".repeat(100));
        System.out.println();

        System.out.println("Выводим все подзадачи определенного эпика.");
        System.out.println();

        System.out.println("Подзадачи первого эпика: " + taskManager.getAllSubtaskOfEpic(epicOne.getId()));

        System.out.println("-".repeat(100));
        System.out.println();

        System.out.println("Удаляем все задачи, все подзадачи и все эпики.");
        System.out.println();

        taskManager.deleteAllOfTask();
        taskManager.deleteAllOfSubtask();
        taskManager.deleteAllOfEpic();
        System.out.println("Все задачи: " + taskManager.getAllOfTask());
        System.out.println("Все подзадачи: " + taskManager.getAllOfSubtask());
        System.out.println("Все эпики: " + taskManager.getAllOfEpic());

        //Дополнительное задание по техническому заданию к спринту №6.
        // Реализуем пользовательский сценарий.
        Task task1 = new Task(12, "Задача1",
                "Описание Задачи1", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(30));
        Task task2 = new Task(13, "Задача2",
                "Описание Задачи2",
                Status.NEW,
                Instant.now().plus(100, ChronoUnit.MINUTES),
                Duration.ofMinutes(30));

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        Epic epic1 = new Epic(14, "Эпик1", "Описание Эпика1");
        taskManager.addNewEpic(epic1);
        Subtask subtask1InEpic1 = new Subtask(15, "Подзадача1",
                "Описание Подзадачи1", Status.NEW,
                Instant.now().plus(1000, ChronoUnit.MINUTES),
                Duration.ofMinutes(30), epic1.getId());
        Subtask subtask2InEpic1 = new Subtask(16, "Подзадача2",
                "Описание Подзадачи2", Status.NEW,
                Instant.now().plus(10000, ChronoUnit.MINUTES),
                Duration.ofMinutes(30), epic1.getId());
        Subtask subtask3InEpic1 = new Subtask(17, "Подзадача3",
                "Описание Подзадачи3", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(30), epic1.getId());

        taskManager.addNewSubtask(subtask1InEpic1);
        taskManager.addNewSubtask(subtask2InEpic1);
        taskManager.addNewSubtask(subtask3InEpic1);

        Epic epic2 = new Epic(18, "Эпик2", "Описание Эпика2");
        taskManager.addNewEpic(epic2);

        System.out.println("-".repeat(100));
        System.out.println();
        System.out.println("Созданы и добавлены задачи, эпики, подзадачи для ТЗ №6.");
        System.out.println();

        System.out.println("Все задачи: " + taskManager.getAllOfTask());
        System.out.println("Все подзадачи: " + taskManager.getAllOfSubtask());
        System.out.println("Все эпики: " + taskManager.getAllOfEpic());

        System.out.println("-".repeat(100));
        System.out.println();

        System.out.println("Для проверки истории просмотра выводим/просматриваем задачи, подзадачи, эпики по id.");
        System.out.println();
        System.out.println("Задача1 по id: " + taskManager.getByIdTask(task1.getId()));
        System.out.println("Задача2 по id: " + taskManager.getByIdTask(task2.getId()));
        System.out.println("Подзадача1 по id: " + taskManager.getByIdSubtask(subtask1InEpic1.getId()));
        System.out.println("Подзадача2 по id: " + taskManager.getByIdSubtask(subtask2InEpic1.getId()));
        System.out.println("Подзадача3 по id: " + taskManager.getByIdSubtask(subtask3InEpic1.getId()));
        System.out.println("Эпик1 по id: " + taskManager.getByIdEpic(epic1.getId()));
        System.out.println("Эпик2 по id: " + taskManager.getByIdEpic(epic2.getId()));

        System.out.println("-".repeat(100));
        System.out.println();

        System.out.println("История просмотра:");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println();

        System.out.println("Для проверки истории просмотра повторно выводим/просматриваем задачи по id.");
        System.out.println();
        System.out.println("Задача1 по id: " + taskManager.getByIdTask(task1.getId()));
        System.out.println("Подзадача3 по id: " + taskManager.getByIdSubtask(subtask3InEpic1.getId()));
        System.out.println("Эпик2 по id: " + taskManager.getByIdEpic(epic2.getId()));
        System.out.println("Задача2 по id: " + taskManager.getByIdTask(task2.getId()));

        System.out.println("-".repeat(100));
        System.out.println();

        System.out.println("История просмотра:");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("-".repeat(100));
        System.out.println();
        System.out.println("Удаляем Задачу1");

        taskManager.deleteTask(task1.getId());

        System.out.println("-".repeat(100));
        System.out.println();

        System.out.println("История просмотра:");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("-".repeat(100));
        System.out.println();
        System.out.println("Удаляем Эпик1");

        taskManager.deleteEpic(epic1.getId());

        System.out.println("-".repeat(100));
        System.out.println();

        System.out.println("История просмотра:");

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}