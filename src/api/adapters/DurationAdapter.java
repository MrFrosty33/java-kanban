package api.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration != null) {
            if (duration.toHours() < 24) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH 'Hours', mm 'Minutes'");
                jsonWriter.value(LocalTime.of(duration.toHoursPart(), duration.toMinutesPart()).format(formatter));
            } else {
                jsonWriter.value(String.format("%02d Days, %02d Hours, %02d Minutes",
                        duration.toDaysPart(),
                        duration.toHoursPart(),
                        duration.toMinutesPart()));
            }
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        // пример "03 Days, 10 Hours, 25 Minutes"
        String nextString = jsonReader.nextString();
        Duration duration = Duration.ZERO;

        if (nextString == null || nextString.isEmpty()) {
            return null;
        }

        String[] durationParts = nextString.trim().split(",");

        if (durationParts.length == 3) {
            int days = Integer.parseInt(durationParts[0].replace(" Days", "").trim());
            int hours = Integer.parseInt(durationParts[1].replace(" Hours", "").trim());
            int minutes = Integer.parseInt(durationParts[2].replace(" Minutes", "").trim());
            duration = Duration.ofDays(days).plusHours(hours).plusMinutes(minutes);
        } else if (durationParts.length == 2) {
            int hours = Integer.parseInt(durationParts[0].replace(" Hours", "").trim());
            int minutes = Integer.parseInt(durationParts[1].replace(" Minutes", "").trim());
            duration = Duration.ofHours(hours).plusMinutes(minutes);
        }

        return duration;
    }
}
