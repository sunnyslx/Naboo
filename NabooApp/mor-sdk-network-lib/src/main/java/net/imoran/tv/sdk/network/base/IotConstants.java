package net.imoran.tv.sdk.network.base;

import net.imoran.tv.sdk.network.BuildConfig;

/*****************************************************
 * iot家居控制的相关接口
 * Created by bobge on 2018/4/12.
 *****************************************************
 */
public class IotConstants {
    public static String BaseUrlDebug = "http://39.106.107.61:8100/IoT-user";
    public static String BaseUrlDebug2 = "http://47.95.114.20:8011";

    public static String BaseUrlRelease = "http://api.xiaomor.com";

    public static String BaseUrl = BuildConfig.DEBUG ? BaseUrlDebug : BaseUrlDebug;
    public static String BaseUrl2 = BuildConfig.DEBUG ? BaseUrlDebug2 : BaseUrlDebug2;

    /**
     * 获取所有支持的设备类型接口1
     * /device/typeList
     */
    public static String URL_IOT_DEVICE_TYPELIST = BaseUrl + "/device/typeList";
    /**
     * 网关绑定、解绑接口2、3
     */
    public static String URL_IOT_USER_UNBIND = BaseUrl + "/user/unbind";
    public static String URL_IOT_USER_BIND = BaseUrl + "/user/bind";
    /**
     * 获取用户下的设备列表4
     */
    public static String URL_IOT_DEVICE_DEVICELIST = BaseUrl + "/device/deviceList";
    /**
     * 更新设备信息5
     */
    public static String URL_IOT_DEVICE_UPDATE = BaseUrl + "/device/update";
    /**
     * 更新设备位置6
     */
    public static String URL_IOT_DEVICE_UPDATEPOSITION = BaseUrl + "/device/updatePosition";
    /**
     * 保存或更新个性化配置7
     */
    public static String URL_IOT_SCENE_SAVEORUPDATE = BaseUrl + "/scene/saveOrUpdate";
    /**
     * 删除个性化配置8
     */
    public static String URL_IOT_SCENE_DELETE = BaseUrl + "/scene/delete";
    /**
     * 获取个性化配置列表9
     */
    public static String URL_IOT_SCENE_SCENELIST = BaseUrl + "/scene/sceneList";

    /**
     * 获取 access token接口， 用于后续所有接口的访问， token有效期，目前设置为半天。
     */
    public static String URL_IOT_OAUTH_TOKEN = BaseUrl2 + "/iot/oauth2/token";

    /**
     * OTA升级接口用于查找用户在线升级设备，通过此接口将 设备的升级信息下发到需要升级的设备。
     */
    public static String URL_IOT_PUSH_MSG = BaseUrl2 + "/iot/ota/pushmsg";


    /**
     * APP，家居skills平台下发删除网关指令到到IoTCloud- service，会将指定网关下的子设备删除
     */
    public static String URL_IOT_GATEWAY_DELDEVICE = BaseUrl2 + "/iot/gateway/device/delDevice";

    /**
     * 家居skills平台下发设备控制指令到到IoTCloud- service， IoTCloud-service会将控制指令映射成具体设备 的控制指令， 通过MQTT消息中间件传送到网关和设备执 行。
     */
    public static String URL_IOT_GATEWAY_DEVICECONTR = BaseUrl2 + "/iot/gateway/device/deviceContr";

    /**
     * 下发查询指令查询设备状态
     */
    public static String URL_IOT_GATEWAY_DEVICEINFO = BaseUrl2 + "/iot/gateway/device/getDeviceInf";

}


