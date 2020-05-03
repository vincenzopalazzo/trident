package org.trident.view.container;

import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import mdlaf.utils.MaterialColors;
import mdlaf.utils.MaterialImageFactory;
import org.trident.control.ActionsMediator;
import org.trident.util.Constant;

import javax.swing.*;

public class ToolBarButtonsCodeContainer extends JPanel {

    private JButton runCode;
    private JButton stopCode;
    private JButton consoleCode;

    public ToolBarButtonsCodeContainer() {
        initView();
    }

    private void initView(){
        initComponent();
        initAction();
    }

    private void initComponent(){
        this.runCode = new JButton(MaterialImageFactory.getInstance().getImage(
                GoogleMaterialDesignIcons.PLAY_ARROW,
                MaterialColors.COSMO_GREEN
        ));
        this.stopCode = new JButton(MaterialImageFactory.getInstance().getImage(
                GoogleMaterialDesignIcons.STOP,
                MaterialColors.COSMO_RED
        ));
        this.consoleCode = new JButton(MaterialImageFactory.getInstance().getImage(
                GoogleMaterialDesignIcons.CODE,
                MaterialColors.COSMO_BLACK
        ));
        runCode.setFocusable(false);
        stopCode.setFocusable(false);
        consoleCode.setFocusable(false);
        super.add(runCode);
        super.add(stopCode);
        super.add(consoleCode);
    }

    private void initAction(){
        runCode.addActionListener(ActionsMediator.getInstance().getAction(Constant.ACTION_NOT_IMPLEMENTED));
        stopCode.addActionListener(ActionsMediator.getInstance().getAction(Constant.ACTION_NOT_IMPLEMENTED));
        consoleCode.addActionListener(ActionsMediator.getInstance().getAction(Constant.ACTION_NOT_IMPLEMENTED));
    }
}
