package fewodre.useraccounts;
import com.mysema.commons.lang.Pair;

import java.awt.*;


public class Coordinates {
	public String size;
	public float x;
	public float x_ref;
	public float x_ratio;
	public float y;
	public float y_ref;
	public float y_ratio;

	public Coordinates(String size){
		String[] parts = size.split("-");
		x = Float.parseFloat(parts[0]);
		x_ref = Float.parseFloat(parts[1]);
		y = Float.parseFloat(parts[2]);
		y_ref = Float.parseFloat(parts[3]);
		x_ratio=x/x_ref;
		y_ratio=y/y_ref;
		System.out.println(x_ratio);
		System.out.println(y_ratio);

	}
	public Coordinates(){}

}

