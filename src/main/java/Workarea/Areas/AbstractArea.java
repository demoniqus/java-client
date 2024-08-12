package Workarea.Areas;

import Interfaces.IWorkarea;
import jdk.jfr.Description;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractArea implements IWorkarea {
    private Boolean isInitiated = false;

    public AbstractArea() {
        if (!isInitiated) {
            init();
            isInitiated = true;
        }
    }

    @Description("Инициализация рабочего пространства")
    protected void init(){
        //Дополнительная инициализация контейнера
    }

    protected void clearContainer(Container container) {
        if (container instanceof JFrame) {
            ((JFrame)container).getContentPane().removeAll();
        }
        else {
            container.removeAll();
        }
    }
}
