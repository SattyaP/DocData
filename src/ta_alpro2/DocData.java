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
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
    private SimpleWebSocketClient client;
    UserSession session;
    DefaultListModel<String> model;

    public DocData() {
        initComponents();
        initSocket();
        initDB();

        session = UserSession.getInstance();
        model = new DefaultListModel<>();

        Sidebar.setVisible(false);
        Navbar.setVisible(false);
    }

    public void updateAntrianModel(String message) {
        SwingUtilities.invokeLater(() -> {
            System.out.print(message);
            model.addElement(message);
            list_antrian.setModel(model);
        });
    }

    public void updateStatus(String bool) {
        SwingUtilities.invokeLater(() -> {
            status.setText(bool);
            if (bool.toLowerCase().contains("not")) {
                status.setForeground(Color.RED);
            } else if (bool.toLowerCase().contains("checking")) {
                status.setForeground(Color.ORANGE);
            } else {
                status.setForeground(Color.GREEN);
            }
        }
        );
    }

    public void displayAntrian(String code) {
        SwingUtilities.invokeLater(() -> {
            String kodePasien = code.split("NEXT_ANTRIAN:")[0];
            kodePasien = kodePasien.split(" - ")[0];
            codes.setText(kodePasien);
            btnPeriksa.setEnabled(true);
        });
    }

    private void handlePanel(String name) {
        CardLayout panel = (CardLayout) baseLayout.getLayout();
        panel.show(baseLayout, name);
    }

    private boolean isAntrianAlreadyExists(String newAntrian) {
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).equals(newAntrian)) {
                return true;
            }
        }
        return false;
    }

    private void handleRoles(String role) {
        if (role.equals("Doctor")) {
            handlePanel("panelDoctor");
            btnMain.setText("Riwayat Pasien");
            btnAddRiwayat.setVisible(false);
        } else {
            JLabel[] field = {nama, nik};

            for (JLabel component : field) {
                if (component.getName() != null) {
                    component.setVisible(false);
                    break;
                }
            }

            handlePanel("panelStaff");
            btnMain.setText("Data Pasien");
            btnAddRiwayat.setVisible(true);
        }
    }

    private void initDB() {
        try {
            con = Config.configDB();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    public void loadMedicalRecords(Boolean isDokter) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nama Dokter");
        model.addColumn("Tanggal Kunjungan");
        model.addColumn("Diagnosis");
        model.addColumn("Treatment");
        model.addColumn("Notes");

        try {
            if (isDokter) {
                sql = "SELECT username, s.created_at, diagnosis, treatment, notes FROM medicalrecords s JOIN users u ON u.user_id = s.doctor_id JOIN patients p ON p.patient_id = s.patient_id where code = '" + codes.getText().trim() + "'";
            } else {
                sql = "SELECT username, s.created_at, diagnosis, treatment, notes FROM medicalrecords s JOIN users u ON u.user_id = s.doctor_id JOIN patients p ON p.patient_id = s.patient_id where code = '" + nik.getText().trim() + "'";
            }

            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),});
            }

            medis_record.setModel(model);
        } catch (Exception e) {
            System.out.println("Gagal Mendapatkan data " + e.getMessage());
        }
    }

    public void initSocket() {
        try {
            client = new SimpleWebSocketClient(new URI("ws://localhost:8881"), this);
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private static class SimpleWebSocketClient extends WebSocketClient {

        private DocData app;

        public SimpleWebSocketClient(URI serverUri, DocData app) {
            super(serverUri);
            this.app = app;
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            System.out.println("Connected to server");
        }

        public void onMessage(String message) {
            if (message.toLowerCase().contains("ready") || message.toLowerCase().contains("checking")) {
                app.updateStatus(message);
            } else if (message.startsWith("NEXT_ANTRIAN:")) {
                app.displayAntrian(message.substring("NEXT_ANTRIAN:".length()));
            } else if (message.startsWith("ADD_ANTRIAN:")) {
                app.updateAntrianModel(message.substring("ADD_ANTRIAN:".length()));
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("Connection closed: " + reason);
        }

        @Override
        public void onError(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void logout() {
        handlePanel("panelAuth");
        Sidebar.setVisible(false);
        Navbar.setVisible(false);
        session.clearSession();
    }

    private void clear() {
        p_first_name.setText("");
        p_last_name.setText("");
        p_date_of_birth.setText("");
        p_gender.setSelectedItem("L");
        p_number.setText("");
        p_address.setText("");
        p_email.setText("");
    }

    private void searchPasien(String query) {
        try {
            if (!query.equals("")) {
                sql = "SELECT * FROM patients WHERE first_name LIKE ?";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, "%" + query + "%");
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    JLabel[] field = {nama, nik};

                    for (JLabel component : field) {
                        if (component.getName() != null) {
                            component.setVisible(true);
                            break;
                        }
                    }

                    notfound.setVisible(false);
                    detail_btn.setEnabled(true);
                    delete_btn.setEnabled(true);
                    edit_btn.setEnabled(true);
                    add_antrianBtn.setEnabled(true);
                    field[0].setText(rs.getString("first_name"));
                    field[1].setText(rs.getString("code"));
                    rs.close();
                } else {
                    JOptionPane.showMessageDialog(this, "Pasien tidak ditemukan");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Field search tidak boleh kosong");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
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

        Navbar = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        Sidebar = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        present_name = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        btnMain = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        baseLayout = new javax.swing.JPanel();
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
        notfound = new javax.swing.JLabel();
        detail_btn = new javax.swing.JButton();
        delete_btn = new javax.swing.JButton();
        edit_btn = new javax.swing.JButton();
        add_antrianBtn = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        list_antrian = new javax.swing.JList<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        status = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        clearAntrian = new javax.swing.JButton();
        panelDoctor = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btn_status = new javax.swing.JButton();
        status_doctor = new javax.swing.JLabel();
        medical_check = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        codes = new javax.swing.JLabel();
        btnPeriksa = new javax.swing.JButton();
        detailPasien = new javax.swing.JPanel();
        heading_detail = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        d_name = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        d_nik = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        d_no = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        d_address = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        d_jk = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        d_created = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        medis_record = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        btnAddRiwayat = new javax.swing.JButton();
        createPasien = new javax.swing.JPanel();
        headingForm = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        p_first_name = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        p_last_name = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        p_date_of_birth = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        p_gender = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        p_number = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        p_address = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        p_email = new javax.swing.JTextField();
        btnSubmit = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        dashboard = new javax.swing.JPanel();
        heading_detail1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        total_pasien = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        panelSetting = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        username = new javax.swing.JLabel();
        emails = new javax.swing.JLabel();
        rolest = new javax.swing.JLabel();
        tambahRiwayat = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        rp_id_patient = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        rp_id_doctor = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        rp_diagnosis = new javax.swing.JTextArea();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        rp_treatment = new javax.swing.JTextArea();
        jLabel36 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        rp_notes = new javax.swing.JTextArea();
        jButton4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DocData");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);
        setSize(new java.awt.Dimension(1024, 600));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Navbar.setBackground(new java.awt.Color(255, 255, 255));
        Navbar.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(153, 153, 153)));

        jLabel50.setIcon(new javax.swing.ImageIcon("E:\\Downloads\\Kuliah\\Semester 2\\Algoritma Pemrograman 2\\TA\\1.jpeg")); // NOI18N

        javax.swing.GroupLayout NavbarLayout = new javax.swing.GroupLayout(Navbar);
        Navbar.setLayout(NavbarLayout);
        NavbarLayout.setHorizontalGroup(
            NavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavbarLayout.createSequentialGroup()
                .addGap(414, 414, 414)
                .addComponent(jLabel50)
                .addContainerGap(446, Short.MAX_VALUE))
        );
        NavbarLayout.setVerticalGroup(
            NavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavbarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(23, 23, 23))
        );

        getContentPane().add(Navbar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1030, 60));

        Sidebar.setBackground(new java.awt.Color(255, 255, 255));
        Sidebar.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 1, new java.awt.Color(153, 153, 153)));

        jLabel46.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel46.setText("Selamat Datang");

        present_name.setFont(new java.awt.Font("Poppins", 1, 15)); // NOI18N
        present_name.setText("Dr. Kevin SH.S");

        jLabel48.setText("Foto Doctor");
        jLabel48.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton10.setBackground(new java.awt.Color(255, 0, 0));
        jButton10.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("Logout");
        jButton10.setBorder(null);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButton11.setText("Dashboard");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        btnMain.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnMain.setText("Data Pasien");
        btnMain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMainActionPerformed(evt);
            }
        });

        jButton13.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButton13.setText("Setting");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SidebarLayout = new javax.swing.GroupLayout(Sidebar);
        Sidebar.setLayout(SidebarLayout);
        SidebarLayout.setHorizontalGroup(
            SidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SidebarLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(SidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(present_name, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
            .addGroup(SidebarLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(SidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(31, 31, 31))
        );
        SidebarLayout.setVerticalGroup(
            SidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SidebarLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(SidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SidebarLayout.createSequentialGroup()
                        .addComponent(jLabel46)
                        .addGap(18, 18, 18)
                        .addComponent(present_name)
                        .addGap(18, 18, 18)
                        .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                .addGap(87, 87, 87)
                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMain, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(183, 183, 183))
        );

        getContentPane().add(Sidebar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, -1, 560));

        baseLayout.setLayout(new java.awt.CardLayout());

        panelAuth.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(60, 63, 65));
        jLabel2.setText("Welcome back");

        jLabel3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 102, 102));
        jLabel3.setText("Hopefully recording data will be easier, have a beautiful day");

        email.setText("satya@docdata.id");

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
                .addGap(51, 51, 51)
                .addGroup(panelAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAuthLayout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel4)
                        .addComponent(jLabel5)
                        .addGroup(panelAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(email, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(password, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelAuthLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 139, Short.MAX_VALUE)
                                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(315, Short.MAX_VALUE))
        );
        panelAuthLayout.setVerticalGroup(
            panelAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAuthLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
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
                .addContainerGap(159, Short.MAX_VALUE))
        );

        baseLayout.add(panelAuth, "panelAuth");

        panelStaff.setBackground(new java.awt.Color(241, 242, 246));
        panelStaff.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel8.setText("Antrian");
        panelStaff.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 340, -1, -1));

        form_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                form_searchActionPerformed(evt);
            }
        });
        panelStaff.add(form_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 570, 40));

        jButton2.setBackground(new java.awt.Color(0, 0, 255));
        jButton2.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Cari");
        jButton2.setBorder(null);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        panelStaff.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 80, 121, 36));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        nama.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        nik.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        notfound.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        notfound.setText("Pasien tidak ditemukan");

        detail_btn.setBackground(new java.awt.Color(51, 51, 51));
        detail_btn.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        detail_btn.setForeground(new java.awt.Color(255, 255, 255));
        detail_btn.setText("Detail");
        detail_btn.setEnabled(false);
        detail_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detail_btnActionPerformed(evt);
            }
        });

        delete_btn.setBackground(new java.awt.Color(255, 0, 0));
        delete_btn.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        delete_btn.setText("Delete");
        delete_btn.setEnabled(false);
        delete_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_btnActionPerformed(evt);
            }
        });

        edit_btn.setBackground(new java.awt.Color(255, 255, 0));
        edit_btn.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        edit_btn.setText("Edit");
        edit_btn.setEnabled(false);
        edit_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_btnActionPerformed(evt);
            }
        });

        add_antrianBtn.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        add_antrianBtn.setText("Tambah Antrian");
        add_antrianBtn.setEnabled(false);
        add_antrianBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_antrianBtnActionPerformed(evt);
            }
        });

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
                        .addComponent(nik))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(notfound)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
                .addComponent(add_antrianBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(detail_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edit_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(delete_btn)
                .addGap(22, 22, 22))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(notfound)
                        .addGap(0, 0, 0)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nama)
                            .addComponent(nik))
                        .addGap(38, 38, 38))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(detail_btn)
                            .addComponent(delete_btn)
                            .addComponent(edit_btn)
                            .addComponent(add_antrianBtn))
                        .addGap(32, 32, 32))))
        );

        panelStaff.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, 710, 100));

        jButton3.setBackground(new java.awt.Color(51, 51, 51));
        jButton3.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Tambah Pasien");
        jButton3.setBorder(null);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        panelStaff.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 140, 121, 38));

        list_antrian.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        list_antrian.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                list_antrianMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(list_antrian);

        panelStaff.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 380, 250, 140));

        jLabel10.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel10.setText("Data Pasien");
        panelStaff.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, -1));

        jLabel23.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel23.setText("Status Doctor :");
        panelStaff.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 30, -1, -1));

        status.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        status.setForeground(new java.awt.Color(255, 0, 0));
        status.setText("Not Ready yet");
        panelStaff.add(status, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 30, -1, -1));

        jButton1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton1.setText("NEXT ANTRIAN");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        panelStaff.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 410, 150, 70));

        clearAntrian.setBackground(new java.awt.Color(204, 0, 0));
        clearAntrian.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        clearAntrian.setForeground(new java.awt.Color(255, 255, 255));
        clearAntrian.setText("Bersihkan Antrian");
        clearAntrian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAntrianActionPerformed(evt);
            }
        });
        panelStaff.add(clearAntrian, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 420, 160, 50));

        baseLayout.add(panelStaff, "panelStaff");

        jLabel1.setFont(new java.awt.Font("Poppins", 0, 24)); // NOI18N
        jLabel1.setText("Status :");

        btn_status.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btn_status.setText("Ready");
        btn_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_statusActionPerformed(evt);
            }
        });

        status_doctor.setFont(new java.awt.Font("Poppins", 0, 24)); // NOI18N
        status_doctor.setForeground(new java.awt.Color(255, 0, 0));
        status_doctor.setText("Not Ready yet");

        medical_check.setBackground(new java.awt.Color(255, 255, 255));
        medical_check.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel24.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel24.setText("Code Pasien :");

        btnPeriksa.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        btnPeriksa.setText("Mulai Memeriksa");
        btnPeriksa.setEnabled(false);
        btnPeriksa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPeriksaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout medical_checkLayout = new javax.swing.GroupLayout(medical_check);
        medical_check.setLayout(medical_checkLayout);
        medical_checkLayout.setHorizontalGroup(
            medical_checkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(medical_checkLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(medical_checkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPeriksa)
                    .addGroup(medical_checkLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addGap(31, 31, 31)
                        .addComponent(codes)))
                .addContainerGap(487, Short.MAX_VALUE))
        );
        medical_checkLayout.setVerticalGroup(
            medical_checkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(medical_checkLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(medical_checkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(codes))
                .addGap(18, 18, 18)
                .addComponent(btnPeriksa)
                .addContainerGap(261, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelDoctorLayout = new javax.swing.GroupLayout(panelDoctor);
        panelDoctor.setLayout(panelDoctorLayout);
        panelDoctorLayout.setHorizontalGroup(
            panelDoctorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDoctorLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(panelDoctorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_status)
                    .addGroup(panelDoctorLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(status_doctor))
                    .addComponent(medical_check, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        panelDoctorLayout.setVerticalGroup(
            panelDoctorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDoctorLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addGroup(panelDoctorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(status_doctor))
                .addGap(18, 18, 18)
                .addComponent(btn_status)
                .addGap(29, 29, 29)
                .addComponent(medical_check, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        baseLayout.add(panelDoctor, "panelDoctor");

        detailPasien.setBackground(new java.awt.Color(241, 242, 246));

        heading_detail.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        heading_detail.setText("Detail Pasien");

        jLabel9.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel9.setText("Nama Pasien");

        d_name.setEditable(false);

        jLabel11.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel11.setText("Kode Unik");

        d_nik.setEditable(false);
        d_nik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                d_nikActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel12.setText("No Telepon");

        d_no.setEditable(false);

        jLabel13.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel13.setText("Alamat");

        d_address.setEditable(false);

        jLabel14.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel14.setText("Jenis Kelamin");

        d_jk.setEditable(false);

        jLabel15.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel15.setText("Tanggal Bergabung");

        d_created.setEditable(false);
        d_created.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                d_createdActionPerformed(evt);
            }
        });

        medis_record.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nama Dokter", "Tanggal Kunjungan", "Diagnosis", "Treatment", "Notes"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(medis_record);

        jLabel7.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel7.setText("Riwayat Medis");

        btnAddRiwayat.setText("Tambah Riwayat");
        btnAddRiwayat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRiwayatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout detailPasienLayout = new javax.swing.GroupLayout(detailPasien);
        detailPasien.setLayout(detailPasienLayout);
        detailPasienLayout.setHorizontalGroup(
            detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPasienLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(detailPasienLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddRiwayat))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 717, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(heading_detail)
                    .addGroup(detailPasienLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(170, 170, 170)
                        .addComponent(jLabel12))
                    .addGroup(detailPasienLayout.createSequentialGroup()
                        .addComponent(d_name, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(124, 124, 124)
                        .addComponent(d_no, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(detailPasienLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(185, 185, 185)
                        .addComponent(jLabel13))
                    .addGroup(detailPasienLayout.createSequentialGroup()
                        .addComponent(d_nik, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(124, 124, 124)
                        .addComponent(d_address, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(detailPasienLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(168, 168, 168)
                        .addComponent(jLabel15))
                    .addGroup(detailPasienLayout.createSequentialGroup()
                        .addComponent(d_jk, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(124, 124, 124)
                        .addComponent(d_created, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(243, 243, 243))
        );
        detailPasienLayout.setVerticalGroup(
            detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPasienLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(heading_detail)
                .addGap(18, 18, 18)
                .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel12))
                .addGap(6, 6, 6)
                .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(d_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(d_no, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel13))
                .addGap(6, 6, 6)
                .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(d_nik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(d_address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15))
                .addGap(6, 6, 6)
                .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(d_jk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(d_created, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(detailPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(btnAddRiwayat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        baseLayout.add(detailPasien, "detailPasien");

        createPasien.setBackground(new java.awt.Color(241, 242, 246));

        headingForm.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        headingForm.setText("Tambah Pasien");

        jLabel16.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel16.setText("First Name");

        jLabel17.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel17.setText("Last Name");

        p_last_name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p_last_nameActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel18.setText("Tgl Lahir");

        jLabel19.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel19.setText("Jenis Kelamin");

        p_gender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "L", "P" }));

        jLabel20.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel20.setText("Contact Pasien");

        jLabel21.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel21.setText("Alamat");

        jLabel22.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel22.setText("Email");

        btnSubmit.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        btnSubmit.setText("Tambah");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(102, 102, 102));
        jButton5.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Back");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout createPasienLayout = new javax.swing.GroupLayout(createPasien);
        createPasien.setLayout(createPasienLayout);
        createPasienLayout.setHorizontalGroup(
            createPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createPasienLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(createPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(headingForm)
                    .addGroup(createPasienLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(193, 193, 193)
                        .addComponent(jLabel20))
                    .addGroup(createPasienLayout.createSequentialGroup()
                        .addComponent(p_first_name, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(104, 104, 104)
                        .addComponent(p_number, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(createPasienLayout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(194, 194, 194)
                        .addComponent(jLabel21))
                    .addGroup(createPasienLayout.createSequentialGroup()
                        .addComponent(p_last_name, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(104, 104, 104)
                        .addComponent(p_address, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(createPasienLayout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(205, 205, 205)
                        .addComponent(jLabel22))
                    .addGroup(createPasienLayout.createSequentialGroup()
                        .addComponent(p_date_of_birth, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(104, 104, 104)
                        .addComponent(p_email, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel19)
                    .addComponent(p_gender, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(createPasienLayout.createSequentialGroup()
                        .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(333, Short.MAX_VALUE))
        );
        createPasienLayout.setVerticalGroup(
            createPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createPasienLayout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(headingForm)
                .addGap(43, 43, 43)
                .addGroup(createPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel20))
                .addGap(12, 12, 12)
                .addGroup(createPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(p_first_name, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(p_number, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(createPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel21))
                .addGap(12, 12, 12)
                .addGroup(createPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(p_last_name, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(p_address, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(createPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(jLabel22))
                .addGap(12, 12, 12)
                .addGroup(createPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(p_date_of_birth, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(p_email, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel19)
                .addGap(12, 12, 12)
                .addComponent(p_gender, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(createPasienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(75, Short.MAX_VALUE))
        );

        baseLayout.add(createPasien, "createPasien");
        createPasien.getAccessibleContext().setAccessibleName("");

        heading_detail1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        heading_detail1.setText("Dashboard");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        total_pasien.setFont(new java.awt.Font("Poppins", 0, 36)); // NOI18N
        total_pasien.setText("0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(total_pasien)
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(total_pasien)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jLabel26.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel26.setText("Total Pasien");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jLabel26)))
                .addContainerGap(540, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26)
                .addContainerGap(271, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dashboardLayout = new javax.swing.GroupLayout(dashboard);
        dashboard.setLayout(dashboardLayout);
        dashboardLayout.setHorizontalGroup(
            dashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardLayout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(dashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(heading_detail1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        dashboardLayout.setVerticalGroup(
            dashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardLayout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(heading_detail1)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        baseLayout.add(dashboard, "dashboard");

        jLabel25.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel25.setText("Settings Account");

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel27.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel27.setText("Username :");

        jLabel28.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel28.setText("Email :");

        jLabel29.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel29.setText("Jabatan :");

        username.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        emails.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        rolest.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addGap(18, 18, 18)
                        .addComponent(username))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28)
                            .addComponent(jLabel29))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rolest)
                            .addComponent(emails))))
                .addContainerGap(438, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(username))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(emails))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(rolest))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelSettingLayout = new javax.swing.GroupLayout(panelSetting);
        panelSetting.setLayout(panelSettingLayout);
        panelSettingLayout.setHorizontalGroup(
            panelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSettingLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(panelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addContainerGap(162, Short.MAX_VALUE))
        );
        panelSettingLayout.setVerticalGroup(
            panelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSettingLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel25)
                .addGap(29, 29, 29)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(289, Short.MAX_VALUE))
        );

        baseLayout.add(panelSetting, "panelSetting");

        jLabel30.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel30.setText("Tambah Riwayat");

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel31.setText("ID Pasien");

        jLabel33.setText("ID Doctor");

        jLabel34.setText("Diagnosis");

        rp_diagnosis.setColumns(20);
        rp_diagnosis.setRows(5);
        jScrollPane2.setViewportView(rp_diagnosis);

        jLabel35.setText("Penanganan");

        rp_treatment.setColumns(20);
        rp_treatment.setRows(5);
        jScrollPane4.setViewportView(rp_treatment);

        jLabel36.setText("Notes");

        rp_notes.setColumns(20);
        rp_notes.setRows(5);
        jScrollPane5.setViewportView(rp_notes);

        jButton4.setText("Submit");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel31)
                        .addComponent(jLabel33)
                        .addComponent(rp_id_patient, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                        .addComponent(rp_id_doctor))
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(105, 105, 105)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel35)
                    .addComponent(jLabel34)
                    .addComponent(jLabel36)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                    .addComponent(jScrollPane4)
                    .addComponent(jScrollPane5))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(jLabel34))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel35)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel36))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(rp_id_patient, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel33)
                        .addGap(18, 18, 18)
                        .addComponent(rp_id_doctor, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tambahRiwayatLayout = new javax.swing.GroupLayout(tambahRiwayat);
        tambahRiwayat.setLayout(tambahRiwayatLayout);
        tambahRiwayatLayout.setHorizontalGroup(
            tambahRiwayatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tambahRiwayatLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(tambahRiwayatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        tambahRiwayatLayout.setVerticalGroup(
            tambahRiwayatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tambahRiwayatLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        baseLayout.add(tambahRiwayat, "tambahRiwayat");

        getContentPane().add(baseLayout, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, 780, 560));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1030, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 610, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1030, 610));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        try {
            sql = "SELECT user_id, email, password, username, role_name FROM users s JOIN roles r ON r.role_id = s.role_id WHERE email = '" + email.getText() + "' AND password='" + password.getText() + "'";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                if (email.getText().trim().equals(rs.getString("email")) && password.getText().equals(rs.getString("password"))) {
                    UserSession.getInstance(rs.getString("username"), rs.getString("role_name"), rs.getString("user_id"));

                    Sidebar.setVisible(true);
                    Navbar.setVisible(true);

                    session = UserSession.getInstance();
                    handleRoles(session.getRole());
                    rs.close();

                    if (session.getRole().equals("Doctor")) {
                        present_name.setText("Dr. " + session.getUsername());
                    } else {
                        present_name.setText("Staff. " + session.getUsername());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Maaf, kombinasi Username dan Password anda salah");
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
        try {
            handlePanel("detailPasien");
            sql = "select * from patients where code = ?";
            pstmt = con.prepareStatement(sql);
            String niks = nik.getText().trim();
            pstmt.setString(1, niks);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                d_name.setText(rs.getString("first_name") + " " + rs.getString("last_name"));
                d_nik.setText(rs.getString("code"));
                d_no.setText(rs.getString("contact_number"));
                d_jk.setText(rs.getString("gender"));
                d_address.setText(rs.getString("address"));
                d_created.setText(rs.getString("created_at"));
                loadMedicalRecords(false);
                btnAddRiwayat.setVisible(false);
            } else {
                System.out.print("Data Tidak Ditemukan");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_detail_btnActionPerformed

    private void d_createdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_d_createdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_d_createdActionPerformed

    private void edit_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_btnActionPerformed
        try {
            handlePanel("createPasien");
            headingForm.setText("Edit Pasien");
            btnSubmit.setText("Update Pasien");

            sql = "select * from patients where code = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, nik.getText());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                p_first_name.setText(rs.getString("first_name"));
                p_last_name.setText(rs.getString("last_name"));
                p_date_of_birth.setText(rs.getString("date_of_birth"));
                p_gender.setSelectedItem(rs.getString("gender"));
                p_number.setText(rs.getString("contact_number"));
                p_address.setText(rs.getString("address"));
                p_email.setText(rs.getString("email"));
            } else {
                JOptionPane.showMessageDialog(this, "Pasien tidak ditemukan");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_edit_btnActionPerformed

    private void btn_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_statusActionPerformed
        if (btn_status.getText().equals("Ready") & status_doctor.getText().equals("Not Ready yet")) {
            btn_status.setText("Selesai");
            status_doctor.setText("Ready");
            status_doctor.setForeground(Color.GREEN);
            client.send("Ready");
        } else {
            btn_status.setText("Ready");
            status_doctor.setText("Not Ready yet");
            status_doctor.setForeground(Color.RED);
            client.send("Not Ready yet");
        }
    }//GEN-LAST:event_btn_statusActionPerformed

    private void d_nikActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_d_nikActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_d_nikActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        clear();
        handlePanel("createPasien");
        headingForm.setText("Tambah Pasien");
        btnSubmit.setText("Tambah");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void p_last_nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p_last_nameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_p_last_nameActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        if (headingForm.getText().equals("Tambah Pasien")) {
            try {
                sql = "SELECT code FROM patients ORDER BY code DESC LIMIT 1";
                pstmt = con.prepareStatement(sql);
                rs = pstmt.executeQuery();

                int code = 0;
                if (rs.next()) {
                    code = Integer.parseInt(rs.getString("code"));
                }

                rs.close();

                sql = "INSERT INTO patients (code, first_name, last_name, date_of_birth, gender, contact_number, address, email) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                pstmt = con.prepareStatement(sql);
                pstmt.setInt(1, (code + 1));
                pstmt.setString(2, p_first_name.getText());
                pstmt.setString(3, p_last_name.getText());
                pstmt.setString(4, p_date_of_birth.getText());
                pstmt.setString(5, (String) p_gender.getSelectedItem());
                pstmt.setString(6, p_number.getText());
                pstmt.setString(7, p_address.getText());
                pstmt.setString(8, p_email.getText());
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Pasien Berhasil di Tambah");
                clear();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Data Gagal di Tambah " + e.getMessage());
            }
        } else {
            try {
                String sql = "UPDATE patients SET first_name = '" + p_first_name.getText() + "', last_name = '" + p_last_name.getText()
                        + "', date_of_birth = '" + p_date_of_birth.getText() + "', gender = '" + p_gender.getSelectedItem() + "', contact_number = '" + p_number.getText()
                        + "', address = '" + p_address.getText() + "', email = '" + p_email.getText() + "' WHERE code = '"
                        + nik.getText() + "'";
                pstmt = con.prepareStatement(sql);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Pasien Berhasil di Edit");

                nama.setText(p_first_name.getText());
                UserSession session = UserSession.getInstance();
                handleRoles(session.getRole());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Perubahan Data Gagal " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        handleRoles(session.getRole());
    }//GEN-LAST:event_jButton5ActionPerformed

    private void delete_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_btnActionPerformed
        try {
            int confirmation = JOptionPane.showConfirmDialog(null, "Apakah kamu benar - benar ingin menghapus pasien ini ?", "Hapus Pasien", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                String sql = "delete from patients where code='" + nik.getText() + "'";
                pstmt = con.prepareStatement(sql);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Pasien Berhasil di Hapus");

                nama.setText("");
                nik.setText("");
                notfound.setVisible(true);
                detail_btn.setEnabled(false);
                delete_btn.setEnabled(false);
                edit_btn.setEnabled(false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_delete_btnActionPerformed

    private void form_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_form_searchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_form_searchActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        handlePanel("dashboard");

        try {
            sql = "SELECT COUNT(patient_id) as total FROM patients";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                total_pasien.setText(rs.getString("total"));

                rs.close();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Data Gagal Diambil " + e.getMessage());
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void btnMainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMainActionPerformed
        handleRoles(session.getRole());
        if (session.getRole().equalsIgnoreCase("Dokter")) {
            loadMedicalRecords(true);
        }
    }//GEN-LAST:event_btnMainActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        logout();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void add_antrianBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_antrianBtnActionPerformed
        String codePatient = nik.getText().trim() + " - " + nama.getText().trim();
        if (!isAntrianAlreadyExists(codePatient)) {
            String addAntrianMessage = "ADD_ANTRIAN:" + nik.getText().trim() + " - " + nama.getText().trim();
            client.send(addAntrianMessage);
        } else {
            JOptionPane.showMessageDialog(this, "Pasien sudah masuk antrian");
        }
    }//GEN-LAST:event_add_antrianBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (!model.isEmpty()) {
            String nextAntrianMessage = "NEXT_ANTRIAN:" + model.getElementAt(0);
            if (status.getText().equals("Not Ready yet") || status.getText().toLowerCase().equals("checking")) {
                JOptionPane.showMessageDialog(this, "Dokter Belum Siap");
            } else {
                if (btnPeriksa.isEnabled() && status.getText().equals("Checking")) {
                    JOptionPane.showMessageDialog(this, "Sedang ada pasien berlangsung");
                } else {
                    client.send(nextAntrianMessage);
                    model.removeElementAt(0);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Antrian kosong");
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        handlePanel("panelSetting");

        try {
            sql = "SELECT username, email, role_name FROM users s join roles r on r.role_id = s.role_id WHERE username = '" + session.getUsername() + "'";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                username.setText(rs.getString("username"));
                emails.setText(rs.getString("email"));
                rolest.setText(rs.getString("role_name"));

                rs.close();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Data Gagal Diambil " + e.getMessage());
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void btnPeriksaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPeriksaActionPerformed
        try {
            handlePanel("detailPasien");
            client.send("Checking");

            btnAddRiwayat.setVisible(true);
            sql = "select * from patients where code = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, codes.getText());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                d_name.setText(rs.getString("first_name") + " " + rs.getString("last_name"));
                d_nik.setText(rs.getString("code"));
                d_no.setText(rs.getString("contact_number"));
                d_jk.setText(rs.getString("gender"));
                d_address.setText(rs.getString("address"));
                d_created.setText(rs.getString("created_at"));
                loadMedicalRecords(true);

            } else {
                System.out.print("Data Tidak Ditemukan");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btnPeriksaActionPerformed

    private void btnAddRiwayatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRiwayatActionPerformed

        try {
            sql = "select patient_id from patients where code = '" + codes.getText() + "'";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                handlePanel("tambahRiwayat");
                rp_id_patient.setText(rs.getString("patient_id"));
                rp_id_doctor.setText(session.getId());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Data Gagal di Tambah " + e.getMessage());
        }
    }//GEN-LAST:event_btnAddRiwayatActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {

            sql = "INSERT INTO medicalrecords (patient_id, doctor_id, diagnosis, treatment, notes)"
                    + "VALUES (?, ?, ?, ?, ?)";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, rp_id_patient.getText());
            pstmt.setString(2, rp_id_doctor.getText());
            pstmt.setString(3, rp_diagnosis.getText());
            pstmt.setString(4, rp_treatment.getText());
            pstmt.setString(5, rp_notes.getText());
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Riwayat Berhasil di Tambah");

            handlePanel("panelDoctor");
            client.send("Ready");

            codes.setText("");
            btnPeriksa.setEnabled(false);
            rp_id_patient.setText("");
            rp_id_doctor.setText("");
            rp_diagnosis.setText("");
            rp_treatment.setText("");
            rp_notes.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Riwayat Gagal di Tambah " + e.getMessage());
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void clearAntrianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearAntrianActionPerformed
        int confirmation = JOptionPane.showConfirmDialog(null, "Apakah kamu benar - benar ingin menghapus pasien ini ?", "Hapus Pasien", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            model.clear();
        }

    }//GEN-LAST:event_clearAntrianActionPerformed

    private void list_antrianMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_list_antrianMouseClicked
        int selectedIndex = list_antrian.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedElement = (String) list_antrian.getModel().getElementAt(selectedIndex);
            String[] parts = selectedElement.split(" - ");//            
            nama.setText(parts[1].trim());
            nik.setText(parts[0].trim());
        } else {
            nama.setText("");
            nik.setText("");
        }
    }//GEN-LAST:event_list_antrianMouseClicked

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
            java.util.logging.Logger.getLogger(DocData.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DocData.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DocData.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DocData.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
    private javax.swing.JPanel Navbar;
    private javax.swing.JPanel Sidebar;
    private javax.swing.JButton add_antrianBtn;
    private javax.swing.JPanel baseLayout;
    private javax.swing.JButton btnAddRiwayat;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnMain;
    private javax.swing.JButton btnPeriksa;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btn_status;
    private javax.swing.JButton clearAntrian;
    private javax.swing.JLabel codes;
    private javax.swing.JPanel createPasien;
    private javax.swing.JTextField d_address;
    private javax.swing.JTextField d_created;
    private javax.swing.JTextField d_jk;
    private javax.swing.JTextField d_name;
    private javax.swing.JTextField d_nik;
    private javax.swing.JTextField d_no;
    private javax.swing.JPanel dashboard;
    private javax.swing.JButton delete_btn;
    private javax.swing.JPanel detailPasien;
    private javax.swing.JButton detail_btn;
    private javax.swing.JButton edit_btn;
    private javax.swing.JTextField email;
    private javax.swing.JLabel emails;
    private javax.swing.JTextField form_search;
    private javax.swing.JLabel headingForm;
    private javax.swing.JLabel heading_detail;
    private javax.swing.JLabel heading_detail1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
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
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JList<String> list_antrian;
    private javax.swing.JPanel medical_check;
    private javax.swing.JTable medis_record;
    private javax.swing.JLabel nama;
    private javax.swing.JLabel nik;
    private javax.swing.JLabel notfound;
    private javax.swing.JTextField p_address;
    private javax.swing.JTextField p_date_of_birth;
    private javax.swing.JTextField p_email;
    private javax.swing.JTextField p_first_name;
    private javax.swing.JComboBox<String> p_gender;
    private javax.swing.JTextField p_last_name;
    private javax.swing.JTextField p_number;
    private javax.swing.JPanel panelAuth;
    private javax.swing.JPanel panelDoctor;
    private javax.swing.JPanel panelSetting;
    private javax.swing.JPanel panelStaff;
    private javax.swing.JPasswordField password;
    private javax.swing.JLabel present_name;
    private javax.swing.JLabel rolest;
    private javax.swing.JTextArea rp_diagnosis;
    private javax.swing.JTextField rp_id_doctor;
    private javax.swing.JTextField rp_id_patient;
    private javax.swing.JTextArea rp_notes;
    private javax.swing.JTextArea rp_treatment;
    private javax.swing.JLabel status;
    private javax.swing.JLabel status_doctor;
    private javax.swing.JPanel tambahRiwayat;
    private javax.swing.JLabel total_pasien;
    private javax.swing.JLabel username;
    // End of variables declaration//GEN-END:variables
}
