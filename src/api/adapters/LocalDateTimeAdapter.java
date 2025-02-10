package api.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy|HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime != null) jsonWriter.value(localDateTime.format(formatter));
        else jsonWriter.nullValue();
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String nextString = jsonReader.nextString();
        if (!nextString.isEmpty()) return LocalDateTime.parse(nextString, formatter);
        else return null;
    }
}
