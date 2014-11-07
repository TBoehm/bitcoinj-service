/**
 * 
 */
package org.tb.bitcoinj.service;

import java.io.File;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.tb.bitcoinj.service.cli.RuntimeCLIController;
import org.tb.bitcoinj.service.jsonrpc.JSONRPCManager;
import org.tb.bitcoinj.service.server.ServerWalletAppKit;
import org.tb.bitcoinj.service.server.ServerWalletFacade;

/**
 * @author ToBe
 *
 */
public class StartUp {

	public static void main(String[] args) {
		
		startUp();
	}
	
	private static void startUp(){
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
		final ServerWalletAppKit serverWalletAppKit = new ServerWalletAppKit(params, new File("."), "walletappkit-server");
		
		// In case you want to connect with your local bitcoind tell the kit to connect to localhost.
		// You must do that in reg test mode.
		//kit.connectToLocalHost();
		// Now we start the kit and sync the blockchain.
		// bitcoinj is working a lot with the Google Guava libraries. The WalletAppKit extends the AbstractIdleService. Have a look at the introduction to Guava services: https://code.google.com/p/guava-libraries/wiki/ServiceExplained
		serverWalletAppKit.startAsync();
		serverWalletAppKit.awaitRunning();
		// To observe wallet events (like coins received) we implement a EventListener class that extends the AbstractWalletEventListener bitcoinj then calls the different functions from the EventListener class
		final WalletListener wListener = new WalletListener();
		serverWalletAppKit.wallet().addEventListener(wListener);
		// Ready to run. The kit syncs the blockchain and our wallet event listener gets notified when something happens.
		// To test everything we create and print a fresh receiving address. Send some coins to that address and see if everything works.
		System.out.println("send money to: " + serverWalletAppKit.wallet().freshReceiveAddress().toString());

		// Create facade
		final ServerWalletFacade facade = new ServerWalletFacade(serverWalletAppKit);
		
		// Create CLI interface
		final RuntimeCLIController runtimeCLIController = new RuntimeCLIController(facade, System.in);

		// Create JSON RPC Interface
		final JSONRPCManager jsonrpcManager = new JSONRPCManager(facade, 33333);
	}
}


