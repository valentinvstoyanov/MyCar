package stoyanov.valentin.mycar.adapters;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import stoyanov.valentin.mycar.ActivityType;
import stoyanov.valentin.mycar.realm.models.RealmSettings;
import stoyanov.valentin.mycar.utils.RealmUtils;

public abstract class BaseRealmAdapter<T extends RealmModel, VH extends RealmViewHolder>
        extends RealmBasedRecyclerViewAdapter<T, VH> {

    private int color;
    private String vehicleId;
    private RealmSettings realmSettings;
    private ActivityType deleteType;


    public BaseRealmAdapter(Context context, RealmResults<T> realmResults, boolean automaticUpdate,
                            boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
    }

    @Override
    public void onItemSwipedDismiss(final int position) {
        Realm myRealm = Realm.getDefaultInstance();
        myRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                T object = realmResults.get(position);
                RealmUtils.deleteProperty(object, deleteType);
            }
        });
        myRealm.close();
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setRealmSettings(RealmSettings realmSettings) {
        this.realmSettings = realmSettings;
    }

    public RealmSettings getRealmSettings() {
        return realmSettings;
    }

    public void setDeleteType(ActivityType deleteType) {
        this.deleteType = deleteType;
    }
}
