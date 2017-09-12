package de.uni_marburg.mathematik.ds.serval.controller.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.model.event.EventCallback;
import de.uni_marburg.mathematik.ds.serval.model.event.GenericEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Loads {@link Event events} asynchronously and informs the calling {@link EventAsyncTask#context
 * context} about its completion with a {@link EventAsyncTask#eventCallback callback}.
 *
 * @param <T> {@link Event} type
 */
public class EventAsyncTask<T extends Event> extends AsyncTask<String, Void, List<T>> {
    
    /**
     * Calling context
     */
    private final Context context;
    
    /**
     * Informs the calling {@link EventAsyncTask#context context} that the task is finished.
     */
    private EventCallback<T> eventCallback;
    
    /**
     * Is used to download JSON data.
     */
    private OkHttpClient client;
    
    /**
     * Serializes JSON to an {@link Event event}.
     */
    private JsonAdapter eventAdapter;
    
    /**
     * Creates a new asynchronous task.
     *
     * @param context Calling context
     */
    public EventAsyncTask(Context context) {
        this.context = context;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // TODO Figure out why this is unsafe or change to RxJava
        //noinspection unchecked
        eventCallback = (EventCallback<T>) context;
        client = new OkHttpClient();
        Moshi moshi = new Moshi.Builder().build();
        // TODO Figure out how to implement a generic adapter
        eventAdapter = moshi.adapter(GenericEvent.class);
    }
    
    @Override
    protected List<T> doInBackground(String... urls) {
        final List<T> events = new ArrayList<>();
        
        for (String url : urls) {
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Crashlytics.log(String.format(
                            context.getString(R.string.log_message_fail_event_load),
                            e.getMessage()
                    ));
                }
                
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response)
                        throws IOException {
                    if (!response.isSuccessful()) {
                        Crashlytics.log(String.format(
                                context.getString(R.string.log_message_response_unsuccessful),
                                response.toString()
                        ));
                    }
                    
                    @SuppressWarnings("ConstantConditions")
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
