package kgec.group11.ui;

import kgec.group11.persistence.HistoryRecord;

public interface HistoryUpdateListener {
    void onRecordAdded(HistoryRecord record);
}
