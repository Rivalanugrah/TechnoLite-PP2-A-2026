package util;

import model.Order;
import model.OrderDetail;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Utility class for exporting borrowing reports to PDF files.
 * Uses iTextPDF library for professional PDF generation.
 */
public class PDFExporter {

    // Define fonts
    private static Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    private static Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
    private static Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.GRAY);
    private static Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);

    // Colors
    private static BaseColor PRIMARY_COLOR = new BaseColor(41, 128, 185); // Blue
    private static BaseColor LIGHT_GRAY = new BaseColor(245, 245, 245);

    /**
     * Export peminjaman receipt to PDF
     */
    public static boolean exportPeminjaman(Order order, List<OrderDetail> details, String filePath) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm");
            SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("dd MMMM yyyy");

            // Header
            Paragraph header = new Paragraph();
            header.setAlignment(Element.ALIGN_CENTER);
            header.add(new Chunk("TANDA BUKTI PEMINJAMAN", TITLE_FONT));
            header.setSpacingAfter(5);
            document.add(header);

            // Subtitle
            Paragraph subtitle = new Paragraph("Perpustakaan TechnoLite", SMALL_FONT);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // Divider line
            addDividerLine(document);

            // Order Info Table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(15);
            infoTable.setSpacingAfter(15);
            infoTable.setWidths(new float[] { 1, 2 });

            addInfoRow(infoTable, "ID Peminjaman", "#" + order.getId());
            addInfoRow(infoTable, "Tanggal Pinjam", dateFormat.format(order.getTanggalPinjam()));
            addInfoRow(infoTable, "Tanggal Kembali", dateOnlyFormat.format(order.getTanggalKembali()));
            addInfoRow(infoTable, "Status", order.getStatus().toUpperCase());

            document.add(infoTable);

            // Books Section Header
            Paragraph booksHeader = new Paragraph("BUKU YANG DIPINJAM", HEADER_FONT);
            booksHeader.setSpacingBefore(10);
            booksHeader.setSpacingAfter(10);
            document.add(booksHeader);

            // Books Table
            PdfPTable booksTable = new PdfPTable(4);
            booksTable.setWidthPercentage(100);
            booksTable.setWidths(new float[] { 0.5f, 2.5f, 1.5f, 0.5f });

            // Table Header
            addTableHeader(booksTable, "No");
            addTableHeader(booksTable, "Judul Buku");
            addTableHeader(booksTable, "Penulis");
            addTableHeader(booksTable, "Qty");

            // Table Content
            int no = 1;
            for (OrderDetail detail : details) {
                addTableCell(booksTable, String.valueOf(no++), Element.ALIGN_CENTER);
                addTableCell(booksTable, detail.getBukuJudul(), Element.ALIGN_LEFT);
                addTableCell(booksTable, detail.getBukuPenulis(), Element.ALIGN_LEFT);
                addTableCell(booksTable, String.valueOf(detail.getJumlah()), Element.ALIGN_CENTER);
            }

            document.add(booksTable);

            // Footer note
            addDividerLine(document);

            Paragraph footer = new Paragraph();
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            footer.add(new Chunk("Harap kembalikan buku tepat waktu!", BOLD_FONT));
            footer.add(Chunk.NEWLINE);
            footer.add(new Chunk("Terima kasih telah menggunakan layanan kami.", SMALL_FONT));
            document.add(footer);

            document.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Export report to PDF
     */
    public static boolean exportReport(List<Order> orders, String filePath) {
        Document document = new Document(PageSize.A4.rotate(), 40, 40, 40, 40); // Landscape for report

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat reportDateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm");

            // Header
            Paragraph header = new Paragraph();
            header.setAlignment(Element.ALIGN_CENTER);
            header.add(new Chunk("LAPORAN PEMINJAMAN PERPUSTAKAAN", TITLE_FONT));
            header.setSpacingAfter(5);
            document.add(header);

            // Generated date
            Paragraph genDate = new Paragraph("Digenerate pada: " + reportDateFormat.format(new java.util.Date()),
                    SMALL_FONT);
            genDate.setAlignment(Element.ALIGN_CENTER);
            genDate.setSpacingAfter(20);
            document.add(genDate);

            addDividerLine(document);

            // Orders Table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(15);
            table.setWidths(new float[] { 0.8f, 2.5f, 1.5f, 1.5f, 1.2f });

            // Table Header
            addTableHeader(table, "ID");
            addTableHeader(table, "Peminjam");
            addTableHeader(table, "Tgl Pinjam");
            addTableHeader(table, "Tgl Kembali");
            addTableHeader(table, "Status");

            // Table Content
            int totalPinjam = 0;
            int totalKembali = 0;

            for (Order order : orders) {
                addTableCell(table, String.valueOf(order.getId()), Element.ALIGN_CENTER);

                String userName = order.getUserName() != null ? order.getUserName() : "-";
                if (userName.length() > 20) {
                    userName = userName.substring(0, 20) + "...";
                }
                addTableCell(table, userName, Element.ALIGN_LEFT);
                addTableCell(table, dateFormat.format(order.getTanggalPinjam()), Element.ALIGN_CENTER);
                addTableCell(table, dateFormat.format(order.getTanggalKembali()), Element.ALIGN_CENTER);

                // Status with color
                PdfPCell statusCell = new PdfPCell(new Phrase(order.getStatus(), NORMAL_FONT));
                statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                statusCell.setPadding(8);
                if ("dipinjam".equals(order.getStatus())) {
                    statusCell.setBackgroundColor(new BaseColor(255, 243, 205)); // Yellow-ish
                    totalPinjam++;
                } else if ("dikembalikan".equals(order.getStatus())) {
                    statusCell.setBackgroundColor(new BaseColor(212, 237, 218)); // Green-ish
                    totalKembali++;
                }
                table.addCell(statusCell);
            }

            document.add(table);

            // Summary Section
            addDividerLine(document);

            Paragraph summaryHeader = new Paragraph("RINGKASAN", HEADER_FONT);
            summaryHeader.setSpacingBefore(15);
            summaryHeader.setSpacingAfter(10);
            document.add(summaryHeader);

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(40);
            summaryTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            summaryTable.setWidths(new float[] { 2, 1 });

            addSummaryRow(summaryTable, "Total Peminjaman", String.valueOf(orders.size()));
            addSummaryRow(summaryTable, "Sedang Dipinjam", String.valueOf(totalPinjam));
            addSummaryRow(summaryTable, "Sudah Dikembalikan", String.valueOf(totalKembali));

            document.add(summaryTable);

            document.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper methods
    private static void addDividerLine(Document document) throws DocumentException {
        Paragraph line = new Paragraph();
        line.add(new Chunk(new LineSeparator(1, 100, PRIMARY_COLOR, Element.ALIGN_CENTER, -2)));
        line.setSpacingBefore(10);
        line.setSpacingAfter(10);
        document.add(line);
    }

    private static void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private static void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(PRIMARY_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10);
        cell.setBorderColor(BaseColor.WHITE);

        // Set font color to white for header
        Font whiteHeaderFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
        cell.setPhrase(new Phrase(text, whiteHeaderFont));

        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL_FONT));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(8);
        cell.setBorderColor(LIGHT_GRAY);
        table.addCell(cell);
    }

    private static void addSummaryRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, NORMAL_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        labelCell.setBackgroundColor(LIGHT_GRAY);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, BOLD_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setBackgroundColor(LIGHT_GRAY);
        table.addCell(valueCell);
    }
}
