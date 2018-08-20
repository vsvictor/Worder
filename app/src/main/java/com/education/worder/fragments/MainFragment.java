package com.education.worder.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.education.worder.AudioService;
import com.education.worder.MainActivity;
import com.education.worder.R;
import com.education.worder.data.Word;
import com.education.worder.data.WordAdapter;
import com.education.worder.data.Words;

import java.util.Collections;

public class MainFragment extends Fragment implements AudioService.OnAudioService, WordAdapter.OnRow{

    public interface OnMainFragmentListener {
        void onAddWord();
        void onDeleteWord(Word word);
        void onUpdateWord(Word word);
        void onStartSelected();
    }

    private OnMainFragmentListener listener;

    private FloatingActionButton fab;
    private Words words;
    private WordAdapter adapter;
    private RecyclerView rcWords;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.select_all:
                    selectAll();
                    return true;
                case R.id.unselect_all:
                    unselectAll();
                    return true;
/*
                case R.id.delete_selected:
                    deleteSelected();
                    return true;
*/
                case R.id.start_selected:
                    if(listener != null) listener.onStartSelected();
                    return true;
                case R.id.add_new_word:
                    if(listener != null) listener.onAddWord();
                    return true;

            }
            return false;
        }
    };


    public MainFragment() {
    }
   public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle data){
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rcWords = (RecyclerView) view.findViewById(R.id.rcWords);
        rcWords.setHasFixedSize(true);
        rcWords.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcWords.getContext(), manager.getOrientation());
        rcWords.addItemDecoration(dividerItemDecoration);

        BottomNavigationView navigation = (BottomNavigationView) view.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainFragmentListener) {
            listener = (OnMainFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMainFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void selectAll(){
        for(Word w : words.getData()){
            w.setSelected(true);
            words.updateWord(w);
        }
    }
    public void unselectAll(){
        for(Word w : words.getData()){
            w.setSelected(false);
            words.updateWord(w);
        }
    }
    public void deleteSelected(){
        for(Word w: words.getData()){
            if(w.isSelected()) words.deleteWord(w);
        }
    }

    @Override
    public void onLoaded(Words words) {
        this.words = words;
        adapter = new WordAdapter(this.words);
        adapter.setOnRowListener(this);
        rcWords.setAdapter(adapter);
    }
    @Override
    public void onRowClick(Word word) {
        if(listener != null) listener.onUpdateWord(word);
    }

    @Override
    public void onRowLongClick(Word word) {
        if(listener != null) listener.onDeleteWord(word);
    }

}
