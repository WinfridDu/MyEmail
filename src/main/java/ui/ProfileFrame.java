package ui;

import model.GlobalData;
import util.MailUtils;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Properties;

public class ProfileFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    MainFrame mainFrame;
    JTextField popHostField = new JTextField();
    JTextField smtpHostField = new JTextField();
    JTextField UserField = new JTextField();
    JPasswordField PasswordField = new JPasswordField();

    public ProfileFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        init();
        setVisible(true);
    }

    private void init() {
        // 屏幕居中
        this.setSize(new Dimension(500,675));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dialogSize = this.getSize();
        this.setLocation((screenSize.width - dialogSize.width) / 2,
                (screenSize.height - dialogSize.height) / 2);
        getContentPane().setLayout(null);
        setResizable(false);

        JLabel label1 = new JLabel("邮箱帐号登录");
        label1.setFont(new Font("Segoe", Font.BOLD, 24));
        label1.setBounds(175,55,150,25);
        getContentPane().add(label1);
        popHostField.setFont(new Font("Segoe", Font.PLAIN, 20));
        popHostField.addFocusListener(new JTextFieldHintListener(popHostField, "POP3服务器"));
        popHostField.setBounds(40, 127, 425, 60);
        getContentPane().add(popHostField);
        smtpHostField.setFont(new Font("Segoe", Font.PLAIN, 20));
        smtpHostField.addFocusListener(new JTextFieldHintListener(smtpHostField, "SMTP服务器"));
        smtpHostField.setBounds(40, 215, 425, 60);
        getContentPane().add(smtpHostField);
        UserField.setFont(new Font("Segoe", Font.PLAIN, 20));
        UserField.addFocusListener(new JTextFieldHintListener(UserField, "邮箱账号"));
        UserField.setBounds(40, 303, 425, 60);
        getContentPane().add(UserField);
        PasswordField.setFont(new Font("Segoe", Font.PLAIN, 20));
        PasswordField.addFocusListener(new JPasswordFieldHintListener(PasswordField, "授权码"));
        PasswordField.setBounds(40, 391, 425, 60);
        getContentPane().add(PasswordField);
        JButton submitBtn = new JButton("登 录");
        submitBtn.setFont(new Font("Segoe", Font.BOLD, 26));
        submitBtn.setBounds(40, 479, 425, 64);
        getContentPane().add(submitBtn);

        loadProps();

        submitBtn.addActionListener(e -> saveProps());
    }

    // 加载配置
    void loadProps() {
        try {
            Properties p = MailUtils.getProps();
            popHostField.setText(p.getProperty("mail.pop3.host"));
            smtpHostField.setText(p.getProperty("mail.smtp.host"));
            UserField.setText(p.getProperty("mail.user"));
            PasswordField.setText(p.getProperty("mail.password"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 保存配置
    void saveProps() {
        Properties props = new Properties();
        props.setProperty("mail.pop3.host", popHostField.getText());
        props.setProperty("mail.smtp.host", smtpHostField.getText());
        props.setProperty("mail.user", UserField.getText());
        props.setProperty("mail.password", new String(PasswordField.getPassword()));
        MailUtils.saveProps(props);
        ProfileFrame.this.dispose();
        try {
            GlobalData.setMailListModel(MailUtils.receiveMail());
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(this,"服务器连接失败！请查看网络连接或邮件服务器设置。","连接信息",JOptionPane.INFORMATION_MESSAGE);
        }
    }
}