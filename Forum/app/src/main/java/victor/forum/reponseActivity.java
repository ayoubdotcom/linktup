package victor.forum;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class reponseActivity extends AppCompatActivity {

    private DatabaseReference dataRef;
    private EditText replyEditText;
    private String key;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reponse);
        dataRef = FirebaseDatabase.getInstance().getReference("messages");

        key = getIntent().getExtras().getString("key");
        replyEditText = (EditText)findViewById(R.id.replyEditText);
        listView = (ListView)findViewById(R.id.listView);


        dataRef.child(key).child("reponse").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> reponsesList = new ArrayList<String>();


                for(DataSnapshot reponse:dataSnapshot.getChildren())
                {
                    reponsesList.add(reponse.getValue().toString());
                }
                Log.e("nb reponses",reponsesList.size()+"");
                Collections.reverse(reponsesList);
                ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listreponses,R.id.reponsesTextView, reponsesList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void replyClick(View view)
    {
        String reply = replyEditText.getText().toString();
        dataRef.child(key).child("reponse").push().setValue(reply);
    }
}
