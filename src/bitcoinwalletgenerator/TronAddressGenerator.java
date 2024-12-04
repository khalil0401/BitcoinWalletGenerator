import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bitcoinj.core.Base58;

public class TronAddressGenerator {

    public static void main(String[] args) {
        // المفتاح العام بالصيغة الهيكسية
        String hexPublicKey = "03d7ab7edb7f3ef6c60a0b468f4fb048a3beb7bb6dd587b19613357985636c1e38";

        // تحويل المفتاح العام من هيكس إلى بايتات
        byte[] publicKey = hexStringToByteArray(hexPublicKey);

        // تطبيق SHA3-256 (Keccak-256) على المفتاح العام
        byte[] sha3Hash = sha3(publicKey);

        // أخذ آخر 20 بايت من الـ SHA3-256
        byte[] addressBytes = new byte[20];
        System.arraycopy(sha3Hash, sha3Hash.length - 20, addressBytes, 0, 20);

        // إضافة البادئة 0x41 (لتمييز العناوين في شبكة ترون)
        byte[] addressWithPrefix = new byte[addressBytes.length + 1];
        addressWithPrefix[0] = (byte) 0x41; // البادئة الخاصة بعناوين ترون
        System.arraycopy(addressBytes, 0, addressWithPrefix, 1, addressBytes.length);

        // ترميز العنوان باستخدام Base58Check
        String tronAddress = Base58.encode(addressWithPrefix);

        // طباعة العنوان الناتج
        System.out.println("Tron Address: " + tronAddress);
    }

    // دالة لتحويل هيكس إلى بايتات
    public static byte[] hexStringToByteArray(String hex) {
        int length = hex.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    // دالة لتطبيق SHA3-256 (Keccak-256)
    public static byte[] sha3(byte[] input) {
        KeccakDigest keccak = new KeccakDigest(256);  // SHA3-256
        keccak.update(input, 0, input.length);
        byte[] hash = new byte[keccak.getDigestSize()];
        keccak.doFinal(hash, 0);
        return hash;
    }
}
