package co.edu.icesi.i2t.mosquitos.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import co.edu.icesi.i2t.mosquitos.R;
import co.edu.icesi.i2t.mosquitos.custom.Datos;

public class TabHostController  extends AppCompatActivity{

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_host_controller);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ResumenPredio(), getResources().getString(R.string.res_pred));

        Iterator it = Datos.datosCriaderos.keySet().iterator();

        int contador = 1;
        while (it.hasNext()) {
            String key = (String)it.next();
            ResumenCriadero resCriad = new ResumenCriadero();
            resCriad.setId(key);
            adapter.addFragment(resCriad, getResources().getString(R.string.criad_label)+" "+contador);
            contador++;
        }
        viewPager.setAdapter(adapter);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /*TabHost tabHost;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tab_host_controller);

        tabHost = getTabHost();

        TabHost.TabSpec predio = tabHost.newTabSpec(getResources().getString(R.string.res_pred));

        predio.setContent(new Intent().setClass(this, ResumenPredio.class));
        predio.setIndicator(getResources().getString(R.string.res_pred));

        tabHost.addTab(predio);

        Iterator it = Datos.datosCriaderos.keySet().iterator();

        int contador = 1;
        while (it.hasNext()) {
            String key = (String)it.next();
            Object a = Datos.datosCriaderos.get(key);

            String[] info = key.split(" ");

            TabHost.TabSpec criadero = tabHost.newTabSpec(key);

            Bundle bundle = new Bundle();
            bundle.putString("idCriadero", key);

            Intent intent = new Intent();
            intent.putExtras(bundle);
            criadero.setContent(intent.setClass(this, ResumenCriadero.class));
            criadero.setIndicator(getResources().getString(R.string.criad_label)+" "+contador);

            tabHost.addTab(criadero);
            contador++;
        }
    }*/
}