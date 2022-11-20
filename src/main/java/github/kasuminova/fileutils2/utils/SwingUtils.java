package github.kasuminova.fileutils2.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SwingUtils {
    public static class PopupMenuAdapter extends MouseAdapter {
        private final JPopupMenu popupMenu;
        private final Component component;

        public PopupMenuAdapter(JPopupMenu popupMenu, Component component) {
            this.popupMenu = popupMenu;
            this.component = component;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) MiscUtils.showPopupMenu(popupMenu, component, e);
        }
    }
}
