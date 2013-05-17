// Copyright 2007-2012 Chris Patterson, Dru Sellers, Travis Smith, et. al.
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
package com.masstransitproject.crosstown.newid;

import java.io.IOException;

public class NewIdGenerator {
	final int _c;
	final int _d;

	final Object _sync = new Object();
	final ITickProvider _tickProvider;
	final byte[] _workerId;
	final int _workerIndex;
	int _a;
	int _b;
	long _lastTick;

	int _sequence;

	public NewIdGenerator(ITickProvider tickProvider,
			IWorkerIdProvider workerIdProvider) throws IOException {
		this(tickProvider, workerIdProvider, 0);

	}

	public NewIdGenerator(ITickProvider tickProvider,
			IWorkerIdProvider workerIdProvider, int workerIndex)
			throws IOException {
		_workerIndex = workerIndex;
		_workerId = workerIdProvider.GetWorkerId(_workerIndex);
		_tickProvider = tickProvider;

		_c = _workerId[0] << 24 | _workerId[1] << 16 | _workerId[2] << 8
				| _workerId[3];
		_d = _workerId[4] << 24 | _workerId[5] << 16;
	}

	public NewId Next() {
		int sequence;

		long ticks = _tickProvider.getTicks();
		synchronized (_sync) {
			if (ticks > _lastTick)
				UpdateTimestamp(ticks);

			if (_sequence == 65535) // we are about to rollover, so we need to
									// increment ticks
				UpdateTimestamp(_lastTick + 1);

			sequence = _sequence++;
		}

		return new NewId(_a, _b, _c, _d | sequence);
	}

	void UpdateTimestamp(long tick) {
		_lastTick = tick;
		_sequence = 0;

		_a = (int) (tick >> 32);
		_b = (int) (tick & 0xFFFFFFFF);
	}
}
