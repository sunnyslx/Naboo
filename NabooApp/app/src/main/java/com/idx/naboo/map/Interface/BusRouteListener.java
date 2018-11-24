package com.idx.naboo.map.Interface;

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;

/**
 * Created by hayden on 18-4-27.
 */

public interface BusRouteListener {
    void clickItem(int pos, BusPath item, BusRouteResult result, String startName, String endName);
}
