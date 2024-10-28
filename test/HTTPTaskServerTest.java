import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskServerTest {
    private HttpTaskServer taskServer;
    private final Gson gson = Managers.getGson();
    private TaskManager taskManager;

    @BeforeEach
    void beforeEach() throws IOException {
        taskManager = Managers.getDefault();
        taskManager.deleteAllOfTask();
        taskManager.deleteAllOfSubtask();
        taskManager.deleteAllOfEpic();
        taskServer = new HttpTaskServer(taskManager);

        taskServer.start();
    }

    @AfterEach
    void afterEach() {
        taskServer.stop();
    }

    //Тесты для задач
    @Test
    void shouldGetAllOfTask() throws IOException, InterruptedException {
        //prepare
        Task task = new Task(0, "Test task", "Test task description",
                Status.NEW, Instant.now(), Duration.ofMinutes(14));
        Task task1 = new Task(5, "Test task1", "Test task1 description",
                Status.NEW, Instant.now().plus(10000, ChronoUnit.MINUTES), Duration.ofMinutes(14));

        taskManager.addNewTask(task);
        taskManager.addNewTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        //do
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> taskActual = gson.fromJson(response.body(), taskType);

        //check
        assertNotNull(taskActual, "Задачи не возвращаются");
        assertEquals(2, taskActual.size(), "Некорректное количество задач");
        assertEquals("Test task", taskActual.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        //prepare
        Task task = new Task(1, "Test task", "Test task description",
                Status.NEW, Instant.now(), Duration.ofMinutes(14));

        Task newTask = taskManager.addNewTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + newTask.getId());

        //do
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Task>() {}.getType();
        Task taskActual = gson.fromJson(response.body(), taskType);

        //check
        assertNotNull(taskActual, "Задача не возвращаются");
        assertEquals(newTask.getId(), taskActual.getId());
        assertEquals("Test task", taskActual.getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldAddTask() throws IOException, InterruptedException {
        //prepare
        Task task = new Task(0, "Test task", "Test task description",
                Status.NEW, Instant.now(), Duration.ofMinutes(14));

        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        //do
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllOfTask();

        //check
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test task", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        //prepare
        Task task = new Task(1, "Test task", "Test task description",
                Status.NEW, Instant.now(), Duration.ofMinutes(14));

        Task newTask = taskManager.addNewTask(task);
        int id = newTask.getId();
        Task task1 = new Task(id, "Test task1", "Test task1 description",
                Status.NEW, Instant.now(), Duration.ofMinutes(14));

        String taskJson = gson.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        //do
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllOfTask();

        //check
        assertNotNull(tasksFromManager, "Задачa не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test task1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {
        //prepare
        Task task = new Task(1, "Test task", "Test task description",
                Status.NEW, Instant.now(), Duration.ofMinutes(14));

        Task newTask = taskManager.addNewTask(task);
        int id = newTask.getId();

        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        //do
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllOfTask();

        //check
        assertNull(tasksFromManager, "Задачи не удаляются");
    }

    //Тесты для подзадач
    @Test
    void shouldGetAllOfSubtask() throws IOException, InterruptedException {
        //prepare
        Epic epic = new Epic(1, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(2, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());

        taskManager.addNewSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        //do
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type subtaskType = new TypeToken<ArrayList<Subtask>>() {}.getType();
        List<Subtask> subtaskActual = gson.fromJson(response.body(), subtaskType);

        //check
        assertNotNull(subtaskActual, "Задачи не возвращаются");
        assertEquals(1, subtaskActual.size(), "Некорректное количество задач");
        assertEquals("Test subtask", subtaskActual.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldGetSubtaskById() throws IOException, InterruptedException {
        //prepare
        Epic epic = new Epic(1, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(2, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());

        Subtask newSubtask = taskManager.addNewSubtask(subtask);;

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + newSubtask.getId());

        //do
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type subtaskType = new TypeToken<Subtask>() {}.getType();
        Subtask subtaskActual = gson.fromJson(response.body(), subtaskType);

        //check
        assertNotNull(subtaskActual, "Задача не возвращаются");
        assertEquals(newSubtask.getId(), subtaskActual.getId());
        assertEquals("Test subtask", subtaskActual.getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldAddSubtask() throws IOException, InterruptedException {
        //prepare
        Epic epic = new Epic(1, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(2, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());

        String subtaskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        //do
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getAllOfSubtask();

        //check
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test subtask", subtasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {
        //prepare
        Epic epic = new Epic(1, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(2, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());

        Subtask newSubtask = taskManager.addNewSubtask(subtask);
        int id = newSubtask.getId();

        Subtask subtask1 = new Subtask(id, "Test subtask1", "Test subtask1 description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());

        String subtaskJson = gson.toJson(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        //do
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getAllOfSubtask();

        //check
        assertNotNull(subtasksFromManager, "Подзадачa не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test subtask1", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    void shouldDeleteSubtask() throws IOException, InterruptedException {
        //prepare
        Epic epic = new Epic(1, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(2, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());

        Subtask newSubtask = taskManager.addNewSubtask(subtask);
        int id = newSubtask.getId();

        String subtaskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        //do
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getAllOfSubtask();

        //check
        assertNull(subtasksFromManager, "Подзадачи не удаляются");
    }

    //Тесты для эпика
    @Test
    void shouldGetAllOfEpic() throws IOException, InterruptedException {
        //prepare
        Epic epic = new Epic(1, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        //do
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type epicType = new TypeToken<ArrayList<Epic>>() {}.getType();
        List<Epic> epicActual = gson.fromJson(response.body(), epicType);

        //check
        assertNotNull(epicActual, "Эпики не возвращаются");
        assertEquals(1, epicActual.size(), "Некорректное количество эпиков");
        assertEquals("Test epic", epicActual.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException {
        //prepare
        Epic epic = new Epic(1, "Test epic", "Test epic description");
        Epic newEpic = taskManager.addNewEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + newEpic.getId());

        //do
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type epicType = new TypeToken<Epic>() {}.getType();
        Epic epicActual = gson.fromJson(response.body(), epicType);


        //check
        assertNotNull(epicActual, "Эпик не возвращается");
        assertEquals(newEpic.getId(), epicActual.getId());
        assertEquals("Test epic", epicActual.getName(), "Некорректное имя эпика");
    }

    @Test
    void shouldGetAllSubtaskOfEpic() throws IOException, InterruptedException {
        //prepare
        Epic epic = new Epic(1, "Test epic", "Test epic description");
        Epic newEpic = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(2, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), newEpic.getId());
        taskManager.addNewSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + newEpic.getId() + "/subtasks");

        //do
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type epicType = new TypeToken<ArrayList<Subtask>>() {}.getType();
        List<Subtask> subtasksOfEpicActual = gson.fromJson(response.body(), epicType);

        //check
        assertNotNull(subtasksOfEpicActual, "Подзадачи не возвращается");
        assertEquals(1, subtasksOfEpicActual.size(), "Некорректное количество подзадач");
        assertEquals("Test subtask", subtasksOfEpicActual.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    void shouldAddEpic() throws IOException, InterruptedException {
        //prepare
        Epic epic = new Epic(1, "Test epic", "Test epic description");

        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        //do
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epicFromManager = taskManager.getAllOfEpic();

        //check
        assertNotNull(epicFromManager, "Эпики не возвращаются");
        assertEquals(1, epicFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test epic", epicFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    void shouldUpdateEpic() throws IOException, InterruptedException {
        //prepare
        Epic epic = new Epic(1, "Test epic", "Test epic description");
        Epic newEpic = taskManager.addNewEpic(epic);
        int id = newEpic.getId();

        Epic epic1 = new Epic(id, "Test epic1", "Test epic1 description");

        String epicJson = gson.toJson(epic1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        //do
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epicFromManager = taskManager.getAllOfEpic();

        //check
        assertNotNull(epicFromManager, "Эпики не возвращаются");
        assertEquals(1, epicFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test epic1", epicFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    void shouldDeleteEpic() throws IOException, InterruptedException {
        //prepare
        Epic epic = new Epic(1, "Test epic", "Test epic description");
        Epic newEpic = taskManager.addNewEpic(epic);

        int id = newEpic.getId();

        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        //do
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epicFromManager = taskManager.getAllOfEpic();

        //check
        assertNull(epicFromManager, "Эпики не удаляются");
    }

    // Тест для истории
    @Test
    void shouldGetHistory() throws IOException, InterruptedException {
        //prepare
        Task task = new Task(0, "Test task", "Test task description",
                Status.NEW, Instant.now(), Duration.ofMinutes(14));
        taskManager.addNewTask(task);
        Epic epic = new Epic(1, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(2, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(100, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        taskManager.addNewSubtask(subtask);

        taskManager.getByIdTask(task.getId());
        taskManager.getByIdSubtask(subtask.getId());
        taskManager.getByIdEpic(epic.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");

        //do
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type historyType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> historyActual = gson.fromJson(response.body(), historyType);

        //check
        assertNotNull(historyActual, "История не возвращается");
        assertEquals(3, historyActual.size(), "Некорректное количество задач");
        assertEquals("Test epic", historyActual.get(0).getName(),
                "Некорректное имя задачи, которая просмотрена последней");
    }

    // Тест для списка задач в порядке приоритета
    @Test
    void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        //prepare
        Task task = new Task(0, "Test task", "Test task description",
                Status.NEW, Instant.now(), Duration.ofMinutes(14));
        taskManager.addNewTask(task);
        Epic epic = new Epic(1, "Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask(2, "Test subtask", "Test subtask description",
                Status.NEW, Instant.now().plus(100, ChronoUnit.MINUTES),
                Duration.ofMinutes(10), epic.getId());
        taskManager.addNewSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");

        //do
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type prioritizedType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> prioritizedActual = gson.fromJson(response.body(), prioritizedType);

        //check
        assertNotNull(prioritizedActual, "Список задач в порядке приоритета не возвращаются");
        assertEquals(2, prioritizedActual.size(), "Некорректное количество задач");
        assertEquals("Test task", prioritizedActual.get(0).getName(),
                "Некорректное имя самой приоритетной задачи");
    }
}
