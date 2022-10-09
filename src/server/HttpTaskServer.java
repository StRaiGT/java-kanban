package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.List;

public class HttpTaskServer {
    private TaskManager taskManager;
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static Gson gson;
    public static HttpServer server;

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefault();
        gson = Managers.getGson();
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/task", this::TasksHandler);
        server.createContext("/tasks/epic", this::EpicsHandler);
        server.createContext("/tasks/subtask", this::SubtasksHandler);
        server.createContext("/tasks/", this::HistoryHandler);
    }

    public void start() {
        System.out.println("Сервер запущен на порту " + PORT);
        System.out.println("http://localhost:" + PORT + "/tasks/task");
        System.out.println("http://localhost:" + PORT + "/tasks/epic");
        System.out.println("http://localhost:" + PORT + "/tasks/subtask");
        System.out.println("http://localhost:" + PORT + "/tasks");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }


    public void TasksHandler(HttpExchange httpExchange) {
        try {
            final String query = httpExchange.getRequestURI().getQuery();
            int id;
            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    if (query == null) {
                        final List<Task> tasks = taskManager.getTasks();
                        final String response = gson.toJson(tasks);
                        System.out.println("Задачи получены");
                        sendText(httpExchange, response);
                        break;
                    }

                    id = Integer.parseInt(query.substring(3));
                    final Task task = taskManager.getTask(id);
                    final String response = gson.toJson(task);
                    System.out.println("Получили задачу с id = " + id);
                    sendText(httpExchange, response);
                    break;
                case "DELETE":
                    if (query == null) {
                        taskManager.removeAllTasks();
                        System.out.println("Удалили все задачи");
                        httpExchange.sendResponseHeaders(200, 0);
                        sendText(httpExchange, null);
                        break;
                    }

                    id = Integer.parseInt(query.substring(3));
                    taskManager.removeTask(id);
                    System.out.println("Удалили задачу " + id);
                    httpExchange.sendResponseHeaders(200, 0);
                    sendText(httpExchange, null);
                    break;
                case "POST":
                    String json = readText(httpExchange);
                    if (json.isEmpty()) {
                        System.out.println("Тело запроса пусто");
                        httpExchange.sendResponseHeaders(400, 0);
                        break;
                    }

                    final Task taskToPost = gson.fromJson(json, Task.class);
                    id = taskToPost.getId();
                    if (id != 0) {
                        taskManager.updateTask(id, taskToPost);
                        System.out.println("Обновили задачу с id = " + id);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        id = taskManager.addTask(taskToPost);
                        System.out.println("Создали новую задачу с id = " + id);
                        final String responseToPost = gson.toJson(taskToPost);
                        sendText(httpExchange, responseToPost);
                    }
                    break;
                default:
                    System.out.println("/task получен " + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException exception) {
            System.out.println("Во время обработки запроса TasksHandler что-то пошло не так");
        }
    }

    public void EpicsHandler(HttpExchange httpExchange) {
        try {
            final String query = httpExchange.getRequestURI().getQuery();
            int id;
            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    if (query == null) {
                        final List<Epic> epics = taskManager.getEpics();
                        final String response = gson.toJson(epics);
                        System.out.println("Эпики получены");
                        sendText(httpExchange, response);
                        break;
                    }

                    final String idIndex = query.substring(3);
                    id = Integer.parseInt(idIndex);
                    Epic epic = taskManager.getEpic(id);
                    String response = gson.toJson(epic);
                    System.out.println("Получили эпик с id = " + id);
                    sendText(httpExchange, response);
                    break;
                case "DELETE":
                    if (query == null) {
                        taskManager.removeAllEpics();
                        System.out.println("Удалили все задачи");
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    }

                    id = Integer.parseInt(query.substring(3));
                    taskManager.removeEpic(id);
                    System.out.println("Удалили задачу " + id);
                    httpExchange.sendResponseHeaders(200, 0);
                    sendText(httpExchange, null);
                    break;
                case "POST":
                    String json = readText(httpExchange);
                    if (json.isEmpty()) {
                        System.out.println("Тело запроса пусто");
                        httpExchange.sendResponseHeaders(400, 0);
                        break;
                    }

                    final Epic epicToPost = gson.fromJson(json, Epic.class);
                    id = epicToPost.getId();

                    if (id != 0) {
                        taskManager.updateEpic(id, epicToPost);
                        System.out.println("Обновили эпик с id = " + id);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        id = taskManager.addEpic(epicToPost);
                        System.out.println("Создали новый эпик с id = " + id);
                        final String responseToPost = gson.toJson(epicToPost);
                        sendText(httpExchange, responseToPost);
                    }
                    break;
                default:
                    System.out.println("/epic получен " + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException exception) {
            System.out.println("Во время обработки запроса EpicsHandler что-то пошло не так");
        }
    }

    public void SubtasksHandler(HttpExchange httpExchange) {
        try {
            final String query = httpExchange.getRequestURI().getQuery();
            int id;
            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    if (query == null) {
                        final List<Subtask> subtasks = taskManager.getSubtasks();
                        final String response = gson.toJson(subtasks);
                        System.out.println("Подзадачи получены");
                        sendText(httpExchange, response);
                        break;
                    }

                    final String idIndex = query.substring(3);
                    id = Integer.parseInt(idIndex);
                    final Subtask subtask = taskManager.getSubtask(id);
                    final String response = gson.toJson(subtask);
                    System.out.println("Получили подзадачу с id = " + id);
                    sendText(httpExchange, response);
                    break;
                case "DELETE":
                    if (query == null) {
                        taskManager.removeAllSubtasks();
                        System.out.println("Удалили все задачи");
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    }

                    id = Integer.parseInt(query.substring(3));
                    taskManager.removeSubtask(id);
                    System.out.println("Удалили задачу " + id);
                    httpExchange.sendResponseHeaders(200, 0);
                    sendText(httpExchange, null);
                    break;
                case "POST":
                    String json = readText(httpExchange);
                    if (json.isEmpty()) {
                        System.out.println("Тело запроса пусто");
                        httpExchange.sendResponseHeaders(400, 0);
                        break;
                    }

                    final Subtask subtaskToPost = gson.fromJson(json, Subtask.class);
                    id = subtaskToPost.getId();

                    if (id != 0) {
                        taskManager.updateSubtask(id, subtaskToPost);
                        System.out.println("Обновили подзадачу с id = " + id);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        id = taskManager.addSubtask(subtaskToPost);
                        System.out.println("Создали новую подзадачу с id = " + id);
                        final String responseToPost = gson.toJson(subtaskToPost);
                        sendText(httpExchange, responseToPost);
                    }
                    break;
                default:
                    System.out.println("/subtask получен " + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException exception) {
            System.out.println("Во время обработки запроса SubtasksHandler что-то пошло не так ");
        }
    }

    public void HistoryHandler(HttpExchange httpExchange) {
        try {
            String method = httpExchange.getRequestMethod();
            URI responseURI = httpExchange.getRequestURI();
            String path = responseURI.getPath();

            if (path.endsWith("/history") && method.equals("GET")) {
                String historyJson = gson.toJson(taskManager.getHistory());
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(historyJson.getBytes());
                }
            } else if (path.endsWith("/tasks/") && method.equals("GET")) {
                String prTasksJson = gson.toJson(taskManager.getPrioritizedTasks());
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(prTasksJson.getBytes(StandardCharsets.UTF_8));
                }
                httpExchange.close();
            } else {
                httpExchange.sendResponseHeaders(404, 0);
                httpExchange.close();
            }
        } catch (IOException exception) {
            System.out.println("Во время обработки запроса HistoryHandler что-то пошло не так");
        }
    }

    private String readText(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        return body;
    }

    private void sendText(HttpExchange httpExchange, String response) throws IOException {
        String responseJson = gson.toJson(response);
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(responseJson.getBytes(DEFAULT_CHARSET));
        }
    }
}
