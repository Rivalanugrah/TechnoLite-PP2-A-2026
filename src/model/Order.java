package model;

import java.sql.Timestamp;
import java.sql.Date;

public class Order {
    private int id;
    private int userId;
    private Timestamp tanggalPinjam;
    private Date tanggalKembali;
    private Date tanggalDikembalikan;
    private String status;
    private double denda;

    // For display purposes
    private String userName;

    // Late fee per day
    public static final double DENDA_PER_HARI = 20000;

    public Order() {
    }

    public Order(int userId, Date tanggalKembali) {
        this.userId = userId;
        this.tanggalKembali = tanggalKembali;
        this.status = "dipinjam";
        this.denda = 0;
    }

    // Calculate late fee
    public double hitungDenda() {
        if (tanggalKembali == null)
            return 0;

        Date today = new Date(System.currentTimeMillis());
        Date checkDate = tanggalDikembalikan != null ? tanggalDikembalikan : today;

        if (checkDate.after(tanggalKembali)) {
            long diffMillis = checkDate.getTime() - tanggalKembali.getTime();
            long diffDays = diffMillis / (24 * 60 * 60 * 1000);
            return diffDays * DENDA_PER_HARI;
        }
        return 0;
    }

    // Check if late
    public boolean isTerlambat() {
        if (tanggalKembali == null)
            return false;

        Date today = new Date(System.currentTimeMillis());
        Date checkDate = tanggalDikembalikan != null ? tanggalDikembalikan : today;

        return checkDate.after(tanggalKembali);
    }

    // Get days late
    public long getHariTerlambat() {
        if (tanggalKembali == null)
            return 0;

        Date today = new Date(System.currentTimeMillis());
        Date checkDate = tanggalDikembalikan != null ? tanggalDikembalikan : today;

        if (checkDate.after(tanggalKembali)) {
            long diffMillis = checkDate.getTime() - tanggalKembali.getTime();
            return diffMillis / (24 * 60 * 60 * 1000);
        }
        return 0;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getTanggalPinjam() {
        return tanggalPinjam;
    }

    public void setTanggalPinjam(Timestamp tanggalPinjam) {
        this.tanggalPinjam = tanggalPinjam;
    }

    public Date getTanggalKembali() {
        return tanggalKembali;
    }

    public void setTanggalKembali(Date tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }

    public Date getTanggalDikembalikan() {
        return tanggalDikembalikan;
    }

    public void setTanggalDikembalikan(Date tanggalDikembalikan) {
        this.tanggalDikembalikan = tanggalDikembalikan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getDenda() {
        return denda;
    }

    public void setDenda(double denda) {
        this.denda = denda;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
