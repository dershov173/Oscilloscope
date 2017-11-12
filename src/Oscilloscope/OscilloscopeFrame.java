/**
 * GraphFrame.JAVA
 */
package Oscilloscope;

import Oscilloscope.SignalLogicalAnalyzer.RealSignalDispatcher;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdr.core.Format;
import sdr.core.FormatException;

/**
 * Окно.
 * 
 * @author Anna
 * 
 */
public class OscilloscopeFrame extends javax.swing.JFrame {    
    
    public OscilloscopeFrame() throws FormatException {
        initComponents(); 
        jButton5.setEnabled(false);
        jButton6.setEnabled(true);
    }
 
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        oscilloscopeMainPanel1 = new Oscilloscope.OscilloscopeMainPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(100, 100, 100), 1, true));
        jPanel4.setForeground(java.awt.Color.lightGray);

        jButton5.setText("Oscilloscope");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Spectrum Analyzer");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton8.setText("Exit");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton7.setText("Connect");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 406, Short.MAX_VALUE)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton6)
                    .addComponent(jButton8)
                    .addComponent(jButton7))
                .addGap(37, 37, 37))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(oscilloscopeMainPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oscilloscopeMainPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        jButton5.setEnabled(false);
        jButton6.setEnabled(true);
        GlobalVariables.displayWindow = 0;    
        OscilloscopeMainPanel.jTextField1.setText("ms");
        OscilloscopeMainPanel.jTextField4.setText("ms");
        OscilloscopeMainPanel.jTextField8.setText("ms");        
        
        OscilloscopeMainPanel.jTextField2.setText("mV"); 
        OscilloscopeMainPanel.jTextField6.setText("mV");
        OscilloscopeMainPanel.jTextField10.setText("mV");
        
        OscilloscopeMainPanel.jLabel1.setText("Channel");
        OscilloscopeMainPanel.jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Real", "Image", "Real+Image", "Envelope"}));
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

        jButton5.setEnabled(true);
        jButton6.setEnabled(false);
        GlobalVariables.displayWindow = 1;     
        OscilloscopeMainPanel.jTextField1.setText("Hz");
        OscilloscopeMainPanel.jTextField4.setText("Hz");
        OscilloscopeMainPanel.jTextField8.setText("Hz");        
        
        OscilloscopeMainPanel.jTextField2.setText(""); 
        OscilloscopeMainPanel.jTextField6.setText("");
        OscilloscopeMainPanel.jTextField10.setText("");
        
        OscilloscopeMainPanel.jLabel1.setText("Method");      
        OscilloscopeMainPanel.jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"FFT", "FFT(dB)","Periodogram"}));
        
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        
        setVisible(false);
        System.exit(0);
        
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        
        ConnectPannel cn = new ConnectPannel(oscilloscopeMainPanel1.getDisplayPannel1());
        cn.setLocation(200, 200);
        cn.setVisible(true);        
        
    }//GEN-LAST:event_jButton7ActionPerformed

    public static void main(String args[]) {
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OscilloscopeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
       
        /**
         * Создание и отображение формы
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {                 
                try {                    
                    GlobalVariables.frame = new OscilloscopeFrame();
                    GlobalVariables.frame.setVisible(true);
                    GlobalVariables.displayWindow = 0;
                    
                    OscilloscopeMainPanel.jTextField1.setText("ms");
                    OscilloscopeMainPanel.jTextField4.setText("ms");
                    OscilloscopeMainPanel.jTextField8.setText("ms");

                    OscilloscopeMainPanel.jTextField2.setText("mV");
                    OscilloscopeMainPanel.jTextField6.setText("mV");
                    OscilloscopeMainPanel.jTextField10.setText("mV");

                    OscilloscopeMainPanel.jLabel1.setText("Channel");
                } catch (FormatException ex) {
                    Logger.getLogger(OscilloscopeFrame.class.getName()).log(Level.SEVERE, null, ex);               
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JButton jButton5;
    public static javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JPanel jPanel4;
    public static Oscilloscope.OscilloscopeMainPanel oscilloscopeMainPanel1;
    // End of variables declaration//GEN-END:variables
}
