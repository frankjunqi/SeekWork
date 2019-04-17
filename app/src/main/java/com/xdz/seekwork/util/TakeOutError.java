package com.xdz.seekwork.util;

import java.io.Serializable;

/**
 * 出货异常错误类
 * Created by kjh08490 on 2016/12/14.
 */

public class TakeOutError implements Serializable {

    // 此人消费次数未满，可以进行消费
    private static final String CAN_TAKEOUT = "操作成功，谢谢.";
    public static final int CAN_TAKEOUT_FLAG = 1;

    // 此人消费次数已满，不可以进行消费
    private static final String FAILE_TAKEOUT = "此人消费次数已满，不可以进行消费.";
    public static final int FAILE_TAKEOUT_FLAG = 2;
    // 此商品暂时没有赋予出货权限
    private static final String PRO_HAS_NOPOWER = "此商品暂时没有赋予出货权限.";
    public static final int PRO_HAS_NOPOWER_FLAG = 3;

    // 无此员工 --- 失败页面
    private static final String HAS_NOEMPLOYEE = "此卡没有对应员工编号，无法消费，联系管理员，然后再来重新刷卡.";
    public static final int HAS_NOEMPLOYEE_FLAG = 4;

    // 此人无权限
    private static final String HAS_NOPOWER = "此卡无权限,请换卡重新刷卡...";
    public static final int HAS_NOPOWER_FLAG = 5;

    // 串口打开螺纹柜子失败
    private static final String OPEN_LUOWEN_SERIAL_FAILED = "串口打开螺纹柜子失败...";
    public static final int OPEN_LUOWEN_SERIAL_FAILED_FLAG = 6;

    // 串口打开螺纹柜子失败
    private static final String OPEN_GEZI_SERIAL_FAILED = "串口打开格子柜子失败...";
    public static final int OPEN_GEZI_SERIAL_FAILED_FLAG = 7;

    private int takeOutFLag;

    // 接口返回的错误信息
    public String serverMsg = "";

    public TakeOutError(int takeOutFLag) {
        this.takeOutFLag = takeOutFLag;
    }

    /**
     * 获取描述信息
     *
     * @return
     */
    public String getTakeOutMsg() {
        switch (takeOutFLag) {
            case CAN_TAKEOUT_FLAG:
                return CAN_TAKEOUT;
            case FAILE_TAKEOUT_FLAG:
                return FAILE_TAKEOUT;
            case PRO_HAS_NOPOWER_FLAG:
                return PRO_HAS_NOPOWER;
            case HAS_NOEMPLOYEE_FLAG:
                return HAS_NOEMPLOYEE;
            case HAS_NOPOWER_FLAG:
                return HAS_NOPOWER;
            case OPEN_LUOWEN_SERIAL_FAILED_FLAG:
                return OPEN_LUOWEN_SERIAL_FAILED;
            case OPEN_GEZI_SERIAL_FAILED_FLAG:
                return OPEN_GEZI_SERIAL_FAILED;
            default:
                return FAILE_TAKEOUT;
        }
    }

    /**
     * 判断是否消费成功
     *
     * @return
     */
    public boolean isSuccess() {
        return takeOutFLag == 1;
    }

}
