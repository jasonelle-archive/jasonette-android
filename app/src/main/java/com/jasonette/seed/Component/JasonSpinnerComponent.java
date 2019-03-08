package com.jasonette.seed.Component;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
//import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.jasonette.seed.Core.JasonViewActivity;
import com.jasonette.seed.Helper.JasonHelper;
import org.json.JSONException;
import org.json.JSONObject;


public class JasonSpinnerComponent{

    public static View build(View view, final JSONObject component, final JSONObject parent, final Context context) {
        if(view == null){
            return new Spinner(context);
        } else {
            try {
                JSONObject options = component.getJSONObject("options");

                String title = options.get("title").toString();
                String data = title +"|"+ options.get("data").toString(); //title will be first item, as title for component

                final String[] spinnerData = data.split("\\|");
                view = JasonComponent.build(view, component, parent, context);

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String> (context, android.R.layout.simple_spinner_item, spinnerData){
                    @Override
                    public boolean isEnabled(int position){
                        if(position == 0){// Disable the first item <Title> from Spinner
                            return false;
                        } else {
                            return true;
                        }
                    }
                    
                    //do first item more like a title, grayed out
                    
                };
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ((Spinner)view).setAdapter(spinnerArrayAdapter);

                ((Spinner)view).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            ((JasonViewActivity) context).model.var.put(component.getString("name"), spinnerData[position]);
                        } catch (JSONException e) {
                            Log.e("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((Spinner)view).requestLayout();
                return view;             
                

            } catch (Exception e){
                Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
                return new View(context);
            }
        }
    }
}