/**
 * 
 */
package org.tb.bitcoinj.service.cli;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;

/**
 * @author ToBe
 *
 */
public abstract class AbsCLIParser {
	
	protected volatile GnuParser mGNUcliParser = new GnuParser();
	protected volatile Options mCLIoptions = new Options();
	
	public AbsCLIParser(){
		
		initCLI(mCLIoptions);
	}
		
	public abstract void initCLI(final Options pOptions);
}
