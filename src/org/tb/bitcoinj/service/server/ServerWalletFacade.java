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
	
	public int getBlockCount() throws BlockStoreException {
		
		return mServerWalletAppKit.chain().getBlockStore().getChainHead().getHeight();
	}

	public String getBalance(final BalanceType pBalanceType) {
		
		return mServerWalletAppKit.wallet().getBalance(pBalanceType).toPlainString();
	}

	public boolean encryptWallet(final String pPassphrase) {
		
		if(mServerWalletAppKit.wallet().isEncrypted()){
					
			return false;
			
		}else{
		
			mServerWalletAppKit.wallet().encrypt(pPassphrase);
			
			return true;
		}
	}
	
	public boolean decryptWallet(final String pPassphrase) {
		
		if(mServerWalletAppKit.wallet().isEncrypted()){
		
			try{
			
				mServerWalletAppKit.wallet().decrypt(pPassphrase);
				
				return true;
				
			}catch(KeyCrypterException e){
				
				e.printStackTrace();
				return false;
			}
			
		}else{
			
			return false;
		}
	}

	public boolean isWalletEncrypted() {
		
		return mServerWalletAppKit.wallet().isEncrypted();
	}
}
