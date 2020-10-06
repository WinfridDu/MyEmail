import ui.MainFrame;
import ui.ProfileFrame;

import javax.swing.*;

public class Application {
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new ProfileFrame(new MainFrame());
    }
}
