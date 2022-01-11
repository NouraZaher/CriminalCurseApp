package com.example.criminalscurse;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {
  //  Button get_location;
    TextView tv_latitude, tv_longitude;
    FusedLocationProviderClient client;
    public String sel_item;
    EditText phone_text;
    public View v ;
    public Button reportButton;
    public EditText emaile;
    public EditText namee;
    public EditText agee;
    EditText ane;
    String username;
    String longitude,latitude;
   // Button image_btn;
   // ImageView imageView;   //public static final int CAMERA_ACTION_CODE = 1;
    Button video_btn;
    VideoView vv ;
    static final int REQUEST_VIDEO_CAPTURE = 2;

    public FirebaseStorage storage;
    public StorageReference storageReference;

    public Uri imageUri;
    public Uri videoUri;
    public SharedPreferences sharedPreferences;

    public static final String mypreference = "mypref";
    public static final String SName = "username";

    public static  String key;
public Timer t;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();




// Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_main, container, false);
        tv_latitude = v.findViewById(R.id.tv_latitude);
        tv_longitude = v.findViewById(R.id.tv_longitude);
//get_location = (Button) v.findViewById(R.id.getlocation);
        //username = "";
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        t=new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                getCurrentLocation();
            }
        },0,10);

/*
        imageView = v.findViewById(R.id.image_view);
        image_btn = v.findViewById(R.id.btn_image);
        */

        video_btn = v.findViewById(R.id.bt_video);
        vv = v.findViewById(R.id.videoView);
        video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
                else{
                    Toast.makeText(getActivity(),"No app that support this action",Toast.LENGTH_LONG).show();
                }
            }
        });

/*        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                //  if(intent.resolveActivity(getActivity().getPackageManager())!=null){
                if(intent.resolveActivity(getContext().getPackageManager())!=null){
                    startActivityForResult(intent,CAMERA_ACTION_CODE);
                }else{
                    Toast.makeText(getActivity(),"No app that support this action",Toast.LENGTH_LONG).show();
                }
            }
        });
*/
        final Spinner spinner = (Spinner) v.findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);

                if(!selectedItem.equals("Choose Category"))
                {
                    spinner.setBackgroundResource(R.drawable.edit_text);
                    // ((TextView) parent.getChildAt(0)).setBackgroundColor(Color.BLACK);
                }
                sel_item = selectedItem;
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        phone_text = v.findViewById(R.id.phone_edit_text);
        phone_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (phone_text.getText().toString().length() > 8) {
                    Toast.makeText(getActivity(),"Phone number cannot be more than 8 characters",Toast.LENGTH_SHORT).show();
                    phone_text.setText("");
                    return;
                }
            }
        });
        namee = (EditText) v.findViewById(R.id.editname);
        agee = (EditText)v.findViewById(R.id.ed_age);
        emaile =(EditText) v.findViewById(R.id.ed_email);
        ane = (EditText)v.findViewById(R.id.an);


        reportButton = v.findViewById(R.id.reportbtn);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = namee.getText().toString();
                String age = agee.getText().toString();
                String email = emaile.getText().toString();
                String phone = phone_text.getText().toString();
                String additional_information = ane.getText().toString();


                if (videoUri!=null && !name.isEmpty() && !phone.isEmpty() && !email.isEmpty()
                        && !age.isEmpty() && !sel_item.equals("Choose Category")){

                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle("Please Wait....");
                    progressDialog.show();
                    //StorageReference reference = storageReference.child("videos/" + UUID.randomUUID().toString());
                    key =  UUID.randomUUID().toString();
                    StorageReference reference = storageReference.child("stations/");


                    try {
                        SharedPreferences shared=PreferenceManager.getDefaultSharedPreferences(getActivity());
                        reference.child(shared.getString("station","empty")+"/").child("videos/").child(key+".mp4").putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Video successfully uploaded", Toast.LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error occured in video Uploading" + e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                progressDialog.setMessage("Saved" + (int) progress + "%");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "The name cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (phone.isEmpty()) {
                    Toast.makeText(getActivity(), "The phone number cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (email.isEmpty()) {
                    Toast.makeText(getActivity(), "The email cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (age.isEmpty()) {
                    Toast.makeText(getActivity(), "The age cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (sel_item.equals("Choose Category")) {
                    Toast.makeText(getActivity(), "You have to choose catergory", Toast.LENGTH_LONG).show();
                    return;
                }

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference root = database.getReference().child("Reports");

                longitude = tv_longitude.getText().toString();
                latitude = tv_latitude.getText().toString();
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    //getCurrentLocation();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                    Toast.makeText(getActivity(),
                            "request to access location ", Toast.LENGTH_SHORT).show();
                }

                SharedPreferences shared=PreferenceManager.getDefaultSharedPreferences(getActivity());
                    username =shared.getString("username", "Empty");
                    Log.i("Username", "onClick: "+username);
                root.push().setValue(new ReportCase(name,username,age,email,phone,sel_item,longitude,latitude,additional_information,key,shared.getString("station","Empty")));

/*
                if (imageUri==null){
                    Toast.makeText(getActivity(), "Image is null", Toast.LENGTH_LONG).show();
                }
*/
/*
                if (imageUri != null) {
                    uploadMethod();
                }
                */

                Toast.makeText(getActivity(),"Your information have been added to the firebase",Toast.LENGTH_LONG).show();
                namee.setText("");
                phone_text.setText("");
                agee.setText("");
                emaile.setText("");
                agee.setText("");
                ane.setText("");
                tv_longitude.setText("");
                tv_latitude.setText("");
                getCurrentLocation();

            }  });

        return v;
    }
/*
    private void uploadMethod() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait....");
        progressDialog.show();
        //StorageReference reference = storageReference.child("videos/" + UUID.randomUUID().toString());
        StorageReference reference = storageReference.child("images/" + "hi.jpg"); //substitute here name by username when
        // finishing because the username should be the key that links the storage files with the real-time database

        try {
            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Images successfully uploaded", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Error occured in Image Uploading" + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Saved" + (int) progress + "%");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      /* super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAMERA_ACTION_CODE && resultCode==RESULT_OK){
            Bundle bundle = data.getExtras();
            Bitmap final_photo =(Bitmap) bundle.get("data");
            imageView.setImageBitmap(final_photo);
            imageUri = Uri.parse(data.getExtras().toString()) ;
            Log.i("uri: ", "onActivityResult: "+imageUri);

        }
*/
         if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUri = data.getData();

            vv.setVideoURI(videoUri);
            vv.start();
            vv.canPause();
        }
    }
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && (grantResults.length > 0) &&
                (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            Toast.makeText(getActivity(),
                    "permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if ((ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission
                    (getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        tv_latitude.setText(String.valueOf(location.getLatitude()));
                        tv_longitude.setText(String.valueOf(location.getLongitude()));
                    } else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                tv_latitude.setText(String.valueOf(location1.getLatitude()));
                                tv_longitude.setText(String.valueOf(location1.getLongitude()));

                            }
                        };
                        if (ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission
                                        (getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;

                        }
                        client.requestLocationUpdates(locationRequest
                                , locationCallback, Looper.myLooper());
                    }
                }
            });
        }else{
            //when location service is not enabled
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        t.cancel();
        t.purge();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        t.cancel();
        t.purge();
    }
}
class ReportCase {

    public String full_name,username,age,email,phone_no,type_of_crime,longitude,latitude,additional_notes,videokey,station;

    public ReportCase(String name,String username, String age,String email,String phone_number,
                      String type_of_crime,String longitude,String latitude,String additional_notes,String videokey,String station) {
        this.full_name = name;
        this.username = username;
        this.age = age;
        this.email = email;
        this.phone_no = phone_number;
        this.type_of_crime = type_of_crime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.additional_notes = additional_notes;
        this.videokey = videokey;
        this.station=station;

    }

}