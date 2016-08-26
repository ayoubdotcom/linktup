package victor.forum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    private TextView emailView;
    private TextView usernameView;
    private TextView passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        emailView = (TextView)findViewById(R.id.email);
        usernameView = (TextView)findViewById(R.id.username);
        passwordView = (TextView)findViewById(R.id.password);
    }

    public void registerClick(View view)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataRef = database.getReference("users");

        String email = emailView.getText().toString();
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        User user = new User(username,email, password);

        dataRef.push().setValue(user);

        Toast.makeText(getBaseContext(), "Tu es bien enregistré", Toast.LENGTH_LONG).show();

        Intent memberStart = new Intent(this, MemberActivity.class);
        startActivity(memberStart);
    }
}
