package de.uni_marburg.mathematik.ds.serval.view.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Locale;

import de.uni_marburg.mathematik.ds.serval.R;
import ru.noties.markwon.Markwon;

/**
 * Created by thames1990 on 09.09.17.
 */
public class ChangelogDialog extends DialogFragment {
    
    public static final String TAG = "ChangelogDialog";
    
    private static final String VERSION_NAME = "VERSION_NAME";
    
    private static final String CHANGELOG = "CHANGELOG";
    
    public static ChangelogDialog newInstance(String versionName, String changelog) {
        ChangelogDialog dialog = new ChangelogDialog();
        Bundle args = new Bundle();
        args.putString(VERSION_NAME, versionName);
        args.putString(CHANGELOG, changelog);
        dialog.setArguments(args);
        return dialog;
    }
    
    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (!getArguments().containsKey(VERSION_NAME)) {
            throw new RuntimeException(String.format(
                    Locale.getDefault(),
                    getString(R.string.exception_fragment_must_contain_key),
                    VERSION_NAME
            ));
        }
        String versionName = getArguments().getString(VERSION_NAME);
        
        if (!getArguments().containsKey(CHANGELOG)) {
            throw new RuntimeException(String.format(
                    Locale.getDefault(),
                    getString(R.string.exception_fragment_must_contain_key),
                    CHANGELOG
            ));
        }
        String changelog = getArguments().getString(CHANGELOG);
        
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.changelog_dialog, null);
        TextView text = view.findViewById(R.id.changelog);
        Markwon.setMarkdown(text, changelog);
        return new MaterialDialog.Builder(getActivity())
                .title(versionName)
                .customView(view, true)
                .positiveText(android.R.string.ok)
                .build();
    }
}
