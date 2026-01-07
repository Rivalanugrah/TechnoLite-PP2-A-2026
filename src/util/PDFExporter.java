package util;

import model.Order;
import model.OrderDetail;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Utility class for exporting borrowing reports to text files.
 * For a library system.
 */
public class PDFExporter {

    // Export peminjaman receipt
    public static boolean exportPeminjaman(Order order, List<OrderDetail> details, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm");
            SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("dd MMMM yyyy");

            writer.println("================================================");
            writer.println("           TANDA BUKTI PEMINJAMAN               ");
            writer.println("================================================");
            writer.println();
            writer.println("ID Peminjaman : #" + order.getId());
            writer.println("Tgl Pinjam    : " + dateFormat.format(order.getTanggalPinjam()));
            writer.println("Tgl Kembali   : " + dateOnlyFormat.format(order.getTanggalKembali()));
            writer.println("Status        : " + order.getStatus().toUpperCase());
            writer.println();
            writer.println("------------------------------------------------");
            writer.println("BUKU YANG DIPINJAM:");
            writer.println("------------------------------------------------");

            for (OrderDetail detail : details) {
                writer.println(String.format("- %s (%s) x%d",
                        detail.getBukuJudul(),
                        detail.getBukuPenulis(),
                        detail.getJumlah()));
            }

            writer.println();
            writer.println("================================================");
            writer.println("Harap kembalikan buku tepat waktu!");
            writer.println("================================================");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Export report
    public static boolean exportReport(List<Order> orders, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            writer.println("================================================");
            writer.println("        LAPORAN PEMINJAMAN PERPUSTAKAAN         ");
            writer.println("================================================");
            writer.println();
            writer.println(String.format("%-6s %-15s %-12s %-12s %-12s",
                    "ID", "Peminjam", "Tgl Pinjam", "Tgl Kembali", "Status"));
            writer.println("------------------------------------------------");

            int totalPinjam = 0;
            int totalKembali = 0;
            int totalTerlambat = 0;

            for (Order order : orders) {
                writer.println(String.format("%-6d %-15s %-12s %-12s %-12s",
                        order.getId(),
                        order.getUserName() != null
                                ? (order.getUserName().length() > 12 ? order.getUserName().substring(0, 12)
                                        : order.getUserName())
                                : "-",
                        dateFormat.format(order.getTanggalPinjam()),
                        dateFormat.format(order.getTanggalKembali()),
                        order.getStatus()));

                if ("dipinjam".equals(order.getStatus())) {
                    totalPinjam++;
                } else if ("dikembalikan".equals(order.getStatus())) {
                    totalKembali++;
                }
            }

            writer.println();
            writer.println("================================================");
            writer.println("RINGKASAN:");
            writer.println("Total Peminjaman  : " + orders.size());
            writer.println("Sedang Dipinjam   : " + totalPinjam);
            writer.println("Sudah Dikembalikan: " + totalKembali);
            writer.println("================================================");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
