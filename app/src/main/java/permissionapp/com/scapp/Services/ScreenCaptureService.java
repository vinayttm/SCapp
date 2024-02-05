package permissionapp.com.scapp.Services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.ImageReader;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import permissionapp.com.scapp.R;
import permissionapp.com.scapp.Utils.Config;

public class ScreenCaptureService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        int resultCode = intent.getIntExtra("result-code", -1);
        Intent resultData = intent.getParcelableExtra("data");
        Config.mediaProjection = ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).getMediaProjection(resultCode, resultData);
        startImageReader();
        return Service.START_NOT_STICKY;
    }

    private void startImageReader() {
        if (Config.mediaProjection != null) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            Config.imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 10);
            Config.mediaProjection.createVirtualDisplay(
                    "ScreenCapture",
                    screenWidth,
                    screenHeight,
                    getResources().getDisplayMetrics().densityDpi,
                    0,
                    Config.imageReader.getSurface(),
                    null,
                    null
            );
        }
    }


    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "screenCaptureChannelId",
                    "Screen Capture Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this, "screenCaptureChannelId")
                    .setContentTitle("Screen Capture Service")
                    .setContentText("Capturing screen...")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .build();
            startForeground(1, notification);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
