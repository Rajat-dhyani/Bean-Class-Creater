
import java.io.*;
import java.sql.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rajat_000
 */
public class BeanClassCreator extends javax.swing.JFrame {

    /**
     * Creates new form BeanClassCreator
     */
     private Connection con;
    String str;
    String url="jdbc:mysql://localhost:3306/";
    String user="root";
    String pass="root";
    String primarykey="";
    
    public BeanClassCreator() {
        initComponents();
    }
    
      
    private void createBean()
    {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            
            con = DriverManager.getConnection(url+""+jTDatabase.getText() , user , pass);
            System.out.println("Done");
             
            String fname = jTTable.getText();
             
             PreparedStatement ps = con.prepareStatement("Select * from "+fname);
             ResultSet rs = ps.executeQuery();
             ResultSetMetaData rsmd = rs.getMetaData();
             
             int count = rsmd.getColumnCount();
             
             fname = toFirstUpperCase(fname);
            
             fname +="Bean";
             File f = new File(str+"\\\\"+ fname +".java");
             FileWriter fw = new FileWriter(f);
             PrintWriter pw = new PrintWriter(fw);
             
             pw.println("package "+jTPackage.getText()+";");
             pw.println();
             pw.println("import java.sql.*;");
             pw.println("import java.util.*;");
             pw.println("import " +jTConnection.getText()+"."+jTConnectionClass.getText()+";");
             pw.println();
                      
             pw.println("public class "+fname );
             pw.println("{");
             for (int i= 0; i<count;i++)
             { 
                 String type= getType(rsmd.getColumnType(i+1));
                 
                 pw.println("\t private "+type+" " +rsmd.getColumnName(i+1)+ ";" );
             }
                 
             pw.println("\t public "+fname+"()");
             pw.println("\t  {  }");             
             pw.print("\t public "+fname+"(");             
             
             for (int i= 0; i<count;i++)
             { 
                   String type= getType(rsmd.getColumnType(i+1));
                    
                 if ( i != count-1 )    
                     pw.print(type+" "+rsmd.getColumnName(i+1)+ "," );
                 else
                     pw.println(type+" "+rsmd.getColumnName(i+1)+")" );
                 
             }
             pw.println("\t {");
             for (int i= 0; i<count;i++)
             { 
                 pw.println("\t\t  this."+rsmd.getColumnName(i+1)+" = "+ rsmd.getColumnName(i+1)+";");
             }
             pw.println(" \t}");
             
             for (int i= 0; i<count;i++)
             {
                 String type = getType(rsmd.getColumnType(i+1));
                 String cname = rsmd.getColumnName(i+1);
                 
                 cname = toFirstUpperCase(cname);
             
                 pw.println("\t public "+type+" get"+ cname+"( )");
                 pw.println("\t {");
                 pw.println("\t\t  return "+rsmd.getColumnName(i+1)+";");
                 pw.println("\t  }");
                 pw.println("\t public void"+" set"+ cname+"( "+type+" "+ rsmd.getColumnName(i+1)+")");
                 pw.println("\t {");
                 pw.println("\t\t  this."+rsmd.getColumnName(i+1)+" = "+ rsmd.getColumnName(i+1)+";");
                 pw.println("\t  }");
            
             }
             
             pw.println("\t public void insert()throws SQLException" );
             pw.println("\t { " );
             pw.print("\t\t PreparedStatement ps = " +jTConnectionClass.getText()+".getConnection().prepareStatement(\"insert into "
                                                                + jTTable.getText() +" values(");
             
             for (int i=0; i<count ; i++)
             {
                 if ( i != count-1 )    
                    pw.print("?,");
                 else
                    pw.println("? )\" );");
             }
             
             for (int i=0; i<count ; i++)
             {
                 String type = getType(rsmd.getColumnType(i+1));
                 String cname = toFirstUpperCase(type);
                 
                 pw.println("\t\t ps.set"+cname+"("+(i+1)+","+rsmd.getColumnName(i+1)+");");
             }
             pw.println("\t\t  ps.executeUpdate();");
             pw.println("\t\t ps.close();");
             pw.println("\t }");
             
             pw.println("\t public void update()throws SQLException" );
             pw.println("\t { " );
             pw.print("\t\t PreparedStatement ps = " +jTConnectionClass.getText()+".getConnection().prepareStatement(\"update "
                                                                + jTTable.getText() +" set ");
             
             for (int i=0; i<count ; i++)
             {
                 if ( !( rsmd.getColumnName(i+1).equals(getPrimarykey())) && ( i != count-1 ) )    
                    pw.print(rsmd.getColumnName(i+1)+"= ?,");
                 else
                 if ( !( rsmd.getColumnName(i+1).equals(getPrimarykey())) && ( i == count-1 ) )    
                    pw.print(rsmd.getColumnName(i+1)+"= ?");
                 
                  
                     
             }
             pw.println(" where "+getPrimarykey()+"= ? \"); ");
             
             for (int i=0; i<count ; i++)
             {
                 String type = getType(rsmd.getColumnType(i+1));
                 String cname = toFirstUpperCase(type);
                 if (rsmd.getColumnName(i+1).equals(getPrimarykey()))
                     pw.println("\t\t ps.set"+cname+"("+count+","+rsmd.getColumnName(i+1)+");");
                 else
                     pw.println("\t\t ps.set"+cname+"("+i+","+rsmd.getColumnName(i+1)+");");
             }
             
             pw.println("\t\t int val = ps.executeUpdate();");
             pw.println("\t\t ps.close();");
             pw.println("\t\t if(val==0)" );
             pw.println("\t\t\t throw new SQLException(\"Record Not Found\");");
             pw.println("\t }");
             
             pw.println("\t public void delete()throws SQLException" );
             pw.println("\t { " );
             pw.print("\t\t PreparedStatement ps = " +jTConnectionClass.getText()+".getConnection().prepareStatement(\"delete from "
                                                                + jTTable.getText() +" ");
             
            
             pw.println("where "+getPrimarykey()+"= ? \"); ");
             
             for (int i=0; i<count ; i++)
             {
                 String type = getType(rsmd.getColumnType(i+1));
                 String cname = type;
                 
                 cname = toFirstUpperCase(cname);
                 
                 if (rsmd.getColumnName(i+1).equals(getPrimarykey()))
                     pw.println("\t\t ps.set"+cname+"("+1+","+rsmd.getColumnName(i+1)+");");
                 
             }
             pw.println("\t\t int val = ps.executeUpdate();");
             pw.println("\t\t ps.close();");
             pw.println("\t\t if(val==0)" );
             pw.println("\t\t\t throw new SQLException(\"Record Not Found\");");
             pw.println("\t }");
             
             
             pw.println("\t public void select()throws SQLException" );
             pw.println("\t { " );
             pw.print("\t\t PreparedStatement ps = " +jTConnectionClass.getText()+".getConnection().prepareStatement(\"select * from "
                                                                + jTTable.getText() +" ");
             
            
             pw.println("where "+getPrimarykey()+"= ? \"); ");
             for (int i=0; i<count ; i++)
             {
                 String type = getType(rsmd.getColumnType(i+1));
                 String cname = type;
                 
                 cname = toFirstUpperCase(cname);
                 
                 if (rsmd.getColumnName(i+1).equals(getPrimarykey()))
                     pw.println("\t\t ps.set"+cname+"( 1,"+rsmd.getColumnName(i+1)+");");
                 
             }  
             pw.println("\t\t ResultSet rs = ps.executeQuery();");
             pw.println("\t\t boolean chk = false;");
             pw.println("\t\t if(rs.next())");
             pw.println("\t\t {");
             pw.println("\t\t\t  chk=true;");
             
             for (int i=0; i<count ; i++)
             {
                 String type = getType(rsmd.getColumnType(i+1));
                 String cname = toFirstUpperCase(type);
                 
                 
                 if (!(rsmd.getColumnName(i+1).equals(getPrimarykey())))
                     pw.println("\t\t\t "+rsmd.getColumnName(i+1)+"="+"rs.get"+cname+"("+(i+1)+");");
                 
             }
             pw.println("\t\t }");
             pw.println("\t\t int val = ps.executeUpdate();");
             pw.println("\t\t ps.close();");
             pw.println("\t\t if(val==0)" );
             pw.println("\t\t\t throw new SQLException(\"Record Not Found\");");
             pw.println("\t }");
             
             
             pw.println("\t public static ArrayList<"+fname +"> getList()throws SQLException");
             pw.println("\t { " );
             pw.println("\t\t ArrayList<"+fname+"> al = new ArrayList<"+fname+">();" );
             pw.println("\t\t Statement st = " +jTConnectionClass.getText()+".getConnection().createStatement();" );
             pw.println("\t\t ResultSet rs= st.executeQuery(\"Select * from "+ jTTable.getText() +"\");" );
             pw.println("\t\t while(rs.next())" );
             pw.println("\t\t { " );
             pw.println("\t\t\t "+ fname +" "+ fname.toLowerCase() + " = new "+fname+"();");
             
             for (int i= 0; i<count;i++)
             {
                 String type = getType(rsmd.getColumnType(i+1));
                 String cname = toFirstUpperCase(rsmd.getColumnName(i+1));
                 String typename = toFirstUpperCase(type);
                 
                 
                pw.println("\t\t\t "+fname.toLowerCase()+".set"+cname+"(rs.get"+typename+"("+ (i+1) +"));" );        
             }
             pw.println("\t\t\t al.add("+ fname.toLowerCase()  +");");
             pw.println("\t\t }");
             pw.println("\t\t return  al;");
             pw.println("\t }" );
             pw.println("\t public static String[] getHead()throws SQLException" );
             pw.println("\t { " );
             pw.println("\t\t return " +jTConnectionClass.getText()+".getHead(\"Select * from "+ jTTable.getText() +"\");");
             pw.println("\t } ");
             pw.println("\t public static String[][] getDetails()throws SQLException");
             pw.println("\t { " );
             pw.println("\t\t return " +jTConnectionClass.getText()+".getDetails(\"Select * from "+ jTTable.getText() +"\");");
             pw.println("\t }");
             pw.println(" }");
             
             pw.flush();
             pw.close();
             fw.close();
             rs.close();
             ps.close();
             con.close();
             JOptionPane.showMessageDialog(null, "Bean Class Created Successfully");
             
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null,ex.getStackTrace());
        }
    }
    
    private String getPrimarykey()
    {
        try{
            DatabaseMetaData dbms = con.getMetaData();
            ResultSet rs = dbms.getPrimaryKeys(null, jTDatabase.getText(), jTTable.getText());
            
            
           if (rs.next()) {
                primarykey = rs.getString(4);
             }
           rs.close();
           
           
            
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        return primarykey;
    }
   private String toFirstUpperCase(String cname)
   {
       if (cname.charAt(0) >= 97 && cname.charAt(0)<=122)
                 {
                    cname = cname.replace(cname.charAt(0),(char)(cname.charAt(0) - 32));
                 }
       return cname;
   }
   private String getType(int t)
   {
       String type="";
                 
                 if (t == (Types.INTEGER))
                     type = "int";
                 else
                 if (t == (Types.DOUBLE))
                     type = "double";
                 else
                 if (t == (Types.FLOAT))
                     type = "float";
                 else
                 if (t ==(Types.DATE))
                     type = "java.sql.Date";
                 else
                 if (t ==(Types.VARCHAR))
                     type = "String";
        return type;
   }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTDatabase = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTTable = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTFileChooser = new javax.swing.JTextField();
        jBFileChooser = new javax.swing.JButton();
        jTPackage = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTConnectionClass = new javax.swing.JTextField();
        jBSubmit = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jTConnection = new javax.swing.JTextField();
        jBReset = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Enter Database Name:");

        jLabel2.setText("Enter Table  Name:");

        jTTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTTableActionPerformed(evt);
            }
        });

        jLabel3.setText("Enter location to be saved:");

        jBFileChooser.setText("Browse");
        jBFileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBFileChooserActionPerformed(evt);
            }
        });

        jTPackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTPackageActionPerformed(evt);
            }
        });

        jLabel4.setText("EnterBean Class' Package name:");

        jLabel5.setText("Enter Connection Class Pacakage name:");

        jTConnectionClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTConnectionClassActionPerformed(evt);
            }
        });

        jBSubmit.setText("Submit");
        jBSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSubmitActionPerformed(evt);
            }
        });

        jLabel6.setText("Enter Connection Class name:");

        jTConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTConnectionActionPerformed(evt);
            }
        });

        jBReset.setText("Reset");
        jBReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBResetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(115, 115, 115)
                .addComponent(jBSubmit)
                .addGap(41, 41, 41)
                .addComponent(jBReset)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jTTable, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                                    .addComponent(jTFileChooser, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTDatabase))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBFileChooser))
                            .addComponent(jTConnectionClass, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTPackage, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTConnection, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(73, 73, 73)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTPackage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTConnection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTConnectionClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTFileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBFileChooser))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBSubmit)
                    .addComponent(jBReset))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTTableActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTTableActionPerformed

    private void jBFileChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBFileChooserActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fileChooser.setDialogTitle("Browse");
        if ( fileChooser.showOpenDialog(jBFileChooser) == JFileChooser.APPROVE_OPTION)
        {
        }
        str = fileChooser.getSelectedFile().getAbsolutePath();
        jTFileChooser.setText(""+fileChooser.getSelectedFile());

        str = str.replace("\\", "\\\\");

    }//GEN-LAST:event_jBFileChooserActionPerformed

    private void jTPackageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTPackageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTPackageActionPerformed

    private void jTConnectionClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTConnectionClassActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTConnectionClassActionPerformed

    private void jBSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSubmitActionPerformed
        // TODO add your handling code here:
       
        
        createBean();
    }//GEN-LAST:event_jBSubmitActionPerformed

    private void jTConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTConnectionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTConnectionActionPerformed

    private void jBResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBResetActionPerformed
        // TODO add your handling code here:
        jTConnection.setText("");
        jTConnectionClass.setText("");
        jTDatabase.setText("");
        jTFileChooser.setText("");
        jTPackage.setText("");
        jTTable.setText("");
    }//GEN-LAST:event_jBResetActionPerformed

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
            java.util.logging.Logger.getLogger(BeanClassCreator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BeanClassCreator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BeanClassCreator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BeanClassCreator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BeanClassCreator().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBFileChooser;
    private javax.swing.JButton jBReset;
    private javax.swing.JButton jBSubmit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField jTConnection;
    private javax.swing.JTextField jTConnectionClass;
    private javax.swing.JTextField jTDatabase;
    private javax.swing.JTextField jTFileChooser;
    private javax.swing.JTextField jTPackage;
    private javax.swing.JTextField jTTable;
    // End of variables declaration//GEN-END:variables
}
