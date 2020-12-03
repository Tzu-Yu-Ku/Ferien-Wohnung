package fewodre.utils;

/**
 * Proxy welcher es erlauben soll das, dass die komplette Systemzeit, per {@link DefaultBusinessTime} , auf einmal
 * alterniert werden kann und es somit synchron bleibt.
 * @author jimmy
 */
public class ProxyBusinessTime {
	private static DefaultBusinessTime time;

	public static DefaultBusinessTime getBusinessTime(){
		if( time == null){
			time = new DefaultBusinessTime();
		}
		return time;
	}
}
