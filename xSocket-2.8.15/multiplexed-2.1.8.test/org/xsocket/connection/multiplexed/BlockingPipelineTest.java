/*
 *  Copyright (c) xsocket.org, 2006 - 2010. All rights reserved.
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
package org.xsocket.connection.multiplexed;




import java.net.InetAddress;


import org.junit.Assert;
import org.junit.Test;

import org.xsocket.connection.IServer;
import org.xsocket.connection.NonBlockingConnection;
import org.xsocket.connection.Server;
import org.xsocket.connection.ConnectionUtils;
import org.xsocket.connection.multiplexed.IBlockingPipeline;
import org.xsocket.connection.multiplexed.IMultiplexedConnection;
import org.xsocket.connection.multiplexed.MultiplexedConnection;
import org.xsocket.connection.multiplexed.MultiplexedProtocolAdapter;




/**
*
* @author grro@xsocket.org
*/
public final class BlockingPipelineTest {
	

	
	@Test 
	public void testSimple() throws Exception {
		IServer server = new Server(new MultiplexedProtocolAdapter(new EchoHandler()));
		ConnectionUtils.start(server);
		
		IMultiplexedConnection connection = new MultiplexedConnection(new NonBlockingConnection(InetAddress.getByName("localhost"), server.getLocalPort()));
		
		for (int i = 0; i < 10; i++) {
			String pipelineId = connection.createPipeline();
			Assert.assertEquals(1, connection.listOpenPipelines().length);
			
			IBlockingPipeline pipeline = connection.getBlockingPipeline(pipelineId);
			
			pipeline.write("Hello echo" + EchoHandler.DELIMITER);
			String response = pipeline.readStringByDelimiter(EchoHandler.DELIMITER);
			
			Assert.assertEquals("Hello echo", response);
			
			pipeline.close();
			Assert.assertEquals(0, connection.listOpenPipelines().length);
		}
		
		connection.close();
		server.close();
	}
}