package tetracoveragearea.gui.panels.primitives;

import net.miginfocom.swing.MigLayout;
import tetracoveragearea.gui.panels.WithInvoking;

import javax.swing.*;
import java.awt.*;

/**
 * Created by anatoliy on 31.03.17.
 */
public class SubPanel extends JPanel implements WithInvoking {


    public SubPanel() {

        setLayout(new MigLayout());

        setPreferredSize(new Dimension(250, 450));

    }


    @Override
    public void onInvoke() {

    }

    @Override
    public void onRevoke() {

    }
}
