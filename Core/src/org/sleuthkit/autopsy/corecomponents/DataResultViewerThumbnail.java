/*
 * Autopsy Forensic Browser
 *
 * Copyright 2011-2016 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.corecomponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.corecomponentinterfaces.DataResultViewer;
import org.sleuthkit.autopsy.coreutils.ImageUtils;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.TskCoreException;

/**
 * Thumbnail view of images in data result with paging support.
 *
 * Paging is added to reduce memory footprint and load only up to (currently)
 * 1000 images at a time. This works whether or not the underlying content nodes
 * are being lazy loaded or not.
 *
 */
// @@@ Restore implementation of DataResultViewerThumbnail as a DataResultViewer 
// service provider when DataResultViewers can be made compatible with node 
// multi-selection actions.
//@ServiceProvider(service = DataResultViewer.class)
final class DataResultViewerThumbnail extends AbstractDataResultViewer {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(DataResultViewerThumbnail.class.getName());
    //flag to keep track if images are being loaded
    private int curPage;
    private int totalPages;
    private int curPageImages;
    private int iconSize = ImageUtils.ICON_SIZE_MEDIUM;
    private final PageUpdater pageUpdater = new PageUpdater();

    /**
     * Creates a DataResultViewerThumbnail object that is compatible with node
     * multiple selection actions.
     */
    public DataResultViewerThumbnail(ExplorerManager explorerManager) {
        super(explorerManager);
        initialize();
    }

    /**
     * Creates a DataResultViewerThumbnail object that is NOT compatible with
     * node multiple selection actions.
     */
    public DataResultViewerThumbnail() {
        initialize();
    }

    @NbBundle.Messages({"DataResultViewerThumbnail.thumbnailSizeComboBox.small=Small Thumbnails",
                "DataResultViewerThumbnail.thumbnailSizeComboBox.medium=Medium Thumbnails",
                "DataResultViewerThumbnail.thumbnailSizeComboBox.large=Large Thumbnails"
    })
    private void initialize() {
        initComponents();

        iconView.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        em.addPropertyChangeListener(new ExplorerManagerNodeSelectionListener());
        thumbnailSizeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] {  Bundle.DataResultViewerThumbnail_thumbnailSizeComboBox_small(),
                                Bundle.DataResultViewerThumbnail_thumbnailSizeComboBox_medium(),
                                Bundle.DataResultViewerThumbnail_thumbnailSizeComboBox_large() }));

        curPage = -1;
        totalPages = 0;
        curPageImages = 0;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pageLabel = new javax.swing.JLabel();
        pagesLabel = new javax.swing.JLabel();
        pagePrevButton = new javax.swing.JButton();
        pageNextButton = new javax.swing.JButton();
        imagesLabel = new javax.swing.JLabel();
        imagesRangeLabel = new javax.swing.JLabel();
        pageNumLabel = new javax.swing.JLabel();
        filePathLabel = new javax.swing.JLabel();
        goToPageLabel = new javax.swing.JLabel();
        goToPageField = new javax.swing.JTextField();
        thumbnailSizeComboBox = new javax.swing.JComboBox<>();
        iconView = new org.openide.explorer.view.IconView();

        pageLabel.setText(org.openide.util.NbBundle.getMessage(DataResultViewerThumbnail.class, "DataResultViewerThumbnail.pageLabel.text")); // NOI18N

        pagesLabel.setText(org.openide.util.NbBundle.getMessage(DataResultViewerThumbnail.class, "DataResultViewerThumbnail.pagesLabel.text")); // NOI18N

        pagePrevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_back.png"))); // NOI18N
        pagePrevButton.setText(org.openide.util.NbBundle.getMessage(DataResultViewerThumbnail.class, "DataResultViewerThumbnail.pagePrevButton.text")); // NOI18N
        pagePrevButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_back_disabled.png"))); // NOI18N
        pagePrevButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        pagePrevButton.setPreferredSize(new java.awt.Dimension(55, 23));
        pagePrevButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_back_hover.png"))); // NOI18N
        pagePrevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pagePrevButtonActionPerformed(evt);
            }
        });

        pageNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_forward.png"))); // NOI18N
        pageNextButton.setText(org.openide.util.NbBundle.getMessage(DataResultViewerThumbnail.class, "DataResultViewerThumbnail.pageNextButton.text")); // NOI18N
        pageNextButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_forward_disabled.png"))); // NOI18N
        pageNextButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        pageNextButton.setMaximumSize(new java.awt.Dimension(27, 23));
        pageNextButton.setMinimumSize(new java.awt.Dimension(27, 23));
        pageNextButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/corecomponents/btn_step_forward_hover.png"))); // NOI18N
        pageNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pageNextButtonActionPerformed(evt);
            }
        });

        imagesLabel.setText(org.openide.util.NbBundle.getMessage(DataResultViewerThumbnail.class, "DataResultViewerThumbnail.imagesLabel.text")); // NOI18N

        imagesRangeLabel.setText(org.openide.util.NbBundle.getMessage(DataResultViewerThumbnail.class, "DataResultViewerThumbnail.imagesRangeLabel.text")); // NOI18N

        pageNumLabel.setText(org.openide.util.NbBundle.getMessage(DataResultViewerThumbnail.class, "DataResultViewerThumbnail.pageNumLabel.text")); // NOI18N

        filePathLabel.setText(org.openide.util.NbBundle.getMessage(DataResultViewerThumbnail.class, "DataResultViewerThumbnail.filePathLabel.text")); // NOI18N

        goToPageLabel.setText(org.openide.util.NbBundle.getMessage(DataResultViewerThumbnail.class, "DataResultViewerThumbnail.goToPageLabel.text")); // NOI18N

        goToPageField.setText(org.openide.util.NbBundle.getMessage(DataResultViewerThumbnail.class, "DataResultViewerThumbnail.goToPageField.text")); // NOI18N
        goToPageField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToPageFieldActionPerformed(evt);
            }
        });

        thumbnailSizeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thumbnailSizeComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(filePathLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(pageLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pageNumLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(pagesLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(pagePrevButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(pageNextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(goToPageLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(goToPageField, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(imagesLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(imagesRangeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(thumbnailSizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(iconView, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(pageLabel)
                        .addComponent(pagesLabel)
                        .addComponent(pagePrevButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(pageNumLabel))
                    .addComponent(pageNextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(imagesLabel)
                        .addComponent(imagesRangeLabel)
                        .addComponent(goToPageLabel)
                        .addComponent(goToPageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(thumbnailSizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iconView, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filePathLabel))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pagePrevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pagePrevButtonActionPerformed
        previousPage();
    }//GEN-LAST:event_pagePrevButtonActionPerformed

    private void pageNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pageNextButtonActionPerformed
        nextPage();
    }//GEN-LAST:event_pageNextButtonActionPerformed

    private void goToPageFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToPageFieldActionPerformed
        goToPage(goToPageField.getText());
    }//GEN-LAST:event_goToPageFieldActionPerformed

    private void thumbnailSizeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thumbnailSizeComboBoxActionPerformed

        iconSize = ImageUtils.ICON_SIZE_MEDIUM;   //default size
        switch (thumbnailSizeComboBox.getSelectedIndex()) {
            case 0:
                iconSize = ImageUtils.ICON_SIZE_SMALL;
                break;
            case 2:
                iconSize = ImageUtils.ICON_SIZE_LARGE;
                break;
        }

        Node root = em.getRootContext();
        for (Children c : Arrays.asList(root.getChildren())) {
            ((ThumbnailViewChildren) c).setIconSize(iconSize);
        }

        for (Node page : root.getChildren().getNodes()) {
            for (Node node : page.getChildren().getNodes()) {
                ((ThumbnailViewNode) node).setIconSize(iconSize);
            }
        }

        // Temporarily set the explored context to the root, instead of a child node.
        // This is a workaround hack to convince org.openide.explorer.ExplorerManager to
        // update even though the new and old Node values are identical. This in turn
        // will cause the entire view to update completely. After this we 
        // immediately set the node back to the current child by calling switchPage().        
        em.setExploredContext(root);
        switchPage();
    }//GEN-LAST:event_thumbnailSizeComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel filePathLabel;
    private javax.swing.JTextField goToPageField;
    private javax.swing.JLabel goToPageLabel;
    private org.openide.explorer.view.IconView iconView;
    private javax.swing.JLabel imagesLabel;
    private javax.swing.JLabel imagesRangeLabel;
    private javax.swing.JLabel pageLabel;
    private javax.swing.JButton pageNextButton;
    private javax.swing.JLabel pageNumLabel;
    private javax.swing.JButton pagePrevButton;
    private javax.swing.JLabel pagesLabel;
    private javax.swing.JComboBox<String> thumbnailSizeComboBox;
    // End of variables declaration//GEN-END:variables

    @Override
    public boolean isSupported(Node selectedNode) {
        if (selectedNode == null) {
            return false;
        }
        return true;
    }

    @Override
    public void setNode(Node givenNode) {
        // change the cursor to "waiting cursor" for this operation
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            if (givenNode != null) {
                ThumbnailViewChildren childNode = new ThumbnailViewChildren(givenNode, iconSize);

                final Node root = new AbstractNode(childNode);
                pageUpdater.setRoot(root);
                root.addNodeListener(pageUpdater);
                em.setRootContext(root);
            } else {
                Node emptyNode = new AbstractNode(Children.LEAF);
                em.setRootContext(emptyNode); // make empty node

                iconView.setBackground(Color.BLACK);
            }
        } finally {
            this.setCursor(null);
        }
    }

    @Override
    public String getTitle() {
        return NbBundle.getMessage(this.getClass(), "DataResultViewerThumbnail.title");
    }

    @Override
    public DataResultViewer createInstance() {
        return new DataResultViewerThumbnail();
    }

    @Override
    public void resetComponent() {
        super.resetComponent();
        this.totalPages = 0;
        this.curPage = -1;
        curPageImages = 0;
        updateControls();

    }

    @Override
    public void clearComponent() {
        this.iconView.removeAll();
        this.iconView = null;

        super.clearComponent();
    }

    private void nextPage() {
        if (curPage < totalPages) {
            curPage++;

            switchPage();
        }
    }

    private void previousPage() {
        if (curPage > 1) {
            curPage--;

            switchPage();
        }
    }

    private void goToPage(String pageNumText) {
        int newPage;
        try {
            newPage = Integer.parseInt(pageNumText);
        } catch (NumberFormatException e) {
            //ignore input
            return;
        }

        if (newPage > totalPages || newPage < 1) {
            JOptionPane.showMessageDialog(this,
                    NbBundle.getMessage(this.getClass(),
                            "DataResultViewerThumbnail.goToPageTextField.msgDlg",
                            totalPages),
                    NbBundle.getMessage(this.getClass(),
                            "DataResultViewerThumbnail.goToPageTextField.err"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        curPage = newPage;
        switchPage();
    }

    private void switchPage() {

        EventQueue.invokeLater(() -> {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        });

        //Note the nodes factories are likely creating nodes in EDT anyway, but worker still helps 
        new SwingWorker<Object, Void>() {
            private ProgressHandle progress;

            @Override
            protected Object doInBackground() throws Exception {
                pagePrevButton.setEnabled(false);
                pageNextButton.setEnabled(false);
                goToPageField.setEnabled(false);
                progress = ProgressHandle.createHandle(
                        NbBundle.getMessage(this.getClass(), "DataResultViewerThumbnail.genThumbs"));
                progress.start();
                progress.switchToIndeterminate();
                Node root = em.getRootContext();
                Node pageNode = root.getChildren().getNodeAt(curPage - 1);
                em.setExploredContext(pageNode);
                curPageImages = pageNode.getChildren().getNodesCount();
                return null;
            }

            @Override
            protected void done() {
                progress.finish();
                setCursor(null);
                updateControls();
                // see if any exceptions were thrown
                try {
                    get();
                } catch (InterruptedException | ExecutionException ex) {
                    NotifyDescriptor d
                            = new NotifyDescriptor.Message(
                                    NbBundle.getMessage(this.getClass(), "DataResultViewerThumbnail.switchPage.done.errMsg",
                                            ex.getMessage()),
                                    NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(d);
                    logger.log(Level.SEVERE, "Error making thumbnails: {0}", ex.getMessage()); //NON-NLS
                } // catch and ignore if we were cancelled
                catch (java.util.concurrent.CancellationException ex) {
                }
            }
        }.execute();

    }

    private void updateControls() {
        if (totalPages == 0) {
            pagePrevButton.setEnabled(false);
            pageNextButton.setEnabled(false);
            goToPageField.setEnabled(false);
            pageNumLabel.setText("");
            imagesRangeLabel.setText("");
            thumbnailSizeComboBox.setEnabled(false);
        } else {
            pageNumLabel.setText(
                    NbBundle.getMessage(this.getClass(), "DataResultViewerThumbnail.pageNumbers.curOfTotal",
                            Integer.toString(curPage), Integer.toString(totalPages)));
            final int imagesFrom = (curPage - 1) * ThumbnailViewChildren.IMAGES_PER_PAGE + 1;
            final int imagesTo = curPageImages + (curPage - 1) * ThumbnailViewChildren.IMAGES_PER_PAGE;
            imagesRangeLabel.setText(imagesFrom + "-" + imagesTo);

            pageNextButton.setEnabled(!(curPage == totalPages));
            pagePrevButton.setEnabled(!(curPage == 1));
            goToPageField.setEnabled(totalPages > 1);
            thumbnailSizeComboBox.setEnabled(true);
        }

    }

    /**
     * Listens for root change updates and updates the paging controls
     */
    private class PageUpdater implements NodeListener {

        private Node root;

        void setRoot(Node root) {
            this.root = root;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        }

        @Override
        public void childrenAdded(NodeMemberEvent nme) {
            totalPages = root.getChildren().getNodesCount();

            if (totalPages == 0) {
                curPage = -1;
                updateControls();
                return;
            }

            if (curPage == -1 || curPage > totalPages) {
                curPage = 1;
            }

            //force load the curPage node
            final Node pageNode = root.getChildren().getNodeAt(curPage - 1);

            //em.setSelectedNodes(new Node[]{pageNode});
            if (pageNode != null) {
                pageNode.addNodeListener(new NodeListener() {
                    @Override
                    public void childrenAdded(NodeMemberEvent nme) {
                        curPageImages = pageNode.getChildren().getNodesCount();
                        updateControls();
                    }

                    @Override
                    public void childrenRemoved(NodeMemberEvent nme) {
                        curPageImages = 0;
                        updateControls();
                    }

                    @Override
                    public void childrenReordered(NodeReorderEvent nre) {
                    }

                    @Override
                    public void nodeDestroyed(NodeEvent ne) {
                    }

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                    }
                });

                em.setExploredContext(pageNode);
            }

            updateControls();

        }

        @Override
        public void childrenRemoved(NodeMemberEvent nme) {
            totalPages = 0;
            curPage = -1;
            updateControls();
        }

        @Override
        public void childrenReordered(NodeReorderEvent nre) {
        }

        @Override
        public void nodeDestroyed(NodeEvent ne) {
        }
    }

    private class ExplorerManagerNodeSelectionListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(ExplorerManager.PROP_SELECTED_NODES)) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    Node[] selectedNodes = em.getSelectedNodes();
                    if (selectedNodes.length == 1) {
                        AbstractFile af = selectedNodes[0].getLookup().lookup(AbstractFile.class);
                        if (af == null) {
                            filePathLabel.setText("");
                        } else {
                            try {
                                String uPath = af.getUniquePath();
                                filePathLabel.setText(uPath);
                                filePathLabel.setToolTipText(uPath);
                            } catch (TskCoreException e) {
                                logger.log(Level.WARNING, "Could not get unique path for content: {0}", af.getName()); //NON-NLS
                            }
                        }
                    } else {
                        filePathLabel.setText("");
                    }
                } finally {
                    setCursor(null);
                }
            }
        }
    }
}
