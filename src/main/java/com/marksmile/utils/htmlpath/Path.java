package com.marksmile.utils.htmlpath;

import java.util.ArrayList;
import java.util.List;

public class Path {
    private List<String> listCss = new ArrayList<String>();
    private List<BaseNode> listTxt = new ArrayList<BaseNode>();

    public List<String> getListCss() {
        return listCss;
    }

    public void setListCss(List<String> listCss) {
        this.listCss = listCss;
    }

    public List<BaseNode> getListTxt() {
        return listTxt;
    }

    public void setListTxt(List<BaseNode> listTxt) {
        this.listTxt = listTxt;
    }

    public static String t(String str) {
        return str.replaceAll("\\\\/", "/").replaceAll("\\\\\\(", "(").replaceAll("\\\\\\)", ")").replaceAll("\\\\,", ",");
    }

    public void parserPath(String path) {
        listCss.clear();
        listTxt.clear();
        char[] cs = path.toCharArray();
        int index = -1;
        for (int i = cs.length - 1; i >= 1; i--) {
            if (cs[i] == '/' && cs[i - 1] != '\\') {
                index = i;
                break;
            }
        }
        if (index == -1) {
//            throw new RuntimeException("不合法的Path:" + path);
            listCss.add(path);
            return;
        }
        String cssPath = path.substring(0, index);
        String textPath = path.substring(index + 1);

        cs = cssPath.toCharArray();
        int preIndex = -1;
        for (int i = 1; i < cs.length; i++) {
            if (cs[i] == '/' && cs[i - 1] != '\\') {
                listCss.add(cssPath.substring(preIndex + 1, i));
                preIndex = i;
            }
        }
        listCss.add(cssPath.substring(preIndex + 1));

        List<String> _listCss = new ArrayList<String>();
        for (String str : listCss) {
            _listCss.add(t(str));
        }
        listCss.clear();
        listCss.addAll(_listCss);
        _listCss.clear();

        listTxt = new ArrayList<BaseNode>();

        parseTextPath(listTxt, textPath);
    }

    private void parseTextPath(List<BaseNode> list, String txtPath) {
        char[] cs = txtPath.toCharArray();
        if (cs.length < 1) {
            return;
        }
        if (cs[0] == '(') {
            for (int i = 1; i < cs.length; i++) {
                if (cs[i] == ')' && cs[i - 1] != '\\') {
                    String pre = txtPath.substring(1, i);
                    String post = txtPath.substring(i + 1);
                    MidNode midNode = new MidNode();
                    midNode.parsePath(pre);
                    list.add(midNode);
                    parseTextPath(list, post);
                    break;
                }
            }
        } else if (cs[0] == '[' && cs[cs.length - 1] == ']') {
            AttrNode attrNode = new AttrNode();
            attrNode.parsePath(txtPath.substring(1, txtPath.length() - 1));
            list.add(attrNode);
        } else if ("text".equalsIgnoreCase(txtPath)) {
            list.add(new TextNode());
        }
    }

    public static void main(String[] args) {
        Path path = new Path();
        // path.parserPath("title/text");
        path.parserPath("div[bg='\\/abc\\/ss']/div.class[bbb==xxxx\\(\\)]/(abc,ddd\\/\\(\\),卢鑫丰)(abc,ddd\\/\\(\\,\\),卢鑫丰)");
        System.out.println("Ok");
    }
}
