package comparators;

import models.Task;

import java.util.Comparator;

public class StartTimeComparator<T extends Task> implements Comparator<T> {
    @Override
    public int compare(T t1, T t2) {
        if(t1.getStartTime() == null) return 1;
        if(t2.getStartTime() == null) return -1;
        if(t1.getStartTime().isBefore(t2.getStartTime()) ) return -1;
        else if (t1.getStartTime().isAfter(t2.getStartTime())) return 1;
        else return 0;
    }
}
