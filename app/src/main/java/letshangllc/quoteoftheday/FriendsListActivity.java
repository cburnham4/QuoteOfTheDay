package letshangllc.quoteoftheday;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity {
    private static final String TAG = FriendsListActivity.class.getSimpleName();

    private  ArrayList<Person> persons;
    private FriendsNameAdapter friendsNameAdapter;
    private ListView lvFriends;

    private AdsHelper adsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        this.setupViews();

        adsHelper = new AdsHelper(getWindow().getDecorView(), getResources().getString(R.string.admob_banner_main), this);
        adsHelper.runAds();

    }

    public void setupViews(){
        lvFriends = (ListView) findViewById(R.id.lv_friends);

        persons = new PreferencesManager(this).getPeople();

        friendsNameAdapter = new FriendsNameAdapter(this,
                persons);

        Log.i(TAG,persons.size()+"");

        lvFriends.setAdapter(friendsNameAdapter);
        registerForContextMenu(lvFriends);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(toolbar != null){getSupportActionBar().setDisplayHomeAsUpEnabled(true);}
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_friends_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_remove_friend:
                Person person = persons.get(info.position);
                new PreferencesManager(this).removePerson(person, info.position);
                persons.remove(person);
                friendsNameAdapter.notifyDataSetChanged();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
