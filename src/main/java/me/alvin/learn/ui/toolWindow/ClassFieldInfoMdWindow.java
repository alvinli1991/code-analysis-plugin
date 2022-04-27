package me.alvin.learn.ui.toolWindow;

import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

/**
 * @author: Li Xiang
 * Date: 2022/1/7
 * Time: 10:33 AM
 */
public class ClassFieldInfoMdWindow {
    private JTextArea fieldInfoMd;
    private JPanel fieldMdContent;
    private JScrollPane scrollPane;
    private ToolWindow toolWindow;

    public ClassFieldInfoMdWindow(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        fieldInfoMd.setText("在执行action后此处会显示class field信息的markdown格式");
    }

    public JPanel getFieldMdContent() {
        return fieldMdContent;
    }

    public void refreshFieldInfoMd(String content){
        this.fieldInfoMd.setText(content);
    }

    public void showToolWindow(){
        this.toolWindow.activate(null);
    }
}
