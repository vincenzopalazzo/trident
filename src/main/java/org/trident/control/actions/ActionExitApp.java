package org.trident.control.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is an action, exstend abstract action and implement the method actionPerformed
 * Swing called action performed when you click on exit
 *
 * After look this class look inside the class ActionsMediator
 *
 * After you looked in ActionMediator, look inside Frame class and find the exit item
 */

public class ActionExitApp extends AbstractAction implements ActionListener {

    public ActionExitApp() {
        super();
        super.putValue(Action.NAME, "Exist"); //After will think to support all language
        //super.putValue(Action.ACCELERATOR_KEY, );
        //super.putValue(Action.MNEMONIC_KEY, );
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        System.exit(0);
    }
}
