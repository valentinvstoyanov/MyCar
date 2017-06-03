package stoyanov.valentin.mycar.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.BaseActivity;
import stoyanov.valentin.mycar.preferences.PreferenceManager;
import stoyanov.valentin.mycar.realm.models.Brand;
import stoyanov.valentin.mycar.realm.models.Color;
import stoyanov.valentin.mycar.realm.models.Company;
import stoyanov.valentin.mycar.realm.models.RealmSettings;
import stoyanov.valentin.mycar.realm.models.ServiceType;
import stoyanov.valentin.mycar.utils.ColorUtils;
import stoyanov.valentin.mycar.utils.FileUtils;

public class WelcomeActivity extends BaseActivity implements ViewPager.OnPageChangeListener{

    private PreferenceManager preferenceManager;
    private ViewPager viewPager;
    private Button buttonNext;
    private View tab1, tab2, tab3;
    private int[] sliders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext(), MODE_PRIVATE);
        if (!preferenceManager.isFirstLaunch()) {
            launchMainActivity();
            finish();
        }else {
            setContentView(R.layout.activity_welcome);
            setStatusBarColor(ResourcesCompat.getColor(getResources(), R.color.colorWelcome1, null));
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.pb_realm_seeding);
            progressBar.setMax(100);
            progressBar.getIndeterminateDrawable().setColorFilter(
                    ResourcesCompat.getColor(getResources(),
                            R.color.colorAccent, null), PorterDuff.Mode.MULTIPLY);
            progressBar.setProgress(50);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream inputStream = getResources().openRawResource(R.raw.brands);
                    final String[] brandNames = FileUtils.getParsedCsv(inputStream);
                    inputStream = getResources().openRawResource(R.raw.service_types);
                    final String[] serviceTypeNames = FileUtils.getParsedCsv(inputStream);
                    inputStream = getResources().openRawResource(R.raw.companies);
                    final String[] companies = FileUtils.getParsedCsv(inputStream);
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    progressBar.setProgress(20);

                    final TypedArray primaryColors = getResources().obtainTypedArray(R.array.vehicles_primary_colors);
                    final TypedArray darkColors = getResources().obtainTypedArray(R.array.vehicles_dark_colors);
                    final Realm myRealm = Realm.getDefaultInstance();

                    myRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            for (String brandName : brandNames) {
                                Brand brand = realm.createObject(Brand.class,
                                        UUID.randomUUID().toString());
                                brand.setName(brandName);
                            }
                            progressBar.incrementProgressBy(10);
                            for (String typeName : serviceTypeNames) {
                                ServiceType serviceType = realm.createObject(ServiceType.class,
                                        UUID.randomUUID().toString());
                                serviceType.setName(typeName);
                            }
                            progressBar.incrementProgressBy(20);
                            for (String companyName : companies) {
                                Company company = realm.createObject(Company.class,
                                        UUID.randomUUID().toString());
                                company.setName(companyName);
                            }
                            progressBar.incrementProgressBy(30);
                            for (int i = 0; i < primaryColors.length(); i++) {
                                Color color = realm.createObject(Color.class, UUID.randomUUID().toString());
                                color.setColor(primaryColors.getColor(i, 0));
                                color.setRelevantDarkColor(darkColors.getColor(i, 0));
                                color.setTextIconsColor(ColorUtils
                                        .pickColorByBackground(getApplicationContext(),
                                                color.getColor()));
                            }
                            progressBar.incrementProgressBy(15);
                            RealmSettings settings = realm.createObject(RealmSettings.class);
                            settings.setLengthUnit("km");
                            settings.setDistanceInAdvance(1000);
                            settings.setCurrencyUnit("BGN");
                            progressBar.incrementProgressBy(5);
                        }
                    });
                    myRealm.close();
                    primaryColors.recycle();
                    darkColors.recycle();
                }
            }).start();
            initComponents();
            setComponentListeners();
            WelcomeViewPagerAdapter viewPagerAdapter = new WelcomeViewPagerAdapter();
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.addOnPageChangeListener(this);
            addTabColors(0);
        }
    }

    @Override
    public void initComponents() {
        viewPager = (ViewPager) findViewById(R.id.vp_welcome);
        buttonNext = (Button) findViewById(R.id.btn_welcome_next);
        tab1 = findViewById(R.id.view_welcome1);
        tab2 = findViewById(R.id.view_welcome2);
        tab3 = findViewById(R.id.view_welcome3);
        sliders = new int[] {
                R.layout.slide_welcome1,
                R.layout.slide_welcome2,
                R.layout.slide_welcome3
        };
    }

    @Override
    public void setComponentListeners() {
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int viewPagerCurrentSlider = viewPager.getCurrentItem() + 1;
                if (viewPagerCurrentSlider < sliders.length) {
                    viewPager.setCurrentItem(viewPagerCurrentSlider);
                }else {
                    launchMainActivity();
                    finish();
                }
            }
        });
    }

    @Override
    public void setContent() {

    }

    private void setStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(color);
    }

    private void launchMainActivity() {
        preferenceManager.setFirstTimeLaunch(false);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void addTabColors(int currentSlider) {
        int[] activeColors = getResources().getIntArray(R.array.activeColors);
        int[] inactiveColors = getResources().getIntArray(R.array.inactiveColors);
        switch (currentSlider) {
            case 0:
                setStatusBarColor(ResourcesCompat.getColor(getResources(),
                        R.color.colorWelcome1, null));
                tab1.setBackgroundColor(activeColors[currentSlider]);
                tab2.setBackgroundColor(inactiveColors[currentSlider]);
                tab3.setBackgroundColor(inactiveColors[currentSlider]);
                break;
            case 1:
                setStatusBarColor(ResourcesCompat.getColor(getResources(),
                        R.color.colorWelcome2, null));
                tab1.setBackgroundColor(inactiveColors[currentSlider]);
                tab2.setBackgroundColor(activeColors[currentSlider]);
                tab3.setBackgroundColor(inactiveColors[currentSlider]);
                break;
            case 2:
                setStatusBarColor(ResourcesCompat.getColor(getResources(),
                        R.color.colorWelcome3, null));
                tab1.setBackgroundColor(inactiveColors[currentSlider]);
                tab2.setBackgroundColor(inactiveColors[currentSlider]);
                tab3.setBackgroundColor(activeColors[currentSlider]);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        addTabColors(position);
        int currentPosition = position + 1;
        if (currentPosition == sliders.length) {
            buttonNext.setText(getString(R.string.got_it));
        }else {
            buttonNext.setText(getString(R.string.next));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class WelcomeViewPagerAdapter extends PagerAdapter{

        private LayoutInflater inflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(sliders[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return sliders.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
