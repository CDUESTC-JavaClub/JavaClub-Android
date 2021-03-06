package club.jw.net.entity.response;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import club.jw.net.anno.Info;

public abstract class JSONResponse extends Response{
    private final Map<String, Field> fieldMap = new HashMap<>();
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    protected JSONResponse(){
        for (Field field : this.getClass().getDeclaredFields()) {
            if(!field.isAccessible()) field.setAccessible(true);
            Info info = field.getAnnotation(Info.class);
            if(info == null) continue;
            fieldMap.put(info.value(), field);
        }
    }

    public JSONObject asJSON(){
        JSONObject object = new JSONObject();
        fieldMap.forEach((key, value) -> {
            try {
                object.put(key, value.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return object;
    }

    public Object get(String name){
        try {
            return fieldMap.get(name).get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setField(String name, String value){
        Field field = fieldMap.get(name);
        if(field != null){
            try {
                switch (field.getType().getName()){
                    case "int":
                        if(value.isEmpty() || value.contains("-")){
                            field.set(this, 0);
                        }else {
                            field.set(this, Integer.parseInt(value));
                        }
                        break;
                    case "double":
                        if(value.isEmpty() || value.contains("-")) {
                            field.set(this, 0.0);
                        }else {
                            field.set(this, Double.parseDouble(value));
                        }
                        break;
                    case "java.util.Date":
                        field.set(this, format.parse(value));
                        break;
                    default:
                        field.set(this, value);
                }
            } catch (IllegalAccessException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return asJSON().toString();
    }
}
