package model;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

public class MyCellRenderer extends JLabel implements ListCellRenderer<Message> {
    private static final long serialVersionUID = 1L;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/d");

    @Override
    public Component getListCellRendererComponent(JList<? extends Message> list, Message value,
                                                  int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        try {
            //日期
            String date = dateFormat.format(value.getSentDate());
//            String date = "0/0";
            //发件人
            Address[] from = value.getFrom();
            String sender = "匿名";
            if(from != null){
                //如果发件人地址采用64编码，则进行解码，否则，直接取其值
                String temp=from[0].toString();
                if(temp.startsWith("=?"))
                    sender = MimeUtility.decodeText(temp);
                else
                    sender = temp;
            }
            //主题
            String subject = value.getSubject();
            if (subject != null) {
                if (subject.startsWith("=?"))
                    subject = MimeUtility.decodeText(subject);
            } else {
                subject = "no subject";
            }
            setText("<html><p style='margin:5px;font-size:16px;font-family:system-ui;'>" +
                    "<span style='font-size:14px;color:gray'>"+date+"&nbsp;</span>"+sender
                    +"</p><p style='font-size:14px;margin:5px'>"+subject+"</p><html/>");
        } catch (MessagingException ignored) {
        } catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setOpaque(true);
        return this;
    }
}
