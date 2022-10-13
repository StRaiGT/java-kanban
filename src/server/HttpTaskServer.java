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
    private final TaskManager taskManager;
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private Gson gson;
    public HttpServer server;

    public HttpTaskServer() throws IOException {
        this.taskManager = Managers.getDefault();
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
            String query = httpExchange.getRequestURI().getQuery();
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
                        httpExchange.sendResponseHeaders(StatusCode.OK.getCode(), 0);
                        break;
                    }

                    id = Integer.parseInt(query.substring(3));
                    taskManager.removeTask(id);
                    System.out.println("Удалили задачу " + id);
                    httpExchange.sendResponseHeaders(StatusCode.OK.getCode(), 0);
                    break;
                case "POST":
                    String json = readText(httpExchange);
                    if (json.isEmpty()) {
                        System.out.println("Тело запроса пусто");
                        httpExchange.sendResponseHeaders(StatusCode.NO_BODY.getCode(), 0);
                        break;
                    }

                    final Task taskToPost = gson.fromJson(json, Task.class);
                    id = taskToPost.getId();
                    if (id != 0) {
                        taskManager.updateTask(id, taskToPost);
                        System.out.println("Обновили задачу с id = " + id);
                        httpExchange.sendResponseHeaders(StatusCode.OK.getCode(), 0);
                    } else {
                        id = taskManager.addTask(taskToPost);
                        System.out.println("Создали новую задачу с id = " + id);
                        final String responseToPost = gson.toJson(taskToPost);
                        sendText(httpExchange, responseToPost);
                    }
                    break;
                default:
                    System.out.println("/tasks получен " + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(StatusCode.UNKNOWN.getCode(), 0);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
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
                        httpExchange.sendResponseHeaders(StatusCode.OK.getCode(), 0);
                        break;
                    }

                    id = Integer.parseInt(query.substring(3));
                    taskManager.removeEpic(id);
                    System.out.println("Удалили задачу " + id);
                    httpExchange.sendResponseHeaders(StatusCode.OK.getCode(), 0);
                    break;
                case "POST":
                    String json = readText(httpExchange);
                    if (json.isEmpty()) {
                        System.out.println("Тело запроса пусто");
                        httpExchange.sendResponseHeaders(StatusCode.NO_BODY.getCode(), 0);
                        break;
                    }

                    final Epic epicToPost = gson.fromJson(json, Epic.class);
                    id = epicToPost.getId();

                    if (id != 0) {
                        taskManager.updateEpic(id, epicToPost);
                        System.out.println("Обновили эпик с id = " + id);
                        httpExchange.sendResponseHeaders(StatusCode.OK.getCode(), 0);
                    } else {
                        id = taskManager.addEpic(epicToPost);
                        System.out.println("Создали новый эпик с id = " + id);
                        final String responseToPost = gson.toJson(epicToPost);
                        sendText(httpExchange, responseToPost);
                    }
                    break;
                default:
                    System.out.println("/epic получен " + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(StatusCode.UNKNOWN.getCode(), 0);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
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
                        httpExchange.sendResponseHeaders(StatusCode.OK.getCode(), 0);
                        break;
                    }

                    id = Integer.parseInt(query.substring(3));
                    taskManager.removeSubtask(id);
                    System.out.println("Удалили задачу " + id);
                    httpExchange.sendResponseHeaders(StatusCode.OK.getCode(), 0);
                    break;
                case "POST":
                    String json = readText(httpExchange);
                    if (json.isEmpty()) {
                        System.out.println("Тело запроса пусто");
                        httpExchange.sendResponseHeaders(StatusCode.NO_BODY.getCode(), 0);
                        break;
                    }

                    final Subtask subtaskToPost = gson.fromJson(json, Subtask.class);
                    id = subtaskToPost.getId();

                    if (id != 0) {
                        taskManager.updateSubtask(id, subtaskToPost);
                        System.out.println("Обновили подзадачу с id = " + id);
                        httpExchange.sendResponseHeaders(StatusCode.OK.getCode(), 0);
                    } else {
                        id = taskManager.addSubtask(subtaskToPost);
                        System.out.println("Создали новую подзадачу с id = " + id);
                        final String responseToPost = gson.toJson(subtaskToPost);
                        sendText(httpExchange, responseToPost);
                    }
                    break;
                default:
                    System.out.println("/subtask получен " + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(StatusCode.UNKNOWN.getCode(), 0);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    public void HistoryHandler(HttpExchange httpExchange) {
        try {
            String method = httpExchange.getRequestMethod();
            URI responseURI = httpExchange.getRequestURI();
            String path = responseURI.getPath();

            if (path.endsWith("/history") && method.equals("GET")) {
                final String historyJson = gson.toJson(taskManager.getHistory());
                System.out.println("История получена");
                sendText(httpExchange, historyJson);
            } else if (path.endsWith("/tasks/") && method.equals("GET")) {
                final String prTasksJson = gson.toJson(taskManager.getPrioritizedTasks());
                System.out.println("Список приоритетных задач получен");
                sendText(httpExchange, prTasksJson);
            } else {
                httpExchange.sendResponseHeaders(StatusCode.NO_FOUND.getCode(), 0);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private String readText(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        return new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
    }

    private void sendText(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(StatusCode.OK.getCode(), 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(DEFAULT_CHARSET));
        }
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}