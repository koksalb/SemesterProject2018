package fr.eurecom.Ready2Meet;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;
import com.takusemba.multisnaprecyclerview.OnSnapListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.eurecom.Ready2Meet.database.Event;
import fr.eurecom.Ready2Meet.database.User;

import fr.eurecom.Ready2Meet.uiExtensions.ImageArrayAdapter;
import fr.eurecom.Ready2Meet.uiExtensions.MultiSelectSpinner;


public class profilepageActivity extends AppCompatActivity {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm a", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Serializable incomingid = getIntent().getSerializableExtra("userid");
        final String useridtoshow = incomingid.toString();

        final TextView displaynameText = (TextView) findViewById(R.id.displaynametext);
        final TextView locationText = (TextView) findViewById(R.id.locationtext);
        final CircleImageView profileimgView = (CircleImageView) findViewById(R.id.profilePictureCircleImageView);
        final TextView descriptionText = (TextView) findViewById(R.id.descriptionText);
        final EditText descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);


        final TextView followerstext = (TextView) findViewById(R.id.followerstext);
        final TextView followingtext = (TextView) findViewById(R.id.followingtext);
        final TextView categoriestext = (TextView) findViewById(R.id.categoriestext);

        final FrameLayout premiumframe = (FrameLayout) findViewById(R.id.premiumFrame);
        final TextView premiumdayslefttext = (TextView) findViewById(R.id.premiumdayslefttext);

        final Button followbutton = (Button) findViewById(R.id.followbutton);
        final Button messagebutton = (Button) findViewById(R.id.messagebutton);
        final Button blockbutton = (Button) findViewById(R.id.blockbutton);

        final TextView participatingeventstext = (TextView) findViewById(R.id.participatingeventstext);
        final TextView oldeventstext = (TextView) findViewById(R.id.oldeventstext);


        final ArrayList<Event> eventlist = new ArrayList<Event>();
        final ArrayList<Event> oldeventlist = new ArrayList<Event>();


        final MultiSnapRecyclerView multiSnapRecyclerView = (MultiSnapRecyclerView) findViewById(R.id.multisnaprecylertest);
        final MultiSnapRecyclerView multiSnapRecyclerView2 = (MultiSnapRecyclerView) findViewById(R.id.oldmultisnaprecylertest);

        final LinearLayout ButtonsLayout = (LinearLayout) findViewById(R.id.ButtonsLayout);

//people cant see profiles of people tho blocked them
        DatabaseReference blocktest = FirebaseDatabase.getInstance().getReference("Users/" + useridtoshow + "/BlockedUsers/").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        blocktest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean result = dataSnapshot.getValue(Boolean.class);
                if(result!=null)
                {
                    Toast.makeText(getApplicationContext(),"This user blocked you :(",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //people cant check profiles of people THEY blocked themselves
        DatabaseReference blocktest2 = FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/BlockedUsers/").child(useridtoshow);
        blocktest2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean result = dataSnapshot.getValue(Boolean.class);
                if(result!=null)
                {
                    Toast.makeText(getApplicationContext(),"You blocked this user!\nUnblock them to see their profile",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + useridtoshow);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final User user = snapshot.getValue(User.class);

                displaynameText.setText(user.DisplayName);
                descriptionText.setText(user.Description);
                descriptionEditText.setText(user.Description);
                locationText.setText("("+user.LastKnownCity+ " / " + user.LastKnownCountry +")");

                Picasso.with(getApplicationContext()).load(user.ProfilePictureURL).fit().into(profileimgView);

            if(user.Followers!=null) {
                    followerstext.setText("Followers (" + user.Followers.size() + ")");
            }
            else
            {
                followerstext.setText("Followers (0)");
            }

                if(user.Following!=null) {
                    followingtext.setText("Following (" + user.Following.size() + ")");
                }
                else
                {
                    followingtext.setText("Following (0)");
                }

                if(user.InterestedCategories!=null) {
                    categoriestext.setText("Categories (" + user.InterestedCategories.size() + ")");
                }
                else
                {
                    categoriestext.setText("Categories (0)");
                }



                Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(format.parse(user.PremiumTill));
            }catch(Exception e){}

                Date premiumtilldate = cal.getTime();

                Date today = Calendar.getInstance().getTime();

                if(today.after(premiumtilldate))
                {
                    premiumframe.setVisibility(View.GONE);
                }
                else
                {
                    premiumframe.setVisibility(View.VISIBLE);
                    premiumdayslefttext.setText(String.valueOf(getDifferenceDays(today,premiumtilldate)));
                }



                if(useridtoshow.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    ButtonsLayout.setVisibility(View.GONE);
                    followbutton.setEnabled(false);
                    blockbutton.setEnabled(false);
                    descriptionText.setVisibility(View.GONE);
                    descriptionEditText.setVisibility(View.VISIBLE);

                }
                else
                {
                    ButtonsLayout.setVisibility(View.VISIBLE);
                    followbutton.setEnabled(true);
                    blockbutton.setEnabled(true);
                    descriptionText.setVisibility(View.VISIBLE);
                    descriptionEditText.setVisibility(View.GONE);
                }




                followerstext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu menu = new PopupMenu(getApplicationContext(), view);
                        if(user.Followers==null){return;}
                        for(String key : user.Followers.keySet()) {
                                menu.getMenu().add(key);
                        }

                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {


                                    default:
                                        Intent intent = new Intent(getApplication(), profilepageActivity.class);
                                        intent.putExtra("userid", item.toString());
                                        startActivity(intent);
                                        return true;
                                             }
                            }
                        });
                        menu.show();

                    }
                });

                followingtext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu menu = new PopupMenu(getApplicationContext(), view);
                        if(user.Following==null){return;}
                        for(String key : user.Following.keySet()) {
                            menu.getMenu().add(key);
                        }

                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {


                                    default:
                                        Intent intent = new Intent(getApplication(), profilepageActivity.class);
                                        intent.putExtra("userid", item.toString());
                                        startActivity(intent);
                                        return true;
                                }
                            }
                        });
                        menu.show();


                    }
                });




                categoriestext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu menu = new PopupMenu(getApplicationContext(), view);

                        if (user.InterestedCategories != null) {
                            for (String key : user.InterestedCategories.keySet()) {
                                menu.getMenu().add(key);
                            }

                            menu.show();

                        }
                    }
                });



                final eventthumbsAdapter adapter = new eventthumbsAdapter(getApplicationContext(), eventlist,useridtoshow);
                final eventthumbsAdapter adapter2 = new eventthumbsAdapter(getApplicationContext(), oldeventlist,useridtoshow);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                LinearLayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

                multiSnapRecyclerView.setLayoutManager(layoutManager);
                multiSnapRecyclerView2.setLayoutManager(layoutManager2);


                multiSnapRecyclerView.setAdapter(adapter);
                multiSnapRecyclerView.setOnSnapListener(new OnSnapListener() {
                    @Override
                    public void snapped(int position) {
                        // do something with the position of the snapped view
                    }
                });

                multiSnapRecyclerView2.setAdapter(adapter2);
                multiSnapRecyclerView2.setOnSnapListener(new OnSnapListener() {
                    @Override
                    public void snapped(int position) {
                        // do something with the position of the snapped view
                    }
                });

                Map<String, Boolean> map3 = new HashMap<>();
                if(user.OldEvents!=null)
                {
                    map3.putAll(user.OldEvents);
                }
                else
                {
                    user.OldEvents = new HashMap<>();
                }
                if(user.ParticipatingEvents!=null)
                {
                    map3.putAll(user.ParticipatingEvents);
                }
                else
                {
                    user.ParticipatingEvents = new HashMap<>();
                }

                if(map3==null){return;}
                for(final String key : map3.keySet()) {
                    FirebaseDatabase.getInstance().getReference().child("Events").child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            Event value = dataSnapshot.getValue(Event.class);
                            Log.d("test", "Value is: " + value.title);

                            Calendar cal = Calendar.getInstance();
                            try {
                                cal.setTime(format.parse(value.endTime));
                            }catch(Exception e){}

                            Date enddateofevent = cal.getTime();

                            Date today = Calendar.getInstance().getTime();

                            //TODO: Move events to old events someway different. this way it only moves when profile is opened.

                            if(today.after(enddateofevent))
                            {
                                if(!oldeventlist.contains(value)){

                                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("OldEvents").child(key).setValue(true);
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ParticipatingEvents").child(key).removeValue();

                                    oldeventlist.add(value);
                                    oldeventstext.setText("Old Events (" + oldeventlist.size() + ")");
                                    Collections.sort(oldeventlist,new EventComparator());
                                    adapter2.notifyDataSetChanged();
                                }
                            }
                            else
                            {
                                if(!eventlist.contains(value)){
                                    eventlist.add(value);
                                    participatingeventstext.setText("Participating Events (" + eventlist.size() + ")");
                                    Collections.sort(eventlist,new EventComparator());
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w("test", "Failed to read value.", error.toException());
                        }
                    });
                }




//end
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: Error handling
            }
        });


        FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Following").child(useridtoshow).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    followbutton.setText("Unfollow");
                }
                else
                {
                    followbutton.setText("Follow");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        followbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String follower = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final String followed = useridtoshow;

                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + follower + "/Following");
                mDatabase.child(followed).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!=null)
                        {
                            mDatabase.child(followed).removeValue();
                            DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference("Users/" + useridtoshow + "/Followers");
                            mDatabase2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                        }
                        else
                        {
                            mDatabase.child(followed).setValue(true);
                            DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference("Users/" + useridtoshow + "/Followers");
                            mDatabase2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });





            }
        });


        FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/BlockedUsers").child(useridtoshow).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    blockbutton.setText("Unblock");
                }
                else
                {
                    blockbutton.setText("Block");
                }

            }






            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        blockbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String blocker = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final String blocked = useridtoshow;

                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + blocker + "/BlockedUsers");
                mDatabase.child(blocked).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!=null)
                        {
                            mDatabase.child(blocked).removeValue();
                        }
                        else
                        {
                            mDatabase.child(blocked).setValue(true);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });


        messagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String otheruser = useridtoshow;
                String thisuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                String channelid = "";
                if(otheruser.compareTo(thisuser)<0)
                {
                    channelid = otheruser + thisuser;
                }
                else
                {
                    channelid = thisuser + otheruser;
                }


                Intent intent = new Intent(getApplication(), ChatActivity2.class);
                intent.putExtra("EventId", channelid);
                intent.putExtra("EventTitle", displaynameText.getText().toString());
                startActivity(intent);





            }
        });








        descriptionEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                String newdescription = s.toString();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + useridtoshow);
                mDatabase.child("Description").setValue(newdescription);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }


    public class EventComparator implements Comparator<Event> {
        @Override
        public int compare(Event o1, Event o2) {

            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(format.parse(o1.startTime));
            }catch(Exception e){}

            Date eventonedate = cal.getTime();

            try {
                cal.setTime(format.parse(o2.startTime));
            }catch(Exception e){}


            Date eventtwodate = cal.getTime();

            if(eventonedate.after(eventtwodate))
            {
                return -1;
            }
            else if(eventonedate.before(eventtwodate))
            {
                return 1;
            }
            else
            {
                return 0;
            }


        }
    }





}
