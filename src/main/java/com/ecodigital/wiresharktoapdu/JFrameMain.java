/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecodigital.wiresharktoapdu;


import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author gluque
 */
public class JFrameMain extends javax.swing.JFrame {

    
    private List<byte[]> apdus = new ArrayList<byte[]>();
    private List<CommandByte> commands = new ArrayList<>();
    
    /**
     * Creates new form NewJFrame
     */
    public JFrameMain() {
        initComponents();
        customUI();
        enableDragAndDrop();
        
    }


    private void resetValues() {
        jTextPane1.setText("");
        jTextPane1.setFont(MyFont.REGULAR.deriveFont(14f));
        jLabel2.setText("");
        apdus       = new ArrayList<byte[]>();
        commands    = new ArrayList<>();
    }    
    
    private void customUI(){
        jTextPane1.setBackground(MyColor.COLOR_WHITE);
        jTextPane1.setEditable(false);
        jTextPane1.setFont(MyFont.REGULAR.deriveFont(35f));
        jTextPane1.setContentType("text/html");
        String inicio = "<font color='#9B9B9B'><br/><br/><br/><br/><br/><br/><br/><center>Arrastre y suelte aqui <br/>un documento.</center><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/></font>";
        jTextPane1.setText(inicio);
        jTextPane1.setCaretPosition(0);
        
        jCheckBox1.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
              showData();
            }
        });
    }
    
    public void showData(String file) {
        byte[] data = MyFile.readFile(file);
        System.out.println("tamanio:"+data.length);
        int i=0;
        int length = 0;
        int index_pad = 27;
        int index_OUT_IN = 16;
        while(i + 1 < data.length){
            if(data[i] == (byte)0x1B && data[i + 1] == (byte)0x00){
                length = data[ i - 4] & 0xFF; // sin signo
                if(length > index_pad) {
                    byte OUT_IN = data[i+index_OUT_IN];
                    byte[] tmp = HexUtils.subArray(data, i+index_pad, length-index_pad);
                    if(tmp[0] != (byte) 0x50){ // NO INTERRUPT
                        apdus.add(tmp);
                        commands.add(new CommandByte(OUT_IN, tmp));
                    }
                }
                i+=length;
            } else {
                i++;
            }
        }
        showData();
        
    }
    
    
    private void showData() {
        boolean onlyApdu = jCheckBox1.isSelected();
        int head_apdu = 10;
        StringBuilder stringBuilder = new StringBuilder();
        if(commands.size()> 0) {
            for(int i=0; i< commands.size(); i++) {
                if(onlyApdu) {
                    if(commands.get(i).getData().length > head_apdu) {
                        if(commands.get(i).getOut_in() == (byte)0x00) { // OUT
                            stringBuilder.append("<br/><font color='gray'>----------------------------------------------------------------------------------</font>");
                            stringBuilder.append("<br/><font color='blue'>"+ HexUtils.hexify(HexUtils.subArray(commands.get(i).getData(),head_apdu, commands.get(i).getData().length-head_apdu) , true) +"</font>");
                        } else if(commands.get(i).getOut_in() == (byte)0x01) { // IN
                            stringBuilder.append("<br/><font color='red'>"+ HexUtils.hexify(HexUtils.subArray(commands.get(i).getData(),head_apdu, commands.get(i).getData().length-head_apdu) , true) +"</font>");
                        }
                    }
                } else {
                    if(commands.get(i).getOut_in() == (byte)0x00) { // OUT
                        stringBuilder.append("<br/><font color='gray'>----------------------------------------------------------------------------------</font>");
                        stringBuilder.append("<br/><font color='blue'>"+ HexUtils.hexify(commands.get(i).getData() , true) +"</font>");
                    } else if(commands.get(i).getOut_in() == (byte)0x01) { // IN
                        stringBuilder.append("<br/><font color='red'>"+ HexUtils.hexify(commands.get(i).getData() , true) +"</font>");
                    }
                }
            }
        } else {
            stringBuilder.append("<font color='red'> NO HAY DATOS QUE MOSTRAR</font>");
        }
        jTextPane1.setText(stringBuilder.toString());
        jTextPane1.setCaretPosition(0);
    }
    
    /*
    private void showData() {
        boolean onlyApdu = jCheckBox1.isSelected();
        int head_apdu = 10;
        StringBuilder stringBuilder = new StringBuilder();
        if(apdus.size()> 0) {
            for(int i=0; i< apdus.size(); i = i + 2) {
                if(onlyApdu) {
                    if(apdus.get(i)[0] == (byte)0x6F) { // si solo es un comando APDU
                        stringBuilder.append("<font color='blue'>"+ ( ( head_apdu < apdus.get(i).length) ?  HexUtils.hexify(HexUtils.subArray(apdus.get(i),head_apdu, apdus.get(i).length-head_apdu) , true)  : "..." )+"</font>");
                        stringBuilder.append("<br/><font color='red'>"+ ( ( head_apdu < apdus.get(i+1).length) ?  HexUtils.hexify(HexUtils.subArray(apdus.get(i+1),head_apdu, apdus.get(i+1).length-head_apdu) , true)  : "..." )+"</font>");
                        stringBuilder.append("<br/><font color='gray'>----------------------------------------------------------------------------------</font><br/>");
                    }
                } else {
                    stringBuilder.append("<font color='blue'>"+ HexUtils.hexify(apdus.get(i), true) +"</font>");
                    stringBuilder.append("<br/><font color='red'>"+ HexUtils.hexify(apdus.get(i+1), true) +"</font>");
                    stringBuilder.append("<br/><font color='gray'>----------------------------------------------------------------------------------</font><br/>");
                }
            }
        } else {
            stringBuilder.append("<font color='red'> NO HAY DATOS QUE MOSTRAR</font>");
        }
        jTextPane1.setText(stringBuilder.toString());
        jTextPane1.setCaretPosition(0);
    }
    */
    
    
    int fila = 1;
    
    
    private void enableDragAndDrop() {
        
        DropTarget target=new DropTarget(jTextPane1,new DropTargetListener(){
            public void dragEnter(DropTargetDragEvent e) {
                SimpleAttributeSet background = new SimpleAttributeSet();
                StyleConstants.setBackground(background, MyColor.ALERT_DANGER_BG);
                jTextPane1.getStyledDocument().setParagraphAttributes(0, 
                        jTextPane1.getDocument().getLength(), background, false);
            }
            
            public void dragExit(DropTargetEvent e) {
                SimpleAttributeSet background = new SimpleAttributeSet();
                StyleConstants.setBackground(background, MyColor.COLOR_WHITE);
                jTextPane1.getStyledDocument().setParagraphAttributes(0, 
                        jTextPane1.getDocument().getLength(), background, false);
            }
            
            public void dragOver(DropTargetDragEvent e) {
                
                SimpleAttributeSet background = new SimpleAttributeSet();
                StyleConstants.setBackground(background, MyColor.ALERT_DANGER_BG);
                jTextPane1.getStyledDocument().setParagraphAttributes(0, 
                        jTextPane1.getDocument().getLength(), background, false);

            }
            
            public void dropActionChanged(DropTargetDragEvent e){
            
            }
            
            public void drop(DropTargetDropEvent e) {
                try {
                    // Accept the drop first, important!
                    e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    
                    // Get the files that are dropped as java.util.List
                    java.util.List list=(java.util.List) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    
                    // Now get the first file from the list,
                    File file=(File)list.get(0);
                    //jTextPane1.setBackground(MyColor.COLOR_WHITE);
                    
                    resetValues();
                    jLabel2.setText(file.getName());
                    showData(file.getAbsolutePath());
                } catch(Exception ex){
                    ex.printStackTrace();
                }
            }

            
        });
    }
    
    
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setText("Sniffer APDU :");

        jScrollPane2.setBackground(new java.awt.Color(255, 0, 51));
        jScrollPane2.setOpaque(false);
        jScrollPane2.setViewportView(jTextPane1);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText(" ");

        jCheckBox1.setText("Mostrar solo APDU");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2))
                            .addComponent(jCheckBox1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrameMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrameMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrameMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrameMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrameMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
