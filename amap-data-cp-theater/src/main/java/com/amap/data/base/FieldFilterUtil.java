package com.amap.data.base;

import java.util.ArrayList;
import java.util.List;

import com.amap.data.base.fieldfilter.RegexFilter;
import com.amap.data.base.fieldfilter.SecretFilter;
import com.amap.data.base.fieldfilter.ValueListFilter;

public class FieldFilterUtil {

    public static List<FieldFilter> genFieldFilterList(TempletConfig templet) {
        List<FieldFilter> lff = new ArrayList<FieldFilter>();

        // 过滤
        if (templet.getString("value_filter_field") != null) {
            FieldFilter fnm = new ValueListFilter(templet);
            lff.add(fnm);
        }

        //涉密过滤
        if (templet.getString("secret_filter_name_field") != null) {
            FieldFilter fnm = new SecretFilter(templet);
            lff.add(fnm);
        }

        //正则过滤
        if (templet.getString("regex_filter") != null) {
            FieldFilter fnm = new RegexFilter(templet);
            lff.add(fnm);
        }
        return lff;
    }

}
