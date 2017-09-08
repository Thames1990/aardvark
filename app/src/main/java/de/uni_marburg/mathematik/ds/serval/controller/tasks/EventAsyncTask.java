package de.uni_marburg.mathematik.ds.serval.controller.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.uni_marburg.mathematik.ds.serval.interfaces.EventCallback;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.model.event.GenericEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by thames1990 on 08.09.17.
 */
public class EventAsyncTask<T extends Event> extends AsyncTask<String, Void, List<T>> {
    
    private Context context;
    
    private EventCallback<T> eventCallback;
    
    private Request request;
    
    private OkHttpClient client;
    
    private JsonAdapter eventAdapter;
    
    public EventAsyncTask(Context context) {
        this.context = context;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //noinspection unchecked
        eventCallback = (EventCallback<T>) context;
        client = new OkHttpClient();
        Moshi moshi = new Moshi.Builder().build();
        eventAdapter = moshi.adapter(GenericEvent.class);
    }
    
    @Override
    protected List<T> doInBackground(String... urls) {
        List<T> events = new ArrayList<>();
        
        for (String url : urls) {
            request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(getClass().getSimpleName(), "Failed to load events: " + e.getMessage());
                }
                
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response)
                        throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e(
                                getClass().getSimpleName(),
                                "Response wasn't successfull for request: " + request.toString()
                        );
                    }
                    
                    //noinspection ConstantConditions
                    InputStream inputStream = response.body().byteStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        //noinspection unchecked
                        events.add((T) eventAdapter.fromJson(line));
                    }
                }
            });
        }
        
        return events;
    }
    
    @Override
    protected void onPostExecute(List<T> events) {
        super.onPostExecute(events);
        eventCallback.onEventsLoaded(events);
    }
}
