import manager.TaskManager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();

    //Тесты для задач
    @Test
    public void shouldAddNewTaskAndGetByIdTask() {
        //prepare
        Task task = new Task(0, "Test task", "Test task description",
                Status.NEW, Instant.now(), Duration.ofMinutes(14));

        //do
        final int taskId = taskManager.addNewTask(task).getId();
        final Task savedTask = taskManager.getByIdTask(taskId).orElse(null);
        ;

        //check
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void shouldAddTaskInMemoryTaskManagerTakingIntoAccountAllFields() {
        //prepare
        Task task = new Task(0, "Test task", "Test task description", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));

        //do
        Task createdTask = taskManager.addNewTask(task);
        final int taskId = createdTask.getId();
        final Task savedTask = taskManager.getByIdTask(taskId).orElse(null);
        String nameCreatedTask = createdTask.getName();
        String descriptionCreatedTask = createdTask.getDescription();
        Status statusCreatedTask = createdTask.getStatus();
        Instant startTimeCreatedTask = createdTask.getStartTime();
        Duration durationCreatedTask = createdTask.getDuration();
        String nameSavedTask = savedTask.getName();
        String descriptionSavedTask = savedTask.getDescription();
        Status statusSavedTask = savedTask.getStatus();
        Instant startTimeSavedTask = savedTask.getStartTime();
        Duration durationSavedTask = savedTask.getDuration();

        //check
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(createdTask, savedTask, "Задачи не совпадают по id.");
        assertEquals(nameCreatedTask, nameSavedTask, "Задачи не совпадают по имени.");
        assertEquals(descriptionCreatedTask, descriptionSavedTask, "Задачи не совпадают по описанию.");
        assertEquals(statusCreatedTask, statusSavedTask, "Задачи не совпадают по статусу.");
        assertEquals(startTimeCreatedTask, startTimeSavedTask, "Задачи не совпадают по времени старта.");
        assertEquals(durationCreatedTask, durationSavedTask, "Задачи не совпадают по длительности.");
    }

    @Test
    public void shouldGetAllOfTask() {
        //prepare
        Task task = new Task(0, "Test task", "Test task description", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        taskManager.addNewTask(task);

        //do
        final ArrayList<Task> tasks = (ArrayList<Task>) taskManager.getAllOfTask();

        //check
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void shouldUpdateTask() {
        //prepare
        Task task = new Task(0, "Test addNewTask", "Test addNewTask description", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        final int taskId = taskManager.addNewTask(task).getId();
        Task task2 = new Task(taskId, "Test updateTask", "Test updateTask description", Status.DONE,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));

        //do
        taskManager.updateTask(task2);

        //check
        assertEquals(task, task2, "Задачa не обновилась.");
    }

    @Test
    public void shouldDeleteTask() {
        //prepare
        Task task = new Task(0, "Test task", "Test task description", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        final int taskId = taskManager.addNewTask(task).getId();

        //do
        taskManager.deleteTask(taskId);

        //check
        assertNull(taskManager.getByIdTask(taskId).orElse(null), "Задачa не удалена.");
    }

    @Test
    public void shouldDeleteAllOfTask() {
        //prepare
        Task task = new Task(0, "Test task", "Test task description", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        Task task2 = new Task(0, "Test task2", "Test task2 description", Status.NEW,
                Instant.now().plus(100, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        taskManager.addNewTask(task);
        taskManager.addNewTask(task2);

        //do
        taskManager.deleteAllOfTask();
        final ArrayList<Task> tasks = (ArrayList<Task>) taskManager.getAllOfTask();

        //check
        assertNull(tasks, "Задачи не удалились.");
    }

    @Test
    public void shouldAddTaskInHistory() {
        //prepare
        Task task = new Task(0, "Test task", "Test task description", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        taskManager.addNewTask(task);
        Task taskTest = taskManager.getByIdTask(task.getId()).orElse(null);

        //do
        final List<Task> history = taskManager.getHistory();

        //check
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size());
    }

    //Тесты для подзадач
    @Test
    public void shouldAddNewSubtaskAndGetByIdSubtask() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());

        //do
        Subtask createdSubtask = taskManager.addNewSubtask(subtask);
        final int subtaskId = createdSubtask.getId();
        final Subtask savedSubtask = taskManager.getByIdSubtask(subtaskId).orElse(null);

        //check
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(createdSubtask, savedSubtask, "Подзадачи не совпадают.");
    }

    @Test
    public void shouldGetAllOfSubtask() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        taskManager.addNewSubtask(subtask);

        //do
        final ArrayList<Subtask> subtasks = (ArrayList<Subtask>) taskManager.getAllOfSubtask();

        //check
        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    public void shouldUpdateSubtask() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        Subtask createdSubtask = taskManager.addNewSubtask(subtask);
        final int subtaskId = createdSubtask.getId();
        Subtask subtask2 = new Subtask(subtaskId, "Test subtask2", "Test subtask2 description",
                Status.DONE, Instant.now().plus(100, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());

        //do
        taskManager.updateSubtask(subtask2);

        //check
        assertEquals(createdSubtask, subtask2, "Подзадачa не обновилась.");
    }

    @Test
    public void shouldDeleteSubtask() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        Subtask createdSubtask = taskManager.addNewSubtask(subtask);
        final int subtaskId = createdSubtask.getId();

        //do
        taskManager.deleteSubtask(subtaskId);

        //check
        assertNull(taskManager.getByIdSubtask(subtaskId).orElse(null), "Подзадачa не удалена.");
    }

    @Test
    public void shouldDeleteAllOfSubtask() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        taskManager.addNewSubtask(subtask);
        Subtask subtask2 = new Subtask(1, "Test subtask2", "Test subtask2 description",
                Status.DONE, Instant.now().plus(100, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        taskManager.addNewSubtask(subtask2);

        //do
        taskManager.deleteAllOfSubtask();
        final ArrayList<Subtask> subtasks = (ArrayList<Subtask>) taskManager.getAllOfSubtask();

        //check
        assertNull(subtasks, "Подзадачи не удалились.");
    }

    @Test
    public void shouldAddSubtaskInHistory() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        taskManager.addNewSubtask(subtask);
        Subtask subtaskTest = taskManager.getByIdSubtask(subtask.getId()).orElse(null);

        //do
        final List<Task> history = taskManager.getHistory();

        //check
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size());
    }

    @Test
    public void shouldGetEpicIdFromSubtask() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        Subtask createdSubtask = taskManager.addNewSubtask(subtask);

        //do
        int epicId = createdSubtask.getEpicId();

        //check
        assertNotNull(epicId, "Id эпика не получен.");
        assertEquals(epicId, epic.getId(), "Id эпика не верный");
    }

    //Тесты для эпиков
    @Test
    public void shouldAddNewEpicAndGetByIdEpic() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");

        //do
        Epic createdEpic = taskManager.addNewEpic(epic);
        final int epicId = createdEpic.getId();
        final Epic savedEpic = taskManager.getByIdEpic(epicId).orElse(null);

        //check
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(createdEpic, savedEpic, "Эпики не совпадают.");
    }

    @Test
    public void shouldGetAllOfEpic() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);

        //do
        final ArrayList<Epic> epics = (ArrayList<Epic>) taskManager.getAllOfEpic();

        //check
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    public void shouldUpdateEpicIfUpdateSubtaskOrAddSubtaskOrDeleteSubtask() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        Subtask createdSubtask = taskManager.addNewSubtask(subtask);
        final int subtaskId = createdSubtask.getId();
        Subtask subtask2 = new Subtask(subtaskId, "Test subtask2", "Test subtask2 description",
                Status.DONE, Instant.now().plus(100, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        Subtask subtask3 = new Subtask(1, "Test subtask3", "Test subtask3 description",
                Status.NEW, Instant.now().plus(1000, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());

        //do
        taskManager.updateSubtask(subtask2);
        int epicID = subtask2.getEpicId();
        Epic updateEpic = taskManager.getByIdEpic(epicID).orElse(null);

        //check
        assertEquals(epic, updateEpic, "Эпик не обновился.");

        //do
        final int subtaskId3 = taskManager.addNewTask(subtask3).getId();
        int epicIDForSubtask3 = subtask3.getEpicId();
        Epic updateEpicAfterAdd = taskManager.getByIdEpic(epicIDForSubtask3).orElse(null);

        //check
        assertEquals(epic, updateEpicAfterAdd, "Эпик не обновился.");

        //do
        taskManager.deleteSubtask(subtaskId3);
        Epic updateEpicAfterDelete = taskManager.getByIdEpic(epicIDForSubtask3).orElse(null);

        //check
        assertEquals(epic, updateEpicAfterDelete, "Эпик не обновился.");
    }

    @Test
    public void shouldUpdateEpic() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        Epic createdEpic = taskManager.addNewEpic(epic);
        final int epicId = createdEpic.getId();
        Epic epic2 = new Epic(epicId, "Test epic2", "Test epic description2");

        //do
        taskManager.updateEpic(epic2);

        //check
        assertEquals(createdEpic, epic2, "Эпик не обновился.");
    }

    @Test
    public void shouldDeleteEpicAndSubtasksInThisEpic() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        Epic createdEpic = taskManager.addNewEpic(epic);
        final int epicId = createdEpic.getId();
        Subtask subtask = new Subtask(1, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        Subtask createdSubtask = taskManager.addNewSubtask(subtask);
        final int subtaskId = createdSubtask.getId();
        Subtask subtask2 = new Subtask(2, "Test subtask2", "Test subtask2 description",
                Status.DONE, Instant.now().plus(100, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        Subtask createdSubtask2 = taskManager.addNewSubtask(subtask2);
        final int subtaskId2 = createdSubtask2.getId();

        //do
        taskManager.deleteEpic(epicId);

        //check
        assertNull(taskManager.getByIdEpic(epicId).orElse(null), "Эпик не удален.");
        assertNull(taskManager.getByIdSubtask(subtaskId).orElse(null), "Подзадача не удалена.");
        assertNull(taskManager.getByIdSubtask(subtaskId2).orElse(null), "Подзадача2 не удалена.");
        assertFalse(createdEpic.getSubtaskIds().contains(subtaskId), "Id подзадачи не удалилось.");
        assertFalse(createdEpic.getSubtaskIds().contains(subtaskId2), "Id подзадачи2 не удалилось.");
    }

    @Test
    public void shouldDeleteAllOfEpicAndSubtasksInThisEpics() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        taskManager.addNewSubtask(subtask);
        Subtask subtask2 = new Subtask(1, "Test subtask2", "Test subtask2 description",
                Status.DONE, Instant.now().plus(100, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        taskManager.addNewSubtask(subtask2);
        Epic epic2 = new Epic(15, "Test epic2", "Test epic2 description");
        taskManager.addNewEpic(epic2);
        Subtask subtask3 = new Subtask(2, "Test subtask3", "Test subtask3 description",
                Status.NEW, Instant.now().plus(1000, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        taskManager.addNewSubtask(subtask3);

        //do
        taskManager.deleteAllOfEpic();
        final ArrayList<Epic> epics = (ArrayList<Epic>) taskManager.getAllOfEpic();
        final ArrayList<Subtask> subtasks = (ArrayList<Subtask>) taskManager.getAllOfSubtask();

        //check
        assertNull(epics, "Эпики не удалились.");
        assertNull(subtasks, "Подзадачи не удалились.");
        assertTrue(epic.getSubtaskIds().isEmpty(), "Id подзадач не удалились.");
    }

    @Test
    public void shouldAddEpicInHistory() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Epic epicTest = taskManager.getByIdEpic(epic.getId()).orElse(null);

        //do
        final List<Task> history = taskManager.getHistory();

        //check
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size());
    }

    @Test
    public void shouldAddSubtaskIdWhenAddSubtaskInEpic() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        Epic createdEpic = taskManager.addNewEpic(epic);
        final int epicId = createdEpic.getId();

        //do
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epicId);
        Subtask createdSubtask = taskManager.addNewSubtask(subtask);
        final int subtaskId = createdSubtask.getId();
        Subtask subtask2 = new Subtask(1, "Test subtask2", "Test subtask2 description",
                Status.DONE, Instant.now().plus(100, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epicId);
        Subtask createdSubtask2 = taskManager.addNewSubtask(subtask2);
        final int subtaskId2 = createdSubtask2.getId();

        //check
        assertTrue(createdEpic.getSubtaskIds().contains(subtaskId), "Id подзадачи не добавилось в список.");
        assertTrue(createdEpic.getSubtaskIds().contains(subtaskId2), "Id подзадачи не добавилось в список.");
    }

    @Test
    public void shouldCalculationStatusEpicIfAllSubtaskStatusNEW() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(20, ChronoUnit.MINUTES), Duration.ofMinutes(10), epic.getId());
        Subtask subtask2 = new Subtask(1, "Test subtask2", "Test subtask2 description",
                Status.NEW, Instant.now().plus(100, ChronoUnit.MINUTES), Duration.ofMinutes(15), epic.getId());
        Subtask subtask3 = new Subtask(2, "Test subtask3", "Test subtask3 description",
                Status.NEW, Instant.now().plus(200, ChronoUnit.MINUTES), Duration.ofMinutes(35), epic.getId());

        //do
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);
        //check
        assertEquals(epic.getStatus(), Status.NEW, "Статус эпика должен быть NEW.");
    }

    @Test
    public void shouldCalculationStatusEpicIfAllSubtaskStatusDONE() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.DONE, Instant.now().plus(20, ChronoUnit.MINUTES), Duration.ofMinutes(10), epic.getId());
        Subtask subtask2 = new Subtask(1, "Test subtask2", "Test subtask2 description",
                Status.DONE, Instant.now().plus(100, ChronoUnit.MINUTES), Duration.ofMinutes(15), epic.getId());
        Subtask subtask3 = new Subtask(2, "Test subtask3", "Test subtask3 description",
                Status.DONE, Instant.now().plus(200, ChronoUnit.MINUTES), Duration.ofMinutes(35), epic.getId());

        //do
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);
        //check
        assertEquals(epic.getStatus(), Status.DONE, "Статус эпика должен быть DONE.");
    }

    @Test
    public void shouldCalculationStatusEpicIfAllSubtaskStatusNEWAndDONE() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(20, ChronoUnit.MINUTES), Duration.ofMinutes(10), epic.getId());
        Subtask subtask2 = new Subtask(1, "Test subtask2", "Test subtask2 description",
                Status.DONE, Instant.now().plus(100, ChronoUnit.MINUTES), Duration.ofMinutes(15), epic.getId());
        Subtask subtask3 = new Subtask(2, "Test subtask3", "Test subtask3 description",
                Status.DONE, Instant.now().plus(200, ChronoUnit.MINUTES), Duration.ofMinutes(35), epic.getId());

        //do
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);
        //check
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статус эпика должен быть IN_PROGRESS.");
    }

    @Test
    public void shouldCalculationStatusEpicIfAllSubtaskStatusIN_PROGRESS() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.IN_PROGRESS, Instant.now().plus(20, ChronoUnit.MINUTES), Duration.ofMinutes(10), epic.getId());
        Subtask subtask2 = new Subtask(1, "Test subtask2", "Test subtask2 description",
                Status.IN_PROGRESS, Instant.now().plus(100, ChronoUnit.MINUTES), Duration.ofMinutes(15), epic.getId());
        Subtask subtask3 = new Subtask(2, "Test subtask3", "Test subtask3 description",
                Status.IN_PROGRESS, Instant.now().plus(200, ChronoUnit.MINUTES), Duration.ofMinutes(35), epic.getId());

        //do
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);
        //check
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статус эпика должен быть IN_PROGRESS.");
    }

    // Для задач и подзадач
    @Test
    public void shouldNotAcrossTasks() throws IOException {
        //prepare
        Instant commonStart = Instant.now().plus(100, ChronoUnit.MINUTES);
        Task task = new Task(0, "Test task", "Test task description", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        Task task2 = new Task(1, "Test task2", "Test task2 description", Status.NEW,
                commonStart, Duration.ofMinutes(10));
        Task task3 = new Task(10, "Test task3", "Test task2 description", Status.NEW,
                commonStart, Duration.ofMinutes(10));

        //do
        taskManager.addNewTask(task);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        //check
        assertNotEquals(task.getStartTime(), task2.getStartTime());
        assertEquals(task2.getStartTime(), task3.getStartTime());
        assertNull(taskManager.getByIdTask(task3.getId()).orElse(null));
    }
}
