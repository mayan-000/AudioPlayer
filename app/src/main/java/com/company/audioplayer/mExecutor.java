package com.company.audioplayer;

import java.util.concurrent.Executor;

public class mExecutor implements Executor {

    public mExecutor() {}

    @Override
    public void execute(Runnable command) {
        new Thread(command).start();
    }
}
