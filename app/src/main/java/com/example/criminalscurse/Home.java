package com.example.criminalscurse;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chibde.visualizer.LineVisualizer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class Home extends LocalizationActivity {
    SharedPreferences shared;
    SharedPreferences.Editor editor;
    FragmentManager manager;
    FragmentTransaction transaction;
    ImageButton buttonmenu;
    Vibrator vibrator;
    Timer t;
    int counter;
    boolean menuOpened;
    MediaPlayer mediaPlayer;
    boolean scrollright;
    File file;
    final int version = android.os.Build.VERSION.SDK_INT;
    MediaRecorder recorder;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        recorder = new MediaRecorder();
        counter = 0;
        final TextView onlinecounter = findViewById(R.id.counter);
        progressBar = new ProgressBar(Home.this);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setId(View.generateViewId());
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        menuOpened = false;
        scrollright = true;
        final HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.scrollview);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollView.getScrollX() == 0) {
                    scrollright = true;
                    return;
                }
                if (version >= 13) {
                    Point size = new Point();
                    Display display = getWindowManager().getDefaultDisplay();
                    display.getSize(size);
                    if (scrollView.getScrollX() == scrollView.getChildAt(0).getMeasuredWidth() - size.x) {
                        scrollright = false;
                        return;
                    }
                } else {
                    if (scrollView.getScrollX() == scrollView.getChildAt(0).getMeasuredWidth() - getWindowManager().getDefaultDisplay().getWidth()) {
                        scrollright = false;
                        return;
                    }
                }

            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                t.cancel();
                t.purge();
                return false;
            }
        });
        ////
        mediaPlayer = MediaPlayer.create(this, R.raw.blop);
        buttonmenu = findViewById(R.id.menu);
        buttonmenu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (buttonmenu.getBackground().getConstantState() == ContextCompat.getDrawable(Home.this, R.drawable.close).getConstantState() && scrollView.getVisibility() == View.VISIBLE) {
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    buttonmenu.setBackground(ContextCompat.getDrawable(Home.this, R.drawable.close));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    buttonmenu.setBackground(ContextCompat.getDrawable(Home.this, R.drawable.menu));
                }
                return false;
            }
        });
        shared = PreferenceManager.getDefaultSharedPreferences(this);
        editor = shared.edit();
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (manager.getActiveNetwork() == null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                    .setSmallIcon(R.drawable.robber)
                    .setContentTitle(getString(R.string.nointernet))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager;
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel("1", "Connection Error", importance);
                notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(1, builder.build());
            } else {
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                notificationManagerCompat.notify(1, builder.build());
            }
        }
        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = shared.edit();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            editor.clear();
            editor.commit();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            editor.clear();
            editor.commit();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            editor.clear();
            editor.commit();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            editor.clear();
            editor.commit();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            editor.clear();
            editor.commit();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            editor.clear();
            editor.commit();
        }
        String username = shared.getString("username", "Empty");
        if (username.equals("Empty")) {
            editor.clear();
            editor.commit();
        }
        String password = shared.getString("password", "Empty");
        if (password.equals("Empty")) {
            editor.clear();
            editor.commit();
        }
        String newUser = shared.getString("New", "Yes");
        if (newUser.equals("Yes")) {
            editor.clear();
            editor.commit();
            startActivity(new Intent(this, Introduction.class));
            finish();
            overridePendingTransition(R.anim.fade_in, 0);
            return;
        }
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(0, 0);
        transaction.replace(R.id.frag, new FirstFragment()).commit();
        final FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebase.getReference("Users/").child(shared.getString("username", "") + "/").child("presence/");
        ref.setValue("online");
        ref.onDisconnect().setValue("offline");
        firebase.getReference("Users/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int x = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.child("presence").getValue().equals("online") && data.child("station").getValue().equals(shared.getString("station", ""))) {
                        x++;
                    }
                    onlinecounter.setText(x + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final LineVisualizer visualizer = findViewById(R.id.visualizer);
        visualizer.setColor(Color.WHITE);
        visualizer.setStrokeWidth(10);
        database.collection("stations").whereEqualTo("username", shared.getString("station", "")).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://criminals-curse-1592081187332.appspot.com/stations/" + shared.getString("station", "") + "/Voice/");
                ref.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ListResult> task) {
                        if (counter == 0) {
                            counter++;
                            return;
                        }
                        if (task.getResult().getItems().size() == 0) {
                            return;
                        }

                        task.getResult().getItems().get(task.getResult().getItems().size() - 1).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this, "2")
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle("New Voice Message!")
                                        .setAutoCancel(true)
                                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationManager notificationManager;
                                    int importance = NotificationManager.IMPORTANCE_HIGH;
                                    NotificationChannel channel = new NotificationChannel("2", "New Voice Message", importance);
                                    notificationManager = getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);
                                    notificationManager.notify(2, builder.build());
                                } else {
                                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(Home.this);
                                    notificationManagerCompat.notify(2, builder.build());
                                }
                                final MediaPlayer mediaPlayer = new MediaPlayer();
                                try {
                                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    mediaPlayer.setDataSource(Home.this, task.getResult());
                                    mediaPlayer.prepareAsync();
                                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            final TextView recording = findViewById(R.id.recording);
                                            mediaPlayer.setVolume(100,100);
                                            mediaPlayer.start();
                                            if(recording.getVisibility()==View.VISIBLE){
                                                visualizer.setPlayer(mediaPlayer.getAudioSessionId());
                                                return;
                                            }
                                            visualizer.setVisibility(View.VISIBLE);
                                            visualizer.setPlayer(mediaPlayer.getAudioSessionId());
                                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                @Override
                                                public void onCompletion(MediaPlayer mp) {
                                                    visualizer.setVisibility(View.GONE);
                                                    final ImageView recordpic=findViewById(R.id.recordpic);
                                                    recordpic.setVisibility(View.VISIBLE);
                                                    recordpic.startAnimation(AnimationUtils.loadAnimation(Home.this,R.anim.fade_in));
                                                }
                                            });
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        });

        final TextView recording = findViewById(R.id.recording);
        final ImageView recordingimage = findViewById(R.id.recordingimage);
        recordingimage.startAnimation(AnimationUtils.loadAnimation(Home.this, R.anim.live));
        final View trigger = findViewById(R.id.trigger);
        final ImageView recordpic=findViewById(R.id.recordpic);
        recordpic.startAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_in));
        trigger.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    recordpic.setVisibility(View.GONE);
                    if (recording.getText().toString().equals(getString(R.string.send))) {
                        return true;
                    }
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Criminal's Curse/Voice/");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    file = new File(dir, new Date().getTime() + ".mpeg");
                    if(Build.VERSION.SDK_INT <26) {
                        recorder.setOutputFile(file.getAbsoluteFile().getPath());
                    }else{
                        recorder.setOutputFile(file.getAbsoluteFile());
                    }
                    recorder.setAudioEncodingBitRate(16 * 44100);
                    recorder.setAudioSamplingRate(44100);
                    try {
                        recorder.prepare();
                        recorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recording.setText(getString(R.string.recording));
                    visualizer.setVisibility(View.GONE);
                    recording.setVisibility(View.VISIBLE);
                    recordingimage.setVisibility(View.VISIBLE);
                    recordingimage.startAnimation(AnimationUtils.loadAnimation(Home.this, R.anim.live));

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (recording.getText().toString().equals(getString(R.string.send))) {
                        return true;
                    }
                    recording.setText(getString(R.string.send));
                    ViewGroup.LayoutParams params = recordingimage.getLayoutParams();
                    LinearLayout linear = findViewById(R.id.linearrecord);
                    linear.addView(progressBar, params);
                    linear.removeView(recording);
                    linear.addView(recording);
                    progressBar.setVisibility(View.VISIBLE);
                    recordingimage.clearAnimation();
                    recordingimage.setVisibility(View.GONE);
                    try {
                        recorder.stop();
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        recording.setVisibility(View.GONE);
                        recordpic.setVisibility(View.VISIBLE);
                        recordpic.startAnimation(AnimationUtils.loadAnimation(Home.this,R.anim.fade_in));
                        linear.removeView(progressBar);
                        recording.setText(getString(R.string.recording));
                        return true;
                    }
                    recorder.release();
                    StorageMetadata metadata = new StorageMetadata.Builder().setContentType("audio/mpeg").build();
                    FirebaseStorage.getInstance().getReferenceFromUrl("gs://criminals-curse-1592081187332.appspot.com/stations/" + shared.getString("station", "") + "/Voice/" + new Date().getTime() + ".mpeg").putFile(Uri.fromFile(file), metadata).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            database.collection("stations").whereEqualTo("username", shared.getString("station", "")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (final DocumentSnapshot document : task.getResult()) {
                                        database.collection("stations").document(document.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                String x = (String) task.getResult().getData().get("chat");
                                                if (x.length() == 10) {
                                                    x = "";
                                                }
                                                database.collection("stations").document(document.getId()).update("chat", x + "1");
                                                progressBar.setVisibility(View.GONE);
                                                recording.setVisibility(View.GONE);
                                                recordpic.setVisibility(View.VISIBLE);
                                                recordpic.startAnimation(AnimationUtils.loadAnimation(Home.this,R.anim.fade_in));
                                                LinearLayout linear = findViewById(R.id.linearrecord);
                                                linear.removeView(progressBar);
                                                recording.setText(getString(R.string.recording));
                                                file.delete();
                                            }
                                        });

                                    }
                                }
                            });
                        }

                    });
                }
                return true;
            }

        });
    }

    public void open(View v) {
        View white = findViewById(R.id.white);
        if (white.getScaleY() < 26.0 && white.getScaleY() > 1) {
            return;
        }
        ImageButton menu = (ImageButton) findViewById(R.id.menu);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
        }
        mediaPlayer.start();
        HorizontalScrollView scrollBar = (HorizontalScrollView) findViewById(R.id.scrollview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        FrameLayout r = findViewById(R.id.frag);
        if (!menuOpened) {
            if(Build.VERSION.SDK_INT >26) {
                menu.setTooltipText(getString(R.string.closemenu));
            }
            menuOpened = true;
            menu.setBackground(ContextCompat.getDrawable(this, R.drawable.close));
            Animation animation = AnimationUtils.loadAnimation(Home.this, R.anim.zoomin_menu);
            white.animate().scaleYBy(25).setDuration(400);
            scrollBar.setVisibility(View.VISIBLE);
            scrollBar.startAnimation(animation);
            t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.scrollview);
                    if (scrollView.getVisibility() == View.VISIBLE) {
                        if (scrollright == true) {
                            scrollView.setScrollX(scrollView.getScrollX() + 1);
                        } else {
                            scrollView.setScrollX(scrollView.getScrollX() - 1);
                        }
                    }

                }
            }, 0, 30);
            return;
        } else {
            menuOpened = false;
            if(Build.VERSION.SDK_INT >26) {
                menu.setTooltipText(getString(R.string.menu));
            }
            Animation animm = AnimationUtils.loadAnimation(Home.this, R.anim.zoomout_menu);
            scrollBar.startAnimation(animm);
            scrollBar.setVisibility(View.INVISIBLE);
            menu.setBackground(ContextCompat.getDrawable(this, R.drawable.menu));
            white.animate().scaleY(1).setDuration(600);
            t.cancel();
            t.purge();
            return;
        }

    }

    public void menuOpen(View v) {
        TextView title = (TextView) findViewById(R.id.title);
        Animation anim = AnimationUtils.loadAnimation(Home.this, R.anim.live);
        HorizontalScrollView scrollBar = (HorizontalScrollView) findViewById(R.id.scrollview);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.face:
                open(v);
                transaction.setCustomAnimations(0, 0);
                transaction.replace(R.id.frag, new face()).commit();
                ImageView face = findViewById(R.id.face);
                ImageView image = findViewById(R.id.map);
                ImageView image123 = findViewById(R.id.phoneinspection);
                ImageView license1222243 = findViewById(R.id.report);
                license1222243.clearAnimation();
                image123.clearAnimation();
                ImageView settings11 = findViewById(R.id.settings);
                settings11.clearAnimation();
                image.clearAnimation();
                face.startAnimation(anim);
                break;
            case R.id.map:
                open(v);
                transaction.setCustomAnimations(0, 0);
                transaction.replace(R.id.frag, new MapFragment()).commit();
                ImageView license = findViewById(R.id.map);
                ImageView image122 = findViewById(R.id.phoneinspection);
                image122.clearAnimation();
                ImageView license1222233 = findViewById(R.id.report);
                license1222233.clearAnimation();
                ImageView image1 = findViewById(R.id.face);
                image1.clearAnimation();
                ImageView settings4 = findViewById(R.id.settings);
                settings4.clearAnimation();
                ImageView criminal5 = findViewById(R.id.criminals);
                criminal5.clearAnimation();
                license.startAnimation(anim);
                break;
            case R.id.phoneinspection:
                open(v);
                transaction.setCustomAnimations(0, 0);
                transaction.replace(R.id.frag, new PhoneListFragment()).commit();
                ImageView license1 = findViewById(R.id.map);
                ImageView license122223 = findViewById(R.id.report);
                license122223.clearAnimation();
                ImageView image2 = findViewById(R.id.face);
                ImageView image124 = findViewById(R.id.phoneinspection);
                image124.clearAnimation();
                image2.clearAnimation();
                ImageView settings3 = findViewById(R.id.settings);
                settings3.clearAnimation();
                ImageView criminal3 = findViewById(R.id.criminals);
                criminal3.clearAnimation();
                image124.startAnimation(anim);
                break;
          case R.id.report:
                open(v);
                transaction.setCustomAnimations(0, 0);
                transaction.replace(R.id.frag, new MainFragment()).commit();
                ImageView license12 = findViewById(R.id.map);
                ImageView license12222 = findViewById(R.id.report);
                license12222.clearAnimation();
                ImageView image22 = findViewById(R.id.face);
              ImageView criminal1 = findViewById(R.id.criminals);
              criminal1.clearAnimation();
              ImageView settings2 = findViewById(R.id.settings);
              settings2.clearAnimation();
                ImageView image1242 = findViewById(R.id.phoneinspection);
                image1242.clearAnimation();
                image22.clearAnimation();
                license12222.startAnimation(anim);
                break;
            case R.id.criminals:
                open(v);
                transaction.setCustomAnimations(0, 0);
                transaction.replace(R.id.frag, new BlankFragment()).commit();
                ImageView license121 = findViewById(R.id.map);
                license121.clearAnimation();
                ImageView license122224 = findViewById(R.id.report);
                license122224.clearAnimation();
                ImageView image223 = findViewById(R.id.face);
                ImageView image12422 = findViewById(R.id.phoneinspection);
                ImageView criminal = findViewById(R.id.criminals);
                ImageView settings1 = findViewById(R.id.settings);
                settings1.clearAnimation();
                criminal.clearAnimation();
                image12422.clearAnimation();
                image223.clearAnimation();
                criminal.startAnimation(anim);
                break;
            case R.id.settings:
                open(v);
                transaction.setCustomAnimations(0, 0);
                transaction.replace(R.id.frag, new Settings()).commit();
                ImageView license12122 = findViewById(R.id.map);
                license12122.clearAnimation();
                ImageView license1222244 = findViewById(R.id.report);
                license1222244.clearAnimation();
                ImageView image2233 = findViewById(R.id.face);
                ImageView image124221 = findViewById(R.id.phoneinspection);
                ImageView criminal11 = findViewById(R.id.criminals);
                ImageView settings = findViewById(R.id.settings);
                settings.clearAnimation();
                criminal11.clearAnimation();
                image124221.clearAnimation();
                image2233.clearAnimation();
                settings.startAnimation(anim);
                break;


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/").child(shared.getString("username", "") + "/").child("presence/");
        final TextView onlinecounter = findViewById(R.id.counter);
        ref.setValue("online");
    }
}
