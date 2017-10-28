package com.example.guo.linechartnotify.bean;

import java.util.List;

/**
 * Created by ${GuoZhaoHui} on 2017/9/21.
 * email:guozhaohui628@gmail.com
 */

public class ChartDayBean {


    /**
     * meta : {"success":true,"message":"ok"}
     * data : [{"saveTime":"2017-09-21 04:00:00.0","attribValue":0},{"saveTime":"2017-09-21 04:05:00.0","attribValue":0},{"saveTime":"2017-09-21 04:10:00.0","attribValue":0}]
     */

    private MetaBean meta;
    private List<DataBean> data;

    @Override
    public String toString() {
        return "ChartDayBean{" +
                "meta=" + meta +
                ", data=" + data +
                '}';
    }

    public MetaBean getMeta() {
        return meta;
    }

    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class MetaBean {
        /**
         * success : true
         * message : ok
         */

        private boolean success;
        private String message;

        @Override
        public String toString() {
            return "MetaBean{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    '}';
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class DataBean {
        /**
         * saveTime : 2017-09-21 04:00:00.0
         * attribValue : 0
         */

        private String saveTime;
        private int attribValue;

        @Override
        public String toString() {
            return "DataBean{" +
                    "saveTime='" + saveTime + '\'' +
                    ", attribValue=" + attribValue +
                    '}';
        }

        public String getSaveTime() {
            return saveTime;
        }

        public void setSaveTime(String saveTime) {
            this.saveTime = saveTime;
        }

        public int getAttribValue() {
            return attribValue;
        }

        public void setAttribValue(int attribValue) {
            this.attribValue = attribValue;
        }
    }
}
