package API.adapters;

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
        StringBuilder sb = new StringBuilder(jsonReader.nextString());
        String[] json = jsonReader.nextString().split(",");
        Duration duration;

        if (!sb.isEmpty()) {
            if (json.length == 2) {
                int hhIndex = sb.indexOf("Hours");
                int hhLastIndex = sb.lastIndexOf("Hours");
                int mmIndex = sb.indexOf("Minutes");
                int mmLastIndex = sb.lastIndexOf("Minutes");

                sb.delete(hhIndex, hhLastIndex);
                sb.delete(mmIndex, mmLastIndex);
                String formatted = sb.toString().trim();
                String[] durationParts = formatted.split(",");

                duration = Duration.ofHours(Integer.parseInt(durationParts[0]))
                        .plusMinutes(Integer.parseInt(durationParts[1]));
                return duration;
            } else {
                int ddIndex = sb.indexOf("Days");
                int ddLastIndex = sb.lastIndexOf("Days");
                int hhIndex = sb.indexOf("Hours");
                int hhLastIndex = sb.lastIndexOf("Hours");
                int mmIndex = sb.indexOf("Minutes");
                int mmLastIndex = sb.lastIndexOf("Minutes");

                sb.delete(ddIndex, ddLastIndex);
                sb.delete(hhIndex, hhLastIndex);
                sb.delete(mmIndex, mmLastIndex);
                String formatted = sb.toString().trim();
                String[] durationParts = formatted.split(",");

                duration = Duration.ofDays(Integer.parseInt(durationParts[0]))
                        .plusHours(Integer.parseInt(durationParts[1]))
                        .plusMinutes(Integer.parseInt(durationParts[2]));
                return duration;
            }
        } else {
            return null;
        }
    }
}
