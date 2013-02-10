/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import microfeed.*;
import sql.*;
import util.*;

/**
 * The mainframe which contains the main -and only- user interface functionality for the
 * project
 *
 * @author Alex Hughes <alexhughes117@gmail.com>
 */
public class MainFrame extends GUI {

    /**
     * Creates new form MainFrame
     */
    private GUI pFrame;
    private Connector con;
    private Fetcher fetcher;

    public MainFrame(GUI aPreviousFrame, Connector aConnector) {
        pFrame = aPreviousFrame;
        con = aConnector;
        fetcher = new Fetcher(con);

        initComponents();
        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });
        try {
            if (isSchemaCompatible()) {
                loadFeeds();
                loadAuthors();
                loadDraft();
            } else {
                MesDial.dbSchemaError(this);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        super.setFrameLocationCenter(this);
        this.setVisible(true);
    }

    /**
     * This function checks whether the database schema is compatible with the
     * application. Changes may arise to the database schema that are not
     * synchronized with the application version.
     *
     * @return
     */
    public boolean isSchemaCompatible() {
        return true;
    }

    public void loadFeeds() throws SQLException {
        fetcher.fetchFeeds();

        //TODO load Stream and JTable
    }

    public void loadAuthors() throws SQLException {
        fetcher.fetchAuthors();

        authorCombo.removeAllItems();

        for (int i = 0; i < fetcher.getAuthors().size(); i++) {
            authorCombo.addItem((String) fetcher.getAuthors().get(i));
        }
    }
    
    /**
     * This function loads the latest draft from the database.
     * @throws SQLException 
     */
    public void loadDraft() throws SQLException {
        Feed draft = fetcher.fetchDraft();
        
        if(draft != null) {
            authorCombo.setSelectedItem(draft.getAuthor());
            titleF.setText(draft.getTitle());
            contentArea.setText(draft.getContent());
            
            statusL.setText("Latest draft loaded.");
        } else {
            statusL.setText("No drafts available.");
        }
    }

    private void shutdown() {
        int answer = MesDial.exitQuestion(this);
        if (answer == JOptionPane.YES_OPTION) {
            try {
                con.closeConnection();
            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.exit(0);
        }

    }

    /**
     * Reloads feeds and author combo
     */
    private void refresh() {
        try {
            loadFeeds();
            loadAuthors();
        } catch (SQLException ex) {
            MesDial.conError(this);
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createFeed(boolean isDraft) {
        
        //setting -1 as status, it's not a draft nor a post yet
        Feed feed = new Feed(StrVal.sntS((String) authorCombo.getSelectedItem()), 
                StrVal.sntS(titleF.getText()), StrVal.sntS(contentArea.getText()), -1);
        
        //setting the appropriate status
        if(isDraft){
            feed.setStatus(0);        
        } else {
            feed.setStatus(1);
        }
        
        //inserting in the database the created field.
        try {
            fetcher.createFeed(feed);
                                    
            //creating the appropriate message for the user
            if(isDraft) {
                statusL.setText("Draft saved successfully! || " 
                        + new Date());
            } else {
                MesDial.postSuccess(this);
            }
        } catch (SQLException ex) {
            MesDial.conError(this);
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //clearing fields - drafts
        if(!isDraft) {
            titleF.setText("");
            contentArea.setText("");
            try {
                fetcher.cleanDrafts();
            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainTabbedPanel = new javax.swing.JTabbedPane();
        newFeedPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        quitBtn = new javax.swing.JButton();
        publishBtn = new javax.swing.JButton();
        draftBtn = new javax.swing.JButton();
        previewBtn = new javax.swing.JButton();
        tinyfeedPanel = new javax.swing.JPanel();
        authorLbl = new javax.swing.JLabel();
        titleF = new javax.swing.JTextField();
        titleLbl = new javax.swing.JLabel();
        contentLbl = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        contentArea = new javax.swing.JTextArea();
        authorCombo = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        statusL = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Microfeed");
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });

        newFeedPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                newFeedPanelKeyTyped(evt);
            }
        });

        buttonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        quitBtn.setText("Quit");
        quitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitBtnActionPerformed(evt);
            }
        });

        publishBtn.setText("Publish!");
        publishBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                publishBtnActionPerformed(evt);
            }
        });

        draftBtn.setText("Save Draft");
        draftBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                draftBtnActionPerformed(evt);
            }
        });

        previewBtn.setText("Preview");
        previewBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(publishBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(draftBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(previewBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(quitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(publishBtn)
                    .addComponent(draftBtn)
                    .addComponent(previewBtn)
                    .addComponent(quitBtn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tinyfeedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("New Microfeed"));

        authorLbl.setText("Author:");

        titleLbl.setText("Title:");

        contentLbl.setText("Content:");

        contentArea.setColumns(20);
        contentArea.setLineWrap(true);
        contentArea.setRows(5);
        contentArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(contentArea);

        authorCombo.setEditable(true);

        javax.swing.GroupLayout tinyfeedPanelLayout = new javax.swing.GroupLayout(tinyfeedPanel);
        tinyfeedPanel.setLayout(tinyfeedPanelLayout);
        tinyfeedPanelLayout.setHorizontalGroup(
            tinyfeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tinyfeedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tinyfeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(titleLbl)
                    .addComponent(contentLbl)
                    .addComponent(authorLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tinyfeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(titleF)
                    .addComponent(authorCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tinyfeedPanelLayout.setVerticalGroup(
            tinyfeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tinyfeedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tinyfeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(authorLbl)
                    .addComponent(authorCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tinyfeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleLbl)
                    .addComponent(titleF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tinyfeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contentLbl)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        statusL.setText("null");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusL)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusL)
        );

        javax.swing.GroupLayout newFeedPanelLayout = new javax.swing.GroupLayout(newFeedPanel);
        newFeedPanel.setLayout(newFeedPanelLayout);
        newFeedPanelLayout.setHorizontalGroup(
            newFeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newFeedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(newFeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(newFeedPanelLayout.createSequentialGroup()
                        .addGroup(newFeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(newFeedPanelLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(tinyfeedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(2, 2, 2)))
                .addContainerGap())
        );
        newFeedPanelLayout.setVerticalGroup(
            newFeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newFeedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tinyfeedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        mainTabbedPanel.addTab("New", newFeedPanel);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 429, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 319, Short.MAX_VALUE)
        );

        mainTabbedPanel.addTab("All", jPanel1);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Stream of Microfeeds"));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
        );

        mainTabbedPanel.addTab("Stream", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTabbedPanel)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTabbedPanel)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void quitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitBtnActionPerformed
        shutdown();
    }//GEN-LAST:event_quitBtnActionPerformed

    private void previewBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewBtnActionPerformed
        refresh();
    }//GEN-LAST:event_previewBtnActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_F5) {
            refresh();
            System.out.println("REFRESH");
        }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped
        
    }//GEN-LAST:event_formKeyTyped

    private void newFeedPanelKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newFeedPanelKeyTyped
        
    }//GEN-LAST:event_newFeedPanelKeyTyped

    private void draftBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_draftBtnActionPerformed
        createFeed(true);
    }//GEN-LAST:event_draftBtnActionPerformed

    private void publishBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publishBtnActionPerformed
        createFeed(false);
    }//GEN-LAST:event_publishBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox authorCombo;
    private javax.swing.JLabel authorLbl;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JTextArea contentArea;
    private javax.swing.JLabel contentLbl;
    private javax.swing.JButton draftBtn;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane mainTabbedPanel;
    private javax.swing.JPanel newFeedPanel;
    private javax.swing.JButton previewBtn;
    private javax.swing.JButton publishBtn;
    private javax.swing.JButton quitBtn;
    private javax.swing.JLabel statusL;
    private javax.swing.JPanel tinyfeedPanel;
    private javax.swing.JTextField titleF;
    private javax.swing.JLabel titleLbl;
    // End of variables declaration//GEN-END:variables
}
