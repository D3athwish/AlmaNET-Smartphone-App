package com.learntodroid.postrequestwithjson;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Comment;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity {

    //All of the edit text fields and buttons:
    private EditText idInput;
    private EditText deviceNameInput;
    private EditText longitudeInput;
    private EditText latitudeInput;
    private Button postButton;
    private Button getButton;

    private PostRepository commentsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        //Klicanje buttona preko id-ja
        postButton = (Button) findViewById(R.id.postButton);
        getButton= (Button) findViewById(R.id.getButton);

        // Get field id
        idInput = (EditText) findViewById(R.id.idInputEditText);
        deviceNameInput = (EditText) findViewById(R.id.deviceNameInput);
        longitudeInput = (EditText) findViewById(R.id.longitudeInput);
        latitudeInput = (EditText) findViewById(R.id.latitudeInput);

//        idInput = "2";
//        deviceNameInput = "Gasper Tine";
//        longitudeInput = "13.33337";
//        latitudeInput = "13.33338";

        commentsRepository = commentsRepository.getInstance();

        // When we click on the post button the following happens:
        // Get input from user
        // Create new object with user input
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // OBVEZNO moramo najprej iz EditText convertirati v String, ker mi podamo obliko EditText!
                String idSend = idInput.getText().toString();
                String deviceNameSend = deviceNameInput.getText().toString();
                String latitudeSend = longitudeInput.getText().toString();
                String longitudeSend = latitudeInput.getText().toString();

                Post post = new Post(
                        idSend,
                        deviceNameSend,
                        latitudeSend,
                        longitudeSend
                );


                //Don't know exactly how to implement POST without a callback right now, so yolo
                commentsRepository.getCommentsService().createComment(post).enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> r) {
                        //Toast.makeText(getApplicationContext(), "Comment " + r.body().getId() + " created", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {
                        //Toast.makeText(getApplicationContext(), "Error Creating Comment: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        getButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                openGetActivity();
            }
        });
    }

    private void openGetActivity() {
        Intent intent = new Intent(this, GetActivity.class);
        startActivity(intent);
    }
}
