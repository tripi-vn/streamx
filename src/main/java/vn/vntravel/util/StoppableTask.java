package vn.vntravel.util;

import java.util.concurrent.TimeoutException;

public interface StoppableTask {
    void requestStop() throws Exception;
    void awaitStop(Long timeout) throws TimeoutException;
}
