package com.example.networktest;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author 陈明涛 Email:cmt96@foxmail.com
 * @version V1.0
 * @Description: SAX解析
 * @Date 2021/3/6 13:17
 */
public class ContentHandler extends DefaultHandler {
    private String nodeName;
    private StringBuilder id;
    private StringBuilder name;
    private StringBuilder version;

    @Override // 开始解析XML时调用
    public void startDocument() throws SAXException {
        id = new StringBuilder();
        name = new StringBuilder();
        version = new StringBuilder();
    }

    @Override // 解析某个节点时调用
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // 记录当前节点名
        nodeName = localName;
    }

    @Override // 获取节点中那内容时调用
    public void characters(char[] ch, int start, int length) throws SAXException {
        // 根据当前的节点名判断将内容添加到哪一个StringBuilder对象中
        if ("id".equals(nodeName)) {
            id.append(ch, start, length);
        } else if ("name".equals(nodeName)) {
            id.append(ch, start, length);
        } else if ("version".equals(nodeName)) {
            id.append(ch, start, length);
        }
    }

    @Override // 完成解析某个节点时调用
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("app".equals(localName)) {
            Log.d("ContentHandler", "id is" + id.toString().trim());
            Log.d("ContentHandler", "name is" + name.toString().trim());
            Log.d("ContentHandler", "version is" + version.toString().trim());
            // 清空StringBuilder
            id.setLength(0);
            name.setLength(0);
            version.setLength(0);
        }
    }

    @Override // 完成整个XML解析时调用
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}
