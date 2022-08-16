package game;

public enum Layer {
	SILICON_LAYER(new byte[] {P_TYPE,N_TYPE,N_GATE,P_GATE}),
	VIA_LAYER(new byte[]{METAL,POWER}),
	METAL_LAYER(new byte[]{VIA});
	
	
	public final byte[] members;
	
	private Layer(byte[] members) {
		this.members = members;
	}
	
	public boolean isAdjacent(Layer l) {
		return Math.abs(this.ordinal() - l.ordinal()) <= 1;
	}
	
}
