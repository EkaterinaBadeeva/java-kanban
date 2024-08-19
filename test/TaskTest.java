import manager.InMemoryTaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private static InMemoryTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void shouldAddNewTaskAndGetByIdTask() {
        //prepare
        Task task = new Task(0, "Test task", "Test task description", Status.NEW);

        //do
        final int taskId = taskManager.addNewTask(task).getId();
        final Task savedTask = taskManager.getByIdTask(taskId);

        //check
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void shouldAddTaskInMemoryTaskManagerTakingIntoAccountAllFields() {
        //prepare
        Task task = new Task(0, "Test task", "Test task description", Status.NEW);

        //do
        Task createdTask = taskManager.addNewTask(task);
        final int taskId = createdTask.getId();
        final Task savedTask = taskManager.getByIdTask(taskId);
        String nameCreatedTask = createdTask.getName();
        String descriptionCreatedTask = createdTask.getDescription();
        Status statusCreatedTask = createdTask.getStatus();
        String nameSavedTask = savedTask.getName();
        String descriptionSavedTask = savedTask.getDescription();
        Status statusSavedTask = savedTask.getStatus();

        //check
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(createdTask, savedTask, "Задачи не совпадают по id.");
        assertEquals(nameCreatedTask, nameSavedTask, "Задачи не совпадают по имени.");
        assertEquals(descriptionCreatedTask, descriptionSavedTask, "Задачи не совпадают по описанию.");
        assertEquals(statusCreatedTask, statusSavedTask, "Задачи не совпадают по статусу.");
    }

    @Test
    public void shouldGetAllOfTask() {
        //prepare
        Task task = new Task(0, "Test task", "Test task description", Status.NEW);
        taskManager.addNewTask(task);

        //do
        final ArrayList<Task> tasks = taskManager.getAllOfTask();

        //check
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void shouldUpdateTask() {
        //prepare
        Task task = new Task(0, "Test addNewTask", "Test addNewTask description", Status.NEW);
        final int taskId = taskManager.addNewTask(task).getId();
        Task task2 = new Task(taskId, "Test updateTask", "Test updateTask description", Status.DONE);

        //do
        taskManager.updateTask(task2);

        //check
        assertEquals(task, task2, "Задачa не обновилась.");
    }

    @Test
    public void shouldDeleteTask() {
        //prepare
        Task task = new Task(0, "Test task", "Test task description", Status.NEW);
        final int taskId = taskManager.addNewTask(task).getId();

        //do
        taskManager.deleteTask(taskId);

        //check
        assertNull(taskManager.getByIdTask(taskId), "Задачa не удалена.");
    }

    @Test
    public void shouldDeleteAllOfTask() {
        //prepare
        Task task = new Task(0, "Test task", "Test task description", Status.NEW);
        Task task2 = new Task(0, "Test task2", "Test task2 description", Status.NEW);
        taskManager.addNewTask(task);
        taskManager.addNewTask(task2);

        //do
        taskManager.deleteAllOfTask();
        final ArrayList<Task> tasks = taskManager.getAllOfTask();

        //check
        assertNull(tasks, "Задачи не удалились.");
    }

    @Test
    public void shouldAddTaskInHistory() {
        //prepare
        Task task = new Task(0, "Test task", "Test task description", Status.NEW);
        taskManager.addNewTask(task);
        Task taskTest = taskManager.getByIdTask(task.getId());

        //do
        final List<Task> history = taskManager.getHistory();

        //check
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size());
    }
}