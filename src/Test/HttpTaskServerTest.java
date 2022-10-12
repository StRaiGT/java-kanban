package Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import model.*;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;
import server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTest {
    static HttpTaskServer server;
    static KVServer kvServer;
    Gson gson = Managers.getGson();
    HttpClient client = HttpClient.newHttpClient();
    int epicId;
    int taskId;
    int subtaskId;

    @BeforeAll
    static void startServers() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer();
        server.start();
    }

    @BeforeEach
    public void beforeEach() {
        server.getTaskManager().removeAllTasks();
        server.getTaskManager().removeAllEpics();
        server.getTaskManager().removeAllSubtasks();

        taskId = server.getTaskManager().addTask(new Task(0, "task 1", State.NEW, "description task 1",
                Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 6, 30)));
        epicId = server.getTaskManager().addEpic(new Epic("epic 1", "description epic 1"));

        subtaskId = server.getTaskManager().addSubtask(new Subtask(0, "subtask 1", State.NEW, "description subtask 1",
                epicId, Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 9, 30)));
    }

    @AfterAll
    static void stopServers() {
        try {
            kvServer.stop();
        } catch (IllegalMonitorStateException | InterruptedException e) {}
        server.stop();
    }

    @Test
    void shouldAddTask() throws IOException, InterruptedException {
        Task task = new Task(0, "task 2", State.NEW, "description task 2",
                Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 15, 30));
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(2, server.getTaskManager().getTasks().size());
    }

    @Test
    void shouldNotAddNullTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals(1, server.getTaskManager().getTasks().size());
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        Task task = new Task(1, "update task 1", State.NEW, "update description task 1",
                Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 6, 30));
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldGetTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
    }

    @Test
    void shouldGetAllTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
        assertEquals(1, server.getTaskManager().getTasks().size());
    }

    @Test
    void shouldRemoveTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(null, server.getTaskManager().getTask(1));
    }

    @Test
    void shouldRemoveAllTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, server.getTaskManager().getTasks().size());
    }

    @Test
    void shouldAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic 2", "description epic 2");
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(2, server.getTaskManager().getEpics().size());
    }

    @Test
    void shouldNotAddNullEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals(1, server.getTaskManager().getEpics().size());
    }

    @Test
    void shouldUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(2, new Epic("update epic 1", "update description epic 1"));
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldGetEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
    }

    @Test
    void shouldGetAllEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
        assertEquals(1, server.getTaskManager().getEpics().size());
    }

    @Test
    void shouldRemoveEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(null, server.getTaskManager().getEpic(2));
    }

    @Test
    void shouldRemoveAllEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, server.getTaskManager().getEpics().size());
    }

    @Test
    void shouldAddSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(0, "subtask 2", State.NEW, "description subtask 2", epicId,
                Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 12, 30));
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(2, server.getTaskManager().getSubtasks().size());
    }

    @Test
    void shouldNotAddNullSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals(1, server.getTaskManager().getSubtasks().size());
    }

    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(subtaskId, "subtask 1", State.NEW, "description subtask 1", epicId,
                Duration.ofMinutes(30), LocalDateTime.of(2003, 1, 28, 12, 30));
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldGetSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
    }

    @Test
    void shouldGetAllSubtasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
        assertEquals(1, server.getTaskManager().getSubtasks().size());
    }

    @Test
    void shouldRemoveSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(null, server.getTaskManager().getSubtask(3));
    }

    @Test
    void shouldRemoveAllSubtasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, server.getTaskManager().getSubtasks().size());
    }

    @Test
    void shouldGetPrioritizedTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> list = gson.fromJson(response.body().toString(), new TypeToken<List<Task>>() {}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(2, list.size());
    }

    @Test
    void shouldGetHistory() throws IOException, InterruptedException {
        server.getTaskManager().getTask(taskId);
        server.getTaskManager().getSubtask(subtaskId);

        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> list = gson.fromJson(response.body().toString(), new TypeToken<List<Task>>() {}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(2, list.size());
    }
}
