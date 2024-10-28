package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            String path = exchange.getRequestURI().getPath();
            // получаем HTTP-метод, который клиент использовал при отправке запроса
            String method = exchange.getRequestMethod();

            switch (method) {

                case "GET": {
                    if (Pattern.matches("/tasks$", path)) {
                        List<Task> tasks = taskManager.getAllOfTask();

                        if (tasks != null) {
                            String response = gson.toJson(tasks);
                            sendText(exchange, response);

                        } else {
                            sendText(exchange, "Список задач пустой\n");
                        }
                        break;
                    }

                    if (Pattern.matches("/tasks/\\d+$", path)) {
                        String pathID = path.replaceFirst("/tasks/", "");
                        int id = parsePathId(pathID);
                        Task task = taskManager.getByIdTask(id).orElse(null);
                        if (id != -1 && task != null) {
                            String response = gson.toJson(task);
                            sendText(exchange, response);
                        } else {
                            System.out.println("Нет задачи с id = " + pathID);
                            exchange.sendResponseHeaders(404, 0);
                            break;
                        }
                    }
                    break;
                }

                case "POST": {
                    if (Pattern.matches("/tasks$", path)) {
                        String textOfBody = readText(exchange);
                        Task task = gson.fromJson(textOfBody, Task.class);
                        Task newTask = taskManager.addNewTask(task);

                        if (newTask != null) {
                            String response = gson.toJson(newTask);
                            sendText(exchange, "Задача добавлена\n" + response);
                        } else {
                            exchange.sendResponseHeaders(406, 0);
                        }
                        break;
                    }

                    if (Pattern.matches("/tasks/\\d+$", path)) {
                        String pathID = path.replaceFirst("/tasks/", "");
                        int id = parsePathId(pathID);
                        String textOfBody = readText(exchange);
                        Task task = gson.fromJson(textOfBody, Task.class);
                        Task updatedTask = taskManager.updateTask(task);

                        if (id != -1 && updatedTask != null) {
                            String response = gson.toJson(updatedTask);
                            sendText(exchange, response);
                        } else if (id == -1) {
                            System.out.println("Получен не корректный id = " + pathID);
                            exchange.sendResponseHeaders(404, 0);
                        } else if (updatedTask == null) {
                            System.out.println("Задача пересекается с существующими задачами");
                            exchange.sendResponseHeaders(406, 0);
                        }
                        break;
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }

                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("/tasks/\\d+$", path)) {
                        String pathID = path.replaceFirst("/tasks/", "");
                        int id = parsePathId(pathID);
                        if (id != -1) {
                            taskManager.deleteTask(id);
                            System.out.println("Задача с id " + id + " удалена");
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            System.out.println("Получен не корректный id = " + pathID);
                            exchange.sendResponseHeaders(405, 0);
                        }
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                default:
                    System.out.println("Вы использовали какой-то другой метод!" + method);
                    exchange.sendResponseHeaders(405, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }

    }
}
