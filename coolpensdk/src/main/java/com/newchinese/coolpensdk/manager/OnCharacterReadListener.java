package com.newchinese.coolpensdk.manager;

/**
 * @anthor wubinbin
 * @time 2017/5/4 14:22
 */

public abstract class OnCharacterReadListener{

    /**
     * 历史数据
     *
     * @param historicalInfo
     */
    public abstract void onReadHistoricalData(String historicalInfo);

    /**
     * 即时数据
     *
     * @param instantInfo
     */
    public abstract void onReadInstantData(String instantInfo);
}
