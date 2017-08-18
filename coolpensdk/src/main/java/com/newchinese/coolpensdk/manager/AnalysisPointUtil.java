package com.newchinese.coolpensdk.manager;


import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.coolpensdk.utils.SystemTransformUtils;

/**
 * Description:   解析蓝牙原始数据为NotePoint
 * author         xulei
 * Date           2017/4/11 14:08
 */
class AnalysisPointUtil {
    public static AnalysisPointUtil getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static AnalysisPointUtil instance = new AnalysisPointUtil();
    }

    /**
     * 解析成浮点型 x,y坐标；解析 string数据为integer数据，再变成二级制
     *
     * @param src 元数据
     * @return
     */
    public float[] getAxisByRawString(String src) {
        float[] result = new float[2];
        float tempX = 0;
        float tempY = 0;
        //下面的方法将蓝牙传过来的数据当做16进制数解析为2进制数，以字符串形式存在
        String str = SystemTransformUtils.parseHexStrToBinaryStr(src, 16);
        //下面的操作时将2进制数转转为我们人类熟悉的10进制
        char[] chars = str.toCharArray();
        //判断是我加的，避免去解析42和45
        int float_num = 8;//小数部分长8位
        int int_num = 14;//整数部分长14位
        int total_num = 63;//报文+预留+44位点数据在角标为63位处终止

        //X integer 因为要取和，所以要从最低位开始加，不取=是对的
        int inx_start = 63;//x整数的最低位在63位
        for (int i = inx_start; i > inx_start - int_num; i--) {
            if (chars[i] == '1') {
                tempX += Math.pow(2, inx_start - i);
            }
        }

        //Y integer 同上
        int iny_start = 63 - 14;//y整数的最低位在63-14==49
        for (int i = iny_start; i > iny_start - int_num; i--) {
            if (chars[i] == '1') {
                tempY += Math.pow(2, iny_start - i);
            }
        }

        //X decimal fraction 同上
        int flx_start = 63 - 14 - 14;//x小数的最低位在63-14-14位：35
        for (int i = flx_start; i > flx_start - float_num; i--) {
            if (chars[i] == '1') {
                tempX += Math.pow(2, (flx_start - i) - float_num + 1);
            }
        }

        //Y decimal fraction同上
        int fly_start = 63 - 14 - 14 - 8;//y小数的最低位在63-14-14-8位：27
        for (int i = fly_start; i > fly_start - float_num; i--) {
            if (chars[i] == '1') {
                tempY += Math.pow(2, (fly_start - i) - float_num + 1);
            }
        }
        //和取完，得到十进制数的x、y原始坐标
        result[0] = tempX;
        result[1] = tempY;
        return result;
    }

    /**
     * 解析成Point对象，包括 x,y,press,RGB
     *
     * @param src 元数据
     * @return
     */
    public NotePoint parseStringToBean(String src) {
        float baseXOffset = LogicController.getInstance().getBaseXOffset();
        float baseYOffset = LogicController.getInstance().getBaseYOffset();
        //将笔传来的16进制的字符串转换为2进制的字符串
        String binaryStr = SystemTransformUtils.parseHexStrToBinaryStr(src, 16);
        //有时候会连传两个16位的16进制数，变成二级制数组为64位然后报错
        char[] chars = binaryStr.toCharArray();
        //带有点信息的点是26位的16进制数，变成二级制数组为104位
        NotePoint point = new NotePoint();

        //初始压力值解析
        int press_num = 8;
        int first_press_start = 64 + 8 - 1;
        int first_press_end = 64;//第64、65、66、67、68、69、70、71为是初始压力位？
        float first_tempPress_y = 0;
        //修改i >press_end||i == press_end，异常变为了canvarview 415行 null异常
        for (int i = first_press_start; i > first_press_end || i == first_press_end; i--) {
            if (chars[i] == '1') {
                first_tempPress_y += Math.pow(2, first_press_start - i);
            }
        }

        //实际压力值解析
        int press_start = 72 + 8 - 1;
        int press_end = 72;//第72、73、74、75、76、77、78、79为是实际压力位？
        float tempPress_y = 0;
//java.lang.ArrayIndexOutOfBoundsException: length=64; index=71;因为有时候收到的点是64位的。是我对442,445的处理不当吗
//           我修改成i >press_end||i == press_end，异常变为了canvarview 415行 null异常
        for (int i = press_start; i > press_end || i == press_end; i--) {
            if (chars[i] == '1') {
                tempPress_y += Math.pow(2, press_start - i);
            }
        }

        //时间解析
        int testTime_start = 80 + 8 - 1;
        int testTime_end = 80;//第72、73、74、75、76、77、78、79为是实际压力位？
        float testTime_y = 0;
//java.lang.ArrayIndexOutOfBoundsException: length=64; index=71;因为有时候收到的点是64位的。是我对442,445的处理不当吗
//           我修改成i >press_end||i == press_end，异常变为了canvarview 415行 null异常
        for (int i = testTime_start; i > testTime_end || i == testTime_end; i--) {
            if (chars[i] == '1') {
                testTime_y += Math.pow(2, testTime_start - i);
            }
        }

        //X,Y坐标解析
        float[] axisByRawString = getAxisByRawString(src);

        //书写页数解析
        int pageDesIndex = getPageDesIndex(axisByRawString[0], axisByRawString[1] + baseYOffset, Constant.ACTIVE_PAGE_X, Constant.ACTIVE_PAGE_Y);

        point.setTestTime(testTime_y);
        point.setFirstPress(first_tempPress_y);
        point.setPress(tempPress_y);
        point.setPX(axisByRawString[0] + baseXOffset);
        point.setPY(axisByRawString[1] + baseYOffset);
        point.setPageIndex(pageDesIndex);
        return point;
    }

    /**
     * 根据 x,y坐标点，和一页的大小，计算出该坐标点在Book中所在的页数
     *
     * @param x
     * @param y
     * @param pageX
     * @param pageY
     * @return
     */
    public int getPageDesIndex(float x, float y, float pageX, float pageY) {//前两个是原始坐标xy，后两个是页宽高

        int AXIS_X = (int) Math.floor(Constant.POINT_MAX_X / pageX);//【Math.floor浮点数向下取整】Constant.POINT_MAX_X / pageX ---得到的值是n余m，n是坐标系在x向横跨的页数，存在m说明最后有个半页。向下取整，表明认为半页不算独立的一页;x向总页数
        int AXIS_Y = (int) Math.floor(Constant.POINT_MAX_Y / pageY);//竖向页数

        int current_num_X = (int) Math.ceil(x / pageX);//【Math.ceil浮点数向上取整】 x / pageX ---得到的值是n余m，n是这点在x向横跨的页，存在m说明x落在n+1 页 。 所以向上取整 得到x向的页位current_num_X
        int current_num_Y = (int) Math.ceil(y / pageY);//【Math.ceil浮点数向上取整】 y / pageY ---得到的值是n余m，n是这点在y向横跨的页，存在m说明x落在n+1 页 。 所以向上取整 得到y向的页位current_num_Y

        int pageIndex = (current_num_Y - 1) * AXIS_X + current_num_X;//（点所在竖向页位-1）*AXIS_X是每行总页数---得到这点所在的行 在大坐标系上方区域的总页数；current_num_X是点在所在行的以左边第一页为第一页的页数；两者和就是点所在页数

        return pageIndex;
        /**
         *  这种算法下，打坐标每行的组后一页的区域会比实际给定值大一些。所以如果厂家切页时每行最后多余的边角不切的话，把他作为下一页的一部分切出的话，就会出现一页上出现两个页码的情况
         *  在x为109的情况下，这种情况下 16000/109=146.78 。故此错页预计第一次出现在147页，本页会有146和147页，后面会一直乱页
         */
        /**
         *  本app中连点用的都是原始点坐标xy，没用过转换当前页坐标，记住这一点。这也是画线会偶尔出现飞出当前页又飞回来的场景  的 原因之一
         */
    }
}
