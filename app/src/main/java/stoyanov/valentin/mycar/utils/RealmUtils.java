package stoyanov.valentin.mycar.utils;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import stoyanov.valentin.mycar.ActivityType;
import stoyanov.valentin.mycar.realm.models.Brand;
import stoyanov.valentin.mycar.realm.models.Color;
import stoyanov.valentin.mycar.realm.models.Company;
import stoyanov.valentin.mycar.realm.models.DateNotification;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Model;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.ServiceType;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;

public class RealmUtils {

    public static void deleteProperty(RealmModel model, ActivityType type) {
        switch (type) {
            case SERVICE:
                Service service = (Service) model;
               // service.getNote().deleteFromRealm();
                DateNotification dateNotification = service.getDateNotification();
                if (dateNotification != null) {
                    dateNotification.deleteFromRealm();
                }
                //service.getAction().deleteFromRealm();
                //Stop ALARM IF NEEDED
                service.deleteFromRealm();
                break;
            case INSURANCE:
                Insurance insurance = (Insurance) model;
                //insurance.getNote().deleteFromRealm();
                insurance.getNotification().deleteFromRealm();
                //insurance.getAction().deleteFromRealm();
                //STOP ALARM
                insurance.deleteFromRealm();
                break;
            default:
                ((RealmObject)model).deleteFromRealm();
                break;
           /* case REFUELING:
                Refueling refueling = (Refueling) model;
               // refueling.getNote().deleteFromRealm();
               // refueling.getAction().deleteFromRealm();
                refueling.deleteFromRealm();
                break;
            case EXPENSE:
                Expense expense = (Expense) model;
                //expense.getNote().deleteFromRealm();
                //expense.getAction().deleteFromRealm();
                expense.deleteFromRealm();
                break;*/
        }
    }

    public static void deleteVehicle(Vehicle vehicle) {
       // vehicle.getNote().deleteFromRealm();

        //Reverse iteration due to an error
        RealmList<Service> services = vehicle.getServices();
        for (int i = services.size() - 1; i >= 0; i--) {
            deleteProperty(services.get(i), ActivityType.SERVICE);
        }
        services.deleteAllFromRealm();

        RealmList<Insurance> insurances = vehicle.getInsurances();
        for (int i = insurances.size() - 1; i >= 0; i--) {
            deleteProperty(insurances.get(i), ActivityType.INSURANCE);
        }
        insurances.deleteAllFromRealm();

        RealmList<Refueling> refuelings = vehicle.getRefuelings();
        for (int i = refuelings.size() - 1; i >= 0; i--) {
            deleteProperty(refuelings.get(i), ActivityType.REFUELING);
        }
        refuelings.deleteAllFromRealm();

        RealmList<Expense> expenses = vehicle.getExpenses();
        for (int i = expenses.size() - 1; i >= 0; i--) {
            deleteProperty(expenses.get(i), ActivityType.EXPENSE);
        }
        expenses.deleteAllFromRealm();

        vehicle.getFuelTanks().deleteAllFromRealm();
        vehicle.deleteFromRealm();
    }

    public static void importVehicle(final Vehicle vehicle, final boolean exists, Realm myRealm,
                                     Realm.Transaction.OnSuccess onSuccess,
                                     Realm.Transaction.OnError onError) {
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (exists) {
                    deleteVehicle(realm.where(Vehicle.class).equalTo(RealmTable.NAME, vehicle.getName())
                            .findFirst());
                }
                Brand brand = realm.where(Brand.class)
                        .equalTo(RealmTable.NAME, vehicle.getBrand().getName())
                        .findFirst();
                if (brand == null) {
                    brand = vehicle.getBrand();
                }
                vehicle.setBrand(brand);

                Model model = realm.where(Model.class)
                        .equalTo(RealmTable.NAME, vehicle.getModel().getName())
                        .findFirst();
                if (model == null) {
                    model = vehicle.getModel();
                }
                vehicle.setModel(model);

                Color color = realm.where(Color.class)
                        .equalTo(RealmTable.COLOR, vehicle.getColor().getColor())
                        .findFirst();
                if (color == null) {
                    color = vehicle.getColor();
                }
                vehicle.setColor(color);

               /* VehicleType vehicleType = realm.where(VehicleType.class)
                        .equalTo(RealmTable.NAME, vehicle.getType().getName())
                        .findFirst();
                if (vehicleType == null) {
                    vehicleType = vehicle.getType();
                }*/
                //vehicle.setType(vehicleType);

                /*for (Expense expense : vehicle.getExpenses()) {
                    ExpenseType expenseType = realm.where(ExpenseType.class)
                            .equalTo(RealmTable.NAME, expense.getType().getName())
                            .findFirst();
                    if (expenseType == null) {
                        expenseType = expense.getType();
                    }
                    expense.setType(expenseType);
                }*/

                /*for (FuelTank fuelTank : vehicle.getFuelTanks()) {
                    FuelType fuelType = realm.where(FuelType.class)
                            .equalTo(RealmTable.NAME, fuelTank.getFuelType().getName())
                            .findFirst();
                    if (fuelType == null) {
                        fuelType = fuelTank.getFuelType();
                    }
                    fuelTank.setFuelType(fuelType);
                }*/

                for (Insurance insurance : vehicle.getInsurances()) {
                    Company company = realm.where(Company.class)
                            .equalTo(RealmTable.NAME, insurance.getCompany().getName())
                            .findFirst();
                    if (company == null) {
                        company = insurance.getCompany();
                    }
                    insurance.setCompany(company);
                }

                for (Service service : vehicle.getServices()) {
                    ServiceType serviceType = realm.where(ServiceType.class)
                            .equalTo(RealmTable.NAME, service.getType().getName())
                            .findFirst();
                    if (serviceType == null) {
                        serviceType = service.getType();
                    }
                    service.setType(serviceType);
                }

                realm.copyToRealm(vehicle);
            }
        }, onSuccess, onError);
    }
}
