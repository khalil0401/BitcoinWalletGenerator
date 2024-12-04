/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitcoinwalletgenerator;

import java.util.Arrays;
import static bitcoinwalletgenerator.BitcoinWalletGenerator.InsertBitcoinWallet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.params.MainNetParams;
import org.bouncycastle.jcajce.provider.digest.Keccak;

/**
 * FXML Controller class
 *
 * @author ATECH STORE
 */
public class GUIBTCController implements Initializable {

    @FXML
    private TextArea textarea;
    @FXML
    private Text checked;
    @FXML
    private Text found;
    @FXML
    private Text Bitcoin;
    @FXML
    private Text Bitcoin1;
    Thread t;
    boolean Stat = true;
    private static final int HARDENED_BIT = 0x80000000;
    @FXML
    private Text Ethereum;
    @FXML
    private Text Bitcoin11;
    @FXML
    private Text Litecoin;
    @FXML
    private Text Bitcoin12;
    @FXML
    private TextArea textarea1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private void startCheck() throws InterruptedException {
        Stat = true;
        BTCWallet bTCWallet = new BTCWallet();
        TronWallet tronWallet = new TronWallet();
        LitecoinWallet litecoinWallet = new LitecoinWallet();
        while (Stat) {
            try {
                List<String> mnemonic = generateMnemonic();
                 //BTC
                bTCWallet.getAccountKeyFromMnemonic(mnemonic);
                BTCCH(bTCWallet, mnemonic);
                // TRON
               // tronWallet.generateTronWallet(mnemonic, "");      
               // TRONCH(tronWallet, mnemonic);
               // litecoinWallet.generateAddress(mnemonic, 2);
                // litecoinWallet
               
                //LITCH(litecoinWallet, mnemonic);
                
               
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }

    }

    public void LITCH(LitecoinWallet litecoinWallet, List<String> mnemonic) throws SQLException {
        if (!litecoinWallet.Balance.equals("0")) {
            InsertBitcoinWallet(litecoinWallet.PrivateKey, litecoinWallet.PublicKey, litecoinWallet.Address, litecoinWallet.Balance, String.join(" ", mnemonic));
            double bitc = (Long.parseLong(litecoinWallet.Balance));
            double bitc1 = Double.parseDouble(Litecoin.getText().toString());
            Litecoin.setText((bitc1 + bitc) + "");

            // double Ethc=(Long.parseLong(balanceETH));
            //double Ethc1=Double.parseDouble(Litecoin.getText().toString());
            // Litecoin.setText((Ethc+Ethc1)+"");
            textarea1.appendText("\n"
                    + "Seed Phrase: " + String.join(" ", mnemonic) + "\n"
                    + "Litecoin Address: " + litecoinWallet.Address + "\n"
                    + "Address Balance: " + litecoinWallet.Balance + "\n"
                    + "-----------------------------------------------------------------------------");
            found.setText( Integer.valueOf(found.getText().toString())+ 1 + "");
        }
        textarea.setText("\n"
                + "Seed Phrase: " + String.join(" ", mnemonic) + "\n"
                + "Litecoin Address: " + litecoinWallet.Address + "\n"
                + "Address Balance: " + litecoinWallet.Balance + "\n"
                + "-----------------------------------------------------------------------------");
        checked.setText(Integer.valueOf(checked.getText().toString()) + 1 + "");

    }

    public void TRONCH(TronWallet tronWallet, List<String> mnemonic) throws SQLException {
        if (!tronWallet.Balance.equals("0.0")) {
            InsertBitcoinWallet(tronWallet.PrivateKey, tronWallet.PublicKey, tronWallet.Address, tronWallet.Balance, String.join(" ", mnemonic));
            double bitc = (Long.parseLong(tronWallet.Balance));
            double bitc1 = Double.parseDouble(Ethereum.getText().toString());
            Ethereum.setText((bitc1 + bitc) + "");

            // double Ethc=(Long.parseLong(balanceETH));
            //double Ethc1=Double.parseDouble(Litecoin.getText().toString());
            // Litecoin.setText((Ethc+Ethc1)+"");
            textarea1.appendText("\n"
                    + "Seed Phrase: " + String.join(" ", mnemonic) + "\n"
                    + "TRON Address: " + tronWallet.Address + "\n"
                    + "Address Balance: " + tronWallet.Balance + "\n"
                    + "-----------------------------------------------------------------------------");
            found.setText( Integer.valueOf(found.getText().toString())+ 1 + "");
        }
        textarea.setText("\n"
                + "Seed Phrase: " + String.join(" ", mnemonic) + "\n"
                + "TRON Address: " + tronWallet.Address + "\n"
                + "Address Balance: " + tronWallet.Balance + "\n"
                + "-----------------------------------------------------------------------------");
        checked.setText(Integer.valueOf(checked.getText().toString()) + 1 + "");

    }

    public void BTCCH(BTCWallet bTCWallet, List<String> mnemonic) throws SQLException {
        if (!bTCWallet.Balance.equals("0")) {
            InsertBitcoinWallet(bTCWallet.PrivateKey, bTCWallet.PublicKey, bTCWallet.Address, bTCWallet.Balance, String.join(" ", mnemonic));
            double bitc = (Long.parseLong(bTCWallet.Balance) / 100000000);
            double bitc1 = Double.parseDouble(Bitcoin.getText().toString());
            Bitcoin.setText((bitc1 + bitc) + "");

            // double Ethc=(Long.parseLong(balanceETH));
            //double Ethc1=Double.parseDouble(Litecoin.getText().toString());
            // Litecoin.setText((Ethc+Ethc1)+"");
            textarea1.appendText("\n"
                    + "Seed Phrase: " + String.join(" ", mnemonic) + "\n"
                    + "Bitcoin Address: " + bTCWallet.Address + "\n"
                    + "Address Balance: " + bTCWallet.Balance + "\n"
                    + "-----------------------------------------------------------------------------");
        }
        textarea.setText("\n"
                + "Seed Phrase: " + String.join(" ", mnemonic) + "\n"
                + "Bitcoin Address: " + bTCWallet.Address + "\n"
                + "Address Balance: " + bTCWallet.Balance + "\n"
                + "-----------------------------------------------------------------------------");
        checked.setText(Integer.valueOf(checked.getText().toString()) + 1 + "");

    }

    @FXML
    private void stop(ActionEvent event) throws InterruptedException {
        Stat = false;

    }

    // Generate a random mnemonic (seed phrase)
    private static List<String> generateMnemonic() throws Exception {
        byte[] entropy = new byte[16]; // 128-bit entropy
        new SecureRandom().nextBytes(entropy);

        return MnemonicCode.INSTANCE.toMnemonic(entropy);
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

    @FXML
    private void startcheck(ActionEvent event) {
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startCheck();
                } catch (InterruptedException ex) {
                    Logger.getLogger(GUIBTCController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();

    }

// تحويل Byte Array إلى Hex String
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
