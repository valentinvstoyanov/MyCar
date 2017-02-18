package stoyanov.valentin.mycar.utils;

import java.math.BigDecimal;
import java.util.ArrayList;

import io.realm.RealmList;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Service;

public class ChartUtils {

    public static ArrayList<Float> getPricesFromServices(RealmList<Service> list) {
        ArrayList<Float> floats = new ArrayList<>(list.size());
        for (Service service : list) {
            floats.add(MoneyUtils.longToFloat(new BigDecimal(service.getPrice())));
        }
        return floats;
    }

    public static ArrayList<Float> getPricesFromInsurances(RealmList<Insurance> list) {
        ArrayList<Float> floats = new ArrayList<>(list.size());
        for (Insurance insurance : list) {
            floats.add(MoneyUtils.longToFloat(new BigDecimal(insurance.getPrice())));
        }
        return floats;
    }

    public static ArrayList<Float> getPricesFromRefuelings(RealmList<Refueling> list) {
        ArrayList<Float> floats = new ArrayList<>(list.size());
        for (Refueling refueling : list) {
            floats.add(MoneyUtils.longToFloat(new BigDecimal(refueling.getPrice())));
        }
        return floats;
    }

    public static ArrayList<Float> getPricesFromExpenses(RealmList<Expense> list) {
        ArrayList<Float> floats = new ArrayList<>(list.size());
        for (Expense expense : list) {
            floats.add(MoneyUtils.longToFloat(new BigDecimal(expense.getPrice())));
        }
        return floats;
    }
}
