import manager.FileBackedTaskManager;

import manager.TaskManager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static manager.FileBackedTaskManager.loadFromFile;
import static org.junit.jupiter.api.Assertions.*;


class FileBackedTaskManagerTest {
    private static TaskManager taskManager;

    @Test
    public void shouldLoadFromFile() throws IOException {
        //prepare
        File testFile = File.createTempFile("test",".csv");
        taskManager = new FileBackedTaskManager(testFile.getPath());
        Task task = new Task(0, "Test task", "Test task description", Status.NEW);
        taskManager.addNewTask(task);
        Task taskFromTaskManager = taskManager.getByIdTask(task.getId());

        //do
        TaskManager testTaskManager = loadFromFile(String.valueOf(testFile));
        Task taskFromTestTaskManager = testTaskManager.getByIdTask(task.getId());

        //check
        assertEquals(taskFromTaskManager, taskFromTestTaskManager);
    }

    @Test
    public void shouldWorkFromEmptyFile() throws IOException {
        //prepare
        File testFile = File.createTempFile("test", ".csv");
        taskManager = new FileBackedTaskManager(testFile.getPath());

        //do
        BufferedReader br = new BufferedReader(new FileReader(testFile));
        int i = 0;
        while (br.ready()) {
            i++;
            return;
        }

        //check
        assertTrue(testFile.canRead() && testFile.exists());
        assertEquals(i, 0);
    }

    @Test
    public void shouldLoadFromEmptyFile() throws IOException {
        //prepare
        File testFile = File.createTempFile("test",".csv");

        //do
        TaskManager testTaskManager = loadFromFile(String.valueOf(testFile));
        BufferedReader br = new BufferedReader(new FileReader(testFile));
        int i = 0;
        while (br.ready()) {
            i++;
            return;
        }

        //check
        assertTrue(testFile.canRead() && testFile.exists());
        assertEquals(i, 0);
    }

    @Test
    public void shouldSaveInFileWhenAddUpdateDeleteAndDeleteAll() throws IOException {
        //prepare
        File testFile = File.createTempFile("test",".csv");
        taskManager = new FileBackedTaskManager(testFile.getPath());
        Task task = new Task(0, "Test task", "Test task description", Status.NEW);
        Task task2 = new Task(0, "Test task2", "Test task2 description", Status.NEW);
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, epic.getId());

        //do
        taskManager.addNewTask(task);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);

        BufferedReader br = new BufferedReader(new FileReader(testFile));
        int i = 0;
        while (br.ready()) {
            i++;
            return;
        }

        //check
        assertTrue(testFile.canRead() && testFile.exists());
        assertEquals(i, 5);

        //do
        Subtask subtask2 = new Subtask(subtask.getId(), "Test subtask2", "Test subtask2 description",
                Status.DONE, epic.getId());
        taskManager.updateSubtask(subtask2);
        int epicID = subtask2.getEpicId();
        Epic updateEpic = taskManager.getByIdEpic(epicID);

        //check
        assertEquals(epic, updateEpic, "Эпик не обновился.");
        assertTrue(testFile.canRead() && testFile.exists());
        assertEquals(i, 5);

        //do
        taskManager.deleteTask(task.getId());

        //check
        assertTrue(testFile.canRead() && testFile.exists());
        assertEquals(i, 4);

        //do
        taskManager.deleteAllOfTask();
        taskManager.deleteAllOfSubtask();
        taskManager.deleteAllOfEpic();

        //check
        assertTrue(testFile.canRead() && testFile.exists());
        assertEquals(i, 1);
    }

}