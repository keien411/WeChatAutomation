package per.edward.wechatautomationutil.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import per.edward.wechatautomationutil.model.TestModel;

/**
 * @author keien
 * @date 2018/10/17
 */
public class JsonUtils {
    /**
     *数据封装成json
     *
     * @param items 物料入库数据
     * @return json
     * @throws JSONException
     */
    public static String String2Json(List<TestModel> items) throws JSONException {
        if (items == null) return "";
        JSONArray array = new JSONArray();
        JSONObject jsonObject = null;
        TestModel info = null;
        for (int i = 0; i < items.size(); i++) {
            info = items.get(i);
            jsonObject = new JSONObject();
            jsonObject.put("name", info.getName());
            jsonObject.put("id", info.getId());
            array.put(jsonObject);
        }
        return array.toString();
    }

}
