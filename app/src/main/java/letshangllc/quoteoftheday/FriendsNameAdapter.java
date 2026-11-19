package letshangllc.quoteoftheday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class FriendsNameAdapter extends ArrayAdapter<Person> {

    private static class ViewHolder {
        TextView name;
        TextView number;
    }

    public FriendsNameAdapter(Context context, ArrayList<Person> persons) {
        super(context, R.layout.item_name, persons);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Person item = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_name, parent, false);
            viewHolder.name = convertView.findViewById(R.id.tv_itemName);
            viewHolder.number = convertView.findViewById(R.id.tv_number);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (item != null) {
            viewHolder.name.setText(item.getName());
            viewHolder.number.setText(item.getNumber());
        }
        return convertView;
    }
}
