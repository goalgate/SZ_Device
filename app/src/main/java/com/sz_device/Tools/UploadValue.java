package com.sz_device.Tools;

/**
 * Created by zbsz on 2017/9/22.
 */

public class UploadValue {
    private Boolean isUploading = false;

    public synchronized Boolean getIsUploading() {
       return isUploading;
    }
    public synchronized void setIsUploading(Boolean value) {
        isUploading = value;
    }

}
