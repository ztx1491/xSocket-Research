/*
 * Copyright (c) xlightweb.org, 2006 - 2010. All rights reserved.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Please refer to the LGPL license at: http://www.gnu.org/copyleft/lesser.txt
 * The latest copy of this software may be found on http://www.xsocket.org/
 */
package org.xsocket.connection;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.List;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.BlockingConnection;
import org.xsocket.connection.IBlockingConnection;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.Server;



/**
*
* @author grro@xsocket.org
*/
public final class DispatcherInitialcountTest {


	@Ignore   // setting the system property will have side-effects by running all tests  
	@Test 
	public void testSimple() throws Exception {
		
		System.setProperty("org.xsocket.connection.dispatcher.initialCount", "7");
		
		Handler serverHandler = new Handler(); 
		Server server = new Server(serverHandler);
		server.start();
		
		List<IBlockingConnection> cons = new ArrayList<IBlockingConnection>();
		for (int i = 0; i < 100; i++) {
			IBlockingConnection con = new BlockingConnection("localhost", server.getLocalPort());
			cons.add(con);
			
			con.write("test\r\n");
			Assert.assertEquals("test", con.readStringByDelimiter("\r\n"));
		}

		Assert.assertTrue(server.getAcceptor().getDispatcherPool().getDispatcherSize() == 7);
		

		for (IBlockingConnection con : cons) {
			con.close();
		}
		
		server.close();
	}
	
	
	

	private static final class Handler implements IDataHandler {
		
		public boolean onData(INonBlockingConnection connection) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
			connection.write(connection.readStringByDelimiter("\r\n") + "\r\n");
			return true;
		}
	}
}
