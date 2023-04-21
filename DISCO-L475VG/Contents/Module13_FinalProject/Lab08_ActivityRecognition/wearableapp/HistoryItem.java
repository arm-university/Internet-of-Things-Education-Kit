package inf.mobileintelligent.wearableapp;

import androidx.annotation.Nullable;

public class HistoryItem {

    private String startTime;
    private String endTime;
    private Float confidence;
    private String activity;
    private String id;
    private String count;

    public HistoryItem(String activity, String startTime, String endTime, Float confidence, String id, String count){
        this.activity = activity;
        this.startTime = startTime;
        this.endTime = endTime;
        this.confidence = confidence;
        this.id = id;
        this.count = count;
    }

    public String getStartTime(){
        return startTime;
    }

    public Float getConfidence(){
        return confidence;
    }

    public String getEndTime() {
        return endTime;
    }
    public String getActivity(){
        return activity;
    }

    public String getCount() {
        return count;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        HistoryItem item = (HistoryItem) obj;
        if (startTime!=null ? !startTime.equals(item.getStartTime()): item.getStartTime() != null ) return false;
        if (endTime!= null ? !endTime.equals(item.getEndTime()): item.getEndTime() != null) return false;
        if (activity!= null ? !activity.equals(item.getActivity()): item.getActivity()!=null) return false;
        if (confidence!= null ? confidence.equals(item.getConfidence()) : item.getConfidence()!=null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(getId());
    }
}
