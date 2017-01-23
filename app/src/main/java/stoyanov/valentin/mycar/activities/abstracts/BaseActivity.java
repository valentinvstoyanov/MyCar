package stoyanov.valentin.mycar.activities.abstracts;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import stoyanov.valentin.mycar.activities.interfaces.IBaseActivity;

public abstract class BaseActivity extends AppCompatActivity
                    implements IBaseActivity{

    protected void setToolbarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
