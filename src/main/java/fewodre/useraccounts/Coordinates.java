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
	final Polygon klotsche = new Polygon(new int[]{389,395,411,417,412,413,409,412,412,416,419,426,431,432,429,424,418,415,415,414,411,403,398,395,394,395,395,387,382,379,373,371,366,363,356,359,357,353,344,346,340,349,356,380,388},
			new int[]{308,316,289,290,302,304,315,318,326,324,326,314,322,329,335,345,361,372,384,394,402,408,416,427,423,415,415,414,421,419,427,417,401,406,404,398,395,397,377,371,343,313,289,304,307}, 45);
	public Coordinates(String size){
		String[] parts = size.split("-");
		x = Float.parseFloat(parts[0]);
		x_ref = Float.parseFloat(parts[1]);
		y = Float.parseFloat(parts[2]);
		y_ref = Float.parseFloat(parts[3]);
		x_ratio=Math.round(x/x_ref*1000);
		y_ratio=Math.round(y/y_ref*1000);
		System.out.println(x_ratio + ",");
		System.out.println(y_ratio + ",");
		if(weixdorf.contains(x_ratio, y_ratio)){
			System.out.println("Weixdorf");
		}
		if(klotsche.contains(x_ratio, y_ratio)){
			System.out.println("Klotsche");
		}

	}
	public Coordinates(){}

}

