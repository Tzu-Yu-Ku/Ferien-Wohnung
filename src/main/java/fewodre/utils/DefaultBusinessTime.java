package fewodre.utils;

import org.salespointframework.time.BusinessTime;

import java.time.Duration;
import java.time.LocalDateTime;


/**
 * Komponente welche zugang zur Geschäftszeit erlaubt.
 * Normallerweise wird es die derzeitige Geschäftszeit zurück geben,
 * kann aber manuell erhöht werden, um eine Bestimmte {@link Duration},
 * so dass es besser für Testzwecke genutzt werden kann.
 * Um einzelne Komponenten zu testen wird empfohlen direkt die DefaultBusinessTime zu nutzen
 * jedoch sollte diese möglichst am Ende nur noch indirekt über {@link ProxyBusinessTime} geschehen
 * um die Zeit zwischen den Komponenten möglichst konstant zu halten
 * @author jimmy
 */
public class DefaultBusinessTime implements BusinessTime {
	// Why the fuck has java no operation overloading ?!
	public Duration offset;

	public DefaultBusinessTime(){
		offset = Duration.ZERO;
	}

	/**
	 * Gibt die derzeitige BuisnessTime zurük. Welche im normallfall die Systemzeit der Anwendung sein wird
	 * aber durch aufrufen von {@link #forward(Duration)} erhöht werden kann.
	 *
	 * @return
	 */
	@Override
	public LocalDateTime getTime() {
		return LocalDateTime.now().plus(offset);
	}

	/**
	 * Erhöht die derzeitige BuisnessTime um die gegebene {@link Duration}
	 *
	 * @param duration
	 */
	@Override
	public void forward(Duration duration) {
		offset = offset.plus(duration);
	}

	/**
	 * Gibt die derzeitige Abweichung der BusinessTime von der System-Zeit zurück.
	 * Welche durch {@link #forward(Duration)} erzeugt wurde.
	 *
	 * @return
	 */
	@Override
	public Duration getOffset() {
		return offset;
	}

	/**
	 * Hebt jegliche abweichung der BusinessTime von der System-Zeit wieder auf.
	 */
	@Override
	public void reset() {
		offset = Duration.ZERO;
	}
}
