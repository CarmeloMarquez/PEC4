package com.jcarlosprofesor.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {
    //constante para identificar la hora dentro del bundle
    //de argumentos del fragment
    private static  final String ARG_TIME = "time";
    //Creamos la constante para identificar la hora que pasamos en el intent
    public static  final String EXTRA_TIME = "intent_time";

    private TimePicker timePicker;
    //Metodo statico que devuelve un objeto TimePickerFragment
    //Dentro usamos un Bundle para almacenar informacion
    public static TimePickerFragment newInstante(Date time){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, time);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time,null);
        timePicker = (TimePicker) view.findViewById(R.id.dialog_time_picker);
        //OnCreate Dialog puede devolver un objeto tipo TimePickerDialog
        //Por ello pasamos creamos un objeto de este tipo y en el constructor tenemos que pasar
        //Un contexto,OnTimeSetListener(en este caso, uso onTimeSet como clase anonima
        // en vez de implementar el metodo),hora,minuto,formato de tipo de hora

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            //OnTimeSet sirve para crear que en el momento que elegimos la hora y el minuto
            //haga un set de esa informacion
            @Override
            //le he cambiado el nombre por parametro para ser mas identificativo de lo que es realmente
            //ya que con "i" y "i1" era poco descriptivo
            public void onTimeSet(TimePicker timePicker, int horaActual, int minutoActual) {
                //creamos un objeto de tipo fecha que almacena la hora y el minuto
                Date time = new Date();
                time.setHours(horaActual);
                time.setMinutes(minutoActual);

                sendResult(Activity.RESULT_OK,time);
            }
        },hour,minute,true);
        //agregamos el titulo
        timePickerDialog.setTitle(R.string.time_picker_title);
        //lo mostramos
        timePickerDialog.show();
        //devolvemos el objeto
        return timePickerDialog;
    }

    //send result sirve para enviar la informacion
    private void sendResult(int resultOk, Date time) {
        if(getTargetFragment()==null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, time);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultOk,intent);
    }
}
