package stoyanov.valentin.mycar.activities.interfaces;

public interface INewBaseActivity extends IBaseActivity {
    boolean isInputValid();
    void saveToRealm();
}
