import org.junit.jupiter.api.Test;

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
        Task task = new Task(0, "Test task", "Test task description", Status.NEW);
        Task taskTest = taskManager.addNewTask(task);
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        Epic epicTest = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, epic.getId());
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