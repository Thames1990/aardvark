package de.uni_marburg.mathematik.ds.serval.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.GenericEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by thames1990 on 01.09.17.
 */
public class GenericEventUtil {

    public static ArrayList<GenericEvent> loadData(Context context) {
        ArrayList<GenericEvent> events = new ArrayList<>();

        Request request = new Request.Builder()
                .url(context.getString(R.string.url_rest_api))
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException();
                }

                Gson gson = new Gson();
                @SuppressWarnings("ConstantConditions")
                InputStream in = response.body().byteStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                // Read line by line (append file)
                while ((line = reader.readLine()) != null) {
                    // Create an event per line
                    GenericEvent event = gson.fromJson(line, GenericEvent.class);
                    events.add(event);
                }
            }
        });

        return events;
    }
}
