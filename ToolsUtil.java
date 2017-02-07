package jp.co.pasonacareer.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.InvocationTargetException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;


/**
 * Created by xiejinpeng on 16/3/29.
 */
public class ToolsUtil {

    public static void hideSoftKeyBoard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static class DisplayUtil {
        public static int px2dp(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }

        public static int dp2px(Context context, float dipValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5f);
        }

        public static int px2sp(Context context, float pxValue) {
            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (pxValue / fontScale + 0.5f);
        }

        public static int sp2px(Context context, float spValue) {
            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (spValue * fontScale + 0.5f);
        }
    }

    public static class ModelUtil {

        /**
         * Separate string by separator.
         *
         * eg: separator = "_"
         * <ul>
         *   <li>someFieldName ---> some_Field_Name</li>
         *   <li>_someFieldName ---> _some_Field_Name</li>
         *   <li>aStringField ---> a_String_Field</li>
         *   <li>aURL ---> a_U_R_L</li>
         * </ul>
         *
         * @param string      the origin string
         * @param separator   the separator
         * @return the separate string.
         */
        public static String separateCamelCase(String string, String separator) {
            StringBuilder translation = new StringBuilder();
            char oldChar = 0;
            for (int i = 0; i < string.length(); i++) {
                char character = string.charAt(i);
                String s = string.substring(0, i);
                if (!s.endsWith(separator) && Character.isUpperCase(character) && translation.length() != 0) {
                    translation.append(separator);
                }
                translation.append(character);
                oldChar = character;
            }
            return translation.toString();
        }


    public static class StringUtil {


        /**
         * 引数に渡した文字列を全角カタカナのみの文字列に変換する
         */
        public static String convertToKatakana(@NonNull String target) {
            if (target.isEmpty()) {
                return target;
            }

            StringBuilder builder = new StringBuilder(convertToFullWidth(target));
            for (int i = 0; i < builder.length(); i++) {
                char c = builder.charAt(i);
                if (c >= 'ぁ' && c <= 'ん') {
                    builder.setCharAt(i, (char) (c - 'ぁ' + 'ァ'));
                }
            }
            return removeNotKatakana(builder.toString());
        }

        /**
         * 半角 -> 全角
         */
        public static String convertToFullWidth(@NonNull String target) {
            return Normalizer.normalize(target, Normalizer.Form.NFKC);
        }

        /**
         * カタカナ以外の文字を排除する
         */

        public static String removeNotKatakana(@NonNull String target) {
            return target.replaceAll("[^ァ-ー]+", "");
        }
    }

        /**
        *  readJsonFile From Asset
        */

	    public static <T> T getJsonFromAssets(String fileName ,  Class<T> classOfT) {
        String json = "";
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        model = new SearchConditionModel();
        Gson gson = new Gson();
        model = gson.fromJson(json, classOfT);
    }
    /**
     * Gets string from assets file.
     *
     * @param resources the resources
     * @param fileName  the assets file name
     * @return the string from assets file
     * @throws IOException the io exception
     */
    public static String getStringFromAssets(Resources resources, String fileName) throws IOException {
            InputStream is = resources.getAssets().open(fileName);
            BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LogUtil.e(e);
            return "";
        }

        StringBuilder sb = new StringBuilder();

        try {
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException e) {
            LogUtil.e(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LogUtil.e(e);
            }
        }

        return sb.toString();
    }

}

    /**
     *  add thousandBit for num
     *  eg: 1000 - > 1,000
     */

    public static String addThousandBit(int num) {
        String text = String.valueOf(num).replaceAll(",","");
        text = text.replaceAll("(?<=\\d)(?=(?:\\d{3})+$)", ",");
        return text;
    }

    public static String addThousandBit(String num) {
        String text = num.replaceAll(",", "");
        text = text.replaceAll("(?<=\\d)(?=(?:\\d{3})+$)", ",");
        return text;
    }

    public static class RxBus {
    private Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    private static RxBus rxBus;
    private RxBus(){}

    public static RxBus getInstance(){
        if(rxBus == null)
            synchronized (RxBus.class){
                if(rxBus == null)
                    rxBus = new RxBus();
            }
        return rxBus;
    }

    public void send(Object event){
        bus.onNext(event);
    }

    public <T> Observable<T> toObservable(Class<T> clazz){
        return bus.asObservable()
                .filter(clazz::isInstance)
                .map(o -> (T)o)
                .doOnError(LogUtil::e)
                .observeOn(AndroidSchedulers.mainThread());
    }
}

public static class LogUtil {
    private static boolean debug = BuildConfig.DEBUG;

    private LogUtil(){}

    public static void setDebug(boolean debug) {
        LogUtil.debug = debug;
    }

    public static void m(String tag, String msg) {
        if(debug)
            System.out.println(tag + "| " + msg);
    }

    public static void m(Class clazz, String msg) {
        m(clazz.getSimpleName(), msg);
    }

    public static void m(Object from, String msg) {
        m(from.getClass().getSimpleName(), msg);
    }

    public static void m(String msg) {
        if(debug)
            System.out.println(msg);
    }

    public static void e(Throwable t) {
        if(debug)
            t.printStackTrace();
    }
}

}
