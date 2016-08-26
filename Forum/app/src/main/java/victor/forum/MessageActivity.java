package victor.forum;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;


public class MessageActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private EditText editText;
    private String message;
    private ImageView messageImageView;
    private Button galleryButton;
    private static int RESULT_LOAD_IMAGE = 2;
    private String imgDecodableString;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private Location mCurrentLocation;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_message);

        if(mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
        }

        messageImageView = (ImageView)findViewById(R.id.messageImageView);
        galleryButton = (Button)findViewById(R.id.gallerieButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        editText = (EditText)findViewById(R.id.editText);

        Log.e("inside","onCreate");
    }


    public void ajouterMessageClick(View view)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://linktup-f2f87.appspot.com");
        Date date = new Date();
        String mediaUrl =  date.toString() + getIntent().getStringExtra("name") + "image.jpg";
        message = editText.getText().toString();



        // Get the data from an ImageView as bytes
        BitmapDrawable drawable = (BitmapDrawable)messageImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.child(mediaUrl).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                if(message.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "le message est vide.",Toast.LENGTH_LONG).show();
                }
                else
                {
                    String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference dataRef = database.getReference("messages");
                    Message msg;
                    Log.e("Download url", downloadUrl);
                    if(mCurrentLocation != null)
                    {
                        msg = new Message("ayoub", editText.getText().toString(),downloadUrl, mCurrentLocation.getLongitude(), mCurrentLocation.getLatitude());
                        dataRef.push().setValue(msg);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Location is enabled", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        i++;
    }


    /*
    private void setMessageLocation(Location location, String message)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataRef = database.getReference("users");
        Message msg;
        msg = new Message(message,location.getLongitude(),location.getLatitude());
        dataRef.push().setValue(msg);
    }*/



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // if taking
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && null != data) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // my ImageView
            ImageView myPhotoImage = (ImageView) findViewById(R.id.messageImageView);
            myPhotoImage.setImageBitmap(imageBitmap);

            // if choosing
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // my ImageView
            ImageView myPhotoImage = (ImageView) findViewById(R.id.messageImageView);
            myPhotoImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Log.e("inside","onStart");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("inside","onConnected1");
        try{
            // Get last known recent location.
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            // Note that this can be NULL if last location isn't already known.
            if (mCurrentLocation != null) {
                // Print current location if not null
                Log.d("DEBUG", "current location: " + mCurrentLocation.toString());
                LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            }
            // Begin polling for new location updates.
            startLocationUpdates();
        }
        catch(SecurityException ex)
        {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        Log.e("inside","onConnected2");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("inside","onConnectionSuspended2");
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
        Log.e("inside","onConnectionSuspended1");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Location","location failed");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        Log.e("inside","onStop");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("inside","onLocationChanged1");
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.e("inside","onLocationChanged2");
    }

    protected void startLocationUpdates() {

        try{
            // Create the location request
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(UPDATE_INTERVAL)
                    .setFastestInterval(FASTEST_INTERVAL);
            // Request location updates
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch(SecurityException ex)
        {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void takePictureClick(View v)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }



}
