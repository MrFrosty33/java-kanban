package validators;

import models.Task;

public abstract class TaskValidator<T extends Task> {

    public static <T extends Task> boolean validateTime(T t1, T t2){
        if(t2.getStartTime().isBefore(t1.getEndTime())) return true;
        else return false;
    }
}
