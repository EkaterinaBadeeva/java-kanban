import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    private static HistoryManager historyManager;
    private static InMemoryTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    public void shouldAddNewEpicAndGetByIdEpic() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");

        //do
        Epic createdEpic = taskManager.addNewEpic(epic);
        final int epicId = createdEpic.getId();
        final Epic savedEpic = taskManager.getByIdEpic(epicId);

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
        final ArrayList<Epic> epics = taskManager.getAllOfEpic();

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
                Status.NEW, epic.getId());
        Subtask createdSubtask = taskManager.addNewSubtask(subtask);
        final int subtaskId = createdSubtask.getId();
        Subtask subtask2 = new Subtask(subtaskId, "Test subtask2", "Test subtask2 description",
                Status.DONE, epic.getId());
        Subtask subtask3 = new Subtask(1, "Test subtask3", "Test subtask3 description",
                Status.NEW, epic.getId());

        //do
        taskManager.updateSubtask(subtask2);
        int epicID = subtask2.getEpicId();
        Epic updateEpic = taskManager.getByIdEpic(epicID);

        //check
        assertEquals(epic, updateEpic, "Эпик не обновился.");

        //do
        final int subtaskId3 = taskManager.addNewTask(subtask3).getId();
        int epicIDForSubtask3 = subtask3.getEpicId();
        Epic updateEpicAfterAdd = taskManager.getByIdEpic(epicIDForSubtask3);

        //check
        assertEquals(epic, updateEpicAfterAdd, "Эпик не обновился.");

        //do
        taskManager.deleteSubtask(subtaskId3);
        Epic updateEpicAfterDelete = taskManager.getByIdEpic(epicIDForSubtask3);

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
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, epic.getId());
        Subtask createdSubtask = taskManager.addNewSubtask(subtask);
        final int subtaskId = createdSubtask.getId();
        Subtask subtask2 = new Subtask(1, "Test subtask2", "Test subtask2 description",
                Status.DONE, epic.getId());
        Subtask createdSubtask2 = taskManager.addNewSubtask(subtask2);
        final int subtaskId2 = createdSubtask2.getId();

        //do
        taskManager.deleteEpic(epicId);

        //check
        assertNull(taskManager.getByIdEpic(epicId), "Эпик не удален.");
        assertNull(taskManager.getByIdSubtask(subtaskId), "Подзадача не удалена.");
        assertNull(taskManager.getByIdSubtask(subtaskId2), "Подзадача2 не удалена.");
        assertFalse(createdEpic.getSubtaskIds().contains(subtaskId), "Id подзадачи не удалилось.");
        assertFalse(createdEpic.getSubtaskIds().contains(subtaskId2), "Id подзадачи2 не удалилось.");
    }

    @Test
    public void shouldDeleteAllOfEpicAndSubtasksInThisEpics() {
        //prepare
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, epic.getId());
        taskManager.addNewSubtask(subtask);
        Subtask subtask2 = new Subtask(1, "Test subtask2", "Test subtask2 description",
                Status.DONE, epic.getId());
        taskManager.addNewSubtask(subtask2);
        Epic epic2 = new Epic(15, "Test epic2", "Test epic2 description");
        taskManager.addNewEpic(epic2);
        Subtask subtask3 = new Subtask(2, "Test subtask3", "Test subtask3 description",
                Status.NEW, epic.getId());
        taskManager.addNewSubtask(subtask3);

        //do
        taskManager.deleteAllOfEpic();
        final ArrayList<Epic> epics = taskManager.getAllOfEpic();
        final ArrayList<Subtask> subtasks = taskManager.getAllOfSubtask();

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
        Epic epicTest = taskManager.getByIdEpic(epic.getId());

        //do
        final List<Task> history = historyManager.getHistory();

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
                Status.NEW, epicId);
        Subtask createdSubtask = taskManager.addNewSubtask(subtask);
        final int subtaskId = createdSubtask.getId();
        Subtask subtask2 = new Subtask(1, "Test subtask2", "Test subtask2 description",
                Status.DONE, epicId);
        Subtask createdSubtask2 = taskManager.addNewSubtask(subtask2);
        final int subtaskId2 = createdSubtask2.getId();

        //check
        assertTrue(createdEpic.getSubtaskIds().contains(subtaskId), "Id подзадачи не добавилось в список.");
        assertTrue(createdEpic.getSubtaskIds().contains(subtaskId2), "Id подзадачи не добавилось в список.");
    }
}



