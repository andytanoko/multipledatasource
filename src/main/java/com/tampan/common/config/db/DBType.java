package com.tampan.common.config.db;

public enum DBType {
	MASTER, SLAVE;
	
	private int currentActiveIndex = -1;
	private int slaveCount;
	
	public synchronized String currentActive() {
		if (currentActiveIndex == -1) {
			return this.toString();
		} else {
			String currentActiveSlave = this.toString() + (--currentActiveIndex);
			if(currentActiveIndex == 0) currentActiveIndex = slaveCount;
			return currentActiveSlave;
		}
	}
	
	public void setSlaveCount(int count) {
		slaveCount = count;
		currentActiveIndex = count;
	}
}
