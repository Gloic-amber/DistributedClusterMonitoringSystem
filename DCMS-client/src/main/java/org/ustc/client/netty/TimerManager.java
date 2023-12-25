package org.ustc.client.netty;

import java.util.Timer;
import java.util.TimerTask;

/**
 * ClassName: TimerManager
 * Package: org.ustc.client.netty
 * Description:
 *
 * @Author Gloic
 * @Create 2023/12/25
 * @Version 1.0
 */
public class TimerManager {

    private static TimerManager instance = new TimerManager();

    private Timer timer = new Timer();
    private TimerTask timerTask;

    private TimerManager() {}

    public static TimerManager getInstance() {
        return instance;
    }

    public void schedule(TimerTask task, long delay, long period) {
        if (timerTask != null) {
            timerTask.cancel();
        }
        timerTask = task;
        timer.schedule(timerTask, delay, period);
    }
}
