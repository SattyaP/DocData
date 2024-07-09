/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ta_alpro2;

import java.awt.CardLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author 3330
 */
public class DocData extends javax.swing.JFrame {

    Connection con;
    Statement stmt;
    ResultSet rs;
    String sql;
    PreparedStatement pstmt;

    public DocData() {
        initComponents();
        initDB();
        load_table1();
        load_table2();
    }

    private void handlePanel(String name) {
        CardLayout panel = (CardLayout) baseLayout.getLayout();
        panel.show(baseLayout, name);
    }

    private void handleRoles(String role) {
        if (role.equals("Doctor")) {
            handlePanel("panelDoctor");
        } else {
            JLabel[] field = {nama, nik, no, alamat, gender};

            for (JLabel component : field) {
                if (component.getName() != null) {
                    component.setVisible(false);
                    break;
                }
            }

            handlePanel("panelStaff");
        }
    }

    private void initDB() {
        try {
            con = Config.configDB();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

    }

    private void searchPasien(String query) {
        try {
            sql = "SELECT * FROM patients WHERE first_name LIKE ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "%" + query + "%");
            rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.print(rs.getString("first_name"));
                JLabel[] field = {nama, nik, no, alamat, gender};

                for (JLabel component : field) {
                    if (component.getName() != null) {
                        component.setVisible(true);
                        break;
                    }
                }
                
                notfound.setVisible(false);
                detail_btn.setEnabled(true);
                delete_btn.setEnabled(true);
                field[0].setText(rs.getString("first_name"));
                field[1].setText(rs.getString("last_name"));
                field[2].setText(rs.getString("contact_number"));
                field[3].setText(rs.getString("address"));
                field[4].setText(rs.getString("gender"));
            } else {
                JOptionPane.showMessageDialog(this, "Pasien tidak ditemukan");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
        private void load_table1() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("No Patient");
        model.addColumn("Code Patient");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Date of Birth");
        model.addColumn("Gender");
        model.addColumn("Contact Number");
        model.addColumn("address");
        model.addColumn("email");

        try {
            String sql = "SELECT patient_id, code_patients, first_name, last_name, date_of_birth, gender, contact_number, address, email FROM patients";
            java.sql.Connection conn = (Connection) Config.configDB();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet res = stmt.executeQuery(sql);
            while (res.next()) {
                model.addRow(new Object[]
                {res.getString("patient_id"), 
                res.getString("code_patients"), 
                res.getString("first_name"),
                res.getString("last_name"), 
                res.getString("date_of_birth"),
                res.getString("gender"),
                res.getString("contact_number"),
                res.getString("address"),
                res.getString("email")});
            }
            jTable1.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
        private void load_table2() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("No Record");
        model.addColumn("No Patient");
        model.addColumn("Date Visit");
        model.addColumn("No Doctor");
        model.addColumn("Diagnosis");
        model.addColumn("Treatment");
        model.addColumn("Notes");

        try {
            String sql = "SELECT record_id, patient_id, visit_date, doctor_id, diagnosis, treatment, notes FROM medicalrecords";
            java.sql.Connection conn = (Connection) Config.configDB();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet res = stmt.executeQuery(sql);
            while (res.next()) {
                model.addRow(new Object[]
                {res.getString("record_id"), 
                res.getString("patient_id"), 
                res.getString("visit_date"),
                res.getString("doctor_id"), 
                res.getString("diagnosis"),
                res.getString("treatment"),
                res.getString("notes")});
            }
            jTable2.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
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

        baseLayout = new javax.swing.JPanel();
        panelRiwayatPasien = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        rp_id_patient = new javax.swing.JTextField();
        rp_id_doctor = new javax.swing.JTextField();
        rp_date_visit = new javax.swing.JTextField();
        rp_diagnosis = new javax.swing.JTextField();
        rp_treatment = new javax.swing.JTextField();
        rp_notes = new javax.swing.JTextField();
        rp_id_record = new javax.swing.JTextField();
        btn_create_rp = new javax.swing.JButton();
        btn_delete_rp = new javax.swing.JButton();
        btn_update_rp = new javax.swing.JButton();
        btn_reset_rp = new javax.swing.JButton();
        detailPasien = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        p_code = new javax.swing.JTextField();
        p_last_name = new javax.swing.JTextField();
        p_first_name = new javax.swing.JTextField();
        p_date_of_birth = new javax.swing.JTextField();
        p_gender = new javax.swing.JTextField();
        p_number = new javax.swing.JTextField();
        p_address = new javax.swing.JTextField();
        p_email = new javax.swing.JTextField();
        p_id = new javax.swing.JTextField();
        btn_create = new javax.swing.JButton();
        btn_delete = new javax.swing.JButton();
        btn_update = new javax.swing.JButton();
        btn_reset = new javax.swing.JButton();
        panelAuth = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        email = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        panelStaff = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        form_search = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        nama = new javax.swing.JLabel();
        nik = new javax.swing.JLabel();
        no = new javax.swing.JLabel();
        alamat = new javax.swing.JLabel();
        gender = new javax.swing.JLabel();
        notfound = new javax.swing.JLabel();
        detail_btn = new javax.swing.JButton();
        delete_btn = new javax.swing.JButton();
        panelDoctor = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DocData");
        setSize(new java.awt.Dimension(1024, 600));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        baseLayout.setLayout(new java.awt.CardLayout());

        jLabel18.setText("No Record");

        jLabel19.setText("No Patient");

        jLabel20.setText("Date Visit");

        jLabel21.setText("No Doctor");

        jLabel22.setText("Diagnosis");

        jLabel23.setText("Treatment");

        jLabel24.setText("Notes");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        rp_date_visit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rp_date_visitActionPerformed(evt);
            }
        });

        btn_create_rp.setText("Create");
        btn_create_rp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_create_rpActionPerformed(evt);
            }
        });

        btn_delete_rp.setText("Delete");
        btn_delete_rp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_delete_rpActionPerformed(evt);
            }
        });

        btn_update_rp.setText("Update");
        btn_update_rp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_update_rpActionPerformed(evt);
            }
        });

        btn_reset_rp.setText("Reset");
        btn_reset_rp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_reset_rpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRiwayatPasienLayout = new javax.swing.GroupLayout(panelRiwayatPasien);
        panelRiwayatPasien.setLayout(panelRiwayatPasienLayout);
        panelRiwayatPasienLayout.setHorizontalGroup(
            panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRiwayatPasienLayout.createSequentialGroup()
                .addGroup(panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRiwayatPasienLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                                .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel24))
                        .addGap(52, 52, 52)
                        .addGroup(panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rp_notes, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rp_treatment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rp_diagnosis, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rp_id_doctor, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rp_id_patient, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rp_date_visit, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rp_id_record, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelRiwayatPasienLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(btn_create_rp)
                        .addGap(18, 18, 18)
                        .addComponent(btn_update_rp)
                        .addGap(18, 18, 18)
                        .addComponent(btn_delete_rp)
                        .addGap(18, 18, 18)
                        .addComponent(btn_reset_rp)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(110, 110, 110))
        );
        panelRiwayatPasienLayout.setVerticalGroup(
            panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRiwayatPasienLayout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelRiwayatPasienLayout.createSequentialGroup()
                        .addGroup(panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(rp_id_record, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(rp_id_patient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(rp_date_visit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(rp_id_doctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(rp_diagnosis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(rp_treatment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(rp_notes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(125, 125, 125)
                        .addGroup(panelRiwayatPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_create_rp)
                            .addComponent(btn_update_rp)
                            .addComponent(btn_delete_rp)
                            .addComponent(btn_reset_rp))))
                .addGap(112, 112, 112))
        );

        baseLayout.add(panelRiwayatPasien, "card6");

        jLabel9.setText("No Patiens");

        jLabel10.setText("Code Patiens");

        jLabel11.setText("First Name");

        jLabel12.setText("Last Name");

        jLabel13.setText("Date Of Birth");

        jLabel14.setText("Gender");

        jLabel15.setText("Contact Number");

        jLabel16.setText("Address");

        jLabel17.setText("Email");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        p_first_name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p_first_nameActionPerformed(evt);
            }
        });

        btn_create.setText("Create");
        btn_create.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_createActionPerformed(evt);
            }
        });

        btn_delete.setText("Delete");
        btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_deleteActionPerformed(evt);
            }
        });

        btn_update.setText("Update");
        btn_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updateActionPerformed(evt);
            }
        });

        btn_reset.setText("Reset");
        btn_reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_resetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout detailPasienLayout = new javax.swing.GroupLayout(detailPasien);
        detailPasien.setLayout(detailPasienLayout);
        detailPasienLayout.setHorizontalGroup(
            detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPasienLayout.createSequentialGroup()
                .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detailPasienLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel15))
                        .addGap(52, 52, 52)
                        .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(p_address, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(p_number, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(p_gender, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(p_date_of_birth, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(p_last_name, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(p_code, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(p_first_name, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(p_id, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(p_email, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(detailPasienLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(btn_create)
                        .addGap(18, 18, 18)
                        .addComponent(btn_update)
                        .addGap(18, 18, 18)
                        .addComponent(btn_delete)
                        .addGap(18, 18, 18)
                        .addComponent(btn_reset)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(110, 110, 110))
        );

        detailPasienLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {p_address, p_code, p_date_of_birth, p_email, p_first_name, p_gender, p_id, p_last_name, p_number});

        detailPasienLayout.setVerticalGroup(
            detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPasienLayout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(detailPasienLayout.createSequentialGroup()
                        .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(p_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(p_code, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(p_first_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(p_last_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(p_date_of_birth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(p_gender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(p_number, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(p_address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(p_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(45, 45, 45)
                        .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_create)
                            .addComponent(btn_update)
                            .addComponent(btn_delete)
                            .addComponent(btn_reset))))
                .addGap(112, 112, 112))
        );

        detailPasienLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {p_code, p_date_of_birth, p_first_name, p_gender, p_id, p_last_name, p_number});

        baseLayout.add(detailPasien, "detailPasien");

        panelAuth.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(60, 63, 65));
        jLabel2.setText("Welcome back");

        jLabel3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 102, 102));
        jLabel3.setText("Hopefully recording data will be easier, have a beautiful day");

        email.setText("fahmi@doctor.docdata.id");

        jLabel4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(60, 63, 65));
        jLabel4.setText("Email");

        jLabel5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(60, 63, 65));
        jLabel5.setText("Password");

        password.setText("123");

        btnLogin.setBackground(new java.awt.Color(51, 51, 255));
        btnLogin.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 255));
        jLabel6.setText("Forget Password ?");

        javax.swing.GroupLayout panelAuthLayout = new javax.swing.GroupLayout(panelAuth);
        panelAuth.setLayout(panelAuthLayout);
        panelAuthLayout.setHorizontalGroup(
            panelAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAuthLayout.createSequentialGroup()
                .addGap(418, 418, 418)
                .addComponent(jLabel2)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAuthLayout.createSequentialGroup()
                .addContainerGap(312, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(304, 304, 304))
            .addGroup(panelAuthLayout.createSequentialGroup()
                .addGap(299, 299, 299)
                .addGroup(panelAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addGroup(panelAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(email, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(password, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelAuthLayout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 139, Short.MAX_VALUE)
                            .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelAuthLayout.setVerticalGroup(
            panelAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAuthLayout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(50, 50, 50)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(panelAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(155, 155, 155))
        );

        baseLayout.add(panelAuth, "panelAuth");

        jLabel8.setText("Search Pasien");

        jButton2.setText("Cari");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        notfound.setText("Pasien tidak ditemukan");

        detail_btn.setText("Detail");
        detail_btn.setEnabled(false);
        detail_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detail_btnActionPerformed(evt);
            }
        });

        delete_btn.setText("Delete");
        delete_btn.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(nama)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(nik)
                        .addGap(65, 65, 65)
                        .addComponent(no)
                        .addGap(50, 50, 50)
                        .addComponent(alamat)
                        .addGap(40, 40, 40)
                        .addComponent(gender))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(notfound)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(detail_btn)
                .addGap(18, 18, 18)
                .addComponent(delete_btn)
                .addGap(22, 22, 22))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(notfound)
                        .addGap(0, 0, 0)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nama)
                            .addComponent(nik)
                            .addComponent(no)
                            .addComponent(alamat)
                            .addComponent(gender))
                        .addGap(38, 38, 38))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(detail_btn)
                            .addComponent(delete_btn))
                        .addGap(32, 32, 32))))
        );

        javax.swing.GroupLayout panelStaffLayout = new javax.swing.GroupLayout(panelStaff);
        panelStaff.setLayout(panelStaffLayout);
        panelStaffLayout.setHorizontalGroup(
            panelStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStaffLayout.createSequentialGroup()
                .addGap(253, 253, 253)
                .addGroup(panelStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8)
                    .addGroup(panelStaffLayout.createSequentialGroup()
                        .addComponent(form_search, javax.swing.GroupLayout.PREFERRED_SIZE, 497, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(141, Short.MAX_VALUE))
        );
        panelStaffLayout.setVerticalGroup(
            panelStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStaffLayout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addGroup(panelStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(form_search))
                .addGap(91, 91, 91)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(277, Short.MAX_VALUE))
        );

        baseLayout.add(panelStaff, "panelStaff");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1024, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Status :");

        jButton1.setText("READY");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 0, 0));
        jLabel7.setText("Not Ready yet");

        javax.swing.GroupLayout panelDoctorLayout = new javax.swing.GroupLayout(panelDoctor);
        panelDoctor.setLayout(panelDoctorLayout);
        panelDoctorLayout.setHorizontalGroup(
            panelDoctorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDoctorLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(panelDoctorLayout.createSequentialGroup()
                .addGap(290, 290, 290)
                .addGroup(panelDoctorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addGroup(panelDoctorLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDoctorLayout.setVerticalGroup(
            panelDoctorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDoctorLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addGroup(panelDoctorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addGap(0, 402, Short.MAX_VALUE))
        );

        baseLayout.add(panelDoctor, "panelDoctor");

        getContentPane().add(baseLayout, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1030, 600));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        try {
            sql = "SELECT email, password, username, role_name FROM users s JOIN roles r ON r.role_id = s.role_id WHERE email = '" + email.getText() + "' AND password='" + password.getText() + "'";
            con = Config.configDB();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                if (email.getText().equals(rs.getString("email")) && password.getText().equals(rs.getString("password"))) {
                    UserSession.getInstance(rs.getString("username"), rs.getString("role_name"));

                    UserSession session = UserSession.getInstance();
                    handleRoles(session.getRole());
                    rs.close();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, kombinasi Username dan Password anda salah");
            }

            rs.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        searchPasien(form_search.getText());
    }//GEN-LAST:event_jButton2ActionPerformed

    private void detail_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detail_btnActionPerformed
        handlePanel("detailPasien");
    }//GEN-LAST:event_detail_btnActionPerformed

    private void p_first_nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p_first_nameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_p_first_nameActionPerformed

    private void btn_createActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_createActionPerformed
        try {
            String sql = "INSERT INTO patients (patient_id, code_patients, first_name, last_name, date_of_birth, gender, contact_number, address, email) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            java.sql.Connection conn = (Connection) Config.configDB();
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, p_id.getText());
            pst.setString(2, p_code.getText()); 
            pst.setString(3, p_first_name.getText());
            pst.setString(4, p_last_name.getText()); 
            pst.setString(5, p_date_of_birth.getText()); 
            pst.setString(6, p_gender.getText());
            pst.setString(7, p_number.getText());
            pst.setString(8, p_address.getText()); 
            pst.setString(9, p_email.getText()); 
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil di Tambah");
            load_table1();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Data Gagal di Tambah " + e.getMessage());
        }      
    }//GEN-LAST:event_btn_createActionPerformed

    private void btn_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_updateActionPerformed
        try {
            String sql = "UPDATE patients SET patient_id = '" + p_id.getText() + "', code_patients = '"
                    + p_code.getText() + "', first_name = '" + p_first_name.getText() + "', last_name = '" + p_last_name.getText() 
                    + "', date_of_birth = '" + p_date_of_birth.getText() + "', gender = '" + p_gender.getText() + "', contact_number = '" + p_number.getText() 
                    + "', address = '" + p_address.getText() + "', email = '" + p_email.getText() + "' WHERE patient_id = '"
                    + p_id.getText() + "'";
            java.sql.Connection conn = (Connection) Config.configDB();
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil di Edit");
            load_table1();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Perubahan Data Gagal " + e.getMessage());
        }
    }//GEN-LAST:event_btn_updateActionPerformed

    private void btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_deleteActionPerformed
        try {
            String sql = "delete from patients where patient_id='" + p_id.getText() + "'";
            java.sql.Connection conn = (Connection) Config.configDB();
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Berhasil di Hapus");
            load_table1();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btn_deleteActionPerformed

    private void btn_resetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_resetActionPerformed
        p_id.setText("");
        p_code.setText("");
        p_first_name.setText("");
        p_last_name.setText("");
        p_date_of_birth.setText("");
        p_gender.setText("");
        p_number.setText("");
        p_address.setText("");
        p_email.setText("");
        JOptionPane.showMessageDialog(this, "Data Berhasil di Reset");
    }//GEN-LAST:event_btn_resetActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int baris = jTable1.rowAtPoint(evt.getPoint());

        String patient_id = jTable1.getValueAt(baris, 0).toString();
        p_id.setText(patient_id);

        String code_patients = jTable1.getValueAt(baris, 1).toString();
        p_code.setText(code_patients);

        String first_name = jTable1.getValueAt(baris, 2).toString();
        p_first_name.setText(first_name);

        String last_name = jTable1.getValueAt(baris, 3).toString();
        p_last_name.setText(last_name);

        String date_of_birth = jTable1.getValueAt(baris, 4).toString();
        p_date_of_birth.setText(date_of_birth);

        String gender = jTable1.getValueAt(baris, 5).toString();
        p_gender.setText(gender);

        String contact_number = jTable1.getValueAt(baris, 6).toString();
        p_number.setText(contact_number);

        String address = jTable1.getValueAt(baris, 7).toString();
        p_address.setText(address);

        String email = jTable1.getValueAt(baris, 8).toString();
        p_email.setText(email);
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        int baris = jTable2.rowAtPoint(evt.getPoint());

        String record_id = jTable2.getValueAt(baris, 0).toString();
        rp_id_record.setText(record_id);

        String patient_id = jTable2.getValueAt(baris, 1).toString();
        rp_id_patient.setText(patient_id);

        String visit_date = jTable2.getValueAt(baris, 2).toString();
        rp_date_visit.setText(visit_date);

        String doctor_id = jTable2.getValueAt(baris, 3).toString();
        rp_id_doctor.setText(doctor_id);

        String diagnosis = jTable2.getValueAt(baris, 4).toString();
        rp_diagnosis.setText(diagnosis);

        String treatment = jTable2.getValueAt(baris, 5).toString();
        rp_treatment.setText(treatment);

        String notes = jTable2.getValueAt(baris, 6).toString();
        rp_notes.setText(notes);
    }//GEN-LAST:event_jTable2MouseClicked

    private void rp_date_visitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_date_visitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_date_visitActionPerformed

    private void btn_create_rpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_create_rpActionPerformed
        try {
            String sql = "INSERT INTO medicalrecords (record_id, patient_id, visit_date, doctor_id, diagnosis, treatment, notes)"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            java.sql.Connection conn = (Connection) Config.configDB();
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, rp_id_record.getText());
            pst.setString(2, rp_id_patient.getText()); 
            pst.setString(3, rp_date_visit.getText());
            pst.setString(4, rp_id_doctor.getText()); 
            pst.setString(5, rp_diagnosis.getText()); 
            pst.setString(6, rp_treatment.getText());
            pst.setString(7, rp_notes.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil di Tambah");
            load_table2();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Data Gagal di Tambah " + e.getMessage());
        }      
    }//GEN-LAST:event_btn_create_rpActionPerformed

    private void btn_delete_rpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_delete_rpActionPerformed
        try {
            String sql = "delete from medicalrecords where record_id='" + rp_id_record.getText() + "'";
            java.sql.Connection conn = (Connection) Config.configDB();
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Berhasil di Hapus");
            load_table2();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btn_delete_rpActionPerformed

    private void btn_update_rpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_update_rpActionPerformed
        try {
            String sql = "UPDATE medicalrecords SET record_id = '" + rp_id_record.getText() + "', patient_id = '"
                    + rp_id_patient.getText() + "', visit_date = '" + rp_date_visit.getText() + "', doctor_id = '" + rp_id_doctor.getText() 
                    + "', diagnosis = '" + rp_diagnosis.getText() + "', treatment = '" + rp_treatment.getText() + "', notes = '" + rp_notes.getText() 
                    + "' WHERE record_id = '"
                    + rp_id_record.getText() + "'";
            java.sql.Connection conn = (Connection) Config.configDB();
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil di Edit");
            load_table2();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Perubahan Data Gagal " + e.getMessage());
        }
    }//GEN-LAST:event_btn_update_rpActionPerformed

    private void btn_reset_rpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_reset_rpActionPerformed
        rp_id_record.setText("");
        rp_id_patient.setText("");
        rp_date_visit.setText("");
        rp_id_doctor.setText("");
        rp_diagnosis.setText("");
        rp_treatment.setText("");
        rp_notes.setText("");
        JOptionPane.showMessageDialog(this, "Data Berhasil di Reset");
    }//GEN-LAST:event_btn_reset_rpActionPerformed

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
            java.util.logging.Logger.getLogger(DocData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DocData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DocData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DocData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DocData().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel alamat;
    private javax.swing.JPanel baseLayout;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btn_create;
    private javax.swing.JButton btn_create_rp;
    private javax.swing.JButton btn_delete;
    private javax.swing.JButton btn_delete_rp;
    private javax.swing.JButton btn_reset;
    private javax.swing.JButton btn_reset_rp;
    private javax.swing.JButton btn_update;
    private javax.swing.JButton btn_update_rp;
    private javax.swing.JButton delete_btn;
    private javax.swing.JPanel detailPasien;
    private javax.swing.JButton detail_btn;
    private javax.swing.JTextField email;
    private javax.swing.JTextField form_search;
    private javax.swing.JLabel gender;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel nama;
    private javax.swing.JLabel nik;
    private javax.swing.JLabel no;
    private javax.swing.JLabel notfound;
    private javax.swing.JTextField p_address;
    private javax.swing.JTextField p_code;
    private javax.swing.JTextField p_date_of_birth;
    private javax.swing.JTextField p_email;
    private javax.swing.JTextField p_first_name;
    private javax.swing.JTextField p_gender;
    private javax.swing.JTextField p_id;
    private javax.swing.JTextField p_last_name;
    private javax.swing.JTextField p_number;
    private javax.swing.JPanel panelAuth;
    private javax.swing.JPanel panelDoctor;
    private javax.swing.JPanel panelRiwayatPasien;
    private javax.swing.JPanel panelStaff;
    private javax.swing.JPasswordField password;
    private javax.swing.JTextField rp_date_visit;
    private javax.swing.JTextField rp_diagnosis;
    private javax.swing.JTextField rp_id_doctor;
    private javax.swing.JTextField rp_id_patient;
    private javax.swing.JTextField rp_id_record;
    private javax.swing.JTextField rp_notes;
    private javax.swing.JTextField rp_treatment;
    // End of variables declaration//GEN-END:variables
}
