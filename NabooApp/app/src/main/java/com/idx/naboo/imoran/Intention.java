package com.idx.naboo.imoran;

/**
 * Created by derik on 18-4-20.
 */

public interface Intention {
    String SEARCHING = "searching";  // 查询
    String SENDING = "sending"; //发短信/微信
    String LISTENING = "listening"; //播放音乐
    String INSTRUCTING = "instructing"; //指令
    String BOOKING = "booking"; // 预订
    String LOCATING = "locating"; //坐标定位
    String READING = "reading"; //朗读短信/微信
    String BLOCKING = "blocking"; //屏蔽
    String UNBLOCKING = "unblocking"; //取消屏蔽
    String ROUTING = "routing"; //路线规划
    String NAVIGATING = "navigating"; //导航
    String WATCHING = "watching"; //播放视频
    String DISPLAYING = "displaying"; //详情页展现
}
