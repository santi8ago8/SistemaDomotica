package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluetooth.jverges.sistemadomotica.MainActivity;
import com.bluetooth.jverges.sistemadomotica.R;
import com.bluetooth.jverges.sistemadomotica.StatusUpdate;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment implements StatusUpdate {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MAINACTIVITY = "MAINACTIVITY";

    // TODO: Rename and change types of parameters
    private String mParam1;

    public static MainActivity mainActivity;

    private OnFragmentInteractionListener mListener;

    private View view;
    private TextView hora;
    private TextView luces_hora_on;
    private TextView luces_hora_off;
    private TextView riego_1_on;
    private TextView riego_2_on;
    private TextView alarma;

    public StatusFragment() {
        // Required empty public constructor
        mainActivity = null;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String param1) {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MAINACTIVITY, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_MAINACTIVITY);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_status, container, false);
        hora = (TextView) this.view.findViewById(R.id.status_hora);
        luces_hora_on = (TextView) this.view.findViewById(R.id.status_luces_hora_on);
        luces_hora_off = (TextView) this.view.findViewById(R.id.status_luces_hora_off);
        riego_1_on = (TextView) this.view.findViewById(R.id.status_riego_1_on);
        riego_2_on = (TextView) this.view.findViewById(R.id.status_riego_2_on);
        this.alarma = (TextView) this.view.findViewById(R.id.txtEstadoAlarma);

        if (null == mainActivity) {
            mainActivity = MainActivity.mainActivity;
        }
         mainActivity.status.addToNotify(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void Update() {

        Log.d("tag","update interfaz");
        this.hora.setText(mainActivity.status.hora.toString());
        this.luces_hora_on.setText(mainActivity.status.hora_luces_on.toString(MainActivity.formatterTime));
        this.luces_hora_off.setText(mainActivity.status.hora_luces_off.toString(MainActivity.formatterTime));
        this.riego_1_on.setText(mainActivity.status.hora_riego_1_on.toString(MainActivity.formatterTime));
        this.riego_2_on.setText(mainActivity.status.hora_riego_2_on.toString(MainActivity.formatterTime));

        String s = "";
        switch (MainActivity.mainActivity.status.alarma) {
            case "a":
                s = mainActivity.getString(R.string.activada);
                break;
            case "d":
                s = mainActivity.getString(R.string.desactivada);
                break;
            case "p":
                s = mainActivity.getString(R.string.panico);
                break;
        }

        this.alarma.setText(s);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
