package com.example.criminalscurse;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class MapFragment extends Fragment {
    private LatLng l;
    ArrayList<LatLng> positonList = new ArrayList<LatLng>();
    View view;
    SupportMapFragment supportMapFragment;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    // Create a Cloud Storage reference from the app
    StorageReference storageRef = storage.getReference();
    // Create a reference to 'images/mountains.jpg'
    StorageReference videoref;
    VideoView videoView;
    MediaController mc;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Reports");
    ArrayList<String> longitude = new ArrayList<>();
    ArrayList<String> latitude = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialize view
        view = inflater.inflate(R.layout.fragment_map, container, false);

        //Initialize map fragment
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        //add Latlng
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get map of users in datasnapshot
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String lat = ds.child("latitude").getValue(String.class);
                        String lon = ds.child("longitude").getValue(String.class);
                        Double la = Double.parseDouble(lat) ;
                        Double lo = Double.parseDouble(lon);
                        l = new LatLng(la,lo);
                        positonList.add(l);
                    }
                }
                map();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
            }
        });
        // Return view
        return view;

    }

    private void map() {
        //Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                for(int i = 0; i< MapFragment.this.positonList.size(); i++) {
                    final int finalI = i;
                    root.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    if (ds.child("station").getValue(String.class).equals(shared.getString("station", "Empty"))) {

                                        String lat = ds.child("latitude").getValue(String.class);
                                        String lon = ds.child("longitude").getValue(String.class);
                                        Double la = Double.parseDouble(lat);
                                        Double lo = Double.parseDouble(lon);
                                        if (la == positonList.get(finalI).latitude) {
                                            if (lo == positonList.get(finalI).longitude) {
                                                String type_of_crime = ds.child("type_of_crime").getValue(String.class);
                                                //Set marker
                                                googleMap.addMarker(new MarkerOptions().position(MapFragment.this.positonList.get(finalI)).title(type_of_crime));

                                                //Animating to zoom the marker
                                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(50));
                                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(MapFragment.this.positonList.get(finalI)));
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {
                        root.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                        if (ds.child("station").getValue(String.class).equals(shared.getString("station", "Empty"))) {
                                            String lat = ds.child("latitude").getValue(String.class);
                                            String lon = ds.child("longitude").getValue(String.class);
                                            Double la = Double.parseDouble(lat);
                                            Double lo = Double.parseDouble(lon);
                                            if (la == marker.getPosition().latitude) {
                                                if (lo == marker.getPosition().longitude) {
                                                    String full_name = ds.child("full_name").getValue(String.class);
                                                    String age = ds.child("age").getValue(String.class);
                                                    String email = ds.child("email").getValue(String.class);
                                                    String phone_no = ds.child("phone_no").getValue(String.class);
                                                    String additional_notes = ds.child("additional_notes").getValue(String.class);
                                                    String videokey = ds.child("videokey").getValue(String.class);


                                                    //dialog
                                                    final AlertDialog builder = new AlertDialog.Builder(getActivity()).create();

                                                    // Get the layout inflater
                                                    LayoutInflater inflater = requireActivity().getLayoutInflater();
                                                    View v = inflater.inflate(R.layout.dialog, null);

                                                    //set Text
                                                    TextView t = (TextView) v.findViewById(R.id.title);
                                                    t.setText(marker.getTitle());
                                                    TextView n = (TextView) v.findViewById(R.id.name);
                                                    n.setText(full_name);
                                                    TextView a = (TextView) v.findViewById(R.id.age);
                                                    a.setText(age);
                                                    TextView e = (TextView) v.findViewById(R.id.email);
                                                    e.setText(email);
                                                    TextView ph = (TextView) v.findViewById(R.id.phone);
                                                    ph.setText(phone_no);
                                                    TextView note = (TextView) v.findViewById(R.id.note);
                                                    note.setText(additional_notes);
                                                    if (!videokey.equals("")) {
                                                        videoref = storageRef.child("stations/"+shared.getString("station","Empty")+"/"+"videos/" + videokey+".mp4");
                                                        videoView = v.findViewById(R.id.videoView);
                                                        videoView.setVisibility(v.VISIBLE);
                                                        videoref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {

                                                                videoView.setVideoURI(uri);
                                                            }
                                                        });


                                                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                            @Override
                                                            public void onPrepared(MediaPlayer mp) {
                                                                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                                                                    @Override
                                                                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                                                        mc = new MediaController(getContext());
                                                                        videoView.setMediaController(mc);
                                                                        mc.setAnchorView(videoView);
                                                                    }
                                                                });
                                                            }
                                                        });
                                                        Log.i("TAG", "onCreate: " + videoView.getDuration());
                                                        videoView.start();
                                                    }


                                                    // Inflate and set the layout for the dialog
                                                    // Pass null as the parent view because its going in the dialog layout
                                                    builder.setView(v);

                                                    final Button b = (Button) v.findViewById(R.id.button2);
                                                    b.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            builder.dismiss();
                                                        }
                                                    });


                                                    builder.show();
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        return false;
                    }
                });
            }
        });
    }


}