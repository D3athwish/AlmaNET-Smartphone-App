package com.learntodroid.postrequestwithjson;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Comment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO: We probably have to optimize the code a little bit...
// TODO: But I'm tired ;_;
public class PostActivity extends AppCompatActivity {

    private static final String TAG = "TAG" ;

    //All of the edit text fields and buttons:
    private EditText idInput;
    private EditText deviceNameInput;
    private EditText longitudeInput;
    private EditText latitudeInput;

    // Handler za autoPOST
    // Not sure if I can move this somewhere else, probably can?
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable()
    {
        @Override
        public void run() {
            Log.d(TAG, "autoPOST was executed!");
            sendPost();
            handler.postDelayed(this, 10000);
        }
    };

    // TODO: Automate GPS fetching

    private PostRepository commentsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        //Klicanje buttona preko id-ja
        Button postButton = (Button) findViewById(R.id.postButton);
        Button getButton = (Button) findViewById(R.id.getButton);
        Switch autoPostSwitch = (Switch) findViewById(R.id.autoPostSwitch);

        // Get field id
        idInput = (EditText) findViewById(R.id.idInputEditText);
        deviceNameInput = (EditText) findViewById(R.id.deviceNameInput);
        longitudeInput = (EditText) findViewById(R.id.longitudeInput);
        latitudeInput = (EditText) findViewById(R.id.latitudeInput);

        // idInput = "2";
        // deviceNameInput = "Gasper Tine";
        // longitudeInput = "13.33337";
        // latitudeInput = "13.33338";

        commentsRepository = PostRepository.getInstance();
        autoPostSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            private static final String TAG = "TAG";

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    handler.postDelayed(runnable, 10000);

                }else{
                    Log.d(TAG, "autoPOST has been stopped!");
                    handler.removeCallbacks(runnable);
                }
            }
        });

        // Create post request when we click on POST
        postButton.setOnClickListener(v -> sendPost());

        // onClick for switching between activites this one goes from POST to GET menu
        getButton.setOnClickListener(v -> openGetActivity());
    }

    private void openGetActivity() {
        Intent intent = new Intent(this, GetActivity.class);
        startActivity(intent);
    }

    private void sendPost(){
        // OBVEZNO moramo najprej iz EditText convertirati v String, ker mi podamo obliko EditText!
        String idSend = idInput.getText().toString();
        String deviceNameSend = deviceNameInput.getText().toString();
        String latitudeSend = latitudeInput.getText().toString();
        String longitudeSend = longitudeInput.getText().toString();

        Post post = new Post(
                idSend,
                deviceNameSend,
                latitudeSend,
                longitudeSend
        );

        //Don't know exactly how to implement POST without a callback right now, so yolo
        commentsRepository.getCommentsService().createComment(post).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(@NotNull Call<Comment> call, @NotNull Response<Comment> r) {
                //Toast.makeText(getApplicationContext(), "Comment " + r.body().getId() + " created", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(@NotNull Call<Comment> call, @NotNull Throwable t) {
                //Toast.makeText(getApplicationContext(), "Error Creating Comment: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}