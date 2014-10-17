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
	
	protected final GnuParser mGNUcliParser = new GnuParser();
	protected final Options mCLIoptions = new Options();
	
	public AbsCLIParser(){
		
		initCLI(mCLIoptions);
	}
		
	public abstract void initCLI(final Options pOptions);
}
