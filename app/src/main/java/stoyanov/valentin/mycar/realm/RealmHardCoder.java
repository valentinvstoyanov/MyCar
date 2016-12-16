package stoyanov.valentin.mycar.realm;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.repositories.IBrandRepository;
import stoyanov.valentin.mycar.realm.repositories.IVehicleTypeRepository;
import stoyanov.valentin.mycar.realm.repositories.impl.BrandRepository;
import stoyanov.valentin.mycar.realm.repositories.impl.VehicleRepository;
import stoyanov.valentin.mycar.realm.repositories.impl.VehicleTypeRepository;
import stoyanov.valentin.mycar.utils.CsvUtils;

public class RealmHardCoder {

    private Context context;

    public RealmHardCoder(Context context) {
        this.context = context;
    }


    public void hardCodeVehicleTypes() {
        VehicleTypeRepository vehicleTypeRepository = new VehicleTypeRepository();
        vehicleTypeRepository.addManyVehicleTypes(context.getResources()
                        .getStringArray(R.array.vehicle_types),
                new IVehicleTypeRepository.OnVehicleTypesAdded() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "Vehicle Types Added...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(context, "Someting went wrong...", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void hardCodeBrands() {
        BrandRepository brandRepository = new BrandRepository();
        InputStream inputStream = context.getResources().openRawResource(R.raw.brands);
        brandRepository.addManyBrands(CsvUtils.getParsedCsv(inputStream),
                new IBrandRepository.OnAddBrandListCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "Brands added...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
