package bitcoinwalletgenerator;

import org.bitcoinj.core.Base58;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.Security;
import java.util.Arrays;
import java.util.List;

public class TronWallet {

    public String PrivateKey = "";
    public String PublicKey = "";
    public String Address = "";
  public  String Balance="0";
    static {
        // Register BouncyCastle as a security provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    // Method to encode using Base58Check with version byte
    private static String encodeChecked(int version, byte[] payload) {
        byte[] checksum = calculateChecksum(payload);
        byte[] result = new byte[payload.length + checksum.length];
        System.arraycopy(payload, 0, result, 0, payload.length);
        System.arraycopy(checksum, 0, result, payload.length, checksum.length);
        return Base58.encode(result);
    }

    // Method to calculate the checksum (first 4 bytes of the double SHA256 hash)
    private static byte[] calculateChecksum(byte[] payload) {
        try {
            byte[] firstHash = MessageDigest.getInstance("SHA-256").digest(payload);
            byte[] secondHash = MessageDigest.getInstance("SHA-256").digest(firstHash);
            byte[] checksum = new byte[4];
            System.arraycopy(secondHash, 0, checksum, 0, 4);
            return checksum;
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate checksum", e);
        }
    }

    // Generate TRON wallet from mnemonic and passphrase
    public void generateTronWallet(List<String> mnemonic, String passphrase) {
        try {
            int HARDENED_BIT = 0x80000000;
            // Generate seed from mnemonic
            byte[] seed = MnemonicCode.toSeed(mnemonic, passphrase);
            DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seed);
            // المشتقات حسب المسار m/44'/195'/0'/0/0
            DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, 44 | HARDENED_BIT);
            DeterministicKey coinTypeKey = HDKeyDerivation.deriveChildKey(purposeKey, 195 | HARDENED_BIT);
            DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinTypeKey, 0 | HARDENED_BIT);
            DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(accountKey, 0);
            DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(changeKey, 0);
            // Store private and public key
            this.PrivateKey = addressKey.getPrivateKeyAsHex();
            this.PublicKey = addressKey.getPublicKeyAsHex();

            // Apply Keccak-256 hash to public key
            Keccak.Digest256 digest = new Keccak.Digest256();
            byte[] keccakHash = digest.digest(addressKey.getPubKey());

            // Take the last 20 bytes
            byte[] payload = new byte[20];
            System.arraycopy(keccakHash, keccakHash.length - 20, payload, 0, 20);

            // Add version byte (0x41 for TRON)
            byte[] addressBytes = new byte[21];
            addressBytes[0] = (byte) 0x41; // 0x41 represents TRON version byte
            System.arraycopy(payload, 0, addressBytes, 1, payload.length);

            // Convert to Base58Check format with the version byte
           String base58Address =encodeChecked(0x41, addressBytes);
            this.Address =  base58Address;

            //System.out.println("Generated Tron Address: " + base58Address);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
      private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // Example mnemonic and passphrase
        List<String> mnemonic = Arrays.asList("cannon", "direct", "kick", "enroll", "bag", "trouble", "grid", "require", "arrange", "like", "cherry", "search");
        String passphrase = "";

        TronWallet wallet = new TronWallet();
        wallet.generateTronWallet(mnemonic, passphrase);
        System.out.println("Seed Phrase: " + String.join(" ", mnemonic));

        System.out.println("Private Key: " + wallet.PrivateKey);
        System.out.println("Public Key: " + wallet.PublicKey);
        System.out.println("Tron Address: " + wallet.Address);
    }
}
