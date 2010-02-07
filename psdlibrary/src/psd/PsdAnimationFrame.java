package psd;

public class PsdAnimationFrame {
	private int delay;
	private int id;
	private int number;
	
	public PsdAnimationFrame(int id, int number, int delay) {
		this.id = id;
		this.number = number;
		this.delay = delay;
	}
	
	public int getNumber() {
		return number;
	}
	
	public int getId() {
		return id;
	}
	
	public int getDelay() {
		return delay;
	}
}
