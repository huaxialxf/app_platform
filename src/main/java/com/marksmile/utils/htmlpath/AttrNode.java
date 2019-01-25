package com.marksmile.utils.htmlpath;

public class AttrNode extends BaseNode {
    private String attName;

    public void parsePath(String path) {
        this.attName = path;
    }

    public String getAttName() {
        return attName;
    }

    public void setAttName(String attName) {
        this.attName = attName;
    }

    public String toString() {
        return "[" + attName + "]";
    }

}
