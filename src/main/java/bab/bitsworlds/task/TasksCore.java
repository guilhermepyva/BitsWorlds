package bab.bitsworlds.task;

import java.util.LinkedList;
import java.util.Queue;

public class TasksCore {
    public static Queue<BWTask> queue;

    public static void init() {
        queue = new LinkedList<>();
    }
}