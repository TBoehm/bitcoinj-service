/**
 * 
 */
package org.tb.bitcoinj.service.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bitcoinj.core.Wallet.BalanceType;
import org.bitcoinj.store.BlockStoreException;
import org.tb.bitcoinj.service.server.ServerWalletFacade;

/**
 * @author ToBe
 *
 */
public class RuntimeCLIController extends AbsCLIParser{

	private static enum CLIrpc{
		// implemented options
		stop,
		help,
		getblockcount,
		getbalance, // opt. args AVAILABLE, ESTIMATED
		encryptwallet, // arg "passphrase"
		decryptwallet, // arg "passphrase"
		iswalletencrypted,
		// unimplemented options
		getdifficulty,
		addmultisigaddress, // nrequired ["key",...] ( "account" )
		addnode,// "node" "add|remove|onetry"
		backupwallet,// "destination"
		createmultisig,// nrequired ["key",...]
		createrawtransaction,// [{"txid":"id","vout":n},...] {"address":amount,...}
		decoderawtransaction,// "hexstring"
		decodescript,// "hex"
		dumpprivkey,// "bitcoinaddress"
		dumpwallet,// "filename"
		getaccount,// "bitcoinaddress"
		getaccountaddress,// "account"
		getaddednodeinfo,// dns ( "node" )
		getaddressesbyaccount,// "account"
		getbestblockhash,
		getblock,// "hash" ( verbose )
		getblockchaininfo,
		getblockhash,// index
		getblocktemplate,// ( "jsonrequestobject" )
		getconnectioncount,
		getgenerate,
		gethashespersec,
		getinfo,
		getmininginfo,
		getnettotals,
		getnetworkhashps,// ( blocks height )
		getnetworkinfo,
		getnewaddress,// ( "account" )
		getpeerinfo,//
		getrawchangeaddress,//
		getrawmempool,// ( verbose )
		getrawtransaction,// "txid" ( verbose )
		getreceivedbyaccount,// "account" ( minconf )
		getreceivedbyaddress,// "bitcoinaddress" ( minconf )
		gettransaction,// "txid"
		gettxout,// "txid" n ( includemempool )
		gettxoutsetinfo,
		getunconfirmedbalance,
		getwalletinfo,
		getwork,// ( "data" )
		importprivkey,// "bitcoinprivkey" ( "label" rescan )
		importwallet,// "filename"
		keypoolrefill,// ( newsize )
		listaccounts,// ( minconf )
		listaddressgroupings,
		listlockunspent,
		listreceivedbyaccount,// ( minconf includeempty )
		listreceivedbyaddress,// ( minconf includeempty )
		listsinceblock,// ( "blockhash" target-confirmations )
		listtransactions,// ( "account" count from )
		listunspent,// ( minconf maxconf ["address",...] )
		lockunspent,// unlock [{"txid":"txid","vout":n},...]
		move,// "fromaccount" "toaccount" amount ( minconf "comment" )
		ping,//
		sendfrom,// "fromaccount" "tobitcoinaddress" amount ( minconf "comment" "comment-to" )
		sendmany,// "fromaccount" {"address":amount,...} ( minconf "comment" )
		sendrawtransaction,// "hexstring" ( allowhighfees )
		sendtoaddress,// "bitcoinaddress" amount ( "comment" "comment-to" )
		setaccount,// "bitcoinaddress" "account"
		setgenerate,// generate ( genproclimit )
		settxfee,// amount
		signmessage,// "bitcoinaddress" "message"
		signrawtransaction,// "hexstring" ( [{"txid":"id","vout":n,"scriptPubKey":"hex","redeemScript":"hex"},...] ["privatekey1",...] sighashtype )
		submitblock,// "hexdata" ( "jsonparametersobject" )
		validateaddress,// "bitcoinaddress"
		verifychain,// ( checklevel numblocks )
		verifymessage // "bitcoinaddress" "signature" "message"
	}
	
	private final ServerWalletFacade mServerWalletFacade;
	
	
	public RuntimeCLIController(final ServerWalletFacade pServerWalletFacade, final InputStream pCLIinputStream) {
	
		mServerWalletFacade = pServerWalletFacade;
		
		final InputStreamReader converter = new InputStreamReader(pCLIinputStream);
	    final BufferedReader bufferedReader = new BufferedReader(converter);
				
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(true){
					
					try {

						// read/wait for new input
						final String[] args = bufferedReader.readLine().split(" ");
						
						// parse the command line arguments
						final CommandLine commandLine = mGNUcliParser.parse(mCLIoptions, args);
						processCMDLine(commandLine);
						
					} catch (IOException e) {

						// reading from cli failed
				        System.err.println( "Reading console input failed.  Reason: " + e.getMessage() );
						
					}catch( ParseException exp ) {
				        // oops, something went wrong
				        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
				    }
				}
			}
		}).start();
	}
	
	private void processCMDLine(final CommandLine pCommandLine) {
				
		if(pCommandLine.hasOption(CLIrpc.stop.name())){
			
			handleStop();
		}
		
		if(pCommandLine.hasOption(CLIrpc.getblockcount.name())){
			
			handleGetBlockCount();			
		}
		
		if(pCommandLine.hasOption(CLIrpc.help.name())){
			
			printHelp();
		}
		
		if(pCommandLine.hasOption(CLIrpc.getdifficulty.name())){
			
			handleGetDifficulty();
		}
		
		if(pCommandLine.hasOption(CLIrpc.getbalance.name())){
				
			final String balanceTypeArg = pCommandLine.getOptionValue(CLIrpc.getbalance.name());
			if(balanceTypeArg != null){
			
				try{
				
					final BalanceType balanceType = BalanceType.valueOf(balanceTypeArg);
					handleGetBalance(balanceType);
					
				}catch(IllegalArgumentException e){
					
					System.out.println("The balance type " + balanceTypeArg
										+ " is invalid. Try AVAILABLE or"
										+ " ESTIMATED instead.");
				}
			
			}else{
				
				handleGetBalance(BalanceType.AVAILABLE);
			}
		}
		
		if(pCommandLine.hasOption(CLIrpc.encryptwallet.name())){
			
			handleWalletEncryption(pCommandLine.getOptionValue(CLIrpc.encryptwallet.name()));
		}
		
		if(pCommandLine.hasOption(CLIrpc.decryptwallet.name())){
			
			handleWalletDecryption(pCommandLine.getOptionValue(CLIrpc.decryptwallet.name()));
		}
		
		if(pCommandLine.hasOption(CLIrpc.iswalletencrypted.name())){
			
			handleIsWalletEncrypted();
		}
	}

	private void handleIsWalletEncrypted() {
		
		System.out.println(mServerWalletFacade.isWalletEncrypted());
	}

	private void handleWalletEncryption(final String pPassphrase) {
		
		if(mServerWalletFacade.encryptWallet(pPassphrase)){
			
			System.out.println("Wallet successfully encrypted!");	
		
		}else{
		
			System.out.println("Wallet encryption failed! Maybe the wallet "
								+ "already is encrypted?");	
		}
	}

	private void handleWalletDecryption(final String pPassphrase) {
		
		if(mServerWalletFacade.decryptWallet(pPassphrase)){
			
			System.out.println("Wallet successfully decrypted!");	
		
		}else{
			
			System.out.println("Wallet decryption failed! Did you use the "
								+ "correct passphrase? Maybe the wallet is not"
								+ "encrypted at all?");	
		}
	}

	private void handleGetBalance(final BalanceType pBalanceType) {
		
		System.out.println(mServerWalletFacade.getBalance(pBalanceType));
	}

	private void handleGetDifficulty() {
		// TODO Auto-generated method stub
		
	}

	private void handleGetBlockCount() {
		
		try {
			System.out.println(mServerWalletFacade.getBlockCount());
		} catch (BlockStoreException e) {

			System.out.println(e.getMessage());
		}
	}

	private void printHelp(){

		final HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("ServerWalletApp", mCLIoptions);
	}
	
	private void handleStop(){
		
		System.out.println("shutting down service");
		mServerWalletFacade.shutdown();	
		System.out.println("service offline");
		System.exit(0);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void initCLI(Options pOptions) {
		
		pOptions.addOption(OptionBuilder.withDescription("Stops the wallet "
														+ "service and shuts "
														+ "down the program.")
										.create(CLIrpc.stop.name()));
		
		pOptions.addOption(OptionBuilder.withDescription("Prints help information")
										.create(CLIrpc.help.name()));
		
		pOptions.addOption(OptionBuilder.withDescription("Prints the number of "
														+ "blocks processed")
										.create(CLIrpc.getblockcount.name()));
		
		pOptions.addOption(OptionBuilder.hasOptionalArg()
							.withDescription("Returns the current AVAILABLE "
									+ "balance. You can also provide an optional"
									+ " parameter ESTIMATED or AVAILABLE.")
							.create(CLIrpc.getbalance.name()));
		
		pOptions.addOption(OptionBuilder.hasArg()
							.withDescription("Encrypts the wallet with the given"
									+ "passphrase.")
							.create(CLIrpc.encryptwallet.name()));
		
		pOptions.addOption(OptionBuilder.hasArg()
							.withDescription("Decrypts the wallet with the given"
									+ "passphrase.")
							.create(CLIrpc.decryptwallet.name()));
		
		pOptions.addOption(OptionBuilder.withDescription("Informs you about "
														+ "wheter or not the"
														+ " wallet is encrypted.")
										.create(CLIrpc.iswalletencrypted.name()));
	}
}
