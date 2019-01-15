package bab.bitsworlds.task.responses;

import bab.bitsworlds.task.BWTaskResponse;

public class DefaultResponse implements BWTaskResponse {
    public int code;

    public DefaultResponse(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return 0;
    }
}
