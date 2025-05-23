package com.alex.mpchart.marker.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alex.mpchart.marker.data.model.KLineEntry;
import com.alex.mpchart.marker.data.repository.KLineRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private final KLineRepository repository = new KLineRepository();
    private MediatorLiveData<List<KLineEntry>> kLineData;
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);

    public LiveData<List<KLineEntry>> getKLineData() {
        if (kLineData == null) {
            kLineData = new MediatorLiveData<>();
            loadKLineData();
        }
        return kLineData;
    }

    public LiveData<Boolean> getIsRefreshing() {
        return isRefreshing;
    }

    public void refreshData() {
        isRefreshing.setValue(true);
        loadKLineData();
    }

    private void loadKLineData() {
        LiveData<List<KLineEntry>> source = repository.getKLineData();
        kLineData.addSource(source, data -> {
            kLineData.setValue(data);
            isRefreshing.setValue(false);
            // 移除之前的数据源，避免重复观察
            kLineData.removeSource(source);
        });
    }
}