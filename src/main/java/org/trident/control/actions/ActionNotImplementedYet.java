package org.trident.control.actions;

import io.swingsnackbar.SnackBar;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import mdlaf.utils.MaterialColors;
import mdlaf.utils.MaterialImageFactory;
import org.trident.TridentApp;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionNotImplementedYet implements ActionListener{

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.openSnackBar();
    }

    private void openSnackBar(){
        SnackBar.make(TridentApp.getInstance(),"Action Not Implemented", SnackBar.SHORT_LONG)
        .setIcon(MaterialImageFactory.getInstance().getImage(
                GoogleMaterialDesignIcons.WARNING,
                MaterialColors.COSMO_ORANGE
        ))
        .run();
    }
}
