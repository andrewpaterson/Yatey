package net.tileeditor.general;

import net.tileeditor.general.AcceptedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;

public class AcceptCancelPanel extends JPanel
{
    protected AcceptedListener acceptedListener;

    public AcceptCancelPanel(AcceptedListener acceptedListener)
    {
        super(new GridBagLayout());
        this.acceptedListener = acceptedListener;

        JButton accept = new JButton("Accept");
        JButton cancel = new JButton("Cancel");
        add(new JPanel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
        add(accept, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
        add(cancel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));

        accept.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                AcceptCancelPanel.this.acceptedListener.accepted();
            }
        });

        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                AcceptCancelPanel.this.acceptedListener.cancelled();
            }
        });
    }
}
