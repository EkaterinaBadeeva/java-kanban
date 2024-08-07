public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task taskOne = new Task(12, "Литература",
                "Прочитать книгу Война и Мир", Status.NEW);
        Task taskTwo = new Task(13, "Литература",
                "Прочитать Книгу Гарри Поттер и Дары Смерти",
                Status.NEW);

        Task createdTaskOne = taskManager.addNewTask(taskOne);
        Task createdTaskTwo = taskManager.addNewTask(taskTwo);

        Epic epicOne = new Epic(14, "Уборка", "Уборка в доме");
        Epic createdEpicOne = taskManager.addNewEpic(epicOne);
        Subtask subtaskOneInEpicOne = new Subtask(15, "Пропылесосить",
                "Пропылесосить во всех комнатах, " +
                "и не забыть под диваном", Status.NEW, epicOne.getId());

        Epic epicTwo = new Epic(16, "Путешествие", "Собрать все необходимие в путешествие");
        Epic createdEpicTwo = taskManager.addNewEpic(epicTwo);
        Subtask subtaskOneInEpicTwo = new Subtask(17, "Найти чемодан", "поискать чемодан, " +
                "отчаяться и купить новый чемодан", Status.NEW, epicTwo.getId());
        Subtask subtaskTwoInEpicTwo = new Subtask(18, "Собрать чемодан",
                "Не забыть положить вещи, которые ни разу не надену", Status.NEW, epicTwo.getId());

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
                "Прочитать книгу Война и Мир", Status.DONE);
        taskTwo = new Task(taskTwo.getId(), "Литература",
                "Прочитать Книгу Гарри Поттер и Дары Смерти",
                Status.NEW);
        Task updatedTaskOne = taskManager.updateTask(taskOne);
        Task updatedTaskTwo = taskManager.updateTask(taskTwo);

        epicOne = new Epic(epicOne.getId(), "Уборка", "Уборка в доме");
        subtaskOneInEpicOne = new Subtask(subtaskOneInEpicOne.getId(), "Пропылесосить",
                "Пропылесосить во всех комнатах, " +
                        "и не забыть под диваном", Status.IN_PROGRESS, epicOne.getId());

        epicTwo = new Epic(epicTwo.getId(), "Путешествие", "Собрать все необходимие в путешествие");
        subtaskOneInEpicTwo = new Subtask(subtaskOneInEpicTwo.getId(), "Найти чемодан",
                "Поискать чемодан, отчаяться и купить новый чемодан", Status.DONE, epicTwo.getId());
        subtaskTwoInEpicTwo = new Subtask(subtaskTwoInEpicTwo.getId(), "Собрать чемодан",
                "Не забыть положить вещи, которые ни разу не надену", Status.DONE, epicTwo.getId());

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

        System.out.println("Выводим задачу, подзадачу, эпик по id.");
        System.out.println();

        System.out.println("Задача по id: " + taskManager.getByIdTask(taskTwo.getId()));
        System.out.println("Подзадача по id: " + taskManager.getByIdSubtask(subtaskOneInEpicOne.getId()));
        System.out.println("Эпик по id: " + taskManager.getByIdEpic(epicTwo.getId()));

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
    }
}