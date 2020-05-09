package com.piyush.carbrightness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity implements Application.ActivityLifecycleCallbacks {
    private String FILENAME = "last_brightness.txt";
    private String TAG = "MainHandler";

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerActivityLifecycleCallbacks(this);
        setContentView(R.layout.activity_main);
    }

    public void ButtonClickHandler(View view) {
        BrightnessHandler();
    }

    public void BrightnessHandler() {
        if (!Settings.System.canWrite(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }

        int brightness_auto = Settings.System.getInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);

        if (brightness_auto == 0) {
            int brightness = Settings.System.getInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
            SaveBrightness(brightness);
            Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 1);
            Toast.makeText(this, "Brightness set to auto", Toast.LENGTH_SHORT).show();
        }
        else {
            int brightness = GetBrightness();
            Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
            Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            Toast.makeText(this, "Brightness restored", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void SaveBrightness(int brightness) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.openFileOutput(FILENAME, MODE_PRIVATE)))) {
            writer.write(Integer.toString(brightness));
        }
        catch (FileNotFoundException e) {
            Toast.makeText(this, "File doesn't exist", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    private int GetBrightness() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.openFileInput(FILENAME)))) {
            String text = reader.readLine();
            return Integer.parseInt(text);
        }
        catch (FileNotFoundException e) {
            Toast.makeText(this, "File doesn't exist", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        return 512;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        BrightnessHandler();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}