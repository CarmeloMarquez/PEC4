package com.jcarlosprofesor.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    //Propiedades para controlar si no hay crimenes
    private TextView mNoCrimesTextView;
    private Button mCreateCrimeButton;
    //1º En primer lugar definimos una variable miembro que guarde un objeto que implemente
    //Callbacks
    private Callbacks mCallbacks;

    //2º Definimos la interface requerida para las activity que albergan al fragment
    public interface Callbacks{
        //Metodo que nos va a permitir comunicar el crimen seleccionado
        void onCrimeSelected(Crime crime);
    }
    //3º Sobreescribimos los metodos del ciclo de vida de los fragment.
    //En el metodo onAttach asignamos la activity
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    //Indicamos al FragmentManager que su fragment va a recibir
    //una llamada al metodo onCreateOptionsMenu
    //Indicamos al FragmentManager que CrimeListFragment va a recibir
    //llamadas del menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;



        public CrimeHolder(LayoutInflater inflater, ViewGroup parent ){
            super(inflater.inflate(R.layout.list_item_crime,parent,false));
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved_img);
            itemView.setOnClickListener(this);

        }
        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            //Mejora nº1 Configuramos como se va a mostrar
            java.text.DateFormat formateadorFechaUS = java.text.DateFormat
                    .getDateInstance(java.text.DateFormat.FULL, new Locale("US"));
            mDateTextView.setText(formateadorFechaUS.format(mCrime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved()?View.VISIBLE:View.GONE);
        }
        public String formatDate(Date dateCrime){
            
          return null;
        }

        @Override
        public void onClick(View v) {
            /*Toast.makeText(getActivity(),
                            mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT)
                            .show();*/
            //Intent intent = CrimeActivity.newIntent(getActivity(),mCrime.getId());
            /*Intent intent = CrimePagerActivity.newIntent(getActivity(),mCrime.getId());
            startActivity(intent);*/
            mCallbacks.onCrimeSelected(mCrime);
        }
    }
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        //Metodo que reemplaza la lista de crimenes que se muestran
        public void setCrimes (List<Crime> crimes){
            mCrimes = crimes;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Enlazamos los elementos del xml con el codigo
        mNoCrimesTextView = (TextView) view.findViewById(R.id.ningun_crimen_text);
        mCreateCrimeButton = (Button) view.findViewById(R.id.ningun_crimen_button);

        //Hacemos que el boton sea Clickable
        mCreateCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un nuevo crimen
                Crime crime = new Crime();
                //Lo añadimos
                CrimeLab.get(getActivity()).addCrime(crime);
                //Intent para iniciar CrimePagerActivity
                Intent intent = CrimePagerActivity
                        .newIntent(getActivity(),crime.getId());
                startActivity(intent);
            }
        });
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        mAdapter = new CrimeAdapter(crimes);
        mCrimeRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public void onResume(){
        super.onResume();
        updateUI();
    }
    //hacemos public updateUI para que pueda ser llamado desde CrimeListActivity
    public void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if(mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }else{
            //Actualizamos la lista que debe usar el adaptador para mostrar los crimenes
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
        //Controlamos si no hay elementos en la Lista Crime
        if(crimes.size()==0){
            mNoCrimesTextView.setVisibility(View.VISIBLE);
            mCreateCrimeButton.setVisibility(View.VISIBLE);
            mCreateCrimeButton.setEnabled(true);
        }else{
            mNoCrimesTextView.setVisibility(View.GONE);
            mCreateCrimeButton.setVisibility(View.GONE);
            mCreateCrimeButton.setEnabled(false);
        }

    }

    //Inyectamos el archivo de layout del menu, en el objeto menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);

        //Actualizamos el menu
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else
        {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                /*Intent intent = CrimePagerActivity
                        .newIntent(getActivity(),crime.getId());
                startActivity(intent);*/
                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount,crimeCount);
        if(!mSubtitleVisible){
            subtitle=null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
}
