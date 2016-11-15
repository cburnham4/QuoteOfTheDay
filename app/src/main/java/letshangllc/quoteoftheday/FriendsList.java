package letshangllc.quoteoftheday;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ListMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendsList extends AppCompatActivity {
    private static final String TAG = FriendsList.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        this.setupViews();

    }

    public void setupViews(){
        ListView lvFriends = (ListView) findViewById(R.id.lv_friends);

        ArrayList<Person> persons = new PreferencesManager(this).getPeople();

        FriendsNameAdapter friendsNameAdapter = new FriendsNameAdapter(this,
                new PreferencesManager(this).getPeople());

        Log.i(TAG,persons.size()+"");

        lvFriends.setAdapter(friendsNameAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
