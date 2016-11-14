package chanathip.gotogether;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateNewGroupActivity extends AppCompatActivity {
    @BindView(R.id.textinputlayout_textgroupname)
    TextInputLayout _textgroupname;
    @BindView(R.id.textinputlayout_textgroupdescription)
    TextInputLayout _textgroupdescription;

    private UserData userData;
    private GroupData groupData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);

        ButterKnife.bind(this);

        userData = new UserData();
        groupData = new GroupData();

        //get information from bundle
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                userData.UserUid = null;
            } else {
                userData.UserUid = extras.getString("userUid");
            }
        } else {
            userData.UserUid = (String) savedInstanceState.getSerializable("userUid");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //noinspection ConstantConditions
        _textgroupdescription.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(_textgroupdescription.getEditText().getLayout().getLineCount() > 3){
                    _textgroupdescription.getEditText().getText()
                            .delete(_textgroupdescription.getEditText().length() -1,
                                    _textgroupdescription.getEditText().length());
                    InputMethodManager ime = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    ime.hideSoftInputFromWindow(_textgroupdescription.getWindowToken(), 0);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString("userUid", userData.UserUid);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    @OnClick(R.id.btncreate)
    public void creategroup(){
        groupData.Name = _textgroupname.getEditText().getText().toString();
        groupData.Description = _textgroupdescription.getEditText().getText().toString();

        if (CheckGroupData()) {
            Snackbar snackbar = Snackbar.make(_textgroupname, "Register...", Snackbar.LENGTH_SHORT);
            snackbar.show();

            DatabaseReference groupdatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups");
            String key = groupdatabaseReference.push().getKey();
            groupdatabaseReference.child(key).child("name").setValue(groupData.Name);
            groupdatabaseReference.child(key).child("description").setValue(groupData.Description);
            groupdatabaseReference.child(key).child("member").child(userData.UserUid).setValue("true");

            DatabaseReference userdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(userData.UserUid).child("group");
            userdatabaseReference.child(key).setValue("leader");

            onBackPressed();
        }
    }

    private boolean CheckGroupData(){
        if (groupData.Name.length() == 0) {
            _textgroupname.setError("please type group name");
            Snackbar snackbar =
                    Snackbar.make(_textgroupname, "Please input group name", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (groupData.Description.length() == 0) {
            _textgroupdescription.setError("please type group description");
            Snackbar snackbar =
                    Snackbar.make(_textgroupname, "Please input group description", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        }
        return true;
    }
}
