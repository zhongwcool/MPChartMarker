package com.alex.mpchart.marker.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alex.mpchart.marker.data.model.KLineEntry;
import com.alex.mpchart.marker.data.repository.KLineRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private final KLineRepository repository = new KLineRepository();
    private final LiveData<List<KLineEntry>> kLineData = repository.getKLineData();

    public LiveData<List<KLineEntry>> getKLineData() {
        return kLineData;
    }
}