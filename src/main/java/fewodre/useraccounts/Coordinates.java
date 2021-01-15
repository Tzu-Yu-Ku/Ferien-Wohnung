package fewodre.useraccounts;

import java.awt.*;
import java.util.ArrayList;


public class Coordinates {
	public String size;
	public float x;
	public float xRef;
	public int xRatio;
	public float y;
	public float yRef;
	public int yRatio;
	public static final ArrayList<Integer> xValue = new ArrayList<>();
	private static final ArrayList<Integer> yValue = new ArrayList<>();
	private String district;

	final Polygon pieschen = new Polygon(new int[]{359, 380, 401, 402, 398, 404, 412, 412, 418, 427, 433, 441, 433,
			430, 436, 436, 434, 428, 418, 404, 395, 386, 375, 357, 351, 334, 314, 306, 299, 292, 286, 276, 259, 296,
			346, 360, 362},
			new int[]{338, 381, 377, 385, 395, 399, 405, 397, 397, 416, 424, 438, 448, 458, 477, 501, 519, 517, 528,
					534, 517, 501, 503, 515, 542, 554, 556, 552, 532, 503, 477, 438, 401, 342, 304, 310, 342}, 37);
	final Polygon weixdorf = new Polygon(new int[]{476, 489, 530, 547, 536, 539, 530, 536, 545, 546, 568, 572, 568,
			572, 603, 608, 633, 633, 446, 343, 446, 473},
			new int[]{214, 226, 173, 177, 198, 204, 218, 251, 246, 238, 220, 228, 234, 238, 212, 216, 181, 20, 6,
					128, 200, 210}, 22);
	final Polygon klotsche = new Polygon(new int[]{475, 492, 527, 533, 546, 536, 527, 534, 531, 533, 540, 556, 568,
			569, 574, 576, 581, 582, 575, 563, 552, 540, 540, 542, 531, 518, 500, 488, 494, 489, 470, 466, 465, 455,
			440, 431, 418, 411, 408, 395, 401, 395, 382, 364, 357, 362, 414, 473},
			new int[]{210, 228, 177, 171, 177, 204, 220, 234, 244, 251, 244, 234, 218, 238, 238, 234, 246, 255, 265,
					281, 306, 336, 350, 373, 391, 401, 422, 442, 418, 418, 418, 424, 430, 434, 442, 422, 399, 391,
					405, 399, 389, 379, 383, 348, 306, 200, 159, 210}, 48);
	final Polygon cossebaude = new Polygon(new int[]{277, 256, 250, 237, 228, 209, 196, 179, 167, 151, 160, 154, 150,
			145, 138, 99, 141, 205, 237, 273},
			new int[]{434, 446, 442, 471, 471, 468, 497, 479, 483, 475, 464, 452, 446, 462, 448, 403, 354, 340,
					377, 430}, 20);
	final Polygon oberwartha = new Polygon(new int[]{138, 147, 145, 148, 153, 153, 157, 158, 155, 163, 157, 160,
			163, 157, 142, 141, 139, 128, 118, 99, 113, 110, 139},
			new int[]{452, 462, 454, 444, 444, 452, 452, 464, 479, 485, 489, 501, 505, 513, 521, 534, 542, 536,
					538, 513, 444, 462, 452}, 23);
	final Polygon gompitz = new Polygon(new int[]{100, 76, 106, 119, 141, 139, 139, 163, 170, 186, 192, 202, 206,
			212, 218, 231, 234, 240, 245, 247, 244, 250, 238, 222, 214, 208, 205, 163, 87, 63, 99},
			new int[]{560, 548, 523, 521, 526, 542, 556, 554, 605, 617, 623, 613, 599, 587, 578, 581, 574, 576,
					601, 623, 625, 656, 666, 674, 662, 682, 707, 786, 701, 583, 562}, 31);
	final Polygon altfranken = new Polygon(new int[]{205, 206, 215, 215, 235, 238, 248, 254, 259, 202, 208},
			new int[]{711, 699, 666, 672, 666, 680, 676, 693, 715, 739, 703}, 11);
	final Polygon cotta = new Polygon(new int[]{253, 257, 270, 296, 298, 308, 314, 324, 337, 351, 354, 354, 354, 354,
			359, 363, 369, 375, 375, 379, 370, 363, 360, 350, 335, 330, 324, 314, 306, 302, 298, 292, 288, 272, 256,
			250, 243, 232, 238, 248, 261, 267, 259, 257, 261, 256, 241, 238, 244, 245, 243, 243, 253},
			new int[]{699, 762, 823, 815, 778, 760, 756, 754, 754, 754, 752, 739, 721, 709, 701, 701, 693, 672, 666,
					642, 629, 636, 617, 613, 609, 593, 560, 554, 552, 540, 521, 493, 485, 430, 452, 442, 456, 479,
					483, 491, 497, 501, 523, 538, 544, 550, 562, 576, 619, 642, 660, 680, 699}, 53);
	final Polygon plauen = new Polygon(new int[]{296, 304, 312, 325, 346, 354, 351, 359, 364, 375, 383, 389, 399,
			405, 418, 424, 455, 456, 466, 468, 456, 450, 459, 457, 466, 484, 485, 465, 463, 469, 460, 312, 274, 296},
			new int[]{778, 766, 762, 754, 754, 748, 727, 697, 701, 672, 666, 652, 656, 664, 672, 676, 699, 711, 733,
					739, 760, 768, 778, 792, 786, 792, 809, 837, 847, 860, 937, 858, 853, 776}, 34);
	final Polygon prohlis = new Polygon(new int[]{470, 463, 463, 469, 460, 460, 456, 452, 457, 466, 460, 455, 455,
			466, 482, 501, 521, 533, 537, 540, 550, 562, 576, 592, 621, 619, 630, 640, 653, 653, 627, 578, 533, 513,
			470, 468},
			new int[]{856, 851, 841, 827, 805, 796, 776, 764, 752, 739, 725, 707, 697, 707, 719, 733, 748, 756, 770,
					778, 782, 788, 809, 831, 870, 878, 884, 894, 917, 992, 990, 996, 992, 953, 882, 860}, 36);
	final Polygon leuben = new Polygon(new int[]{658, 655, 636, 621, 607, 595, 578, 584, 594, 598, 598, 600, 607,
			616, 623, 629, 636, 646, 651, 656, 671, 685, 711, 717, 725, 732, 745, 674, 655},
			new int[]{927, 915, 890, 870, 849, 831, 811, 801, 788, 776, 762, 754, 741, 731, 725, 699, 707, 731, 756,
					776, 796, 813, 837, 864, 900, 919, 988, 982, 919}, 24);
	final Polygon loschwitz = new Polygon(new int[]{739, 738, 735, 727, 719, 711, 703, 698, 697, 685, 669, 656, 646,
			642, 637, 626, 614, 604, 595, 588, 581, 572, 539, 540, 552, 556, 560, 539, 550, 542, 533, 537, 542, 542,
			540, 540, 546, 556, 562, 569, 579, 582, 578, 604, 604, 608, 603, 603, 611, 620, 621, 639, 648, 656, 661,
			666, 666, 672, 674, 680, 678, 681, 746, 780, 781, 787, 832, 796, 756, 741, 723, 703, 707, 716, 717, 713,
			707, 700, 690, 681, 674, 659, 651, 645, 653, 648, 651, 656, 666, 668, 675, 680, 687, 704, 704, 706, 711,
			717, 736, 755, 745, 749, 754, 762, 784, 825, 764, 742, 736},
			new int[]{966, 945, 929, 898, 872, 862, 847, 829, 823, 811, 796, 766, 743, 719, 707, 699, 686, 670, 648,
					625, 605, 583, 564, 548, 534, 519, 511, 438, 432, 424, 391, 385, 375, 359, 340, 328, 316, 299,
					283, 271, 261, 246, 234, 222, 238, 255, 265, 273, 271, 275, 283, 297, 301, 293, 293, 287, 281,
					277, 277, 277, 269, 265, 249, 273, 330, 377, 466, 495, 528, 523, 536, 554, 566, 576, 589, 603,
					619, 625, 631, 642, 650, 658, 662, 666, 678, 691, 697, 705, 725, 727, 733, 741, 752, 764, 776,
					780, 788, 790, 780, 780, 794, 807, 813, 819, 821, 847, 943, 957, 947}, 109);
	final Polygon schoenefeldWeissig = new Polygon(new int[]{788, 786, 777, 771, 771, 759, 752, 749, 749, 755, 752,
			736, 730, 720, 714, 710, 709, 723, 711, 697, 684, 681, 671, 661, 655, 648, 652, 655, 649, 653, 662, 674,
			678, 690, 700, 709, 716, 716, 717, 704, 700, 707, 717, 732, 739, 775, 826, 893, 919, 928, 921, 918, 890,
			874, 857, 813, 799},
			new int[]{815, 805, 803, 805, 817, 813, 807, 801, 790, 780, 780, 780, 780, 782, 792, 784, 766, 762, 756,
					760, 743, 737, 723, 713, 703, 695, 682, 672, 666, 662, 658, 646, 640, 633, 627, 617, 607, 585, 578,
					570, 556, 550, 538, 528, 523, 519, 507, 501, 552, 615, 682, 735, 774, 801, 829, 831, 790}, 57);
	final Polygon langebrueck = new Polygon(new int[]{719, 704, 701, 696, 675, 665, 651, 633, 608, 597, 600, 607,
			611, 633, 640, 649, 668, 703, 713, 738, 722},
			new int[]{263, 265, 273, 279, 277, 293, 304, 299, 279, 273, 253, 240, 214, 179, 173, 177, 198,
					232, 230, 238, 271}, 21);
	final Polygon schoenborn = new Polygon(new int[]{713, 709, 651, 640, 681, 716, 761, 767, 739, 714},
			new int[]{228, 228, 177, 130, 106, 100, 145, 214, 236, 226}, 10);
	final Polygon neustadt = new Polygon(new int[]{668, 742, 713, 537, 536, 543, 550, 553, 558, 563, 574, 520, 508,
			494, 494, 485, 466, 468, 462, 450, 439, 433, 433, 437, 434, 434, 427, 418, 402, 408, 415, 418, 424, 433,
			449, 460, 470, 475, 488, 497, 513, 523, 537, 539},
			new int[]{181, 173, 153, 564, 558, 548, 538, 523, 509, 503, 489, 399, 409, 438, 418, 418, 420, 428, 434,
					432, 440, 448, 458, 477, 501, 513, 517, 523, 532, 544, 564, 581, 591, 601, 605, 595, 587, 585, 572,
					564, 562, 562, 562, 556}, 44);
	final Polygon altstadt = new Polygon(new int[]{325, 333, 349, 354, 354, 359, 366, 375, 389, 395, 401, 410, 417, 424,
			433, 441, 450, 457, 466, 475, 485, 491, 507, 515, 530, 531, 534, 517, 514, 510, 504, 500, 494, 497, 504,
			515, 508, 497, 476, 463, 443, 425, 420, 412, 405, 404, 396, 389, 380, 373, 373, 375, 378, 375, 363, 362,
			359, 351, 335, 330, 327, 324},
			new int[]{558, 554, 552, 538, 521, 513, 505, 501, 503, 519, 530, 546, 566, 583, 597, 605, 605, 599, 593,
					583, 576, 570, 564, 564, 562, 585, 609, 615, 613, 625, 638, 650, 664, 670, 680, 686, 707, 731, 715,
					707, 691, 674, 676, 674, 662, 658, 652, 652, 658, 668, 662, 652, 642, 629, 640, 629, 619, 613, 609,
					593, 574, 560}, 62);
	final Polygon blasewitz = new Polygon(new int[]{632, 626, 621, 621, 614, 606, 600, 598, 597, 598, 592, 588, 579,
			578, 569, 560, 556, 549, 543, 540, 537, 529, 523, 514, 505, 497, 500, 514, 511, 491, 497, 504, 510, 513,
			520, 527, 530, 531, 533, 534, 536, 543, 558, 569, 578, 584, 587, 595, 600, 604, 611, 621, 629},
			new int[]{697, 709, 719, 727, 733, 741, 754, 764, 776, 784, 788, 796, 807, 811, 798, 786, 786, 784, 778,
					766, 760, 756, 750, 741, 739, 729, 721, 688, 684, 668, 658, 640, 625, 617, 615, 613, 613, 605, 593,
					585, 564, 564, 576, 583, 597, 613, 625, 646, 662, 674, 682, 691, 701}, 53);
	final Polygon mobschatz = new Polygon(new int[]{137, 150, 164, 163, 174, 171, 179, 189, 198, 199, 202, 208, 214,
			215, 222, 231, 237, 237, 245, 251, 257, 260, 257, 260, 263, 263, 257, 250, 240, 238, 228, 222, 221, 208,
			199, 190, 195, 190, 184, 177, 168, 160, 160, 160, 160, 157, 147, 141, 141, 139, 137},
			new int[]{558, 556, 552, 564, 581, 607, 611, 623, 623, 615, 605, 597, 587, 583, 578, 583, 574, 562, 554,
					554, 550, 538, 519, 511, 503, 495, 491, 487, 477, 471, 473, 483, 479, 471, 468, 471, 495, 503, 489,
					487, 483, 487, 493, 503, 509, 515, 517, 523, 536, 544, 558}, 51);

	//final Polygon baum = new Polygon(new int[]{0, 0, 100, 100}, new int[]{0, 100, 100, 0}, 4);
	public Coordinates(String size) {
		String[] parts = size.split("-");
		x = Float.parseFloat(parts[0]);
		xRef = Float.parseFloat(parts[1]);
		y = Float.parseFloat(parts[2]);
		yRef = Float.parseFloat(parts[3]);
		xRatio = Math.round(x / xRef * 1000);
		yRatio = Math.round(y / yRef * 1000);
		xValue.add(xRatio);
		yValue.add(yRatio);
		district = "Stadt";
		decideDistrict();
	}

	private void decideDistrict() {
		if (weixdorf.contains(xRatio, yRatio)) {
			System.out.println("Weixdorf");
			this.district = "Weixdorf";
		} else if (klotsche.contains(xRatio, yRatio)) {
			System.out.println("Klotsche");
			this.district = "Klotsche";
		} else if (pieschen.contains(xRatio, yRatio)) {
			System.out.println("Pieschen");
			this.district = "Pieschen";
		} else if (cossebaude.contains(xRatio, yRatio)) {
			System.out.println("Cossebaude");
			this.district = "Cossebaude";
		} else if (oberwartha.contains(xRatio, yRatio)) {
			System.out.println("Cossebaude");
			this.district = "Cossebaude";
		} else if (gompitz.contains(xRatio, yRatio)) {
			System.out.println("Gompitz");
			this.district = "Gompitz";
		} else if (altfranken.contains(xRatio, yRatio)) {
			System.out.println("Altfranken");
			this.district = "Altfranken";
		} else if (cotta.contains(xRatio, yRatio)) {
			System.out.println("Cotta");
			this.district = "Cotta";
		} else if (plauen.contains(xRatio, yRatio)) {
			System.out.println("Plauen");
			this.district = "Plauen";
		} else if (prohlis.contains(xRatio, yRatio)) {
			System.out.println("Prohlis");
			this.district = "Prohlis";
		} else if (leuben.contains(xRatio, yRatio)) {
			System.out.println("Leuben");
			this.district = "Leuben";
		} else if (loschwitz.contains(xRatio, yRatio)) {
			System.out.println("Loschwitz");
			this.district = "Loschwitz";
		} else if (schoenefeldWeissig.contains(xRatio, yRatio)) {
			System.out.println("Schönefeld-Weißig");
			this.district = "Schönefeld-Weißig";
		} else if (langebrueck.contains(xRatio, yRatio)) {
			System.out.println("Langebrück");
			this.district = "Langebrück";
		} else if (schoenborn.contains(xRatio, yRatio)) {
			System.out.println("Schönborn");
			this.district = "Schönborn";
		} else if (neustadt.contains(xRatio, yRatio)) {
			System.out.println("Neustadt");
			this.district = "Neustadt";
		} else if (altstadt.contains(xRatio, yRatio)) {
			System.out.println("Altstadt");
			this.district = "Altstadt";
		} else if (blasewitz.contains(xRatio, yRatio)) {
			System.out.println("Blasewitz");
			this.district = "Blasewitz";
		} else if (mobschatz.contains(xRatio, yRatio)) {
			System.out.println("Mobschatz");
			this.district = "Mobschatz";
		}
	}

	@Override
	public String toString() {
		return "Coordinates{" +
				"x=" + x +
				", y=" + y +
				'}';
	}

	public String getDistrict() {
		return district;
	}
}

