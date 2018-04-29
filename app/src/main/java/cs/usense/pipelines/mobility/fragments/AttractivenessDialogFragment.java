package cs.usense.pipelines.mobility.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.text.DecimalFormat;

import cs.usense.R;
import cs.usense.pipelines.mobility.models.MTrackerAP;

/**
 * Created by copelabs on 08/01/2018.
 */

@SuppressLint("ValidFragment")
public class AttractivenessDialogFragment extends DialogFragment {

    private static final String TAG = AttractivenessDialogFragment.class.getSimpleName();
    double  mAttractiveness;
    private MTrackerAP mTrackerAP;
    public interface AttractivenessDialogListener {
        void onUpdateAP(MTrackerAP ap);
        void connectToAP(MTrackerAP ap);
    }

    AttractivenessDialogListener mListener;

    @SuppressLint("ValidFragment")
    public AttractivenessDialogFragment (MTrackerAP ap){
        mTrackerAP=ap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final DecimalFormat df = new DecimalFormat("##.00");
        View v = inflater.inflate(R.layout.fragment_attractiveness, container, false);
        TextView txtSSID = (TextView) v.findViewById(R.id.textView);
        TextView textView = (TextView) v.findViewById(R.id.textView4);
        txtSSID.setText("SSID: " + mTrackerAP.getSSID());
        textView.setText("Attractiveness:");
        final TextView txtAttractiveness = (TextView) v.findViewById(R.id.textView3);
        txtAttractiveness.setText(mTrackerAP.getAttractiveness()+"");

        Button button = (Button)v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mListener.onUpdateAP(mTrackerAP);
                getDialog().cancel();
            }
        });

        Button buttonConnect = (Button)v.findViewById(R.id.btnConnect);
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mListener.connectToAP(mTrackerAP);
                getDialog().cancel();
            }
        });

        mAttractiveness= mTrackerAP.getAttractiveness();
        final double factor = 0.1;
        Button btnAttractivenessUp = (Button) v.findViewById(R.id.buttonUp);
        Button btnAttractivenessDown = (Button) v.findViewById(R.id.buttonDown);

        btnAttractivenessUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mAttractiveness<1.0) {
                    mAttractiveness = mAttractiveness + factor;
                    mAttractiveness = Double.valueOf(df.format(mAttractiveness));
                    txtAttractiveness.setText("" + mAttractiveness);
                    mTrackerAP.setAttractiveness(mAttractiveness);
                }
            }

        });

        btnAttractivenessDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAttractiveness>0.0) {
                    mAttractiveness = (mAttractiveness - factor);
                    mAttractiveness = Double.valueOf(df.format(mAttractiveness));
                    txtAttractiveness.setText("" + mAttractiveness);
                    mTrackerAP.setAttractiveness(mAttractiveness);
                }
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AttractivenessDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
