package permissionapp.com.scapp;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import permissionapp.com.scapp.Services.ScreenCaptureService;
import permissionapp.com.scapp.Utils.AutoWorker;
import permissionapp.com.scapp.Utils.Config;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startScreenCapture();
    }

    private static final int REQUEST_MEDIA_PROJECTION = 1000;
    private final AutoWorker  autoWorker = new AutoWorker(this::takingPicture);


    private void startScreenCapture() {
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        if (mediaProjectionManager != null) {
            Intent permissionIntent = mediaProjectionManager.createScreenCaptureIntent();
            startActivityForResult(permissionIntent, REQUEST_MEDIA_PROJECTION);
            Toast.makeText(this, "MediaProjectionManager", Toast.LENGTH_SHORT).show();
            autoWorker.startAutoWorker();
        } else {
            Toast.makeText(this, "MediaProjectionManager is null", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == RESULT_OK && data != null) {
            Intent serviceIntent = new Intent(this, ScreenCaptureService.class);
            serviceIntent.putExtra("data", data);
            serviceIntent.putExtra("result-code", resultCode);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Toast.makeText(this, "startForegroundService", Toast.LENGTH_SHORT).show();
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
                Toast.makeText(this, "startService", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "onActivityResult Failed", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void takingPicture()
    {
        System.out.println("---------------------");
        System.out.println("Taking a picture...");
        if (Config.imageReader != null) {
            autoWorker.setAutoWorker();
            Image image =  Config.imageReader.acquireNextImage();
            if (image != null) {
                try {
                    Image.Plane[] planes = image.getPlanes();
                    Image.Plane plane = planes[0];
                    ByteBuffer buffer = plane.getBuffer();
                    int pixelStride = plane.getPixelStride();
                    int rowStride = plane.getRowStride();
                    int rowPadding = rowStride - pixelStride * image.getWidth();
                    Bitmap bitmap = Bitmap.createBitmap(
                            image.getWidth() + rowPadding / pixelStride,
                            image.getHeight(),
                            Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    saveBitmap(bitmap);
                    System.out.println("Saved !");
                    autoWorker.setAutoWorker();
                    System.out.println("---------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    image.close();
                }
            }
        }
    }


    public static void saveBitmap(Bitmap bitmap) {
        String imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/vinay.jpg";
        File fileScreenshot = new File(imagePath);
        if (fileScreenshot.exists()) {
            fileScreenshot.delete();
        } else {
            try {
                fileScreenshot.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Log.d("Screen Shot Image Path ", "Main Screenshot saved to: " + fileScreenshot.getAbsolutePath());
                FileOutputStream fileOutputStream = new FileOutputStream(fileScreenshot);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}