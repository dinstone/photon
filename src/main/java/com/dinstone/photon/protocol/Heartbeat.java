package com.dinstone.photon.protocol;

public class Heartbeat {

	private int tick;

	public Heartbeat(int tick) {
		this.tick = tick;
	}

	public int increase() {
		return tick++;
	}

	public int decrease() {
		return tick--;
	}

	public int getTick() {
		return tick;
	}

	public void setTick(int tick) {
		this.tick = tick;
	}

}
