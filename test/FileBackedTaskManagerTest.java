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
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static manager.FileBackedTaskManager.loadFromFile;
import static org.junit.jupiter.api.Assertions.*;


class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Test
    public void shouldLoadFromFile() throws IOException {
        //prepare
        File testFile = File.createTempFile("test", ".csv");
        taskManager = new FileBackedTaskManager(testFile.getPath());
        Task task = new Task(0, "Test task", "Test task description", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        taskManager.addNewTask(task);
        Task taskFromTaskManager = taskManager.getByIdTask(task.getId()).orElse(null);

        //do
        TaskManager testTaskManager = loadFromFile(String.valueOf(testFile));
        Task taskFromTestTaskManager = testTaskManager.getByIdTask(task.getId()).orElse(null);

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
        File testFile = File.createTempFile("test", ".csv");

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
        File testFile = File.createTempFile("test", ".csv");
        taskManager = new FileBackedTaskManager(testFile.getPath());
        Task task = new Task(0, "Test task", "Test task description", Status.NEW,
                Instant.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        Task task2 = new Task(0, "Test task2", "Test task2 description", Status.NEW,
                Instant.now().plus(100, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        Epic epic = new Epic(14, "Test epic", "Test epic description");
        Subtask subtask = new Subtask(0, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(1000, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());

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
                Status.DONE, Instant.now().plus(10000, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
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

    @Override
    protected FileBackedTaskManager createTaskManager() {
        File testFile = null;
        try {
            testFile = File.createTempFile("test", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new FileBackedTaskManager(testFile.getPath());
    }
}