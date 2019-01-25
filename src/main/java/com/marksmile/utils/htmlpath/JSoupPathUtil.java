package com.marksmile.utils.htmlpath;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JSoupPathUtil {
    public static void main(String[] args) {
        String str = "<html><table><tr><td>1</td><td id = 'lxf' >abc123ccd</td></tr></table><table><tr><td>3</td><td>4</td></tr></table></html>";

        Document doc = Jsoup.parse(str);
        List<String> list = getSelect(doc, "td#lxf/(abc,ccd)");
        for (String string : list) {
            System.out.println(string);
        }
    }

    public static List<String> getSelect(Document doc, String path) {
        if (doc == null || path == null) {
            return null;
        }
        Path _path = new Path();
        _path.parserPath(path);

        List<String> listCss = _path.getListCss();
        List<BaseNode> listTxt = _path.getListTxt();

        int cssIndex = 0;
        if (listCss.size() < 1) {
            throw new RuntimeException("不合法的Path:" + path);
        }
        Elements elements = doc.select(listCss.get(cssIndex));
        List<Element> listElement = new ArrayList<Element>();
        for (Element element2 : elements) {
            getSelect(listElement, element2, listCss, cssIndex);
        }
        List<String> ret = new ArrayList<String>();
        for (Element e : listElement) {
            ret.add(getElementValue(e, listTxt));
        }

        return ret;
    }

    private static String getElementValue(Element e, List<BaseNode> listTxt) {

        String html = e.html();
        for (BaseNode baseNode : listTxt) {
            if (baseNode instanceof TextNode) {
                TextNode node = (TextNode) baseNode;
                return html;
            }
            if (baseNode instanceof AttrNode) {
                AttrNode node = (AttrNode) baseNode;
                return e.attr(node.getAttName());
            }

            if (baseNode instanceof MidNode) {
                MidNode node = (MidNode) baseNode;
                html = mid(html, node.getFirst(), node.getSecond());
                if (html == null || "".equals(html)) {
                    html = node.getDefaultValue();
                }
            }

        }
        return html;
    }

    private static void getSelect(List<Element> listElement, Element element, List<String> listCss, int cssIndex) {
        if (cssIndex == listCss.size()) {
            return;
        }
        if (cssIndex == (listCss.size() - 1)) {
            Elements es = element.select(listCss.get(cssIndex));
            for (Element e : es) {
                listElement.add(e);
            }
        } else {
            Elements es = element.select(listCss.get(cssIndex));
            for (Element e : es) {
                getSelect(listElement, e, listCss, cssIndex + 1);
            }
        }
    }

    public static String mid(String all, String pre, String post) {
        int indexPre = 0;
        if(pre.equals("$N")){
            pre = "";
        }
        if(post.equals("$N")){
            post = "";
        }
        if (pre != null && !"".equals(pre)) {

            indexPre = all.indexOf(pre);
            if (indexPre == -1) {
                return null;
            }
            indexPre = indexPre + pre.length();
        }
        int indexPost = all.length() ;
        if (post != null && !"".equals(post)) {
            indexPost = all.indexOf(post, indexPre);
        }
        if (indexPost == -1) {
            return null;
        }
        return all.substring(indexPre, indexPost);
    }

}
