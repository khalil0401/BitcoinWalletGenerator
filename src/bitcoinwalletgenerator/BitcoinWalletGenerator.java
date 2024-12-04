package bitcoinwalletgenerator;

import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.core.Bech32;
import org.bitcoinj.core.SegwitAddress;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.bitcoinj.script.Script.ScriptType;

public class BitcoinWalletGenerator extends Application{
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent parentroot = FXMLLoader.load(getClass().getResource("GUIBTC.fxml"));
        primaryStage.setTitle("GUIBTC");
        primaryStage.setResizable(false);
      

        primaryStage.setScene(new Scene(parentroot));
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    // Generate a random mnemonic (seed phrase)
    private static List<String> generateMnemonic() throws Exception {
        byte[] entropy = new byte[16]; // 128-bit entropy
        new SecureRandom().nextBytes(entropy);
      
        return MnemonicCode.INSTANCE.toMnemonic(entropy);
    }

     private static DeterministicKey getAccountKeyFromMnemonic(List<String> mnemonic) throws Exception {
        int HARDENED_BIT = 0x80000000;

        // توليد المفتاح الرئيسي من العبارة
        byte[] seed = MnemonicCode.toSeed(mnemonic, "");
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seed);

        // المشتقات حسب المسار m/84'/0'/0'
        DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, 84 | HARDENED_BIT);
        DeterministicKey coinTypeKey = HDKeyDerivation.deriveChildKey(purposeKey, 0 | HARDENED_BIT);
        DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinTypeKey, 0 | HARDENED_BIT);

        return accountKey;
    }

    // اشتقاق مفتاح العنوان (الفرعي) من مفتاح الحساب
    private static DeterministicKey deriveAddressKey(DeterministicKey accountKey, int change, int index) {
        DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(accountKey, change);
        return HDKeyDerivation.deriveChildKey(changeKey, index);
    }

    // توليد عنوان Bitcoin SegWit (Bech32)
    private static String getBitcoinSegwitAddress(DeterministicKey privateKey) {
        NetworkParameters params = MainNetParams.get();
        return SegwitAddress.fromKey(params, privateKey).toString();
    }
    // Check Bitcoin address balance using Blockstream API
    private static String checkAddressBalance(String address) throws Exception {
        String url = "https://blockstream.info/api/address/" + address;
        System.err.println("Checking balance for URL: " + url);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        CloseableHttpResponse response = client.execute(request);

        try {
            String jsonResponse = EntityUtils.toString(response.getEntity());
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            long balance = jsonObject.get("chain_stats").getAsJsonObject().get("funded_txo_sum").getAsLong();
            return balance + " satoshis";
        } finally {
            response.close();
            client.close();
        }
    }

    // Database connection for storing wallet information
    public static Connection SQliteConn() throws SQLException {
        String DATABASE_URL = "jdbc:mysql://localhost:3306/walletgenerator?useSSL=false";
        String DATABASE_UESRNAME = "root";
        String DATABASE_PASSWORD = "";
        return DriverManager.getConnection(DATABASE_URL, DATABASE_UESRNAME, DATABASE_PASSWORD);
    }

    // Insert wallet information into the database
    public static void InsertBitcoinWallet(String privateKey, String publicKey, String bitcoinAddress, String addressBalance, String seedPhrase) throws SQLException {
        Connection conn = SQliteConn();
        String sql = "INSERT INTO bitcoinwalletgenerator (PrivateKey, PublicKey, BitcoinAddress, AddressBalance, SeedPhrase) "
                   + "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement p = conn.prepareStatement(sql);
        p.setString(1, privateKey);
        p.setString(2, publicKey);
        p.setString(3, bitcoinAddress);
        p.setString(4, addressBalance);
        p.setString(5, seedPhrase);
        p.execute();
    }
}
