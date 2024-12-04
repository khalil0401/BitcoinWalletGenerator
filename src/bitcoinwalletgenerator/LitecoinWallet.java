/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitcoinwalletgenerator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.List;
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

/**
 *
 * @author ATECH STORE
 */
public class LitecoinWallet {
     public String PrivateKey="";
 public   String PublicKey="";
  public  String Address="";
  public  String Balance="";
             private static final int HARDENED_BIT = 0x80000000;

    public static String checkLitecoinBalance(String address) throws IOException {
    String url = "https://api.blockcypher.com/v1/ltc/main/addrs/" + address + "/balance";
    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet request = new HttpGet(url);
    CloseableHttpResponse response = client.execute(request);
    
    try {
        String jsonResponse = EntityUtils.toString(response.getEntity());
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        int balance = jsonObject.get("final_balance").getAsInt();
        return balance / 100000000.0+"";  // Convert satoshis to LTC
    } finally {
        response.close();
        client.close();
    }
}
    public  String generateAddress(List<String> mnemonic, int coinType) throws Exception {
        byte[] seed = MnemonicCode.toSeed(mnemonic, "");
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seed);

        // المسار: m/44'/coin_type'/0'/0/0
        DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, 44 | HARDENED_BIT);
        DeterministicKey coinTypeKey = HDKeyDerivation.deriveChildKey(purposeKey, coinType | HARDENED_BIT);
        DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinTypeKey, 0 | HARDENED_BIT);
        DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(accountKey, 0);
        DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(changeKey, 0);
      NetworkParameters  params;
       
        params = MainNetParams.get();
        this.Address=SegwitAddress.fromKey(params, addressKey).toString();
        System.err.println(Address);
        this.PrivateKey=addressKey.getPrivateKeyAsHex();
        this.PublicKey=addressKey.getPublicKeyAsHex();
        this.Balance=checkLitecoinBalance(this.Address);
       

        return SegwitAddress.fromKey(params, addressKey).toString();
    }

}
