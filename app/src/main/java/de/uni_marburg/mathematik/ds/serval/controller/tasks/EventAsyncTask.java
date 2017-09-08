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
import java.util.Locale;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.event.EventCallback;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.model.event.GenericEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Loads {@link Event events} asynchronously.
 * <p>
 * Once it is finished the calling {@link android.app.Activity activity} gets informed through an
 * {@link EventCallback event callback} and is able to update the UI:
 */
public class EventAsyncTask<T extends Event> extends AsyncTask<String, Void, List<T>> {
    
    private Context context;
    
    /**
     * Informs the calling {@link android.app.Activity activity} that the loading is finished.
     */
    private EventCallback<T> eventCallback;
    
    /**
     * Holds requests (multiple URLs) that should be used to load {@link Event events}.
     */
    private Request request;
    
    /**
     * Is used to download JSON data.
     */
    private OkHttpClient client;
    
    /**
     * Serializes JSON to an {@link Event event}.
     */
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
        // TODO Figure out how to implement a generic adapter
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
                    Log.e(
                            getClass().getSimpleName(),
                            String.format(
                                    Locale.getDefault(),
                                    context.getString(R.string.log_message_fail_event_load),
                                    e.getMessage()
                            )
                    );
                }
                
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response)
                        throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e(
                                getClass().getSimpleName(),
                                String.format(
                                        Locale.getDefault(),
                                        context.getString(R.string.log_message_response_unsuccessful),
                                        request.toString()
                                )
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
        // Inform calling Activity that events are loaded and the UI can be updated
        eventCallback.onEventsLoaded(events);
    }
}
