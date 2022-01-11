package com.example.criminalscurse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PhoneListFragment extends Fragment  {

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference("Phone Number");
    private View view;
    private  ListView listData;
    private EditText txtSearch;
    private TextView phone_number,spam,spam2,block;
    private ImageButton dial,report,contact;
    private String ph[] = {""};
    private SharedPreferences sharedPreferences;
    public static final String mypreference = "mypref";
    public static final String SName = "username";
    public DataSnapshot snapshot1 = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Save();
        checkspam();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_phone_list, container, false);

        listData = (ListView) view.findViewById(R.id.listData);
        txtSearch = (EditText) view.findViewById(R.id.txtSearch);
        phone_number = (TextView) view.findViewById(R.id.phonenumber);
        dial = (ImageButton) view.findViewById(R.id.dial);
        contact = (ImageButton) view.findViewById(R.id.addcontact);
        report = (ImageButton) view.findViewById(R.id.report);
        spam = (TextView) view.findViewById(R.id.spam);
        spam2 = (TextView) view.findViewById(R.id.spam2);
        block = (TextView) view.findViewById(R.id.blocking);



        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                snapshot1 = snapshot;
                txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        ArrayList<String> numbers = new ArrayList<String>();
                        if (actionId == 3 || actionId == 0) {
                            int txtltenght = txtSearch.getText().toString().length();

                            if (txtltenght == 8 ) {
                                String firstNumbers = txtSearch.getText().toString().charAt(0) + "" + txtSearch.getText().toString().charAt(1);
                                if (chekFirstNumber(firstNumbers)){
                                    String phone =  txtSearch.getText().toString();
                                    if (checkName(phone) == false) {
                                        if (snapshot1.exists()) {
                                            for (DataSnapshot ds : snapshot1.getChildren()) {
                                                String phone1 = ds.child("phonenumber").getValue(String.class);
                                                numbers.add(phone1);

                                            }
                                            if (checkNew(numbers, phone) == false) {
                                                addNewNumber(phone);
                                            }

                                        } else {
                                            addNewNumber(phone);
                                        }

                                        searchUser(phone);

                                        return true;
                                    } else {
                                        blockNumber();
                                    }
                                }else {
                                    txtSearch.setText("");
                                    Toast.makeText(getContext(), "You Enter the phone number wrong1 "+firstNumbers, Toast.LENGTH_SHORT).show();
                                    hideKeyboard();
                                }
                            }else{
                                txtSearch.setText("");
                                Toast.makeText(getContext(), "You Enter the phone number wrong", Toast.LENGTH_SHORT).show();
                                hideKeyboard();
                            }
                        }

                        return false;
                    }
                });

                reaData(snapshot1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

    //Check first two digits are written right
    private boolean chekFirstNumber(String firstNumbers) {
        if(firstNumbers.equals("03")  ||firstNumbers.equals("70") ||firstNumbers.equals("71") ||firstNumbers.equals("76")  ||firstNumbers.equals("78") ||firstNumbers.equals("79") ||firstNumbers.equals("81")){
            return true;}
        else{
            return false;}
    }

    //check if this user block the number or not
    private boolean checkName(String phone) {
        ArrayList<String> names = new ArrayList<>();
        String name2 = "";
        String name = "";
        for (DataSnapshot ds:snapshot1.getChildren()){
            if(ds.child("phonenumber").getValue(String.class).equals(phone)){
                name = ds.child("blockednames").getValue(String.class);
                break;
            }
        }
        for(int i=0;i<name.length();i++){
            if(name.charAt(i) != ','){
                name2 += name.charAt(i);
            }else{
                names.add(name2);
                name2 = "";
            }
        }
        for (int i=0;i<names.size();i++){
            if(names.get(i).equals(get())){
                return true;
            }
        }
        return false;
    }

    // If the number is spam
    private void spamNumber() {
        listData.setVisibility(View.GONE);
        spam.setVisibility(View.GONE);
        contact.setVisibility(View.GONE);
        report.setVisibility(View.GONE);
        dial.setVisibility(View.GONE);
        phone_number.setVisibility(View.GONE);
        block.setVisibility(View.GONE);
        spam2.setVisibility(View.VISIBLE);
    }
    // If the number is spam
    private void blockNumber() {
        listData.setVisibility(View.GONE);
        spam.setVisibility(View.GONE);
        contact.setVisibility(View.GONE);
        report.setVisibility(View.GONE);
        dial.setVisibility(View.GONE);
        phone_number.setVisibility(View.GONE);
        spam2.setVisibility(View.GONE);
        block.setVisibility(View.VISIBLE);
    }
    public void hideKeyboard(){
        InputMethodManager imm =(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

//Save number to the list

    private void addNewNumber(String phone) {
        User user = new User(phone,0,"False","");
        String UserID = root.push().getKey();
        root.child(UserID).setValue(user);
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot1 = snapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //check if the number in firebase
    private boolean checkNew(ArrayList<String> numbers, String phone){
        for(int i= 0;i<numbers.size();i++){
            if(numbers.get(i).equals(phone)){
                return true;
            }
        }
        return false;

    }



    // Search bar function get the data
    private void searchUser(String phone) {
        Query query = root.orderByChild("phonenumber").equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                if (snapshot.exists()){

                    String phone__number ="";
                    final int[] count_report = {0};
                    String s = null;
                    // Get the data from firebase
                    for (DataSnapshot ds:snapshot.getChildren())
                    {
                        String p = ds.child("phonenumber").getValue(String.class);
                        User user = new User(p,ds.child("count_report").getValue(Integer.class),ds.child("spam").getValue(String.class),ds.child("blockednames").getValue(String.class));
                        phone__number = ""+user.getPhonenumber();
                        count_report[0] = user.getCount_report();
                        s =ds.child("spam").getValue(String.class);
                    }

                    if(s.equals("False")) {
                        phone_number.setText(phone__number);

                        listData.setVisibility(View.GONE);
                        spam.setVisibility(View.GONE);
                        spam2.setVisibility(View.GONE);
                        block.setVisibility(View.GONE);
                        phone_number.setVisibility(View.VISIBLE);
                        dial.setVisibility(View.VISIBLE);
                        report.setVisibility(View.VISIBLE);
                        contact.setVisibility(View.VISIBLE);


                        report.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                count_report[0]++;

                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    root.child(ds.getKey()).child("count_report").setValue(count_report[0]);
                                    String usernames =get()+"," +ds.child("blockednames").getValue(String.class);
                                    root.child(ds.getKey()).child("blockednames").setValue(usernames);
                                    Toast.makeText(view.getContext(), "The number has been reported", Toast.LENGTH_LONG).show();

                                }
                                checkspam();
                                blockNumber();
                            }
                        });
                        dial.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String s = (String) phone_number.getText();
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + s));
                                startActivity(callIntent);

                            }
                        });
                        final String finalPhone__number = phone__number;
                        contact.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                insertContact("", finalPhone__number);
                            }
                        });

                        txtSearch.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                return false;
                            }
                        });
                    }else{
                        spamNumber();
                    }
                }else{
                    Log.d("users", "No data found ");
                }
                hideKeyboard();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    //Add contact
    public void insertContact(String name, String email) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, email);
        startActivity(intent);

    }
    // Display the list of spam numbers
    private void reaData(DataSnapshot snapshot) {
        if(snapshot.exists())
        {
            ArrayList<String> listuser = new ArrayList<>();
            for (DataSnapshot ds:snapshot.getChildren()){
                String spam = ds.child("spam").getValue(String.class);
                if(spam.equals("True")){
                    User user = new User(ds.child("phonenumber").getValue(String.class));
                    listuser.add(""+user.getPhonenumber());
                }
            }
            ArrayAdapter arrayAdapter;
            arrayAdapter = new ArrayAdapter(view.getContext(),android.R.layout.simple_list_item_1,listuser){
                public View getView(int position, View convertView, ViewGroup parent){
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);

                    // Set the text color of TextView (ListView Item)
                    tv.setTextColor(Color.WHITE);
                    tv.setTextSize(25);
                    return view;
                }
            };
            listData.setAdapter(arrayAdapter);
            listData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    spamNumber();
                }
            });
        }else {
            Log.i("Snapshot", "reaData: No Data Available");
        }
    }
    //check if there is a spam
    public void checkspam(){
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren())
                {
                    Integer count = ds.child("count_report").getValue(Integer.class) ;
                    if(count>= 15){
                        root.child(ds.getKey()).child("spam").setValue("True");

                    }else{
                        root.child(ds.getKey()).child("spam").setValue("False");
                    }
                }
                snapshot1 = snapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    //sharedPreferences
    public void Save(){
        String n = "Ahmad";
        sharedPreferences =getContext().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SName, n);
        editor.commit();
    }

    //sharedPreferences
    public String get(){
        String userName ="";

        if (sharedPreferences.contains(SName)){
            userName = sharedPreferences.getString(SName, "");
        }
        return userName;
    }


}

//add and retrieve from database
class User{
    public String phonenumber;
    public int count_report;
    public String blockednames = "";

    public String getSpam() {
        return spam;
    }

    public String spam;

    public int getCount_report() {
        return count_report;
    }





    public User(String phonenumber, int count_report, String spam,String blockednames) {
        this.phonenumber = phonenumber;
        this.spam = spam;
        this.count_report = count_report;
        this.blockednames = blockednames;
    }

    public User (String phonenumber){
        this.phonenumber = phonenumber;
    }

    public String getPhonenumber (){
        return phonenumber;
    }

}
