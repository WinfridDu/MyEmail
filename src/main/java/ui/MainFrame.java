package ui;

import model.GlobalData;
import model.MyCellRenderer;
import util.MailUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeUtility;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JLabel fromLabel;
    private JLabel toLabel;
    private JLabel themeLabel;
    private JLabel timeLabel;
    private JButton attachmentButton;
    private JEditorPane mailContentArea = new JEditorPane();
    private JList<Message> messageList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月d日EE hh:mm");
    private List<String> attachmentFiles = new ArrayList<>();
    private List<InputStream> attachmentInputStream = new ArrayList<>();
    private JComboBox<String> comboBox;

    public MainFrame(){
        init();
        setVisible(true);
    }

    public void init(){
        JPanel contentPane = (JPanel) this.getContentPane();
        Font dialog = new Font("Segoe", Font.PLAIN, 20);
        // 屏幕居中
        this.setSize(new Dimension(1005,760));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dialogSize = this.getSize();
        this.setLocation((screenSize.width - dialogSize.width) / 2,
                (screenSize.height - dialogSize.height) / 2);
        // 工具栏
        JToolBar toolBar = new JToolBar();
        contentPane.add(toolBar, BorderLayout.NORTH);
        //工具栏按钮
        JButton receiveButton = new JButton(" 收信 ");
        receiveButton.setFont(dialog);
        receiveButton.setPreferredSize(new Dimension(80, 33));
        receiveButton.addActionListener(e -> receiveEmail());
        toolBar.add(receiveButton,null);
        JButton sendButton = new JButton(" 写信 ");
        sendButton.setFont(dialog);
        sendButton.addActionListener(e -> new NewMailFrame());
        toolBar.add(sendButton,null);
        JButton deleteButton = new JButton(" 删除 ");
        deleteButton.setFont(dialog);
        deleteButton.addActionListener(e -> delete());
        toolBar.add(deleteButton,null);
        JButton switchButton = new JButton(" 切换账号 ");
        switchButton.setFont(dialog);
        switchButton.addActionListener(e -> new ProfileFrame(this));
        toolBar.add(switchButton,null);

        //左边列表面板
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());
        //右边详情面板
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BorderLayout());
        // 创建一个分隔窗格
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                listPanel, detailPanel);
        splitPane.setDividerLocation(315);
        splitPane.setDividerSize(0);
        splitPane.setOneTouchExpandable(true);
        contentPane.add(splitPane, BorderLayout.CENTER);

        //右上边主题附件面板
        JPanel themePanel = new JPanel();
        themePanel.setLayout(new BorderLayout());
        //右下边邮件内容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        // 创建一个分隔窗格
        JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                themePanel, contentPanel);
        splitPane2.setDividerLocation(150);
        splitPane2.setDividerSize(0);
        detailPanel.add(splitPane2, BorderLayout.CENTER);

        //发送人面板
        JPanel senderPanel = new JPanel();
        senderPanel.setLayout(null);
        senderPanel.setPreferredSize(new Dimension(675, 40));
        themePanel.add(senderPanel, BorderLayout.NORTH);
        JLabel jLabel = new JLabel("发件人：");
        jLabel.setFont(dialog);
        jLabel.setForeground(Color.GRAY);
        jLabel.setBounds(10,5,80,30);
        senderPanel.add(jLabel);
        fromLabel = new JLabel("");
        fromLabel.setFont(dialog);
        fromLabel.setBounds(90,5,550,30);
        senderPanel.add(fromLabel);

        //收件人及主题面板
        JPanel receiverPanel = new JPanel();
        receiverPanel.setLayout(null);
        themePanel.add(receiverPanel, BorderLayout.CENTER);
        JLabel jLabel1 = new JLabel("收件人：");
        jLabel1.setFont(dialog);
        jLabel1.setForeground(Color.GRAY);
        jLabel1.setBounds(10,0,80,35);
        receiverPanel.add(jLabel1);
        toLabel = new JLabel("");
        toLabel.setFont(dialog);
        toLabel.setForeground(Color.blue);
        toLabel.setBounds(90,0,550,35);
        receiverPanel.add(toLabel);
        JLabel separateLabel = new JLabel("———————————————"
                +"——————————————————————————————————————-");
        separateLabel.setForeground(Color.GRAY);
        separateLabel.setBounds(10,35,650,5);
        receiverPanel.add(separateLabel);
        themeLabel = new JLabel("主题");
        themeLabel.setFont(dialog);
        themeLabel.setBounds(10,40,500,30);
        receiverPanel.add(themeLabel);
        timeLabel = new JLabel("时间");
        timeLabel.setFont(new Font("Segoe", Font.PLAIN, 18));
        timeLabel.setForeground(Color.GRAY);
        timeLabel.setBounds(10,70,500,35);
        receiverPanel.add(timeLabel);
        comboBox = new JComboBox<>();
        comboBox.setFont(dialog);
        comboBox.setBounds(300,70,220,33);
        receiverPanel.add(comboBox);
        attachmentButton = new JButton("保存附件");
        attachmentButton.setFont(dialog);
        attachmentButton.setBounds(540,70,115,33);
        attachmentButton.setEnabled(false);
        attachmentButton.addActionListener(e -> download());
        receiverPanel.add(attachmentButton);

        //邮件内容面板
        mailContentArea.setEditable(false);
        mailContentArea.setFont(dialog);
        JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.getViewport().add(mailContentArea, null);
        contentPanel.add(scrollPane1,  BorderLayout.CENTER);

        messageList = new JList<>(GlobalData.getMailListModel());
        messageList.setCellRenderer(new MyCellRenderer());
        //设置为单选模式
        messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listPanel.add(new JScrollPane(messageList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        messageList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                attachmentFiles.clear();
                comboBox.removeAllItems();
                attachmentInputStream.clear();
                Message message = messageList.getSelectedValue();
                try {
                    String from = message.getFrom()[0].toString();
                    if(from.startsWith("=?")){
                        from = MimeUtility.decodeText(from);
                    }
                    fromLabel.setText(from);
                    String to =message.getRecipients(Message.RecipientType.TO)[0].toString();
                    if(to.startsWith("=?")){
                        to = MimeUtility.decodeText(to);
                    }
                    toLabel.setText(to);
                    String subject = message.getSubject();
                    if(subject.startsWith("=?")){
                        subject=MimeUtility.decodeText(subject);
                    }
                    themeLabel.setText(subject);
                    timeLabel.setText(dateFormat.format(message.getSentDate()));
//                    timeLabel.setText("0/0");
                    mailContentArea.setText("");
                    loadBody(message);
                } catch (MessagingException ignored) {
                } catch (UnsupportedEncodingException ex){
                    ex.printStackTrace();
                }
            }
        });

        //关闭窗口
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    //对邮件内容进行递归处理的方法
    private void loadBody(Part part){
        try{
            if(part.isMimeType("Multipart/*")){  //为multipart类型
                Multipart mpart = (Multipart)part.getContent();
                int count = mpart.getCount();
                for(int i = 0; i < count; i++){
                    loadBody(mpart.getBodyPart(i));
                }
                return;
            }else{  //为邮件正文或附件
                String disposition = part.getDisposition();
                if((disposition == null)) { //为邮件正文
                    mailContentArea.setContentType("text/plain");
                    mailContentArea.setText("Error：邮件内容无法显示");
                    //邮件正文为文本格式
                    if(part.isMimeType("text/plain")){
                        mailContentArea.setContentType("text/plain");
                        String mailContent = new String(part.getContent().toString()
                                .getBytes("gb2312"));
                        mailContentArea.setText(mailContent);
                    }//邮件正文为html格式
                    else if(part.isMimeType("text/html")){
                        mailContentArea.setContentType("text/html");
                        mailContentArea.setText(part.getContent().toString());
                    }
                    mailContentArea.setCaretPosition(0);
                }else if(disposition.equals(Part.ATTACHMENT)|| disposition.equals(Part.INLINE)){
                    //为附件
                    String tempFileName = part.getFileName();
                    String attachmentFileName;
                    if(null != tempFileName){
                        if(tempFileName.startsWith("=?"))
                            attachmentFileName = MimeUtility.decodeText(tempFileName);
                        else
                            attachmentFileName = new String(tempFileName.getBytes("GBK"));
                        attachmentFiles.add(attachmentFileName);
                        comboBox.addItem(attachmentFileName);
                        attachmentInputStream.add(part.getDataHandler().getInputStream());
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if(attachmentFiles.size() == 0){
            attachmentButton.setEnabled(false);
        }else{
            attachmentButton.setEnabled(true);
        }
    }

    public void receiveEmail(){
        try {
            GlobalData.setMailListModel(MailUtils.receiveMail());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void delete(){
        Message message = messageList.getSelectedValue();
        if(null != message){
            int n = JOptionPane.showConfirmDialog(null, "确认删除吗?", "确认对话框", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                GlobalData.getMailListModel().deleteEmail(message);
                JOptionPane.showMessageDialog(new JFrame(),"已删除");
            }
        }else{
            JOptionPane.showMessageDialog(null, "没有选中任何邮件，请重新选择");
        }
    }

    private void download(){
        int selectedIndex = comboBox.getSelectedIndex();
        String fileName = (String)comboBox.getSelectedItem();
        JFileChooser fileChooser = new JFileChooser();
        assert fileName != null;
        fileChooser.setSelectedFile(new File(fileName));
        int operation = fileChooser.showSaveDialog(this);
        if(operation == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            //保存文件
            boolean saveOrNot = true;
            //保存文件
            if(file.exists()){
                int choice = JOptionPane.showConfirmDialog(this,"该文件已经存在，需要覆盖吗",
                        "信息提示",JOptionPane.OK_CANCEL_OPTION);
                //不操作
                if (choice != 0)
                    saveOrNot = false;
            }
            if(saveOrNot){  //保存文件
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream is = attachmentInputStream.get(
                            selectedIndex);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) != -1){
                        fos.write(buffer,0,len);
                    }
                    fos.flush();
                    fos.close();
                    is.close();
                    JOptionPane.showMessageDialog(null, "保存成功");
                }
                catch (Exception ex) {
                    System.out.println("附件保存出错");
                }
            }
        }
    }
}
