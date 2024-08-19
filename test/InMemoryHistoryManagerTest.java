import manager.InMemoryTaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static InMemoryTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void shouldAddObjectInHistoryNoMoreThan10() {
        for (int i = 1; i <= 11; i++) {
            Task task = new Task(i, "Test task", "Test task description", Status.NEW);
            taskManager.addNewTask(task);
            taskManager.getByIdTask(i);
        }
        final List<Task> history = taskManager.getHistory();

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
        final List<Task> history = taskManager.getHistory();
        String nameFirstVersionTask = history.getLast().getName();
        String nameSecondVersionTask = task2.getName();

        //check
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size());
        assertNotEquals(nameFirstVersionTask, nameSecondVersionTask);
    }
}