package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bluetooth.jverges.sistemadomotica.MainActivity;
import com.bluetooth.jverges.sistemadomotica.R;
import com.bluetooth.jverges.sistemadomotica.StatusUpdate;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Alarma.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Alarma#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Alarma extends Fragment implements StatusUpdate {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static MainActivity mainActivity;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private android.widget.Button btnOnAlarma;
    private android.widget.Button btnOffAlarma;
    private android.widget.Button btnPanico;
    private android.widget.TextView textView8;
    private android.widget.TextView statusalarma;

    public Alarma() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Alarma.
     */
    // TODO: Rename and change types and number of parameters
    public static Alarma newInstance(String param1, String param2) {
        Alarma fragment = new Alarma();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_alarma, container, false);
        this.statusalarma = (TextView) rootView.findViewById(R.id.status_alarma);
        this.textView8 = (TextView) rootView.findViewById(R.id.textView8);
        this.btnPanico = (Button) rootView.findViewById(R.id.btnPanico);
        this.btnOffAlarma = (Button) rootView.findViewById(R.id.btnOffAlarma);
        this.btnOnAlarma = (Button) rootView.findViewById(R.id.btnOnAlarma);

        MainActivity.mainActivity.status.addToNotify(this);
        this.mainActivity = MainActivity.mainActivity;

        return rootView;

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

        String s = "";
        switch (MainActivity.mainActivity.status.alarma) {
            case "a":
                s = MainActivity.mainActivity.getString(R.string.activada);
                break;
            case "d":
                s = MainActivity.mainActivity.getString(R.string.desactivada);
                break;
            case "p":
                s = MainActivity.mainActivity.getString(R.string.panico);
                break;
        }

        this.statusalarma.setText(s);
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
