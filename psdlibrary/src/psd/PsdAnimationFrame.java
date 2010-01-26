package psd;

public class PsdAnimationFrame {
	private int delay;
	private int id;
	
	public PsdAnimationFrame(int id, int delay) {
		this.id = id;
		this.delay = delay;
	}
	
	public int getId() {
		return id;
	}
	
	public int getDelay() {
		return delay;
	}
}
