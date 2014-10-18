/**
 * 
 */
package org.tb.bitcoinj.service.cli;

import org.apache.commons.cli.Options;

/**
 * @author ToBe
 *
 */
public class StartupCLIargsHandler extends AbsCLIParser {

	private enum CLIOption{
		walletFilename,
		netParam
	}
	
	
	@Override
	public void initCLI(Options pOptions) {
		
		pOptions.addOption("w", CLIOption.walletFilename.name(), true, "Sets the name of the wallet file without file ending.");
		pOptions.addOption("n", CLIOption.netParam.name(), true, "Sets the network. Available are 'regtest','testnet' and 'prodnet'");
	}
}
