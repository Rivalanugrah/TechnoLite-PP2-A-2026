package util;

import java.util.regex.Pattern;

public class Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email))
            return true; // Email optional
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone))
            return false;
        return PHONE_PATTERN.matcher(phone.replaceAll("[\\s-]", "")).matches();
    }

    public static boolean isValidPassword(String password) {
        return !isEmpty(password) && password.length() >= 6;
    }

    public static boolean isPositiveNumber(String value) {
        try {
            double num = Double.parseDouble(value);
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNonNegativeInteger(String value) {
        try {
            int num = Integer.parseInt(value);
            return num >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveInteger(String value) {
        try {
            int num = Integer.parseInt(value);
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String validateLogin(String username, String password) {
        if (isEmpty(username))
            return "Username tidak boleh kosong!";
        if (isEmpty(password))
            return "Password tidak boleh kosong!";
        return null;
    }

    public static String validateRegister(String username, String password,
            String nama, String email) {
        if (isEmpty(username))
            return "Username tidak boleh kosong!";
        if (!isValidPassword(password))
            return "Password minimal 6 karakter!";
        if (isEmpty(nama))
            return "Nama tidak boleh kosong!";
        if (!isValidEmail(email))
            return "Format email tidak valid!";
        return null;
    }

    public static String validateBuku(String judul, String penulis,
            String harga, String stok) {
        if (isEmpty(judul))
            return "Judul tidak boleh kosong!";
        if (isEmpty(penulis))
            return "Penulis tidak boleh kosong!";
        if (!isPositiveNumber(harga))
            return "Harga harus angka positif!";
        if (!isNonNegativeInteger(stok))
            return "Stok harus angka >= 0!";
        return null;
    }

    public static String validatePembelian(int jumlah, int stokTersedia) {
        if (jumlah <= 0)
            return "Jumlah harus lebih dari 0!";
        if (jumlah > stokTersedia)
            return "Stok tidak mencukupi! Tersedia: " + stokTersedia;
        return null;
    }
}
