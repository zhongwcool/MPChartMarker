package com.alex.mpchart.marker.data.model;

import java.util.List;

public class MarkerResponse {
    public List<MarkerItem> items;
    public int total;
    public int page;
    public int size;
    public int pages;

    public static class MarkerItem {
        public String updated_at;
        public String flag_at;      // 标记日期，格式：YYYY-MM-DD
        public int type;            // 标记类型：0-数字标记，1-买入，2-卖出，3-上三角，4-下三角
        public String extra;        // 额外信息，如标记文本内容

        public MarkerItem() {
        }

        public MarkerItem(String updated_at, String flag_at, int type, String extra) {
            this.updated_at = updated_at;
            this.flag_at = flag_at;
            this.type = type;
            this.extra = extra;
        }
    }
} 