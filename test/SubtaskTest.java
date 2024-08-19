import manager.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private static InMemoryTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void shouldAddNewSubtaskAndGetByIdSubtask() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, epic.getId());

        //do
        Subtask createdSubtask = taskManager.addNewSubtask(subtask);
        final int subtaskId = createdSubtask.getId();
        final Subtask savedSubtask = taskManager.getByIdSubtask(subtaskId);

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
                Status.NEW, epic.getId());
        taskManager.addNewSubtask(subtask);

        //do
        final ArrayList<Subtask> subtasks = taskManager.getAllOfSubtask();

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
                Status.NEW, epic.getId());
        Subtask createdSubtask = taskManager.addNewSubtask(subtask);
        final int subtaskId = createdSubtask.getId();
        Subtask subtask2 = new Subtask(subtaskId, "Test subtask2", "Test subtask2 description",
                Status.DONE, epic.getId());

        //do
        taskManager.updateSubtask(subtask2);

        //check
        assertEquals(createdSubtask, subtask2, "Подзадачa не обновилась.");
    }

    @Test
    public void shouldDeleteSabtask() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, epic.getId());
        Subtask createdSubtask = taskManager.addNewSubtask(subtask);
        final int subtaskId = createdSubtask.getId();

        //do
        taskManager.deleteSubtask(subtaskId);

        //check
        assertNull(taskManager.getByIdSubtask(subtaskId), "Подзадачa не удалена.");
    }

    @Test
    public void shouldDeleteAllOfSubtask() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, epic.getId());
        taskManager.addNewSubtask(subtask);
        Subtask subtask2 = new Subtask(1, "Test subtask2", "Test subtask2 description",
                Status.DONE, epic.getId());
        taskManager.addNewSubtask(subtask2);

        //do
        taskManager.deleteAllOfSubtask();
        final ArrayList<Subtask> subtasks = taskManager.getAllOfSubtask();

        //check
        assertNull(subtasks, "Подзадачи не удалились.");
    }

    @Test
    public void shouldAddSubtaskInHistory() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, epic.getId());
        taskManager.addNewSubtask(subtask);
        Subtask subtaskTest = taskManager.getByIdSubtask(subtask.getId());

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
                Status.NEW, epic.getId());
        Subtask createdSubtask = taskManager.addNewSubtask(subtask);

        //do
        int epicId = createdSubtask.getEpicId();

        //check
        assertNotNull(epicId, "Id эпика не получен.");
        assertEquals(epicId, epic.getId(), "Id эпика не верный");
    }
}