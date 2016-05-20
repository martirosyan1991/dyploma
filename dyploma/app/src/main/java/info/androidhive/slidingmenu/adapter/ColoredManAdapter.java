package info.androidhive.slidingmenu.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyploma.garik.dyploma.R;

import java.util.ArrayList;

/**
 * Created by Harry on 18.05.2016.
 */
public class ColoredManAdapter extends ArrayAdapter<ImageView> {

    private final Context context;
    private final ArrayList<ImageView> menArrayList;

    public ColoredManAdapter(Context context, ArrayList<ImageView> menArrayList) {
        super(context, R.layout.one_colored_man, menArrayList);
        this.context = context;
        this.menArrayList = menArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView = inflater.inflate(R.layout.one_colored_man, parent, false);

        // 3. Get icon,title & counter views from the rowView
        ImageView imgView = menArrayList.get(position);

        // 4. Set the text for textView

        int mycolor = context.getResources().getColor(R.color.colorPrimary);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.green_man).mutate();
        //drawable.setColorFilter(mycolor, PorterDuff.Mode.SRC_IN);
        //imgView.setImageDrawable(drawable);
        ((ImageView) rowView.findViewById(R.id.colored_man_imageview)).setImageDrawable(imgView.getDrawable());

        // 5. retrn rowView
        return rowView;
    }
}
