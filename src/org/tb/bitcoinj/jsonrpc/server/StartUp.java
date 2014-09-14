/**
 * 
 */
package org.tb.bitcoinj.jsonrpc.server;

import java.io.IOException;

/**
 * @author ToBe
 *
 */
public class StartUp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		new Server();
		
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
