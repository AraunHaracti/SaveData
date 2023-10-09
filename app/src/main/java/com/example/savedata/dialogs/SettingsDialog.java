package com.example.savedata.dialogs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.savedata.databinding.SettingsDialogBinding;

import java.time.LocalDateTime;
import java.util.Calendar;

public class SettingsDialog extends DialogFragment {

    private static final String PREFS_FILE = "PersonFile";
    private static final String PREF_NAME = "Name";
    private static final String PREF_SURNAME = "Surname";
    private static final String PREF_GENDER = "Gender";
    private static final String PREF_BIRTHDAY = "Birthday";
    private static final String PREF_TIME = "Time";
    private static final String PREF_IQ = "IQ";

    SettingsDialogBinding settingsDialogBinding;

    Calendar date = Calendar.getInstance();
    Calendar time = Calendar.getInstance();

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    public static SettingsDialog getInstance() {
        Bundle args = new Bundle();
        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.setArguments(args);
        return settingsDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getContext().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        settingsDialogBinding = SettingsDialogBinding.inflate(inflater, container, false);
        return settingsDialogBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settingsDialogBinding.birthdayTextView.setOnClickListener(this::setDate);
        settingsDialogBinding.timeTextView.setOnClickListener(this::setTime);

        settingsDialogBinding.iqSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                settingsDialogBinding.iqTextView.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        getPerson(view);
    }

    @Override
    public void onPause() {
        super.onPause();
        savePerson(this.getView());
    }

    public void setDate(View v) {
        new DatePickerDialog(this.getContext(), d,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public void setTime(View v) {
        new TimePickerDialog(this.getContext(), t,
                time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE), true)
                .show();
    }

    TimePickerDialog.OnTimeSetListener t = (view, hourOfDay, minute) -> {
        time.set(Calendar.HOUR_OF_DAY, hourOfDay);
        time.set(Calendar.MINUTE, minute);
        setInitialTime();
    };

    DatePickerDialog.OnDateSetListener d = (view, year, monthOfYear, dayOfMonth) -> {
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, monthOfYear);
        date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setInitialDate();
    };

    private void setInitialDate() {
        settingsDialogBinding.birthdayTextView
                .setText(DateUtils.formatDateTime(this.getContext(),
                date.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    private void setInitialTime() {
        settingsDialogBinding.timeTextView
                .setText(DateUtils.formatDateTime(this.getContext(),
                time.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME));
    }

    public void savePerson(View v) {
        editor = settings.edit();
        editor.putString(PREF_NAME, String.valueOf(settingsDialogBinding.nameEditText.getText()));
        editor.putString(PREF_SURNAME, String.valueOf(settingsDialogBinding.surnameEditText.getText()));
        editor.putInt(PREF_GENDER, settingsDialogBinding.genderRadioGroup.getCheckedRadioButtonId());
        editor.putLong(PREF_BIRTHDAY, date.getTimeInMillis());
        editor.putLong(PREF_TIME, time.getTimeInMillis());
        editor.putInt(PREF_IQ, Integer.parseInt(settingsDialogBinding.iqTextView.getText().toString()));

        editor.apply();
    }

    public void getPerson(View v) {
        settingsDialogBinding.nameEditText.setText(settings.getString(PREF_NAME, ""));
        settingsDialogBinding.surnameEditText.setText(settings.getString(PREF_SURNAME, ""));
        settingsDialogBinding.genderRadioGroup.check(settings.getInt(PREF_GENDER, settingsDialogBinding.genderRadioGroup.getId()));
        date.setTimeInMillis(settings.getLong(PREF_BIRTHDAY, 0));
        time.setTimeInMillis(settings.getLong(PREF_TIME, 0));
        setInitialDate();
        setInitialTime();
        settingsDialogBinding.iqSeekBar.setProgress(settings.getInt(PREF_IQ, 100));
    }
}