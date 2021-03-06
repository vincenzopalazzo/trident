package org.trident.view;
/*
 *  org.trident.view.FindAndReplace.java
 *  (c) Copyright, 2020 - 2021 Krishna Moorthy
 *  akrishnamoorthy007@gmail.com | github.com/KrishnaMoorthy12
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.trident.Trident;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

/*
 * The Find and Replace
 * (Apache v2) Trident > org.trident.view.FindAndReplace
 * For debugging purposes only - deprecated
 * @author: Krishna Moorthy
 * Since: v3.1
 * Deprecated since: v4.0
 * Replaced by org.trident.view.FindReplace
 */

class FindAndReplace {
  public static JDialog findDialog, replaceDialog;
  public static JButton findButton, findNextButton, replaceButton, replaceAllButton, closeButton;
  public static JTextField findField, replaceField;
  public static JCheckBox matchCase, wholeWords;
  static boolean fimOpen, rimOpen;
  static FindReplaceButtonsListener frbl = new FindReplaceButtonsListener();

  @Deprecated
  public static void findUI() {
    if (fimOpen) {
      findDialog.requestFocus();
      return;
    }
    if (rimOpen) {
      rimOpen = false;
      replaceDialog.dispose();
    }
    fimOpen = true;
    findDialog = new JDialog(Trident.getInstance().getFrame(), "Find");
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BorderLayout(5, 5));

    JLabel findLabel = new JLabel("Find ");
    findField = new JTextField(43);
    // TODO Add copy, paste pop up
    JPanel mainPanel = new JPanel(new GridLayout(2, 1, 1, 1));
    JPanel fieldPane = new JPanel();
    fieldPane.setBorder(new EmptyBorder(5, 0, 0, 0));
    fieldPane.add(findLabel);
    fieldPane.add(findField);
    mainPanel.add(fieldPane);

    wholeWords = new JCheckBox("Whole Words only");
    matchCase = new JCheckBox("Match Case");
    JPanel opsPane = new JPanel(new GridLayout(1, 2, 1, 1));
    opsPane.setBorder(new TitledBorder(new EtchedBorder(), "Options", 0, TitledBorder.CENTER));
    opsPane.add(wholeWords);
    opsPane.add(matchCase);
    mainPanel.add(opsPane);

    findButton = new JButton("Find");
    findButton.addActionListener(frbl);
    findNextButton = new JButton("Find Next");
    findNextButton.addActionListener(frbl);
    closeButton = new JButton("Close");
    closeButton.addActionListener(frbl);

    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.add(findButton);
    buttonPanel.add(findNextButton);
    buttonPanel.add(closeButton);

    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPanel.add(mainPanel, BorderLayout.NORTH);
    contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    findDialog.add(contentPanel);
    findDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    findDialog.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent we) {
        fimOpen = false;
        findDialog.dispose();
      }
    });
    closeButton.addActionListener(new FindReplaceButtonsListener());
    findDialog.setSize(400, 165);
    findDialog.setLocationRelativeTo(Trident.getInstance().getFrame());
    findDialog.setResizable(false);
    findDialog.setVisible(true);
  }

  @Deprecated
  public static void replaceUI() {
    if (rimOpen) {
      replaceDialog.requestFocus();
      return;
    }
    if (fimOpen) {
      fimOpen = false;
      findDialog.dispose();
    }
    rimOpen = true;
    replaceDialog = new JDialog(Trident.getInstance().getFrame(), "Find and Replace");
    JPanel contentPanel = new JPanel();

    JLabel findLabel = new JLabel("Find ");
    findField = new JTextField(40);
    JLabel replaceLabel = new JLabel("Replace ");
    replaceField = new JTextField(38);
    JPanel mainPanel = new JPanel(new GridLayout(2, 1, 1, 1));
    JPanel fPane = new JPanel(new FlowLayout());
    JPanel rPane = new JPanel(new FlowLayout());
    fPane.add(findLabel);
    fPane.add(findField);
    rPane.add(replaceLabel);
    rPane.add(replaceField);
    mainPanel.add(fPane);
    mainPanel.add(rPane);

    wholeWords = new JCheckBox("Whole Words only");
    matchCase = new JCheckBox("Match Case");
    JPanel opsPane = new JPanel(new GridLayout(1, 2, 1, 1));
    opsPane.setBorder(new TitledBorder(new EtchedBorder(), "Options", 0, TitledBorder.CENTER));
    opsPane.add(wholeWords);
    opsPane.add(matchCase);

    findButton = new JButton("Find");
    findButton.addActionListener(frbl);

    findNextButton = new JButton("Find Next");
    findNextButton.addActionListener(frbl);

    replaceButton = new JButton("Replace");
    replaceButton.addActionListener(frbl);

    replaceAllButton = new JButton("Replace All");
    replaceAllButton.addActionListener(frbl);

    closeButton = new JButton("Close");
    closeButton.addActionListener(frbl);

    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.add(findButton);
    buttonPanel.add(findNextButton);
    buttonPanel.add(replaceButton);
    buttonPanel.add(replaceAllButton);
    buttonPanel.add(closeButton);

    contentPanel.setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPanel.add(mainPanel, BorderLayout.NORTH);
    contentPanel.add(opsPane, BorderLayout.CENTER);
    contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    replaceDialog.add(contentPanel);
    replaceDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    replaceDialog.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent we) {
        rimOpen = false;
        replaceDialog.dispose();
      }
    });
    closeButton.addActionListener(new FindReplaceButtonsListener());
    replaceDialog.setSize(400, 195);
    replaceDialog.setLocationRelativeTo(Trident.getInstance().getFrame());
    replaceDialog.setResizable(false);
    replaceDialog.setVisible(true);
  }
}

/*
 * org.trident.view.FindAndReplace Buttons Listener
 *
 * (Apache v2) Trident > Find and Replace
 *
 * @author: Krishna Moorthy
 *
 * Since: v3.1
 *
 * Deprecated since: v4.0
 *
 * Replaced by org.trident.view.FRButtonsListener
 */

class FindReplaceButtonsListener implements ActionListener {
  static boolean found = false;
  static ArrayList<Integer> foundStarts;
  static ArrayList<Integer> foundEnds;

  @Deprecated
  public static void find() {
    /*
     * Finds the given string in the trident text area, based on the settings from
     * UI
     *
     * Depricated since: v4.0
     */
    foundStarts = new ArrayList<>();
    foundEnds = new ArrayList<>();
    foundStarts.clear();
    foundEnds.clear();
    Pattern pattern;
    // Whole words only
    if (FindAndReplace.wholeWords.isSelected() && !FindAndReplace.matchCase.isSelected()) {
      pattern = Pattern.compile("\\b" + FindAndReplace.findField.getText() + "\\b", Pattern.CASE_INSENSITIVE);
    } // WW Only and Match Case
    else if (FindAndReplace.wholeWords.isSelected() && FindAndReplace.matchCase.isSelected()) {
      pattern = Pattern.compile("\\b" + FindAndReplace.findField.getText() + "\\b");
    } // Not Ww, not Mc
    else if (!FindAndReplace.wholeWords.isSelected() && !FindAndReplace.matchCase.isSelected()) {
      pattern = Pattern.compile(FindAndReplace.findField.getText(), Pattern.CASE_INSENSITIVE);
    } // Match case alone
    else if (!FindAndReplace.wholeWords.isSelected() && FindAndReplace.matchCase.isSelected())
      pattern = Pattern.compile(FindAndReplace.findField.getText());
    else // Exceptional cases : Taken as both selected to give accuracy
      pattern = Pattern.compile("\\b" + FindAndReplace.findField.getText() + "\\b");

    Matcher matcher = pattern.matcher(Trident.getInstance().getTextarea().getText());
    while (matcher.find()) {
      foundStarts.add(matcher.start());
      foundEnds.add(matcher.end());
      Trident.getInstance().getstatusText().setText("Found " + foundStarts.size() + " matches.");
      found = true;
    }
    if (!found) {
      Trident.getInstance().getstatusText().setText("No match found.");
    }
  }

  static int i = 0;

  @Deprecated
  public static void findNext() {
    /*
     * Moves selection to next match in the textarea
     *
     * Depricated since v4.0
     */
    find();
    try {
      if (i >= foundEnds.size()) {
        i = 0;
      }
      Trident.getInstance().getTextarea().setSelectionStart(foundStarts.get(i));
      Trident.getInstance().getTextarea().setSelectionEnd(foundEnds.get(i));
      i++;

    } catch (IndexOutOfBoundsException iob) {
      Trident.getInstance().getstatusText().setText("No more matches found");
    }
  }

  @Deprecated
  public static void replace() {
    /*
     * Replaces the hightlighted text with the entered text in Replace Field in UI
     *
     * Depricated since: v4.0
     */
    find();
    if (foundStarts.size() == 0)
      return;
    String replacement = FindAndReplace.replaceField.getText();
    Trident.getInstance().getTextarea().replaceSelection(replacement);
    Trident.getInstance().getstatusText().setText("One occurance was replaced.");
    find();
    findNext();
  }

  @Deprecated
  public static void replaceAll() {
    /*
     * Replaces all the occurances of a given string with another given string
     *
     * Depricated since v4.0
     */

    find();
    for (int i = 0; i <= foundStarts.size(); i++) {
      replace();
    }
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    /*
     * Controls button actions of Find and Replace Dialog
     */
    switch (ae.getActionCommand()) {
      case "Find":
        find();
        break;

      case "Find Next":
        findNext();
        break;

      case "Replace":
        replace();
        break;

      case "Replace All":
        replaceAll();
        break;

      case "Close":
        if (FindAndReplace.fimOpen) {
          FindAndReplace.fimOpen = false;
          FindAndReplace.findDialog.dispose();
        }
        if (FindAndReplace.rimOpen) {
          FindAndReplace.rimOpen = false;
          FindAndReplace.replaceDialog.dispose();
        }
        break;
    }
  }
}
