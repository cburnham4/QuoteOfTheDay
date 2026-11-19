package letshangllc.quoteoftheday;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity {
    private static final String TAG = FriendsListActivity.class.getSimpleName();

    private ArrayList<Person> persons;
    private FriendsNameAdapter friendsNameAdapter;
    private ListView lvFriends;
    private AdsHelper adsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        View root = findViewById(R.id.activity_friends_list);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupViews();

        adsHelper = new AdsHelper(
                getWindow().getDecorView(),
                getString(R.string.admob_banner_friends),
                this
        );
        adsHelper.runAds();
    }

    public void setupViews() {
        lvFriends = findViewById(R.id.lv_friends);

        persons = new PreferencesManager(this).getPeople();
        friendsNameAdapter = new FriendsNameAdapter(this, persons);

        Log.i(TAG, persons.size() + "");

        lvFriends.setAdapter(friendsNameAdapter);
        registerForContextMenu(lvFriends);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_friends_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info == null) {
            return super.onContextItemSelected(item);
        }
        if (item.getItemId() == R.id.action_remove_friend) {
            Person person = persons.get(info.position);
            new PreferencesManager(this).removePerson(person, info.position);
            persons.remove(person);
            friendsNameAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adsHelper != null) {
            adsHelper.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adsHelper != null) {
            adsHelper.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (adsHelper != null) {
            adsHelper.onDestroy();
        }
        super.onDestroy();
    }
}
