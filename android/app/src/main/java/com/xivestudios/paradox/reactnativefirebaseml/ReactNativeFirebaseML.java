package com.xivestudios.paradox.reactnativefirebaseml;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;

import java.io.IOException;
import java.util.List;

public class ReactNativeFirebaseML extends ReactContextBaseJavaModule {

    private float _ConfidenceThreshold = 0.8f;

    private FirebaseVisionLabelDetectorOptions options;
    private FirebaseVisionImage image;

//    private Promise firebaseVisionImagePromise;

    public ReactNativeFirebaseML(ReactApplicationContext reactContext) {
        super(reactContext);

    }

    @ReactMethod
    public void show(String message, int duration) {
        Toast.makeText(getReactApplicationContext(), message, duration).show();
    }

    @ReactMethod
    public void visionImage_fromFilePath (float confidenceThreshold, String uri,
                                          final Callback successCallback, Callback errorCallback){
        Activity currentActivity = getCurrentActivity();
        System.out.println("xx act : "+currentActivity.getPackageName());
        if (currentActivity != null) {
            try {
                if (confidenceThreshold > 0.0f && confidenceThreshold <= 1.0f) {
                    options = new FirebaseVisionLabelDetectorOptions.Builder()
                            .setConfidenceThreshold(confidenceThreshold)
                            .build();
                } else {
                    options = new FirebaseVisionLabelDetectorOptions.Builder()
                            .setConfidenceThreshold(_ConfidenceThreshold)
                            .build();
                }
                System.out.println("xx confidence : "+confidenceThreshold);
                if (options == null) {
//                    promise.reject("Firebase_Option_Null","FirebaseVisionLabelDetectorOptions returned null");
                      throw new IllegalViewOperationException("FirebaseVisionLabelDetectorOptions returned null!");
                }else{
                    Uri path = Uri.parse(uri);

                    if(path==null){
                        throw new IllegalViewOperationException("URI returned null!");
                    }

                    image = FirebaseVisionImage.fromFilePath(currentActivity.getApplicationContext(), path);

                    if(image==null){
                        throw new IllegalViewOperationException("FirebaseVisionImage returned null!");
                    }
                    System.out.println("xx img : "+image.toString());
                    FirebaseVisionLabelDetector detector = FirebaseVision.getInstance()
                            .getVisionLabelDetector(options);

                    final Task<List<FirebaseVisionLabel>> result =
                            detector.detectInImage(image)
                                    .addOnSuccessListener(
                                            new OnSuccessListener<List<FirebaseVisionLabel>>() {
                                                @Override
                                                public void onSuccess(List<FirebaseVisionLabel> labels) {
                                                    successCallback.invoke(labels.toArray().toString());
                                                }
                                            })
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(Exception e) {
                                                    // TODO: 05/06/2018 do sothing here
                                                }
                                            });
                }

            } catch (IOException e) {
                System.out.println("xx error : "+e.getMessage());
                errorCallback.invoke(e.getMessage());
            } catch (IllegalViewOperationException e) {
                System.out.println("xx error : "+e.getMessage());
                errorCallback.invoke(e.getMessage());
            } catch (Exception e){
                System.out.println("xx error : "+e.getMessage());
                errorCallback.invoke(e.getMessage());
            }
        }else{
            errorCallback.invoke("Activity is Null");
        }
    }

    @Override
    public String getName() {
        return "ReactNativeFirebaseML";
    }
}
