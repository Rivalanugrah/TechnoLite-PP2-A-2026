package model;

public class OrderDetail {
    private int id;
    private int orderId;
    private int bukuId;
    private int jumlah;

    // For display purposes
    private String bukuJudul;
    private String bukuPenulis;

    public OrderDetail() {
    }

    public OrderDetail(int orderId, int bukuId, int jumlah) {
        this.orderId = orderId;
        this.bukuId = bukuId;
        this.jumlah = jumlah;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getBukuId() {
        return bukuId;
    }

    public void setBukuId(int bukuId) {
        this.bukuId = bukuId;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getBukuJudul() {
        return bukuJudul;
    }

    public void setBukuJudul(String bukuJudul) {
        this.bukuJudul = bukuJudul;
    }

    public String getBukuPenulis() {
        return bukuPenulis;
    }

    public void setBukuPenulis(String bukuPenulis) {
        this.bukuPenulis = bukuPenulis;
    }
}
