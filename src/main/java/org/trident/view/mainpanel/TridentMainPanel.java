package org.trident.view.mainpanel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.trident.view.ITridentComponentView;
import org.trident.view.container.ToolBarButtonsCodeContainer;
import org.trident.view.container.ToolBarCompilerComponent;

import javax.swing.*;
import java.awt.*;

/**
 * @author https://github.com/vincenzopalazzo
 */
public class TridentMainPanel extends JPanel implements ITridentComponentView {

    private RSyntaxTextArea codeEditorText;
    private RTextScrollPane textScrollPane;
    private JToolBar toolBarEditor;
    private ToolBarButtonsCodeContainer containerButtonsCode;
    private ToolBarCompilerComponent containerCompilerInfo;
    private JTree fileTree;
    private JLabel statusText;
    private JLabel savedStatus;
    private JLabel fileTypeStatus;
    private JLabel positionStatus;

    public TridentMainPanel() {
        initView(); //TODO Remember this, if you don't call this here, you don't see nothing -- > I see!
    }

    @Override
    public void initView() {
        initComponent();
        initLayout();
        initActions();

        //I call here because if I call inside the initCompoennt the layout override the configuration
        super.add(textScrollPane);
        super.setVisible(true);
    }

    @Override
    public void initComponent() {

        // TODO look the example here https://github.com/bobbylight/RSyntaxTextArea#example-usage
        this.codeEditorText = new RSyntaxTextArea(20, 60);
        this.codeEditorText.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON); //This is only an example, should be set inside the action
        this.codeEditorText.setCodeFoldingEnabled(true);
        this.textScrollPane = new RTextScrollPane(codeEditorText);

        this.codeEditorText.setText("print('Hello, welcome in the new trindent')");

        this.toolBarEditor = new JToolBar();
        toolBarEditor.setLayout(new BorderLayout());

        this.containerButtonsCode = new ToolBarButtonsCodeContainer();

        this.containerCompilerInfo = new ToolBarCompilerComponent();

        this.fileTree = new JTree();

        toolBarEditor.add(containerButtonsCode, BorderLayout.CENTER);
        toolBarEditor.add(containerCompilerInfo, BorderLayout.EAST);
    }

    @Override
    public void initLayout() {
        super.setLayout(new BorderLayout());
        super.add(this.toolBarEditor, BorderLayout.NORTH);
        super.add(this.fileTree, BorderLayout.WEST);
    }

    @Override
    public void initActions() {
        //do nothing for the moment
    }

    //only getter !! NOT Setter
    //TODO before generate if we need to rename variable of the label
    public RSyntaxTextArea getCodeEditorText() {
        return codeEditorText;
    }
}
