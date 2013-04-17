/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import microfeed.*;
import sql.*;
import twitter.MicroTweet;
import twitter4j.TwitterException;
import util.*;

/**
 * The mainframe which contains the main -and only- user interface functionality
 * for the project
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
    private MicroTweet microtweet;
    private int microID = -1;

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

        //initialising main functionality
        try {
            loadFeeds();
            loadAuthors();
            loadDraft();
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        //initialising twitter functionality
        try {
            microtweet = new MicroTweet();
            statusL.setText(statusL.getText() + " || Twitter Connected.");
        } catch (TwitterException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            statusL.setText(statusL.getText() + " || Twitter Disconnected.");
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            statusL.setText(statusL.getText() + " || Twitter Disconnected.");
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
     *
     * @throws SQLException
     */
    public void loadDraft() throws SQLException {
        Feed draft = fetcher.fetchDraft();

        if (draft != null) {
            authorCombo.setSelectedItem(draft.getAuthor());
            titleF.setText(draft.getTitle());
            textArea.setText(draft.getContent());

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
     * Sends the latest draft to the browser.
     */
    private void preview() {
        String link = microtweet.getUrl() + "?microID=" + microID;

        //checking if operation is supported
        if (java.awt.Desktop.isDesktopSupported()) {
            //getting desktop object and checking if BROWSE operation is supported
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                //checking if microID is in cache
                if (microID != -1) {
                    try {
                        URI uri = new URI(link);
                        desktop.browse(uri);
                    } catch (IOException ex) {
                        // browser exception error
                        MesDial.browserError(this);
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (URISyntaxException ex) {
                        //wrong URI error
                        MesDial.uriError(this);
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    MesDial.nullMicroError(this);
                }
            } else {
                MesDial.browseNotSupportedError(this);
            }
        } else {
            MesDial.awtNotSupportedError(this);
        }

    }

    private void createFeed(boolean isDraft) {

        //setting -1 as status, it's not a draft nor a post yet
        Feed feed = new Feed((String) authorCombo.getSelectedItem(),
                titleF.getText(), textArea.getText(), -1);

        //setting the appropriate status
        if (isDraft) {
            feed.setStatus(0);
        } else {
            feed.setStatus(1);
        }

        //inserting in the database and tweeting the created feed.
        try {
            microID = fetcher.createFeed(feed);

            //creating the appropriate message for the user
            if (isDraft) {
                statusL.setText("Draft saved successfully! || "
                        + new Date());
            } else {
                //sending the microtweet if operation selected by user.
                if (tweetChk.isSelected()) {

                    String link = microtweet.getUrl() + "?microID=" + microID;

                    //creating the actual tweet and finally tweeting it
                    String tweet = microtweet.composeTweet(titleF.getText(), link);

                    int ans = MesDial.tweetQuestion(this, tweet);
                    if (ans == JOptionPane.YES_OPTION) {
                        microtweet.updateStatus(tweet);
                    }
                }
                MesDial.postSuccess(this);
            }
        } catch (SQLException ex) {
            MesDial.conError(this);
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TwitterException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        //clearing fields - drafts
        if (!isDraft) {
            titleF.setText("");
            textArea.setText("");
            try {
                fetcher.cleanDrafts();
            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This function fills the feed table. If all feeds option is selected, then
     * it fetches all the feeds from the database.
     *
     * @param allFeeds
     */
    private void refreshFeedTable(boolean allFeeds) {
        String query = ""
                + "SELECT * "
                + "FROM microfeed ";

        if (!allFeeds) {
            query += "WHERE Status = 1 "
                    + "ORDER BY DatePosted DESC "
                    + "LIMIT 25 ";
        } else {
            query += "ORDER BY DatePosted DESC ";
        }

        try {
            TableParser.fillTable(query, feedTable, con);
        } catch (SQLException ex) {
            MesDial.conError(this);
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This function checks whether the database schema is compatible with the
     * application. Changes may arise to the database schema that are not
     * synchronized with the application version.
     *
     */
    public void checkSchema() {
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
        tweetChk = new javax.swing.JCheckBox();
        tinyfeedPanel = new javax.swing.JPanel();
        authorLbl = new javax.swing.JLabel();
        titleF = new javax.swing.JTextField();
        titleLbl = new javax.swing.JLabel();
        contentLbl = new javax.swing.JLabel();
        authorCombo = new javax.swing.JComboBox();
        contentPane = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        statusL = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        feedTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        editFeedBtn = new javax.swing.JButton();
        deleteFeedBtn = new javax.swing.JButton();
        allFeedsBtn = new javax.swing.JButton();
        refreshFeedsBtn = new javax.swing.JButton();
        quitBtn1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        panelCont = new javax.swing.JScrollPane();
        mainMenu = new javax.swing.JMenuBar();
        fileM = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        helpM = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

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

        tweetChk.setText("Tweet");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tweetChk)
                .addContainerGap(41, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(publishBtn)
                    .addComponent(draftBtn)
                    .addComponent(previewBtn)
                    .addComponent(quitBtn)
                    .addComponent(tweetChk))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tinyfeedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("New Microfeed"));

        authorLbl.setText("Author:");

        titleF.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        titleLbl.setText("Title:");

        contentLbl.setText("Content:");

        authorCombo.setEditable(true);
        authorCombo.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N

        textArea.setColumns(20);
        textArea.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        textArea.setLineWrap(true);
        textArea.setRows(5);
        textArea.setWrapStyleWord(true);
        contentPane.setViewportView(textArea);

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
                    .addComponent(titleF)
                    .addComponent(authorCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(contentPane))
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
                    .addGroup(tinyfeedPanelLayout.createSequentialGroup()
                        .addComponent(contentLbl)
                        .addGap(0, 130, Short.MAX_VALUE))
                    .addComponent(contentPane))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        statusL.setText(null);

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
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(statusL)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        feedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(feedTable);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        editFeedBtn.setText("Edit");
        editFeedBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editFeedBtnActionPerformed(evt);
            }
        });

        deleteFeedBtn.setText("Delete");
        deleteFeedBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteFeedBtnActionPerformed(evt);
            }
        });

        allFeedsBtn.setText("All Feeds");
        allFeedsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allFeedsBtnActionPerformed(evt);
            }
        });

        refreshFeedsBtn.setText("Refresh");
        refreshFeedsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshFeedsBtnActionPerformed(evt);
            }
        });

        quitBtn1.setText("Quit");
        quitBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitBtn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(editFeedBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(deleteFeedBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(allFeedsBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(refreshFeedsBtn)
                .addGap(13, 13, 13)
                .addComponent(quitBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editFeedBtn)
                    .addComponent(deleteFeedBtn)
                    .addComponent(allFeedsBtn)
                    .addComponent(refreshFeedsBtn)
                    .addComponent(quitBtn1))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        mainTabbedPanel.addTab("All", jPanel1);

        panelCont.setBorder(javax.swing.BorderFactory.createTitledBorder("Stream of Microfeeds"));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCont, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCont, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
        );

        mainTabbedPanel.addTab("Stream", jPanel2);

        fileM.setText("File");

        jMenuItem5.setText("Set Up Twitter");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        fileM.add(jMenuItem5);

        jMenuItem2.setText("Disconnect");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        fileM.add(jMenuItem2);

        jMenuItem4.setText("Exit");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        fileM.add(jMenuItem4);

        mainMenu.add(fileM);

        helpM.setText("Help");

        jMenuItem1.setText("About");
        helpM.add(jMenuItem1);

        mainMenu.add(helpM);

        setJMenuBar(mainMenu);

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
        preview();
    }//GEN-LAST:event_previewBtnActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
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

    private void quitBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitBtn1ActionPerformed
        shutdown();
    }//GEN-LAST:event_quitBtn1ActionPerformed

    private void refreshFeedsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshFeedsBtnActionPerformed
        refreshFeedTable(false);
    }//GEN-LAST:event_refreshFeedsBtnActionPerformed

    private void allFeedsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allFeedsBtnActionPerformed
        refreshFeedTable(true);
    }//GEN-LAST:event_allFeedsBtnActionPerformed

    private void deleteFeedBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteFeedBtnActionPerformed
        int answer = MesDial.deleteQuestion(this);

        if (answer == JOptionPane.YES_OPTION) {
            int[] rows = feedTable.getSelectedRows();
            try {
                for (int i = 0; i < rows.length; i++) {
                    int microID = Integer.parseInt((String) feedTable.getValueAt(rows[i], 0));

                    con.sendUpdate(""
                            + "DELETE "
                            + "FROM microfeed "
                            + "WHERE microID = " + microID);
                }
            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        refreshFeedTable(false);
    }//GEN-LAST:event_deleteFeedBtnActionPerformed

    private void editFeedBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editFeedBtnActionPerformed
        if (feedTable.getSelectedRowCount() == 1) {
            try {
                Feed feed = fetcher.fetchFeed(Integer.parseInt((String) feedTable.getValueAt(feedTable.getSelectedRow(), 0)));
                FeedFrame f = new FeedFrame(this, con, feed);
            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            MesDial.rowSelectionError(this);
        }
    }//GEN-LAST:event_editFeedBtnActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        shutdown();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        try {
            con.closeConnection();
            this.dispose();
            pFrame.setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        if (!TwitterFrame.isInstanceAlive()) {
            new TwitterFrame();
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton allFeedsBtn;
    private javax.swing.JComboBox authorCombo;
    private javax.swing.JLabel authorLbl;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel contentLbl;
    private javax.swing.JScrollPane contentPane;
    private javax.swing.JButton deleteFeedBtn;
    private javax.swing.JButton draftBtn;
    private javax.swing.JButton editFeedBtn;
    private javax.swing.JTable feedTable;
    private javax.swing.JMenu fileM;
    private javax.swing.JMenu helpM;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JMenuBar mainMenu;
    private javax.swing.JTabbedPane mainTabbedPanel;
    private javax.swing.JPanel newFeedPanel;
    private javax.swing.JScrollPane panelCont;
    private javax.swing.JButton previewBtn;
    private javax.swing.JButton publishBtn;
    private javax.swing.JButton quitBtn;
    private javax.swing.JButton quitBtn1;
    private javax.swing.JButton refreshFeedsBtn;
    private javax.swing.JLabel statusL;
    private javax.swing.JTextArea textArea;
    private javax.swing.JPanel tinyfeedPanel;
    private javax.swing.JTextField titleF;
    private javax.swing.JLabel titleLbl;
    private javax.swing.JCheckBox tweetChk;
    // End of variables declaration//GEN-END:variables
}
