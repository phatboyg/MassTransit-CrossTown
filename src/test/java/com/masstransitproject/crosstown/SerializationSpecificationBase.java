package com.masstransitproject.crosstown;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;

import junit.framework.TestCase;

import com.masstransitproject.crosstown.context.ISendContext;
import com.masstransitproject.crosstown.context.SendContext;
import com.masstransitproject.crosstown.serialization.IMessageSerializer;
import com.masstransitproject.crosstown.serialization.JsonMessageSerializer;

// Copyright 2007-2010 The Apache Software Foundation.
// 
// Licensed under the Apache License, Version 2.0 (the "License"); you may not use 
// this file except in compliance with the License. You may obtain a copy of the 
// License at 
// 
//     http://www.apache.org/licenses/LICENSE-2.0 
// 
// Unless required by applicable law or agreed to in writing, software distributed 
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
// CONDITIONS OF ANY KIND, either express or implied. See the License for the 
// specific language governing permissions and limitations under the License.

public class SerializationSpecificationBase extends TestCase {
	URI _sourceUri;
	URI _responseUri;
	URI _faultUri;
	URI _destinationUri;
	int _retryCount;

	public void TestSerialization(IMessage message) throws Exception {
		byte[] data;
		IMessageSerializer serializer = new JsonMessageSerializer();

		_sourceUri = new URI("loopback://localhost/source");
		_responseUri = new URI("loopback://localhost/response");
		_faultUri = new URI("loopback://localhost/fault");
		_destinationUri = new URI("loopback://localhost/destination");
		_retryCount = 69;

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ISendContext context = new SendContext();
		context.setSourceAddress(_sourceUri);
		// context.sendResponseTo(_responseUri);
		// context.sendFaultTo(_faultUri);
		context.setDestinationAddress(_destinationUri);
		context.setRetryCount(_retryCount);

		serializer.Serialize(output, message, context);

		data = output.toByteArray();

		// Trace.WriteLine(Encoding.UTF8.GetString(data));

		ByteArrayInputStream input = new ByteArrayInputStream(data);
		IMessage copy = serializer.Deserialize(input);

		assertEquals(message, copy);

		//
		// Assert.AreEqual(_retryCount, context.RetryCount);
		// Assert.AreEqual(_sourceUri, context.SourceAddress);
		// Assert.AreEqual(_responseUri, context.ResponseAddress);
		// Assert.AreEqual(_faultUri, context.FaultAddress);
		// Assert.AreEqual(_destinationUri, context.DestinationAddress);
		// // Assert.AreEqual(message.GetType().ToMessageName(),
		// CurrentMessage.Headers.MessageType);
	}

}