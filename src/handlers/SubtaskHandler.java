package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {

                case "GET": {
                    if (Pattern.matches("/subtasks$", path)) {

                        List<Subtask> subtasks = taskManager.getAllOfSubtask();

                        if (subtasks != null) {
                            String response = gson.toJson(subtasks);
                            sendText(exchange, response);

                        } else {
                            sendText(exchange, "Список подзадач пустой\n");
                        }
                        break;
                    }

                    if (Pattern.matches("/subtasks/\\d+$", path)) {
                        String pathID = path.replaceFirst("/subtasks/", "");
                        int id = parsePathId(pathID);
                        Subtask subtask = taskManager.getByIdSubtask(id).orElse(null);

                        if (id != -1 && subtask != null) {
                            String response = gson.toJson(subtask);
                            sendText(exchange, response);

                        } else {
                            System.out.println("Нет подзадачи с id = " + pathID);
                            exchange.sendResponseHeaders(404, 0);
                            break;
                        }
                    }
                    break;
                }

                case "POST": {
                    if (Pattern.matches("/subtasks$", path)) {
                        String textOfBody = readText(exchange);
                        Subtask subtask = gson.fromJson(textOfBody, Subtask.class);
                        Subtask newSubtask = taskManager.addNewSubtask(subtask);

                        if (newSubtask != null) {
                            String response = gson.toJson(newSubtask);
                            sendText(exchange, response);
                        } else {
                            exchange.sendResponseHeaders(406, 0);
                        }
                        break;
                    }

                    if (Pattern.matches("/subtasks/\\d+$", path)) {
                        String pathID = path.replaceFirst("/subtasks/", "");
                        int id = parsePathId(pathID);
                        String textOfBody = readText(exchange);
                        Subtask subtask = gson.fromJson(textOfBody, Subtask.class);
                        Subtask updatedSubtask = taskManager.updateSubtask(subtask);

                        if (id != -1 && updatedSubtask != null) {
                            String response = gson.toJson(updatedSubtask);
                            sendText(exchange, response);
                        } else if (id == -1) {
                            System.out.println("Получен не корректный id = " + pathID);
                            exchange.sendResponseHeaders(404, 0);
                        } else if (updatedSubtask == null) {
                            System.out.println("Подзадача пересекается с существующими задачами");
                            exchange.sendResponseHeaders(406, 0);
                        }
                        break;
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }

                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("/subtasks/\\d+$", path)) {
                        String pathID = path.replaceFirst("/subtasks/", "");
                        int id = parsePathId(pathID);
                        if (id != -1) {
                            taskManager.deleteSubtask(id);
                            System.out.println("Подзадача с id " + id + " удалена");
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
