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
import com.bluetooth.jverges.sistemadomotica.StatusItem;
import com.bluetooth.jverges.sistemadomotica.StatusUpdate;

import org.joda.time.format.DateTimeFormatter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Riego.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Riego#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Riego extends Fragment implements StatusUpdate {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static MainActivity mainActivity;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private TextView riego_1_on;
    private TextView riego_1_off;
    private TextView riego_2_on;
    private TextView riego_2_off;

    private OnFragmentInteractionListener mListener;
    private TextView textView6;
    private TextView textView11;
    private TextView textView7;
    private TextView statusriego1on;
    private Button btn1On;
    private TextView textView12;
    private TextView textView13;
    private TextView statusriego2on;
    private Button btn2On;
    private TextView textView10;
    private Button button3;
    private Button button4;
    private TextView textView8;
    private TextView statusriego;

    public Riego() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Riego.
     */
    // TODO: Rename and change types and number of parameters
    public static Riego newInstance(String param1, String param2) {
        Riego fragment = new Riego();
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

        View rootView = this.view = inflater.inflate(R.layout.fragment_riego, container, false);
        this.statusriego = (TextView) rootView.findViewById(R.id.status_riego);
        this.textView8 = (TextView) rootView.findViewById(R.id.textView8);
        this.button4 = (Button) rootView.findViewById(R.id.button4);
        this.button3 = (Button) rootView.findViewById(R.id.button3);
        this.textView10 = (TextView) rootView.findViewById(R.id.textView10);
        this.btn2On = (Button) rootView.findViewById(R.id.btn2On);
        this.statusriego2on = (TextView) rootView.findViewById(R.id.status_riego_2_on);
        this.textView13 = (TextView) rootView.findViewById(R.id.textView13);
        this.textView12 = (TextView) rootView.findViewById(R.id.textView12);
        this.btn1On = (Button) rootView.findViewById(R.id.btn1On);
        this.statusriego1on = (TextView) rootView.findViewById(R.id.status_riego_1_on);
        this.textView7 = (TextView) rootView.findViewById(R.id.textView7);
        this.textView11 = (TextView) rootView.findViewById(R.id.textView11);
        this.textView6 = (TextView) rootView.findViewById(R.id.textView6);


        if (mainActivity == null) {
            this.mainActivity = MainActivity.mainActivity;
        }

        //asignación de tags.
        this.btn1On.setTag(new StatusItem(R.string.configurar_prendido, "C", this.mainActivity.status.hora_riego_1_on, true));
        this.btn2On.setTag(new StatusItem(R.string.configurar_prendido, "D", this.mainActivity.status.hora_riego_2_on, true));

        this.mainActivity.status.addToNotify(this);

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
        this.statusriego1on.setText(this.mainActivity.status.hora_riego_1_on.toString(MainActivity.formatterTime));
        this.statusriego2on.setText(this.mainActivity.status.hora_riego_2_on.toString(MainActivity.formatterTime));
        this.statusriego.setText(this.mainActivity.status.Sriego?mainActivity.getString(R.string.regando):mainActivity.getString(R.string.no_regando));
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
