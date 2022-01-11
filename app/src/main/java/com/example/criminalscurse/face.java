package com.example.criminalscurse;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class face extends Fragment {
    PortraitCameraView camera;
    CascadeClassifier faceDetection;
    CascadeClassifier eyeDetection;
    MediaPlayer mediaPlayer;
    FaceRecognizer faceRecognizer;
    Vibrator vibrator;
    int counter;
    int counterbefore;
    int counterbeforebefore;
    int counterbeforebeforebefore;
    Bitmap bit;
    HashMap<Integer,String> names;
    public face() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_face, container, false);
        counterbefore=1;
        counterbeforebefore=2;
        counterbeforebeforebefore=3;
        bit=null;
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.beep);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        System.loadLibrary("opencv_java3");
        faceRecognizer = LBPHFaceRecognizer.create();
        ;
        camera = view.findViewById(R.id.camera);
        camera.setMaxFrameSize(720, 480);
        final InputStream inputStream = getActivity().getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
        File directory = getActivity().getDir("cascade", Context.MODE_PRIVATE);
        final File cascadeFile = new File(directory, "haarcascade_frontalface_alt.xml");
        try {
            FileOutputStream outputStream = new FileOutputStream(cascadeFile);
            byte[] buffer = new byte[4096];
            int byteread;
            while ((byteread = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteread);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //eyes
        InputStream inputStream1 = getActivity().getResources().openRawResource(R.raw.haarcascade_eye);
        File directory1 = getActivity().getDir("cascade", Context.MODE_PRIVATE);
        final File cascadeFile1 = new File(directory1, "haarcascade_eye.xml");
        try {
            FileOutputStream outputStream1 = new FileOutputStream(cascadeFile1);
            byte[] buffer1 = new byte[4096];
            int byteread;
            while ((byteread = inputStream1.read(buffer1)) != -1) {
                outputStream1.write(buffer1, 0, byteread);
            }
            inputStream1.close();
            outputStream1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        faceDetection = new CascadeClassifier(cascadeFile.getAbsolutePath());
        eyeDetection = new CascadeClassifier(cascadeFile1.getAbsolutePath());
        final ImageButton save = (ImageButton) view.findViewById(R.id.save);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (save.getBackground().getConstantState() == ContextCompat.getDrawable(getActivity(), R.drawable.saveoff).getConstantState()) {
                    save.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.saveon));

                } else {
                    save.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.saveoff));

                }
            }
        });
        save.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    save.animate().scaleY(0.7f).scaleX(0.7f).setDuration(40);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    save.animate().scaleX(1.0f).scaleY(1.0f).setDuration(40);
                }
                return false;
            }
        });
        final ImageButton flashlight = (ImageButton) view.findViewById(R.id.flashlight);
        flashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashlight.getBackground().getConstantState() == ContextCompat.getDrawable(getActivity(), R.drawable.flashlightoff).getConstantState()) {
                    Camera.Parameters p = camera.getCamera().getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.getCamera().setParameters(p);
                    flashlight.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.flashlighton));
                } else {
                    Camera.Parameters p = camera.getCamera().getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.getCamera().setParameters(p);
                    flashlight.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.flashlightoff));
                }
            }
        });
        flashlight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    flashlight.animate().scaleY(0.7f).scaleX(0.7f).setDuration(40);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    flashlight.animate().scaleX(1.0f).scaleY(1.0f).setDuration(40);
                }
                return false;
            }
        });
////

        camera.setVisibility(View.GONE);
        flashlight.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        final ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setId(View.generateViewId());
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        final TextView info = new TextView(getActivity());
        info.setText(getActivity().getString(R.string.preprocess));
        Typeface typeface = ResourcesCompat.getFont(getActivity(), R.font.acrade);
        info.setTypeface(typeface);
        info.setTextColor(Color.WHITE);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.addRule(RelativeLayout.BELOW, progressBar.getId());
        params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        RelativeLayout rel = view.findViewById(R.id.lay);
        rel.addView(info, params1);
        rel.addView(progressBar, params);

         names=new HashMap<>();
        final List<Mat> images = new ArrayList<>();
        final MatOfInt labels = new MatOfInt();
        final List<Integer> labelsList = new ArrayList<>();
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        counter = 0;
        storage.getReferenceFromUrl("gs://criminals-curse-1592081187332.appspot.com/stations/" + shared.getString("station", "") + "/Wanted/").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult task) {
                for (final StorageReference s : task.getPrefixes()) {
                    storage.getReferenceFromUrl("gs://criminals-curse-1592081187332.appspot.com/stations/" + shared.getString("station", "") + "/Wanted/" + s.getName()).listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ListResult> task) {
                            for (StorageReference stor : task.getResult().getItems()) {
                                stor.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                            bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            final Mat image = new Mat(bit.getHeight(), bit.getWidth(), CvType.CV_8UC4);
                                            Utils.bitmapToMat(bit, image);
                                            Imgproc.resize(image, image, new Size(720, 480));
                                            MatOfRect original = new MatOfRect();
                                            faceDetection.detectMultiScale(image, original);
                                           Rect cropped =null;
                                            try {
                                                cropped = original.toArray()[0];
                                            }catch(Exception e) {
                                            return;
                                            }
                                            Mat imageresized = new Mat();
                                            imageresized = image.submat(new Rect(cropped.x, cropped.y, cropped.width, cropped.height));
                                            Imgproc.resize(imageresized, imageresized, new Size(200, 200));
                                            Imgproc.cvtColor(imageresized, imageresized, 6);
                                            Imgproc.equalizeHist(imageresized, imageresized);
                                            //Mat ed=new Mat();
                                            //Imgproc.adaptiveThreshold(imageresized,imageresized,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,15,30);
                                            //Imgproc.Canny(imageresized,ed,100,100);
                                            images.add(imageresized);
                                            labelsList.add(counter);
                                            names.put(counter,s.getName());
                                            counter++;
                                        labels.fromList(labelsList);
                                        faceRecognizer.train(images, labels);

                                    }
                                });
                            }
                        }
                    });
                }
                                    }
                                });
final Timer timer=new Timer();
timer.schedule(new TimerTask() {
    @Override
    public void run() {
        counterbeforebeforebefore=counterbeforebefore;
        counterbeforebefore=counterbefore;
        counterbefore=counter;
if(counter==counterbefore&& counterbefore==counterbeforebefore&& counterbeforebeforebefore==counterbeforebefore){
    getActivity().runOnUiThread(new Runnable()
    {
        public void run()
        {

    progressBar.setVisibility(View.INVISIBLE);
    info.setVisibility(View.INVISIBLE);
    camera.enableView();
    camera.setVisibility(View.VISIBLE);
    flashlight.setVisibility(View.VISIBLE);
    save.setVisibility(View.VISIBLE);
    timer.cancel();
    timer.purge();
}
    });
}
    }
},1000,1000);

        ////
        camera.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener() {
            @Override
            public void onCameraViewStarted(int width, int height) {
            }

            @Override
            public void onCameraViewStopped() {
            }

            @Override
            public Mat onCameraFrame(Mat inputFrame) {
                final MatOfRect faces = new MatOfRect();
                faceDetection.detectMultiScale(inputFrame, faces);
                for (Rect rect : faces.toArray()) {
                    if (faces.toArray().length > 0) {
                        Mat res = new Mat();
                        res = inputFrame.submat(new Rect(rect.x, rect.y, rect.width, rect.height));
                        if (save.getBackground().getConstantState() == ContextCompat.getDrawable(getActivity(), R.drawable.saveon).getConstantState()) {
                            Bitmap bitmap = Bitmap.createBitmap(rect.width, rect.height, Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(res, bitmap);
                            saveImageStorage(bitmap);
                        }
                        Imgproc.resize(res, res, new Size(200, 200));
                        Imgproc.cvtColor(res, res, 6);
                        Imgproc.equalizeHist(res,res);
                        //Mat edges=new Mat();
                        //Imgproc.adaptiveThreshold(res,res,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,15,30);
                        //Imgproc.Canny(res,edges,100,100);
                        //double[]confidence=new double[1];
                        //int[] label=new int[1];
                        // Bitmap bit=Bitmap.createBitmap(res.width(),res.height(), Bitmap.Config.ARGB_8888);
                        //Utils.matToBitmap(res,bit);
                        //saveImageStorage(bit);
                        int[]label={-1};
                        double confidence[]={0.0};
                        faceRecognizer.predict(res,label,confidence);
                        Mat intruder = new Mat();
                        Utils.bitmapToMat(BitmapFactory.decodeResource(getResources(), R.drawable.face_detect), intruder);
                        Mat safe = new Mat();
                        Utils.bitmapToMat(BitmapFactory.decodeResource(getResources(), R.drawable.safe), safe);

                        try {
                            if(confidence[0]<=80) {
                                Mat submat = inputFrame.submat(new Rect(rect.x, rect.y - rect.height, rect.width, rect.height));
                                Imgproc.resize(intruder, intruder, new Size(rect.width, rect.height));
                                intruder.copyTo(submat);
                                Imgproc.putText(inputFrame, names.get(label[0]) + " | " + Math.round(100-confidence[0])+"%", new Point(rect.x - rect.width / 2, rect.y + rect.height + (rect.height / 2)), 3, 1, new Scalar(255, 0, 0));
                                if (!mediaPlayer.isPlaying()) {
                                    mediaPlayer.start();
                                }
                                vibrator.vibrate(5);
                            }else{
                                    Mat submat = inputFrame.submat(new Rect(rect.x, rect.y - rect.height, rect.width, rect.height));
                                    Imgproc.resize(safe, safe, new Size(rect.width, rect.height));
                                    safe.copyTo(submat);
                            }
                            } catch (Exception e) {

                        }
                    }

                }
                return inputFrame;
            }


        });
        return view;
    }

    private void saveImageStorage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Criminal's Curse/Pictures/");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(myDir, new Date().getTime() + ".jpg");
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // inform to the media scanner about the new file so that it is immediately available to the user.
        MediaScannerConnection.scanFile(getActivity(), new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

    }

}
