package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
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
                    if (Pattern.matches("/epics$", path)) {
                        List<Epic> epics = taskManager.getAllOfEpic();

                        if (epics != null) {
                            String response = gson.toJson(epics);
                            sendText(exchange, response);
                        } else {
                            sendText(exchange, "Список эпиков пустой\n");
                        }
                        break;
                    }

                    if (Pattern.matches("/epics/\\d+$", path)) {
                        String pathID = path.replaceFirst("/epics/", "");
                        int id = parsePathId(pathID);
                        Epic epic = taskManager.getByIdEpic(id).orElse(null);
                        if (id != -1 && epic != null) {
                            String response = gson.toJson(epic);
                            sendText(exchange, response);
                        } else {
                            System.out.println("Нет эпика с id = " + pathID);
                            exchange.sendResponseHeaders(404, 0);
                            break;
                        }
                    }

                    if (Pattern.matches("/epics/\\d+/subtasks$", path)) {
                        String pathID = path.replaceFirst("/epics/", "")
                                .replaceFirst("/subtasks", "");
                        int id = parsePathId(pathID);
                        Epic epic = taskManager.getByIdEpic(id).orElse(null);
                        if (id != -1 && epic != null) {
                            List<Subtask> subtasks = taskManager.getAllSubtaskOfEpic(id);
                            String response = gson.toJson(subtasks);
                            sendText(exchange, response);
                        } else {
                            System.out.println("Нет эпика с id = " + pathID);
                            exchange.sendResponseHeaders(404, 0);
                            break;
                        }
                    }

                    break;
                }

                case "POST": {
                    if (Pattern.matches("/epics$", path)) {
                        String textOfBody = readText(exchange);
                        Epic epic = gson.fromJson(textOfBody, Epic.class);
                        Epic newEpic = taskManager.addNewEpic(epic);

                        if (newEpic != null) {
                            String response = gson.toJson(newEpic);
                            sendText(exchange, "Эпик добавлен\n" + response);
                        } else {
                            exchange.sendResponseHeaders(406, 0);
                        }
                        break;

                    } else if (Pattern.matches("/epics/\\d+$", path)) {
                        String pathID = path.replaceFirst("/epics/", "");
                        int id = parsePathId(pathID);
                        String textOfBody = readText(exchange);
                        Epic epic = gson.fromJson(textOfBody, Epic.class);
                        Epic updatedEpic = taskManager.updateEpic(epic);

                        if (id != -1 && updatedEpic != null) {
                            String response = gson.toJson(updatedEpic);
                            sendText(exchange, response);
                        } else {
                            System.out.println("Получен не корректный id = " + pathID);
                            exchange.sendResponseHeaders(404, 0);
                        }
                        break;
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("/epics/\\d+$", path)) {
                        String pathID = path.replaceFirst("/epics/", "");
                        int id = parsePathId(pathID);
                        if (id != -1) {
                            taskManager.deleteEpic(id);
                            System.out.println("Эпик с id " + id + " удален");
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
                    System.out.println("Вы использовали какой-то другой метод! " + method);
                    exchange.sendResponseHeaders(405, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}
