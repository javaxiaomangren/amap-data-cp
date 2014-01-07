package com.amap.data.base;

import com.amap.data.base.fieldmap.AidMap;
import com.amap.data.base.fieldmap.CodeMap;
import com.amap.data.base.fieldmap.DefaultValueMap;
import com.amap.data.base.fieldmap.FieldnameMap;
import com.amap.data.base.fieldmap.HotelStarMap;
import com.amap.data.base.fieldmap.HtmlTrans;
import com.amap.data.base.fieldmap.ImgMap;
import com.amap.data.base.fieldmap.NameAllMap;
import com.amap.data.base.fieldmap.NameMap;
import com.amap.data.base.fieldmap.NewtypeMap;
import com.amap.data.base.fieldmap.NullTrans;
import com.amap.data.base.fieldmap.ObjectMap;
import com.amap.data.base.fieldmap.OptTypeMap;
import com.amap.data.base.fieldmap.PicInfoMap;
import com.amap.data.base.fieldmap.RegexMap;
import com.amap.data.base.fieldmap.SrcidMap;
import com.amap.data.base.fieldmap.StringMap;
import com.amap.data.base.fieldmap.SubFieldMap;
import com.amap.data.base.fieldmap.TelMap;
import com.amap.data.base.fieldmap.TimeFormatMap;
import com.amap.data.base.fieldmap.TimeMap;

import java.util.ArrayList;
import java.util.List;

public class FieldMapUtil {

    public static List<FieldMap> genFieldMapList(TempletConfig templet) {
        List<FieldMap> lfm = new ArrayList<FieldMap>();

        // 字段名称映射
        if (templet.getString("field_map") != null) {
            FieldMap fnm = new FieldnameMap(templet);
            lfm.add(fnm);
        }

        // 名称name_chn映射
        if (templet.getString("name_especial_map") != null) {
            FieldMap fnm = new NameMap(templet);
            lfm.add(fnm);
        }

        // srcid映射
        if (templet.getString("srcid_gen") != null) {
            FieldMap fnm = new SrcidMap(templet);
            lfm.add(fnm);
        }

        // code映射
        if (templet.getString("code_map") != null) {
            FieldMap fnm = new CodeMap(templet);
            lfm.add(fnm);
        }

        // 时间映射
        if (templet.getString("time_map") != null) {
            FieldMap fnm = new TimeMap(templet);
            lfm.add(fnm);
        }

        // 电话处理
        if (templet.getString("tel_map") != null) {
            FieldMap fnm = new TelMap(templet);
            lfm.add(fnm);
        }

        // id映射
        if (templet.getString("aid_gen") != null) {
            FieldMap fnm = new AidMap(templet);
            lfm.add(fnm);
        }

        // name all map
        if (templet.getString("name_map") != null) {
            FieldMap fnm = new NameAllMap(templet);
            lfm.add(fnm);
        }

        // new_type映射
        if (templet.getString("newtype_map") != null) {
            FieldMap fnm = new NewtypeMap(templet);
            lfm.add(fnm);
        }

        // regex_map映射
        if (templet.getString("regex_map") != null) {
            FieldMap fnm = new RegexMap(templet);
            lfm.add(fnm);
        }

        if (templet.getString("subfield_map") != null) {
            FieldMap fnm = new SubFieldMap(templet);
            lfm.add(fnm);
        }

        if (templet.getString("opt_type_map") != null) {
            FieldMap fnm = new OptTypeMap(templet);
            lfm.add(fnm);
        }

        // 默认值映射
        if (templet.getString("defaultvalue_map") != null) {
            FieldMap fnm = new DefaultValueMap(templet);
            lfm.add(fnm);
        }

        //Object数据类型映射
        if (templet.getString("object_map") != null) {
            FieldMap fnm = new ObjectMap(templet);
            lfm.add(fnm);
        }

        // 图片处理
        if (templet.getString("pic_field_map") != null) {
            FieldMap fnm = new PicInfoMap(templet);
            lfm.add(fnm);
        }

        // 优惠类图片处理
        if (templet.getString("picinfo_map") != null) {
            FieldMap fnm = new ImgMap(templet);
            lfm.add(fnm);
        }

        // 空字符串替换
        if (templet.getString("null_trans_map") != null) {
            FieldMap fnm = new NullTrans(templet);
            lfm.add(fnm);
        }

        // 时间格式替换
        if (templet.getString("time_name_map") != null
                && templet.getString("time_format_map") != null) {
            FieldMap fnm = new TimeFormatMap(templet);
            lfm.add(fnm);
        }

        // 酒店星级
        if (templet.getString("star_map") != null) {
            FieldMap fnm = new HotelStarMap(templet);
            lfm.add(fnm);
        }

        // html标签替换
        if (templet.getString("html_map") != null) {
            FieldMap fnm = new HtmlTrans(templet);
            lfm.add(fnm);
        }

        // 字符串替换
        if (templet.getString("string_trans_map") != null) {
            FieldMap fnm = new StringMap(templet);
            lfm.add(fnm);
        }
        return lfm;
    }

}
