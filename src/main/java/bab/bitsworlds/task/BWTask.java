package bab.bitsworlds.task;

import bab.bitsworlds.task.responses.DefaultResponse;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BWTask {
    public BWTask() {
        TasksCore.queue.add(this);
    }

    public BWTaskResponse execute() {
        try {
            return Executors.newSingleThreadExecutor().submit(() -> {
                while (true) {
                    if (TasksCore.queue.peek().equals(this)) {
                        TasksCore.queue.remove();
                        return run();
                    }

                    TimeUnit.MILLISECONDS.sleep(50);
                }
            }).get();
        } catch (Exception e) {
            return new BWExceptionResponse(0, e);
        }
    }

    protected BWTaskResponse run() throws Exception {
        throw new IllegalAccessException("This method is not for use");
    }

    public class BWExceptionResponse extends DefaultResponse {
        public Exception exception;

        public BWExceptionResponse(int code, Exception exception) {
            super(code);
            this.code = code;
            this.exception = exception;
        }
    }
}