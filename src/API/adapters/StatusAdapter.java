package API.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import models.Status;

import java.io.IOException;

public class StatusAdapter extends TypeAdapter<Status> {
    @Override
    public void write(JsonWriter jsonWriter, Status status) throws IOException {
        switch (status) {
            case NEW:
                jsonWriter.value("NEW");
                break;
            case IN_PROGRESS:
                jsonWriter.value("IN_PROGRESS");
                break;
            case DONE:
                jsonWriter.value("DONE");
                break;
        }
    }

    @Override
    public Status read(JsonReader jsonReader) throws IOException {
        String status = jsonReader.nextString();
        switch (status) {
            case "NEW":
                return Status.NEW;
            case "IN_PROGRESS":
                return Status.IN_PROGRESS;
            case "DONE":
                return Status.DONE;
            default:
                return null;
        }
    }
}
