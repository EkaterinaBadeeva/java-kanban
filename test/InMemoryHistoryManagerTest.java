import manager.InMemoryTaskManager;
import model.Status;
import model.Task;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Test
    public void shouldAddAllObjectInHistoryAndDeleteRepeats() {
        //prepare
        for (int i = 1; i <= 11; i++) {
            Task task = new Task(i, "Test task", "Test task description", Status.NEW,
                    Instant.now().plus(10 + i * 100, ChronoUnit.MINUTES), Duration.ofMinutes(5));
            taskManager.addNewTask(task);
            taskManager.getByIdTask(i);
        }

        //Проверка на добавление в историю повторов
        for (int i = 1; i <= 4; i++) {
            Task task = new Task(i, "Test task", "Test task description", Status.NEW,
                    Instant.now().plus(10 + i * 10000, ChronoUnit.MINUTES), Duration.ofMinutes(50));
            taskManager.addNewTask(task);
            taskManager.getByIdTask(i);
        }

        //Проверка на удалении задачи из истории просмотров
        Task task1 = new Task(12, "Test task", "Test task description", Status.NEW,
                Instant.now().plus(1000000, ChronoUnit.MINUTES), Duration.ofMinutes(50));
        taskManager.addNewTask(task1);
        taskManager.getByIdTask(task1.getId());

        //do
        taskManager.deleteTask(task1.getId());
        final List<Task> history = taskManager.getHistory();

        //check
        assertNotNull(history, "История пустая.");
        assertEquals(11, history.size());
    }

    @Test
    public void shouldSaveFirstVersionTask() {
        //prepare
        Task task = new Task(0, "Test task Version1", "Test task Version1 description", Status.NEW);
        taskManager.addNewTask(task);
        int taskId = task.getId();
        taskManager.getByIdTask(taskId);

        //do
        Task task2 = new Task(taskId, "Test task Version2", "Test task Version2 description", Status.DONE);
        taskManager.updateTask(task2);
        final List<Task> history = taskManager.getHistory();
        String nameFirstVersionTask = history.getLast().getName();
        String nameSecondVersionTask = task2.getName();

        //check
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size());
        assertNotEquals(nameFirstVersionTask, nameSecondVersionTask);
    }

    @Test
    public void shouldEmptyHistory() {
        //prepare
        Task task = new Task(1, "Test task", "Test task description", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(50));

        //do
        taskManager.addNewTask(task);
        final List<Task> history = taskManager.getHistory();

        //check
        assertTrue(history.isEmpty(), "История не пустая.");
        assertEquals(0, history.size());
    }

    @Test
    public void shouldNotAddRepeats() {
        //prepare
        Task task1 = new Task(1, "Test task1", "Test task1 description", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        Task task2 = new Task(2, "Test task2", "Test task2 description", Status.NEW,
                Instant.now().plus(100, ChronoUnit.MINUTES), Duration.ofMinutes(20));
        Task task3 = new Task(3, "Test task3", "Test task3 description", Status.NEW,
                Instant.now().plus(1000, ChronoUnit.MINUTES), Duration.ofMinutes(30));


        taskManager.addNewTask(task1);
        taskManager.getByIdTask(task1.getId());
        taskManager.addNewTask(task2);
        taskManager.getByIdTask(task2.getId());
        taskManager.addNewTask(task3);
        taskManager.getByIdTask(task3.getId());

        taskManager.getByIdTask(task1.getId());

        //do
        final List<Task> history = taskManager.getHistory();

        //check
        assertNotNull(history, "История пустая.");
        assertEquals(3, history.size());
        assertEquals(task1, history.getFirst());
        assertEquals(task2, history.getLast());

    }

    @Test
    public void shouldDeleteTaskInHistory() {
        //prepare
        Task task1 = new Task(1, "Test task1", "Test task1 description", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        Task task2 = new Task(2, "Test task2", "Test task2 description", Status.NEW,
                Instant.now().plus(100, ChronoUnit.MINUTES), Duration.ofMinutes(20));
        Task task3 = new Task(3, "Test task3", "Test task3 description", Status.NEW,
                Instant.now().plus(1000, ChronoUnit.MINUTES), Duration.ofMinutes(30));
        Task task4 = new Task(4, "Test task4", "Test task4 description", Status.NEW,
                Instant.now().plus(10000, ChronoUnit.MINUTES), Duration.ofMinutes(40));
        Task task5 = new Task(5, "Test task5", "Test task5 description", Status.NEW,
                Instant.now().plus(100000, ChronoUnit.MINUTES), Duration.ofMinutes(50));
        taskManager.addNewTask(task1);
        taskManager.getByIdTask(task1.getId());
        taskManager.addNewTask(task2);
        taskManager.getByIdTask(task2.getId());
        taskManager.addNewTask(task3);
        taskManager.getByIdTask(task3.getId());
        taskManager.addNewTask(task4);
        taskManager.getByIdTask(task4.getId());
        taskManager.addNewTask(task5);
        taskManager.getByIdTask(task5.getId());

        //do
        taskManager.deleteTask(task1.getId());
        taskManager.deleteTask(task3.getId());
        taskManager.deleteTask(task5.getId());
        final List<Task> history = taskManager.getHistory();

        //check
        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size());
        assertFalse(history.contains(task1), "Задача не удалена");
        assertFalse(history.contains(task3), "Задача не удалена");
        assertFalse(history.contains(task5), "Задача не удалена");
    }

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}
