/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitcoinwalletgenerator;

import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.core.SegwitAddress;

import java.util.Arrays;
import java.util.List;

public class test {

    public static void main(String[] args) throws Exception {
        // استخدام عبارة مفتاح ثابتة للاختبار
        List<String> mnemonic = Arrays.asList("economy", "soft", "green", "tobacco", "service", "tuition", 
                                              "wire", "step", "portion", "grain", "segment", "frown");
        System.out.println("Seed Phrase: " + String.join(" ", mnemonic));

        // توليد المفاتيح من العبارة
        DeterministicKey accountKey = getAccountKeyFromMnemonic(mnemonic);
        System.out.println("Account Extended Private Key: " + accountKey.serializePrivB58(MainNetParams.get()));
        System.out.println("Account Extended Public Key: " + accountKey.serializePubB58(MainNetParams.get()));

        // اشتقاق المفتاح الخاص الأول (العنوان الأول)
        DeterministicKey addressKey = deriveAddressKey(accountKey, 0, 0);
        System.out.println("Private Key: " + addressKey.getPrivateKeyAsHex());
        System.out.println("Public Key: " + addressKey.getPublicKeyAsHex());

        // توليد عنوان Bitcoin SegWit (Bech32)
        String segwitAddress = getBitcoinSegwitAddress(addressKey);
        System.out.println("Bitcoin Address (SegWit): " + segwitAddress);
    }

    // اشتقاق المفتاح الرئيسي للحساب من عبارة المفتاح
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
}
