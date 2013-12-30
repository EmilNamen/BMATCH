package co.bmatch;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.bmatch.ChatField;

public class ChatAdapter extends ArrayAdapter<ChatField>{

	private TextView countryName;
	private List<ChatField> countries = new ArrayList<ChatField>();
	private LinearLayout wrapper;

	@Override
	public void add(ChatField object) {
		countries.add(object);
		super.add(object);
	}

	public ChatAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.countries.size();
	}

	public ChatField getItem(int index) {
		return this.countries.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.chat_intern, parent, false);
		}

		wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

		ChatField coment = getItem(position);

		countryName = (TextView) row.findViewById(R.id.comment);

		countryName.setText(coment.comment);

		countryName.setBackgroundResource(coment.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
		wrapper.setGravity(coment.left ? Gravity.LEFT : Gravity.RIGHT);

		return row;
	}
}
