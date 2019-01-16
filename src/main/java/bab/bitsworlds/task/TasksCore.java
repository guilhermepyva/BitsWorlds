package bab.bitsworlds.task;

import java.util.LinkedList;
import java.util.Queue;

public class TasksCore {
    public static Queue<BWTask> globalQueue;

    public static void init() {
        globalQueue = new LinkedList<>();
    }
}