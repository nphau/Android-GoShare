package com.yosta.goshare.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ConverterUtils {

    /**
     * Normalizer: Chuẩn hóa văn bản Unicode thành văn bản tương đương.
     * Nhằm mục đích thuận tiện cho việc sắp xếp và tìm kiếm chuỗi.
     * String temp = Normalizer.normalize(s, Normalizer.Form.NFD):
     * Tiêu chuẩn hóa chuỗi s được truyền vào theo định dạng NFD.
     * Kết quả trả về là chuỗi đã được tiêu chuẩn hóa.
     * Lớp Pattern:  Dùng để nhận Regexp (Cấu trúc đại diện hay Regular Expression)
     * vào và kiểm tra những String cho vào dựa trên Regexp đã tạo ra.
     * Thông thường để nhận một Regexp, thì dùng phương thức compile.
     * matcher: Dùng để so sánh, tìm kiếm những chữ đưa vào dựa trên Regexp đã tạo ra.
     */
    public static String Vn2NonVn(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp)
                .replaceAll("")
                .replaceAll("Đ", "D")
                .replace("đ", "");
    }

    public static Bitmap String2Bitmap(String s) {
        try {
            byte[] decodeString = Base64.decode(s, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }
/*
    public static ArrayList<Sight> parseListFoodFromJson(String json) {
        ArrayList<Sight> listSight = new ArrayList<>();

        try {

            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);

                Sight sight = new Sight();

                if (object.has("SightID"))
                    sight.setSightID(object.getInt("SightID"));


                if (object.has("Name")) {
                    String name = object.getString("Name");
                    sight.setName(name);
                    sight.setNonVietnamesName(Vn2NonVn(name));
                }

                if (object.has("Latitude") && object.has("Longitude")) {
                    sight.setLatLng(new Location(
                            object.getDouble("Latitude"),
                            object.getDouble("Longitude")));
                }
                if (object.has("ProvinceID") && object.has("District") &&
                        object.has("Ward") && object.has("Street")) {
                    String provinceID = object.getString("ProvinceID");

                    sight.setAddress(new VAddress(
                            Integer.parseInt(provinceID.trim()),
                            object.getString("District"),
                            object.getString("Ward"),
                            object.getString("Street")));
                }
                if (object.has("ExtraInfo"))
                    sight.setExtraInfo(object.getString("ExtraInfo"));
                if (object.has("Point"))
                    sight.setPoint(object.getDouble("Point"));
                if (object.has("SmallImg"))
                    sight.setSmallImg(object.getString("SmallImg"));

                listSight.add(sight);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            listSight = null;
        }

        return listSight;
    }*/
}
