package manager;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Epic;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    /*
    static public TaskManager getDefault() {
        return new HttpTaskManager(KVServer.PORT, true);
    }

    public static KVServer getDefaultKVServer() throws IOException {
        final KVServer kvServer = new KVServer();
        kvServer.start();
        return kvServer;
    }
     */

    public static Gson getGson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        return gson;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
