package chat.client.gui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by desenv on 30/10/13.
 */
@ContentView(R.layout.layout_terms_screen)
public class TermsActivity extends RoboActivity implements View.OnClickListener{

    @InjectView(R.id.useTermsBackBtn) private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == backBtn.getId()){
            finish();
        }
    }
}
