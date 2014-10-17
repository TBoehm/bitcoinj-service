/**
 * 
 */
package org.tb.bitcoinj.service.server;

import org.bitcoinj.core.Wallet.BalanceType;
import org.bitcoinj.crypto.KeyCrypterException;
import org.bitcoinj.store.BlockStoreException;

/**
 * @author ToBe
 *
 */
public class ServerWalletFacade {

	private final ServerWalletAppKit mServerWalletAppKit;
	
	public ServerWalletFacade(final ServerWalletAppKit pServerWalletAppKit){
		
		mServerWalletAppKit = pServerWalletAppKit;
	}
	
	public void shutdown(){
		
		mServerWalletAppKit.stopAsync();
		mServerWalletAppKit.awaitTerminated();
	}
	
	public int getBlockCount() {
		
		try {
			return mServerWalletAppKit.chain().getBlockStore().getChainHead().getHeight();
		} catch (BlockStoreException e) {
			
			e.printStackTrace();
			
			return -1;
		}
	}

	public String getBalance(final BalanceType pBalanceType) {
		
		return mServerWalletAppKit.wallet().getBalance(pBalanceType).toPlainString();
	}

	public String encryptWallet(final String pPassphrase) {
		
		if(mServerWalletAppKit.wallet().isEncrypted()){
					
			return "Wallet is already encrypted! Call -decryptwallet first.";
			
		}else{
		
			mServerWalletAppKit.wallet().encrypt(pPassphrase);
			
			return "Wallet is now encrypted!";
		}
	}
	
	public String decryptWallet(final String pPassphrase) {
		
		if(mServerWalletAppKit.wallet().isEncrypted()){
		
			try{
			
				mServerWalletAppKit.wallet().decrypt(pPassphrase);
				return "Wallet is now decrypted!";
								
			}catch(KeyCrypterException e){
				
				return e.getMessage();
			}
			
		}else{
			
			return "Wallet is unencrypted! You have to -encryptwallet first.";
		}
	}
}
