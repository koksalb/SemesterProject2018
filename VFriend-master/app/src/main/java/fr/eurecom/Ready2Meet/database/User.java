package fr.eurecom.Ready2Meet.database;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class User {
    public String DisplayName;
    public Map<String, Boolean> ParticipatingEvents;
    public Map<String, Boolean> InvitedEvents;
    public Map<String, Boolean> OldEvents;
    public String ProfilePictureURL;

    public String PremiumTill;

    public Map<String, Boolean> Followers;
    public Map<String, Boolean> Following;
    public Map<String, Boolean> InterestedCategories;

    public Map<String, Boolean> BlockedUsers;

    public String Description;

    public Double LastKnownLatitude;
    public Double LastKnownLongitude;

    public String LastKnownCity;
    public String LastKnownCountry;

    public User() {
    }

    public User(String displayName, Map<String, Boolean> participatingEvents, String
            profilePictureUrl) {
        this.DisplayName = displayName;
        this.ParticipatingEvents = participatingEvents;
        this.ProfilePictureURL = profilePictureUrl;

        this.PremiumTill = "2010-10-10 at 10:00 AM";
        this.Description = "Hello, I am " + displayName;

        this.LastKnownLatitude = 0.0;
        this.LastKnownLongitude=0.0;

        this.LastKnownCity="Istanbul";
        this.LastKnownCountry="Turkey";
    }





}
