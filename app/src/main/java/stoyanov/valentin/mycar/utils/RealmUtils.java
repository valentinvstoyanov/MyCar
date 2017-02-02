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

        RealmList<Service> services = vehicle.getServices();
        for (Service service : services) {
            deleteProperty(service, DeleteType.SERVICE);
        }
        services.deleteAllFromRealm();

        RealmList<Insurance> insurances = vehicle.getInsurances();
        for (Insurance insurance : insurances) {
            deleteProperty(insurance, DeleteType.INSURANCE);
        }
        insurances.deleteAllFromRealm();

        RealmList<Refueling> refuelings = vehicle.getRefuelings();
        for (Refueling refueling : refuelings) {
            deleteProperty(refueling, DeleteType.REFUELING);
        }
        refuelings.deleteAllFromRealm();

        RealmList<Expense> expenses = vehicle.getExpenses();
        for (Expense expense : expenses) {
            deleteProperty(expense, DeleteType.EXPENSE);
        }
        expenses.deleteAllFromRealm();

        vehicle.getFuelTanks().deleteAllFromRealm();
    }

    public enum DeleteType {
        SERVICE, INSURANCE, REFUELING, EXPENSE
    }
}
