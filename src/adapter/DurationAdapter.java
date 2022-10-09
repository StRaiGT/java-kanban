package adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration localDate) throws IOException {
        if (localDate == null) {
            jsonWriter.nullValue();
            return;
        }
        String duration = localDate.toString();
        jsonWriter.value(duration);
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        String duration = jsonReader.nextString();
        return Duration.parse(duration);
    }
}
