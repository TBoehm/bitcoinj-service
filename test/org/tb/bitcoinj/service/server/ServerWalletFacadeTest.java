package org.tb.bitcoinj.service.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerWalletFacadeTest {
	private ServerWalletAppKit pServerWalletAppKit = mock(ServerWalletAppKit.class);
	private ServerWalletFacade serverWalletFacade;

	@Before
	public void createServerWalletFacade() throws Exception {
		serverWalletFacade = new ServerWalletFacade(pServerWalletAppKit);
	}

	@Test
	public void testServerWalletFacade() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testShutdown() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetBlockCount() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetBalance() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testEncryptWallet() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testDecryptWallet() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testIsWalletEncrypted() {
		fail("Not yet implemented"); // TODO
	}
}
