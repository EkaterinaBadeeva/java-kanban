package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            switch (method) {

                case "GET": {
                    if (Pattern.matches("/prioritized$", path)) {
                        String response = gson.toJson(taskManager.getPrioritizedTasks());
                        sendText(exchange, response);
                        break;
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
