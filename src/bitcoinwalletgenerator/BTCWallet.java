/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitcoinwalletgenerator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
public class BTCWallet {
     public String PrivateKey="";
 public   String PublicKey="";
  public  String Address="";
  public  String Balance="0";
    public  String checkAddressBalance(String address) throws Exception {
        String url = "https://blockstream.info/api/address/" + address;
        //System.err.println("Checking balance for URL: " + url);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        CloseableHttpResponse response = client.execute(request);

        try {
            String jsonResponse = EntityUtils.toString(response.getEntity());
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            long balance = jsonObject.get("chain_stats").getAsJsonObject().get("funded_txo_sum").getAsLong()-jsonObject.get("chain_stats").getAsJsonObject().get("spent_txo_sum").getAsLong();
            return balance + "";
        } finally {
            response.close();
            client.close();
        }
    }
        public  void getBitcoinSegwitAddress(DeterministicKey privateKey) throws Exception {
        NetworkParameters params = MainNetParams.get();
       this. PrivateKey=privateKey.getPrivateKeyAsHex();
        this. PublicKey=privateKey.getPublicKeyAsHex();
        this.Address=SegwitAddress.fromKey(params, privateKey).toString();
       this.Balance=checkAddressBalance(Address);
        
    }
    public  DeterministicKey deriveAddressKey(DeterministicKey accountKey, int change, int index) {
        DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(accountKey, change);
        return HDKeyDerivation.deriveChildKey(changeKey, index);
    }
     public  void getAccountKeyFromMnemonic(List<String> mnemonic) throws Exception {
        int HARDENED_BIT = 0x80000000;

        // توليد المفتاح الرئيسي من العبارة
        byte[] seed = MnemonicCode.toSeed(mnemonic, "");
         
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seed);

        // المشتقات حسب المسار m/84'/0'/0'
        DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, 84 | HARDENED_BIT);
        DeterministicKey coinTypeKey = HDKeyDerivation.deriveChildKey(purposeKey, 0 | HARDENED_BIT);
        DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinTypeKey, 0 | HARDENED_BIT);

     DeterministicKey addressKey= deriveAddressKey(accountKey, 0, 0);
    this.getBitcoinSegwitAddress(addressKey);
    }


}
