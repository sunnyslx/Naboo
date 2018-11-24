package com.idx.naboo.service.listener;

import com.idx.naboo.service.SessionState;

/**
 * Created by derik on 18-4-16.
 * Email: weilai0314@163.com
 */

public interface DataListener {
    void onJsonReceived(String json);

    void onSessionStateChanged(SessionState state);
}
