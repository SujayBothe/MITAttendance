package com.vssb.mitattendance;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by atlas on 10/11/2015.
 */public class CustomHeaderInnerCard extends CardHeader {
    public TextView t1;
    public String subjectName;
    public CustomHeaderInnerCard(Context context,String subjectName) {
        super(context, R.layout.carddemo_example_inner_header);
        this.subjectName = subjectName ;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view!=null){
            t1 = (TextView) view.findViewById(R.id.text_example1);
            if (t1!=null)
                t1.setText(subjectName);
            /*
            TextView t2 = (TextView) view.findViewById(R.id.text_example2);
            if (t2!=null)
                t2.setText(getContext().getString(R.string.demo_header_exampletitle2));*/
        }
    }
}
