package Interfaces;

import javax.swing.*;
import java.awt.*;

public interface IClearContainer {
    static void clearContainer(Container container) {
        if (container instanceof JFrame) {
            ((JFrame)container).getContentPane().removeAll();
        }
        else {
            container.removeAll();
        }
    }
}
