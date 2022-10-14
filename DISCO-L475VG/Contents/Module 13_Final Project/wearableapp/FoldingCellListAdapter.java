package inf.mobileintelligent.wearableapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ramotion.foldingcell.FoldingCell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FoldingCellListAdapter extends ArrayAdapter<HistoryItem> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private HashMap<String, Integer> avatorMap = new HashMap<>();
    private HashMap<String, Integer> colorMap = new HashMap<>();

    public FoldingCellListAdapter(Context context, List<HistoryItem> objects) {
        super(context, 0, objects);
        avatorMap.put("still", R.drawable.stand);
        avatorMap.put("run", R.drawable.run);
        avatorMap.put("walk", R.drawable.walk);

        colorMap.put("still", R.color.main_light);
        colorMap.put("walk", R.color.main);
        colorMap.put("run", R.color.main_dark);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // get item for selected view
        HistoryItem item = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell, parent, false);
            // binding view parts to view holder

            viewHolder.contentActivity = cell.findViewById(R.id.content_activity);
            viewHolder.contentConfidence = cell.findViewById(R.id.content_confidence);
            viewHolder.contentCount = cell.findViewById(R.id.content_count);
            viewHolder.contentStartTime = cell.findViewById(R.id.content_start_time);
            viewHolder.contentEndTime = cell.findViewById(R.id.content_end_time);
            viewHolder.contentId = cell.findViewById(R.id.content_id);
            viewHolder.titleActivity = cell.findViewById(R.id.title_activity);
            viewHolder.titleStartTime = cell.findViewById(R.id.title_start_time);
            viewHolder.titleEndTime = cell.findViewById(R.id.title_end_time);
            viewHolder.contentAvatar = cell.findViewById(R.id.content_avatar);
            viewHolder.titleLayout = cell.findViewById(R.id.title_layout);

            cell.setTag(viewHolder);
        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        if (null == item)
            return cell;

        // bind data from selected element to view through view holder
        viewHolder.contentId.setText(item.getId());
        viewHolder.contentStartTime.setText(item.getStartTime());
        viewHolder.contentEndTime.setText(item.getEndTime());
        viewHolder.contentCount.setText(item.getCount());
        viewHolder.contentConfidence.setText(String.format("%.1f", item.getConfidence()*100) + "%");
        viewHolder.contentId.setText("# " + item.getId());
        viewHolder.contentActivity.setText(item.getActivity());
        viewHolder.titleStartTime.setText(item.getStartTime());
        viewHolder.titleEndTime.setText(item.getEndTime());
        viewHolder.titleActivity.setText(item.getActivity());
        viewHolder.contentAvatar.setImageResource(avatorMap.get(item.getActivity()));
        viewHolder.titleLayout.setBackgroundResource(colorMap.get(item.getActivity()));
        return cell;
    }

    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }
    // View lookup cache
    private static class ViewHolder {
        TextView titleStartTime;
        TextView titleEndTime;
        TextView titleActivity;
        TextView contentId;
        TextView contentActivity;
        TextView contentStartTime;
        TextView contentEndTime;
        TextView contentConfidence;
        TextView contentCount;
        ImageView contentAvatar;
        RelativeLayout titleLayout;
    }
}
