package sg.edu.rp.webservices.firebasetransportify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Incident> {

    Context context;
    ArrayList<Incident> incidents;
    int resource;

    public CustomAdapter(@NonNull Context context, int resource, ArrayList<Incident> incidents) {
        super(context, resource, incidents);
        this.context = context;
        this.resource = resource;
        this.incidents = incidents;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(resource, parent, false);
        Incident currentIncident = incidents.get(position);
        TextView title = rowView.findViewById(R.id.title);
        TextView description = rowView.findViewById(R.id.description);

        title.setText(currentIncident.getType());
        description.setText(currentIncident.getMessage());

        return rowView;
    }
}