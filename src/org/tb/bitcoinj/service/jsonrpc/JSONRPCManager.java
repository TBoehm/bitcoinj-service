/**
 * 
 */
package org.tb.bitcoinj.service.jsonrpc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.Nullable;

import org.tb.bitcoinj.service.server.ServerWalletFacade;

import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.googlecode.jsonrpc4j.StreamServer;

/**
 * @author ToBe
 *
 */
public class JSONRPCManager {

	private final JsonRpcServer mJsonRpcServer;
	
	public JSONRPCManager(final ServerWalletFacade pFacade, final int pPort){
		
		mJsonRpcServer = new JsonRpcServer(pFacade);
		
		final int maxThreads = 128;
		try {
			InetAddress bindAddress = InetAddress.getLocalHost();
	
			final StreamServer streamServer = new StreamServer(mJsonRpcServer, 
					maxThreads, 
					pPort, 
					1024,
					bindAddress);
			
			streamServer.start();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
