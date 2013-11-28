package chat.client.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by desenv on 23/10/13.
 */
@ContentView(R.layout.layout_splash_screen)
public class SplashActivity extends RoboActivity implements View.OnClickListener{

    @InjectView(R.id.agreeBtn) private Button agreeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        agreeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (agreeBtn.getId() == view.getId()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
