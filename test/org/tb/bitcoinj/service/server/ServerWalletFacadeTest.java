package org.tb.bitcoinj.service.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

import junit.framework.Assert;

import org.bitcoinj.core.Wallet.BalanceType;
import org.bitcoinj.crypto.KeyCrypterException;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerWalletFacadeTest {
	
	private static final String PASSWORD = "PASSWORD";
	private ServerWalletAppKit mServerWalletAppKit;
	private ServerWalletFacade mServerWalletFacade;

	@Before
	public void createServerWalletFacade() throws Exception {
		
		mServerWalletAppKit = new ServerWalletAppKit(TestNet3Params.get(), 
														new File("."),
														"walletappkit-server-test");
		mServerWalletAppKit.startAsync();
		mServerWalletAppKit.awaitRunning();
		
		mServerWalletFacade = new ServerWalletFacade(mServerWalletAppKit);
	}
	
	@After
	public void destroyServerWalletFacade(){
					
		mServerWalletAppKit.stopAsync();
		mServerWalletAppKit.awaitTerminated();
		mServerWalletFacade = null;
	}
	
	@Test
	public void testShutdown() {
		
		mServerWalletFacade.shutdown();		
	}

	@Test
	public void testGetBlockCount() {
		
		try {
			assertTrue(mServerWalletFacade.getBlockCount() >= 0);
		} catch (BlockStoreException e) {
			e.printStackTrace();
			
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetBalance() {
		
		Arrays.stream(BalanceType.values())
				.forEach(balanceType -> Assert.assertTrue(mServerWalletFacade.getBalance(balanceType).longValue() >= 0));		
	}

	
	private void beforeTestIsEncryptDecryptWallet(){
		
		// destroy stuff created by gloabl before
		destroyServerWalletFacade();
		
		// build test specific wallet app kit and facade
		mServerWalletAppKit = new ServerWalletAppKit(TestNet3Params.get(), 
				new File("."),
				"walletappkit-server-test-encrypt-wallet");
		mServerWalletAppKit.startAsync();
		mServerWalletAppKit.awaitRunning();
		
		mServerWalletFacade = new ServerWalletFacade(mServerWalletAppKit);
	}
	
	private void afterTestIsEncryptDecryptWallet(){
		
		if(mServerWalletAppKit.wallet().isEncrypted()){
		
			try{
				
				mServerWalletAppKit.wallet().decrypt(PASSWORD);
				
			}catch(KeyCrypterException e){
				
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testEncryptWallet() {
		
		// setup
		beforeTestIsEncryptDecryptWallet();
		
		// wallet is not encrypted
		assertTrue(mServerWalletFacade.encryptWallet(PASSWORD));
		
		// wallet is encrypted and cannot be reencrypted
		assertFalse(mServerWalletFacade.encryptWallet(PASSWORD));
		
		// null argument is illegal
		try{
			
			mServerWalletFacade.encryptWallet(null);
			fail("Encrypting with null argument should not be possible.");
		}catch(IllegalArgumentException e){
			
			// pass
		}
		
		// teardown
		afterTestIsEncryptDecryptWallet();
	}
	
	
	

	@Test
	public void testDecryptWallet() {
		
		// setup
		beforeTestIsEncryptDecryptWallet();
		
		// wallet is not encrypted
		assertFalse(mServerWalletFacade.decryptWallet(PASSWORD));
		
		// wallet is encrypted and cannot be reencrypted
		mServerWalletFacade.encryptWallet(PASSWORD);
		assertTrue(mServerWalletFacade.decryptWallet(PASSWORD));
		
		// null argument is illegal
		try{
			
			mServerWalletFacade.decryptWallet(null);
			fail("Decryption with null argument should not be possible.");
		}catch(IllegalArgumentException e){
			
			// pass
		}
		
		// teardown
		afterTestIsEncryptDecryptWallet();
	}

	@Test
	public void testIsWalletEncrypted() {
	
		// setup
		beforeTestIsEncryptDecryptWallet();
			
		// wallet is not encrypted
		assertFalse(mServerWalletFacade.isWalletEncrypted());
		
		// wallet is encrypted
		mServerWalletFacade.encryptWallet(PASSWORD);
		assertTrue(mServerWalletFacade.isWalletEncrypted());
			
		// teardown
		afterTestIsEncryptDecryptWallet();
	}
}
