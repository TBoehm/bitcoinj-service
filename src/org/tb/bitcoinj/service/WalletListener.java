package org.tb.bitcoinj.service;

import java.util.List;

import org.bitcoinj.core.AbstractWalletEventListener;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.script.Script;

public class WalletListener extends AbstractWalletEventListener {
	
	@Override
	public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
		System.out.println("-----> coins resceived: " + tx.getHashAsString());
		System.out.println("received: " + tx.getValue(wallet));
	}
	@Override
	public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
		System.out.println("-----> confidence changed: " + tx.getHashAsString());
		TransactionConfidence confidence = tx.getConfidence();
		System.out.println("new block depth: " + confidence.getDepthInBlocks());
	}
	@Override
	public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
		
		System.out.println("coins send.");
	}
	@Override
	public void onReorganize(Wallet wallet) {
		
		System.out.println("wallet reorganized");
	}
	@Override
	public void onWalletChanged(Wallet wallet) {
		
		System.out.println("wallet changed");
	}
	
	@Override
	public void onScriptsAdded(Wallet wallet, List<Script> scripts) {
		
		System.out.println("new script added");
	}			
	
	@Override
	public void onKeysAdded(List<ECKey> keys) {
		
		System.out.println("new key added");
	}
}