package br.example.com.brapolar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import br.example.com.brapolar.Activities.ReviewMedicationActivity;
import br.example.com.brapolar.Entities.EntityMedication;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TimePicker;

public class AddMedicationDialog extends AppCompatDialogFragment implements View.OnClickListener {

    EditText name, dose, unit, freq, quantity;
    ImageView timeBtn, dateBtn;
    MySqliteHandler mySqliteHandler;
    ListView MA;
    public Calendar c = Calendar.getInstance();

    private EditText etFecha;
    private EditText etHora;

    int FY, FM, FD, Fm, Fh;

    EntityMedication curr;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_medication, null);

        builder.setView(view).setTitle(R.string.medication_new)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        java.util.Calendar x = java.util.Calendar.getInstance();
                        x.set(java.util.Calendar.YEAR, FY);
                        x.set(java.util.Calendar.MONTH, FM - 1);
                        x.set(java.util.Calendar.DAY_OF_MONTH, FD);
                        x.set(java.util.Calendar.MINUTE, Fm);
                        x.set(java.util.Calendar.HOUR_OF_DAY, Fh);
                        x.set(java.util.Calendar.SECOND, 0);
                        x.set(java.util.Calendar.MILLISECOND, 0);

                        EntityMedication y = new EntityMedication(name.getText().toString(), unit.getText().toString(), dose.getText().toString(), Integer.parseInt(quantity.getText().toString()), Integer.parseInt(freq.getText().toString()), 1, x.getTimeInMillis());
//                        EntityMedication y = new EntityMedication("a","a","a",10,6,1,x.getTimeInMillis());
                        getActivity().finish();
                        Intent review = new Intent(getActivity(), ReviewMedicationActivity.class);
                        review.putExtra("EntityMedication", y);
                        startActivity(review);
                    }
                });
        name = (EditText) view.findViewById(R.id.name);
        dose = (EditText) view.findViewById(R.id.dose);
        unit = (EditText) view.findViewById(R.id.unit);
        freq = (EditText) view.findViewById(R.id.frequency);
        quantity = (EditText) view.findViewById(R.id.quantity);

        etFecha = (EditText) view.findViewById(R.id.date);
        etHora = (EditText) view.findViewById(R.id.time);

        etFecha.setShowSoftInputOnFocus(false);
        etHora.setShowSoftInputOnFocus(false);

        dateBtn = (ImageView) view.findViewById(R.id.dateBtn);
        timeBtn = (ImageView) view.findViewById(R.id.timeBtn);

        etFecha.setOnClickListener(this);
        etHora.setOnClickListener(this);

        dateBtn.setOnClickListener(this);
        timeBtn.setOnClickListener(this);

        if (curr != null) {
            c.setTimeInMillis(curr.getDate());
            name.setText(curr.getName());
            dose.setText(curr.getDose());
            unit.setText(curr.getUnit());
            freq.setText(String.valueOf(curr.getFrequency()));
            quantity.setText(String.valueOf(curr.getQuantity()));

            int year = c.get(java.util.Calendar.YEAR);
            int month = c.get(java.util.Calendar.MONTH);
            int dayOfMonth = c.get(java.util.Calendar.DAY_OF_MONTH);
            int mesActual = month + 1;
            String diaFormateado = (dayOfMonth < 10) ? "0" + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
            String mesFormateado = (mesActual < 10) ? "0" + String.valueOf(mesActual) : String.valueOf(mesActual);
            etFecha.setText(diaFormateado + "/" + mesFormateado + "/" + year);
            FY = year;
            FM = mesActual;
            FD = dayOfMonth;

            int hourOfDay = c.get(java.util.Calendar.HOUR_OF_DAY);
            int minute = c.get(java.util.Calendar.MINUTE);
            String horaFormateada = (hourOfDay < 10) ? String.valueOf("0" + hourOfDay) : String.valueOf(hourOfDay);
            String minutoFormateado = (minute < 10) ? String.valueOf("0" + minute) : String.valueOf(minute);
            String AM_PM;
            if (hourOfDay < 12) {
                AM_PM = "a.m.";
            } else {
                AM_PM = "p.m.";
            }
            etHora.setText(horaFormateada + ":" + minutoFormateado + " " + AM_PM);
            Fh = hourOfDay;
            Fm = minute;
        }

        mySqliteHandler = new MySqliteHandler(getActivity());
        return builder.create();
    }

    private void obtenerFecha() {
        final int mes = c.get(Calendar.MONTH);
        final int dia = c.get(Calendar.DAY_OF_MONTH);
        final int anio = c.get(Calendar.YEAR);
        DatePickerDialog recogerFecha = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final int mesActual = month + 1;
                String diaFormateado = (dayOfMonth < 10) ? "0" + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                String mesFormateado = (mesActual < 10) ? "0" + String.valueOf(mesActual) : String.valueOf(mesActual);
                etFecha.setText(diaFormateado + "/" + mesFormateado + "/" + year);
                FY = year;
                FM = mesActual;
                FD = dayOfMonth;
            }
        }, anio, mes, dia);

        recogerFecha.getDatePicker().setMinDate(java.util.Calendar.getInstance().getTimeInMillis());
        recogerFecha.show();
    }

    private void obtenerHora() {
        final int hora = c.get(Calendar.HOUR_OF_DAY);
        final int minuto = c.get(Calendar.MINUTE);
        TimePickerDialog recogerHora = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String horaFormateada = (hourOfDay < 10) ? String.valueOf("0" + hourOfDay) : String.valueOf(hourOfDay);
                String minutoFormateado = (minute < 10) ? String.valueOf("0" + minute) : String.valueOf(minute);
                String AM_PM;
                if (hourOfDay < 12) {
                    AM_PM = "a.m.";
                } else {
                    AM_PM = "p.m.";
                }
                etHora.setText(horaFormateada + ":" + minutoFormateado + " " + AM_PM);
                Fh = hourOfDay;
                Fm = minute;
            }
        }, hora, minuto, false);

        recogerHora.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateBtn:
                obtenerFecha();
                break;
            case R.id.date:
                obtenerFecha();
                break;
            case R.id.timeBtn:
                obtenerHora();
                break;
            case R.id.time:
                obtenerHora();
                break;
        }
    }

    public void setMedication(EntityMedication EM) {
        curr = EM;
    }
}
