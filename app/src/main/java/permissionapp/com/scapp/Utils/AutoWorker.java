package permissionapp.com.scapp.Utils;

import android.os.Handler;
import android.util.Log;

public class AutoWorker {
    private final Handler handler = new Handler();
    private static final long CHECK_INTERVAL = 5000;
    private final Runnable checkRunnable;

    public AutoWorker(final Runnable callback) {
        checkRunnable = () -> {
            Log.d("AutoWorker", "AutoWorker initial");
            callback.run();
        };
    }

    public void startAutoWorker() {
        handler.postDelayed(checkRunnable, CHECK_INTERVAL);
    }

    public void removeCallback() {
        handler.removeCallbacks(checkRunnable);
    }

    public void setAutoWorker() {
        removeCallback();
        handler.postDelayed(checkRunnable, CHECK_INTERVAL);
    }

}
