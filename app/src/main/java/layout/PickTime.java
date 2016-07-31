package layout;

/**
 * Created by jverges on 7/29/16.
 */

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import com.bluetooth.jverges.sistemadomotica.MainActivity;
import com.bluetooth.jverges.sistemadomotica.StatusItem;

public class PickTime extends DialogFragment {

    TimePickerDialog mTimePicker;
    public StatusItem statusItem;

    public Dialog onCreateDialog(Bundle saveInstState) {
        mTimePicker = new TimePickerDialog(this.getActivity(), new TimePickerDialog.OnTimeSetListener() {
            int callCount = 0;   //To track number of calls to onTimeSet()

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Log.d("tag", "call " + selectedHour + " " + selectedMinute);

                MainActivity.mainActivity.setTime(selectedHour, selectedMinute);
                // Incrementing call count.
            }
        }, statusItem.time.getHourOfDay(),
                statusItem.time.getMinuteOfHour(),
                DateFormat.is24HourFormat(this.getActivity()));
        //d.setTitle();

        return mTimePicker;
    }
}
