/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/5/26.
 * Class is part of the NSense application. It provides support for accelerometer pipeline.
 */


package cs.usense.pipelines.motion;


/**
 * This class is provided as input the classify the the activities in real time.
 * @author Saeik Firdose (COPELABS/ULHT)
 * @version 1.0, 2015
 */
public class WekaClassifier {

	/**
	 * This method classify the activities
	 * @param mObject Object with activity instances
	 * @return activity The current activity
	 */
	public static double classifyLinear(Object[] mObject) {
		return WekaClassifier.N7dfcb280(mObject);
	}

	static double N7dfcb280(Object []i) {
		double p = Double.NaN;
		if (i[64] == null) {
			p = 0;
		} else if (((Double) i[64]).doubleValue() <= 1.813893) {
			p = 0;
		} else if (((Double) i[64]).doubleValue() > 1.813893) {
			p = WekaClassifier.N7592b3211(i);
		} 
		return p;
	}

	static double N7592b3211(Object []i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() <= 510.507192) {
			p = WekaClassifier.N722e77d42(i);
		} else if (((Double) i[0]).doubleValue() > 510.507192) {
			p = WekaClassifier.N2df56f5f11(i);
		} 
		return p;
	}

	static double N722e77d42(Object []i) {
		double p = Double.NaN;
		if (i[7] == null) {
			p = 1;
		} else if (((Double) i[7]).doubleValue() <= 6.886922) {
			p = WekaClassifier.N48bd39dc3(i);
		} else if (((Double) i[7]).doubleValue() > 6.886922) {
			p = WekaClassifier.N5e506d2d8(i);
		} 
		return p;
	}

	static double N48bd39dc3(Object []i) {
		double p = Double.NaN;
		if (i[19] == null) {
			p = 1;
		} else if (((Double) i[19]).doubleValue() <= 4.76803) {
			p = WekaClassifier.N7f21bfd24(i);
		} else if (((Double) i[19]).doubleValue() > 4.76803) {
			p = 2;
		} 
		return p;
	}

	static double N7f21bfd24(Object []i) {
		double p = Double.NaN;
		if (i[23] == null) {
			p = 1;
		} else if (((Double) i[23]).doubleValue() <= 1.607655) {
			p = WekaClassifier.Nb8b5335(i);
		} else if (((Double) i[23]).doubleValue() > 1.607655) {
			p = 1;
		} 
		return p;
	}

	static double Nb8b5335(Object []i) {
		double p = Double.NaN;
		if (i[11] == null) {
			p = 1;
		} else if (((Double) i[11]).doubleValue() <= 1.633435) {
			p = 1;
		} else if (((Double) i[11]).doubleValue() > 1.633435) {
			p = WekaClassifier.N5e8027a36(i);
		} 
		return p;
	}

	static double N5e8027a36(Object []i) {
		double p = Double.NaN;
		if (i[3] == null) {
			p = 2;
		} else if (((Double) i[3]).doubleValue() <= 10.778767) {
			p = 2;
		} else if (((Double) i[3]).doubleValue() > 10.778767) {
			p = WekaClassifier.N2faa6ecd7(i);
		} 
		return p;
	}

	static double N2faa6ecd7(Object []i) {
		double p = Double.NaN;
		if (i[23] == null) {
			p = 1;
		} else if (((Double) i[23]).doubleValue() <= 1.10303) {
			p = 1;
		} else if (((Double) i[23]).doubleValue() > 1.10303) {
			p = 2;
		} 
		return p;
	}

	static double N5e506d2d8(Object []i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() <= 453.714738) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() > 453.714738) {
			p = WekaClassifier.N722fd5ba9(i);
		} 
		return p;
	}

	static double N722fd5ba9(Object []i) {
		double p = Double.NaN;
		if (i[5] == null) {
			p = 2;
		} else if (((Double) i[5]).doubleValue() <= 21.825349) {
			p = 2;
		} else if (((Double) i[5]).doubleValue() > 21.825349) {
			p = WekaClassifier.N2278e0e710(i);
		} 
		return p;
	}

	static double N2278e0e710(Object []i) {
		double p = Double.NaN;
		if (i[1] == null) {
			p = 1;
		} else if (((Double) i[1]).doubleValue() <= 101.205255) {
			p = 1;
		} else if (((Double) i[1]).doubleValue() > 101.205255) {
			p = 2;
		} 
		return p;
	}

	static double N2df56f5f11(Object []i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 2;
		} else if (((Double) i[0]).doubleValue() <= 560.242581) {
			p = WekaClassifier.N4c62dd8b12(i);
		} else if (((Double) i[0]).doubleValue() > 560.242581) {
			p = 2;
		} 
		return p;
	}

	static double N4c62dd8b12(Object []i) {
		double p = Double.NaN;
		if (i[11] == null) {
			p = 1;
		} else if (((Double) i[11]).doubleValue() <= 5.060838) {
			p = 1;
		} else if (((Double) i[11]).doubleValue() > 5.060838) {
			p = WekaClassifier.N6ebef7da13(i);
		} 
		return p;
	}

	static double N6ebef7da13(Object []i) {
		double p = Double.NaN;
		if (i[14] == null) {
			p = 2;
		} else if (((Double) i[14]).doubleValue() <= 14.754917) {
			p = 2;
		} else if (((Double) i[14]).doubleValue() > 14.754917) {
			p = 1;
		} 
		return p;
	}

	/**
	 * This method classify the activities
	 * @param i Object with activity instances
	 * @return activity The current activity
	 */
	public static double classify(Object[] i) {
		return WekaClassifier.N5f283f920(i);
	}

	static double N5f283f920(Object []i) {
		double p = Double.NaN;
		if (i[1] == null) {
			p = 0;
		} else if (((Double) i[1]).doubleValue() <= 3.945875) {
			p = WekaClassifier.N4b65bb0c1(i);
		} else if (((Double) i[1]).doubleValue() > 3.945875) {
			p = WekaClassifier.N72aea36111(i);
		}
		return p;
	}

	static double N4b65bb0c1(Object []i) {
		double p = Double.NaN;
		if (i[6] == null) {
			p = 0;
		} else if (((Double) i[6]).doubleValue() <= 1.58417) {
			p = WekaClassifier.N3db37802(i);
		} else if (((Double) i[6]).doubleValue() > 1.58417) {
			p = WekaClassifier.N27d5e4806(i);
		}
		return p;
	}

	static double N3db37802(Object []i) {
		double p = Double.NaN;
		if (i[64] == null) {
			p = 0;
		} else if (((Double) i[64]).doubleValue() <= 0.901194) {
			p = 0;
		} else if (((Double) i[64]).doubleValue() > 0.901194) {
			p = WekaClassifier.N2d18b67a3(i);
		}
		return p;
	}

	static double N2d18b67a3(Object []i) {
		double p = Double.NaN;
		if (i[31] == null) {
			p = 0;
		} else if (((Double) i[31]).doubleValue() <= 0.11973) {
			p = 0;
		} else if (((Double) i[31]).doubleValue() > 0.11973) {
			p = WekaClassifier.N31741ab74(i);
		}
		return p;
	}

	static double N31741ab74(Object []i) {
		double p = Double.NaN;
		if (i[19] == null) {
			p = 1;
		} else if (((Double) i[19]).doubleValue() <= 0.231357) {
			p = WekaClassifier.N3d85fdbe5(i);
		} else if (((Double) i[19]).doubleValue() > 0.231357) {
			p = 0;
		}
		return p;
	}

	static double N3d85fdbe5(Object []i) {
		double p = Double.NaN;
		if (i[2] == null) {
			p = 0;
		} else if (((Double) i[2]).doubleValue() <= 0.822479) {
			p = 0;
		} else if (((Double) i[2]).doubleValue() > 0.822479) {
			p = 1;
		}
		return p;
	}

	static double N27d5e4806(Object []i) {
		double p = Double.NaN;
		if (i[2] == null) {
			p = 0;
		} else if (((Double) i[2]).doubleValue() <= 4.142097) {
			p = WekaClassifier.N4b8ca3ee7(i);
		} else if (((Double) i[2]).doubleValue() > 4.142097) {
			p = 1;
		}
		return p;
	}

	static double N4b8ca3ee7(Object []i) {
		double p = Double.NaN;
		if (i[24] == null) {
			p = 1;
		} else if (((Double) i[24]).doubleValue() <= 0.087317) {
			p = 1;
		} else if (((Double) i[24]).doubleValue() > 0.087317) {
			p = WekaClassifier.N7e5e85018(i);
		}
		return p;
	}

	static double N7e5e85018(Object []i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 0;
		} else if (((Double) i[0]).doubleValue() <= 31.364411) {
			p = 0;
		} else if (((Double) i[0]).doubleValue() > 31.364411) {
			p = WekaClassifier.N6f6e45779(i);
		}
		return p;
	}

	static double N6f6e45779(Object []i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() <= 77.797149) {
			p = WekaClassifier.N3092c0c810(i);
		} else if (((Double) i[0]).doubleValue() > 77.797149) {
			p = 0;
		}
		return p;
	}

	static double N3092c0c810(Object []i) {
		double p = Double.NaN;
		if (i[3] == null) {
			p = 0;
		} else if (((Double) i[3]).doubleValue() <= 1.255713) {
			p = 0;
		} else if (((Double) i[3]).doubleValue() > 1.255713) {
			p = 1;
		}
		return p;
	}

	static double N72aea36111(Object []i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() <= 152.385971) {
			p = WekaClassifier.N2768701912(i);
		} else if (((Double) i[0]).doubleValue() > 152.385971) {
			p = 1;
		}
		return p;
	}

	static double N2768701912(Object []i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 0;
		} else if (((Double) i[0]).doubleValue() <= 25.233742) {
			p = 0;
		} else if (((Double) i[0]).doubleValue() > 25.233742) {
			p = WekaClassifier.N39978d8513(i);
		}
		return p;
	}

	static double N39978d8513(Object []i) {
		double p = Double.NaN;
		if (i[12] == null) {
			p = 1;
		} else if (((Double) i[12]).doubleValue() <= 1.331948) {
			p = WekaClassifier.Nccc344d14(i);
		} else if (((Double) i[12]).doubleValue() > 1.331948) {
			p = WekaClassifier.N58f295b917(i);
		}
		return p;
	}

	static double Nccc344d14(Object []i) {
		double p = Double.NaN;
		if (i[8] == null) {
			p = 0;
		} else if (((Double) i[8]).doubleValue() <= 0.600598) {
			p = 0;
		} else if (((Double) i[8]).doubleValue() > 0.600598) {
			p = WekaClassifier.N327db1cb15(i);
		}
		return p;
	}

	static double N327db1cb15(Object []i) {
		double p = Double.NaN;
		if (i[31] == null) {
			p = 0;
		} else if (((Double) i[31]).doubleValue() <= 0.088799) {
			p = WekaClassifier.N5a23a05216(i);
		} else if (((Double) i[31]).doubleValue() > 0.088799) {
			p = 1;
		}
		return p;
	}

	static double N5a23a05216(Object []i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() <= 60.640868) {
			p = 1;
		} else if (((Double) i[0]).doubleValue() > 60.640868) {
			p = 0;
		}
		return p;
	}

	static double N58f295b917(Object []i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 0;
		} else if (((Double) i[0]).doubleValue() <= 79.889608) {
			p = 0;
		} else if (((Double) i[0]).doubleValue() > 79.889608) {
			p = WekaClassifier.N1e43d24e18(i);
		}
		return p;
	}

	static double N1e43d24e18(Object []i) {
		double p = Double.NaN;
		if (i[1] == null) {
			p = 0;
		} else if (((Double) i[1]).doubleValue() <= 5.717648) {
			p = 0;
		} else if (((Double) i[1]).doubleValue() > 5.717648) {
			p = WekaClassifier.N76a40e6719(i);
		}
		return p;
	}

	static double N76a40e6719(Object []i) {
		double p = Double.NaN;
		if (i[18] == null) {
			p = 1;
		} else if (((Double) i[18]).doubleValue() <= 4.19749) {
			p = WekaClassifier.Ncadc90b20(i);
		} else if (((Double) i[18]).doubleValue() > 4.19749) {
			p = 0;
		}
		return p;
	}

	static double Ncadc90b20(Object []i) {
		double p = Double.NaN;
		if (i[3] == null) {
			p = 1;
		} else if (((Double) i[3]).doubleValue() <= 12.456585) {
			p = 1;
		} else if (((Double) i[3]).doubleValue() > 12.456585) {
			p = WekaClassifier.N6569f7ad21(i);
		}
		return p;
	}

	static double N6569f7ad21(Object []i) {
		double p = Double.NaN;
		if (i[1] == null) {
			p = 1;
		} else if (((Double) i[1]).doubleValue() <= 30.636989) {
			p = WekaClassifier.N1003b2df22(i);
		} else if (((Double) i[1]).doubleValue() > 30.636989) {
			p = 0;
		}
		return p;
	}

	static double N1003b2df22(Object []i) {
		double p = Double.NaN;
		if (i[0] == null) {
			p = 0;
		} else if (((Double) i[0]).doubleValue() <= 104.195785) {
			p = 0;
		} else if (((Double) i[0]).doubleValue() > 104.195785) {
			p = 1;
		}
		return p;
	}
}