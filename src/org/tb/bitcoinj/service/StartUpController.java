/**
 * 
 */
package org.tb.bitcoinj.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bitcoinj.core.AbstractWalletEventListener;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.tb.bitcoinj.service.server.ServerWalletAppKit;

/**
 * @author ToBe
 *
 */
public class StartUpController {

	private static enum CLIOptions{
		stop,
		help
	}
	
	private static final GnuParser sGNUcliParser = new GnuParser();
	private static final Options sCLIoptions = new Options();
	private static StartUpController sStartupController;
	
	public static void main(String[] args) {
				
		sStartupController = new StartUpController();
		
		initCLI();
		
		do{
			try {
		        // parse the command line arguments
				final CommandLine commandLine = sGNUcliParser.parse(sCLIoptions, args);
				processCMDLine(commandLine);
				
			}catch( ParseException exp ) {
		        // oops, something went wrong
		        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
		    }
			
			// read/wait for new input
			final InputStreamReader converter = new InputStreamReader(System.in);
		    final BufferedReader in = new BufferedReader(converter);
			
			try {
				args = in.readLine().split(" ");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}while(true);
	}
	
	private static void processCMDLine(final CommandLine pCommandLine) {
		
		if(pCommandLine.hasOption(CLIOptions.stop.name())){
			
			sStartupController.shutDown();
		}
		
		if(pCommandLine.hasOption(CLIOptions.help.name())){
			
			final HelpFormatter helpFormatter = new HelpFormatter();
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, sCLIoptions);
		}
	}

	private static void initCLI() {
		
		sCLIoptions.addOption(CLIOptions.stop.name(), false, "Stops the service");
		sCLIoptions.addOption(CLIOptions.help.name(), false, "Print help");

	}

	public final ServerWalletAppKit mServerWalletAppKit;
	
	public StartUpController(){
		// First we configure the network we want to use.
		// The available options are:
		// - MainNetParams
		// - TestNet3Params
		// - RegTestParams
		// While developing your application you probably want to use the Regtest mode and run your local bitcoin network. Run bitcoind with the -regtest flag
		// To test you app with a real network you can use the testnet. The testnet is an alternative bitcoin network that follows the same rules as main network. Coins are worth nothing and you can get coins for example from http://faucet.xeno-genesis.com/
		//
		// For more information have a look at: https://bitcoinj.github.io/testing and https://bitcoin.org/en/developer-examples#testing-applications
		final NetworkParameters params = TestNet3Params.get();
		// Now we initialize a new WalletAppKit. The kit handles all the boilerplate for us and is the easiest way to get everything up and running.
		// Have a look at the WalletAppKit documentation and its source to understand what's happening behind the scenes: https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/kits/WalletAppKit.java
		mServerWalletAppKit = new ServerWalletAppKit(params, new File("."), "walletappkit-server");
		// In case you want to connect with your local bitcoind tell the kit to connect to localhost.
		// You must do that in reg test mode.
		//kit.connectToLocalHost();
		// Now we start the kit and sync the blockchain.
		// bitcoinj is working a lot with the Google Guava libraries. The WalletAppKit extends the AbstractIdleService. Have a look at the introduction to Guava services: https://code.google.com/p/guava-libraries/wiki/ServiceExplained
		mServerWalletAppKit.startAsync();
		mServerWalletAppKit.awaitRunning();
		// To observe wallet events (like coins received) we implement a EventListener class that extends the AbstractWalletEventListener bitcoinj then calls the different functions from the EventListener class
		final WalletListener wListener = new WalletListener();
		mServerWalletAppKit.wallet().addEventListener(wListener);
		// Ready to run. The kit syncs the blockchain and our wallet event listener gets notified when something happens.
		// To test everything we create and print a fresh receiving address. Send some coins to that address and see if everything works.
		System.out.println("send money to: " + mServerWalletAppKit.wallet().freshReceiveAddress().toString());
		// Make sure to properly shut down all the running services when you manually want to stop the kit. The WalletAppKit registers a runtime ShutdownHook so we actually do not need to worry about that when our application is stopping.
		//System.out.println("shutting down again");
		//kit.stopAsync();
		//kit.awaitTerminated();	
	}
	
	public void shutDown(){
		
		System.out.println("shutting down service");
		mServerWalletAppKit.stopAsync();
		mServerWalletAppKit.awaitTerminated();
		System.exit(0);
	}
	
	private class WalletListener extends AbstractWalletEventListener {
		
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
}


