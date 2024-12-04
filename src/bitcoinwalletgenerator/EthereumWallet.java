/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitcoinwalletgenerator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bouncycastle.jcajce.provider.digest.Keccak;

/**
 *
 * @author ATECH STORE
 */
public class EthereumWallet {
             private static final int HARDENED_BIT = 0x80000000;

        private static String generateEthereumAddress(List<String> mnemonic) throws Exception {
        byte[] seed = MnemonicCode.toSeed(mnemonic, "");
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seed);

        // المسار: m/44'/60'/0'/0/0 (60 هو Coin Type لـ Ethereum)
        DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, 44 | HARDENED_BIT);
        DeterministicKey coinTypeKey = HDKeyDerivation.deriveChildKey(purposeKey, 60 | HARDENED_BIT);
        DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinTypeKey, 0 | HARDENED_BIT);
        DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(accountKey, 0);
        DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(changeKey, 0);

        // توليد عنوان Ethereum (Hex)
        byte[] publicKey = addressKey.getPubKeyPoint().getEncoded(false);
        Keccak.Digest256 digest = new Keccak.Digest256();
        byte[] hash = digest.digest(Arrays.copyOfRange(publicKey, 1, publicKey.length));

        // إرجاع أول 20 بايت من hash كعنوان Ethereum
        return "0x" + bytesToHex(Arrays.copyOfRange(hash, 12, 32));
    }
private static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
        sb.append(String.format("%02x", b));
    }
    return sb.toString();
}

    public static String checkEthereumBalance(String address) throws IOException {
    String url = "https://api.etherscan.io/api?module=account&action=balance&address=" + address + "&tag=latest";
    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet request = new HttpGet(url);
    CloseableHttpResponse response = client.execute(request);

    try {
        String jsonResponse = EntityUtils.toString(response.getEntity());
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        String balance = jsonObject.get("result").getAsString();
        // Convert wei to ETH
        double ethBalance = Double.parseDouble(balance) / 1_000_000_000_000_000_000.0;
        return ethBalance + "";
    } finally {
        response.close();
        client.close();
    }
}
}
