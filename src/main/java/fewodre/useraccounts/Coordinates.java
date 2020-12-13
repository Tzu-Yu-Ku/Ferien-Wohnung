package fewodre.useraccounts;
import com.mysema.commons.lang.Pair;

import java.awt.*;


public class Coordinates {
	public String size;
	public float x;
	public float x_ref;
	public int x_ratio;
	public float y;
	public float y_ref;
	public int y_ratio;

	final Polygon cotta = new Polygon(new int[]{0, 0, 100, 100}, new int[]{0, 100, 100, 0}, 4);
	final Polygon weixdorf = new Polygon(new int[]{395,411, 417, 412, 413,409, 422,426, 426, 427, 429, 435, 440, 451, 452, 379, 354, 388            },
			new int[]{317,286, 291, 301, 304, 318, 319, 313, 321, 322, 320, 305,  306, 294, 220, 207, 297, 317 }, 18);
	public Coordinates(String size){
		String[] parts = size.split("-");
		x = Float.parseFloat(parts[0]);
		x_ref = Float.parseFloat(parts[1]);
		y = Float.parseFloat(parts[2]);
		y_ref = Float.parseFloat(parts[3]);
		x_ratio=Math.round(x/x_ref*1000);
		y_ratio=Math.round(y/y_ref*1000);
		System.out.println(x_ratio);
		System.out.println(y_ratio);
		System.out.println(weixdorf.contains(x_ratio, y_ratio));

	}
	public Coordinates(){}

}

