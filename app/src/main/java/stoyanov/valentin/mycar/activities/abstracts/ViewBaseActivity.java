package stoyanov.valentin.mycar.activities.abstracts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import java.math.BigDecimal;
import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public abstract class ViewBaseActivity extends BaseActivity{

    private ViewActivityType activityType;
    private String id;
    private TextView tvType, tvDate, tvNotification, tvPrice, tvOdometer, tvNotes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();
        setComponentListeners();
        setContent();
    }

    @Override
    public void initComponents() {
        /*tvType = findViewById(R.id.tv_view_type);
        tvDate = findViewById(R.id.tv_view_date);
        tvNotification = findViewById(R.id.tv_view_notification);
        tvPrice = findViewById(R.id.tv_view_price);
        tvOdometer = findViewById(R.id.tv_view_odometer);
        tvNotes = findViewById(R.id.tv_view_notes);*/
    }

    @Override
    public void setComponentListeners() {
    //fab
    }

    @Override
    public void setContent() {
        Realm myRealm = Realm.getDefaultInstance();
        switch (activityType) {
            case INSURANCE:
                Insurance insurance = myRealm.where(Insurance.class)
                        .equalTo(RealmTable.ID, id).findFirst();
                tvType.setText(insurance.getCompany().getName());
                tvOdometer.setText(String.valueOf(insurance.getAction().getOdometer()));
                tvDate.setText(DateUtils.datetimeToString(insurance.getAction().getDate()));
                tvNotification.setText(DateUtils.datetimeToString(insurance.getExpirationDate()));
                tvPrice.setText(MoneyUtils.longToString(new BigDecimal(insurance.getAction().getPrice())));
                tvNotes.setText(insurance.getNote().getContent());
                break;
            case EXPENSE:
                Expense expense = myRealm.where(Expense.class)
                        .equalTo(RealmTable.ID, id).findFirst();
                tvType.setText(expense.getType().getName());
                tvOdometer.setText(String.valueOf(expense.getAction().getOdometer()));
                tvDate.setText(DateUtils.datetimeToString(expense.getAction().getDate()));
                tvPrice.setText(MoneyUtils.longToString(new BigDecimal(expense.getAction().getPrice())));
                tvNotes.setText(expense.getNote().getContent());
                break;
            case REFUELING:
                Refueling refueling = myRealm.where(Refueling.class)
                        .equalTo(RealmTable.ID, id).findFirst();
                FuelTank fuelTank = myRealm.where(FuelTank.class)
                        .equalTo(RealmTable.ID, refueling.getFuelTankId()).findFirst();
                tvType.setText(fuelTank.getFuelType().getName());
                tvOdometer.setText(String.valueOf(refueling.getAction().getOdometer()));
                tvDate.setText(DateUtils.datetimeToString(refueling.getAction().getDate()));
                //other tvs
                tvPrice.setText(MoneyUtils.longToString(new BigDecimal(refueling.getAction().getPrice())));
                tvNotes.setText(refueling.getNote().getContent());
                break;
            default:
                Service service = myRealm.where(Service.class)
                        .equalTo(RealmTable.ID, id).findFirst();
                tvType.setText(service.getType().getName());
                tvOdometer.setText(String.valueOf(service.getAction().getOdometer()));
                tvDate.setText(DateUtils.datetimeToString(service.getAction().getDate()));
                tvPrice.setText(MoneyUtils.longToString(new BigDecimal(service.getAction().getPrice())));
                tvNotes.setText(service.getNote().getContent());
                break;
        }
        myRealm.close();
    }

    public enum ViewActivityType {
        INSURANCE, EXPENSE, REFUELING, SERVICE
    }
}
