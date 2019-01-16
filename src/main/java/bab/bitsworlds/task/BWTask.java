package bab.bitsworlds.task;

import bab.bitsworlds.task.responses.DefaultResponse;

import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class BWTask {
    public Queue<BWTask> queue;

    protected BWTask() {
        queue = TasksCore.globalQueue;
        this.queue.add(this);
    }

    public BWTask(Queue<BWTask> queue) {
        this.queue = queue;
        this.queue.add(this);
    }

    public BWTaskResponse execute() {
        try {
            return Executors.newSingleThreadExecutor().submit(() -> {
                while (true) {
                    if (this.queue.peek().equals(this)) {
                        this.queue.remove();

                        return run();
                    }

                    TimeUnit.MILLISECONDS.sleep(50);
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            return new BWExceptionResponse(0, e);
        }
    }

    abstract protected BWTaskResponse run();

    public class BWExceptionResponse extends DefaultResponse {
        public Exception exception;

        public BWExceptionResponse(int code, Exception exception) {
            super(code);
            this.code = code;
            this.exception = exception;
        }
    }
}