package ui;

import util.MailUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class NewMailFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField toTextField = new JTextField();
    private JTextField subjectTextField = new JTextField();
    private JTextField attachTextField = new JTextField();
    private JTextArea contentArea = new JTextArea();

    public NewMailFrame(){
        init();
        setVisible(true);
    }

    private void init(){
        //居中
        this.setSize(new Dimension(500,400));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dialogSize = this.getSize();
        this.setLocation((screenSize.width - dialogSize.width) / 2,
                (screenSize.height - dialogSize.height) / 2);
        setTitle("新邮件");
        setResizable(false);
        JPanel contentPane = (JPanel)getContentPane();
        Font dialog = new Font("Segoe", Font.PLAIN, 20);
        contentPane.setLayout(null);
        toTextField.setFont(dialog);
        toTextField.addFocusListener(new JTextFieldHintListener(toTextField, "收件人"));
        toTextField.setBounds(10, 10, 460, 40);
        contentPane.add(toTextField);
        subjectTextField.setFont(dialog);
        subjectTextField.addFocusListener(new JTextFieldHintListener(subjectTextField, "主题"));
        subjectTextField.setBounds(10, 50, 460, 40);
        contentPane.add(subjectTextField);
        attachTextField.setFont(dialog);
        attachTextField.addFocusListener(new JTextFieldHintListener(attachTextField, "附件"));
        attachTextField.setBounds(10, 90, 380, 40);
        contentPane.add(attachTextField);
        JButton addAttach = new JButton("添加");
        addAttach.setFont(dialog);
        addAttach.setBounds(390, 90, 80, 40);
        addAttach.addActionListener(e -> attachmentButton());
        contentPane.add(addAttach);
        contentArea.setFont(dialog);
        contentArea.setBounds(10, 130, 460, 170);
        contentPane.add(contentArea);
        JButton sendButton = new JButton("发送");
        sendButton.setFont(dialog);
        sendButton.setBounds(390, 305, 80, 40);
        sendButton.addActionListener(e -> sendEmail());
        contentPane.add(sendButton);
    }

    private void sendEmail(){
        //获取邮件相关信息
        String to = toTextField.getText().trim();
        if(0 >= to.length() || to.equals("收件人")){
            JOptionPane.showMessageDialog(this,"缺少收件人","信息提示", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        String subject = subjectTextField.getText().trim();
        if(0 >= subject.length() || subject.equals("主题")){
            JOptionPane.showMessageDialog(this,"缺少主题","信息提示", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        String attach = attachTextField.getText().trim();
        if("附件".equals(attach))
            attach = "";
        String text = contentArea.getText();
        boolean flag = MailUtils.sendMail(to, text, subject, attach);
        if(flag){
            JOptionPane.showMessageDialog(this,"发送邮件成功!","信息提示", JOptionPane.PLAIN_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(this,"发送邮件失败","信息提示",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attachmentButton() {
        //弹出一个文件对话框，并从中选出文件名
        String attachFiles = attachTextField.getText();//取出已添加的附件
        JFileChooser fileChooser = new JFileChooser();
        int operation = fileChooser.showSaveDialog(this);
        if(operation == JFileChooser.APPROVE_OPTION){
            File attach = fileChooser.getSelectedFile();
            String attachName;
            if(attach.isFile()){
                attachName = attach.getAbsolutePath();//取出所选择的文件名
                if (attachFiles.length() > 0 && !attachFiles.startsWith("附件"))
                    attachFiles += "," + attachName;
                else
                    attachFiles = attachName;
            }
        }
        attachTextField.setText(attachFiles);
    }
}
