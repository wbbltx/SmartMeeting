package com.newchinese.coolpensdk.listener;

/**
 * @anthor wubinbin
 * @time 2017/4/26 14:26
 */

public interface OnElectricityRequestListener  {

    /**
     * 电量信息
     * @param electricity
     */
    public abstract void onElectricityDetected(String electricity);
}
