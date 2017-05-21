package stoyanov.valentin.mycar.realm;

public class Constants {

    public static final String ID = "id";
    public static final String ITEM_ID = "ITEM_ID";
    public static final String NAME = "name";
    public static final String ODOMETER = "odometer";
    public static final String TYPE = "type";
    public static final String DATE = "date";
    public static final String COLOR = "color";
    public static final String EXPENSES = "expenses";
    public static final String INSURANCES = "insurances";
    public static final String SERVICES = "services";
    public static final String REFUELINGS = "refuelings";
    public static final String NOTIFICATION_ID = "notificationId";
    public static final String IS_TRIGGERED = "isTriggered";
    public static final String TARGET_ODOMETER = "targetOdometer";
    public static final String PRICE = "price";
    public static final String SHOULD_NOTIFY = "shouldNotify";
    public static final String IS_ODOMETER_TRIGGERED = "isOdometerTriggered";
    public static final String DATE_NOTIFICATION = "dateNotification";
    public static final String FUEL_TYPE = "fuelTank.type";

    public enum ActivityType {
        SERVICE, REFUELING, INSURANCE, EXPENSE
    }
}
