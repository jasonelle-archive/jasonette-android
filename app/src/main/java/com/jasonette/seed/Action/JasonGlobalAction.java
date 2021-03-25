package com.jasonette.seed.Action;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.jasonette.seed.Core.JasonViewActivity;
import com.jasonette.seed.Helper.JasonHelper;
import com.jasonette.seed.Launcher.Launcher;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

public class JasonGlobalAction {
    public Object[] addToArray(Object[] arr, Object toInsert) {
        int i;
        Object[] array = new Object[arr.length + 1];
        for (i = 0; i < arr.length; i++)
            array[i] = arr[i];
        array[arr.length] = toInsert;

        return array;
    }

    public void reset(final JSONObject action, final JSONObject data, final JSONObject event, final Context context) {

        /********************

         The following resets a global variable named "db".
         When a variable is reset, the key itself gets destroyed, so when you check ('db' in $global), it will return false

         {
             "type": "$global.reset",
             "options": {
                 "items": ["db"]
             }
         }

         ********************/

        try {
            SharedPreferences pref = context.getSharedPreferences("global", 0);
            SharedPreferences.Editor editor = pref.edit();



            JSONObject options = action.getJSONObject("options");
            if(options.has("items")){
                JSONArray items = options.getJSONArray("items");
                for (int i=0; i<items.length(); i++) {
                    String item = items.getString(i);
                    editor.remove(item);
                    ((Launcher)context.getApplicationContext()).resetGlobal(item);
                }
                editor.commit();
            }

            // Execute next
            JasonHelper.next("success", action, ((Launcher)context.getApplicationContext()).getGlobal(), event, context);

        } catch (Exception e) {
            Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
        }

    }


    public void set(final JSONObject action, final JSONObject data, final JSONObject event, final Context context) {

        /********************

         The following sets a global variable named "db".

         {
             "type": "$global.set",
             "options": {
                 "db": ["a", "b", "c", "d"]
             }
         }

         Once set, you can access them through template expressions from ANYWHERE within the app, like this:

         {
             "items": {
                 "{{#each $global.db}}": {
                     "type": "label",
                     "text": "{{this}}"
                 }
             }
         }

         ********************/

        try {
            SharedPreferences pref = context.getSharedPreferences("global", 0);
            SharedPreferences.Editor editor = pref.edit();

            JSONObject options = action.getJSONObject("options");



            Iterator<String> keysIterator = options.keys();
            while (keysIterator.hasNext()) {
                String key = (String) keysIterator.next();
                Object val = options.get(key);
                editor.putString(key, val.toString());
                ((Launcher)context.getApplicationContext()).setGlobal(key, val);
            }
            editor.commit();

            // Execute next
            JasonHelper.next("success", action, ((Launcher)context.getApplicationContext()).getGlobal(), event, context);

        } catch (Exception e) {
            Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
        }
    }

    public void push(final JSONObject action, final JSONObject data, final JSONObject event, final Context context) {
        try {
            SharedPreferences pref = context.getSharedPreferences("global", 0);
            SharedPreferences.Editor editor = pref.edit();
            JSONObject globalContext = ((Launcher)context.getApplicationContext()).getGlobal();

            JSONObject options = action.getJSONObject("options");

            Iterator<String> keysIterator = options.keys();
            while (keysIterator.hasNext()) {
                String key = (String) keysIterator.next();
                try {
                    Log.d("testing",  "valued");

                    JSONObject newItem = options.getJSONObject(key);
                    JSONArray existingArray = globalContext.getJSONArray(key);


                    Log.d("ok let us see...", newItem.toString() + existingArray.toString());

                    int JSONlength = existingArray.length();
                    newItem.put("_id", JSONlength);

                    existingArray.put(newItem);

                    JSONObject lookFor_ID = existingArray.getJSONObject(0);
                    if(!lookFor_ID.has("_id")) {
                        JSONArray newJsonArray = new JSONArray();
                        for (int i = 0; i < JSONlength; i++) {
                            JSONObject item = existingArray.getJSONObject(i);
                            if (!item.has("_id")) {
                                item.put("_id", i);
                            }
                            newJsonArray.put(item);
                        }
                        editor.putString(key, newJsonArray.toString());
                        ((Launcher)context.getApplicationContext()).setGlobal(key, newJsonArray);

                    } else {
                        editor.putString(key, existingArray.toString());
                        ((Launcher)context.getApplicationContext()).setGlobal(key, existingArray);
                    }
                        //Log.d("new array?", myTestArray.toString() + myTestArray[0].toString() + myTestArray[1].toString());

                    } catch(Exception e) {
                        //exception when the key is new
                        //in this case, we create a new array with the input values
                        //Log.d("warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
                        JSONArray newArray = new JSONArray();
                        JSONObject newItem = options.getJSONObject(key);
                        newArray.put(newItem);
                        //Log.d("new object", newArray.toString() + "array to set");
                        editor.putString(key, newArray.toString());
                        ((Launcher)context.getApplicationContext()).setGlobal(key, newArray);
                    }

            }
            editor.commit();

            // Execute next
            JasonHelper.next("success", action, ((Launcher)context.getApplicationContext()).getGlobal(), event, context);

        } catch (Exception e) {
            Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
        }
    }

    public void splice(final JSONObject action, final JSONObject data, final JSONObject event, final Context context) {
        try {
            SharedPreferences pref = context.getSharedPreferences("global", 0);
            SharedPreferences.Editor editor = pref.edit();


            JSONObject globalContext = ((Launcher)context.getApplicationContext()).getGlobal();

            JSONObject options = action.getJSONObject("options");


            Iterator<String> keysIterator = options.keys();
            while (keysIterator.hasNext()) {
                String key = (String) keysIterator.next();
                try {
                    int removeItem = Integer.parseInt(options.getString(key));
                    JSONArray existingArray = globalContext.getJSONArray(key);
                    existingArray.remove(removeItem);


                    editor.putString(key, existingArray.toString());
                    ((Launcher)context.getApplicationContext()).setGlobal(key, existingArray);
                } catch(Exception e) {
                    //exception when the key is new
                    //in this case, we create a new array with the input values
                    Log.d("warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
                    JSONArray newArray = new JSONArray();
                    JSONObject newItem = options.getJSONObject(key);
                    newArray.put(newItem);
                    Log.d("new object", newArray.toString() + "array to set");
                    editor.putString(key, newArray.toString());
                    ((Launcher)context.getApplicationContext()).setGlobal(key, newArray);
                }

            }
            editor.commit();

            // Execute next
            JasonHelper.next("success", action, ((Launcher)context.getApplicationContext()).getGlobal(), event, context);

        } catch (Exception e) {
            Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
        }
    }

    public void update(final JSONObject action, final JSONObject data, final JSONObject event, final Context context) {
        try {
            SharedPreferences pref = context.getSharedPreferences("global", 0);
            SharedPreferences.Editor editor = pref.edit();

            JSONObject globalContext = ((Launcher)context.getApplicationContext()).getGlobal();

            JSONObject options = action.getJSONObject("options");


            Iterator<String> keysIterator = options.keys();
            while (keysIterator.hasNext()) {
                String key = (String) keysIterator.next();
                try {
                    JSONObject updatedValue = options.getJSONObject(key);
                    int targetElement = Integer.parseInt(updatedValue.getString("_id"));

                    Log.d("ok let us see...", updatedValue.toString() + " ");
                    JSONArray existingArray = globalContext.getJSONArray(key);

                    int arrayLength = existingArray.length();
                    JSONArray newArray = new JSONArray();

                    for (int i = 0; i < arrayLength; i++) {
                        if (targetElement == i) {
                            newArray.put(targetElement);
                        } else {
                            JSONObject existingObj = existingArray.getJSONObject(i);
                            newArray.put(existingObj);
                        }
                    }

                    editor.putString(key, newArray.toString());
                    ((Launcher)context.getApplicationContext()).setGlobal(key, newArray);
                } catch(Exception e) {
                    //exception when the key is new
                    //in this case, we create a new array with the input values
                    //Log.d("warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
                    JSONArray newArray = new JSONArray();
                    JSONObject newItem = options.getJSONObject(key);
                    newArray.put(newItem);
                    //Log.d("new object", newArray.toString() + "array to set");
                    editor.putString(key, newArray.toString());
                    ((Launcher)context.getApplicationContext()).setGlobal(key, newArray);
                }
            }
            editor.commit();

            // Execute next
            JasonHelper.next("success", action, ((Launcher)context.getApplicationContext()).getGlobal(), event, context);

        } catch (Exception e) {
            Log.d("Warning", e.getStackTrace()[0].getMethodName() + " : " + e.toString());
        }
    }

}
