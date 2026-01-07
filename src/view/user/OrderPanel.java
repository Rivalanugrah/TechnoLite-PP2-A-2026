package view.user;

import controller.OrderController;
import model.Order;
import model.OrderDetail;
import model.User;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderPanel extends JPanel {
    private User currentUser;
    private JTable orderTable;
    private JTable detailTable;
    private DefaultTableModel orderTableModel;
    private DefaultTableModel detailTableModel;
    private JLabel lblOrderCount;
    private OrderController orderController;
    private SimpleDateFormat dateFormat;
    private NumberFormat currencyFormat;

    private static final double DENDA_PER_HARI = 20000;

    // Modern colors
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color BG_COLOR = new Color(249, 250, 251);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    public OrderPanel(User user) {
        this.currentUser = user;
        this.orderController = new OrderController();
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy");
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        initComponents();
        loadOrders();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(BG_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Riwayat Peminjaman");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_PRIMARY);

        lblOrderCount = new JLabel("  •  0 peminjaman");
        lblOrderCount.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblOrderCount.setForeground(TEXT_SECONDARY);

        titlePanel.add(lblTitle);
        titlePanel.add(lblOrderCount);

        JButton btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setBackground(PRIMARY_COLOR);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadOrders());

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);

        // Orders Table (Top) - Added Tgl Dikembalikan column
        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBackground(CARD_COLOR);
        ordersPanel.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        String[] orderColumns = { "ID", "Tgl Pinjam", "Batas Kembali", "Tgl Dikembalikan", "Status", "Denda" };
        orderTableModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(orderTableModel);
        orderTable.setRowHeight(40);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setShowGrid(false);
        orderTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        orderTable.setSelectionBackground(new Color(79, 70, 229));
        orderTable.setSelectionForeground(Color.WHITE);

        JTableHeader header = orderTable.getTableHeader();
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(TEXT_SECONDARY);
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setPreferredSize(new Dimension(0, 38));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        orderTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        orderTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        orderTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane orderScroll = new JScrollPane(orderTable);
        orderScroll.setBorder(null);
        orderScroll.getViewport().setBackground(CARD_COLOR);

        ordersPanel.add(orderScroll);
        splitPane.setTopComponent(ordersPanel);

        // Order Details Table (Bottom)
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(CARD_COLOR);
        detailsPanel.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        JLabel lblDetail = new JLabel("  Buku yang Dipinjam");
        lblDetail.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblDetail.setForeground(TEXT_PRIMARY);
        lblDetail.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblDetail.setBackground(new Color(243, 244, 246));
        lblDetail.setOpaque(true);
        detailsPanel.add(lblDetail, BorderLayout.NORTH);

        String[] detailColumns = { "Judul Buku", "Penulis", "Jumlah" };
        detailTableModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        detailTable = new JTable(detailTableModel);
        detailTable.setRowHeight(35);
        detailTable.setShowGrid(false);
        detailTable.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JTableHeader detailHeader = detailTable.getTableHeader();
        detailHeader.setBackground(new Color(243, 244, 246));
        detailHeader.setForeground(TEXT_SECONDARY);
        detailHeader.setFont(new Font("SansSerif", Font.BOLD, 11));
        detailHeader.setPreferredSize(new Dimension(0, 35));

        JScrollPane detailScroll = new JScrollPane(detailTable);
        detailScroll.setBorder(null);
        detailScroll.getViewport().setBackground(CARD_COLOR);

        detailsPanel.add(detailScroll, BorderLayout.CENTER);
        splitPane.setBottomComponent(detailsPanel);

        add(splitPane, BorderLayout.CENTER);

        // Order selection listener
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                loadOrderDetails();
        });
    }

    private void loadOrders() {
        orderTableModel.setRowCount(0);
        detailTableModel.setRowCount(0);

        List<Order> orders = orderController.getByUserId(currentUser.getId());

        for (Order order : orders) {
            String statusDisplay = getStatusBadge(order);

            // Calculate or get denda
            double denda = order.getDenda();
            if ("dipinjam".equals(order.getStatus())) {
                denda = order.hitungDenda();
            }
            String dendaDisplay = denda > 0 ? currencyFormat.format(denda) : "-";

            // Show actual return date
            String tglDikembalikan = order.getTanggalDikembalikan() != null
                    ? dateFormat.format(order.getTanggalDikembalikan())
                    : "-";

            orderTableModel.addRow(new Object[] {
                    "#" + order.getId(),
                    dateFormat.format(order.getTanggalPinjam()),
                    dateFormat.format(order.getTanggalKembali()),
                    tglDikembalikan,
                    statusDisplay,
                    dendaDisplay
            });
        }

        lblOrderCount.setText("  •  " + orders.size() + " peminjaman");
    }

    private void loadOrderDetails() {
        detailTableModel.setRowCount(0);

        int row = orderTable.getSelectedRow();
        if (row >= 0) {
            String orderIdStr = (String) orderTableModel.getValueAt(row, 0);
            int orderId = Integer.parseInt(orderIdStr.replace("#", ""));

            List<OrderDetail> details = orderController.getOrderDetails(orderId);

            for (OrderDetail detail : details) {
                detailTableModel.addRow(new Object[] {
                        detail.getBukuJudul(),
                        detail.getBukuPenulis(),
                        detail.getJumlah() + " buku"
                });
            }
        }
    }

    private String getStatusBadge(Order order) {
        String status = order.getStatus();

        if ("dikembalikan".equals(status)) {
            if (order.getDenda() > 0) {
                return "Telat+Denda";
            }
            return "Dikembalikan";
        } else if ("dipinjam".equals(status)) {
            if (order.isTerlambat()) {
                long hari = order.getHariTerlambat();
                return "Terlambat " + hari + " hari!";
            }
            return "Dipinjam";
        }
        return status;
    }
}
