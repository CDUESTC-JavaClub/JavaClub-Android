package club.jw.clazz;

import com.alibaba.fastjson.JSONArray;

import org.json.JSONObject;

import club.jw.net.entity.response.ClassesResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ClassTable {

    private ClassTable(){}

    private final List<Clazz> clazzList = new ArrayList<>();

    public static ClassTable convertToTable(ClassesResponse response){
        ClassTable classTable = new ClassTable();
        response.forEach(clazz -> classTable.clazzList.add(new Clazz(clazz)));
        return classTable;
    }

    /**
     * 获取某一天的课程表
     * @param day 1-7（周一 ~ 周日）
     * @return 课程列表
     */
    public List<Clazz> getClassInOneDay(int day){
        return clazzList.stream().filter(c -> c.day == day).collect(Collectors.toList());
    }

    /**
     * 遍历所有课程表
     * @param consumer 消费者
     */
    public void forEach(Consumer<Clazz> consumer){
        clazzList.forEach(consumer);
    }

    public JSONArray toJSONArray(){
        JSONArray array = new JSONArray();
        clazzList.forEach(clazz -> array.add(clazz.toJSON()));
        return array;
    }

    public List<Clazz> getClazzList() {
        return clazzList;
    }
}
