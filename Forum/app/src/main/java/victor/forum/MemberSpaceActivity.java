package victor.forum;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookDialog;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MemberSpaceActivity extends AppCompatActivity{

    private Context context;
    private List<Message> messageList = new ArrayList<>();
    private List<String> replies = new ArrayList<>();
    private List<String> keysList = new ArrayList<>();
    private DatabaseReference dataRef;
    private messageReponseAdapter adapter;
    private RecyclerView messagesRecyclerView;
    private TextView replyTextView;
    private TextView shareTextView;
    private TextView messageTextView;
    private Button addMessageButton;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private Button button2;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference islandRef;
    File localFile;
    private Bitmap bitmap;
    private RecyclerView recycler;
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_space);

        context = getApplicationContext();

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        toolbar.setLogo(R.drawable.lambheadonly32);
        setSupportActionBar(toolbar);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, getApplicationContext().MODE_PRIVATE);


        /*messagesRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.e("message","on Item Click Listener");

                final int myPosition = position;
                messageTextView = (TextView)view.findViewById(R.id.messageTextView);
                messageTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String messageString = messageTextView.getText().toString();
                        Toast.makeText(getApplicationContext(), messageString, Toast.LENGTH_LONG).show();
                    }
                });

                replyTextView = (TextView)view.findViewById(R.id.replyTextView);
                replyTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v ) {
                        String messageString = replyTextView.getText().toString();
                        String key = keysList.get(myPosition);
                        //Toast.makeText(getApplicationContext(), key, Toast.LENGTH_LONG).show();
                        Intent replyStart = new Intent(getApplicationContext(),reponseActivity.class);
                        replyStart.putExtra("key",key);
                        startActivity(replyStart);
                    }
                });

                shareTextView = (TextView)view.findViewById(R.id.shareTextView);
                shareTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = messageTextView.getText().toString();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle(null)
                                    .setContentDescription(
                                            "The 'Hello Facebook' sample  showcases simple Facebook integration")
                                    .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                                    .build();

                            shareDialog.show(linkContent);
                        }
                    }
                });
            }
        });*/

    }


    @Override
    protected void onStart() {
        super.onStart();

        messagesRecyclerView = (RecyclerView)findViewById(R.id.recycler);


        Log.e("essaye","On Create");

        recycler = (RecyclerView)findViewById(R.id.recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(llm);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://linktup-f2f87.appspot.com");

        //facebook
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        //String name = getIntent().getExtras().getString("name");
        //Toast.makeText(this, name, Toast.LENGTH_LONG).show();

        dataRef = FirebaseDatabase.getInstance().getReference("messages");

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("message","on data change");
                messageList.clear();
                keysList.clear();
                Message mesage;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    mesage = postSnapshot.getValue(Message.class);
                    mesage.setKey(postSnapshot.getKey());
                    mesage.setNbReponses((int)postSnapshot.child("reponse").getChildrenCount());
                    messageList.add(mesage);
                }
                if(!messageList.isEmpty())
                {
                    adapter = new messageReponseAdapter(getApplicationContext(), messageList, shareDialog);
                    recycler.setAdapter(adapter);
                    addMessageButton = (Button)findViewById(R.id.addMessageButton);
                    addMessageButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void ouvrirMsgBoxClick(View view)
    {
        Intent startMessage = new Intent(this, MessageActivity.class);
        startMessage.putExtra("name",getIntent().getStringExtra("name"));
        startActivity(startMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.member_menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent startMessage = new Intent(this, MessageActivity.class);
            startActivity(startMessage);
            return true;
        }

        if (id == R.id.notification) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.lambheadonly48)
                            .setContentTitle("Tell in Box")
                            .setContentText("You Get a New Message")
                            .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                            .setSound(alarmSound);

            int mNotificationId = 001;

            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            mNotifyMgr.notify(mNotificationId, mBuilder.build());
            return true;
        }

        if (id == R.id.action_profile) {
            Intent startProfile = new Intent(this, ProfileActivity.class);
            startActivity(startProfile);
            return true;
        }

        if (id == R.id.logout) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear(); //clear all stored data
            editor.commit();
            Intent startLogin = new Intent(this, LoginActivity.class);
            startActivity(startLogin);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);
    }
}
