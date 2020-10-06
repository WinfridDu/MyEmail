package model;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MailListModel extends AbstractListModel<Message> {
    private static final long serialVersionUID = 1L;

    private List<Message> messages;
    private Folder folder;

    public void setFolder(Folder folder) throws MessagingException {
        if (folder != null) {
            folder.open(Folder.READ_WRITE);
            messages = Arrays.asList(folder.getMessages());
            Collections.reverse(messages);
            fireIntervalAdded(this, 1, 0);
        }
        else {
            messages = null;
        }
        // 关闭上次显示的文件夹，以新文件夹刷新邮件列表
        if (this.folder != null)
            this.folder.close(true);
        this.folder = folder;
    }

    public void deleteEmail(Message message){
        try{
            message.setFlag(Flags.Flag.DELETED, true);
            folder.close(true);
            folder.open(Folder.READ_WRITE);
            messages = Arrays.asList(folder.getMessages());
            Collections.reverse(messages);
            fireContentsChanged(this,0,0);
        }
        catch(Exception ex){
            System.out.println("删除邮件时出错");
            ex.printStackTrace();
        }
    }

    public void addElement(Message message) {
        if (messages.contains(message)) {
            return;
        }
        int index = messages.size();
        messages.add(message);
        fireIntervalAdded(this, index, index);
    }

    public void removeElement(Message message) {
        int index = messages.indexOf(message);
        if (index >= 0) {
            fireIntervalRemoved(this, index, index);
        }
        messages.remove(message);
    }

    public int getSize() {
        if(null == messages){
            return 0;
        }
        return messages.size();
    }

    public Message getElementAt(int i) {
        return messages.get(i);
    }
}
