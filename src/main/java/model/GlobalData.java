package model;

import javax.mail.Folder;
import javax.mail.MessagingException;

/**
 * 管理MailListModel类
 */
public class GlobalData {

    private static MailListModel mailListModel = new MailListModel();

    public static void setMailListModel(Folder folder) throws MessagingException {
        GlobalData.mailListModel.setFolder(folder);
    }

    public static MailListModel getMailListModel() {
        return mailListModel;
    }
}
