package manager;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class InstantAdapter extends TypeAdapter<Instant> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public void write(JsonWriter jsonWriter, Instant instant) throws IOException {

        if (instant != null) {
            LocalDateTime time = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
            jsonWriter.value(time.format(formatter));
        } else {
            jsonWriter.value(LocalDateTime.of(1, 1, 1, 0, 0, 0)
                    .format(formatter));
        }
    }

    @Override
    public Instant read(JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), formatter).toInstant(ZoneOffset.UTC);
    }
}
