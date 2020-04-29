package async;

/*
 * Protects the data it holds from concurrent access. This will trivially cause
 * blocking but I mean like what can you do /shrug. Just dont have a million 
 * handlers running simultaneously I guess
 */
public class CriticalData <T> {
	public CriticalData(T payload){
		
	}
}
