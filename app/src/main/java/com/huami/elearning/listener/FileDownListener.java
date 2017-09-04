package com.huami.elearning.listener;

import com.huami.elearning.model.FileDownInfo;

/**
 * Created by Henry on 2017/8/24.
 */

public interface FileDownListener {
    void downSuccess(FileDownInfo info);
    void downError(FileDownInfo info);
    void downFailure(FileDownInfo info);
}
