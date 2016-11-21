package letshangllc.quoteoftheday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Carl on 11/1/2016.
 */

public class FriendsNameAdapter extends ArrayAdapter<Person> {

    private ArrayList<Person> persons;

    private static class ViewHolder {
        TextView name;
        TextView number;
    }

    public FriendsNameAdapter(Context context, ArrayList<Person> persons) {
        super(context, R.layout.item_name, persons);
        this.persons = persons;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Person item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_name, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_itemName);
            viewHolder.number = (TextView) convertView.findViewById(R.id.tv_number);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.name.setText(item.getName());
        viewHolder.number.setText(item.getNumber());
        // Return the completed view to render on screen
        return convertView;
    }
}
