import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class MainApp extends JFrame {

    // --- KONFIGURASI TEMA (MODERN STYLE) ---
    // Warna Utama (Orange GadgetIn yang lebih tajam)
    private static final Color COL_PRIMARY = new Color(244, 81, 30); 
    private static final Color COL_PRIMARY_HOVER = new Color(216, 67, 21);
    
    // Warna Pendukung
    private static final Color COL_BG = new Color(248, 250, 252); // Putih kebiruan sangat muda
    private static final Color COL_WHITE = Color.WHITE;
    private static final Color COL_DARK_TEXT = new Color(33, 33, 33);
    private static final Color COL_GRAY_TEXT = new Color(117, 117, 117);
    
    // Warna Tombol Aksi
    private static final Color COL_SUCCESS = new Color(67, 160, 71); // Hijau
    private static final Color COL_DANGER = new Color(211, 47, 47);  // Merah

    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    // --- PATH DATABASE ---
    private static final String BASE_PATH = "Database";
    private static final String BARANG_FILE = "barang1.txt";
    private static final String TRANSAKSI_FILE = "transaksi.txt";

    // --- STATE ---
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String currentUserId;

    public MainApp() {
        setTitle("GadgetIn - Desktop App");
        setSize(1100, 750); // Sedikit diperlebar
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Menggunakan Font Rendering yang lebih halus
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Setup Database Folder
        new File(BASE_PATH).mkdirs();

        // Setup Layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Add Screens
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createRegisterPanel(), "REGISTER");
        
        add(mainPanel);
    }

    // ==========================================
    //           1. HELPER UI COMPONENTS (CUSTOM)
    // ==========================================
    
    // Custom Button Class agar terlihat Modern & Menonjol
    class ModernButton extends JButton {
        private Color color;
        private Color hoverColor;
        private boolean isOutline;

        public ModernButton(String text, Color baseColor, boolean outline) {
            super(text);
            this.color = baseColor;
            this.hoverColor = darken(baseColor);
            this.isOutline = outline;
            
            setFont(FONT_BOLD);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            if (outline) {
                setForeground(baseColor);
                setBorder(new EmptyBorder(10, 20, 10, 20));
            } else {
                setForeground(Color.WHITE);
                setBorder(new EmptyBorder(10, 25, 10, 25));
            }

            // Hover Effect
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if(isOutline) setForeground(hoverColor);
                    else setBackground(hoverColor);
                    repaint();
                }
                public void mouseExited(MouseEvent e) {
                    if(isOutline) setForeground(color);
                    else setBackground(color);
                    repaint();
                }
            });
        }

        // Helper untuk menggelapkan warna saat hover
        private Color darken(Color c) {
            return new Color(
                Math.max((int)(c.getRed() * 0.9), 0),
                Math.max((int)(c.getGreen() * 0.9), 0),
                Math.max((int)(c.getBlue() * 0.9), 0)
            );
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isOutline) {
                g2.setColor(getModel().isRollover() ? hoverColor : color);
                g2.setStroke(new BasicStroke(2));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-3, getHeight()-3, 15, 15));
            } else {
                g2.setColor(getModel().isRollover() ? hoverColor : color);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JButton createPrimaryButton(String text) {
        return new ModernButton(text, COL_PRIMARY, false);
    }

    private JButton createOutlineButton(String text) {
        return new ModernButton(text, COL_PRIMARY, true);
    }
    
    private JButton createDangerButton(String text) {
        return new ModernButton(text, COL_DANGER, false);
    }
    
    private JButton createSuccessButton(String text) {
        return new ModernButton(text, COL_SUCCESS, false);
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        // Padding di dalam text field
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(10, 15, 10, 15) 
        ));
        return field;
    }
    
    // Method untuk mempercantik JTable
    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(FONT_BODY);
        table.setGridColor(new Color(230,230,230));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(255, 224, 178)); // Orange sangat muda
        table.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(COL_PRIMARY);
        header.setForeground(Color.WHITE);
        header.setBorder(null);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
    }

    // ==========================================
    //              2. LOGIN & REGISTER
    // ==========================================

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COL_BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(COL_WHITE);
        // Efek shadow menggunakan border tebal bertingkat
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(40, 50, 40, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel title = new JLabel("GadgetIn");
        title.setFont(FONT_HEADER);
        title.setForeground(COL_PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(title, gbc);
        
        gbc.gridy++;
        JLabel subTitle = new JLabel("Silakan login untuk melanjutkan");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subTitle.setForeground(COL_GRAY_TEXT);
        subTitle.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(subTitle, gbc);

        gbc.gridy++; gbc.insets = new Insets(20, 0, 5, 0);
        card.add(new JLabel("User ID"), gbc);
        
        JTextField idField = createStyledField();
        idField.setColumns(22);
        gbc.gridy++; gbc.insets = new Insets(5, 0, 10, 0);
        card.add(idField, gbc);

        gbc.gridy++; gbc.insets = new Insets(5, 0, 5, 0);
        card.add(new JLabel("Password"), gbc);

        JPasswordField passField = new JPasswordField();
        passField.setBorder(idField.getBorder()); 
        gbc.gridy++; gbc.insets = new Insets(5, 0, 10, 0);
        card.add(passField, gbc);

        JButton btnLogin = createPrimaryButton("MASUK SEKARANG");
        gbc.gridy++; gbc.insets = new Insets(25, 0, 10, 0);
        card.add(btnLogin, gbc);

        JButton btnReg = createOutlineButton("DAFTAR BARU");
        gbc.gridy++; gbc.insets = new Insets(5, 0, 0, 0);
        card.add(btnReg, gbc);

        panel.add(card);

        btnReg.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));
        btnLogin.addActionListener(e -> doLogin(idField.getText(), new String(passField.getPassword())));

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COL_BG);
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(COL_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(30, 50, 30, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel title = new JLabel("Buat Akun");
        title.setFont(FONT_HEADER);
        title.setForeground(COL_PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(title, gbc);

        gbc.gridy++;
        JLabel lbl = new JLabel("User ID"); lbl.setFont(FONT_BOLD);
        card.add(lbl, gbc);
        JTextField idField = createStyledField();
        gbc.gridy++; card.add(idField, gbc);

        gbc.gridy++;
        JLabel lbl2 = new JLabel("Password"); lbl2.setFont(FONT_BOLD);
        card.add(lbl2, gbc);
        JPasswordField passField = new JPasswordField();
        passField.setBorder(idField.getBorder());
        gbc.gridy++; card.add(passField, gbc);

        gbc.gridy++;
        JLabel lbl3 = new JLabel("Tipe Akun"); lbl3.setFont(FONT_BOLD);
        card.add(lbl3, gbc);
        String[] roles = {"Customer", "Admin"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        roleBox.setFont(FONT_BODY);
        roleBox.setBackground(Color.WHITE);
        gbc.gridy++; card.add(roleBox, gbc);

        JButton btnSubmit = createPrimaryButton("DAFTAR SEKARANG");
        gbc.gridy++; gbc.insets = new Insets(25, 0, 5, 0);
        card.add(btnSubmit, gbc);

        JButton btnBack = new JButton("Sudah punya akun? Login");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBack.setForeground(COL_GRAY_TEXT);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy++;
        card.add(btnBack, gbc);

        panel.add(card);

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        btnSubmit.addActionListener(e -> doRegister(idField.getText(), new String(passField.getPassword()), (String) roleBox.getSelectedItem()));

        return panel;
    }

    // ==========================================
    //           3. ADMIN DASHBOARD
    // ==========================================

    private JPanel createAdminDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COL_WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0,0,2,0, new Color(230,230,230)),
                new EmptyBorder(15, 25, 15, 25)
        ));
        
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(FONT_HEADER);
        title.setForeground(COL_PRIMARY);
        title.setIcon(new UIManager().getIcon("FileView.computerIcon")); // Simple icon placeholder
        
        JButton logout = createDangerButton("Keluar");
        header.add(title, BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);
        
        // Tabs Customization
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BOLD);
        tabs.setBackground(COL_BG);
        tabs.addTab("📦 Manajemen Barang", createAdminInventoryPanel());
        tabs.addTab("✅ Approval Transaksi", createAdminApprovalPanel());
        
        panel.add(header, BorderLayout.NORTH);
        panel.add(tabs, BorderLayout.CENTER);

        logout.addActionListener(e -> {
            mainPanel.remove(panel);
            cardLayout.show(mainPanel, "LOGIN");
        });

        return panel;
    }

    private JPanel createAdminInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COL_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. TABLE SETUP ---
        String[] cols = {"Nama Barang", "Harga (Rp)", "Stok"};
        // Gunakan DefaultTableModel biasa agar BISA diedit
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        
        JTable table = new JTable(model);
        styleTable(table); // Terapkan styling yang sudah ada
        
        // Load data awal
        loadBarangTable(model);

        // --- 2. BUTTON PANEL (SAVE & ADD) ---
        JPanel actionPanel = new JPanel(new BorderLayout(10, 10));
        actionPanel.setBackground(COL_BG);

        // A. Tombol Save Changes
        JButton btnSaveChanges = new ModernButton("Save Change ", new Color(0, 121, 107), false); // Warna Teal/Cyan
        
        // B. Form Tambah Barang (Bagian Bawah)
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        formPanel.setBackground(COL_WHITE);
        formPanel.setBorder(new LineBorder(new Color(230,230,230), 1));
        
        JTextField nameF = createStyledField(); nameF.setColumns(10);
        JTextField priceF = createStyledField(); priceF.setColumns(8);
        JTextField stockF = createStyledField(); stockF.setColumns(5);
        JButton addBtn = createSuccessButton("+ Tambah Baru");

        formPanel.add(new JLabel("Nama:")); formPanel.add(nameF);
        formPanel.add(new JLabel("Harga:")); formPanel.add(priceF);
        formPanel.add(new JLabel("Stok:")); formPanel.add(stockF);
        formPanel.add(addBtn);

        // Gabungkan tombol Save dan Form
        actionPanel.add(btnSaveChanges, BorderLayout.NORTH);
        actionPanel.add(formPanel, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COL_WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        // --- 3. LOGIKA TOMBOL ---

        // LOGIKA: Tambah Barang Baru
        addBtn.addActionListener(e -> {
            try {
                String n = nameF.getText();
                double p = Double.parseDouble(priceF.getText());
                int s = Integer.parseInt(stockF.getText());
                
                // Format harga untuk tampilan tabel
                String pFormatted = formatRupiah(String.valueOf(p));
                
                // Tambah ke tabel GUI dulu
                model.addRow(new Object[]{n, pFormatted, s});
                
                // Kosongkan form
                nameF.setText(""); priceF.setText(""); stockF.setText("");
                
                // Otomatis simpan ke file agar sinkron
                saveTableDataToFile(model);
                
                JOptionPane.showMessageDialog(this, "Barang ditambahkan & Disimpan!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Input Error: Pastikan harga & stok angka.");
            }
        });

        // LOGIKA: Simpan Perubahan (Edit Tabel)
        btnSaveChanges.addActionListener(e -> {
            // Panggil method penyimpanan
            saveTableDataToFile(model);
        });

        return panel;
    }

    // --- Method Khusus untuk Menyimpan Data Tabel ke File ---
    private void saveTableDataToFile(DefaultTableModel model) {
        File f = new File(BASE_PATH, BARANG_FILE);
        try (PrintWriter pw = new PrintWriter(new FileWriter(f, false))) { // false = overwrite mode (timpa file lama)
            
            // Loop semua baris di tabel
            for (int i = 0; i < model.getRowCount(); i++) {
                String name = model.getValueAt(i, 0).toString();
                String priceStr = model.getValueAt(i, 1).toString(); // Masih format "Rp 10.000"
                String stock = model.getValueAt(i, 2).toString();

                // PENTING: Ubah format "Rp 10.000" kembali menjadi angka "10000.0"
                double rawPrice = parseRupiahToDouble(priceStr);

                // Tulis ke file: nama;harga_angka;stok
                pw.println(name + ";" + rawPrice + ";" + stock);
            }
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan ke database!");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + ex.getMessage());
        }
    }

    private JPanel createAdminApprovalPanel() {
        JPanel panel = new JPanel(new BorderLayout(15,15));
        panel.setBackground(COL_BG);
        panel.setBorder(new EmptyBorder(20,20,20,20));

        String[] cols = {"Customer", "Barang", "Harga", "Qty", "Total", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        styleTable(table);
        
        JButton refreshBtn = createOutlineButton("Refresh Data");
        JButton approveBtn = createSuccessButton("✔ Setujui Pesanan");
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(COL_BG);
        btnPanel.add(refreshBtn);
        btnPanel.add(approveBtn);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COL_WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        ActionListener loadData = e -> {
            model.setRowCount(0);
            File f = new File(BASE_PATH, TRANSAKSI_FILE);
            if(!f.exists()) return;
            try(Scanner sc = new Scanner(f)) {
                while(sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] p = line.split(";");
                    String hargaSatuan = formatRupiah(p[2]);
                    String totalHarga = formatRupiah(p[4]);
                    
                    model.addRow(new Object[]{p[0], p[1], hargaSatuan, p[3], totalHarga, p[5]});
                    
                }
            } catch(Exception ex) {}
        };
        
        refreshBtn.addActionListener(loadData);
        loadData.actionPerformed(null); 

        approveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih transaksi terlebih dahulu!");
                return;
            }
            String status = (String) model.getValueAt(row, 5);
            if(!"Pending".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this, "Transaksi ini sudah diproses.");
                return;
            }
            updateTransactionStatus(row, "Approved");
            loadData.actionPerformed(null);
            JOptionPane.showMessageDialog(this, "Penjualan Diterima!");
        });

        return panel;
    }

    // ==========================================
    //           4. CUSTOMER DASHBOARD
    // ==========================================

    private JPanel createCustomerDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Navbar
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(COL_WHITE);
        navbar.setBorder(new MatteBorder(0,0,1,0, new Color(230,230,230)));
        navbar.setPreferredSize(new Dimension(getWidth(), 70));
        
        JLabel brand = new JLabel(" GadgetIn Store ");
        brand.setIcon(UIManager.getIcon("FileView.floppyDriveIcon")); // Placeholder icon
        brand.setFont(new Font("Segoe UI", Font.BOLD, 26));
        brand.setForeground(COL_PRIMARY);
        brand.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        rightNav.setBackground(COL_WHITE);
        
        JButton btnMarket = new JButton("Belanja");
        JButton btnCart = createOutlineButton("🛒 Keranjang");
        JButton btnHist = new JButton("Pesanan Saya");
        JButton btnLogout = createDangerButton("Keluar");
        
        // Styling Text Buttons (Market & History)
        for(JButton b : Arrays.asList(btnMarket, btnHist)) {
            b.setFont(FONT_BOLD);
            b.setForeground(COL_DARK_TEXT);
            b.setContentAreaFilled(false); 
            b.setBorderPainted(false); 
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            b.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { b.setForeground(COL_PRIMARY); }
                public void mouseExited(MouseEvent e) { b.setForeground(COL_DARK_TEXT); }
            });
        }

        rightNav.add(btnMarket); rightNav.add(btnCart); rightNav.add(btnHist); rightNav.add(btnLogout);
        navbar.add(brand, BorderLayout.WEST);
        navbar.add(rightNav, BorderLayout.EAST);

        // Content Area
        JPanel contentArea = new JPanel(new CardLayout());
        contentArea.add(createMarketplacePanel(), "MARKET");
        contentArea.add(createCartPanel(), "CART");
        contentArea.add(createHistoryPanel(), "HISTORY");

        panel.add(navbar, BorderLayout.NORTH);
        panel.add(contentArea, BorderLayout.CENTER);

        // Nav Actions
        ActionListener goMarket = e -> {
            contentArea.remove(0); 
            contentArea.add(createMarketplacePanel(), "MARKET", 0); 
            ((CardLayout)contentArea.getLayout()).show(contentArea, "MARKET");
        };

        brand.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { goMarket.actionPerformed(null); }
        });
        btnMarket.addActionListener(goMarket);

        btnCart.addActionListener(e -> {
             CardLayout cl = (CardLayout) contentArea.getLayout();
             contentArea.add(createCartPanel(), "CART"); 
             cl.show(contentArea, "CART");
        });
        
        btnHist.addActionListener(e -> {
             CardLayout cl = (CardLayout) contentArea.getLayout();
             contentArea.add(createHistoryPanel(), "HISTORY");
             cl.show(contentArea, "HISTORY");
        });

        btnLogout.addActionListener(e -> {
            mainPanel.remove(panel);
            cardLayout.show(mainPanel, "LOGIN");
        });

        return panel;
    }

    // --- A. MARKETPLACE GRID ---
    private JPanel createMarketplacePanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COL_BG);
        
        // Banner
        JLabel banner = new JLabel("Selamat Datang, " + currentUserId + "! Temukan Gadget Impianmu.");
        banner.setFont(FONT_HEADER);
        banner.setBorder(new EmptyBorder(20, 30, 10, 30));
        wrapper.add(banner, BorderLayout.NORTH);

        // Grid Container
        JPanel grid = new JPanel(new GridLayout(0, 3, 20, 20)); 
        grid.setBackground(COL_BG);
        grid.setBorder(new EmptyBorder(20, 30, 30, 30));

        File f = new File(BASE_PATH, BARANG_FILE);
        if(f.exists()) {
            try(Scanner sc = new Scanner(f)) {
                while(sc.hasNextLine()) {
                    String[] p = sc.nextLine().split(";");
                    if(p.length >= 3) {
                        grid.add(createProductCard(p[0], Double.parseDouble(p[1]), Integer.parseInt(p[2])));
                    }
                }
            } catch(Exception e) {}
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createProductCard(String name, double price, int stock) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COL_WHITE);
        card.setPreferredSize(new Dimension(220, 290)); // Sedikit dipertinggi agar gambar muat
        
        // Shadow border halus
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230,230,230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // --- PERUBAHAN DISINI ---
        // Panggil method helper yang baru kita buat tadi
        JLabel icon = getProductImageLabel(name); 
        // ------------------------

        // Info Produk
        JPanel info = new JPanel(new GridLayout(3, 1, 5, 5));
        info.setBackground(COL_WHITE);
        
        JLabel nameLbl = new JLabel(name); 
        nameLbl.setFont(FONT_SUBHEADER);
        
        JLabel priceLbl = new JLabel("Rp " + String.format("%,.0f", price)); 
        priceLbl.setFont(FONT_BOLD); 
        priceLbl.setForeground(COL_PRIMARY);
        
        JLabel stockLbl = new JLabel("Stok Tersedia: " + stock); 
        stockLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        stockLbl.setForeground(COL_GRAY_TEXT);
        
        info.add(nameLbl); info.add(priceLbl); info.add(stockLbl);

        // Button (Tetap sama)
        JButton addBtn = createPrimaryButton("Tambah ke Keranjang");
        if(stock <= 0) {
            addBtn.setText("Stok Habis");
            addBtn.setEnabled(false);
            addBtn.setBackground(Color.GRAY);
        }

        card.add(icon, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        
        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setBackground(COL_WHITE);
        btnWrap.setBorder(new EmptyBorder(15,0,0,0));
        btnWrap.add(addBtn);
        card.add(btnWrap, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            String qtyStr = JOptionPane.showInputDialog(this, "Beli '" + name + "'\nMasukkan Jumlah:");
            if(qtyStr != null && !qtyStr.isEmpty()) {
                try {
                    int qty = Integer.parseInt(qtyStr);
                    if (qty > 0 && qty <= stock) {
                        addToCart(name, price, qty, stock);
                    } else {
                        JOptionPane.showMessageDialog(this, "Stok tidak mencukupi!");
                    }
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, "Masukkan angka yang valid!");
                }
            }
        });

        return card;
    }

    // Method Memuat Gambar dengan SMART RESIZE (Anti Gepeng)
    private JLabel getProductImageLabel(String productName) {
        // Batas maksimum ukuran (kotak pembatas)
        int maxW = 140;
        int maxH = 140;

        String[] extensions = {".png", ".jpg", ".jpeg"};
        File imageFile = null;

        for (String ext : extensions) {
            File f = new File("Images/" + productName + ext);
            if (f.exists()) {
                imageFile = f;
                break;
            }
        }

        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(maxW, maxH)); // Booking tempat agar layout rapi
        label.setBorder(new EmptyBorder(10, 0, 10, 0));

        if (imageFile != null) {
            try {
                ImageIcon originalIcon = new ImageIcon(imageFile.getPath());
                Image originalImage = originalIcon.getImage();

                int oWidth = originalIcon.getIconWidth();
                int oHeight = originalIcon.getIconHeight();
                
                // LOGIKA BARU: Hitung skala agar proporsional
                int newWidth = maxW;
                int newHeight = maxH;

                if (oWidth > oHeight) {
                    // Jika gambar Landscape (lebar), patokannya Width
                    newHeight = (int)((double)oHeight / oWidth * maxW);
                } else {
                    // Jika gambar Portrait (tinggi), patokannya Height
                    newWidth = (int)((double)oWidth / oHeight * maxH);
                }

                Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                setDefaultIcon(label);
            }
        } else {
            setDefaultIcon(label);
        }

        return label;
    }

    // Helper kecil untuk set icon default jika gambar tidak ada
    private void setDefaultIcon(JLabel label) {
        label.setText("📱");
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
    }
    // --- B. CART & CHECKOUT ---
private JPanel createCartPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(COL_BG);
    panel.setBorder(new EmptyBorder(20, 30, 20, 30));

    JLabel title = new JLabel("Keranjang Belanja");
    title.setFont(FONT_HEADER);
    title.setBorder(new EmptyBorder(0,0,20,0));

    String[] cols = {"Nama Barang", "Harga Satuan", "Jumlah", "Total"};
    
    // PERUBAHAN DISINI: Menggunakan model yang tidak bisa diedit
    DefaultTableModel model = createNonEditableModel(cols);
    
    JTable table = new JTable(model);
    styleTable(table);
    
    int grandTotal = loadCartData(model);

    JPanel footer = new JPanel(new BorderLayout());
    footer.setBackground(COL_WHITE);
    footer.setBorder(BorderFactory.createCompoundBorder(
        new LineBorder(new Color(230,230,230), 1),
        new EmptyBorder(20, 20, 20, 20)
    ));
    
    JLabel totalLbl = new JLabel("Total Bayar: Rp " + String.format("%,d", grandTotal));
    totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
    totalLbl.setForeground(COL_PRIMARY);
    
    JButton checkoutBtn = createPrimaryButton("CHECKOUT SEKARANG ➤");
    
    footer.add(totalLbl, BorderLayout.WEST);
    footer.add(checkoutBtn, BorderLayout.EAST);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    
    panel.add(title, BorderLayout.NORTH);
    panel.add(scroll, BorderLayout.CENTER);
    panel.add(footer, BorderLayout.SOUTH);

    checkoutBtn.addActionListener(e -> {
        if(model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Keranjang Kosong!");
            return;
        }
        processCheckout();
    });

    return panel;
}

    // --- C. HISTORY ---
private JPanel createHistoryPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(COL_BG);
    panel.setBorder(new EmptyBorder(20,30,20,30));
    
    JLabel title = new JLabel("Riwayat Pesanan Anda");
    title.setFont(FONT_HEADER);
    title.setBorder(new EmptyBorder(0,0,20,0));
    
    String[] cols = {"Nama Barang", "Jumlah", "Total Harga", "Status Pembayaran"};
    
    // PERUBAHAN DISINI: Menggunakan model yang tidak bisa diedit
    DefaultTableModel model = createNonEditableModel(cols);
    
    JTable table = new JTable(model);
    styleTable(table);

    File f = new File(BASE_PATH, TRANSAKSI_FILE);
    if(f.exists()) {
        try(Scanner sc = new Scanner(f)) {
            while(sc.hasNextLine()) {
                String[] p = sc.nextLine().split(";");
                if(p[0].equals(currentUserId)) {

                    String totalFormatted = formatRupiah(p[4]); 
                    model.addRow(new Object[]{p[1], p[3], totalFormatted, p[5]});
                }
            }
        } catch(Exception e){}
    }

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBorder(BorderFactory.createEmptyBorder());

    panel.add(title, BorderLayout.NORTH);
    panel.add(scroll, BorderLayout.CENTER);
    return panel;
}

    // ==========================================
    //           5. LOGIC & DATA PROCESSING
    // ==========================================

    private void doLogin(String id, String pass) {
        File folder = new File(BASE_PATH + "/" + id);
        if (!folder.exists()) {
            JOptionPane.showMessageDialog(this, "ID Tidak Ditemukan!");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(new File(folder, id + ".txt")))) {
            String fId = br.readLine();
            String fPass = br.readLine();
            String role = br.readLine();

            if (id.equals(fId) && pass.equals(fPass)) {
                currentUserId = id;
                if ("Admin".equalsIgnoreCase(role)) {
                    mainPanel.add(createAdminDashboard(), "ADMIN");
                    cardLayout.show(mainPanel, "ADMIN");
                } else {
                    mainPanel.add(createCustomerDashboard(), "CUSTOMER");
                    cardLayout.show(mainPanel, "CUSTOMER");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Password Salah!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doRegister(String id, String pass, String role) {
        if(id.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data tidak boleh kosong!");
            return;
        }
        File folder = new File(BASE_PATH + "/" + id);
        if (folder.exists()) {
            JOptionPane.showMessageDialog(this, "User ID sudah terpakai!");
            return;
        }
        folder.mkdirs();
        try (PrintWriter pw = new PrintWriter(new File(folder, id + ".txt"))) {
            pw.println(id);
            pw.println(pass);
            pw.println(role);
            JOptionPane.showMessageDialog(this, "Registrasi Berhasil! Silakan Login.");
            cardLayout.show(mainPanel, "LOGIN");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBarangTable(DefaultTableModel model) {
        model.setRowCount(0);
        try (Scanner sc = new Scanner(new File(BASE_PATH, BARANG_FILE))) {
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split(";");
                if(p.length >= 3) {
                    // --- PERUBAHAN DISINI ---
                    String hargaFormatted = formatRupiah(p[1]);
                    model.addRow(new Object[]{p[0], hargaFormatted, p[2]});
                    // ------------------------
                }
            }
        } catch (Exception e) {}
    }

    private void addToCart(String name, double price, int qty, int oldStock) {
        // Update Barang Stok
        List<String> lines = new ArrayList<>();
        File bf = new File(BASE_PATH, BARANG_FILE);
        try(Scanner sc = new Scanner(bf)) {
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] p = line.split(";");
                if(p[0].equalsIgnoreCase(name)) {
                    int newStock = Integer.parseInt(p[2]) - qty;
                    lines.add(p[0]+";"+p[1]+";"+newStock);
                } else {
                    lines.add(line);
                }
            }
        } catch(Exception e) {}

        try(PrintWriter pw = new PrintWriter(bf)) {
            for(String l : lines) pw.println(l);
        } catch(Exception e){}

        // Add to Keranjang
        double total = price * qty;
        try(PrintWriter pw = new PrintWriter(new FileWriter(BASE_PATH+"/keranjang_"+currentUserId+".txt", true))){
            pw.println(name+";"+price+";"+qty+";"+(int)total);
            JOptionPane.showMessageDialog(this, "Berhasil masuk keranjang!");
        } catch(Exception e){}
    }

    private int loadCartData(DefaultTableModel model) {
        int total = 0;
        File f = new File(BASE_PATH, "keranjang_" + currentUserId + ".txt");
        if(!f.exists()) return 0;
        
        try(Scanner sc = new Scanner(f)) {
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] p = line.split(";");
                // p[0]=Nama, p[1]=Harga, p[2]=Qty, p[3]=Total
                
                // --- PERUBAHAN DISINI ---
                // Kita format harga satuan (p[1]) dan total (p[3])
                String hargaFormatted = formatRupiah(p[1]);
                String totalFormatted = formatRupiah(p[3]);
                
                model.addRow(new Object[]{p[0], hargaFormatted, p[2], totalFormatted});
                // ------------------------
                
                total += Integer.parseInt(p[3]);
            }
        } catch(Exception e){}
        return total;
    }

    private void processCheckout() {
            String[] options = {"QRIS", "Bank Transfer", "COD"};
            int choice = JOptionPane.showOptionDialog(this, "Pilih Metode Pembayaran", "Checkout",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            
            String method = (choice >= 0) ? options[choice] : "Unknown";
            
            // Show Payment Info (Simulated from Transaksi.java logic)
            if(choice == 0) JOptionPane.showMessageDialog(this, "Scan QRIS: QR-" + UUID.randomUUID().toString().substring(0,8));
            else if(choice == 1) JOptionPane.showMessageDialog(this, "Transfer ke Bank ABC: 123-456-789");
            else JOptionPane.showMessageDialog(this, "Siapkan uang tunai saat kurir datang.");

            // Move Cart to Transaction
            File cartF = new File(BASE_PATH, "keranjang_" + currentUserId + ".txt");
            try(Scanner sc = new Scanner(cartF);
                PrintWriter transW = new PrintWriter(new FileWriter(BASE_PATH + "/" + TRANSAKSI_FILE, true))) {
                
                while(sc.hasNextLine()) {
                    String line = sc.nextLine(); // nama;harga;qty;total
                    transW.println(currentUserId + ";" + line + ";Pending");
                }
            } catch(Exception e){}
            
            cartF.delete(); // Clear cart
            JOptionPane.showMessageDialog(this, "Checkout Berhasil! Menunggu Persetujuan Admin.");
            
            // Refresh UI
            mainPanel.add(createCustomerDashboard(), "CUSTOMER");
            cardLayout.show(mainPanel, "CUSTOMER");
        }

    private void updateTransactionStatus(int rowIndex, String newStatus) {
        List<String> lines = new ArrayList<>();
        File f = new File(BASE_PATH, TRANSAKSI_FILE);
        try(Scanner sc = new Scanner(f)) {
            while(sc.hasNextLine()) lines.add(sc.nextLine());
        } catch(Exception e){}

        if(rowIndex >= 0 && rowIndex < lines.size()) {
            String[] p = lines.get(rowIndex).split(";");
            // Reconstruct line
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<5; i++) sb.append(p[i]).append(";");
            sb.append(newStatus);
            lines.set(rowIndex, sb.toString());
        }

        try(PrintWriter pw = new PrintWriter(f)) {
            for(String l : lines) pw.println(l);
        } catch(Exception e){}
    }

        // Helper untuk membuat Model Tabel yang TIDAK BISA DI-EDIT
        private DefaultTableModel createNonEditableModel(String[] cols) {
            return new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Mengunci semua sel agar tidak bisa diedit
                }
            };
        }

                // Helper untuk format angka ke Rupiah (Contoh: 10000000 -> Rp 10.000.000)
        private String formatRupiah(String numberStr) {
            try {
                double value = Double.parseDouble(numberStr);
                // Format angka: "%,.0f" memberi pemisah ribuan. 
                // .replace(',', '.') memaksa titik sebagai pemisah (style Indonesia)
                return "Rp " + String.format("%,.0f", value).replace(',', '.');
            } catch (Exception e) {
                return numberStr; // Jika error, kembalikan aslinya
            }
        }

            
    private double parseRupiahToDouble(String rupiahStr) {
        try {
            // Hapus "Rp ", titik, dan spasi
            String cleanStr = rupiahStr.replace("Rp", "")
                                    .replace(".", "")
                                    .replace(",", "") // jaga-jaga jika ada koma
                                    .trim();
            return Double.parseDouble(cleanStr);
        } catch (Exception e) {
            return 0; // Jika gagal parsing
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
    
}
