package stoyanov.valentin.mycar.activities;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;
import stoyanov.valentin.mycar.R;

public abstract class BaseActivity extends AppCompatActivity {
    abstract protected void initComponents();
    abstract protected void setComponentListeners();

    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
