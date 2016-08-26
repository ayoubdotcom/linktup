package victor.forum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity{

    private DatabaseReference dataRef;
    private List<User> usersList;
    private EditText emailEditText;
    private EditText passwordEditText;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       // FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);

    }



    public void ouvrirRegisterClick(View view)
    {
        Intent registerStart = new Intent(getBaseContext(),RegisterActivity.class);
        startActivity(registerStart);
    }



    public void loginClick(View view)
    {
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = null;
                boolean valide = false;

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    if(postSnapshot.getValue(User.class).getEmail().equalsIgnoreCase(emailEditText.getText().toString()))
                    {
                        valide = true;
                        user = postSnapshot.getValue(User.class);
                    }
                }

                if(valide){
                    if(user.getPassword().contentEquals(passwordEditText.getText().toString()))
                    {
                        Intent memberStart = new Intent(getBaseContext(), MemberSpaceActivity.class);
                        memberStart.putExtra("name","Ayoub");
                        startActivity(memberStart);

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("username", emailEditText.getText().toString());
                        editor.commit();

                        finish();
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "password incorrect.", Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(getBaseContext(), "Email n'existe pas.", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //Toast.makeText(getBaseContext(), "le nombre d'enregistrement: " + usersList.size(), Toast.LENGTH_LONG).show();

        //Intent memberStart = new Intent(getBaseContext(), MemberActivity.class);
        //startActivity(memberStart);
    }

    @Override
    protected void onStart() {
        super.onStart();

        dataRef = FirebaseDatabase.getInstance().getReference("users");

        emailEditText = (EditText)findViewById(R.id.emailEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, getApplicationContext().MODE_PRIVATE);

        Log.e("auto log",sharedpreferences.getString("username", ""));

        if(!sharedpreferences.getString("username", "").isEmpty())
        {
            Intent memberStart = new Intent(getBaseContext(), MemberSpaceActivity.class);
            memberStart.putExtra("name","Ayoub");
            startActivity(memberStart);
        }
    }
}
