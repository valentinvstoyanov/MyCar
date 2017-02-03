package stoyanov.valentin.mycar.utils;

import io.realm.RealmList;
import io.realm.RealmModel;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.Vehicle;

public class RealmUtils {

    public static void deleteProperty(RealmModel model, DeleteType type) {
        switch (type) {
            case SERVICE:
                Service service = (Service) model;
                service.getNote().deleteFromRealm();
                service.getNotification().deleteFromRealm();
                service.getAction().deleteFromRealm();
                //Stop ALARM IF NEEDED
                service.deleteFromRealm();
                break;
            case INSURANCE:
                Insurance insurance = (Insurance) model;
                insurance.getNote().deleteFromRealm();
                insurance.getNotification().deleteFromRealm();
                insurance.getAction().deleteFromRealm();
                //STOP ALARM
                insurance.deleteFromRealm();
                break;
            case REFUELING:
                Refueling refueling = (Refueling) model;
                refueling.getNote().deleteFromRealm();
                refueling.getAction().deleteFromRealm();
                refueling.deleteFromRealm();
                break;
            case EXPENSE:
                Expense expense = (Expense) model;
                expense.getNote().deleteFromRealm();
                expense.getAction().deleteFromRealm();
                expense.deleteFromRealm();
                break;
        }
    }

    public static void deleteVehicle(Vehicle vehicle) {
        vehicle.getNote().deleteFromRealm();

        //Reverse iteration due to an error
        RealmList<Service> services = vehicle.getServices();
        for (int i = services.size() - 1; i >= 0; i--) {
            deleteProperty(services.get(i), DeleteType.SERVICE);
        }
        services.deleteAllFromRealm();

        RealmList<Insurance> insurances = vehicle.getInsurances();
        for (int i = insurances.size() - 1; i >= 0; i--) {
            deleteProperty(insurances.get(i), DeleteType.INSURANCE);
        }
        insurances.deleteAllFromRealm();

        RealmList<Refueling> refuelings = vehicle.getRefuelings();
        for (int i = refuelings.size() - 1; i >= 0; i--) {
            deleteProperty(refuelings.get(i), DeleteType.REFUELING);
        }
        refuelings.deleteAllFromRealm();

        RealmList<Expense> expenses = vehicle.getExpenses();
        for (int i = expenses.size() - 1; i >= 0; i--) {
            deleteProperty(expenses.get(i), DeleteType.EXPENSE);
        }
        expenses.deleteAllFromRealm();

        vehicle.getFuelTanks().deleteAllFromRealm();
        vehicle.deleteFromRealm();
    }

    public enum DeleteType {
        SERVICE, INSURANCE, REFUELING, EXPENSE
    }
}
