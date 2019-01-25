package com.marksmile.utils.htmlpath;

import java.util.ArrayList;
import java.util.List;

public class MidNode extends BaseNode {
    private String first;
    private String second;
    private String defaultValue;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void parsePath(String path) {
        List<String> list = new ArrayList<String>();
        int preIndex = -1;
        char[] cs = path.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            if (cs[i] == ',' && cs[i - 1] != '\\') {
                list.add(path.substring(preIndex + 1, i));
                preIndex = i;
            }
        }

        list.add(path.substring(preIndex + 1));
        if (list.size() != 2 && list.size() != 3) {
            throw new RuntimeException("不合法的路径:" + path);
        }
        first = Path.t(list.get(0));
        second = Path.t(list.get(1));
        if (list.size() == 3) {
            this.defaultValue = Path.t(list.get(2));
        }
    }


    public String toString() {
        return String.format("%s,%s,%s", first,second,defaultValue);
    }
    
    public static void main(String[] args) {
        new MidNode().parsePath("abc,\\(\\)abc,\\)adsfa");

    }

    public static void main1(String[] args) {
        System.out.println("\\)".replaceAll("\\\\\\)", ")"));
    }

}
