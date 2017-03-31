package tetracoveragearea.gui.panels.primitives;

import net.miginfocom.swing.MigLayout;
import tetracoveragearea.gui.panels.WithInvoking;

import javax.swing.*;
import java.awt.*;

/**
 * Created by anatoliy on 31.03.17.
 */
public abstract class PrimaryPanel extends JPanel implements WithInvoking {

    public PrimaryPanel() {

        setLayout(new MigLayout());
        setPreferredSize(new Dimension(300, 500));
    }

    @Override
    public abstract void onInvoke();

    @Override
    public abstract void onRevoke();
}
