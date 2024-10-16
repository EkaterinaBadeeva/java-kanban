import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    private static TaskManager taskManager;

    @Test
    public void shouldGetDefault() {
        //do
        taskManager = Managers.getDefault();

        //check
        assertNotNull(taskManager, "Ошибка получения менеджера по умолчанию.");
    }

    @Test
    public void shouldReturnSameTask() {
        //prepare
        taskManager = Managers.getDefault();

        //do
        Task task = new Task(0, "Test task", "Test task description", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        Task taskTest = taskManager.addNewTask(task);
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        Epic epicTest = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(100, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        Subtask subtaskTest = taskManager.addNewSubtask(subtask);

        //check
        assertNotNull(taskTest, "Задача не найдена.");
        assertEquals(task, taskTest, "Задачи не совпадают");
        assertNotNull(subtaskTest, "Подзадача не найдена.");
        assertEquals(subtask, subtaskTest, "Подзадачи не совпадают");
        assertNotNull(epicTest, "Эпик не найден.");
        assertEquals(epic, epicTest, "Эпики не совпадают");
    }
}