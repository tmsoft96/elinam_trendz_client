package com.tmsoft.tm.elinamclient.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elinamclient.Adapters.SearchAdapter;
import com.tmsoft.tm.elinamclient.Holders.allProductsClass;
import com.tmsoft.tm.elinamclient.Holders.autofit;
import com.tmsoft.tm.elinamclient.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 0;
    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private EditText searchBox;
    private ImageButton seachMicroPhone;
    private RelativeLayout noProduct;
    private SwipeRefreshLayout refresh;

    private SpeechRecognizer speechRecognizer;

    private SearchAdapter searchAdapter;

    private ArrayList<allProductsClass> searchList;
    private ArrayList<String> key;
    private autofit noColums;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        myToolBar = findViewById(R.id.search_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Product");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.keepSynced(true);

        noColums = new autofit();
        noColums.autofit(getApplicationContext());

        int mNoofColums = noColums.getNoOfColumn();
        Log.i("Number", mNoofColums + "");

        recyclerView = findViewById(R.id.search_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, mNoofColums));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, GridLayoutManager.VERTICAL));


        searchBox = findViewById(R.id.search_SearchTextBox);
        seachMicroPhone = findViewById(R.id.search_microPhone);
        noProduct = findViewById(R.id.search_searchNoProduct);
        refresh = findViewById(R.id.search_refresh);

        checkProduct();
        displayAll();
        initializeRecognition();


        seachMicroPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForPermission();
            }
        });


        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (!TextUtils.isEmpty(editable.toString()))
                        createAdapter(editable.toString());
                    else
                        displayAll();
                } catch (Exception e){
                    Log.i("error", e.getMessage());
                }
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (TextUtils.isEmpty(searchBox.getText().toString())){
                    checkProduct();
                    displayAll();
                } else {
                    createAdapter(searchBox.getText().toString());
                }
                refresh.setRefreshing(false);
            }
        });

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH){
                    String text = searchBox.getText().toString();

                    if (TextUtils.isEmpty(text))
                        Toast.makeText(SearchActivity.this, "Nothing to search...", Toast.LENGTH_SHORT).show();
                    else {
                        createAdapter(text);
                    }

                    return true;
                }
                return false;
            }
        });

    }

    private void requestForPermission() {
        ArrayList<String> arrPerm = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED){
            arrPerm.add(Manifest.permission.RECORD_AUDIO);
        }

        if(!arrPerm.isEmpty()){
            String[] permissions = new String[arrPerm.size()];
            permissions = arrPerm.toArray(permissions);
            ActivityCompat.requestPermissions(SearchActivity.this, permissions, MY_PERMISSIONS_REQUEST);
        }
    }

    private void checkForPermission(){
        if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            speechRecognizer.startListening(intent);
            Toast.makeText(SearchActivity.this, "Listening, ask for product name or price", Toast.LENGTH_LONG).show();
        } else {
            requestForPermission();
        }

    }

    private void initializeRecognition() {
        if (SpeechRecognizer.isRecognitionAvailable(this)){
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float v) {

                }

                @Override
                public void onBufferReceived(byte[] bytes) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int i) {

                }

                @Override
                public void onResults(Bundle bundle) {
                    List<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(results.get(0));
                }

                @Override
                public void onPartialResults(Bundle bundle) {

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });
        }

    }

    private void processResult(String command) {
        Toast.makeText(this, "searching for (" + command + ")", Toast.LENGTH_SHORT).show();
        command.toLowerCase();
        if (command.indexOf("all") != -1){
            displayAll();
        } else if (command.indexOf("product") != -1){
            displayAll();
        } else {
            createAdapter(command);
        }
    }

    private void createAdapter(final String s) {
        searchList.clear();
        key.clear();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapShot : dataSnapshot.getChildren()){
                    String productSearchName = snapShot.child("ProductSearchName").getValue().toString();
                    String productPrice = snapShot.child("ProductPrice").getValue().toString();

                    if (productSearchName.toLowerCase().contains(s.toLowerCase())){
                        searchList.add(snapShot.getValue(allProductsClass.class));
                        key.add(snapShot.getKey());
                    } else if (productPrice.toUpperCase().contains(s.toUpperCase())){
                        searchList.add(snapShot.getValue(allProductsClass.class));
                        key.add(snapShot.getKey());
                    }
                }

                searchAdapter = new SearchAdapter(SearchActivity.this, searchList, key, noColums.getLayoutWidth(), noColums.getTestLength());
                recyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void checkDisplay(String search) {
        noProduct.setVisibility(View.VISIBLE);
        String getName = search.toLowerCase();

        Query categoryQuery = databaseReference.orderByChild("ProductSearchName")
                .startAt(getName).endAt(getName + "\uf8ff");

        categoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int num = (int) dataSnapshot.getChildrenCount();

                    if (num > 0)
                        noProduct.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkProduct() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int num = (int) dataSnapshot.getChildrenCount();

                    if (num > 0)
                        noProduct.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayAll() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchList = new ArrayList<>();
                key = new ArrayList<>();
                for (DataSnapshot snapShot : dataSnapshot.getChildren()){
                    searchList.add(snapShot.getValue(allProductsClass.class));
                    key.add(snapShot.getKey());
                }

                Collections.reverse(searchList);
                Collections.reverse(key);

                searchAdapter = new SearchAdapter(SearchActivity.this, searchList, key, noColums.getLayoutWidth(), noColums.getTestLength());
                recyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            closeKeyBoard();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
