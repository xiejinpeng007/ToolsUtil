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

import indi.yume.tools.autosharedpref.model.FieldEntity;
import indi.yume.tools.autosharedpref.util.ReflectUtil;

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
         * Convert model to Map<String, String>.
         *
         * @param model the model object
         * @return the map of filed name and value.
         * @throws NoSuchMethodException     the no such method exception
         * @throws IllegalAccessException    the illegal access exception
         * @throws InvocationTargetException the invocation target exception
         */
        public static Map<String, String> convertModel2StringMap(Object model) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
            Map<String, FieldEntity> map = ReflectUtil.getFiledAndValue(model);

            Map<String, String> valueMap = new IdentityHashMap<>();
            for (Map.Entry<String, FieldEntity> entry : map.entrySet()) {
                Object object = entry.getValue().getValue();
                if (object != null) {
                    String[] valueList = String.valueOf(object).split(",");
                    for (String value : valueList)
                        valueMap.put(
                                separateCamelCase(entry.getKey(), "_").toLowerCase(),
                                value);
                }
            }

            return valueMap;
        }

        /**
         * Convert model to Map<String, Object> has all field.
         *
         * @param model the model object
         * @return the map of filed name and value.
         * @throws NoSuchMethodException     the no such method exception
         * @throws IllegalAccessException    the illegal access exception
         * @throws InvocationTargetException the invocation target exception
         */
        public static Map<String, Object> convertModel2ObjectMap(Object model) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
            Map<String, FieldEntity> map = ReflectUtil.getFiledAndValue(model);

            Map<String, Object> valueMap = new HashMap<>();
            for (Map.Entry<String, FieldEntity> entry : map.entrySet())
                valueMap.put(entry.getKey(), entry.getValue().getValue());

            return valueMap;
        }

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
}
