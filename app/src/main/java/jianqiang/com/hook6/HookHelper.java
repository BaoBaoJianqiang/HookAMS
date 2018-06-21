package jianqiang.com.hook6;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @author weishu
 * @date 16/3/7
 */
public final class HookHelper {

    public static void hookActivityManager() {
        try {
            //获取AMN的gDefault单例gDefault，gDefault是静态的
            Object gDefault = RefInvoke.getStaticFieldObject("android.app.ActivityManagerNative", "gDefault");

            // gDefault是一个 android.util.Singleton对象; 我们取出这个单例里面的mInstance字段，IActivityManager类型
            Object rawIActivityManager = RefInvoke.getFieldObject(
                    "android.util.Singleton",
                    gDefault, "mInstance");


            // 创建一个这个对象的代理对象iActivityManagerInterface, 然后替换这个字段, 让我们的代理对象帮忙干活
            Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[] { iActivityManagerInterface },
                    new HookHandler(rawIActivityManager));

            //把Singleton的mInstance替换为proxy
            RefInvoke.setFieldObject("android.util.Singleton", gDefault, "mInstance", proxy);

        } catch (Exception e) {
            throw new RuntimeException("Hook Failed", e);
        }
    }
}
