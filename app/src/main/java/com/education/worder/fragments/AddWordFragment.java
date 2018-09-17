package com.education.worder.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.education.worder.R;
import com.education.worder.data.Word;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class AddWordFragment extends Fragment {

    private static final int REQ_CODE_SPEECH_INPUT = 100;

    private OnAddFragmentListener listener;

    private AppCompatEditText edWord;
    private AppCompatEditText edTranslate;
    private AppCompatCheckBox cbAuto;
    private AppCompatCheckBox cbMulty;
    private FloatingActionButton bVoice;
    private FloatingActionButton bAdd;
    private boolean isAutoTranslate;
    public AddWordFragment() {
    }

    public static AddWordFragment newInstance() {
        AddWordFragment fragment = new AddWordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_word, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle data){

        edWord = view.findViewById(R.id.edWord);
        edWord.requestFocus();
        edTranslate = view.findViewById(R.id.edTranslate);
        edTranslate.setHint(R.string.ed_translate_hint);
        cbAuto = view.findViewById(R.id.cbAutoTranclate);
        cbAuto.setChecked(false);
        cbAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                edTranslate.setHint(isChecked?R.string.ed_auto_translate_hint:R.string.ed_translate_hint);
                edTranslate.setEnabled(!isChecked);
                isAutoTranslate = !isChecked;
                if(listener!= null) listener.onAutoTranslate(isChecked);
            }
        });
        cbMulty = view.findViewById(R.id.cbMulty);
        cbMulty.setChecked(false);
        cbMulty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(listener!= null) listener.onMultypleInput(isChecked);
            }
        });
        bVoice = view.findViewById(R.id.bVoice);
        bVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceInput();
            }
        });

        bAdd = (FloatingActionButton) view.findViewById(R.id.bAdd);
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!cbAuto.isChecked()) {
                    addWord(edWord.getText().toString(), edTranslate.getText().toString());
                }else{
                    runTranslate(edWord.getText().toString());
                }
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddFragmentListener) {
            listener = (OnAddFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnAddFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    edWord.setText(result.get(0));
                }
                break;
            }

        }
    }
    private void addWord(String sWord, String sTranslate){
        if(!sWord.isEmpty() && !sTranslate.isEmpty()) {
            Word word = new Word(sWord, sTranslate);
            if (listener != null) listener.onAdded(word);
        }else{
            Toast.makeText(getActivity(), R.string.err_not_entered, Toast.LENGTH_LONG).show();
        }
    }
    private void runTranslate(String sText){
        new Translator().execute(sText);
    }
    private void voiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.hello_can_you_i_help);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    public void clearInput(){
        edWord.setText("");
        edTranslate.setText("");
    }
    public interface OnAddFragmentListener {
        void onAdded(Word w);
        void onAutoTranslate(boolean is);
        void onMultypleInput(boolean is);
    }
    private class Translator extends AsyncTask<String, Void, String>{
        private String source;
        @Override
        protected String doInBackground(String... strings) {
            source = strings[0];
            String result;
            try {
                AssetManager assManager = getActivity().getAssets();
                InputStream stream = assManager.open("Worder.json");

                TranslateOptions options = TranslateOptions.newBuilder()
                        .setTargetLanguage(Locale.getDefault().getLanguage())
                        .setProjectId("worder-android")
                        .setCredentials(GoogleCredentials.fromStream(stream)).build();
                Translate trService = options.getService();
                Translation translation = trService.translate(source);
                result = translation.getTranslatedText();
            }
            catch(Exception ex) {
                result = "Untranstated";
                ex.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String sResult){
            edTranslate.setEnabled(true);
            edTranslate.setText(sResult);
            addWord(source, sResult);
            edTranslate.setEnabled(isAutoTranslate);
        }
    }
}
