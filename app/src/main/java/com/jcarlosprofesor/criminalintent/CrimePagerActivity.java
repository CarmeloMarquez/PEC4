package com.jcarlosprofesor.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity
implements CrimeFragment.Callbacks{

    private static final String EXTRA_CRIME_ID = "crime_id";
    private ViewPager2 mViewPager;
    private List<Crime> mCrimes;
    //Boton para acceder al primer crimen
    private Button mToFirst;
    //Boton para acceder al ultimo crimen
    private Button mToLast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        //Traemos la lista de elemento Crime contenida en CrimeLab
        mCrimes = CrimeLab.get(this).getCrimes();
        //Creamos el objeto mViewPager que mostrara los crimenes
        mViewPager = (ViewPager2) findViewById(R.id.activity_crime_pager_view_pager);
        //Creamos el objeto FragmentManager para manejar los fragment que vamos a cargar
        //en el objeto mViewPager
        FragmentManager fragmentManager = getSupportFragmentManager();
        //Seteamos el adaptador necesario para leer los objetos Crime de la lista
        mViewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getItemCount() {
                return mCrimes.size();
            }
        });
        for(int i = 0; i < mCrimes.size(); i++){
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
            }
        }
        //Funcionalidad botones
        //Enlazamos los botones
        mToFirst = (Button) findViewById(R.id.button_to_start);
        mToLast = (Button) findViewById(R.id.button_to_last);

        //Le damos el comportamiento a los dos botones
        mToFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Nos lleve al primer crimen
                mViewPager.setCurrentItem(0);
            }
        });
        mToLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Nos lleve al ultimo crimen
                mViewPager.setCurrentItem(mCrimes.size());
            }
        });
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                //Pornelo visible y util dependiendo de la posicion
                //Para los 2 botones
                mToFirst.setEnabled(!(position==0));
                mToFirst.setVisibility((position==0) ? View.GONE:View.VISIBLE);

                mToLast.setEnabled(!(position==mCrimes.size()-1));
                mToLast.setVisibility((position==mCrimes.size()-1) ? View.GONE:View.VISIBLE);
            }
        });

    }
    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent = new Intent(packageContext,CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeId);
        return intent;
    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }
    //Le pasamos el menu que tiene que tener
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_crime_pager,menu);
        return true;
    }

    //Hacemos nuestro switch, que en el caso de que se clicke en el contenedor
    //se elimine el crimen donde estamos situados
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_item:
                CrimeLab.get(this).deleteCrime(mViewPager.getCurrentItem());
                //Dos formas de hacerlo
                //1º Haciendo un intent hacia la main activity
                //startActivity(new Intent(this,CrimeListActivity.class));
                //2º Haciendo un finish cerrando la actual
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
