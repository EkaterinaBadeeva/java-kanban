import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;
    private static InMemoryTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    public void shouldAddTaskEpicSubtaskInHistoryAndGet() {
        //prepare
        Task task = new Task(0, "Test task", "Test task description", Status.NEW);
        taskManager.addNewTask(task);
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, epic.getId());
        taskManager.addNewSubtask(subtask);

        //do
        historyManager.addHistory(task);
        historyManager.addHistory(subtask);
        historyManager.addHistory(epic);
        final List<Task> history = historyManager.getHistory();

        //check
        assertNotNull(history, "История пустая.");
        assertEquals(3, history.size());
        assertTrue(history.contains(task));
        assertTrue(history.contains(subtask));
        assertTrue(history.contains(epic));
    }

    @Test
    public void shouldAddObjectInHistoryNoMoreThan10() {
        for (int i = 1; i <= 11; i++) {
            Task task = new Task(i, "Test task", "Test task description", Status.NEW);
            taskManager.addNewTask(task);
            historyManager.addHistory(task);
        }
        final List<Task> history = historyManager.getHistory();

        //check
        assertNotNull(history, "История пустая.");
        assertEquals(10, history.size());
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
        final List<Task> history = historyManager.getHistory();
        String nameFirstVersionTask = history.getLast().getName();
        String nameSecondVersionTask = task2.getName();

        //check
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size());
        assertNotEquals(nameFirstVersionTask, nameSecondVersionTask);
    }
}