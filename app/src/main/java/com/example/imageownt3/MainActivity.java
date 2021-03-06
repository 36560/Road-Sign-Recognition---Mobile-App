package com.example.imageownt3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity
{
    ImageView internetInfo;
    boolean connected;
    private SwitchCompat speechSwitch, textImgSwitch, vibSwitch, speedSwitch, silenceSwitch;
    private static final int  MY_PERMISSIONS_REQUEST_CAMERA = 0;

    static
    {
        if(OpenCVLoader.initDebug())
        {
            Log.d("MainActivity", "Open CV successfully loaded");
        }
        else
        {
            Log.d("MainActivity", "Open CV error");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCamera = findViewById(R.id.btnCamera);
        textImgSwitch = findViewById(R.id.textSwitch);
        speechSwitch = findViewById(R.id.speechSwitch);
        internetInfo = findViewById(R.id.internetInfo);
        vibSwitch = findViewById(R.id.vibSwitch);
        speedSwitch = findViewById(R.id.speedSwitch);
        silenceSwitch = findViewById(R.id.silenceSwitch);

        textImgSwitch.setChecked(true);
        speechSwitch.setChecked(false);
        speedSwitch.setChecked(true);
        vibSwitch.setChecked(false);
        silenceSwitch.setChecked(false);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }

        internetState(internetInfo);

        btnCamera.setOnClickListener(v -> {
                boolean isTextChecked = textImgSwitch.isChecked();
                boolean isSpeechChecked = speechSwitch.isChecked();
                boolean isVibChecked = vibSwitch.isChecked();
                boolean isSpeedChecked = speedSwitch.isChecked();
                boolean isSilenceChecked = silenceSwitch.isChecked();

                if(!isTextChecked && !isSpeechChecked && !isVibChecked)
                {
                    Toast.makeText(MainActivity.this,R.string.selectingOptionToast, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, DetectorActivity.class);
                    intent.putExtra("textImgOption",isTextChecked);
                    intent.putExtra("speechOption",isSpeechChecked);
                    intent.putExtra("vibrationOption", isVibChecked);
                    intent.putExtra("speedOption", isSpeedChecked);
                    intent.putExtra("silenceOption", isSilenceChecked);

                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
        });
    }

    private void internetState(ImageView internetInfo)
    {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                connected = snapshot.getValue(Boolean.class);

                if (!connected)
                {
                    internetInfo.setVisibility(View.VISIBLE);
                    internetInfo.setImageResource(R.drawable.ic_wifi_off);
                }
                else
                    internetInfo.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                System.err.println("Listener was cancelled");
            }
        });
    }
}