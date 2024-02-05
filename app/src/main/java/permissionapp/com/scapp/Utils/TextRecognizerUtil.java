package permissionapp.com.scapp.Utils;
import android.graphics.Bitmap;
import android.util.Log;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.List;

public class TextRecognizerUtil {

    private static TextRecognizer textRecognizer;

    static {
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    public static void processBitmapImage(Bitmap bitmap) {
        if (bitmap != null) {
            InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
            textRecognizer.process(inputImage)
                    .addOnSuccessListener(TextRecognizerUtil::processTextRecognitionResults)
                    .addOnFailureListener(e -> {
                        Log.e("TextRecognition", "Text recognition error: " + e.getMessage());
                    });
        }
    }

    private static void processTextRecognitionResults(Text text) {
        List<String> stringList = new ArrayList<>();
        List<Text.TextBlock> textBlocks = text.getTextBlocks();
        for (Text.TextBlock textBlock : textBlocks) {
            String recognizedText = textBlock.getText();
            stringList.add(recognizedText);
        }
        Log.d("OUTPUT", stringList.toString());
    }
}