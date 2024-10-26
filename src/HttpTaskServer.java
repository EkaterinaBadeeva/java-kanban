import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import handlers.*;
import manager.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;

    private static HttpServer httpServer;
    private Gson gson;
    private TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.gson = Managers.getGson();
        this.taskManager = taskManager;
        // создаём и привязываем HTTP-сервер к порту
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        // связываем конкретный путь и его обработчик
        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public static void main(String[] args) throws IOException {

        HttpTaskServer httpServer = new HttpTaskServer();

        // запускаем сервер
        httpServer.start();
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
